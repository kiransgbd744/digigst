package com.ey.advisory.einv.dto;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GetSyncGSTINDetailsERPRespDto implements Serializable {

	private static final long serialVersionUID = 7862725070285693520L;

	@SerializedName("gstin")
	@Expose
	@XmlElement(name = "gstin")
	private String gstin;

	@SerializedName("tradeName")
	@Expose
	@XmlElement(name = "trade-name")
	private String tradeName;

	@SerializedName("legalName")
	@Expose
	@XmlElement(name = "legal-name")
	private String legalName;

	@SerializedName("addrBnm")
	@Expose
	@XmlElement(name = "addr-bnm")
	private String addrBnm;

	@SerializedName("addrBno")
	@Expose
	@XmlElement(name = "addr-bno")
	private String addrBno;

	@SerializedName("addrFlno")
	@Expose
	@XmlElement(name = "addr-flno")
	private String addrFlno;

	@SerializedName("addrSt")
	@Expose
	@XmlElement(name = "addr-st")
	private String addrSt;

	@SerializedName("addrLoc")
	@Expose
	@XmlElement(name = "addr-loc")
	private String addrLoc;

	@SerializedName("stateCode")
	@Expose
	@XmlElement(name = "state-code")
	private Integer stateCode;

	@SerializedName("addrPncd")
	@Expose
	@XmlElement(name = "addr-pncd")
	private Integer addrPncd;

	@SerializedName("txpType")
	@Expose
	@XmlElement(name = "txp-type")
	private String txpType;

	@SerializedName("status")
	@Expose
	@XmlElement(name = "status")
	private String status;

	@SerializedName("blkStatus")
	@Expose
	@XmlElement(name = "blk-status")
	public String blkStatus;

	@SerializedName("dtReg")
	@Expose
	@XmlElement(name = "dt-reg")
	private String dtReg;

	@SerializedName("dtDReg")
	@Expose
	@XmlElement(name = "dt-dreg")
	public String dtDReg;

	@SerializedName("errorCode")
	@Expose
	@XmlElement(name = "err-cd")
	private String errorCode;

	@SerializedName("errorMessage")
	@Expose
	@XmlElement(name = "err-msg")
	private String errorMessage;

	@SerializedName("infoErrorCode")
	@Expose
	@XmlElement(name = "info-err-cd")
	private String infoErrorCode;

	@SerializedName("infoErrorMessage")
	@Expose
	@XmlElement(name = "info-err-msg")
	private String infoErrorMessage;

}
