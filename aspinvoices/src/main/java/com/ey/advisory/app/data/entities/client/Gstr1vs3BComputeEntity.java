package com.ey.advisory.app.data.entities.client;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Entity
@Table(name = "GSTR1_VS_3B_COMPUTE")
public class Gstr1vs3BComputeEntity {

	@Expose
	@SerializedName("id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "TAX_PERIOD")
	private String taxPeriod;
	
	@Column(name = "GSTIN")
	private String gstin;
	

	@Column(name = "SECTION_NAME")
	private String sectionName;


	@Column(name = "SUB_SECTION_NAME")
	private String subSectionName;

	@Column(name = "TAXABLE_VALUE")
	private String taxableValue;


	@Column(name = "IGST_AMT")
	private String igst;


	@Column(name = "CGST_AMT")
	private String cgst;
	

	@Column(name = "SGST_AMT")
	private String sgst;

	@Column(name = "CESS_AMT")
	private String cess;
	
	@Column(name = "DERIVED_RET_PERIOD")
	private int derivedRetPeriod;
	




}
