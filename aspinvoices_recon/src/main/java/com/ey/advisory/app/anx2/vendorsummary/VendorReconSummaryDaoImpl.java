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
 * @author Nikhil.Duseja
 *
 */
@Slf4j
@Component("VendorReconSummaryDaoImpl")
public class VendorReconSummaryDaoImpl implements VendorReconSummaryDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<VendorReconSummaryResponseDto> loadReconSummaryDetails(
			VendorReconSummaryReqDto reqDto) {

		String taxPeriod = reqDto.getTaxPeriod();
		List<String> vendorGstinList = reqDto.getVendorGstin();
		List<String> vendorPanList = reqDto.getVendorPan();

		//logic for vPan if not given from ui but Vendor Gstin is/are given 
		if (!(vendorGstinList.isEmpty()) &&( vendorPanList.isEmpty())){
			
			vendorPanList = vendorGstinList.stream()
					.map(s -> s.substring(2, 12)).collect(Collectors.toList());
		}
		
		List<String> gstinList = reqDto.getGstins();
		//List<String> dataFromUi = reqDto.getData();
		
		String vPans = String.join(",", vendorPanList).isEmpty() ? null
				: String.join(",", vendorPanList);
		String vGstins = String.join(",", vendorGstinList).isEmpty() ? null
				: String.join(",", vendorGstinList);
		try {
			
			LOGGER.info("Invoking USP_VENDOR_SUMMARY_RECON Stored Proc");
			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery("USP_VENDOR_SUMMARY_RECON");

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
			
			@SuppressWarnings("unchecked")
			List<Object[]> list = storedProc.getResultList();

			LOGGER.debug("Converting Query And converting to List BEGIN");
			List<VendorReconSummaryResponseDto> retList = list.parallelStream()
					.map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));
			LOGGER.debug("Converting Query And converting to List END");
			return retList;

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Converting ResultSet to List in Vendor "
					+ "Recon Summary Query Execution ");
			throw new AppException("Unexpected error in query execution.");
		}
	}


	

	private VendorReconSummaryResponseDto convert(Object[] arr) {

		VendorReconSummaryResponseDto obj = new VendorReconSummaryResponseDto();

		obj.setVendorPan(arr[0] != null ? (String) arr[0] : "");
		obj.setVendorName(arr[1] != null ? (String) arr[1] : "");
		obj.setGstin(arr[2] != null ? (String) arr[2] : "");
		obj.setDocType(arr[3] != null ? (String) arr[3] : "");
	
		obj.setPrMatchPer((BigDecimal) arr[4]);
		obj.setAnx2MatchAmt((BigDecimal) arr[5]);
		obj.setPrMatchAmt((BigDecimal) arr[6]);
		
		obj.setPrMissMatPer((BigDecimal) arr[7]);
		obj.setAnxrMisMatAmt((BigDecimal) arr[8]);
		obj.setPrMissMatAmt((BigDecimal) arr[9]);
		
		obj.setPrProbPer((BigDecimal) arr[10]);
		obj.setAnx2ProbMat((BigDecimal) arr[11]);
		obj.setPrProbMat((BigDecimal) arr[12]);
		
		obj.setPrForcMatPer((BigDecimal) arr[13]);
		obj.setAnx2ForcMat((BigDecimal) arr[14]);
		obj.setPrForcMat((BigDecimal) arr[15]);
		
		obj.setAddanx2Per((BigDecimal) arr[16]);
		obj.setAddAnx2((BigDecimal) arr[17]);
		
		obj.setAddPrPer((BigDecimal) arr[18]);
		obj.setAddPr((BigDecimal) arr[19]);
		
		obj.setFillStatus("");
		obj.setLevel("L3");
		return obj;
	}

}
