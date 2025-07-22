/**
 * 
 */
package com.ey.advisory.ewb.data.entities.clientBusiness;

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
@Table(name="GET_COUNTER_PARTY_INVOC_CONTROL")
@Getter
@Setter
@ToString
public class CounterPartyInvocControlEntity {
	
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	protected Long id;
	
	@Column(name = "EWB_GEN_DATE")
	@Expose 
	@SerializedName("ewbGenDate")
	protected LocalDate ewbGenDate;
	
	@Column(name = "GSTIN")
	@Expose 
	@SerializedName("gstin")
	protected String gstin;
	
	@Column(name = "INVOC_START")
	@Expose 
	@SerializedName("invocSt")
	protected LocalDateTime invocSt;
	
	@Column(name = "INVOC_END")
	@Expose
	@SerializedName("invocEnd")
	protected LocalDateTime invocEnd;
		
	@Column(name = "STATUS")
	@Expose 
	@SerializedName("status")
	protected String status;	
	
	@Column(name = "ERROR_MSG")
	@Expose 
	@SerializedName("errorMsg")
	protected String errorMsg;
	
	@Column(name = "CREATED_BY")
	@Expose 
	@SerializedName("createdBy")
	protected String createdBy;
	
	@Column(name = "CREATED_ON")
	@Expose @SerializedName("createdOn")
	protected LocalDateTime createdOn;
	
	@Column(name = "REV_INT_STATUS")
	@Expose
	@SerializedName("revIntStatus")
	protected String revIntStatus;
	

}
