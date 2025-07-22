package com.ey.advisory.app.gstr3b;

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
/**
 * 
 * @author vishal.verma
 *
 */

@Setter
@Getter
@Entity
@Table(name = "TBL_GSTR3B_RULE86B_LOG")
public class Gstr3BRule86BEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Expose
	@SerializedName("Gstin")
	@Column(name = "GSTIN")
	protected String gstin;

	@Expose
	@SerializedName("taxPeriod")
	@Column(name = "TAX_PERIOD")
	protected String taxPeriod;
	
	@Expose
	@SerializedName("isRule86B")
	@Column(name = "IS_RULE86B")
	protected boolean isRule86B;
	
	@Expose
	@SerializedName("isActive")
	@Column(name = "IS_ACTIVE")
	protected boolean isActive;
	
	@Expose
	@SerializedName("createdDate")
	@Column(name = "CREATED_DATE")
	protected LocalDateTime createdDate;
	
	@Expose
	@SerializedName("createdBy")
	@Column(name = "CREATED_BY")
	protected String createdBy;
	

}