package com.ey.advisory.services.gstr3b.itc.reclaim;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
@Entity
@Table(name = "TBL_GSTR3B_ITC_RECLAIM")
public class Gstr3BGstinItcReclaimEntity {

	@Id
	@Expose
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Expose
	@Column(name = "GSTIN")
	private String gstin;

	@Expose
	@Column(name = "SECTION_NAME")
	private String sectionName;

	@Expose
	@Column(name = "IGST")
	private BigDecimal igst = BigDecimal.ZERO;

	@Expose
	@Column(name = "SGST")
	private BigDecimal sgst = BigDecimal.ZERO;

	@Expose
	@Column(name = "CGST")
	private BigDecimal cgst = BigDecimal.ZERO;

	@Expose
	@Column(name = "CESS")
	private BigDecimal cess = BigDecimal.ZERO;

	@Expose
	@Column(name = "CREATE_DATE")
	private LocalDateTime createDate;

	@Expose
	@Column(name = "UPDATE_DATE")
	private LocalDateTime updateDate;

	@Expose
	@Column(name = "CREATED_BY")
	private String createdBy;

	@Expose
	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@Expose
	@Column(name = "IS_ACTIVE")
	private Boolean isActive;

}
