package com.ey.advisory.app.gstr2b;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Hema G M
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TBL_GET2B_REQ_STATUS")
public class Gstr2bGet2bRequestStatusEntity {

	@Id
	@Column(name = "REQ_ID")
	private Long reqId;

	@Column(name = "RPT_TYPE")
	private String reportType;

	@Column(name = "GSTIN_CNT")
	private Long gstinCount;

	@Column(name = "TXPRD_CNT")
	private Long taxPeriodCount;

	@Column(name = "FILE_PATH")
	private String filePath;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "CREATED_DATE")
	private LocalDateTime createdDate;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "COMPLETED_ON")
	private LocalDateTime completedOn;
	
	@Column(name = "DOC_ID")
	private String docId;

}
