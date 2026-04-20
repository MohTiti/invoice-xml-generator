<#ftl output_format="XML" encoding='UTF-8'>
<?xml version="1.0" encoding="UTF-8"?>
<Invoice xmlns="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:ext="urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2">
    <cbc:ProfileID>reporting:1.0</cbc:ProfileID>
    <cbc:ID>${invoiceDTO.invoiceNumber}</cbc:ID>
    <cbc:UUID>${invoiceDTO.invoiceUniqueIdentifier}</cbc:UUID>
    <cbc:IssueDate>${invoiceIssueDate}</cbc:IssueDate>
    <cbc:IssueTime>${invoiceIssueTime}</cbc:IssueTime>
    <cbc:InvoiceTypeCode name="${typeCodeWithKind}">${invoiceTypeValue}</cbc:InvoiceTypeCode>
    <#if invoiceDTO.notes?has_content>
        <cbc:Note>${invoiceDTO.notes}</cbc:Note>
    </#if>
    <cbc:DocumentCurrencyCode>${currencyEnum}</cbc:DocumentCurrencyCode>
    <cbc:TaxCurrencyCode>${currencyEnum}</cbc:TaxCurrencyCode>
    <#if invoiceDTO.buyerInvoiceNumber?has_content>
        <cac:OrderReference>
            <cbc:ID>${invoiceDTO.buyerInvoiceNumber}</cbc:ID>
        </cac:OrderReference>
    </#if>
    <#if invoiceDTO.originalInvoiceNumber?has_content>
        <cac:BillingReference>
            <cac:InvoiceDocumentReference>
                <cbc:ID>${invoiceDTO.originalInvoiceNumber}</cbc:ID>
                <cbc:DocumentDescription>${invoiceDTO.originalInvoiceTotal?string["0.000"]}</cbc:DocumentDescription>
            </cac:InvoiceDocumentReference>
        </cac:BillingReference>
    </#if>
    <cac:AdditionalDocumentReference>
        <cbc:ID>ICV</cbc:ID>
        <cbc:UUID></cbc:UUID>
    </cac:AdditionalDocumentReference>
    <cac:AccountingSupplierParty>
        <cac:Party>
            <cbc:EndpointID>${requestFrom}</cbc:EndpointID>
            <cac:PartyIdentification>
                <cbc:ID schemeID="TN">${invoiceDTO.sellerDTO.taxNumber}</cbc:ID>
            </cac:PartyIdentification>
            <cac:PostalAddress>
                <#if invoiceDTO.sellerDTO.postalCode?? && invoiceDTO.sellerDTO.postalCode?has_content>
                    <cbc:PostalZone>${invoiceDTO.sellerDTO.postalCode}</cbc:PostalZone>
                </#if>
                <cac:Country>
                    <cbc:IdentificationCode>JO</cbc:IdentificationCode>
                </cac:Country>
            </cac:PostalAddress>
            <cac:PartyTaxScheme>
                <cbc:CompanyID>${invoiceDTO.sellerDTO.taxNumber}</cbc:CompanyID>
                <cac:TaxScheme>
                    <cbc:ID>VAT</cbc:ID>
                </cac:TaxScheme>
            </cac:PartyTaxScheme>
            <cac:PartyLegalEntity>
                <cbc:RegistrationName>${invoiceDTO.activityDTO.description}</cbc:RegistrationName>
            </cac:PartyLegalEntity>
        </cac:Party>
    </cac:AccountingSupplierParty>
    <cac:AccountingCustomerParty>
        <cac:Party>
            <#if additionalBuyerIdType?has_content && additionalBuyerId?has_content>
                <cac:PartyIdentification>
                    <cbc:ID schemeID="${additionalBuyerIdType}">${additionalBuyerId}</cbc:ID>
                </cac:PartyIdentification>
            </#if>
            <cac:PostalAddress>
                <#if invoiceDTO.buyerDTO?? && invoiceDTO.buyerDTO.postalCode?? && invoiceDTO.buyerDTO.postalCode?has_content>
                    <cbc:PostalZone>${invoiceDTO.buyerDTO.postalCode}</cbc:PostalZone>
                </#if>
                <#if buyerProvinceCode?has_content>
                    <cbc:CountrySubentityCode>${buyerProvinceCode}</cbc:CountrySubentityCode>
                </#if>
                <cac:Country>
                    <cbc:IdentificationCode>JO</cbc:IdentificationCode>
                </cac:Country>
            </cac:PostalAddress>
            <cac:PartyTaxScheme>
                <#if buyerTaxNumber?has_content>
                    <cbc:CompanyID>${buyerTaxNumber!}</cbc:CompanyID>
                </#if>
                <cac:TaxScheme>
                    <cbc:ID>VAT</cbc:ID>
                </cac:TaxScheme>
            </cac:PartyTaxScheme>
            <cac:PartyLegalEntity>
                <#if invoiceDTO.buyerDTO?? && invoiceDTO.buyerDTO.buyerName?? && invoiceDTO.buyerDTO.buyerName?has_content>
                    <cbc:RegistrationName>${invoiceDTO.buyerDTO.buyerName}</cbc:RegistrationName>
                </#if>
                <#if buyerSerialIncomeNumber?has_content>
                    <cbc:CompanyID>${buyerSerialIncomeNumber}</cbc:CompanyID>
                </#if>
            </cac:PartyLegalEntity>
        </cac:Party>
        <#if invoiceDTO.buyerDTO?? && invoiceDTO.buyerDTO.phoneNumber?has_content>
            <cac:AccountingContact>
                <cbc:Telephone>${invoiceDTO.buyerDTO.phoneNumber}</cbc:Telephone>
            </cac:AccountingContact>
        </#if>
    </cac:AccountingCustomerParty>
    <#if activity?has_content>
        <cac:SellerSupplierParty>
            <cac:Party>
                <cac:PartyIdentification>
                    <cbc:ID>${activity}</cbc:ID>
                </cac:PartyIdentification>
            </cac:Party>
        </cac:SellerSupplierParty>
    </#if>
    <#if paymentMethodCode?has_content>
        <cac:PaymentMeans>
            <cbc:PaymentMeansCode listID="UN/ECE 4461">${paymentMethodCode}</cbc:PaymentMeansCode>
            <#if invoiceDTO.reasonOfNote?has_content>
                <cbc:InstructionNote>${invoiceDTO.reasonOfNote}</cbc:InstructionNote>
            </#if>
        </cac:PaymentMeans>
    </#if>
    <cac:AllowanceCharge>
        <cbc:ChargeIndicator>false</cbc:ChargeIndicator>
        <cbc:AllowanceChargeReason>discount</cbc:AllowanceChargeReason>
        <cbc:Amount currencyID="JO">${invoiceDTO.totalDiscountsAmount?string["0.000"]}</cbc:Amount>
    </cac:AllowanceCharge>
    <cac:TaxTotal>
        <cbc:TaxAmount currencyID="JO">${invoiceDTO.totalGeneralTaxesAmount?string["0.##;; roundingMode=halfUp"]}</cbc:TaxAmount>
        <#list taxCategoriesMap as key, value>
            <cac:TaxSubtotal>
                <cbc:TaxableAmount currencyID="JO">${(value.totalAmountAfterDiscount)?string["0.000"]}</cbc:TaxableAmount>
                <cbc:TaxAmount currencyID="JO">${value.generalTaxAmount?string["0.000"]}</cbc:TaxAmount>
                <cac:TaxCategory>
                    <#if key.name() == "EXEMPT">
                        <cbc:ID schemeID="UN/ECE 5305" schemeAgencyID="6">E</cbc:ID>
                    <#else>
                        <cbc:ID schemeID="UN/ECE 5305" schemeAgencyID="6">S</cbc:ID>
                    </#if>
                    <cbc:Percent>${key.percentage?string["0.000"]}</cbc:Percent>
                    <#if key.name() == "EXEMPT">
                        <cbc:TaxExemptionReasonCode>EXEMPT</cbc:TaxExemptionReasonCode>
                        <cbc:TaxExemptionReason>EXEMPT</cbc:TaxExemptionReason>
                    </#if>
                    <cac:TaxScheme>
                        <cbc:ID schemeID="UN/ECE 5153" schemeAgencyID="6">VAT</cbc:ID>
                    </cac:TaxScheme>
                </cac:TaxCategory>
            </cac:TaxSubtotal>
        </#list>
    </cac:TaxTotal>
    <cac:LegalMonetaryTotal>
        <cbc:LineExtensionAmount currencyID="JO">${(invoiceDTO.totalAmountExcludingTaxes)?string["0.000"]}</cbc:LineExtensionAmount>
        <cbc:TaxExclusiveAmount currencyID="JO">${(invoiceDTO.totalAmountExcludingTaxes)?string["0.000"]}</cbc:TaxExclusiveAmount>
        <cbc:TaxInclusiveAmount currencyID="JO">${(invoiceDTO.totalPayableAmount)?string["0.000"]}</cbc:TaxInclusiveAmount>
        <cbc:AllowanceTotalAmount currencyID="JO">${(invoiceDTO.totalDiscountsAmount)?string["0.000"]}</cbc:AllowanceTotalAmount>
        <cbc:PrepaidAmount currencyID="JO">0</cbc:PrepaidAmount>
        <cbc:PayableAmount currencyID="JO">${(invoiceDTO.totalPayableAmount)?string["0.000"]}</cbc:PayableAmount>
    </cac:LegalMonetaryTotal>
    <#list invoiceDTO.invoiceItemDTOList as invoiceItem>
        <cac:InvoiceLine>
            <cbc:ID>${invoiceItem?counter}</cbc:ID>
            <cbc:InvoicedQuantity unitCode="PCE">${invoiceItem.quantity?string["0.000"]}</cbc:InvoicedQuantity>
            <cbc:LineExtensionAmount currencyID="JO">${(invoiceItem.totalAmountAfterDiscount)?string["0.000"]}</cbc:LineExtensionAmount>
            <cac:TaxTotal>
                <cbc:TaxAmount currencyID="JO">${invoiceItem.generalTaxAmount?string["0.000"]}</cbc:TaxAmount>
                <cbc:RoundingAmount currencyID="JO">${invoiceItem.totalAmountAfterTaxes?string["0.000"]}</cbc:RoundingAmount>
                <#if isSpecialTax == "true" && invoiceItem.specialTaxAmount?has_content>
                    <cac:TaxSubtotal>
                        <cbc:TaxableAmount currencyID="JO">${invoiceItem.totalAmountAfterDiscount?string["0.000"]}</cbc:TaxableAmount>
                        <cbc:TaxAmount  currencyID="JO">${invoiceItem.specialTaxAmount?string["0.000"]}</cbc:TaxAmount>
                        <cac:TaxCategory>

                            <cbc:ID schemeID="UN/ECE 5305" schemeAgencyID="6">S</cbc:ID>
                            <cac:TaxScheme>
                                <cbc:ID schemeID="UN/ECE 5153" schemeAgencyID="6">OTH</cbc:ID>
                            </cac:TaxScheme>
                        </cac:TaxCategory>
                    </cac:TaxSubtotal>
                </#if>
                <#if invoiceItem.generalTaxType?has_content>
                    <cac:TaxSubtotal>
                        <cbc:TaxableAmount currencyID="JO">${invoiceItem.totalAmountAfterDiscount?string["0.000"]}</cbc:TaxableAmount>
                        <cbc:TaxAmount currencyID="JO">${invoiceItem.generalTaxAmount?string["0.000"]}</cbc:TaxAmount>
                        <cac:TaxCategory>
                            <#if invoiceItem.generalTaxType == "ZERO">
                                <cbc:ID schemeID="UN/ECE 5305" schemeAgencyID="6">O</cbc:ID>
                                <cbc:Percent>${invoiceItem.generalTaxPercentage?string["0.000"]}</cbc:Percent>
                            <#elseif invoiceItem.generalTaxType == "EXEMPT">
                                <cbc:ID schemeID="UN/ECE 5305" schemeAgencyID="6">Z</cbc:ID>
                                <cbc:Percent>${invoiceItem.generalTaxPercentage?string["0.000"]}</cbc:Percent>
                            <#else>
                                <cbc:ID schemeID="UN/ECE 5305" schemeAgencyID="6">S</cbc:ID>
                                <cbc:Percent>${invoiceItem.generalTaxPercentage?string["0.000"]}</cbc:Percent>
                            </#if>
                            <cac:TaxScheme>
                                <cbc:ID schemeID="UN/ECE 5153" schemeAgencyID="6">VAT</cbc:ID>
                            </cac:TaxScheme>
                        </cac:TaxCategory>
                    </cac:TaxSubtotal>
                </#if>
            </cac:TaxTotal>
            <cac:Item>
                <cbc:Name>${invoiceItem.productDescription}</cbc:Name>
            </cac:Item>
            <cac:Price>
                <#if invoiceItem.invoiceItemType.name() == "PRODUCT">
                    <cbc:PriceAmount currencyID="JO">${invoiceItem.unitPrice?string["0.000"]}</cbc:PriceAmount>
                <#elseif invoiceItem.invoiceItemType.name() == "SERVICE_CHARGE">
                    <cbc:PriceAmount currencyID="JO">${invoiceItem.totalAmountAfterDiscount?string["0.000"]}</cbc:PriceAmount>
                </#if>
                <cbc:BaseQuantity unitCode="C62">1</cbc:BaseQuantity>
                <#if invoiceItem.discountAmount?has_content && invoiceItem.invoiceItemType.name() == "PRODUCT">
                    <cac:AllowanceCharge>
                        <cbc:ChargeIndicator>false</cbc:ChargeIndicator>
                        <cbc:AllowanceChargeReason>DISCOUNT</cbc:AllowanceChargeReason>
                        <cbc:Amount currencyID="JO">${invoiceItem.discountAmount?string["0.000"]}</cbc:Amount>
                    </cac:AllowanceCharge>
                </#if>
                <#if invoiceItem.invoiceItemType.name() == "SERVICE_CHARGE">
                    <cac:AllowanceCharge>
                        <cbc:ChargeIndicator>true</cbc:ChargeIndicator>
                        <cbc:AllowanceChargeReason>SERVICE_CHARGE</cbc:AllowanceChargeReason>
                        <cbc:Amount currencyID="JO">${invoiceItem.totalAmountAfterDiscount?string["0.000"]}</cbc:Amount>
                    </cac:AllowanceCharge>
                </#if>
            </cac:Price>
            <#if invoiceItem.customerPrice??>
                <cac:ItemPriceExtension>
                    <cbc:Amount currencyID="JO">${invoiceItem.customerPrice?string["0.000"]}</cbc:Amount>
                </cac:ItemPriceExtension>
            </#if>
        </cac:InvoiceLine>
    </#list>
</Invoice>