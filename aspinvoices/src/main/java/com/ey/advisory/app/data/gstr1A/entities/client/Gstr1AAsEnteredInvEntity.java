package com.ey.advisory.app.data.gstr1A.entities.client;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Shashikant.Shukla
 *
 */

@Entity
@Table(name = "GSTR1A_AS_ENTERED_INV_SERIES")
@Setter
@Getter
@ToString
public class Gstr1AAsEnteredInvEntity {

	@Expose
	@Id
	@SerializedName("id")
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR1A_AS_ENTERED_INV_SERIES_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Expose
	@SerializedName("fileId")
	@Column(name = "FILE_ID")
	private Long fileId;

	@Expose
	@SerializedName("sgstin")
	@Column(name = "SUPPLIER_GSTIN")
	private String sgstin;
	
	@Expose
	@SerializedName("dataOriginType")
	@Column(name = "DATAORIGINTYPECODE")
	private String dataOriginType;

	@Expose
	@SerializedName("returnPeriod")
	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;

	@Expose
	@SerializedName("serialNo")
	@Column(name = "SERIAL_NUM")
	private String serialNo;

	@Expose
	@SerializedName("natureOfDocument")
	@Column(name = "NATURE_OF_DOC")
	private String natureOfDocument;

	@Expose
	@SerializedName("from")
	@Column(name = "DOC_SERIES_FROM")
	private String from;

	@Expose
	@SerializedName("to")
	@Column(name = "DOC_SERIES_TO")
	private String to;

	@Expose
	@SerializedName("totalNumber")
	@Column(name = "TOT_NUM")
	private String totalNumber;

	@Expose
	@SerializedName("cancelled")
	@Column(name = "CANCELED")
	private String cancelled;

	@Expose
	@SerializedName("netNumber")
	@Column(name = "NET_NUM")
	private String netNumber;

	@Expose
	@SerializedName("invoiceKey")
	@Column(name = "INV_SERIES_KEY")
	private String invoiceKey;

	@Expose
	@SerializedName("derivedRetPeriod")
	@Column(name = "DERIVED_RET_PERIOD")
	private String derivedRetPeriod;

	@Expose
	@SerializedName("isError")
	@Column(name = "IS_ERROR")
	private boolean isError;
	
	@Expose
	@SerializedName("isInfo")
	@Column(name = "IS_INFORMATION")
	private boolean isInfo;
	
	@Expose
	@SerializedName("isDelete")
	@Column(name = "IS_DELETE")
	private boolean isDelete;
	
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
	
	@Transient
	private Long sNo;
	
	@Transient
	private Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap;
	
	@Transient
	private Long entityId;
}