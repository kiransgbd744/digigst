package com.ey.advisory.services.gstr3b.entity.setoff;

import java.math.BigDecimal;

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
public class Gstr3bSetOffEntityPditcDto {

	@Expose
	private String gstin;

	@Expose
	@SerializedName("paidThroughItcIgst")
	private BigDecimal paidThroughItcIgst;

	@Expose
	@SerializedName("paidThroughItcCgst")
	private BigDecimal paidThroughItcCgst;
	
	@Expose
	@SerializedName("paidThroughItcSgst")
	private BigDecimal paidThroughItcSgst;
	
	@Expose
	@SerializedName("paidThroughItcCess")
	private BigDecimal paidThroughItcCess;

}
