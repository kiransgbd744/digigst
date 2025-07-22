/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author Laxmi.Salukuti
 *
 */
@Data
public class NilNonExmptItemReqDto {

	@Expose
	@SerializedName("sgstin")
	private String sgstin;

	@Expose
	@SerializedName("retPrd")
	private String returnPeriod;

	@Expose
	@SerializedName("hsn")
	private String hsn;

	@Expose
	@SerializedName("uqc")
	private String uqc;

	@Expose
	@SerializedName("description")
	private String description;

	@Expose
	@SerializedName("qnt")
	private String qnt;

	@Expose
	@SerializedName("nlInterRr")
	private String nilInterStateReg;

	@Expose
	@SerializedName("nlIntraRr")
	private String nilIntraStateReg;

	@Expose
	@SerializedName("nlInterUnRr")
	private String nilInterStateUnReg;

	@Expose
	@SerializedName("nlIntraUnRr")
	private String nilIntraStateUnReg;

	@Expose
	@SerializedName("exInterRr")
	private String extInterStateReg;

	@Expose
	@SerializedName("exIntraRr")
	private String extIntraStateReg;

	@Expose
	@SerializedName("exInterUnRr")
	private String extInterStateUnReg;

	@Expose
	@SerializedName("exIntraUnRr")
	private String extIntraStateUnReg;

	@Expose
	@SerializedName("noInterRr")
	private String nonInterStateReg;

	@Expose
	@SerializedName("noIntraRr")
	private String nonIntraStateReg;

	@Expose
	@SerializedName("noInterUnRr")
	private String nonInterStateUnReg;

	@Expose
	@SerializedName("noIntraUnRr")
	private String nonIntraStateUnReg;

}
