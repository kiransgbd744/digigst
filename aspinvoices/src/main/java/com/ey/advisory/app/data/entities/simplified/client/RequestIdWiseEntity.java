/**
 * 
 */
package com.ey.advisory.app.data.entities.simplified.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Entity
@Table(name = "REQUEST_ID_WISE")
@Data
public class RequestIdWiseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "PARAMS")
	private String params;

	@Column(name = "TAX_PERIOD")
	private String taxPeriod;

	@Column(name = "NO_OF_GSTINS")
	private Integer NoOfGstins;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "INITITAED_BY")
	private String initiatedBy;

	@Column(name = "INITIATED_ON")
	private LocalDateTime initiatedOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "COMPLETED_ON")
	private LocalDateTime completedOn;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

}
