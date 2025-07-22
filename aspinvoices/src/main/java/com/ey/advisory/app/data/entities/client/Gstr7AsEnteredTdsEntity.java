package com.ey.advisory.app.data.entities.client;

import java.time.LocalDate;
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

import org.javatuples.Pair;

import com.ey.advisory.admin.data.entities.client.EntityConfigPrmtEntity;
import com.ey.advisory.admin.services.onboarding.EntityAtConfigKey;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 
 * @author Anand3.M
 *
 */

@Data
@Entity
@Table(name = "GSTR7_AS_ENTERED_TDS")
public class Gstr7AsEnteredTdsEntity {

	@Expose
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR7_AS_ENTERED_TDS_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@Column(name = "FILE_ID")
	private Long fileId;

	@Column(name = "RETURN_PERIOD")
	private String returnPeriod;
	
	@Expose
	@SerializedName("dataOriginType")
	@Column(name = "DATAORIGINTYPECODE")
	private String dataOriginType;

	@Column(name = "DERIVED_RET_PERIOD")
	private String derReturnPeriod;

	@Column(name = "ACTION_TYPE")
	private String actType;

	@Column(name = "TDS_DEDUCTOR_GSTIN")
	private String tdsGstin;

	@Column(name = "ORG_TDS_DEDUCTEE_GSTIN")
	private String orgTdsGstin;

	@Column(name = "ORG_RETURN_PERIOD")
	private String orgRetPeriod;

	@Column(name = "ORG_GROSS_AMT")
	private String orgGrossAmt;

	@Column(name = "NEW_TDS_DEDUCTEE_GSTIN")
	private String newGstin;

	@Column(name = "NEW_GROSS_AMT")
	private String newGrossAmt;

	@Column(name = "INVOICE_VALUE")
	private String invValue;

	@Column(name = "IGST_AMT")
	private String igstAmt;

	@Column(name = "CGST_AMT")
	private String cgstAmt;

	@Column(name = "SGST_AMT")
	private String sgstAmt;

	@Column(name = "CONTRACT_NUMBER")
	private String conNumber;

	@Column(name = "CONTRACT_DATE")
	private LocalDate conDate;

	@Column(name = "CONTRACT_VALUE")
	private String conValue;

	@Column(name = "PAYMENT_ADV_NUM")
	private String payNum;

	@Column(name = "PAYMENT_ADV_DATE")
	private LocalDate payDate;

	@Column(name = "DOC_NUM")
	private String docNum;

	@Column(name = "DOC_DATE")
	private LocalDate docDate;

	@Column(name = "PLANT_CODE")
	private String plantCode;

	@Column(name = "DIVISION")
	private String division;

	@Column(name = "PURCHASE_ORGANIZATION")
	private String purOrg;

	@Column(name = "PROFIT_CENTRE1")
	private String proCen1;

	@Column(name = "PROFIT_CENTRE2")
	private String proCen2;

	@Column(name = "USERDEFINED_FIELD1")
	private String usrDefField1;

	@Column(name = "USERDEFINED_FIELD2")
	private String usrDefField2;

	@Column(name = "USERDEFINED_FIELD3")
	private String usrDefField3;

	@Column(name = "TDS_INVKEY")
	private String tdsKey;

	@Column(name = "IS_INFORMATION")
	private boolean isInformation;

	@Column(name = "IS_ERROR")
	private boolean isError;

	@Column(name = "IS_DELETE")
	private boolean isDelete;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Transient
	private Long entityId;

	@Transient
	private Long groupId;

	@Transient
	private Map<Long, List<Pair<String, String>>> entityAtValMap;

	@Transient
	private Map<EntityAtConfigKey, Map<Long, String>> entityAtConfMap;

	@Transient
	private Map<Long, List<EntityConfigPrmtEntity>> entityConfigParamMap;

	@Transient
	private String formReturnType;

	@Transient
	private boolean isCgstInMasterCust;
	
	@Transient
	private String errorCode;


}
