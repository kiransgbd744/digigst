/**
 * 
 */
package com.ey.advisory.app.anx2.vendorsummary;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.beanutils.converters.BigDecimalConverter;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Mohit.Basak
 *
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
public class VendorReconResponseRepDto {


	@Expose
	private String level;
	
	@Expose
	private String vendorPan;

	@Expose
	private String gstin;

	@Expose
	private String docType;

	@Expose
	private String vendorName;

	@Expose
	private BigDecimal percentageAcceptA2AndITCA2 = BigDecimal.ZERO;
	@Expose
	private BigDecimal a2AcceptA2AndITCA2 = BigDecimal.ZERO;
	@Expose
	private BigDecimal pRAcceptA2AndITCA2 = BigDecimal.ZERO;

	@Expose
	private BigDecimal percentageAcceptA2AndITCPRavailable = BigDecimal.ZERO;
	@Expose
	private BigDecimal a2AcceptA2AndITCPRavailable = BigDecimal.ZERO;
	@Expose
	private BigDecimal pRAcceptA2AndITCPRavailable = BigDecimal.ZERO;

	@Expose
	private BigDecimal percentageAcceptA2AndITCPRTax = BigDecimal.ZERO;
	@Expose
	private BigDecimal a2AcceptA2AndITCPRTax = BigDecimal.ZERO;
	@Expose
	private BigDecimal pRAcceptA2AndITCPRTax = BigDecimal.ZERO;

	@Expose
	private BigDecimal percentagePendingANX2 = BigDecimal.ZERO;
	@Expose
	private BigDecimal a2PendingANX2 = BigDecimal.ZERO;
	@Expose
	private BigDecimal pRPendingANX2 = BigDecimal.ZERO;

	@Expose
	private BigDecimal percentageRejectANX2 = BigDecimal.ZERO;
	@Expose
	private BigDecimal a2RejectANX2 = BigDecimal.ZERO;
	@Expose
	private BigDecimal pRRejectANX2 = BigDecimal.ZERO;

	@Expose
	private BigDecimal percentageRejectA2AdITCPRavailable = BigDecimal.ZERO;
	@Expose
	private BigDecimal A2RejectA2andITCPRavailable = BigDecimal.ZERO;
	@Expose
	private BigDecimal PRRejectA2andITCPRavailable = BigDecimal.ZERO;

	@Expose
	private BigDecimal percentageRejectA2andITCPRTax = BigDecimal.ZERO;
	@Expose
	private BigDecimal a2RejectA2AndITCPRTax = BigDecimal.ZERO;
	@Expose
	private BigDecimal pRRejectA2AndITCPRTax = BigDecimal.ZERO;

	@Expose
	private BigDecimal percentageITCPRAvailable = BigDecimal.ZERO;
	@Expose
	private BigDecimal a2ITCPRAvailable = BigDecimal.ZERO;
	@Expose
	private BigDecimal pRITCPRAvailable = BigDecimal.ZERO;

	@Expose
	private BigDecimal percentageITCPRTax = BigDecimal.ZERO;
	@Expose
	private BigDecimal a2ITCPRTax = BigDecimal.ZERO;
	@Expose
	private BigDecimal pRITCPRTax = BigDecimal.ZERO;

	@Expose
	private BigDecimal percentageAcceptAnx2OfEarlierTaxPeriod = BigDecimal.ZERO;
	@Expose
	private BigDecimal a2AcceptAnx2OfEarlierTaxPeriod = BigDecimal.ZERO;
	@Expose
	private BigDecimal pRAcceptAnx2OfEarlierTaxPeriod = BigDecimal.ZERO;

	@Expose
	private BigDecimal percentageProvisionalITCPRAvailable = BigDecimal.ZERO;
	@Expose
	private BigDecimal provisionalITCPRAvailable = BigDecimal.ZERO;

	@Expose
	private BigDecimal percentageProvisionalITCPRTax = BigDecimal.ZERO;
	@Expose
	private BigDecimal provisionalITCPRTax = BigDecimal.ZERO;

