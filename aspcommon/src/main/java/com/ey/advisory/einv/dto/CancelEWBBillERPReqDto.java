package com.ey.advisory.einv.dto;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Data;

/**
 * @author Siva Reddy
 *
 */

@Data
public class CancelEWBBillERPReqDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Expose
	@SerializedName("gstin")
	@XmlElement(name = "gstin")
	private String gstin;

	@Expose
	@SerializedName("ewbNo")
	@XmlElement(name = "ewb-no")
	private String ewbNo;

	@Expose
	@SerializedName("cancelRsnCode")
	@XmlElement(name = "cancel-rsn-code")
	private String cancelRsnCode;

	@Expose
	@SerializedName("cancelRmrk")
	@XmlElement(name = "cancel-rmrk")
	private String cancelRmrk;

}
