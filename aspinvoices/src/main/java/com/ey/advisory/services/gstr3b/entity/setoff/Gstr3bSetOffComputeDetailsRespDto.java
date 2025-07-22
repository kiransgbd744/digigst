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
public class Gstr3bSetOffComputeDetailsRespDto {

	@Expose
	private String gstin;
	
	@Expose
	@SerializedName("netLiabilityIgst")
	private BigDecimal netLiabilityIgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("netLiabilityCgst")
	private BigDecimal netLiabilityCgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("netLiabilitySgst")
	private BigDecimal netLiabilitySgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("netLiabilityCess")
	private BigDecimal netLiabilityCess = BigDecimal.ZERO;

	@Expose
	@SerializedName("liabilityReverseChargeIgst")
	private BigDecimal liabilityReverseChargeIgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("liabilityReverseChargeCgst")
	private BigDecimal liabilityReverseChargeCgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("liabilityReverseChargeSgst")
	private BigDecimal liabilityReverseChargeSgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("liabilityReverseChargeCess")
	private BigDecimal liabilityReverseChargeCess = BigDecimal.ZERO;

	@Expose
	@SerializedName("interestAndLateFeeIgst")
	private BigDecimal interestAndLateFeeIgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("interestAndLateFeeCgst")
	private BigDecimal interestAndLateFeeCgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("interestAndLateFeeSgst")
	private BigDecimal interestAndLateFeeSgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("interestAndLateFeeCess")
	private BigDecimal interestAndLateFeeCess = BigDecimal.ZERO;

	@Expose
	@SerializedName("netGstLiabilityIgst")
	private BigDecimal netGstLiabilityIgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("netGstLiabilityCgst")
	private BigDecimal netGstLiabilityCgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("netGstLiabilitySgst")
	private BigDecimal netGstLiabilitySgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("netGstLiabilityCess")
	private BigDecimal netGstLiabilityCess = BigDecimal.ZERO;

	@Expose
	@SerializedName("addnlCashReqIgst")
	private BigDecimal addnlCashReqIgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("addnlCashReqCgst")
	private BigDecimal addnlCashReqCgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("addnlCashReqSgst")
	private BigDecimal addnlCashReqSgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("addnlCashReqCess")
	private BigDecimal addnlCashReqCess = BigDecimal.ZERO;

	public Gstr3bSetOffComputeDetailsRespDto merge(
			Gstr3bSetOffComputeDetailsRespDto that, String gstin) {
		BigDecimal netLiabilityIgst = this.netLiabilityIgst
				.add(that.getNetLiabilityIgst());
		BigDecimal netLiabilityCgst = this.netLiabilityCgst
				.add(that.netLiabilityCgst);
		BigDecimal netLiabilitySgst = this.netLiabilitySgst
				.add(that.netLiabilitySgst);
		BigDecimal netLiabilityCess = this.netLiabilityCess
				.add(that.netLiabilityCess);
		BigDecimal liabilityReverseChargeIgst = this.liabilityReverseChargeIgst
				.add(that.liabilityReverseChargeIgst);
		BigDecimal liabilityReverseChargeCgst = this.liabilityReverseChargeCgst
				.add(that.liabilityReverseChargeCgst);
		BigDecimal liabilityReverseChargeSgst = this.liabilityReverseChargeSgst
				.add(that.liabilityReverseChargeSgst);
		BigDecimal liabilityReverseChargeCess = this.liabilityReverseChargeCess
				.add(that.liabilityReverseChargeCess);
		BigDecimal interestAndLateFeeIgst = this.interestAndLateFeeIgst
				.add(that.interestAndLateFeeIgst);
		BigDecimal interestAndLateFeeCgst = this.interestAndLateFeeCgst
				.add(that.interestAndLateFeeCgst);
		BigDecimal interestAndLateFeeSgst = this.interestAndLateFeeSgst
				.add(that.interestAndLateFeeSgst);
		BigDecimal interestAndLateFeeCess = this.interestAndLateFeeCess
				.add(that.interestAndLateFeeCess);
		BigDecimal netGstLiabilityIgst = this.netGstLiabilityIgst
				.add(that.netGstLiabilityIgst);
		BigDecimal netGstLiabilityCgst = this.netGstLiabilityCgst
				.add(that.netGstLiabilityCgst);
		BigDecimal netGstLiabilitySgst = this.netGstLiabilitySgst
				.add(that.netGstLiabilitySgst);
		BigDecimal netGstLiabilityCess = this.netGstLiabilityCess
				.add(that.netGstLiabilityCess);
		BigDecimal addnlCashReqIgst = this.addnlCashReqIgst
				.add(that.addnlCashReqIgst);
		BigDecimal addnlCashReqCgst = this.addnlCashReqCgst
				.add(that.addnlCashReqCgst);
		BigDecimal addnlCashReqSgst = this.addnlCashReqSgst
				.add(that.addnlCashReqSgst);
		BigDecimal addnlCashReqCess = this.addnlCashReqCess
				.add(that.addnlCashReqCess);
		
		
		return new Gstr3bSetOffComputeDetailsRespDto(gstin,netLiabilityIgst,
				netLiabilityCgst, netLiabilitySgst, netLiabilityCess,
				liabilityReverseChargeIgst, liabilityReverseChargeCgst,
				liabilityReverseChargeSgst, liabilityReverseChargeCess,
				interestAndLateFeeIgst, interestAndLateFeeCgst,interestAndLateFeeSgst,
				interestAndLateFeeCess,netGstLiabilityIgst, netGstLiabilityCgst, netGstLiabilitySgst,
				netGstLiabilityCess, addnlCashReqIgst, addnlCashReqCgst, addnlCashReqSgst, addnlCashReqCess);
	}

	public Gstr3bSetOffComputeDetailsRespDto(BigDecimal netLiabilityIgst2,
			BigDecimal netLiabilityCgst2, BigDecimal netLiabilitySgst2,
			BigDecimal netLiabilityCess2,
			BigDecimal liabilityReverseChargeIgst2,
			BigDecimal liabilityReverseChargeCgst2,
			BigDecimal liabilityReverseChargeSgst2,
			BigDecimal liabilityReverseChargeCess2,
			BigDecimal interestAndLateFeeIgst2,
			BigDecimal interestAndLateFeeCgst2,
			BigDecimal interestAndLateFeeSgst2,
			BigDecimal interestAndLateFeeCess2, BigDecimal netGstLiabilityIgst2,
			BigDecimal netGstLiabilityCgst2, BigDecimal netGstLiabilitySgst2,
			BigDecimal netGstLiabilityCess2, BigDecimal addnlCashReqIgst2,
			BigDecimal addnlCashReqCgst2, BigDecimal addnlCashReqSgst2,
			BigDecimal addnlCashReqCess14) {
		// TODO Auto-generated constructor stub
	}

	

}
