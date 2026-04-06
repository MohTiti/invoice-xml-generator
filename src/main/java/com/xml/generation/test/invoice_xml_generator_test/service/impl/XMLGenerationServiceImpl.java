package com.xml.generation.test.invoice_xml_generator_test.service.impl;

import com.xml.generation.test.invoice_xml_generator_test.logging.CustomLogging;
import com.xml.generation.test.invoice_xml_generator_test.model.dto.InvoiceDTO;
import com.xml.generation.test.invoice_xml_generator_test.model.dto.InvoiceItemDTO;
import com.xml.generation.test.invoice_xml_generator_test.model.dto.LuInvoiceTypeDTO;
import com.xml.generation.test.invoice_xml_generator_test.model.dto.ProvinceDTO;
import com.xml.generation.test.invoice_xml_generator_test.model.enums.GeneralTaxType;
import com.xml.generation.test.invoice_xml_generator_test.model.enums.NoteType;
import com.xml.generation.test.invoice_xml_generator_test.model.enums.RequestFromEnum;
import com.xml.generation.test.invoice_xml_generator_test.service.XMLGenerationService;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static com.xml.generation.test.invoice_xml_generator_test.model.enums.InvoiceTypeEnum.*;


@Service
public class XMLGenerationServiceImpl implements XMLGenerationService {


    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Autowired
    private Template freemarkerTemplate;
    private final String mobileCode = "MOBILE";
    private final String webCode = "WEB";


    @Override
    public String generateXML(InvoiceDTO invoiceDTO, String requestFrom , LuInvoiceTypeDTO luInvoiceTypeDTO) {

        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        model.put("invoiceDTO", invoiceDTO);
        model.put("invoiceIssueDate", dateFormatter.format(invoiceDTO.getIssueDate()));
        model.put("invoiceIssueTime", timeFormatter.format(invoiceDTO.getIssueTime()));
        if (requestFrom.equals(RequestFromEnum.MOBILE.toString())) {
            model.put("requestFrom", mobileCode);
        }else if (requestFrom.equals(RequestFromEnum.WEB.toString())) {
            model.put("requestFrom", webCode);
        }


        if(luInvoiceTypeDTO != null){
            String code = invoiceDTO.getInvoiceTypeCode().getTypeCode();
            model.put("typeCodeWithKind", luInvoiceTypeDTO.getXmlDigitReference()+code.substring(1));
        }


        if(invoiceDTO.getNoteType() == null) {
            model.put("invoiceTypeValue", "388");
        } else if (NoteType.CREDIT_INVOICE.equals(invoiceDTO.getNoteType())) {
            model.put("invoiceTypeValue", "381");
        } else if (NoteType.DEBIT_INVOICE.equals(invoiceDTO.getNoteType())) {
            model.put("invoiceTypeValue", "383");
        }

        model.put("invoiceCounter", Integer.valueOf(invoiceDTO.getInvoiceNumber().replace("EIN", "")));
        model.put("currencyEnum", invoiceDTO.getCurrencyEnum());

        if(invoiceDTO.getBuyerDTO() != null) {
            if (invoiceDTO.getBuyerDTO().getAdditionalBuyerIdTypeLookupDto() != null && invoiceDTO.getBuyerDTO().getAdditionalBuyerIdTypeLookupDto().getCode() != null) {
                model.put("additionalBuyerIdType", LookupFacade.getAdditionalBuyerIdType(invoiceDTO.getBuyerDTO().getAdditionalBuyerIdTypeLookupDto().getCode()).getCode());
                model.put("additionalBuyerId" , invoiceDTO.getBuyerDTO().getAdditionalBuyerId());
            }
            if(invoiceDTO.getBuyerDTO().getAdditionalBuyerIdTn() != null){
                model.put("buyerTaxNumber", invoiceDTO.getBuyerDTO().getAdditionalBuyerIdTn());
            }
            if(invoiceDTO.getBuyerDTO().getAdditionalBuyerIdSin() != null){
                model.put("buyerSerialIncomeNumber", invoiceDTO.getBuyerDTO().getAdditionalBuyerIdSin());
            }

            ProvinceDTO buyerProvinceDTO = invoiceDTO.getBuyerDTO().getProvinceDTO();

            if (buyerProvinceDTO != null && buyerProvinceDTO.getProvinceCode() != null) {
                model.put("buyerProvinceCode", buyerProvinceDTO.getProvinceCode());
            }
        }

        if(invoiceDTO.getActivityDTO() != null) {
            model.put("activity", invoiceDTO.getActivityDTO().getActivity());
        }

        if(CASH_SPECIAL_TAX.equals(invoiceDTO.getInvoiceTypeCode())
                || RECEIVABLE_SPECIAL_TAX.equals(invoiceDTO.getInvoiceTypeCode())) {
            model.put("isSpecialTax", "true");
        } else {
            model.put("isSpecialTax", "false");
        }

        if(CASH_INCOME.equals(invoiceDTO.getInvoiceTypeCode())
                || CASH_GENERAL_TAX.equals(invoiceDTO.getInvoiceTypeCode())
                || CASH_SPECIAL_TAX.equals(invoiceDTO.getInvoiceTypeCode())) {
            model.put("paymentMethodCode", "10");
        } else {
            model.put("paymentMethodCode", "96");
        }
        Map<GeneralTaxType, InvoiceItemDTO> taxCategoriesMap = new HashMap<>();
        for (InvoiceItemDTO invoiceItemDTO : invoiceDTO.getInvoiceItemDTOList()) {
            if(invoiceItemDTO.getGeneralTaxType() != null) {
                if (taxCategoriesMap.containsKey(invoiceItemDTO.getGeneralTaxType())) {
                    InvoiceItemDTO invoiceVATCategoryDetails = taxCategoriesMap.get(invoiceItemDTO.getGeneralTaxType());
                    invoiceVATCategoryDetails.setTotalAmountAfterDiscount(invoiceVATCategoryDetails.getTotalAmountAfterDiscount().add(invoiceItemDTO.getTotalAmountAfterDiscount()));
                    invoiceVATCategoryDetails.setGeneralTaxAmount(invoiceVATCategoryDetails.getGeneralTaxAmount().add(invoiceItemDTO.getGeneralTaxAmount()));
                } else {
                    InvoiceItemDTO invoiceVATCategoryDetails = new InvoiceItemDTO();
                    invoiceVATCategoryDetails.setTotalAmountAfterDiscount(invoiceItemDTO.getTotalAmountAfterDiscount());
                    invoiceVATCategoryDetails.setGeneralTaxAmount(invoiceItemDTO.getGeneralTaxAmount());
                    taxCategoriesMap.put(invoiceItemDTO.getGeneralTaxType(), invoiceVATCategoryDetails);
                }
            }
        }
        model.put("taxCategoriesMap", taxCategoriesMap);
        try {
            freemarkerTemplate.process(model, stringWriter);
        } catch (TemplateException e) {
            CustomLogging.logError("100", null, "FreeMarker template processing failed: {}", e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            CustomLogging.logError("101", null, "FreeMarker IO error during XML generation: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        return stringWriter.getBuffer().toString();

    }
}
