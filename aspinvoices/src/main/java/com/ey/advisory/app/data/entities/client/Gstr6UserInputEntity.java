package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Entity
@Table(name = "GSTR6_TURN_OVER_USERINPUT")
@Setter
@Getter
@ToString
public class Gstr6UserInputEntity {

	@Expose
	@SerializedName("id")
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR6_TURN_OVER_USERINPUT_SEQ", allocationSize = 1)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@Expose
	@SerializedName("batchId")
	@Column(name = "BATCH_ID")
	private Long batchId;

	@Expose
	@SerializedName("derivedRetPeriod")
	@Column(name = "DERIVED_RET_PERIOD")
	protected String derivedRetPeriod;

	@Expose
	@SerializedName("isdGstin")
	@Column(name = "ISD_GSTIN")
	protected String isdGstin;

	@Expose
	@SerializedName("gstin")
	@Column(name = "GSTIN")
	protected String gstin;

	@Expose
	@SerializedName("computeDigiVal")
	@Column(name = "COMPUTE_VALUE")
	protected BigDecimal computeDigiVal;

	@Expose
	@SerializedName("computeGstnVal")
	@Column(name = "GSTIN_COMPUTE_VALUE")
	protected BigDecimal computeGstnVal;

	@Expose
	@SerializedName("userInput")
	@Column(name = "USER_INPUT")
	protected BigDecimal userInput;

	@Expose
	@SerializedName("currentRetPer")
	@Column(name = "CURRENT_RET_PERIOD")
	protected String currentRetPer;

	@Expose
	@SerializedName("turnOverRatio")
	@Column(name = "TURN_OVER_RATIO")
	protected String turnOverRatio;

	@Expose
	@SerializedName("isDelete")
	@Column(name = "IS_DELETE")
	protected boolean isDelete;

	@Expose
	@SerializedName("createdBy")
	@Column(name = "CREATED_BY")
	protected String createdBy;

	@Expose
	@SerializedName("createdOn")
	@Column(name = "CREATED_ON")
	protected LocalDateTime createdOn;

	@Expose
	@SerializedName("modifiedBy")
	@Column(name = "MODIFIED_BY")
	protected String modifiedBy;

	@Expose
	@SerializedName("modifiedOn")
	@Column(name = "MODIFIED_ON")
	protected LocalDateTime modifiedOn;

	@Expose
	@SerializedName("getGstr1Status")
	@Column(name = "GET_GSTR1_STATUS")
	protected String getGstr1Status;

	@Expose
	@SerializedName("getGstr1Time")
	@Column(name = "GET_GSTR1_TIME")
	protected String getGstr1Time;

	@Expose
	@SerializedName("fromDerPeriod")
	@Column(name = "FROM_DERIVED_RET_PERIOD")
	protected Integer fromDerPeriod;

	@Expose
	@SerializedName("toDerPeriod")
	@Column(name = "TO_DERIVED_RET_PERIOD")
	protected Integer toDerPeriod;

	@Expose
	@SerializedName("stateName")
	@Column(name = "STATE_NAME")
	protected String stateName;

}
