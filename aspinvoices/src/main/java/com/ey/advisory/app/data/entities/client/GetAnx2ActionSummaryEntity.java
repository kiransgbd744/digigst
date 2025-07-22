package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.ToString;

/**
 * 
 * @author Dibyakanta.Sahoo
 *
 */
@Entity
@Table(name = "GETANX2_ACTIONSUMMARY")
@Data
public class GetAnx2ActionSummaryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private long id;

	/*
	 * @Column(name = "SEC_SUMM_ID") private long sec_sum_id;
	 */
	@ToString.Exclude
	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn(name = "HEADER_ID", referencedColumnName = "ID")
	private GetANX2B2B_DE_SummaryEntity headerId;
	
/*	@ToString.Exclude
	@ManyToOne // (fetch = FetchType.LAZY)
	@JoinColumn(name = "SEC_SUMM_ID", referencedColumnName = "ID", nullable = false)
	private GetANX2B2B_DE_SummaryEntity secSumId;*/

	/*@Column(name = "SUMM_ID")
	private long summid;
*/
	@Column(name = "ACTION")
	private String action;

	/*@Column(name = "CHKSUM")
	private String chksum;*/

	/*@Column(name = "TOTAL_DOC_NUM")
	private int ttdocnum;
*/
	@Column(name = "TOTAL_DOC")
	private int ttdoC;
	
	@Column(name = "TOTAL_VAlUE")
	private BigDecimal ttlval;
	
	@Column(name = "TOTAL_TAX")
	private BigDecimal tttax;

	@Column(name = "TOTAL_TAX_VAL")
	private BigDecimal tttaxval;

	@Column(name = "TOTAL_IGST")
	private BigDecimal ttigst;

	@Column(name = "TOTAL_SGST")
	private BigDecimal ttsgst;

	@Column(name = "TOTAL_CGST")
	private BigDecimal ttcgst;

	@Column(name = "TOTAL_CESS")
	private BigDecimal ttcess;

	/*@Column(name = "CREATED_BY")
	private String createdby;

	@Column(name = "CREATED_ON")
	private LocalDateTime createon;*/
    
	@Column(name="IS_DELETE")
	private boolean isdelete;

}
