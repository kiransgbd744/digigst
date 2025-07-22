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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Ravindra
 *
 */
@Entity
@Table(name = "GETGSTR2B_STAGING_IMPG_ITEM")
@Data
public class GetGstr2bStagingImpgInvoicesItemEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR2B_STAGING_IMPG_ITEM_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "CHECKSUM")
	private String checksum;

	@Column(name = "TAX_VALUE")
	private BigDecimal taxValue;

	@Column(name = "IGST_AMT")
	private BigDecimal igstAmt;

	@Column(name = "CESS_AMT")
	private BigDecimal cessAmt;

	@Column(name = "REF_DT_ICEGATE")
	private LocalDateTime refDateIcegate;

	@Column(name = "REC_DT_GSTN")
	private LocalDateTime recDateGstin;

	@Column(name = "PORT_CODE")
	private String portCode;

	@Column(name = "BOE_NUM")
	private Long boeNumber;

	@Column(name = "BOE_DATE")
	private LocalDateTime boeDate;

	@Column(name = "ISAMD")
	private Boolean isAmd;

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@ManyToOne
	@JoinColumn(name = "HEADER_ID", referencedColumnName = "ID", nullable = false)
	protected GetGstr2bStagingImpgInvoicesHeaderEntity header;

}
