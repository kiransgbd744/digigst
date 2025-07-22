/**
 * 
 */
package com.ey.advisory.app.data.entities.client;

import java.sql.Clob;
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
@Table(name = "GSTN_USER_REQUEST")
@Data
public class GstnUserRequestEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "RETURN_PERIOD")
	private String taxPeriod;

	@Column(name = "REQUEST_TYPE")
	private String requestType;

	@Column(name = "RETURN_TYPE")
	private String returnType;
	
	@Column(name = "REQUEST_STATUS")
	private Integer requestStatus;
	
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "IS_DELETE")
	private boolean isDelete;
	
	@Column(name = "GET_RESPONSE_PAYLOAD")
	private Clob getResponsePayload;
	
	@Column(name = "AUTO_CALC_RESPONSE")
	private Clob autoCalcResponse;
	
	@Column(name = "DERIVED_RET_PERIOD")
	private Integer derivedRetPeriod;
	
	@Column(name = "IS_NIL_UI")
	private boolean isNilUserInput;
	
	@Column(name = "IS_HSN_UI")
	private boolean isHsnUserInput;
	
	@Column(name = "IS_CROSS_ITC_UI")
	private boolean isCrossItcUserInput;
	
	@Column(name = "INTEREST_AUTO_CALC_RESPONSE")
	private Clob intrtAutoCalcResponse;

}
