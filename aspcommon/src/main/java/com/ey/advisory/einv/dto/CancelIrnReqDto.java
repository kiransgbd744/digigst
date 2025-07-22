/**
 * 
 */
package com.ey.advisory.einv.dto;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import lombok.Data;

/**
 * @author Arun K.A
 *
 */
@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CancelIrnReqDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SerializedName("gstin")
	@Expose
	private String gstin;

	@SerializedName("irn")
	@Expose
	@XmlElement(name = "irn")
	private String irn;

	@SerializedName("cnlRsn")
	@Expose
	@XmlElement(name = "cancel-rsn-code")
	private String cnlRsn;

	@SerializedName("cnlRem")
	@Expose
	@XmlElement(name = "cancel-rmrk")
	private String cnlRem;

	@Expose
	@SerializedName("docHeaderId")
	@XmlTransient
	private Long docHeaderId;

}
