/**
 * 
 */
package com.ey.advisory.ewb.data.entities.clientBusiness;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Arun.KA
 *
 */

@Entity
@Table(name="COUNTER_PARTY_EWAY_BILLS")
@Getter
@Setter
@ToString
public class CounterPartyEwbBillEntity {
	
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	protected Long id;
	
	@Column(name = "EWB_NO")
	@Expose 
	@SerializedName("ewbNo")
	protected String ewbNo;
	
	@Column(name = "EWAY_BILL_DATE")
	@Expose 
	@SerializedName("ewbDate")
	protected LocalDateTime ewbDate;
	
	@Column(name = "GEN_MODE")
	@Expose 
	@SerializedName("genMode")
	protected String genMode;
	
	@Column(name = "GEN_GSTIN")
	@Expose
	@SerializedName("genGstin")
	protected String genGstin;
		
	@Column(name = "DOC_NO")
	@Expose 
	@SerializedName("docNo")
	protected String docNo;
	
	@Column(name = "DOC_DATE")
	@Expose 
	@SerializedName("docDate")
	protected LocalDateTime docDate;
	
	@Column(name = "FROM_GSTIN")
	@Expose 
	@SerializedName("fromGstin")
	protected String fromGstin;	
	
	@Column(name = "FROM_TRADE_NAME")
	@Expose
	@SerializedName("fromTradeName")
	protected String fromTradeName;
	
	@Column(name = "TO_GSTIN")
	@Expose 
	@SerializedName("toGstin")
	protected String toGstin;
	
	@Column(name = "TO_TRADENAME")
	@Expose 
	@SerializedName("toTradeName")
	protected String toTradeName;
	
	@Column(name = "TOT_INV_VALUE")
	@Expose 
	@SerializedName("totInvVal")
	protected BigDecimal totInvVal = BigDecimal.ZERO;
	
	@Column(name = "HSN_CODE")
	@Expose 
	@SerializedName("hsnCode")
	protected Integer hsnCode;	
	
	@Column(name = "HSN_DESC")
	@Expose
	@SerializedName("hsnDesc")
	protected String hsnDesc;
	
	@Column(name = "STATUS")
	@Expose 
	@SerializedName("status")
	protected String status;
	
	@Column(name = "FETCH_STATUS")
	@Expose 
	@SerializedName("fetchStatus")
	protected String fetchStatus;
	
	@Column(name = "REJECT_STATUS")
	@Expose 
	@SerializedName("rejectStatus")
	protected String rejectStatus;
	
	@Column(name = "CREATED_BY")
	@Expose 
	@SerializedName("createdBy")
	protected String createdBy;
	
	@Column(name = "CREATED_ON")
	@Expose 
	@SerializedName("createdOn")
	protected LocalDateTime createdOn;
	
	@Column(name = "MODIFIED_BY")
	@Expose
	@SerializedName("modifiedBy")
	protected String modifiedBy;
	
	@Column(name = "MODIFIED_ON")
	@Expose 
	@SerializedName("modifiedOn")
	protected LocalDateTime modifiedOn;
	
	@Column(name="CONTROL_ID")
	@Expose 
	@SerializedName("controlId")
	protected Long controlId;
	
	@Column(name = "CLIENT_GSTIN")
	@Expose
	@SerializedName("clientGstin")
	protected String clientGstin;
	
	@Column(name = "REV_INT_STATUS")
	@Expose
	@SerializedName("revIntStatus")
	protected String revIntStatus;
	
	@Column(name = "EWB_GEN_DATE")
	@Expose 
	@SerializedName("ewbGenDate")
	protected LocalDate ewbGenDate;
	
	

}
