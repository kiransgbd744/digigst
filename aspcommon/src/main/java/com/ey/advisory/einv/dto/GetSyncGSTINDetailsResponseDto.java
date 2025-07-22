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
public class GetSyncGSTINDetailsResponseDto implements Serializable {

	private static final long serialVersionUID = 7862725070285693520L;

	@SerializedName("Gstin")
	@Expose
	@XmlElement(name = "gstin")
	private String gstin;

	@SerializedName("TradeName")
	@Expose
	@XmlElement(name = "trade-name")
	private String tradeName;

	@SerializedName("LegalName")
	@Expose
	@XmlElement(name = "legal-name")
	private String legalName;

	@SerializedName("AddrBnm")
	@Expose
	@XmlElement(name = "addr-bnm")
	private String addrBnm;

	@SerializedName("AddrBno")
	@Expose
	@XmlElement(name = "addr-bno")
	private String addrBno;

	@SerializedName("AddrFlno")
	@Expose
	@XmlElement(name = "addr-flno")
	private String addrFlno;

	@SerializedName("AddrSt")
	@Expose
	@XmlElement(name = "addr-st")
	private String addrSt;

	@SerializedName("AddrLoc")
	@Expose
	@XmlElement(name = "addr-loc")
	private String addrLoc;

	@SerializedName("StateCode")
	@Expose
	@XmlElement(name = "state-code")
	private Integer stateCode;

	@SerializedName("AddrPncd")
	@Expose
	@XmlElement(name = "addr-pncd")
	private Integer addrPncd;

	@SerializedName("TxpType")
	@Expose
	@XmlElement(name = "txp-type")
	private String txpType;

	@SerializedName("Status")
	@Expose
	@XmlElement(name = "status")
	private String status;

	@SerializedName("BlkStatus")
	@Expose
	@XmlElement(name = "blk-status")
	public String blkStatus;

	@SerializedName("DtReg")
	@Expose
	@XmlElement(name = "dt-reg")
	private String dtReg;

	@SerializedName("DtDReg")
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
