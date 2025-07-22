package com.ey.advisory.app.gstr2b;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Siva.Reddy
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TBL_GET2B_TAGGING_2A")
public class Gstr2BTagging2AEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Column(name = "SECTION")
	private String section;

	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "REMOVAL_STATUS")
	private String removalStatus;

	@Column(name = "GET2B_GENERATION_TIME")
	private LocalDateTime get2BGenTime;

	@Column(name = "INVOICE_COUNT")
	private int invoiceCount;

	@Column(name = "GET2A_TIMESTAMP")
	private LocalDateTime get2ATimeStamp;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "IS_DELETE")
	private Boolean isDelete;

	@Column(name = "SOURCE_TYPE")
	private String source;
}
