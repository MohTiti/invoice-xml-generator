package com.xml.generation.test.invoice_xml_generator_test.converter;

import com.beust.jcommander.internal.Maps;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.xml.generation.test.invoice_xml_generator_test.exception.FailedToGenerateQRImageException;
import com.xml.generation.test.invoice_xml_generator_test.model.dto.*;
import com.xml.generation.test.invoice_xml_generator_test.model.entity.*;
import com.xml.generation.test.invoice_xml_generator_test.model.lookup.*;
import com.xml.generation.test.invoice_xml_generator_test.model.lookup.Currency;
import com.xml.generation.test.invoice_xml_generator_test.service.impl.LookupFacade;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.*;

@Component
public class InvoiceReverseConverter {
    public InvoiceDTO entityToDto(Invoice invoice) throws FailedToGenerateQRImageException {
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setBuyerDTO(populateBuyerData(invoice.getBuyer()));
        if(invoice.getActivity() != null) {
            invoiceDTO.setActivityDTO(populateActivityData(invoice.getActivity()));
        }
        invoiceDTO.setSellerDTO(populateSellerData(invoice.getUser().getTaxpayer()));
        invoiceDTO.setInvoiceItemDTOList(populateInvoiceItems(invoice.getInvoiceItems()));
        populateInvoiceDetails(invoiceDTO, invoice);
        return invoiceDTO;
    }

    private List<InvoiceItemDTO> populateInvoiceItems(List<InvoiceItem> invoiceItems) {
        List<InvoiceItemDTO> invoiceItemDTOList = new ArrayList<>();
        for (InvoiceItem invoiceItem : invoiceItems) {
            InvoiceItemDTO invoiceItemDTO = new InvoiceItemDTO();
            invoiceItemDTO.setUuid(invoiceItem.getUuid());
            invoiceItemDTO.setIndex(invoiceItem.getIndex());
            invoiceItemDTO.setIsic4(invoiceItem.getIsic4());
            if(invoiceItem.getIsic4() != null){
                invoiceItemDTO.setIsic4Dto(LookupFacade.getIsic4Dto(invoiceItem.getIsic4()));
            }
            invoiceItemDTO.setIsic4(invoiceItem.getIsic4());
            invoiceItemDTO.setInvoiceItemType(invoiceItem.getInvoiceItemType());
            invoiceItemDTO.setProductDescription(invoiceItem.getProductDescription());
            invoiceItemDTO.setQuantity(invoiceItem.getQuantity());
            invoiceItemDTO.setUnitPrice(invoiceItem.getUnitPrice());
            invoiceItemDTO.setCustomerPrice((invoiceItem.getCustomerPrice()));
            invoiceItemDTO.setSubtotalAmount(invoiceItem.getSubtotalAmount());
            invoiceItemDTO.setDiscountAmount(invoiceItem.getDiscountAmount());
            invoiceItemDTO.setTotalAmountAfterDiscount(invoiceItem.getTotalAmountAfterDiscount());
            invoiceItemDTO.setSpecialTaxAmount(invoiceItem.getSpecialTaxAmount());
            invoiceItemDTO.setGeneralTaxPercentage(invoiceItem.getGeneralTaxPercentage());
            invoiceItemDTO.setGeneralTaxType(invoiceItem.getGeneralTaxType());
            invoiceItemDTO.setGeneralTaxAmount(invoiceItem.getGeneralTaxAmount());
            invoiceItemDTO.setTotalAmountAfterTaxes(invoiceItem.getTotalAmountAfterTaxes());
            invoiceItemDTO.setTotalAfterSpecialTax(invoiceItem.getTotalAfterSpecialTax());
            invoiceItemDTO.setStandardItemIdentification(invoiceItem.getStandardItemIdentification());
            invoiceItemDTO.setSellerItemIdentification(invoiceItem.getSellerItemIdentification());
            invoiceItemDTOList.add(invoiceItemDTO);

        }
        List<InvoiceItemDTO> invoiceItemDTOSortedList = new ArrayList<>(invoiceItemDTOList);
        invoiceItemDTOSortedList.sort(Comparator.comparing(InvoiceItemDTO::getIndex));
        return invoiceItemDTOSortedList;
    }

