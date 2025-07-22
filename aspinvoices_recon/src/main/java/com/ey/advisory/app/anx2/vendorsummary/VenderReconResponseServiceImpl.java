/**
 * 
 */
package com.ey.advisory.app.anx2.vendorsummary;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mohit.Basak
 *
 */

@Service("VenderReconResponseServiceImpl")
@Slf4j
public class VenderReconResponseServiceImpl implements VenderReconResponseService{

	//not required as of now 
/*	@Autowired
	@Qualifier("BasicCommonSecParam")
	BasicCommonSecParam basicCommonSecParam;*/
	
	@Autowired
	@Qualifier("VendorReconResponseDaoImpl")
	private VendorReconResponseDao vendorReconResponseDao;
	
	public static BigDecimal PERCENTAGE = new BigDecimal(100);

	@Override
	public List<VendorReconResponseRepDto> getReconResponseDetails(
			VendorReconSummaryReqDto reqDto) {
		
		//data from dB setting queried output data as L3
		List<VendorReconResponseRepDto> l3Coll 
		= vendorReconResponseDao.loadReconResponseDetails(reqDto);

		//L2 creation based on GSTIN and summing up elements data in L3
		Collection<VendorReconResponseRepDto> l2Coll = l3Coll.stream()
				.collect(Collectors.groupingBy(o1 -> o1.getGstin(),
						getCollectorForLevel("L2")))
				.values();

		Collection<VendorReconResponseRepDto> l1Coll = l2Coll.stream()
				.collect(Collectors.groupingBy(o1 -> o1.getVendorPan(),
						getCollectorForLevel("L1")))
				.values();//L1 creation based on vendor Pan and summing up elements data in L2
	/*	
		//taking average of counts for percentage in L1
				l1Coll.forEach(o -> 
						{
							o.setPercentageAcceptA2AndITCA2(o.getPercentageAcceptA2AndITCA2()
									.divide(new BigDecimal(o.getCount()),2, BigDecimal.ROUND_HALF_EVEN));
							o.setPercentageAcceptA2AndITCPRavailable(o.getPercentageAcceptA2AndITCPRavailable()
									.divide(new BigDecimal(o.getCount()),2, BigDecimal.ROUND_HALF_EVEN));
							o.setPercentageAcceptA2AndITCPRTax(o.getPercentageAcceptA2AndITCPRTax()
									.divide(new BigDecimal(o.getCount()),2, BigDecimal.ROUND_HALF_EVEN));
							o.setPercentagePendingANX2(o.getPercentagePendingANX2()
									.divide(new BigDecimal(o.getCount()),2, BigDecimal.ROUND_HALF_EVEN));
							o.setPercentageRejectANX2(o.getPercentageRejectANX2()
									.divide(new BigDecimal(o.getCount()),2, BigDecimal.ROUND_HALF_EVEN));
							o.setPercentageRejectA2AdITCPRavailable(o.getPercentageRejectA2AdITCPRavailable()
									.divide(new BigDecimal(o.getCount()),2, BigDecimal.ROUND_HALF_EVEN));
							o.setPercentageRejectA2andITCPRTax(o.getPercentageRejectA2andITCPRTax()
									.divide(new BigDecimal(o.getCount()),2, BigDecimal.ROUND_HALF_EVEN));
							o.setPercentageITCPRAvailable(o.getPercentageITCPRAvailable()
									.divide(new BigDecimal(o.getCount()),2, BigDecimal.ROUND_HALF_EVEN));
							o.setPercentageITCPRTax(o.getPercentageITCPRTax()
									.divide(new BigDecimal(o.getCount()),2, BigDecimal.ROUND_HALF_EVEN));
							o.setPercentageAcceptAnx2OfEarlierTaxPeriod(o.getPercentageAcceptAnx2OfEarlierTaxPeriod()
									.divide(new BigDecimal(o.getCount()),2, BigDecimal.ROUND_HALF_EVEN));
							o.setPercentageProvisionalITCPRAvailable(o.getPercentageProvisionalITCPRAvailable()
									.divide(new BigDecimal(o.getCount()),2, BigDecimal.ROUND_HALF_EVEN));
							o.setPercentageProvisionalITCPRTax(o.getPercentageProvisionalITCPRTax()
									.divide(new BigDecimal(o.getCount()),2, BigDecimal.ROUND_HALF_EVEN));
							o.setPercentageNoActionReconciled(o.getPercentageNoActionReconciled()
									.divide(new BigDecimal(o.getCount()),2, BigDecimal.ROUND_HALF_EVEN));
							o.setPercentageNoActionAddlA2(o.getPercentageNoActionAddlA2()
									.divide(new BigDecimal(o.getCount()),2, BigDecimal.ROUND_HALF_EVEN));
							o.setPercentageNoActionAddlPR(o.getPercentageNoActionAddlPR()
									.divide(new BigDecimal(o.getCount()),2, BigDecimal.ROUND_HALF_EVEN));
							o.setPercentageNoActionAllowed(o.getPercentageNoActionAllowed()
									.divide(new BigDecimal(o.getCount()),2, BigDecimal.ROUND_HALF_EVEN));
							
						});
				
	*/	
		List<VendorReconResponseRepDto> list = new 
				ImmutableList.Builder<VendorReconResponseRepDto>()
				.addAll(l1Coll).addAll(l2Coll).addAll(l3Coll).build();
	
		if(LOGGER.isDebugEnabled()){
			String msg = String.format("The VendorReconResponseRepDto "
					+ " has been generated with %d records", 
					list.size());
			System.out.println(msg);
		}
		// Sort the above list by a key that orders the elements in a
		// hierarchical manner for displaying in the UI as a hierarchy.
		List<VendorReconResponseRepDto> retList = list.stream()
				.sorted(Comparator.comparing(o -> o.getKey()))
				.collect(Collectors.toList());
		return retList;
	}

