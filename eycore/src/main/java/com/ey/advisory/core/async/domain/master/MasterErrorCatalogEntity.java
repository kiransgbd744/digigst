/**
 * 
 */
package com.ey.advisory.core.async.domain.master;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Laxmi.Salukuti
 *
 */
@Entity
@Table(name = "MASTER_ERROR_CATALOGUE")
@Setter
@Getter
@ToString
public class MasterErrorCatalogEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private long id;

	@Column(name = "ERROR_CODE")
	private String errorCode;

	@Column(name = "ERROR_DESCRIPTION")
	private String errorDesc;

	@Column(name = "ERROR_TYPE")
	private String errorType;

	@Column(name = "ERROR_SOURCE")
	private String errorSource;

	@Column(name = "TABLE_TYPE")
	private String tableType;

	@Column(name = "REPORT_COLUMN_NUM")
	private String columnName;
}