    private SellerDTO populateSellerData(Taxpayer taxpayer) {
        SellerDTO sellerDTO = new SellerDTO();
        sellerDTO.setName(taxpayer.getName());
        sellerDTO.setPostalCode(taxpayer.getPostalCode());
        sellerDTO.setMobileNumber(taxpayer.getMobileNumber());
        sellerDTO.setTaxNumber(taxpayer.getTaxNumber());
        if(taxpayer.getCountry() != null) {
            sellerDTO.setCountryDTO(new CountryDTO(taxpayer.getCountry().getCountryCode(), taxpayer.getCountry().getCountryNameEn(),  taxpayer.getCountry().getCountryNameAr()));
        }
        return sellerDTO;
    }

    private ActivityDTO populateActivityData(Activity activity) {
        ActivityDTO activityDTO = new ActivityDTO();
        activityDTO.setActivity(activity.getActivity());
        activityDTO.setDescription(activity.getDescription());
        return activityDTO;
    }

    private BuyerDTO populateBuyerData(Buyer buyer) {
        BuyerDTO buyerDTO = new BuyerDTO();
        AdditionalBuyerIdType luAdditionalBuyerIdTypeDto = null;
        if(buyer != null) {
            if(buyer.getAdditionalBuyerIdType() != null){
                luAdditionalBuyerIdTypeDto = LookupFacade.getAdditionalBuyerIdType(buyer.getAdditionalBuyerIdType());
            }
            buyerDTO.setBuyerName(buyer.getBuyerName());
            buyerDTO.setAdditionalBuyerId(buyer.getAdditionalBuyerId());
            buyerDTO.setAdditionalBuyerIdTypeLookupDto((luAdditionalBuyerIdTypeDto != null) ? new AdditionalBuyerIdTypeLookupDto(luAdditionalBuyerIdTypeDto.getCode() , luAdditionalBuyerIdTypeDto.getArabicDescription() , luAdditionalBuyerIdTypeDto.getEnglishDescription(), false): null);
            buyerDTO.setAdditionalBuyerIdTn(buyer.getAdditionalBuyerIdTn());
            buyerDTO.setAdditionalBuyerIdSin(buyer.getAdditionalBuyerIdSin());
            buyerDTO.setPhoneNumber(buyer.getPhoneNumber());
            buyerDTO.setPostalCode(buyer.getPostalCode());
            if (buyer.getProvince() != null) {
                Province province = buyer.getProvince();
                buyerDTO.setProvinceDTO(new ProvinceDTO(province.getProvinceCode(), province.getProvinceNameEn(), province.getProvinceNameAr()));
            }
        }else{
            buyerDTO.setBuyerName("");
        }
        return buyerDTO;
    }