	@Expose
	private BigDecimal percentageNoActionReconciled = BigDecimal.ZERO;
	@Expose
	private BigDecimal a2NoActionReconciled = BigDecimal.ZERO;
	@Expose
	private BigDecimal pRNoActionReconciled = BigDecimal.ZERO;

	@Expose
	private BigDecimal percentageNoActionAddlA2 = BigDecimal.ZERO;
	@Expose
	private BigDecimal noActionAddlA2 = BigDecimal.ZERO;

	@Expose
	private BigDecimal percentageNoActionAddlPR = BigDecimal.ZERO;
	@Expose
	private BigDecimal noActionAddlPR = BigDecimal.ZERO;

	@Expose
	private String returnFillingStatus;

	@Expose
	private BigDecimal rating = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal percentageNoActionAllowed = BigDecimal.ZERO;
	@Expose
	private BigDecimal a2NoActionAllowed = BigDecimal.ZERO;
	@Expose
	private BigDecimal pRNoActionAllowed = BigDecimal.ZERO;

	@Expose
	private int count = 0;

	public VendorReconResponseRepDto(String level) {
		this.level = level;
	}

	public String getKey() {
		if ("L1".equals(level))
			return vendorPan;
		if ("L2".equals(level))
			return vendorPan + "#" + gstin;
		return vendorPan + "#" + gstin + "#" + docType;
	}
	public VendorReconResponseRepDto(
			String vendorPan,
			String gstin,
			String docType,
			String vendorName,
			
			BigDecimal percentageAcceptA2AndITCA2,
			BigDecimal a2AcceptA2AndITCA2, 
			BigDecimal pRAcceptA2AndITCA2,
			
			BigDecimal percentageAcceptA2AndITCPRavailable,
			BigDecimal a2AcceptA2AndITCPRavailable,
			BigDecimal pRAcceptA2AndITCPRavailable,
			
			BigDecimal percentageAcceptA2AndITCPRTax,
			BigDecimal a2AcceptA2AndITCPRTax,
			BigDecimal pRAcceptA2AndITCPRTax,
			
			BigDecimal percentagePendingANX2, 
			BigDecimal a2PendingANX2,
			BigDecimal pRPendingANX2, 
			
			BigDecimal percentageRejectANX2,
			BigDecimal a2RejectANX2,
			BigDecimal pRRejectANX2,
			
			BigDecimal percentageRejectA2AdITCPRavailable,
			BigDecimal a2RejectA2andITCPRavailable,
			BigDecimal pRRejectA2andITCPRavailable,
			
			BigDecimal percentageRejectA2andITCPRTax,
			BigDecimal a2RejectA2AndITCPRTax,
			BigDecimal pRRejectA2AndITCPRTax,
			
			BigDecimal percentageITCPRAvailable,
			BigDecimal a2itcprAvailable,
			BigDecimal pRITCPRAvailable,
			
			BigDecimal percentageITCPRTax,
			BigDecimal a2itcprTax, 
			BigDecimal pRITCPRTax,
			
			BigDecimal percentageAcceptAnx2OfEarlierTaxPeriod,
			BigDecimal a2AcceptAnx2OfEarlierTaxPeriod,
			BigDecimal pRAcceptAnx2OfEarlierTaxPeriod,
			
			BigDecimal percentageProvisionalITCPRAvailable,
			BigDecimal provisionalITCPRAvailable,
			
			BigDecimal percentageProvisionalITCPRTax,
			BigDecimal provisionalITCPRTax,
			
			BigDecimal percentageNoActionReconciled,
			BigDecimal a2NoActionReconciled,
			BigDecimal pRNoActionReconciled,
			
			BigDecimal percentageNoActionAddlA2,
			BigDecimal noActionAddlA2,
			
			BigDecimal percentageNoActionAddlPR,
			BigDecimal noActionAddlPR,
			
			String returnFillingStatus,
			String level, 
			BigDecimal rating,
			
			BigDecimal percentageNoActionAllowed,
			BigDecimal a2NoActionAllowed,
			BigDecimal pRNoActionAllowed,
			
			int count) {
		super();
		this.vendorPan = vendorPan;
		this.gstin = gstin;
		this.docType = docType;
		this.vendorName = vendorName;
		this.percentageAcceptA2AndITCA2 = percentageAcceptA2AndITCA2;
		this.a2AcceptA2AndITCA2 = a2AcceptA2AndITCA2;
		this.pRAcceptA2AndITCA2 = pRAcceptA2AndITCA2;
		this.percentageAcceptA2AndITCPRavailable = percentageAcceptA2AndITCPRavailable;
		this.a2AcceptA2AndITCPRavailable = a2AcceptA2AndITCPRavailable;
		this.pRAcceptA2AndITCPRavailable = pRAcceptA2AndITCPRavailable;
		this.percentageAcceptA2AndITCPRTax = percentageAcceptA2AndITCPRTax;
		this.a2AcceptA2AndITCPRTax = a2AcceptA2AndITCPRTax;
		this.pRAcceptA2AndITCPRTax = pRAcceptA2AndITCPRTax;
		this.percentagePendingANX2 = percentagePendingANX2;
		this.a2PendingANX2 = a2PendingANX2;
		this.pRPendingANX2 = pRPendingANX2;
		this.percentageRejectANX2 = percentageRejectANX2;
		this.a2RejectANX2 = a2RejectANX2;
		this.pRRejectANX2 = pRRejectANX2;
		this.percentageRejectA2AdITCPRavailable = percentageRejectA2AdITCPRavailable;
		this.A2RejectA2andITCPRavailable = a2RejectA2andITCPRavailable;
		this.PRRejectA2andITCPRavailable = pRRejectA2andITCPRavailable;
		this.percentageRejectA2andITCPRTax = percentageRejectA2andITCPRTax;
		this.a2RejectA2AndITCPRTax = a2RejectA2AndITCPRTax;
		this.pRRejectA2AndITCPRTax = pRRejectA2AndITCPRTax;
		this.percentageITCPRAvailable = percentageITCPRAvailable;
		this.a2ITCPRAvailable = a2itcprAvailable;
		this.pRITCPRAvailable = pRITCPRAvailable;
		this.percentageITCPRTax = percentageITCPRTax;
		this.a2ITCPRTax = a2itcprTax;
		this.pRITCPRTax = pRITCPRTax;
		this.percentageAcceptAnx2OfEarlierTaxPeriod = percentageAcceptAnx2OfEarlierTaxPeriod;
		this.a2AcceptAnx2OfEarlierTaxPeriod = a2AcceptAnx2OfEarlierTaxPeriod;
		this.pRAcceptAnx2OfEarlierTaxPeriod = pRAcceptAnx2OfEarlierTaxPeriod;
		this.percentageProvisionalITCPRAvailable = percentageProvisionalITCPRAvailable;
		this.provisionalITCPRAvailable = provisionalITCPRAvailable;
		this.percentageProvisionalITCPRTax = percentageProvisionalITCPRTax;
		this.provisionalITCPRTax = provisionalITCPRTax;
		this.percentageNoActionReconciled = percentageNoActionReconciled;
		this.a2NoActionReconciled = a2NoActionReconciled;
		this.pRNoActionReconciled = pRNoActionReconciled;
		this.percentageNoActionAddlA2 = percentageNoActionAddlA2;
		this.noActionAddlA2 = noActionAddlA2;
		this.percentageNoActionAddlPR = percentageNoActionAddlPR;
		this.noActionAddlPR = noActionAddlPR;
		this.returnFillingStatus = returnFillingStatus;
		this.level = level;
		this.rating = rating;
		this.percentageNoActionAllowed = percentageNoActionAllowed;
		this.a2NoActionAllowed = a2NoActionAllowed;
		this.pRNoActionAllowed = pRNoActionAllowed;
		this.count= count;
	}

	
}
