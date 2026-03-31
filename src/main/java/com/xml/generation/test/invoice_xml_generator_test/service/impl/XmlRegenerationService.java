package com.xml.generation.test.invoice_xml_generator_test.service.impl;

import com.xml.generation.test.invoice_xml_generator_test.converter.InvoiceReverseConverter;
import com.xml.generation.test.invoice_xml_generator_test.logging.CustomLogging;
import com.xml.generation.test.invoice_xml_generator_test.model.dto.InvoiceDTO;
//import com.xml.generation.test.invoice_xml_generator_test.model.dto.LuInvoiceTypeDTO;
import com.xml.generation.test.invoice_xml_generator_test.model.dto.LuInvoiceTypeDTO;
import com.xml.generation.test.invoice_xml_generator_test.model.entity.Invoice;
import com.xml.generation.test.invoice_xml_generator_test.model.enums.RequestFromEnum;
import com.xml.generation.test.invoice_xml_generator_test.model.lookup.Lu_InvoiceType;
import com.xml.generation.test.invoice_xml_generator_test.service.QrGeneratorService;
import com.xml.generation.test.invoice_xml_generator_test.service.XMLGenerationService;
import com.xml.generation.test.invoice_xml_generator_test.service.XmlCanonicalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class XmlRegenerationService {

    private final InvoiceReverseConverter invoiceReverseConverter;
    private final XMLGenerationService    xmlGenerationService;
    private final XmlCanonicalizer        xmlCanonicalizer;
    private final QrGeneratorService      qrGeneratorService;
    private final SigningCallerService    signingCallerService;

    @Value("${signing.enabled}")
    private boolean signingEnabled;

    /**
     * Full pipeline — mirrors the SME's core-e-invoicing-submit-invoice-for-sme-process:
     *
     *   1. generateXML          → raw FreeMarker XML
     *   2. minifyAndCanonicalize → canonicalized XML          (SME: MinifyAndCanonicalizeXml)
     *   3. generateQRstatically → base64 BER-TLV QR code      (SME: InvoiceQRGenerator)
     *   4. sign                 → POST /document/sign          (SME: invoiceSigningForSme)
     */
    public byte[] regenerateXml(Invoice invoice, boolean forceSigning) throws Exception {

        String taxNumber     = invoice.getUser().getTaxpayer().getTaxNumber();
        String invoiceNumber = invoice.getInvoiceNumber();

        CustomLogging.logInfo(taxNumber, invoiceNumber,
                "Regenerating XML for invoiceId={}", invoice.getId());

        // ── 1. Fetch LuInvoiceType ─────────────────────────────────────────
        Lu_InvoiceType luInvoiceType = LookupFacade.getInvoiceType(invoice.getInvoiceKind());
        LuInvoiceTypeDTO luInvoiceTypeDTO = new LuInvoiceTypeDTO(
                luInvoiceType.getCode(),
                luInvoiceType.getArabicDescription(),
                luInvoiceType.getEnglishDescription(),
                luInvoiceType.getXmlDigitReference(),
                luInvoiceType.getAllowedPercentage(),
                luInvoiceType.getEnabled()
        );

        // ── 2. Convert Invoice entity → InvoiceDTO ─────────────────────────
        InvoiceDTO invoiceDTO;
        try {
            invoiceDTO = invoiceReverseConverter.entityToDto(invoice);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to convert invoice id=" + invoice.getId() + ": " + e.getMessage(), e);
        }

        // ── 3. Determine requestFrom ───────────────────────────────────────
        String requestFrom = RequestFromEnum.MOBILE.equals(invoice.getRequestFromEnum())
                ? RequestFromEnum.MOBILE.toString()
                : RequestFromEnum.WEB.toString();

        // ── 4. Generate raw XML via FreeMarker template ────────────────────
        String rawXml = xmlGenerationService.generateXML(invoiceDTO, requestFrom
               , luInvoiceTypeDTO
        );

        CustomLogging.logInfo(taxNumber, invoiceNumber,
                "Base XML generated for invoiceId={}", invoice.getId());

        // ── 5. Minify + Canonicalize  (SME: MinifyAndCanonicalizeXml step) ─
        String canonicalXml;
        try {
            canonicalXml = xmlCanonicalizer.minifyAndCanonicalize(rawXml)
                    .replaceAll("<\\?xml(.+?)\\?>", "");
            CustomLogging.logInfo(taxNumber, invoiceNumber,
                    "XML canonicalized for invoiceId={}", invoice.getId());
        } catch (Exception e) {
            throw new RuntimeException(
                    "Canonicalization failed for invoiceId=" + invoice.getId() + ": " + e.getMessage(), e);
        }

        // ── 6. Generate QR code  (SME: InvoiceQRGenerator step) ───────────
        //    QR is built from the canonicalized XML, same as SME
        String qrCode;
        try {
            qrCode = qrGeneratorService.generateQRstatically(canonicalXml);
            CustomLogging.logInfo(taxNumber, invoiceNumber,
                    "QR generated for invoiceId={}", invoice.getId());
        } catch (Exception e) {
            throw new RuntimeException(
                    "QR generation failed for invoiceId=" + invoice.getId() + ": " + e.getMessage(), e);
        }

        // ── 7. Call signing service  (SME: invoiceSigningForSme step) ──────
        //    POST { invoice: canonicalXml, qrCode } → signed XML
        if (!signingEnabled && !forceSigning) {
            CustomLogging.logInfo(taxNumber, invoiceNumber,
                    "Signing skipped (signing.enabled=false) for invoiceId={}", invoice.getId());
            return canonicalXml.getBytes(StandardCharsets.UTF_8);
        }

        String signedXml = signingCallerService.sign(canonicalXml, qrCode, invoice);

        CustomLogging.logInfo(taxNumber, invoiceNumber,
                "Signing complete for invoiceId={}", invoice.getId());

        return signedXml.getBytes(StandardCharsets.UTF_8);
    }

    public byte[] regenerateXml(Invoice invoice) throws Exception {
        return regenerateXml(invoice, false);
    }

    public boolean isSigned(byte[] xmlFile) {
        if (xmlFile == null || xmlFile.length == 0) return false;

        String content = new String(xmlFile, StandardCharsets.UTF_8).trim();

        // corrupted / placeholder values
        if (content.isEmpty() || content.equalsIgnoreCase("xml")) return false;

        String xml = resolveXml(content);
        if (xml == null) return false;

        return xml.contains("<ds:Signature")
                || xml.contains("<Signature")
                || xml.contains("<sig:UBLDocumentSignatures");
    }

    // Returns the XML string after decoding base64 once or twice if needed.
    // Returns null if content is not recognizable as XML.
    private String resolveXml(String content) {
        if (looksLikeXml(content)) return content;

        // try decode once
        try {
            String once = new String(java.util.Base64.getDecoder().decode(content), StandardCharsets.UTF_8).trim();
            if (looksLikeXml(once)) return once;

            // try decode twice
            try {
                String twice = new String(java.util.Base64.getDecoder().decode(once), StandardCharsets.UTF_8).trim();
                if (looksLikeXml(twice)) return twice;
            } catch (Exception ignored) {}

        } catch (Exception ignored) {}

        return null;
    }

    private boolean looksLikeXml(String content) {
        return content.startsWith("<");
    }
}