	private Collector<VendorReconResponseRepDto, ?, 
			VendorReconResponseRepDto> getCollectorForLevel(
			String level) {
		return Collectors.reducing(new VendorReconResponseRepDto(level),
				(o1, o2) -> add(o1, o2));
	}

	private VendorReconResponseRepDto add(
			VendorReconResponseRepDto cpt1,
			VendorReconResponseRepDto cpt2) {
		
		String cgstin = "L1".equals(cpt1.getLevel()) ? null : cpt2.getGstin();
		String taxDocType = "L1".equals(cpt1.getLevel()) ? null :
			cpt2.getDocType();
			
		if (cpt1.getLevel().equals("L1")) {

			return new VendorReconResponseRepDto(
					cpt2.getVendorPan(), 
					cgstin,
					taxDocType, 
					cpt2.getVendorName(),

					cpt2.getPercentageAcceptA2AndITCA2(),
					cpt1.getA2AcceptA2AndITCA2()
							.add(cpt2.getA2AcceptA2AndITCA2()),
					cpt1.getPRAcceptA2AndITCA2()
							.add(cpt2.getPRAcceptA2AndITCA2()),

					cpt2.getPercentageAcceptA2AndITCPRavailable(),
					cpt1.getA2AcceptA2AndITCPRavailable()
							.add(cpt2.getA2AcceptA2AndITCPRavailable()),
					cpt1.getPRAcceptA2AndITCPRavailable()
							.add(cpt2.getPRAcceptA2AndITCPRavailable()),

					cpt2.getPercentageAcceptA2AndITCPRTax(),
					cpt1.getA2AcceptA2AndITCPRTax()
							.add(cpt2.getA2AcceptA2AndITCPRTax()),
					cpt1.getPRAcceptA2AndITCPRTax()
							.add(cpt2.getPRAcceptA2AndITCPRTax()),

					cpt2.getPercentagePendingANX2(),
					cpt1.getA2PendingANX2().add(cpt2.getA2PendingANX2()),
					cpt1.getPRPendingANX2().add(cpt2.getPRPendingANX2()),

					cpt2.getPercentageRejectANX2(),
					cpt1.getA2RejectANX2().add(cpt2.getA2RejectANX2()),
					cpt1.getPRRejectANX2().add(cpt2.getPRRejectANX2()),

					cpt2.getPercentageRejectA2AdITCPRavailable(),
					cpt1.getA2RejectA2andITCPRavailable()
							.add(cpt2.getA2RejectA2andITCPRavailable()),
					cpt1.getPRRejectA2andITCPRavailable()
							.add(cpt2.getPRRejectA2andITCPRavailable()),

					cpt2.getPercentageRejectA2andITCPRTax(),
					cpt1.getA2RejectA2AndITCPRTax()
							.add(cpt2.getA2RejectA2AndITCPRTax()),
					cpt1.getPRRejectA2AndITCPRTax()
							.add(cpt2.getPRRejectA2AndITCPRTax()),

					cpt2.getPercentageITCPRAvailable(),
					cpt1.getA2ITCPRAvailable().add(cpt2.getA2ITCPRAvailable()),
					cpt1.getPRITCPRAvailable().add(cpt2.getPRITCPRAvailable()),

					cpt2.getPercentageITCPRTax(),
					cpt1.getA2ITCPRTax().add(cpt2.getA2ITCPRTax()),
					cpt1.getPRITCPRTax().add(cpt2.getPRITCPRTax()),

					cpt2.getPercentageAcceptAnx2OfEarlierTaxPeriod(),
					cpt1.getA2AcceptAnx2OfEarlierTaxPeriod()
							.add(cpt2.getA2AcceptAnx2OfEarlierTaxPeriod()),
					cpt1.getPRAcceptAnx2OfEarlierTaxPeriod()
							.add(cpt2.getPRAcceptAnx2OfEarlierTaxPeriod()),

					cpt2.getPercentageProvisionalITCPRAvailable(),
					cpt1.getProvisionalITCPRAvailable()
							.add(cpt2.getProvisionalITCPRAvailable()),

					cpt2.getPercentageProvisionalITCPRTax(),
					cpt1.getProvisionalITCPRTax()
							.add(cpt2.getProvisionalITCPRTax()),

					cpt2.getPercentageNoActionReconciled(),
					cpt1.getA2NoActionReconciled()
							.add(cpt2.getA2NoActionReconciled()),
					cpt1.getPRNoActionReconciled()
							.add(cpt2.getPRNoActionReconciled()),

					cpt2.getPercentageNoActionAddlA2(),
					cpt1.getNoActionAddlA2().add(cpt2.getNoActionAddlA2()),

					cpt2.getPercentageNoActionAddlPR(),
					cpt1.getNoActionAddlPR().add(cpt2.getNoActionAddlPR()),

					cpt2.getReturnFillingStatus(), cpt1.getLevel(),
					cpt2.getRating(),
					
					cpt2.getPercentageNoActionAllowed(),
					cpt1.getA2NoActionAllowed().add(cpt2.getA2NoActionAllowed()),
					cpt1.getPRNoActionAllowed().add(cpt2.getPRNoActionAllowed()),
					
					cpt2.getCount()+cpt1.getCount()
					);

		}
		if("CR".equalsIgnoreCase(taxDocType) ){

			return new VendorReconResponseRepDto(
					cpt2.getVendorPan(),
					cgstin,
					taxDocType,
					cpt2.getVendorName(),

					cpt2.getPercentageAcceptA2AndITCA2(),
					cpt1.getA2AcceptA2AndITCA2()
							.subtract(cpt2.getA2AcceptA2AndITCA2()),
					cpt1.getPRAcceptA2AndITCA2()
							.subtract(cpt2.getPRAcceptA2AndITCA2()),

					cpt2.getPercentageAcceptA2AndITCPRavailable(),
					cpt1.getA2AcceptA2AndITCPRavailable()
							.subtract(cpt2.getA2AcceptA2AndITCPRavailable()),
					cpt1.getPRAcceptA2AndITCPRavailable()
							.subtract(cpt2.getPRAcceptA2AndITCPRavailable()),

					cpt2.getPercentageAcceptA2AndITCPRTax(),
					cpt1.getA2AcceptA2AndITCPRTax()
							.subtract(cpt2.getA2AcceptA2AndITCPRTax()),
					cpt1.getPRAcceptA2AndITCPRTax()
							.subtract(cpt2.getPRAcceptA2AndITCPRTax()),

					cpt2.getPercentagePendingANX2(),
					cpt1.getA2PendingANX2().subtract(cpt2.getA2PendingANX2()),
					cpt1.getPRPendingANX2().subtract(cpt2.getPRPendingANX2()),

					cpt2.getPercentageRejectANX2(),
					cpt1.getA2RejectANX2().subtract(cpt2.getA2RejectANX2()),
					cpt1.getPRRejectANX2().subtract(cpt2.getPRRejectANX2()),

					cpt2.getPercentageRejectA2AdITCPRavailable(),
					cpt1.getA2RejectA2andITCPRavailable()
							.subtract(cpt2.getA2RejectA2andITCPRavailable()),
					cpt1.getPRRejectA2andITCPRavailable()
							.subtract(cpt2.getPRRejectA2andITCPRavailable()),

					cpt2.getPercentageRejectA2andITCPRTax(),
					cpt1.getA2RejectA2AndITCPRTax()
							.subtract(cpt2.getA2RejectA2AndITCPRTax()),
					cpt1.getPRRejectA2AndITCPRTax()
							.subtract(cpt2.getPRRejectA2AndITCPRTax()),

					cpt2.getPercentageITCPRAvailable(),
					cpt1.getA2ITCPRAvailable().subtract(cpt2.getA2ITCPRAvailable()),
					cpt1.getPRITCPRAvailable().subtract(cpt2.getPRITCPRAvailable()),

					cpt2.getPercentageITCPRTax(),
					cpt1.getA2ITCPRTax().subtract(cpt2.getA2ITCPRTax()),
					cpt1.getPRITCPRTax().subtract(cpt2.getPRITCPRTax()),

					cpt2.getPercentageAcceptAnx2OfEarlierTaxPeriod(),
					cpt1.getA2AcceptAnx2OfEarlierTaxPeriod()
							.subtract(cpt2.getA2AcceptAnx2OfEarlierTaxPeriod()),
					cpt1.getPRAcceptAnx2OfEarlierTaxPeriod()
							.subtract(cpt2.getPRAcceptAnx2OfEarlierTaxPeriod()),

					cpt2.getPercentageProvisionalITCPRAvailable(),
					cpt1.getProvisionalITCPRAvailable()
							.subtract(cpt2.getProvisionalITCPRAvailable()),

					cpt2.getPercentageProvisionalITCPRTax(),
					cpt1.getProvisionalITCPRTax()
							.subtract(cpt2.getProvisionalITCPRTax()),

					cpt2.getPercentageNoActionReconciled(),
					cpt1.getA2NoActionReconciled()
							.subtract(cpt2.getA2NoActionReconciled()),
					cpt1.getPRNoActionReconciled()
							.subtract(cpt2.getPRNoActionReconciled()),

					cpt2.getPercentageNoActionAddlA2(),
					cpt1.getNoActionAddlA2().subtract(cpt2.getNoActionAddlA2()),

					cpt2.getPercentageNoActionAddlPR(),
					cpt1.getNoActionAddlPR().subtract(cpt2.getNoActionAddlPR()),

					cpt2.getReturnFillingStatus(), cpt1.getLevel(),
					cpt2.getRating(),
					
					cpt2.getPercentageNoActionAllowed(),
					cpt1.getA2NoActionAllowed().subtract(cpt2.getA2NoActionAllowed()),
					cpt1.getPRNoActionAllowed().subtract(cpt2.getPRNoActionAllowed()),
					
					cpt1.getCount()+cpt2.getCount()+1);

		}
		


				return new VendorReconResponseRepDto(
						cpt2.getVendorPan(),
						cgstin,
						taxDocType,
						cpt2.getVendorName(),

						cpt2.getPercentageAcceptA2AndITCA2(),
						cpt1.getA2AcceptA2AndITCA2()
								.add(cpt2.getA2AcceptA2AndITCA2()),
						cpt1.getPRAcceptA2AndITCA2()
								.add(cpt2.getPRAcceptA2AndITCA2()),

						cpt2.getPercentageAcceptA2AndITCPRavailable(),
						cpt1.getA2AcceptA2AndITCPRavailable()
								.add(cpt2.getA2AcceptA2AndITCPRavailable()),
						cpt1.getPRAcceptA2AndITCPRavailable()
								.add(cpt2.getPRAcceptA2AndITCPRavailable()),

						cpt2.getPercentageAcceptA2AndITCPRTax(),
						cpt1.getA2AcceptA2AndITCPRTax()
								.add(cpt2.getA2AcceptA2AndITCPRTax()),
						cpt1.getPRAcceptA2AndITCPRTax()
								.add(cpt2.getPRAcceptA2AndITCPRTax()),

						cpt2.getPercentagePendingANX2(),
						cpt1.getA2PendingANX2().add(cpt2.getA2PendingANX2()),
						cpt1.getPRPendingANX2().add(cpt2.getPRPendingANX2()),

						cpt2.getPercentageRejectANX2(),
						cpt1.getA2RejectANX2().add(cpt2.getA2RejectANX2()),
						cpt1.getPRRejectANX2().add(cpt2.getPRRejectANX2()),

						cpt2.getPercentageRejectA2AdITCPRavailable(),
						cpt1.getA2RejectA2andITCPRavailable()
								.add(cpt2.getA2RejectA2andITCPRavailable()),
						cpt1.getPRRejectA2andITCPRavailable()
								.add(cpt2.getPRRejectA2andITCPRavailable()),

						cpt2.getPercentageRejectA2andITCPRTax(),
						cpt1.getA2RejectA2AndITCPRTax()
								.add(cpt2.getA2RejectA2AndITCPRTax()),
						cpt1.getPRRejectA2AndITCPRTax()
								.add(cpt2.getPRRejectA2AndITCPRTax()),

						cpt2.getPercentageITCPRAvailable(),
						cpt1.getA2ITCPRAvailable().add(cpt2.getA2ITCPRAvailable()),
						cpt1.getPRITCPRAvailable().add(cpt2.getPRITCPRAvailable()),

						cpt2.getPercentageITCPRTax(),
						cpt1.getA2ITCPRTax().add(cpt2.getA2ITCPRTax()),
						cpt1.getPRITCPRTax().add(cpt2.getPRITCPRTax()),

						cpt2.getPercentageAcceptAnx2OfEarlierTaxPeriod(),
						cpt1.getA2AcceptAnx2OfEarlierTaxPeriod()
								.add(cpt2.getA2AcceptAnx2OfEarlierTaxPeriod()),
						cpt1.getPRAcceptAnx2OfEarlierTaxPeriod()
								.add(cpt2.getPRAcceptAnx2OfEarlierTaxPeriod()),

						cpt2.getPercentageProvisionalITCPRAvailable(),
						cpt1.getProvisionalITCPRAvailable()
								.add(cpt2.getProvisionalITCPRAvailable()),

						cpt2.getPercentageProvisionalITCPRTax(),
						cpt1.getProvisionalITCPRTax()
								.add(cpt2.getProvisionalITCPRTax()),

						cpt2.getPercentageNoActionReconciled(),
						cpt1.getA2NoActionReconciled()
								.add(cpt2.getA2NoActionReconciled()),
						cpt1.getPRNoActionReconciled()
								.add(cpt2.getPRNoActionReconciled()),

						cpt2.getPercentageNoActionAddlA2(),
						cpt1.getNoActionAddlA2().add(cpt2.getNoActionAddlA2()),

						cpt2.getPercentageNoActionAddlPR(),
						cpt1.getNoActionAddlPR().add(cpt2.getNoActionAddlPR()),

						cpt2.getReturnFillingStatus(), cpt1.getLevel(),
						cpt2.getRating(),
						
						cpt2.getPercentageNoActionAllowed(),
						cpt1.getA2NoActionAllowed().add(cpt2.getA2NoActionAllowed()),
						cpt1.getPRNoActionAllowed().add(cpt2.getPRNoActionAllowed()),
						
						cpt1.getCount()+cpt2.getCount()+1);

			
}

}