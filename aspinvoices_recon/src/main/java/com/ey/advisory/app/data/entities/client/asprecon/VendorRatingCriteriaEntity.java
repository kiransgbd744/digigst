package com.ey.advisory.app.data.entities.client.asprecon;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 
 * @author Jithendra.B
 *
 */
@Entity
@Table(name = "VENDOR_RATING_CRITERIA")
@Data
public class VendorRatingCriteriaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "RETURN_TYPE")
	private String returnType;

	@Column(name = "DUE_TYPE")
	private String dueType;

	@Column(name = "FROM_DAY")
	private Integer fromDay;

	@Column(name = "TO_DAY")
	private Integer toDay;

	@Column(name = "RATING")
	private BigDecimal rating;

	@Column(name = "ENTITY_ID")
	private Long entityId;

	@Column(name = "SOURCE")
	private String source;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "UPDATED_ON")
	private LocalDateTime updatedOn;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

}
