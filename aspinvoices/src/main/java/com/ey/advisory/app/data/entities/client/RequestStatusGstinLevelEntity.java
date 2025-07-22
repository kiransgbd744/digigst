/**
 * 
 */
package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

/**
 * @author Balakrishna.S
 *
 */
@Entity
@Table(name = "REQUEST_STATUS_GSTIN_LEVEL")
@Data
@ToString
public class RequestStatusGstinLevelEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "GSTIN")
	private String gstin;
	
	@Column(name = "TAX_PERIOD")
	private String returnPeriod;
    
	@Expose
	@Column(name = "APPROVAL_STATUS")
	private String approvalStatus;
	
	@Expose
	@Column(name = "APPROVAL_COMMENTS")
	private String approvalComments;
	 
	
	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Expose
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
	@Expose
	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Expose
	@Column(name = "MODIFIED_BY")
	private String modifiedBy;
	
	@Expose
	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	
}
