package com.ey.advisory.app.data.entities.gstr9;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author Hema G M
 *
 */

@Data
@Entity
@Table(name = "TBL_GSTR9_READY_STATUS_FY")
public class Gstr9ComputeReadyStatusFYEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	protected Long id;

	@Expose
	@Column(name = "GSTIN")
	protected String gstin;

	@Expose
	@Column(name = "FY")
	protected Integer fy;
	
	@Expose
	@Column(name = "IS_GSTR1_GET_CALL_GREATER_FILING_DATE")
	protected boolean isGstr1GetCallGreaterFilingDate;
	
	@Expose
	@Column(name = "IS_GSTR1_AMD_GET_CALL_GREATER_FILING_DATE")
	protected boolean isGstr1AmdGetCallGreaterFilingDate;
	
	@Expose
	@Column(name = "IS_GSTR3B_GET_CALL_GREATER_FILING_DATE")
	protected boolean isGstr3bGetCallGreaterFilingDate;
	
	@Expose
	@Column(name = "IS_GSTR1_GET_COMPLETED")
	protected boolean isGstr1GetCompleted;
	
	@Expose
	@Column(name = "IS_GSTR3B_GET_COMPLETED")
	protected boolean isGstr3bGetCompleted;
	
	@Expose
	@Column(name = "IS_GSTR1_AMD_GET_COMPLETED")
	protected boolean isGstr1AmdGetCompleted;
	
	@Expose
	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;
	
	@Expose
	@Column(name = "UPDATED_ON")
	protected LocalDateTime updatedOn;
	
	public Gstr9ComputeReadyStatusFYEntity(){}
	
	public Gstr9ComputeReadyStatusFYEntity(String gstin, Integer fy, LocalDateTime createdOn) {
		super();
		this.gstin = gstin;
		this.fy = fy;
		this.createdOn = createdOn;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Gstr9ComputeReadyStatusFYEntity entity = (Gstr9ComputeReadyStatusFYEntity) obj;
		
		return this.gstin.equals(entity.getGstin()) && this.fy == entity.getFy();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fy == null) ? 0 : fy.hashCode());
		result = prime * result + ((gstin == null) ? 0 : gstin.hashCode());
		return result;
	}
	
	

}
