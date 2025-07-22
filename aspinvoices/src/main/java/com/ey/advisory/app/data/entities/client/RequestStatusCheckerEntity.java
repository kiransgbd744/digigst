/**
 * 
 */
package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.ToString;

/**
 * @author Balakrishna.S
 *
 */
@Entity
@Table(name = "REQUEST_STATUS_CHECKER_LEVEL")
@Data
@ToString
public class RequestStatusCheckerEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Expose
	@Column(name = "REQUEST_ID")
	private Long requestId;
	
	@Expose
	@Column(name = "CHECKER_ID")
	private String checkerId;
    
    @Expose
	@Column(name = "CHECKER_COMMENTS")
	private String checkerComments;

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
