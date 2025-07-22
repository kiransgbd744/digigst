package com.ey.advisory.app.data.entities.client;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Setter
@Getter
@Entity
@ToString
@Table(name = "GSTR_RETURN_STATUS")
public class GstrReturnStatusEntity {
	
	@Expose
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR_RETURN_STATUS_SEQ", allocationSize = 1)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;
	
	@Expose
	@SerializedName("Gstin")
	@Column(name = "GSTIN")
	protected String gstin;

	@Expose
	@SerializedName("taxPeriod")
	@Column(name = "TAXPERIOD")
	protected String taxPeriod;
	
	@Expose
	@SerializedName("returnType")
	@Column(name = "RETURN_TYPE")
	protected String returnType;
	
	@Expose
	@SerializedName("status")
	@Column(name = "STATUS")
	protected String status;
		
	@Expose
	@SerializedName("submitRefid")
	@Column(name = "SUBMIT_REF_ID")
	protected String submitRefid;
	
	@Expose
	@SerializedName("createdOn")
	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;
	
	@Expose
	@SerializedName("updatedOn")
	@Column(name = "UPDATED_ON")
	protected LocalDateTime updatedOn;
	
	@Expose
	@SerializedName("arnNo")
	@Column(name = "ARN_NO")
	protected String arnNo;
	
	@Expose
	@SerializedName("filingDate")
	@Column(name = "FILING_DATE")
	protected LocalDate filingDate;
	
	@Expose
	@Column(name = "IS_COUNTER_PARTY_GSTIN")
	protected Boolean isCounterPartyGstin;
	
	public GstrReturnStatusEntity(){}
	
	public GstrReturnStatusEntity(String gstin, String taxPeriod,
			String returnType, String status, LocalDateTime createdOn) {
		super();
		this.gstin = gstin;
		this.taxPeriod = taxPeriod;
		this.returnType = returnType;
		this.status = status;
		this.createdOn = createdOn;
		this.updatedOn = createdOn;
		this.isCounterPartyGstin = false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gstin == null) ? 0 : gstin.hashCode());
		result = prime * result
				+ ((returnType == null) ? 0 : returnType.hashCode());
		result = prime * result
				+ ((taxPeriod == null) ? 0 : taxPeriod.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GstrReturnStatusEntity other = (GstrReturnStatusEntity) obj;
		if (gstin == null) {
			if (other.gstin != null)
				return false;
		} else if (!gstin.equals(other.gstin))
			return false;
		if (returnType == null) {
			if (other.returnType != null)
				return false;
		} else if (!returnType.equals(other.returnType))
			return false;
		if (taxPeriod == null) {
			if (other.taxPeriod != null)
				return false;
		} else if (!taxPeriod.equals(other.taxPeriod))
			return false;
		return true;
	}
	
	

}
