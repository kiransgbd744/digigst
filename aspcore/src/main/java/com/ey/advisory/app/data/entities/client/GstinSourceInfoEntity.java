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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */

@Entity
@Table(name = "TBL_GSTIN_SOURCE_INFO")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GstinSourceInfoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@Column(name = "GSTIN")
	private String gstin;

	@Expose
	@Column(name = "SOURCE_ID")
	private String sourceId;

	@Column(name = "CREATED_DATE")
	protected LocalDateTime createdDate;

	@Column(name = "CREATED_BY")
	protected String createdBy;

}
