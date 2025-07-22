
package com.ey.advisory.ewb.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemList implements Serializable
{

    @SerializedName("itemNo")
    @Expose
	@XmlElement(name = "item-no")
    private Integer itemNo;
    @SerializedName("productId")
    @Expose
	@XmlElement(name = "product-id")
    private String productId;
    @SerializedName("productName")
    @Expose
	@XmlElement(name = "product-name")
    private String productName;
    @SerializedName("productDesc")
    @Expose
	@XmlElement(name = "product-desc")
    private String productDesc;
    @SerializedName("hsnCode")
    @Expose
	@XmlElement(name = "hsn-code")
    private String hsnCode;
    @SerializedName("quantity")
    @Expose
	@XmlElement(name = "quantity")
    private BigDecimal quantity;
    @SerializedName("qtyUnit")
    @Expose
	@XmlElement(name = "qty-unit")
    private String qtyUnit;
    @SerializedName("cgstRate")
    @Expose
	@XmlElement(name = "cgst-rate")
    private BigDecimal cgstRate;
    @SerializedName("sgstRate")
    @Expose
	@XmlElement(name = "sgst-rate")
    private BigDecimal sgstRate;
    @SerializedName("igstRate")
    @Expose
	@XmlElement(name = "igst-rate")
    private BigDecimal igstRate;
    @SerializedName("cessRate")
    @Expose
	@XmlElement(name = "cess-rate")
    private BigDecimal cessRate;
    @SerializedName("cessNonAdvol")
    @Expose
	@XmlElement(name = "cessnonadvol")
    private BigDecimal cessNonAdvol;
    @SerializedName("taxableAmount")
    @Expose
	@XmlElement(name = "taxable-amt")
    private BigDecimal taxableAmount;
    private static final  long serialVersionUID = 7886704541457221667L;

}
