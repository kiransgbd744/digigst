package com.ey.advisory.app.services.daos.initiaterecon;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.EntityIRDto;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.dto.ReconEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Component("Anx2InitiateReconDaoImpl")
public class Anx2InitiateReconDaoImpl implements Anx2InitiateReconDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<ReconEntity> anx2InitiateRecon(EntityIRDto request) {

		String returnperiod = request.getReturnPeriod();
		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();
		
		String gstn = null;
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstn = key;
					if (dataSecAttrs.get(OnboardingConstant.GSTIN) != null
							&& !dataSecAttrs.get(OnboardingConstant.GSTIN)
									.isEmpty()) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}
			}
			}
		
		StringBuilder prCondition = new StringBuilder();
		if (gstn != null && !gstn.isEmpty() && gstinList != null
				&& !gstinList.isEmpty()) {
			prCondition.append(" AND CUST_GSTIN IN :gstinList");
		}
		if (returnperiod != null && !returnperiod.isEmpty()) {
			prCondition.append(" AND DERIVED_RET_PERIOD = :returnPeriod");
		}
		StringBuilder a2Condition = new StringBuilder();

		if (gstn != null && !gstn.isEmpty() && gstinList != null){
			a2Condition.append(" AND CGSTIN IN :gstinList");
		}

	  if (returnperiod != null && !returnperiod.isEmpty()) {
			a2Condition.append(" AND DERIVED_RET_PERIOD = :returnPeriod");
		}
		String queryStr = createQueryString(prCondition.toString(), 
				a2Condition.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		if (gstn != null && !gstn.isEmpty() && gstinList != null
				&& !gstinList.isEmpty()) {
			q.setParameter("gstinList", gstinList);
		}

		if (returnperiod != null && !returnperiod.isEmpty()) {
			int derivedRetPeriod = GenUtil.convertTaxPeriodToInt(returnperiod);
			q.setParameter("returnPeriod", derivedRetPeriod);
		}

		List<Object[]> list = q.getResultList();
		return list.parallelStream().map(o -> convert(o))
				.collect(Collectors.toCollection(ArrayList::new));
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

	private String createQueryString(String prCondition,String a2Condition) {

		
		return "SELECT * FROM (SELECT count(ID), DOC_TYPE,SUM(TAXABLE_VALUE),"
				+ "SUM(IGST_AMT),SUM(CGST_AMT),SUM(SGST_AMT),"
				+ "SUM(CESS_AMT_SPECIFIC + CESS_AMT_ADVALOREM) AS CESS_AMT,"
				+ "SUM(AVAILABLE_IGST) AS AVAILABLE_IGST,"
				+ "SUM(AVAILABLE_CGST) AS AVAILABLE_CGST,"
				+ "SUM(AVAILABLE_SGST) AS AVAILABLE_SGST,"
				+ "SUM(AVAILABLE_CESS) AS AVAILABLE_CESS "
				+ "FROM ANX_INWARD_DOC_HEADER WHERE IS_PROCESSED = TRUE AND "
				+ " AN_RETURN_TYPE = 'ANX2' AND "
				+ "AN_TAX_DOC_TYPE IN ('B2B','DXP','SEZWP','SEZWOP') "
				+ "AND IS_DELETE = FALSE  " + prCondition
				+ " GROUP BY DOC_TYPE) T1 FULL OUTER JOIN  "
				+ "(SELECT SUM(A2_COUNT), A2_DOC_TYPE, SUM(A2_TAXABLE_VALUE), "
				+ "SUM(A2_IGST_AMT), SUM(A2_CGST_AMT), "
				+ "SUM(A2_SGST_AMT), SUM(A2_CESS_AMT) FROM "
				+ "((SELECT COUNT(ID) AS A2_COUNT,"
				+ "INV_TYPE AS A2_DOC_TYPE, SUM(TAXABLE_VALUE) AS A2_TAXABLE_VALUE, "
				+ "SUM(IGST_AMT) AS A2_IGST_AMT,SUM(CGST_AMT) AS A2_CGST_AMT,"
				+ "SUM(SGST_AMT) AS A2_SGST_AMT,SUM(CESS_AMT) AS A2_CESS_AMT "
				+ "FROM GETANX2_B2B_HEADER  WHERE IS_DELETE = FALSE "
				+ a2Condition + " GROUP BY INV_TYPE) UNION ALL "
				+ "(SELECT COUNT(ID) AS A2_COUNT,INV_TYPE AS A2_DOC_TYPE, "
				+ "SUM(TAXABLE_VALUE) AS A2_TAXABLE_VALUE, "
				+ "SUM(IGST_AMT) AS A2_IGST_AMT,SUM(CGST_AMT) AS A2_CGST_AMT,"
				+ "SUM(SGST_AMT) AS A2_SGST_AMT,SUM(CESS_AMT) AS A2_CESS_AMT "
				+ "FROM GETANX2_DE_HEADER WHERE IS_DELETE = FALSE "
				+ a2Condition + " GROUP BY INV_TYPE) UNION ALL "
				+ "(SELECT COUNT(ID) AS A2_COUNT,INV_TYPE AS A2_DOC_TYPE, "
				+ "SUM(TAXABLE_VALUE) AS A2_TAXABLE_VALUE, "
				+ "SUM(IGST_AMT) AS A2_IGST_AMT,SUM(CGST_AMT) AS A2_CGST_AMT,"
				+ "SUM(SGST_AMT) AS A2_SGST_AMT,SUM(CESS_AMT) AS A2_CESS_AMT "
				+ "FROM GETANX2_SEZWP_HEADER WHERE IS_DELETE = FALSE "
				+ a2Condition + " GROUP BY INV_TYPE) UNION ALL "
				+ "(SELECT COUNT(ID) AS A2_COUNT,INV_TYPE AS A2_DOC_TYPE, "
				+ "SUM(TAXABLE_VALUE) AS A2_TAXABLE_VALUE, "
				+ "'0' AS A2_IGST_AMT,'0'  AS A2_CGST_AMT, '0'  AS A2_SGST_AMT, "
				+ "'0' AS A2_CESS_AMT FROM GETANX2_SEZWOP_HEADER  "
				+ "WHERE IS_DELETE = FALSE " + a2Condition
				+ " GROUP BY INV_TYPE)) "
				+ "GROUP BY A2_DOC_TYPE) T2 ON T1.DOC_TYPE = T2.A2_DOC_TYPE ";
	}

}
