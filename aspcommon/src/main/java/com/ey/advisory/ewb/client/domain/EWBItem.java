package com.ey.advisory.ewb.client.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;



/**
 * 
 * @author Arun.KA
 *
 */

@Entity
@Table(name="EWB_ITEM")
@Getter
@Setter
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class EWBItem {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@XmlTransient
	protected Long id;
	
	@Column(name = "ITEM_NO")
	@Expose 
	@SerializedName("itemNo")
	@XmlElement(name = "ITEM_NO")
	protected Integer itemNo;
	
	@Column(name = "PRODUCT_ID")
	@Expose 
	@SerializedName("productId")
	@XmlElement(name = "PRODUCT_ID")
	protected String productId;
	
	@Column(name = "PRODUCT_NAME")
	@Expose @SerializedName("productName")
	@XmlElement(name = "PRODUCT_NAME")
	protected String productName;
	
	@Column(name = "PRODUCT_DESC")
	@Expose @SerializedName("productDesc")
	@XmlElement(name = "PRODUCT_DESC")
	protected String productDescription;
	
	@Column(name = "HSN_CODE")
	@Expose 
	@SerializedName("hsnCode")
	@XmlElement(name = "HSN_CODE")
	protected String hsnCode;
	
	@Column(name="QUANTITY")
	@Expose 
	@SerializedName("quantity")
	@XmlElement(name = "QUANTITY")
	protected BigDecimal quantity;
	
	@Column(name="QTY_UNIT")
	@Expose 
	@SerializedName("qtyUnit")
	@XmlElement(name = "QTY_UNIT")
	protected String qtyUnit;
	
	@Column(name = "CGST_RATE") 
	@Expose
	@SerializedName("cgstRate")
	@XmlElement(name = "CGST_RATE")
	protected BigDecimal CGSTRate;
	
	@Column(name = "SGST_RATE")
	@Expose 
	@SerializedName("sgstRate")
	@XmlElement(name = "SGST_RATE")
	protected BigDecimal SGSTRate;
	
	@Column(name = "IGST_RATE")
	@Expose
	@SerializedName("igstRate")
	@XmlElement(name = "IGST_RATE")
	protected BigDecimal igstRate;
	
	@Column(name = "CESS_RATE")
	@Expose
	@SerializedName("cessRate")
	@XmlElement(name = "CESS_RATE")
	protected BigDecimal cessRate;
	
	@Column(name = "CESS_NON_ADVOL")
	@Expose
	@SerializedName("cessNonAdvol")
	@XmlElement(name = "CESS_NON_ADVOL")
	protected BigDecimal cessNonAdvol;
	
	@Column(name = "TAXABLE_AMOUNT")
	@Expose
	@SerializedName("taxableAmount")
	@XmlElement(name = "TAXABLE_AMOUNT")
	protected BigDecimal taxableAmount;
	
	@Column(name = "CREATED_BY")
	@Expose
	@SerializedName("createdBy")
	@XmlElement(name = "CREATED_BY")
	protected String createdBy;
	
	@Column(name = "CREATED_ON")
	@Expose 
	@SerializedName("createdOn")
	@XmlElement(name = "CREATED_ON")
	protected LocalDateTime createdOn;
	
	@Column(name = "MODIFIED_BY")
	@Expose 
	@SerializedName("modifiedBy")
	@XmlElement(name = "MODIFIED_BY")
	protected String modifiedBy;
	
	@Column(name = "MODIFIED_ON")
	@Expose
	@SerializedName("modifiedOn")
	@XmlElement(name = "MODIFIED_ON")
	protected LocalDateTime modifiedOn;
	
	@Column(name = "IS_DELETE")
	@Expose 
	@SerializedName("isDelete")
	@XmlElement(name = "IS_DELETE")
	protected Boolean isDelete;
	
	@ManyToOne
	@JoinColumn(name="EWB_GET_ID", referencedColumnName="ID")
	@XmlTransient
	protected EWBHeader eWayBill;
}
