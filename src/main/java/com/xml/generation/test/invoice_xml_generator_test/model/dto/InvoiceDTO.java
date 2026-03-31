package com.xml.generation.test.invoice_xml_generator_test.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xml.generation.test.invoice_xml_generator_test.model.IResponse;
import com.xml.generation.test.invoice_xml_generator_test.model.dto.ActivityDTO;
import com.xml.generation.test.invoice_xml_generator_test.model.dto.BuyerDTO;
import com.xml.generation.test.invoice_xml_generator_test.model.dto.InvoiceItemDTO;
import com.xml.generation.test.invoice_xml_generator_test.model.dto.SellerDTO;
import com.xml.generation.test.invoice_xml_generator_test.model.enums.InvoiceStatusEnum;
import com.xml.generation.test.invoice_xml_generator_test.model.enums.InvoiceTypeEnum;
import com.xml.generation.test.invoice_xml_generator_test.model.enums.NoteType;
import com.xml.generation.test.invoice_xml_generator_test.model.enums.RequestFromEnum;
import com.xml.generation.test.invoice_xml_generator_test.model.lookup.CurrencyLookupDto;
import com.xml.generation.test.invoice_xml_generator_test.model.lookup.InvoiceTypeLookupDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDTO implements Serializable , IResponse {
    private InvoiceTypeEnum invoiceTypeCode;
    private String invoiceNumber;
    private String buyerInvoiceNumber;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate issueDate;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime issueTime;
    private InvoiceStatusEnum invoiceStatus;
    private NoteType noteType;
    private String invoiceKindCode;
    private InvoiceTypeLookupDto invoiceTypeLookupDto;
    private String currencyEnum;
    private CurrencyLookupDto currencyLookupDto;
    private String invoiceUniqueIdentifier;
    private String originalInvoiceNumber;
    private String originalInvoiceUUID;
    private BigDecimal originalInvoiceTotal;
    private String qrCode;
    private String qrCodeImage;
    private String reasonOfNote;
    private String exemptionReason;
    private String reasonOfExemption;
    private String notes;
    private SellerDTO sellerDTO;
    private BuyerDTO buyerDTO;
    private ActivityDTO activityDTO;
    private BigDecimal totalAmountExcludingTaxes;
    private BigDecimal totalDiscountsAmount;
    private BigDecimal totalGeneralTaxesAmount;
    private BigDecimal totalSpecialTaxesAmount = new BigDecimal(BigInteger.ZERO);
    private BigDecimal totalPayableAmount;
    private BigDecimal totalTaxes ;
    private BigDecimal totalAmountAfterSpecialTax;
    private List<InvoiceItemDTO> invoiceItemDTOList = new ArrayList<>();
    private String xml;
    private RequestFromEnum requestFrom;
    private LocalDateTime rate_date;
    private BigDecimal rate;
    private boolean isSignedInvoice;
    private boolean isRequireSmsMsg;
    private String buyerTaxNumber;
}
