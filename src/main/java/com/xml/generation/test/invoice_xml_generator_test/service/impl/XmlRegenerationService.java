package com.xml.generation.test.invoice_xml_generator_test.service.impl;

import com.xml.generation.test.invoice_xml_generator_test.converter.InvoiceReverseConverter;
import com.xml.generation.test.invoice_xml_generator_test.logging.CustomLogging;
import com.xml.generation.test.invoice_xml_generator_test.model.data.Invoice;
import com.xml.generation.test.invoice_xml_generator_test.model.dto.InvoiceDTO;
import com.xml.generation.test.invoice_xml_generator_test.model.enums.RequestFromEnum;
import com.xml.generation.test.invoice_xml_generator_test.service.XMLGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class XmlRegenerationService {

    private final InvoiceReverseConverter invoiceReverseConverter;
    private final XMLGenerationService xmlGenerationService;
    private final XmlCanonicalizer xmlCanonicalizer;
    private final QrGeneratorService qrGeneratorService;
    private final SigningCallerService signingCallerService;

    @Value("${signing.enabled}")
    private boolean signingEnabled;

    public byte[] regenerateXml(Invoice invoice) throws Exception {

        String taxNumber = invoice.getUser().getTaxpayer().getTaxNumber();
        String invoiceNumber = invoice.getInvoiceNumber();

        CustomLogging.logInfo(taxNumber, invoiceNumber, invoice.getId(),
                "Regenerating XML for invoiceId={}", invoice.getId());

        // ── 2. Convert Invoice entity → InvoiceDTO ─────────────────────────
        InvoiceDTO invoiceDTO;
        try {
            invoiceDTO = invoiceReverseConverter.entityToDto(invoice);
        } catch (Exception e) {
            CustomLogging.logError("CONVERT_FAILED", invoice.getId(),
                    "Failed to convert invoiceId={}: {}", invoice.getId(), e.getMessage());
            throw new RuntimeException(
                    "Failed to convert invoice id=" + invoice.getId() + ": " + e.getMessage(), e);
        }

        // ── 3. Determine requestFrom ───────────────────────────────────────
        String requestFrom = RequestFromEnum.MOBILE.equals(invoice.getRequestFromEnum())
                ? RequestFromEnum.MOBILE.toString()
                : RequestFromEnum.WEB.toString();

        // ── 4. Generate raw XML via FreeMarker template ────────────────────
        String rawXml = xmlGenerationService.generateXML(invoiceDTO, requestFrom

        );

        CustomLogging.logInfo(taxNumber, invoiceNumber, invoice.getId(),
                "Base XML generated for invoiceId={}", invoice.getId());

        // ── 5. Minify + Canonicalize  (SME: MinifyAndCanonicalizeXml step) ─
        String canonicalXml;
        try {
            canonicalXml = xmlCanonicalizer.minifyAndCanonicalize(rawXml)
                    .replaceAll("<\\?xml(.+?)\\?>", "");
            CustomLogging.logInfo(taxNumber, invoiceNumber, invoice.getId(),
                    "XML canonicalized for invoiceId={}", invoice.getId());
        } catch (Exception e) {
            CustomLogging.logError("CANONICALIZE_FAILED", invoice.getId(),
                    "Canonicalization failed for invoiceId={}: {}", invoice.getId(), e.getMessage());
            throw new RuntimeException(
                    "Canonicalization failed for invoiceId=" + invoice.getId() + ": " + e.getMessage(), e);
        }

        // ── 6. Generate QR code  (SME: InvoiceQRGenerator step) ───────────
        String qrCode;
        if (invoice.getQrCode().isBlank()) {
            try {
                qrCode = qrGeneratorService.generateQRstatically(canonicalXml);
                CustomLogging.logInfo(taxNumber, invoiceNumber, invoice.getId(),
                        "QR generated for invoiceId={}", invoice.getId());
            } catch (Exception e) {
                CustomLogging.logError("QR_FAILED", invoice.getId(),
                        "QR generation failed for invoiceId={}: {}", invoice.getId(), e.getMessage());
                throw new RuntimeException(
                        "QR generation failed for invoiceId=" + invoice.getId() + ": " + e.getMessage(), e);
            }

        } else {
            qrCode = invoice.getQrCode();
        }

        // ── 7. Call signing service  (SME: invoiceSigningForSme step) ──────
        //    POST { invoice: canonicalXml, qrCode } → signed XML
        if (!signingEnabled) {
            CustomLogging.logWarn("SIGNING_SKIPPED", taxNumber, invoiceNumber, invoice.getId(),
                    "Signing skipped (signing.enabled=false) for invoiceId={}", invoice.getId());
            return canonicalXml.getBytes(StandardCharsets.UTF_8);
        }

        String signedXml;
        try {
            signedXml = signingCallerService.sign(canonicalXml, qrCode, invoice);
        } catch (Exception e) {
            CustomLogging.logError("SIGNING_FAILED", invoice.getId(),
                    "Signing failed for invoiceId={}: {}", invoice.getId(), e.getMessage());
            throw new RuntimeException("Signing failed for invoiceId=" + invoice.getId() + ": " + e.getMessage(), e);
        }

        CustomLogging.logInfo(taxNumber, invoiceNumber, invoice.getId(),
                "Signing complete for invoiceId={}", invoice.getId());

        return signedXml.getBytes(StandardCharsets.UTF_8);
    }
}
