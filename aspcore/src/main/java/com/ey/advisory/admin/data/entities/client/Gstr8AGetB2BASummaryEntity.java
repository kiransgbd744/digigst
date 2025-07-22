/**
 * 
 */
package com.ey.advisory.admin.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Kiran s
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TBL_GETGSTR9_8A_B2BA")
public class Gstr8AGetB2BASummaryEntity {
	
	@Id
	@Column(name = "ID")
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR9_8A_B2BA_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	private Long id;

	@Column(name = "GSTIN")
	private String cgstin;

	@Column(name = "RET_PERIOD")
	private String returnPeriod;
	
	@Column(name = "FY")
	private String finYear;
	
	@Column(name = "DOCID")
	private Integer docId;
	
	@Column(name = "STIN")
	private String sgstin;
	
	@Column(name = "RTNPRD")
	private String retPeriod;

	@Column(name = "FILINGDT")
	private LocalDate filingDate;
	
	@Column(name = "INUM")
	private String invNum;
	
	@Column(name = "IDT")
	private LocalDate invDate;
	
	@Column(name = "OINUM")
	private String originalInvNum;
	
	@Column(name = "OIDT")
	private LocalDate originalInvDate;

	@Column(name = "VAL")
	private BigDecimal invValue;

	@Column(name = "POS")
	private String pos;
	
	@Column(name = "RCHRG")
	private String reverseCharge;
	
	@Column(name = "INV_TYP")
	private String invType;
	
	@Column(name = "TXVAL")
	private BigDecimal taxPayable;
	
	@Column(name = "IAMT")
	private BigDecimal igst;

	@Column(name = "CAMT")
	private BigDecimal cgst;
	
	@Column(name = "SAMT")
	private BigDecimal sgst;
	
	@Column(name = "CSAMT")
	private BigDecimal cess;
	
	@Column(name = "ISELIGIBLE")
	private String eligibleItc;
	
	@Column(name = "REASON")
	private String reason;
	
	@Column(name = "IS_DELETE")
	private Boolean isDelete;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;
	
	
	@Column(name = "TAX_RATE")
	private String tx;

	


}

