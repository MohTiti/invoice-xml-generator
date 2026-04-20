package com.xml.generation.test.invoice_xml_generator_test.model.dto;

import com.xml.generation.test.invoice_xml_generator_test.model.enums.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDTO implements Serializable {
    private InvoiceTypeEnum invoiceTypeCode;
    private String invoiceNumber;
    private String buyerInvoiceNumber;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate issueDate;
    private InvoiceStatusEnum invoiceStatus;
    private NoteType noteType;
    private InvoiceKind invoiceKind;
    private CurrencyEnum currencyEnum;
    private String invoiceUniqueIdentifier;
    private String originalInvoiceNumber;
    private String originalInvoiceUUID;
    private BigDecimal originalInvoiceTotal;
    private String qrCode;
    private String qrCodeImage;
    private String reasonOfNote;
    private String notes;
    private SellerDTO sellerDTO;
    private BuyerDTO buyerDTO;
    private ActivityDTO activityDTO;
    private BigDecimal totalAmountExcludingTaxes;
    private BigDecimal totalDiscountsAmount;
    private boolean isSignedInvoice;
    private BigDecimal totalGeneralTaxesAmount;
    private BigDecimal totalSpecialTaxesAmount = new BigDecimal(BigInteger.ZERO);
    private BigDecimal totalPayableAmount;
    private BigDecimal totalTaxes ;
    private List<InvoiceItemDTO> invoiceItemDTOList = new ArrayList<>();
    private String xml;
    private RequestFromEnum requestFrom;
    private Date rate_date;
    private BigDecimal rate;
    private String logo;
}
