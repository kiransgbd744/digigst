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
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Dibyakanta.sahoo
 *
 */
@Entity
@Table(name="GETANX2_COUNTERPARTY_TOTAL_ACTION_SUMMARY")
@Setter
@Getter
public class GetAnx2CounterPartyTotalActionSummaryEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "HEADER_ID", referencedColumnName = "ID", nullable = false)
	private GetANX2B2B_DE_SummaryEntity headerId;
	
	/*@Column(name = "SUMM_ID")
	private long summId;*/
	
	@Column(name = "CGSTIN")
	private String cgstin;
	
	/*@Column(name = "ACTION")
	private String action;*/
	
	@Column(name = "CHKSUM")
	private String chkSum;
	
	@Column(name = "TOTAL_DOC")
	private int ttdoc;
	
	@Column(name = "TOTAL_TAX")
	private BigDecimal ttTax;
	
	@Column(name="TOTAL_VALUE")
	private int ttval;
	
	/*@Column(name = "CREATED_BY")
	private String createdBy;*/
	
	@Column(name = "IS_DELETE")
	private boolean isdelete;
	
	
	}
