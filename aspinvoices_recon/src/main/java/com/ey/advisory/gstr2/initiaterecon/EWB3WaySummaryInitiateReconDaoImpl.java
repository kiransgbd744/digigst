package com.ey.advisory.gstr2.initiaterecon;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("EWB3WaySummaryInitiateReconDaoImpl")
public class EWB3WaySummaryInitiateReconDaoImpl implements EWB3WaySummaryInitiateReconDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	static final private String Return_Period_Wise = "ReturnPeriodWise";
	
	@Override
	public List<EWB3WaySummaryInitiateReconLineItemDto> ewb3WayInitiateRecon(EWB3WaySummaryInitiateReconDto request) {
		
		Integer toTaxPeriod = null;
		Integer fromTaxPeriod = null;
		
		Date toDocDate = null;
		Date fromDocDate = null;
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String defaultDate = "9999-12-31";
		
		String criteria = request.getCriteria();
		
		if (criteria !=null && !criteria.isEmpty())
		{
			try{
				
			if(Return_Period_Wise.equalsIgnoreCase(criteria))
			{

				toTaxPeriod = request.getToTaxPeriod() != null
						&& request.getToTaxPeriod() != ""
						&& !request.getToTaxPeriod().isEmpty()
								? Integer.parseInt(request.getToTaxPeriod()) : 999999;

				fromTaxPeriod = request.getFromTaxPeriod() != null
						&& request.getFromTaxPeriod() != ""
						&& !request.getFromTaxPeriod().isEmpty()
								? Integer.parseInt(request.getFromTaxPeriod())
								: 999999;		
				toDocDate = formatter.parse(defaultDate);
				fromDocDate = formatter.parse(defaultDate);
			}
			else{
			
				toDocDate = request.getToTaxPeriod() != null
						&& request.getToTaxPeriod() != ""
						&& !request.getToTaxPeriod().isEmpty()
								? formatter.parse(request.getToTaxPeriod())
								: formatter.parse(defaultDate);

				fromDocDate = request.getFromTaxPeriod() != null
						&& request.getFromTaxPeriod() != ""
						&& !request.getFromTaxPeriod().isEmpty()
								? formatter.parse(request.getFromTaxPeriod())
								: formatter.parse(defaultDate);	
				fromTaxPeriod= 999999;
				toTaxPeriod= 999999;
				
			}
			}catch (ParseException e1) {
		e1.printStackTrace();
	
		}	
		}

		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();

		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {

					if (dataSecAttrs.get(OnboardingConstant.GSTIN) != null
							&& !dataSecAttrs.get(OnboardingConstant.GSTIN)
									.isEmpty()) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}
			}
		}

		try {

			StoredProcedureQuery storedProc = null;
			
			LOGGER.info("Invoking USP_3WAY_PRE_RECON_SUMMARY Stored Proc");
				
			storedProc = entityManager
					.createStoredProcedureQuery("USP_3WAY_PRE_RECON_SUMMARY");
			 
			
			storedProc.registerStoredProcedureParameter("P_GSTIN_LIST",
					String.class, ParameterMode.IN);

			storedProc.setParameter("P_GSTIN_LIST",
					String.join(",", gstinList));
			
			storedProc.registerStoredProcedureParameter("P_FROM_TXPRD",
					Integer.class, ParameterMode.IN);
			
			storedProc.setParameter("P_FROM_TXPRD", fromTaxPeriod);

			storedProc.registerStoredProcedureParameter("P_TO_TXPRD",
					Integer.class, ParameterMode.IN);

			storedProc.setParameter("P_TO_TXPRD", toTaxPeriod);

			storedProc.registerStoredProcedureParameter("P_FROM_DATE",
					Date.class, ParameterMode.IN);

			storedProc.setParameter("P_FROM_DATE", fromDocDate);

			storedProc.registerStoredProcedureParameter("P_TO_DATE", Date.class,
					ParameterMode.IN);

			storedProc.setParameter("P_TO_DATE", toDocDate);
			
			@SuppressWarnings("unchecked")
			List<Object[]> list = storedProc.getResultList();

			LOGGER.debug("Coverting object to the DTOList, BEGIN");
			List<EWB3WaySummaryInitiateReconLineItemDto> retList = list.stream().map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));
			LOGGER.debug("Coverting object to the DTOList, END");
			return retList;

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Converting ResultSet into List in EWB3WaySummaryInitiateReconLineItemDto ");
			throw new AppException("Unexpected error in stored procedure "
					+ "execution.", e);
		}
		}
	private EWB3WaySummaryInitiateReconLineItemDto convert(Object[] arr) {
		EWB3WaySummaryInitiateReconLineItemDto reconentity = new EWB3WaySummaryInitiateReconLineItemDto();

		reconentity.setDocTypes((arr[0] != null) ? (String) arr[0] : null);
		reconentity.setEInvCount((arr[1] != null) ? GenUtil.getBigInteger(arr[1]) : BigInteger.ZERO);
		reconentity.setEWBCount((arr[2] != null) ? GenUtil.getBigInteger(arr[2]) : BigInteger.ZERO);
		reconentity.setGSTR1Count((arr[3] != null) ? GenUtil.getBigInteger(arr[3]) : BigInteger.ZERO);
		
		return reconentity;
	}


}
