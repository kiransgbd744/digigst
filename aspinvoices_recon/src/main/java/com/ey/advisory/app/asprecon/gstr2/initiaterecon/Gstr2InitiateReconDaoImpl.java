package com.ey.advisory.app.asprecon.gstr2.initiaterecon;

import java.math.BigDecimal;
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
import com.ey.advisory.core.dto.ReconEntity;

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
@Component("Gstr2InitiateReconDaoImpl")
public class Gstr2InitiateReconDaoImpl implements Gstr2InitiateReconDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<ReconEntity> gstr2InitiateRecon(Gstr2InitiateReconDto request) {

		String reconType = request.getReconType();//2APR or 2BPR
		
		Integer toTaxPeriodPR = request.getToTaxPeriodPR() != null
				&& request.getToTaxPeriodPR() != ""
				&& !request.getToTaxPeriodPR().isEmpty()
						? Integer.parseInt(request.getToTaxPeriodPR()) : 999999;

		Integer fromTaxPeriodPR = request.getFromTaxPeriodPR() != null
				&& request.getFromTaxPeriodPR() != ""
				&& !request.getFromTaxPeriodPR().isEmpty()
						? Integer.parseInt(request.getFromTaxPeriodPR())
						: 999999;
		Integer toTaxPeriod2A = request.getToTaxPeriod2A() != null
				&& request.getToTaxPeriod2A() != ""
				&& !request.getToTaxPeriod2A().isEmpty()
						? Integer.parseInt(request.getToTaxPeriod2A()) : 999999;

		Integer fromTaxPeriod2A = request.getFromTaxPeriod2A() != null
				&& request.getFromTaxPeriod2A() != ""
				&& !request.getFromTaxPeriod2A().isEmpty()
						? Integer.parseInt(request.getFromTaxPeriod2A())
						: 999999;

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String defaultDate = "9999-12-31";

