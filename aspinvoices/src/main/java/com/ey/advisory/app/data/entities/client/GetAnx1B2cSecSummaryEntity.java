/**
 * 
 */
package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
import lombok.ToString;

/**
 * @author Laxmi.Salukuti
 *
 */
@Entity
@Table(name = "GETANX1_B2CSEC_SUMMARY")
@Setter
@Getter
public class GetAnx1B2cSecSummaryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "SECNM")
	private String secNum;

	@Column(name = "CHKSUM")
	private String chksum;

	@Column(name = "NET_TAXABLE_VALUE")
	private BigDecimal netTax;

	@Column(name = "TOTAL_IGST")
	private BigDecimal totalIgst;

	@Column(name = "TOTAL_CGST")
	private BigDecimal totalCgst;

	@Column(name = "TOTAL_SGST")
	private BigDecimal totalSgst;

	@Column(name = "TOTAL_CESS")
	private BigDecimal totalCess;

	@Column(name = "TOTAL_DOC_VALUE")
	private int totalDocVal;

	@Column(name = "TOTAL_DOC_NUM")
	private int totalDocNum;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "IS_DELETE")
	private boolean isDelete;
	
	@Column(name = "SUMMARY_ID")
	private Long b2cSumId;

}