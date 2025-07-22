package com.ey.advisory.app.data.views.client;

public class Gstr6ISDGstnErrorDto {

	private String documentType;
	private String documentNumber;
	private String documentDate;
	private String originalDocumentNumber;
	private String originalDocumentDate;
	private String recipeintGSTIN;
	private String stateCode;
	private String iGSTasIGST;
	private String iGSTasCGST;
	private String iGSTasSGST;
	private String cGSTasIGST;
	private String cGSTasCGST;
	private String sGSTasIGST;
	private String sGSTasSGST;
	private String cESSAmount;
	private String refId;
	private String refIdDateTime;
	private String errorCode;
	private String errorMessage;

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public String getDocumentDate() {
		return documentDate;
	}

	public void setDocumentDate(String documentDate) {
		this.documentDate = documentDate;
	}

	public String getOriginalDocumentNumber() {
		return originalDocumentNumber;
	}

	public void setOriginalDocumentNumber(String originalDocumentNumber) {
		this.originalDocumentNumber = originalDocumentNumber;
	}

	public String getOriginalDocumentDate() {
		return originalDocumentDate;
	}

	public void setOriginalDocumentDate(String originalDocumentDate) {
		this.originalDocumentDate = originalDocumentDate;
	}

	public String getRecipeintGSTIN() {
		return recipeintGSTIN;
	}

	public void setRecipeintGSTIN(String recipeintGSTIN) {
		this.recipeintGSTIN = recipeintGSTIN;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getiGSTasIGST() {
		return iGSTasIGST;
	}

	public void setiGSTasIGST(String iGSTasIGST) {
		this.iGSTasIGST = iGSTasIGST;
	}

	public String getiGSTasCGST() {
		return iGSTasCGST;
	}

	public void setiGSTasCGST(String iGSTasCGST) {
		this.iGSTasCGST = iGSTasCGST;
	}

	public String getiGSTasSGST() {
		return iGSTasSGST;
	}

	public void setiGSTasSGST(String iGSTasSGST) {
		this.iGSTasSGST = iGSTasSGST;
	}

	public String getcGSTasIGST() {
		return cGSTasIGST;
	}

	public void setcGSTasIGST(String cGSTasIGST) {
		this.cGSTasIGST = cGSTasIGST;
	}

	public String getcGSTasCGST() {
		return cGSTasCGST;
	}

	public void setcGSTasCGST(String cGSTasCGST) {
		this.cGSTasCGST = cGSTasCGST;
	}

	public String getsGSTasIGST() {
		return sGSTasIGST;
	}

	public void setsGSTasIGST(String sGSTasIGST) {
		this.sGSTasIGST = sGSTasIGST;
	}

	public String getsGSTasSGST() {
		return sGSTasSGST;
	}

	public void setsGSTasSGST(String sGSTasSGST) {
		this.sGSTasSGST = sGSTasSGST;
	}

	public String getcESSAmount() {
		return cESSAmount;
	}

	public void setcESSAmount(String cESSAmount) {
		this.cESSAmount = cESSAmount;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public String getRefIdDateTime() {
		return refIdDateTime;
	}

	public void setRefIdDateTime(String refIdDateTime) {
		this.refIdDateTime = refIdDateTime;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {
		return "Gstr6ISDGstnErrorDto [documentType=" + documentType + ", documentNumber=" + documentNumber
				+ ", documentDate=" + documentDate + ", originalDocumentNumber=" + originalDocumentNumber
				+ ", originalDocumentDate=" + originalDocumentDate + ", recipeintGSTIN=" + recipeintGSTIN
				+ ", stateCode=" + stateCode + ", iGSTasIGST=" + iGSTasIGST + ", iGSTasCGST=" + iGSTasCGST
				+ ", iGSTasSGST=" + iGSTasSGST + ", cGSTasIGST=" + cGSTasIGST + ", cGSTasCGST=" + cGSTasCGST
				+ ", sGSTasIGST=" + sGSTasIGST + ", sGSTasSGST=" + sGSTasSGST + ", cESSAmount=" + cESSAmount
				+ ", refId=" + refId + ", refIdDateTime=" + refIdDateTime + ", errorCode=" + errorCode
				+ ", errorMessage=" + errorMessage + ", getDocumentType()=" + getDocumentType()
				+ ", getDocumentNumber()=" + getDocumentNumber() + ", getDocumentDate()=" + getDocumentDate()
				+ ", getOriginalDocumentNumber()=" + getOriginalDocumentNumber() + ", getOriginalDocumentDate()="
				+ getOriginalDocumentDate() + ", getRecipeintGSTIN()=" + getRecipeintGSTIN() + ", getStateCode()="
				+ getStateCode() + ", getiGSTasIGST()=" + getiGSTasIGST() + ", getiGSTasCGST()=" + getiGSTasCGST()
				+ ", getiGSTasSGST()=" + getiGSTasSGST() + ", getcGSTasIGST()=" + getcGSTasIGST() + ", getcGSTasCGST()="
				+ getcGSTasCGST() + ", getsGSTasIGST()=" + getsGSTasIGST() + ", getsGSTasSGST()=" + getsGSTasSGST()
				+ ", getcESSAmount()=" + getcESSAmount() + ", getRefId()=" + getRefId() + ", getRefIdDateTime()="
				+ getRefIdDateTime() + ", getErrorCode()=" + getErrorCode() + ", getErrorMessage()=" + getErrorMessage()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}

}