    private void populateInvoiceDetails(InvoiceDTO invoiceDTO, Invoice invoice) throws FailedToGenerateQRImageException {
        invoiceDTO.setInvoiceNumber(invoice.getInvoiceNumber());
        invoiceDTO.setInvoiceStatus(invoice.getInvoiceStatus());
        invoiceDTO.setInvoiceTypeCode(invoice.getInvoiceTypeCode());
        invoiceDTO.setExemptionReason(invoice.getExemptionReason());
        invoiceDTO.setReasonOfExemption(invoice.getExemptionReason());
        invoiceDTO.setIssueDate(invoice.getIssueDate().toLocalDate());
        invoiceDTO.setIssueTime(invoice.getIssueTime());
        invoiceDTO.setBuyerInvoiceNumber(invoice.getBuyerInvoiceNumber());
        invoiceDTO.setInvoiceUniqueIdentifier(invoice.getInvoiceUniqueIdentifier());
        invoiceDTO.setQrCode(invoice.getQrCode());
        BigDecimal generalTaxes = (invoice.getTotalGeneralTaxesAmount()!= null) ? invoice.getTotalGeneralTaxesAmount() : BigDecimal.ZERO;
        invoiceDTO.setTotalTaxes(generalTaxes.add((invoice.getTotalSpecialTaxesAmount()!= null) ? invoice.getTotalSpecialTaxesAmount() : BigDecimal.ZERO));
        invoiceDTO.setRequestFrom(invoice.getRequestFromEnum());
        if(invoice.getQrCode() != null) {
            invoiceDTO.setQrCodeImage(getQRImage(invoice.getQrCode()));
        }
        if(invoice.getXmlFile() != null){
            byte[] xmlFileBytes = invoice.getXmlFile();
            String xmlFileString = new String(xmlFileBytes, StandardCharsets.UTF_8);
            invoiceDTO.setXml(xmlFileString);
        }
        Lu_InvoiceType luInvoiceTypeDTO = LookupFacade.getInvoiceType(invoice.getInvoiceKind());
        invoiceDTO.setInvoiceTypeLookupDto(new InvoiceTypeLookupDto(luInvoiceTypeDTO.getCode() , luInvoiceTypeDTO.getArabicDescription() , luInvoiceTypeDTO.getEnglishDescription() , (luInvoiceTypeDTO.getAllowedPercentage()== null) ? null : luInvoiceTypeDTO.getAllowedPercentage()));
        Currency luCurrency = LookupFacade.getCurrency(invoice.getCurrency());
        invoiceDTO.setCurrencyEnum(luCurrency.getCurrencyEnum());
        invoiceDTO.setCurrencyLookupDto(new CurrencyLookupDto(luCurrency.getCurrencyEnum() , luCurrency.getArabicDescription() , luCurrency.getEnglishDescription()));
        invoiceDTO.setNotes(invoice.getNotes());
        invoiceDTO.setNoteType(invoice.getNoteType());
        invoiceDTO.setReasonOfNote(invoice.getReasonOfNote());
        if(invoice.getOriginalInvoice() != null) {
            invoiceDTO.setOriginalInvoiceNumber(invoice.getOriginalInvoice().getInvoiceNumber());
            invoiceDTO.setOriginalInvoiceTotal(invoice.getOriginalInvoice().getTotalPayableAmount());
        }
        invoiceDTO.setNoteType(invoice.getNoteType());
        invoiceDTO.setTotalAmountExcludingTaxes(invoice.getTotalExcludingTaxes());
        invoiceDTO.setTotalDiscountsAmount(invoice.getTotalDiscountsAmount());
        invoiceDTO.setTotalGeneralTaxesAmount(invoice.getTotalGeneralTaxesAmount());
        invoiceDTO.setTotalSpecialTaxesAmount(invoice.getTotalSpecialTaxesAmount());
        invoiceDTO.setTotalPayableAmount(invoice.getTotalPayableAmount());
        invoiceDTO.setTotalAmountAfterSpecialTax(invoice.getTotalPayableAmount().subtract(invoice.getTotalGeneralTaxesAmount()));
        invoiceDTO.setRate(invoice.getRate());
        invoiceDTO.setRate_date(invoice.getRateDate());
    }
    private String getQRImage(String qrCode) throws FailedToGenerateQRImageException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Map<EncodeHintType, Object> hintMap = Maps.newHashMap();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        hintMap.put(EncodeHintType.MARGIN, Integer.valueOf(0));
        hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");


        try {
            BitMatrix matrix = new MultiFormatWriter().encode(qrCode,
                    BarcodeFormat.QR_CODE, 200, 200, hintMap);
            MatrixToImageWriter.writeToStream(matrix, "png", byteArrayOutputStream);
        } catch (IOException e) {
            throw new FailedToGenerateQRImageException();
        } catch (WriterException e) {
            throw new FailedToGenerateQRImageException();
        }
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());

    }
}
