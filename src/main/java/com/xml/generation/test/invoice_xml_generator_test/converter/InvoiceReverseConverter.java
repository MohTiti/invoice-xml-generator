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
import com.xml.generation.test.invoice_xml_generator_test.model.enums.InvoiceKind;
import com.xml.generation.test.invoice_xml_generator_test.utils.MapperUtil;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.*;

@Component
public class InvoiceReverseConverter {
    //Converting from entity to dto
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
            invoiceItemDTOList.add(invoiceItemDTO);

        }
        List<InvoiceItemDTO> invoiceItemDTOSortedList = new ArrayList<>(invoiceItemDTOList);
        invoiceItemDTOSortedList.sort(Comparator.comparing(InvoiceItemDTO::getIndex));
        return invoiceItemDTOSortedList;
//        return invoiceItemDTOList;
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
        if(buyer != null) {
            buyerDTO.setBuyerName(buyer.getBuyerName());
            buyerDTO.setAdditionalBuyerId(buyer.getAdditionalBuyerId());
            buyerDTO.setAdditionalBuyerIdType(buyer.getAdditionalBuyerIdType());
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

        // Basic invoice details with null checks
        if (invoice.getInvoiceNumber() != null) {
            invoiceDTO.setInvoiceNumber(invoice.getInvoiceNumber());
        }

        if (invoice.getInvoiceStatus() != null) {
            invoiceDTO.setInvoiceStatus(invoice.getInvoiceStatus());
        }

        if (invoice.getInvoiceTypeCode() != null) {
            invoiceDTO.setInvoiceTypeCode(invoice.getInvoiceTypeCode());
        }

        if (invoice.getIssueDate() != null) {
            invoiceDTO.setIssueDate(invoice.getIssueDate().toLocalDate());
        }

        if (invoice.getBuyerInvoiceNumber() != null) {
            invoiceDTO.setBuyerInvoiceNumber(invoice.getBuyerInvoiceNumber());
        }

        if (invoice.getInvoiceUniqueIdentifier() != null) {
            invoiceDTO.setInvoiceUniqueIdentifier(invoice.getInvoiceUniqueIdentifier());
        }

        if (invoice.getQrCode() != null) {
            invoiceDTO.setQrCode(invoice.getQrCode());
        }

        // Calculate total taxes with null checks
        if (invoice.getTotalGeneralTaxesAmount() != null && invoice.getTotalSpecialTaxesAmount() != null) {
            invoiceDTO.setTotalTaxes(invoice.getTotalGeneralTaxesAmount().add(invoice.getTotalSpecialTaxesAmount()));
        } else if (invoice.getTotalGeneralTaxesAmount() != null) {
            invoiceDTO.setTotalTaxes(invoice.getTotalGeneralTaxesAmount());
        } else if (invoice.getTotalSpecialTaxesAmount() != null) {
            invoiceDTO.setTotalTaxes(invoice.getTotalSpecialTaxesAmount());
        }

        if (invoice.getRequestFromEnum() != null) {
            invoiceDTO.setRequestFrom(invoice.getRequestFromEnum());
        }

        // QR Code image generation (already had null check)
        if (invoice.getQrCode() != null) {
            invoiceDTO.setQrCodeImage(getQRImage(invoice.getQrCode()));
        }

        // XML file processing (already had null check)
        if (invoice.getXmlFile() != null) {
            byte[] xmlFileBytes = invoice.getXmlFile();
            String xmlFileString = new String(xmlFileBytes, StandardCharsets.UTF_8);
            invoiceDTO.setXml(xmlFileString);
        }

        if (invoice.getInvoiceKind() != null) {
            String kindCode = MapperUtil.mapITToCode(invoice.getInvoiceKind());
            invoiceDTO.setInvoiceKind(InvoiceKind.valueOf(kindCode));
        }

        if (invoice.getCurrency() != null) {
            invoiceDTO.setCurrencyEnum(invoice.getCurrency());
        }

        if (invoice.getNotes() != null) {
            invoiceDTO.setNotes(invoice.getNotes());
        }

        if (invoice.getNoteType() != null) {
            invoiceDTO.setNoteType(invoice.getNoteType());
        }

        if (invoice.getReasonOfNote() != null) {
            invoiceDTO.setReasonOfNote(invoice.getReasonOfNote());
        }

        // Original invoice details with null checks
        if (invoice.getOriginalInvoice() != null) {
            if (invoice.getOriginalInvoice().getInvoiceNumber() != null) {
                invoiceDTO.setOriginalInvoiceNumber(invoice.getOriginalInvoice().getInvoiceNumber());
            }
            if (invoice.getOriginalInvoice().getTotalPayableAmount() != null) {
                invoiceDTO.setOriginalInvoiceTotal(invoice.getOriginalInvoice().getTotalPayableAmount());
            }
        }

        // Financial amounts with null checks
        if (invoice.getTotalExcludingTaxes() != null) {
            invoiceDTO.setTotalAmountExcludingTaxes(invoice.getTotalExcludingTaxes());
        }

        if (invoice.getTotalDiscountsAmount() != null) {
            invoiceDTO.setTotalDiscountsAmount(invoice.getTotalDiscountsAmount());
        }

        if (invoice.getTotalGeneralTaxesAmount() != null) {
            invoiceDTO.setTotalGeneralTaxesAmount(invoice.getTotalGeneralTaxesAmount());
        }

        if (invoice.getTotalSpecialTaxesAmount() != null) {
            invoiceDTO.setTotalSpecialTaxesAmount(invoice.getTotalSpecialTaxesAmount());
        }

        if (invoice.getTotalPayableAmount() != null) {
            invoiceDTO.setTotalPayableAmount(invoice.getTotalPayableAmount());
        }

        if (invoice.getRate() != null) {
            invoiceDTO.setRate(invoice.getRate());
        }

        if (invoice.getRateDate() != null) {
            invoiceDTO.setRate_date(invoice.getRateDate());
        }
        if(invoice.getSigned() != null){
            invoiceDTO.setSignedInvoice(invoice.getSigned());
        }
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
