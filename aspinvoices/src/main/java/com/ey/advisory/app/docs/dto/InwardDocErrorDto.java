package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This class represents a single document error.
 * This class is responsible for transferring error details
 * response data from server to UI
 *
 * @author Mohana.Dasari
 */
public class InwardDocErrorDto {
	
	@Expose
	@SerializedName("docHeaderId")
	private Long docHeaderId;
	
	@Expose
	@SerializedName("index")
	private Integer index;
	
	@Expose
	@SerializedName("itemNo")
	private Integer itemNo;
	
	@Expose
	@SerializedName("errorCode")
	private String errorCode;
	
	@Expose
	@SerializedName("errorDesc")
	private String errorDesc;
	
	@Expose
	@SerializedName("errorFields")
	private String errorFields;//comma separated fields
	
	@Expose
	@SerializedName("errorType")
	private String errorType;
	
	@Expose
	@SerializedName("valType")
	private String valType;



	/**
	 * @return the index
	 */
	public Integer getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(Integer index) {
		this.index = index;
	}
	
	/**
	 * @return the itemNo
	 */
	public Integer getItemNo() {
		return itemNo;
	}

	/**
	 * @param itemNo the itemNo to set
	 */
	public void setItemNo(Integer itemNo) {
		this.itemNo = itemNo;
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return the errorDesc
	 */
	public String getErrorDesc() {
		return errorDesc;
	}

	/**
	 * @param errorDesc the errorDesc to set
	 */
	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}


	/**
	 * @return the errorFields
	 */
	public String getErrorFields() {
		return errorFields;
	}

	/**
	 * @param errorFields the errorFields to set
	 */
	public void setErrorFields(String errorFields) {
		this.errorFields = errorFields;
	}

	/**
	 * @return the errorType
	 */
	public String getErrorType() {
		return errorType;
	}

	/**
	 * @param errorType the errorType to set
	 */
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}


	/**
	 * @return the docHeaderId
	 */
	public Long getDocHeaderId() {
		return docHeaderId;
	}

	/**
	 * @param docHeaderId the docHeaderId to set
	 */
	public void setDocHeaderId(Long docHeaderId) {
		this.docHeaderId = docHeaderId;
	}

	public String getValType() {
		return valType;
	}

	public void setValType(String valType) {
		this.valType = valType;
	}
}
