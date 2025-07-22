package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * 
 * @author Dibyakanta.Sahoo
 *
 */
@Entity
@Table(name = "GETGSTR6_ISD_UNIT_SUMMARY")
@Data
public class Getgstr6IsdUnitSummaryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private long id;

	@ManyToOne
	@JoinColumn(name = "HEADER_ID", referencedColumnName = "ID", nullable = false)
	private Getgstr6IsdSummaryEntity headerId;

	@Column(name = "CTIN")
	private String ctin;

	@Column(name = "CHKSUM")
	private String chksum;

	@Column(name = "RECORD_COUNT")
	private Integer recordCount;

	@Column(name = "TOTAL_VALUE")
	private BigDecimal taxableValue;

	@Column(name = "TOTAL_TAXABLE_VALUE")
	private BigDecimal totalTaxaBleValue;

	@Column(name = "TOTAL_IGST")
	private BigDecimal ttigst;

	@Column(name = "TOTAL_CGST")
	private BigDecimal ttcgst;

	@Column(name = "TOTAL_SGST")
	private BigDecimal ttsgst;

	@Column(name = "TOTAL_CESS")
	private BigDecimal ttcess;

	@Column(name = "DERIVED_RET_PERIOD")
	private int deriviedReturnPeriod;

	@Column(name = "IS_DELETE")
	private boolean isdelete;

	/*
	 * @OneToMany(mappedBy = "headerId",cascade = CascadeType.ALL)
	 * // @JoinColumn(name = "SEC_SUMM_ID"); private
	 * Set<GetAnx2ActionSummaryEntity> secSumId;
	 */

}
