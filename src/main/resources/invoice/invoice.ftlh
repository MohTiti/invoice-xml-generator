<#ftl output_format="XML" encoding='UTF-8'>
<?xml version="1.0" encoding="UTF-8"?>
<Invoice xmlns="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:ext="urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2">
  <cbc:ProfileID>reporting:1.0</cbc:ProfileID>
  <cbc:ID>${invoiceDTO.invoiceNumber}</cbc:ID>
  <cbc:UUID>${invoiceDTO.invoiceUniqueIdentifier}</cbc:UUID>
  <cbc:IssueDate>${invoiceIssueDate}</cbc:IssueDate>
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
        <cbc:UUID>${invoiceDTO.originalInvoiceUUID}</cbc:UUID>
        <cbc:DocumentDescription>${(invoiceDTO.originalInvoiceTotal!0)?c}</cbc:DocumentDescription>
      </cac:InvoiceDocumentReference>
    </cac:BillingReference>
  </#if>
  <cac:AdditionalDocumentReference>
    <cbc:ID>ICV</cbc:ID>
    <cbc:UUID></cbc:UUID>
  </cac:AdditionalDocumentReference>
  <cac:AccountingSupplierParty>
    <cac:Party>
      <cac:PostalAddress>
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
        <cac:PartyIdentification>
          <#assign buyerId = invoiceDTO.buyerDTO.additionalBuyerId!'' />
          <#if buyerId?has_content>
            <cbc:ID schemeID="${additionalBuyerIdType!'NIN'}">${buyerId}</cbc:ID>
          <#else>
            <cbc:ID schemeID="${additionalBuyerIdType!'NIN'}" />
          </#if>
        </cac:PartyIdentification>
      <cac:PostalAddress>
          <cbc:PostalZone />
        <cac:Country>
          <cbc:IdentificationCode>JO</cbc:IdentificationCode>
        </cac:Country>
      </cac:PostalAddress>
      <cac:PartyTaxScheme>
        <cac:TaxScheme>
          <cbc:ID>VAT</cbc:ID>
        </cac:TaxScheme>
      </cac:PartyTaxScheme>
      <cac:PartyLegalEntity>
        <#if invoiceDTO.buyerDTO?? && invoiceDTO.buyerDTO.buyerName?? && invoiceDTO.buyerDTO.buyerName?has_content>
          <cbc:RegistrationName>${invoiceDTO.buyerDTO.buyerName}</cbc:RegistrationName>
        </#if>
      </cac:PartyLegalEntity>
    </cac:Party>
    <cac:AccountingContact>
      <#if invoiceDTO.buyerDTO.phoneNumber?has_content>
        <cbc:Telephone>${invoiceDTO.buyerDTO.phoneNumber}</cbc:Telephone>
      <#else>
        <cbc:Telephone />
      </#if>
    </cac:AccountingContact>
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
    <#if invoiceTypeValue == "381">
      <cac:PaymentMeans>
        <cbc:PaymentMeansCode listID="UN/ECE 4461">${paymentMethodCode}</cbc:PaymentMeansCode>
        <cbc:InstructionNote>${invoiceDTO.reasonOfNote?has_content?then(invoiceDTO.reasonOfNote, "credit note")}</cbc:InstructionNote>
      </cac:PaymentMeans>
    </#if>
  <cac:AllowanceCharge>
    <cbc:ChargeIndicator>false</cbc:ChargeIndicator>
    <cbc:AllowanceChargeReason>discount</cbc:AllowanceChargeReason>
    <cbc:Amount currencyID="JO">${(invoiceDTO.totalDiscountsAmount!0)?c}</cbc:Amount>
  </cac:AllowanceCharge>
  <cac:TaxTotal>
    <cbc:TaxAmount currencyID="JO">${(invoiceDTO.totalGeneralTaxesAmount!0)?c}</cbc:TaxAmount>
  </cac:TaxTotal>
  <cac:LegalMonetaryTotal>
    <cbc:TaxExclusiveAmount currencyID="JO">${(invoiceDTO.totalAmountExcludingTaxes!0)?c}</cbc:TaxExclusiveAmount>
    <cbc:TaxInclusiveAmount currencyID="JO">${(invoiceDTO.totalPayableAmount!0)?c}</cbc:TaxInclusiveAmount>
    <cbc:AllowanceTotalAmount currencyID="JO">${(invoiceDTO.totalDiscountsAmount!0)?c}</cbc:AllowanceTotalAmount>
    <cbc:PayableAmount currencyID="JO">${(invoiceDTO.totalPayableAmount!0)?c}</cbc:PayableAmount>
  </cac:LegalMonetaryTotal>
  <#list invoiceDTO.invoiceItemDTOList as invoiceItem>
    <cac:InvoiceLine>
      <cbc:ID>${invoiceItem?counter}</cbc:ID>
      <cbc:InvoicedQuantity unitCode="PCE">${(invoiceItem.quantity!0)?c}</cbc:InvoicedQuantity>
      <cbc:LineExtensionAmount currencyID="JO">${(invoiceItem.totalAmountAfterDiscount!0)?c}</cbc:LineExtensionAmount>
      <cac:TaxTotal>
        <cbc:TaxAmount currencyID="JO">${(invoiceItem.generalTaxAmount!0)?c}</cbc:TaxAmount>
        <cbc:RoundingAmount currencyID="JO">${(invoiceItem.totalAmountAfterTaxes!0)?c}</cbc:RoundingAmount>
        <#if isSpecialTax == "true">
          <cac:TaxSubtotal>
            <cbc:TaxableAmount currencyID="JO">${(invoiceItem.totalAmountAfterDiscount!0)?c}</cbc:TaxableAmount>
            <cbc:TaxAmount  currencyID="JO">${(invoiceItem.specialTaxAmount!0)?c}</cbc:TaxAmount>
            <cac:TaxCategory>
              <cbc:ID schemeID="UN/ECE 5305" schemeAgencyID="6">S</cbc:ID>
              <cac:TaxScheme>
                <cbc:ID schemeID="UN/ECE 5153" schemeAgencyID="6">OTH</cbc:ID>
              </cac:TaxScheme>
            </cac:TaxCategory>
          </cac:TaxSubtotal>
        </#if>
        <#if isGeneralTax == "true">
          <cac:TaxSubtotal>
            <cbc:TaxableAmount currencyID="JO">${(invoiceItem.totalAmountAfterDiscount!0)?c}</cbc:TaxableAmount>
            <cbc:TaxAmount currencyID="JO">${(invoiceItem.generalTaxAmount!0)?c}</cbc:TaxAmount>
            <cac:TaxCategory>
              <#if invoiceItem.generalTaxType == "ZERO">
                <cbc:ID schemeID="UN/ECE 5305" schemeAgencyID="6">O</cbc:ID>
              <#elseif invoiceItem.generalTaxType == "EXEMPT">
                <cbc:ID schemeID="UN/ECE 5305" schemeAgencyID="6">Z</cbc:ID>
              <#else>
                <cbc:ID schemeID="UN/ECE 5305" schemeAgencyID="6">S</cbc:ID>
              </#if>
              <cbc:Percent>${(invoiceItem.generalTaxPercentage!0)?c}</cbc:Percent>
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
          <cbc:PriceAmount currencyID="JO">${(invoiceItem.unitPrice!0)?c}</cbc:PriceAmount>
        <#elseif invoiceItem.invoiceItemType.name() == "SERVICE_CHARGE">
          <cbc:PriceAmount currencyID="JO">${(invoiceItem.totalAmountAfterDiscount!0)?c}</cbc:PriceAmount>
        </#if>
        <#if invoiceItem.discountAmount?has_content && invoiceItem.invoiceItemType.name() == "PRODUCT">
          <cac:AllowanceCharge>
            <cbc:ChargeIndicator>false</cbc:ChargeIndicator>
            <cbc:AllowanceChargeReason>DISCOUNT</cbc:AllowanceChargeReason>
            <cbc:Amount currencyID="JO">${(invoiceItem.discountAmount!0)?c}</cbc:Amount>
          </cac:AllowanceCharge>
        </#if>
        <#if invoiceItem.invoiceItemType.name() == "SERVICE_CHARGE">
          <cac:AllowanceCharge>
            <cbc:ChargeIndicator>true</cbc:ChargeIndicator>
            <cbc:AllowanceChargeReason>SERVICE_CHARGE</cbc:AllowanceChargeReason>
            <cbc:Amount currencyID="JO">${(invoiceItem.totalAmountAfterDiscount!0)?c}</cbc:Amount>
          </cac:AllowanceCharge>
        </#if>
      </cac:Price>
      <#if invoiceItem.customerPrice??>
        <cac:ItemPriceExtension>
          <cbc:Amount currencyID="JO">${(invoiceItem.customerPrice!0)?c}</cbc:Amount>
        </cac:ItemPriceExtension>
      </#if>
    </cac:InvoiceLine>
  </#list>
</Invoice>