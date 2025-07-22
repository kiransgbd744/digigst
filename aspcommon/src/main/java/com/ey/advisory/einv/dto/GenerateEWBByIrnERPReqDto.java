package com.ey.advisory.einv.dto;

import java.io.Serializable;

import com.ey.advisory.ewb.dto.EwbMasterSaveDto;
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
public class GenerateEWBByIrnERPReqDto implements Serializable {

	/**
	 * @author Siva.Reddy
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Expose
	@SerializedName("gstin")
	@XmlElement(name = "gstin")
	private String gstin;

	@Expose
	@SerializedName("irn")
	@XmlElement(name = "irn")
	private String irn;

	@Expose
	@SerializedName("distance")
	@XmlElement(name = "distance")
	private Integer distance;

	@Expose
	@SerializedName("transMode")
	@XmlElement(name = "trans-mode")
	private String transMode;

	@Expose
	@SerializedName("transId")
	@XmlElement(name = "trans-id")
	private String transId;

	@Expose
	@SerializedName("transName")
	@XmlElement(name = "trans-name")
	private String transName;

	@Expose
	@SerializedName("trnDocDt")
	@XmlElement(name = "trn-docdt")
	private String trnDocDt;

	@Expose
	@SerializedName("trnDocNo")
	@XmlElement(name = "trn-docno")
	private String trnDocNo;

	@Expose
	@SerializedName("vehNo")
	@XmlElement(name = "veh-no")
	private String vehNo;

	@Expose
	@SerializedName("vehType")
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

	@SerializedName("expShipDtls")
	@Expose
	@XmlElement(name = "exp-dtls")
	private GenEWBByIrnExpShpERPReqDto expShipDtls;

	@SerializedName("dispDtls")
	@Expose
	@XmlElement(name = "disp-dtls")
	private GenEWBByIrnDispERPReqDto dispDtls;

	@Expose
	@SerializedName("docCat")
	@XmlElement(name = "doc-categ")
	protected String docCategory;

	@Expose
	@SerializedName("supplyType")
	@XmlElement(name = "supp-type")
	protected String supplyType;

	@Expose
	@SerializedName("ewbDtls")
	protected EwbMasterSaveDto ewbDetails;

}
