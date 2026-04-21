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

import org.apache.commons.lang3.StringUtils;

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

        CustomLogging.logInfo(taxNumber, invoiceNumber, invoice.getInvoiceId(),
                "Regenerating XML for invoiceId={}", invoice.getInvoiceId());

        // ── 2. Convert Invoice entity → InvoiceDTO ─────────────────────────
        InvoiceDTO invoiceDTO;
        try {
            invoiceDTO = invoiceReverseConverter.entityToDto(invoice);
        } catch (Exception e) {
            CustomLogging.logError("CONVERT_FAILED", invoice.getInvoiceId(),
                    "Failed to convert invoiceId={}: {}", invoice.getInvoiceId(), e.getMessage());
            throw new RuntimeException(
                    "Failed to convert invoice id=" + invoice.getInvoiceId() + ": " + e.getMessage(), e);
        }

        // ── 3. Determine requestFrom ───────────────────────────────────────
        String requestFrom = RequestFromEnum.MOBILE.equals(invoice.getRequestFromEnum())
                ? RequestFromEnum.MOBILE.toString()
                : RequestFromEnum.WEB.toString();

        // ── 4. Generate raw XML via FreeMarker template ────────────────────
        String rawXml = xmlGenerationService.generateXML(invoiceDTO, requestFrom

        );

        CustomLogging.logInfo(taxNumber, invoiceNumber, invoice.getInvoiceId(),
                "Base XML generated for invoiceId={}", invoice.getInvoiceId());

        // ── 5. Minify + Canonicalize  (SME: MinifyAndCanonicalizeXml step) ─
        String canonicalXml;
        try {
            canonicalXml = xmlCanonicalizer.minifyAndCanonicalize(rawXml)
                    .replaceAll("<\\?xml(.+?)\\?>", "");
            CustomLogging.logInfo(taxNumber, invoiceNumber, invoice.getInvoiceId(),
                    "XML canonicalized for invoiceId={}", invoice.getInvoiceId());
        } catch (Exception e) {
            CustomLogging.logError("CANONICALIZE_FAILED", invoice.getInvoiceId(),
                    "Canonicalization failed for invoiceId={}: {}", invoice.getInvoiceId(), e.getMessage());
            throw new RuntimeException(
                    "Canonicalization failed for invoiceId=" + invoice.getInvoiceId() + ": " + e.getMessage(), e);
        }

        // ── 6. Generate QR code  (SME: InvoiceQRGenerator step) ───────────
        String qrCode;
        if (StringUtils.isBlank(invoice.getQrCode())) {
            try {
                qrCode = qrGeneratorService.generateQRstatically(canonicalXml);
                CustomLogging.logInfo(taxNumber, invoiceNumber, invoice.getInvoiceId(),
                        "QR generated for invoiceId={}", invoice.getInvoiceId());
            } catch (Exception e) {
                CustomLogging.logError("QR_FAILED", invoice.getInvoiceId(),
                        "QR generation failed for invoiceId={}: {}", invoice.getInvoiceId(), e.getMessage());
                throw new RuntimeException(
                        "QR generation failed for invoiceId=" + invoice.getInvoiceId() + ": " + e.getMessage(), e);
            }

        } else {
            qrCode = invoice.getQrCode();
        }

        // ── 7. Call signing service  (SME: invoiceSigningForSme step) ──────
        //    POST { invoice: canonicalXml, qrCode } → signed XML
        if (!signingEnabled) {
            CustomLogging.logWarn("SIGNING_SKIPPED", taxNumber, invoiceNumber, invoice.getInvoiceId(),
                    "Signing skipped (signing.enabled=false) for invoiceId={}", invoice.getInvoiceId());
            return canonicalXml.getBytes(StandardCharsets.UTF_8);
        }

        String signedXml;
        try {
            signedXml = signingCallerService.sign(canonicalXml, qrCode, invoice);
        } catch (Exception e) {
            CustomLogging.logError("SIGNING_FAILED", invoice.getInvoiceId(),
                    "Signing failed for invoiceId={}: {}", invoice.getInvoiceId(), e.getMessage());
            throw new RuntimeException("Signing failed for invoiceId=" + invoice.getInvoiceId() + ": " + e.getMessage(), e);
        }

        CustomLogging.logInfo(taxNumber, invoiceNumber, invoice.getInvoiceId(),
                "Signing complete for invoiceId={}", invoice.getInvoiceId());

        return signedXml.getBytes(StandardCharsets.UTF_8);
    }
}
