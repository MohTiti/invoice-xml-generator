package com.xml.generation.test.invoice_xml_generator_test.model.entity;


import com.xml.generation.test.invoice_xml_generator_test.model.enums.GeneralTaxType;
import com.xml.generation.test.invoice_xml_generator_test.model.enums.InvoiceItemTypeEnum;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_item")
@Data
public class InvoiceItem {
    @Id
    @GeneratedValue
    private int id;

    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "product_description", nullable = false, columnDefinition = "VARCHAR2(500)")
    private String productDescription;

    @Column(name = "standard_item_identification")
    private String standardItemIdentification;
    @Column(name = "seller_item_identification")
    private String sellerItemIdentification;
    @Column(name = "invoice_item_type", columnDefinition = "VARCHAR(15)")
    @Enumerated(EnumType.STRING)
    private InvoiceItemTypeEnum invoiceItemType;

    @Column(name = "quantity", precision = 38, scale = 9)
    private BigDecimal quantity;

    @Column(name = "unit_price", precision = 38, scale = 9)
    private BigDecimal unitPrice;

    @Column(name = "customerPrice", precision = 38, scale = 9)
    private BigDecimal customerPrice;

    @Column(name = "sub_total_amount", precision = 38, scale = 9, nullable = false)
    private BigDecimal subtotalAmount;


    @Column(name = "discount_amount", precision = 38, scale = 9)
    private BigDecimal discountAmount;

    @Column(name = "total_amount_after_discount", precision = 38, scale = 9)
    private BigDecimal totalAmountAfterDiscount;

    @Column(name = "special_tax_amount", precision = 38, scale = 9)
    private BigDecimal specialTaxAmount;

    @Column(name = "total_after_special_tax", precision = 38, scale = 9)
    private BigDecimal totalAfterSpecialTax;

    @Column(name = "general_tax_type")
    @Enumerated(EnumType.STRING)
    private GeneralTaxType generalTaxType;

    @Column(name = "general_tax_percentage", precision = 38, scale = 9)
    private BigDecimal generalTaxPercentage;

    @Column(name = "general_tax_amount", precision = 38, scale = 9)
    private BigDecimal generalTaxAmount;

    @Column(name = "total_Amount_After_Taxes", precision = 38, scale = 9, nullable = false)
    private BigDecimal totalAmountAfterTaxes;

    @Column(name = "margin_of_error", precision = 10, scale = 9, nullable = false)
    private BigDecimal marginOfError = BigDecimal.ZERO;

    @Column(name = "isic4", nullable = false, columnDefinition = "VARCHAR2(2000)")
    private String isic4;

    @Column(name = "item_index", nullable = false)
    private int index;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

}
