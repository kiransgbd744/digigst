/**
 * 
 */
package com.ey.advisory.app.anx2.vendorsummary;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mohit.Basak
 *
 */
@Slf4j
@Component("VendorReconResponseDaoImpl")
public class VendorReconResponseDaoImpl implements VendorReconResponseDao{
	
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	
	@Override
	public List<VendorReconResponseRepDto> loadReconResponseDetails(
			VendorReconSummaryReqDto reqDto) {

		String taxPeriod = reqDto.getTaxPeriod();
		//int derivedRetPeriod = GenUtil.convertTaxPeriodToInt(taxPeriod);

		List<String> vendorGstinList = reqDto.getVendorGstin();
		List<String> vendorPanList = reqDto.getVendorPan();

		List<String> gstinList = reqDto.getGstins();
		List<String> dataFromUi = reqDto.getData();
		
		//logic for vPan if not given from ui but Vendor Gstin is/are given 
		if (!(vendorGstinList.isEmpty()) && (vendorPanList.isEmpty())) {

			vendorPanList = vendorGstinList.stream()
					.map(s -> s.substring(2, 12)).collect(Collectors.toList());
		}
	
		String vPans = String.join(",", vendorPanList).isEmpty() ? null
				: String.join(",", vendorPanList);
		String vGstins = String.join(",", vendorGstinList).isEmpty() ? null
				: String.join(",", vendorGstinList);
		try {
			
			LOGGER.info("Invoking USP_VENDOR_SUMMARY_RESPONSE Stored Proc");
			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery("USP_VENDOR_SUMMARY_RESPONSE");

			storedProc.registerStoredProcedureParameter("P_GSTIN", String.class,
					ParameterMode.IN);
			storedProc.registerStoredProcedureParameter("P_TAX_PERIOD",
					String.class, ParameterMode.IN);
			
			storedProc.setParameter("P_GSTIN", String.join(",", gstinList));
			storedProc.setParameter("P_TAX_PERIOD", taxPeriod);

			if (vPans != null) {
				storedProc.registerStoredProcedureParameter("P_VENDOR_PAN",
						String.class, ParameterMode.IN);
				storedProc.setParameter("P_VENDOR_PAN", vPans);
			}
			if (vGstins != null) {
				storedProc.registerStoredProcedureParameter("P_VENDOR_GSTIN",
						String.class, ParameterMode.IN);
				storedProc.setParameter("P_VENDOR_GSTIN", vGstins);
			}

			
			List<Object[]> list = storedProc.getResultList();

			LOGGER.debug("Converting Query And converting to List BEGIN");
			List<VendorReconResponseRepDto> retList = list.parallelStream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));
			LOGGER.debug("Converting Query And converting to List END");
			return retList;

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Converting ResultSet to List in Vendor "
					+ "Recon Response Query Execution ");
			throw new AppException("Unexpected error in query execution.");
		}
		
	}

	
	private VendorReconResponseRepDto convert(Object[] arr) {

		VendorReconResponseRepDto obj = new VendorReconResponseRepDto();

		obj.setVendorPan(arr[0] != null ? (String) arr[0] : "");
		obj.setVendorName(arr[1] != null ? (String) arr[1] : "");
		obj.setGstin(arr[2] != null ? (String) arr[2] : "");
		obj.setDocType(arr[3] != null ? (String) arr[3] : "");
		
		
		obj.setPercentageAcceptA2AndITCA2((BigDecimal) arr[4]);
		obj.setA2AcceptA2AndITCA2((BigDecimal) arr[5]);
		obj.setPRAcceptA2AndITCA2((BigDecimal) arr[6]);
		
		obj.setPercentageAcceptA2AndITCPRavailable((BigDecimal) arr[7]);
		obj.setA2AcceptA2AndITCPRavailable((BigDecimal) arr[8]);
		obj.setPRAcceptA2AndITCPRavailable((BigDecimal) arr[9]);
		
		obj.setPercentageAcceptA2AndITCPRTax((BigDecimal) arr[10]);
		obj.setA2AcceptA2AndITCPRTax((BigDecimal) arr[11]);
		obj.setPRAcceptA2AndITCPRTax((BigDecimal) arr[12]);
		
		obj.setPercentagePendingANX2((BigDecimal) arr[13]);
		obj.setA2PendingANX2((BigDecimal) arr[14]);
		obj.setPRPendingANX2((BigDecimal) arr[15]);
		
		obj.setPercentageRejectANX2((BigDecimal) arr[16]);
		obj.setA2RejectANX2((BigDecimal) arr[17]);
		obj.setPRRejectANX2((BigDecimal) arr[18]);
		
		obj.setPercentageRejectA2AdITCPRavailable((BigDecimal) arr[19]);
		obj.setA2RejectA2andITCPRavailable((BigDecimal) arr[20]);
		obj.setPRRejectA2andITCPRavailable((BigDecimal) arr[21]);
		
		obj.setPercentageRejectA2andITCPRTax((BigDecimal) arr[22]);
		obj.setA2RejectA2AndITCPRTax((BigDecimal) arr[23]);
		obj.setPRRejectA2AndITCPRTax((BigDecimal) arr[24]);
		
		obj.setPercentageITCPRAvailable((BigDecimal) arr[25]);
		obj.setA2ITCPRAvailable((BigDecimal) arr[26]);
		obj.setPRITCPRAvailable((BigDecimal) arr[27]);
		
		obj.setPercentageITCPRTax((BigDecimal) arr[28]);
		obj.setA2ITCPRTax((BigDecimal) arr[29]);
		obj.setPRITCPRTax((BigDecimal) arr[30]);
		
		obj.setPercentageAcceptAnx2OfEarlierTaxPeriod((BigDecimal) arr[31]);
		obj.setA2AcceptAnx2OfEarlierTaxPeriod((BigDecimal) arr[32]);
		obj.setPRAcceptAnx2OfEarlierTaxPeriod((BigDecimal) arr[33]);
		
		
		obj.setPercentageProvisionalITCPRAvailable((BigDecimal) arr[34]);
		obj.setProvisionalITCPRAvailable((BigDecimal) arr[35]);
		
		obj.setPercentageProvisionalITCPRTax((BigDecimal) arr[36]);
		obj.setProvisionalITCPRTax((BigDecimal) arr[37]);
		
		obj.setPercentageNoActionReconciled((BigDecimal) arr[38]);
		obj.setA2NoActionReconciled((BigDecimal) arr[39]);
		obj.setPRNoActionReconciled((BigDecimal) arr[40]);
		
		obj.setPercentageNoActionAddlA2((BigDecimal) arr[41]);
		obj.setNoActionAddlA2((BigDecimal) arr[42]);
		
		
		obj.setPercentageNoActionAddlPR((BigDecimal) arr[43]);
		obj.setNoActionAddlPR((BigDecimal) arr[44]);
		
		obj.setReturnFillingStatus(/*arr[45] != null ? (String) arr[45] : ""*/
				"");
		
		obj.setLevel(/*arr[46] != null ? (String) arr[46] : ""*/"L3");
		
		obj.setRating(new BigDecimal(0));
		
		
		
		return obj;
	}


}