		Date toDocDate = null;
		Date fromDocDate = null;
		try {
			toDocDate = request.getToDocDate() != null
					&& request.getToDocDate() != ""
					&& !request.getToDocDate().isEmpty()
							? formatter.parse(request.getToDocDate())
							: formatter.parse(defaultDate);

			fromDocDate = request.getFromDocDate() != null
					&& request.getFromDocDate() != ""
					&& !request.getFromDocDate().isEmpty()
							? formatter.parse(request.getFromDocDate())
							: formatter.parse(defaultDate);

		} catch (ParseException e1) {
			//e1.printStackTrace();
			LOGGER.error(e1.toString());
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
			
			if (reconType != null && reconType.equalsIgnoreCase("2BPR")) {
			    LOGGER.info("Invoking USP_2BPR_PRE_RECON_SUMMARY Stored Proc");

			    storedProc = entityManager.createStoredProcedureQuery("USP_2BPR_PRE_RECON_SUMMARY");
			    
			    storedProc.registerStoredProcedureParameter("P_GSTIN_LIST", String.class, ParameterMode.IN); // P_GSTIN_LIST
				storedProc.registerStoredProcedureParameter("P_FROM_TXPRD", Integer.class, ParameterMode.IN); // P_FROM_TXPRD
				storedProc.registerStoredProcedureParameter("P_TO_TXPRD", Integer.class, ParameterMode.IN); // P_TO_TXPRD
				storedProc.registerStoredProcedureParameter("P_FROM_DATE", Date.class, ParameterMode.IN); // P_FROM_DATE
				storedProc.registerStoredProcedureParameter("P_TO_DATE", Date.class, ParameterMode.IN); // P_TO_DATE
			    storedProc.registerStoredProcedureParameter("P_B2_FROM_TXPRD", Integer.class, ParameterMode.IN); // P_B2_FROM_TXPRD
			    storedProc.registerStoredProcedureParameter("P_B2_TO_TXPRD", Integer.class, ParameterMode.IN); // P_B2_TO_TXPRD

			    // Index-based parameter registration
				storedProc.setParameter("P_GSTIN_LIST", String.join(",", gstinList));
				storedProc.setParameter("P_FROM_TXPRD", fromTaxPeriodPR);
				storedProc.setParameter("P_TO_TXPRD", toTaxPeriodPR);
				storedProc.setParameter("P_FROM_DATE", fromDocDate);
				storedProc.setParameter("P_TO_DATE", toDocDate);
			
			    // Index-based parameter registration
			
			    storedProc.setParameter("P_B2_FROM_TXPRD", fromTaxPeriod2A);
			    storedProc.setParameter("P_B2_TO_TXPRD", toTaxPeriod2A);
			} else if (reconType != null && reconType.equalsIgnoreCase("EINVPR")) {
			    LOGGER.info("Invoking USP_EINVPR_PRE_RECON_SUMMARY Stored Proc");

			    storedProc = entityManager.createStoredProcedureQuery("USP_EINVPR_PRE_RECON_SUMMARY");
			    
			    storedProc.registerStoredProcedureParameter("P_GSTIN_LIST", String.class, ParameterMode.IN); // P_GSTIN_LIST
							storedProc.registerStoredProcedureParameter("P_FROM_TXPRD", Integer.class, ParameterMode.IN); // P_FROM_TXPRD
							storedProc.registerStoredProcedureParameter("P_TO_TXPRD", Integer.class, ParameterMode.IN); // P_TO_TXPRD
							storedProc.registerStoredProcedureParameter("P_FROM_DATE", Date.class, ParameterMode.IN); // P_FROM_DATE
							storedProc.registerStoredProcedureParameter("P_TO_DATE", Date.class, ParameterMode.IN); // P_TO_DATE
						    storedProc.registerStoredProcedureParameter("P_B2_FROM_TXPRD", Integer.class, ParameterMode.IN); // P_B2_FROM_TXPRD
						    storedProc.registerStoredProcedureParameter("P_B2_TO_TXPRD", Integer.class, ParameterMode.IN); // P_B2_TO_TXPRD

						    // Index-based parameter registration
							storedProc.setParameter("P_GSTIN_LIST", String.join(",", gstinList));
							storedProc.setParameter("P_FROM_TXPRD", fromTaxPeriodPR);
							storedProc.setParameter("P_TO_TXPRD", toTaxPeriodPR);
							storedProc.setParameter("P_FROM_DATE", fromDocDate);
							storedProc.setParameter("P_TO_DATE", toDocDate);
			
			    // Index-based parameter registration

			    storedProc.setParameter("P_B2_FROM_TXPRD", fromTaxPeriod2A);
			    storedProc.setParameter("P_B2_TO_TXPRD", toTaxPeriod2A);
			} else {
			    LOGGER.info("Invoking USP_PRE_RECON_SUMMARY Stored Proc");

			    storedProc = entityManager.createStoredProcedureQuery("USP_PRE_RECON_SUMMARY");
			    
				storedProc.registerStoredProcedureParameter("P_GSTIN_LIST", String.class, ParameterMode.IN); // P_GSTIN_LIST
				storedProc.registerStoredProcedureParameter("P_FROM_TXPRD", Integer.class, ParameterMode.IN); // P_FROM_TXPRD
				storedProc.registerStoredProcedureParameter("P_TO_TXPRD", Integer.class, ParameterMode.IN); // P_TO_TXPRD
				storedProc.registerStoredProcedureParameter("P_FROM_DATE", Date.class, ParameterMode.IN); // P_FROM_DATE
				storedProc.registerStoredProcedureParameter("P_TO_DATE", Date.class, ParameterMode.IN); // P_TO_DATE
			    storedProc.registerStoredProcedureParameter("P_A2_FROM_TXPRD", Integer.class, ParameterMode.IN); // P_A2_FROM_TXPRD
			    storedProc.registerStoredProcedureParameter("P_A2_TO_TXPRD", Integer.class, ParameterMode.IN); // P_A2_TO_TXPRD

			    // Index-based parameter registration
				storedProc.setParameter("P_GSTIN_LIST", String.join(",", gstinList));
				storedProc.setParameter("P_FROM_TXPRD", fromTaxPeriodPR);
				storedProc.setParameter("P_TO_TXPRD", toTaxPeriodPR);
				storedProc.setParameter("P_FROM_DATE", fromDocDate);
				storedProc.setParameter("P_TO_DATE", toDocDate);
				

			    storedProc.setParameter("P_A2_FROM_TXPRD", fromTaxPeriod2A);
			    storedProc.setParameter("P_A2_TO_TXPRD", toTaxPeriod2A);
			}			

			@SuppressWarnings("unchecked")
			List<Object[]> list = storedProc.getResultList();

			LOGGER.debug("Coverting object to the DTOList, BEGIN");
			List<ReconEntity> retList = list.stream().map(o -> convert(o))
					.collect(Collectors.toCollection(ArrayList::new));
			LOGGER.debug("Coverting object to the DTOList, END");
			return retList;

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Converting ResultSet into List in ReconEntity ");
			throw new AppException("Unexpected error in stored procedure "
					+ "execution.", e);
		}
	}

	private ReconEntity convert(Object[] arr) {
		ReconEntity reconentity = new ReconEntity();

		if (arr[0] == null || arr[0].toString().isEmpty()) {
			BigInteger prid = BigInteger.ZERO;
			arr[0] = prid;
		}
		if (arr[2] == null || arr[2].toString().isEmpty()) {
			BigDecimal prtaxableval = BigDecimal.ZERO;
			arr[2] = prtaxableval;
		}
		if (arr[3] == null || arr[3].toString().isEmpty()) {
			BigDecimal prigst = BigDecimal.ZERO;
			arr[3] = prigst;
		}
		if (arr[4] == null || arr[4].toString().isEmpty()) {
			BigDecimal prcgst = BigDecimal.ZERO;
			arr[4] = prcgst;
		}
		if (arr[5] == null || arr[5].toString().isEmpty()) {
			BigDecimal prsgst = BigDecimal.ZERO;
			arr[5] = prsgst;
		}
		if (arr[6] == null || arr[6].toString().isEmpty()) {
			BigDecimal prcess = BigDecimal.ZERO;
			arr[6] = prcess;
		}
		if (arr[7] == null || arr[7].toString().isEmpty()) {
			BigDecimal avIgst = BigDecimal.ZERO;
			arr[7] = avIgst;
		}
		if (arr[8] == null || arr[8].toString().isEmpty()) {
			BigDecimal avCgst = BigDecimal.ZERO;
			arr[8] = avCgst;
		}
		if (arr[9] == null || arr[9].toString().isEmpty()) {
			BigDecimal avSgst = BigDecimal.ZERO;
			arr[9] = avSgst;
		}
		if (arr[10] == null || arr[10].toString().isEmpty()) {
			BigDecimal avCess = BigDecimal.ZERO;
			arr[10] = avCess;
		}
		if (arr[11] == null || arr[11].toString().isEmpty()) {
			BigInteger a2id = BigInteger.ZERO;
			arr[11] = a2id;
		}
		if (arr[13] == null || arr[13].toString().isEmpty()) {
			BigDecimal a2taxableval = BigDecimal.ZERO;
			arr[13] = a2taxableval;
		}
		if (arr[14] == null || arr[14].toString().isEmpty()) {
			BigDecimal a2igst = BigDecimal.ZERO;
			arr[14] = a2igst;
		}
		if (arr[15] == null || arr[15].toString().isEmpty()) {
			BigDecimal a2cgst = BigDecimal.ZERO;
			arr[15] = a2cgst;
		}
		if (arr[16] == null || arr[16].toString().isEmpty()) {
			BigDecimal a2sgst = BigDecimal.ZERO;
			arr[16] = a2sgst;
		}
		if (arr[17] == null || arr[17].toString().isEmpty()) {
			BigDecimal a2cess = BigDecimal.ZERO;
			arr[17] = a2cess;
		}

		BigInteger bb1 = GenUtil.getBigInteger(arr[0]);
		reconentity.setPrid(bb1.intValue());
		reconentity.setPrInvType((String) arr[1]);
		BigDecimal prtaxable = BigDecimal
				.valueOf(Double.valueOf(arr[2].toString()));
		reconentity.setPrTaxableValue(prtaxable);
		BigDecimal prigst = BigDecimal
				.valueOf(Double.valueOf(arr[3].toString()));
		reconentity.setPrIgst(prigst);
		BigDecimal prcgst = BigDecimal
				.valueOf(Double.valueOf(arr[4].toString()));
		reconentity.setPrCgst(prcgst);
		BigDecimal prsgst = BigDecimal
				.valueOf(Double.valueOf(arr[5].toString()));
		reconentity.setPrSgst(prsgst);
		BigDecimal prcess = BigDecimal
				.valueOf(Double.valueOf(arr[6].toString()));
		reconentity.setPrCess(prcess);
		BigDecimal avigst = BigDecimal
				.valueOf(Double.valueOf(arr[7].toString()));
		reconentity.setAvilableIgst(avigst);
		BigDecimal avcgst = BigDecimal
				.valueOf(Double.valueOf(arr[8].toString()));
		reconentity.setAvilableCgst(avcgst);
		BigDecimal avsgst = BigDecimal
				.valueOf(Double.valueOf(arr[9].toString()));
		reconentity.setAvilableSgst(avsgst);
		BigDecimal avcess = BigDecimal
				.valueOf(Double.valueOf(arr[10].toString()));
		reconentity.setAvilableCess(avcess);
		BigInteger bb = GenUtil.getBigInteger(arr[11]);
		reconentity.setAid2(bb.intValue());
		reconentity.setA2InvType((String) arr[12]);
		BigDecimal a2taxable = BigDecimal
				.valueOf(Double.valueOf(arr[13].toString()));
		reconentity.setA2TaxableValue(a2taxable);
		BigDecimal a2igst = BigDecimal
				.valueOf(Double.valueOf(arr[14].toString()));
		reconentity.setA2Igst(a2igst);
		BigDecimal a2cgst = BigDecimal
				.valueOf(Double.valueOf(arr[15].toString()));
		reconentity.setA2Cgst(a2cgst);
		BigDecimal a2sgst = BigDecimal
				.valueOf(Double.valueOf(arr[16].toString()));
		reconentity.setA2Sgst(a2sgst);
		BigDecimal a2cess = BigDecimal
				.valueOf(Double.valueOf(arr[17].toString()));
		reconentity.setA2Cess(a2cess);

		return reconentity;
	}

}
