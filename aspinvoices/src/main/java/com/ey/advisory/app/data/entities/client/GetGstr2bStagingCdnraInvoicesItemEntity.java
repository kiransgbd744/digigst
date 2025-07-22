package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;

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
 * @author Ravindra V S
 *
 */
@Entity
@Table(name = "GETGSTR2B_STAGING_CDNRA_ITEM")
@Data
public class GetGstr2bStagingCdnraInvoicesItemEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR2B_STAGING_CDNRA_ITEM_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "CHECKSUM")
	private String checksum;

	@Column(name = "ITEM_NUMBER")
	private int itemNumber;

	@Column(name = "TAX_RATE")
	private BigDecimal taxRate;

	@Column(name = "TAX_VALUE")
	private BigDecimal taxableValue;

	@Column(name = "IGST_AMT")
	private BigDecimal igstAmt;

	@Column(name = "CGST_AMT")
	private BigDecimal cgstAmt;

	@Column(name = "SGST_AMT")
	private BigDecimal sgstAmt;

	@Column(name = "CESS_AMT")
	private BigDecimal cessAmt;

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@ManyToOne
	@JoinColumn(name = "HEADER_ID", referencedColumnName = "ID", nullable = false)
	protected GetGstr2bStagingCdnraInvoicesHeaderEntity header;

}
