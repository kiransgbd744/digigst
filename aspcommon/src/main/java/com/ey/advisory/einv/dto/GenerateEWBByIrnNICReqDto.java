package com.ey.advisory.einv.dto;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * @author Siva.Reddy
 *
 */
@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GenerateEWBByIrnNICReqDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Expose
	@SerializedName("gstin")
	@XmlElement(name = "gstin")
	private String gstin;
	@Expose
	@SerializedName("Irn")
	@XmlElement(name = "irn")
	private String irn;
	@Expose
	@SerializedName("Distance")
	@XmlElement(name = "distance")
	private Integer distance;

	@Expose
	@SerializedName("TransMode")
	@XmlElement(name = "trans-mode")
	private String transMode;

	@Expose
	@SerializedName("TransId")
	@XmlElement(name = "trans-id")
	private String transId;

	@Expose
	@SerializedName("TransName")
	@XmlElement(name = "trans-name")
	private String transName;

	@Expose
	@SerializedName("TransDocDt")
	@XmlElement(name = "trn-docdt")
	private String trnDocDt;

	@Expose
	@SerializedName("TransDocNo")
	@XmlElement(name = "trn-docno")
	private String trnDocNo;

	@Expose
	@SerializedName("VehNo")
	@XmlElement(name = "veh-no")
	private String vehNo;

	@Expose
	@SerializedName("VehType")
	@XmlElement(name = "veh-type")
	private String vehType;

	@Expose
	@SerializedName("suppPincd")
	@XmlElement(name = "supp-Pincd")
	private Integer suppPincd;

	@Expose
	@SerializedName("custPincd")
	@XmlElement(name = "cust-Pincd")
	private Integer custPincd;

	@Expose
	@SerializedName("shipToPincd")
	@XmlElement(name = "shipto-Pincd")
	private Integer shipToPincd;

	@Expose
	@SerializedName("dispatcherPincd")
	@XmlElement(name = "dispatcher-Pincd")
	private Integer dispatcherPincd;
	
	@SerializedName("nicDistance")
	@Expose
	@XmlElement(name = "nic-dist")
	private String nicDistance;
	
	@SerializedName("ExpShipDtls")
	@Expose
	@XmlElement(name = "exp-dtls")
	private GenEWBByIrnExpShpNICReqDto expShipDtls;
	
	@SerializedName("DispDtls")
	@Expose
	@XmlElement(name = "disp-dtls")
	private GenEWBByIrnDispNICReqDto dispDtls;

}
