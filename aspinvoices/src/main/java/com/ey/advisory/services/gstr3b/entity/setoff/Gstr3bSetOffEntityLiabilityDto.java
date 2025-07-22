package com.ey.advisory.services.gstr3b.entity.setoff;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ravindra V S
 *
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Gstr3bSetOffEntityLiabilityDto {

	@Expose
	private String gstin;

	@Expose
	@SerializedName("liabilityIgst")
	private BigDecimal liabilityIgst;

	@Expose
	@SerializedName("liabilityCgst")
	private BigDecimal liabilityCgst;

	@Expose
	@SerializedName("liabilitySgst")
	private BigDecimal liabilitySgst;

	@Expose
	@SerializedName("liabilityCess")
	private BigDecimal liabilityCess;

	@Expose
	@SerializedName("sectionName")
	private String sectionName;

	@Expose
	@SerializedName("createDate")
	private LocalDateTime createDate;

}
