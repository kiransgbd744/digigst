/**
 * 
 */
package com.ey.advisory.app.docs.dto.einvoice;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Laxmi.Salukuti
 *
 */
@Data
public class TransportPartBDetailsDto {

	@Expose
	@SerializedName("transporterId")
	private String transporterId;
	
	@Expose
	@SerializedName("docNum")
	private String docNum;

	@Expose
	@SerializedName("gstin")
	private String gstin;

	@Expose
	@SerializedName("ewbNo")
	private String ewbNo;

	@Expose
	@SerializedName("vehicleNo")
	private String vehicleNo;

	@Expose
	@SerializedName("transFrom")
	private String transFrom;

	@Expose
	@SerializedName("transFromState")
	private String transFromState;

	@Expose
	@SerializedName("transDocNo")
	private String transDocNo;

	@Expose
	@SerializedName("transDocDate")
	private LocalDate transDocDate;

	@Expose
	@SerializedName("transMode")
	private String transMode;

	@Expose
	@SerializedName("vehicleType")
	private String vehicleType;

	@Expose
	@SerializedName("distance")
	private Integer distance;

	@Expose
	@SerializedName("nicDistance")
	private Integer nicDistance;

	@Expose
	@SerializedName("reasonOthers")
	private String reasonOthers;

	@Expose
	@SerializedName("ewbDate")
	private LocalDateTime ewbDate;

	@Expose
	@SerializedName("updatePartBdate")
	private LocalDateTime updatePartBdate;

	@Expose
	@SerializedName("vehicleUpdate")
	private LocalDateTime vehicleUpdate;

	@Expose
	@SerializedName("errorCode")
	private String errorCode;

	@Expose
	@SerializedName("errorDesc")
	private String errorDesc;

}
