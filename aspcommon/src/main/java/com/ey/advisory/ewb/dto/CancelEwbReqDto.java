/**
 * 
 */
package com.ey.advisory.ewb.dto;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Khalid1.Khan
 *
 */
@Getter
@Setter
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CancelEwbReqDto implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("docHeaderId")
	@XmlTransient
	private Long docHeaderId;

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

	@Expose
	@SerializedName("ewbDtls")
	protected EwbMasterSaveDto ewbDetails;

}
