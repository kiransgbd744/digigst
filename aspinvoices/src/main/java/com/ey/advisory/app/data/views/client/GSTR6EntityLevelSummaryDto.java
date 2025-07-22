package com.ey.advisory.app.data.views.client;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Data
public class GSTR6EntityLevelSummaryDto {

    private String GSTIN;
    private String ReturnPeriod;
    private String taxDocType;
    private String TableDescription;
    private Integer Count;
    private BigDecimal invoiceValue = BigDecimal.ZERO;
    private BigDecimal taxableValue = BigDecimal.ZERO;
    private BigDecimal totalTax = BigDecimal.ZERO;
    private BigDecimal IGST = BigDecimal.ZERO;
    private BigDecimal CGST = BigDecimal.ZERO;
    private BigDecimal SGST = BigDecimal.ZERO;
    private BigDecimal Cess = BigDecimal.ZERO;
    
    
    

    public String getGSTIN() {
        return GSTIN;
    }

    public void setGSTIN(String gSTIN) {
        GSTIN = gSTIN;
    }

    public String getTableDescription() {
        return TableDescription;
    }

    public void setTableDescription(String tableDescription) {
        TableDescription = tableDescription;
    }

    public Integer getCount() {
        return Count;
    }

    public void setCount(Integer count) {
        Count = count;
    }

    public BigDecimal getInvoiceValue() {
        return invoiceValue;
    }

    public void setInvoiceValue(BigDecimal invoiceValue) {
        this.invoiceValue = invoiceValue;
    }

    public BigDecimal getTaxableValue() {
        return taxableValue;
    }

    public void setTaxableValue(BigDecimal taxableValue) {
        this.taxableValue = taxableValue;
    }

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    public BigDecimal getIGST() {
        return IGST;
    }

    public void setIGST(BigDecimal iGST) {
        IGST = iGST;
    }

    public BigDecimal getCGST() {
        return CGST;
    }

    public void setCGST(BigDecimal cGST) {
        CGST = cGST;
    }

    public BigDecimal getSGST() {
        return SGST;
    }

    public void setSGST(BigDecimal sGST) {
        SGST = sGST;
    }

    public BigDecimal getCess() {
        return Cess;
    }

    public void setCess(BigDecimal cess) {
        Cess = cess;
    }

    public GSTR6EntityLevelSummaryDto(String gSTIN, String returnPeriod,
            String taxDocType, String tableDescription, Integer count,
            BigDecimal invoiceValue, BigDecimal taxableValue,
            BigDecimal totalTax, BigDecimal iGST, BigDecimal cGST,
            BigDecimal sGST, BigDecimal cess) {
        super();
        GSTIN = gSTIN;
        ReturnPeriod = returnPeriod;
        this.taxDocType = taxDocType;
        TableDescription = tableDescription;
        Count = count;
        this.invoiceValue = invoiceValue;
        this.taxableValue = taxableValue;
        this.totalTax = totalTax;
        IGST = iGST;
        CGST = cGST;
        SGST = sGST;
        Cess = cess;
    }

    public GSTR6EntityLevelSummaryDto() {
        super();
    }
}
