package com.ey.advisory.app.dashboard.homeOld;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.LandingDashboardBatchRefreshEntity;
import com.ey.advisory.admin.data.entities.client.LandingDashboardComplianceStatusentity;
import com.ey.advisory.admin.data.entities.client.LandingDashboardGstr1VsGstr3BEntity;
import com.ey.advisory.admin.data.repositories.client.LandingDashboardComplianceRepository;
import com.ey.advisory.admin.data.repositories.client.LandingDashboardOutwardSupplyRepository;
import com.ey.advisory.admin.data.repositories.client.LandingDashboardRefreshRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author mohit.basak
 *
 */

@Slf4j
@Component("DashboardHODaoImpl")
public class DashboardHODaoImpl implements DashboardHODao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	LandingDashboardRefreshRepository ldRepo;

	@Autowired
	LandingDashboardComplianceRepository complRepo;

	@Autowired
	GstrReturnStatusRepository gstrRetRepo;

	@Autowired
	LandingDashboardOutwardSupplyRepository outSupRepo;

	@Override
	public List<DashboardHOReturnStatusDto> getDashBoardReturnStatus(
			Long entityId, String taxPeriod) {
		try {

			String itc04TaxPeriod = GenUtil.getITC04TaxPeriod(taxPeriod);
			
			String queryString = createDashBoardReturnStatus(entityId,
					taxPeriod);
			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("entityId", entityId);
			q.setParameter("taxPeriod", taxPeriod);
			
			String queryString1 = createDashBoardReturnITC04Status(entityId,
					itc04TaxPeriod);
			Query q1 = entityManager.createNativeQuery(queryString1);
			q1.setParameter("entityId", entityId);
			q1.setParameter("itc04TaxPeriod", itc04TaxPeriod);
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Executing query %s, entity Id %d," + " taxPeriod %s",
						queryString, entityId, taxPeriod);
				LOGGER.debug(msg);
			}
			@SuppressWarnings("unchecked")
			List<Object[]> list = q.getResultList();
			
			@SuppressWarnings("unchecked")
			List<Object[]> list1 = q1.getResultList();
			
			list.addAll(list1);
			
			List<DashboardHOReturnStatusDto> retList = list.stream()
					.map(o -> convertToDto(o))
					.collect(Collectors.toCollection(ArrayList::new));
			// System.out.println(retList);
			return retList;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException(
					"Exception Occured in " + "DashboardReconDetailsDaoImpl");
		}
	}

	private DashboardHOReturnStatusDto convertToDto(Object[] dbObj) {
		DashboardHOReturnStatusDto outDto = new DashboardHOReturnStatusDto();

		outDto.setTaxType((String) dbObj[0]);
		outDto.setTotalCount(GenUtil.getBigInteger(dbObj[1]));
		outDto.setAvailableCount(GenUtil.getBigInteger(dbObj[2]));
		outDto.setTotalPercentage((BigDecimal) dbObj[3]);

		return outDto;
	}
	
	private String createDashBoardReturnITC04Status(Long entityId,
			String itc04TaxPeriod) {
		String query = "" + "SELECT  A.RETURN_TYPE "
				+ "      , IFNULL(TOTAL_CNT,0) AS TOTAL_CNT "
				+ "      , IFNULL(FILED_CNT,0) AS FILED_CNT "
				+ "      , (IFNULL(FILED_CNT,0)*100)/IFNULL(TOTAL_CNT,0) AS PERCENTAGE "
				+ "FROM ( "
				+ "             SELECT CASE WHEN REG_TYPE IN ('REGULAR','SEZ')  THEN 'ITC04' "
				+ "                                 ELSE REG_TYPE "
				+ "                       END AS \"RETURN_TYPE\", "
				+ "                       COUNT(GSTIN) AS TOTAL_CNT "
				+ "             FROM  GSTIN_INFO "
				+ "             WHERE ENTITY_ID= :entityId "
				+ "             AND   IS_DELETE=FALSE "
				+ "             AND   REG_TYPE IN ('REGULAR','SEZ') "
				+ "             GROUP BY CASE WHEN REG_TYPE IN ('REGULAR','SEZ')  THEN 'ITC04' "
				+ "                           ELSE REG_TYPE "
				+ "                      END " + "                  )A "
				+ "LEFT OUTER JOIN "
				+ "(         SELECT RS.RETURN_TYPE,COUNT(RS.GSTIN) AS FILED_CNT "
				+ "          FROM   GSTR_RETURN_STATUS RS "
				+ "          INNER JOIN   GSTIN_INFO GI ON RS.GSTIN=GI.GSTIN           AND GI.IS_DELETE=FALSE "
				+ "                                   AND GI.ENTITY_ID= :entityId   AND RS.TAXPERIOD= :itc04TaxPeriod "
				+ "          WHERE UPPER(RS.STATUS)='FILED' AND RS.IS_COUNTER_PARTY_GSTIN = false "
				+ "          GROUP BY RETURN_TYPE "
				+ ")B ON A.RETURN_TYPE=B.RETURN_TYPE;";

		return query;
	}


	private String createDashBoardReturnStatus(Long entityId,
			String taxPeriod) {
		String query = "" + "SELECT  A.RETURN_TYPE "
				+ "      , IFNULL(TOTAL_CNT,0) AS TOTAL_CNT "
				+ "      , IFNULL(FILED_CNT,0) AS FILED_CNT "
				+ "      , (IFNULL(FILED_CNT,0)*100)/IFNULL(TOTAL_CNT,0) AS PERCENTAGE "
				+ "FROM ( "
				+ "             SELECT CASE WHEN REG_TYPE IN ('REGULAR','SEZ','SEZD','SEZU')  THEN 'GSTR1' "
				+ "                         WHEN REG_TYPE = 'ISD'               THEN 'GSTR6' "
				+ "                         WHEN REG_TYPE = 'TDS'               THEN 'GSTR7' "
				+ "                         WHEN REG_TYPE = 'TCS'               THEN 'GSTR8' "
				+ "                         ELSE REG_TYPE "
				+ "                     END AS \"RETURN_TYPE\" "
				+ "                    ,COUNT(GSTIN) AS TOTAL_CNT "
				+ "             FROM  GSTIN_INFO "
				+ "             WHERE ENTITY_ID= :entityId "
				+ "             AND IS_DELETE=FALSE "
				+ "             GROUP BY CASE WHEN REG_TYPE IN ('REGULAR','SEZ','SEZD','SEZU') THEN 'GSTR1' "
				+ "                           WHEN REG_TYPE = 'ISD'              THEN 'GSTR6' "
				+ "                           WHEN REG_TYPE = 'TDS'              THEN 'GSTR7' "
				+ "                           WHEN REG_TYPE = 'TCS'              THEN 'GSTR8' "
				+ "                           ELSE REG_TYPE "
				+ "                       END " + "                          "
				+ "             UNION ALL " + "              "
				+ "             SELECT CASE WHEN REG_TYPE IN ('REGULAR','SEZ','SEZD','SEZU')  THEN 'GSTR3B' "
				+ "                                 ELSE REG_TYPE "
				+ "                       END AS \"RETURN_TYPE\", "
				+ "                       COUNT(GSTIN) AS TOTAL_CNT "
				+ "             FROM  GSTIN_INFO "
				+ "             WHERE ENTITY_ID= :entityId "
				+ "             AND   IS_DELETE=FALSE "
				+ "             AND   REG_TYPE IN ('REGULAR','SEZ','SEZD','SEZU') "
				+ "             GROUP BY CASE WHEN REG_TYPE IN ('REGULAR','SEZ','SEZD','SEZU')  THEN 'GSTR3B' "
				+ "                           ELSE REG_TYPE "
				+ "                      END " + "              "
				+ "             UNION ALL " + "              "
				+ "             SELECT CASE WHEN REG_TYPE IN ('REGULAR','SEZ','SEZD','SEZU')  THEN 'GSTR9' "
				+ "                                 ELSE REG_TYPE "
				+ "                       END AS \"RETURN_TYPE\", "
				+ "                       COUNT(GSTIN) AS TOTAL_CNT "
				+ "             FROM  GSTIN_INFO "
				+ "             WHERE ENTITY_ID= :entityId "
				+ "             AND   IS_DELETE=FALSE "
				+ "             AND   REG_TYPE IN ('REGULAR','SEZ','SEZD','SEZU') "
				+ "             GROUP BY CASE WHEN REG_TYPE IN ('REGULAR','SEZ','SEZD','SEZU')  THEN 'GSTR9' "
				+ "                           ELSE REG_TYPE "
				+ "                      END " + "              "
				+ "             UNION ALL " + "              "
				+ "             SELECT CASE WHEN REG_TYPE IN ('REGULAR','SEZ','SEZD','SEZU')  THEN 'GSTR1A' "
				+ "                                 ELSE REG_TYPE "
				+ "                       END AS \"RETURN_TYPE\", "
				+ "                       COUNT(GSTIN) AS TOTAL_CNT "
				+ "             FROM  GSTIN_INFO "
				+ "             WHERE ENTITY_ID= :entityId "
				+ "             AND   IS_DELETE=FALSE "
				+ "             AND   REG_TYPE IN ('REGULAR','SEZ','SEZD','SEZU') "
				+ "             GROUP BY CASE WHEN REG_TYPE IN ('REGULAR','SEZ','SEZD','SEZU')  THEN 'GSTR1A' "
				+ "                           ELSE REG_TYPE "
				+ "                      END " + "              "
				+ "             )A " + "LEFT OUTER JOIN "
				+ "(         SELECT RS.RETURN_TYPE,COUNT(RS.GSTIN) AS FILED_CNT "
				+ "          FROM   GSTR_RETURN_STATUS RS "
				+ "          INNER JOIN   GSTIN_INFO GI ON RS.GSTIN=GI.GSTIN           AND GI.IS_DELETE=FALSE "
				+ "                                   AND GI.ENTITY_ID= :entityId   AND RS.TAXPERIOD=:taxPeriod "
				+ "          WHERE UPPER(RS.STATUS)='FILED' AND RS.IS_COUNTER_PARTY_GSTIN = false "
				+ "          GROUP BY RETURN_TYPE "
				+ ")B ON A.RETURN_TYPE=B.RETURN_TYPE;";

		return query;
	}

	@Override
	public List<DashboardHOReturnComplianceStatusDto> getDashBoardReturnComplianceStatus(
			Long entityId, String taxPeriod, List<String> listofgstins) {
		try {

			// applying data security

			/*
			 * Map<String, List<String>> dataSecurityAttrMap = new HashMap<>();
			 * Map<String, String> outwardSecurityAttributeMap =
			 * DataSecurityAttributeUtil .getOutwardSecurityAttributeMap();
			 * dataSecurityAttrMap = DataSecurityAttributeUtil
			 * .dataSecurityAttrMapForQuery(Arrays.asList(entityId),
			 * outwardSecurityAttributeMap); List<String> listofgstins =
			 * dataSecurityAttrMap .get(OnboardingConstant.GSTIN);
			 */
			if (CollectionUtils.isEmpty(listofgstins))
				throw new AppException("User Does not have any gstin");

			String derivedTaxPeriod = taxPeriod.substring(2)
					.concat(taxPeriod.substring(0, 2));

			LandingDashboardBatchRefreshEntity batchId = ldRepo
					.getCompletedLatestBatchId(derivedTaxPeriod, entityId);
			List<LandingDashboardComplianceStatusentity> complianceEntity = new ArrayList<>();

			if (batchId != null) {
				complianceEntity = complRepo
						.getBatchIdData(batchId.getBatchId());
			} else {
				complianceEntity = complRepo.getentityIdtaxPeriodData(entityId,
						derivedTaxPeriod);
			}

			List<Object[]> retStatus = gstrRetRepo
					.getReturnTypebygstinstaxPeriod(listofgstins, taxPeriod);

			Map<String, List<String>> map = retStatus.stream()
					.collect(Collectors.groupingBy(s -> String.valueOf(s[1]),
							Collectors.mapping(s -> String.valueOf(s[0]),
									Collectors.toList())));

			List<DashboardHOReturnComplianceStatusDto> retList = complianceEntity
					.stream()
					.map(o -> convertToReturnComplianceStatusDto(o, map))
					.collect(Collectors.toCollection(ArrayList::new));

			return retList;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "DashboardReconDetailsDaoImpl" + ex);
		}
	}

	private DashboardHOReturnComplianceStatusDto convertToReturnComplianceStatusDto(
			LandingDashboardComplianceStatusentity entity,
			Map<String, List<String>> retStatMap) {
		DashboardHOReturnComplianceStatusDto outDto = new DashboardHOReturnComplianceStatusDto();
		List<String> retTypeList = retStatMap.get(entity.getGstin());

		outDto.setSupplierGstin(entity.getGstin());
		outDto.setRegType(entity.getRegType());
		outDto.setGstinState(entity.getStateName());

		outDto.setOutwardSupply(entity.getOutwardSupply());
		outDto.setTotalTax(entity.getTotalTax());
		outDto.setIGST(entity.getIgst());
		outDto.setCGST(entity.getCgst());
		outDto.setSGST(entity.getSgst());
		outDto.setCESS(entity.getCess());
		outDto.setCashLedger(entity.getCashLedger());
		outDto.setCreditLedger(entity.getCreditLedger());
		outDto.setLiabilityLedger(entity.getLiabLdger());
		if (retTypeList != null) {
			if (entity.getRegType().equalsIgnoreCase("REGULAR")) {
				outDto.setGstr1Flag(
						retTypeList.contains("GSTR1") ? true : false);
				outDto.setGstr1AFlag(
						retTypeList.contains("GSTR1A") ? true : false);
				outDto.setGstr3BFlag(
						retTypeList.contains("GSTR3B") ? true : false);
			} else if (entity.getRegType().equalsIgnoreCase("ISD")) {
				outDto.setGstr6Flag(
						retTypeList.contains("GSTR6") ? true : false);
			} else if (entity.getRegType().equalsIgnoreCase("TDS")) {
				outDto.setGstr7Flag(
						retTypeList.contains("GSTR7") ? true : false);
			}
		} else {
			if (entity.getRegType().equalsIgnoreCase("REGULAR")
					|| entity.getRegType().equalsIgnoreCase("SEZD")
					|| entity.getRegType().equalsIgnoreCase("SEZU")) {
				outDto.setGstr1Flag(false);
				outDto.setGstr1AFlag(false);
				outDto.setGstr3BFlag(false);
			} else if (entity.getRegType().equalsIgnoreCase("ISD")) {
				outDto.setGstr6Flag(false);
			} else if (entity.getRegType().equalsIgnoreCase("TDS")) {
				outDto.setGstr7Flag(false);
			}

		}
		return outDto;

	}

	private DashboardHOOutwardSupplyDto convertToOutwardSupplyStatusDto(
			LandingDashboardGstr1VsGstr3BEntity dbObj) {
		DashboardHOOutwardSupplyDto outDto = new DashboardHOOutwardSupplyDto();

		outDto.setGSTR1TaxableValue(dbObj.getG1TaxValue() != null
				? dbObj.getG1TaxValue() : BigDecimal.ZERO);
		outDto.setGSTR1ToatalTax(dbObj.getG1totalTax() != null
				? dbObj.getG1totalTax() : BigDecimal.ZERO);
		outDto.setGSTR3BTaxableValue(dbObj.getG3TaxableValue() != null
				? dbObj.getG3TaxableValue() : BigDecimal.ZERO);

		outDto.setGSTR3BTotalTax(dbObj.getG3TotalTax() != null
				? dbObj.getG3TotalTax() : BigDecimal.ZERO);
		outDto.setDifferenceInTaxableValue(dbObj.getDiffTaxVal() != null
				? dbObj.getDiffTaxVal() : BigDecimal.ZERO);
		outDto.setDifferenceInTotalTax(dbObj.getDiffTotalTax() != null
				? dbObj.getDiffTotalTax() : BigDecimal.ZERO);

		return outDto;

	}

	private String createDashBoardReturnComplianceStatus(List<String> GSTIN,
			String taxPeriod) {

		String query = " SELECT   GT.GSTIN        , GT.REG_TYPE  "
				+ " , MS.STATE_NAME  "
				+ " , IFNULL(B.SUPPLIES,0) AS OUTWARD_SUPPLY  "
				+ " , IFNULL(B.TOTAL_TAX,0) AS TOTAL_TAX  "
				+ " , IFNULL(B.IGST,0) AS IGST  "
				+ " , IFNULL(B.CGST,0) AS CGST  "
				+ " , IFNULL(B.SGST,0) AS SGST  "
				+ " , IFNULL(B.CESS,0) AS CESS ,      "
				+ " IFNULL(CASH_LEDGER,0) AS CASH_LEDGER,IFNULL(CREDIT_LEDGER,0) AS CREDIT_LEDGER,IFNULL(LIABILITY_LEDGER,0) AS LIABILITY_LEDGER,GRS.STATUS  "
				+ " FROM   GSTIN_INFO GT  "
				+ " INNER JOIN  MASTER_STATE MS                     ON MS.STATE_CODE = GT.STATE_CODE AND GT.IS_DELETE=FALSE  "
				+ " LEFT JOIN  GSTR_RETURN_STATUS GRS ON GT.GSTIN = GRS.GSTIN                   AND GRS.RETURN_TYPE = 'GSTR1' AND GRS.TaxPeriod=:taxPeriod  "
				+ " LEFT OUTER JOIN  "
				+ " (              SELECT SUPPLIER_GSTIN  "
				+ " , SUM(TAXABLE_VALUE) AS SUPPLIES  "
				+ " , SUM(TOTAL_TAX) AS TOTAL_TAX "
				+ " , SUM(IGST_AMT) AS IGST  " + " , SUM(CGST_AMT) AS CGST  "
				+ " , SUM(SGST_AMT) AS SGST  " + " , SUM(CESS) AS CESS  "
				+ " FROM ( " + "  " + " SELECT SUPPLIER_GSTIN, "
				+ " SUM(IGST_AMT) IGST_AMT, " + " SUM(CGST_AMT) CGST_AMT, "
				+ " SUM(SGST_AMT) SGST_AMT, " + " SUM(CESS) CESS, "
				+ " SUM(TAX_PAYABLE) TOTAL_TAX, "
				+ " SUM(TAXABLE_VALUE) TAXABLE_VALUE " + "  " + "  " + " FROM "
				+ " (SELECT SUPPLIER_GSTIN, "
				+ " SUM(RECORD_COUNT) RECORD_COUNT, " + " SUM(DOC_AMT)DOC_AMT, "
				+ " SUM(IGST_AMT) IGST_AMT, " + " SUM(CGST_AMT) CGST_AMT, "
				+ " SUM(SGST_AMT) SGST_AMT, "
				+ " SUM(TAXABLE_VALUE) TAXABLE_VALUE, "
				+ " SUM(TAX_PAYABLE) TAX_PAYABLE, " + " SUM(CESS) CESS "
				+ " FROM " + " (SELECT SUPPLIER_GSTIN , "
				+ " COUNT(DISTINCT ID) AS RECORD_COUNT, "
				+ " IFNULL(SUM(DOC_AMT), 0) AS DOC_AMT, "
				+ " IFNULL(SUM(IGST_AMT), 0) AS IGST_AMT, "
				+ " IFNULL(SUM(CGST_AMT), 0) AS CGST_AMT, "
				+ " IFNULL(SUM(SGST_AMT), 0) AS SGST_AMT, "
				+ " IFNULL(SUM(TAXABLE_VALUE), 0) AS TAXABLE_VALUE, "
				+ " SUM(IFNULL(IGST_AMT, 0)+IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT_SPECIFIC, 0)+ IFNULL(CESS_AMT_ADVALOREM, 0)) AS TAX_PAYABLE, "
				+ " SUM(IFNULL(CESS_AMT_SPECIFIC, 0)+ IFNULL(CESS_AMT_ADVALOREM, 0)) AS CESS "
				+ " FROM ANX_OUTWARD_DOC_HEADER "
				+ " WHERE ASP_INVOICE_STATUS = 2 "
				+ " AND COMPLIANCE_APPLICABLE = TRUE "
				+ " AND IS_DELETE = FALSE " + " AND SUPPLY_TYPE <> 'CAN' "
				+ " AND RETURN_TYPE='GSTR1' " + " AND TAX_DOC_TYPE IN ('B2B', "
				+ " 'B2BA', " + " 'B2CL', " + " 'B2CLA', " + " 'EXPORTS', "
				+ " 'EXPORTS-A') " + " AND SUPPLIER_GSTIN IN (:GSTIN) "
				+ " AND DERIVED_RET_PERIOD = :taxPeriod "
				+ " GROUP BY SUPPLIER_GSTIN) " + " GROUP BY SUPPLIER_GSTIN "
				+ "  " + " UNION ALL  " + " SELECT SUPPLIER_GSTIN, "
				+ " COUNT(DISTINCT KEY) RECORD_COUNT, "
				+ " SUM(DOC_AMT) DOC_AMT, " + " SUM(IGST_AMT) IGST_AMT, "
				+ " SUM(CGST_AMT)CGST_AMT, " + " SUM(SGST_AMT)SGST_AMT, "
				+ " SUM(TAXABLE_VALUE)TAXABLE_VALUE, "
				+ " SUM(TAX_PAYABLE) TAX_PAYABLE, " + " SUM(CESS) CESS "
				+ " FROM "
				+ " (SELECT SUPPLIER_GSTIN,HDR.TAX_DOC_TYPE AS TAX_DOC_TYPE, (IFNULL(SUPPLIER_GSTIN, '') ||'|'|| IFNULL(HDR.RETURN_PERIOD, '') "
				+ " ||'|'||IFNULL(HDR.DIFF_PERCENT, '') ||'|'||IFNULL(HDR.POS, 9999)||'|'||IFNULL(HDR.ECOM_GSTIN, '') ||'|'||IFNULL(TAX_RATE, 9999)) KEY, "
				+ " 																																	  IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('INV') THEN ONB_LINE_ITEM_AMT "
				+ " END), 0)+ IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('DR') THEN ONB_LINE_ITEM_AMT "
				+ " END), 0)- IFNULL(SUM(CASE "
				+ " 			WHEN DOC_TYPE IN ('CR') THEN ONB_LINE_ITEM_AMT "
				+ " 		END), 0) AS DOC_AMT, " + " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('INV') THEN ITM.IGST_AMT "
				+ " END), 0)+ IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('DR') THEN ITM.IGST_AMT "
				+ " END), 0)- IFNULL(SUM(CASE "
				+ " 			WHEN DOC_TYPE IN ('CR') THEN ITM.IGST_AMT "
				+ " 		END), 0) AS IGST_AMT, " + " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('INV') THEN ITM.CGST_AMT "
				+ " END), 0)+ IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('DR') THEN ITM.CGST_AMT "
				+ " END), 0)- IFNULL(SUM(CASE "
				+ " 			WHEN DOC_TYPE IN ('CR') THEN ITM.CGST_AMT "
				+ " 		END), 0) AS CGST_AMT, " + " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('INV') THEN ITM.SGST_AMT "
				+ " END), 0)+ IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('DR') THEN ITM.SGST_AMT "
				+ " END), 0)- IFNULL(SUM(CASE "
				+ " 			WHEN DOC_TYPE IN ('CR') THEN ITM.SGST_AMT "
				+ " 		END), 0) AS SGST_AMT, " + " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('INV') THEN ITM.TAXABLE_VALUE "
				+ " END), 0)+ IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('DR') THEN ITM.TAXABLE_VALUE "
				+ " END), 0)- IFNULL(SUM(CASE "
				+ " 			WHEN DOC_TYPE IN ('CR') THEN ITM.TAXABLE_VALUE "
				+ " 		END), 0) AS TAXABLE_VALUE, " + " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('INV') THEN IFNULL(ITM.IGST_AMT, 0)+IFNULL(ITM.CGST_AMT, 0)+IFNULL(ITM.SGST_AMT, 0)+ IFNULL(ITM.CESS_AMT_SPECIFIC, "
				+ " 0)+IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
				+ " END), 0)+ IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('DR') THEN IFNULL(ITM.IGST_AMT, 0)+IFNULL(ITM.CGST_AMT, 0)+IFNULL(ITM.SGST_AMT, 0)+ IFNULL(ITM.CESS_AMT_SPECIFIC, "
				+ " 0)+IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
				+ " END), 0)- IFNULL(SUM(CASE "
				+ " 			WHEN DOC_TYPE IN ('CR') THEN IFNULL(ITM.IGST_AMT, 0)+IFNULL(ITM.CGST_AMT, 0)+IFNULL(ITM.SGST_AMT, 0)+ IFNULL(ITM.CESS_AMT_SPECIFIC, "
				+ " 0)+IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
				+ " 		END), 0) AS TAX_PAYABLE, " + " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('INV') THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0)+IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
				+ " END), 0)+ IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('DR') THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0)+IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
				+ " END), 0)- IFNULL(SUM(CASE "
				+ " 			WHEN DOC_TYPE IN ('CR') THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0)+IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
				+ " 		END), 0) AS CESS "
				+ " FROM ANX_OUTWARD_DOC_HEADER HDR "
				+ " INNER JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID=ITM.DOC_HEADER_ID "
				+ " WHERE ASP_INVOICE_STATUS = 2  "
				+ " AND RETURN_TYPE='GSTR1' " + " AND IS_DELETE = FALSE "
				+ " AND HDR.SUPPLY_TYPE <> 'CAN' "
				+ " AND HDR.TAX_DOC_TYPE IN ('B2CS') "
				+ " AND SUPPLIER_GSTIN IN (:GSTIN) "
				+ " AND HDR.DERIVED_RET_PERIOD  = :taxPeriod "
				+ " GROUP BY SUPPLIER_GSTIN, " + " HDR.TAX_DOC_TYPE, "
				+ " SUPPLIER_GSTIN, " + " HDR.RETURN_PERIOD, " + " HDR.POS, "
				+ " HDR.ECOM_GSTIN, " + " HDR.DIFF_PERCENT, " + " ITM.TAX_RATE "
				+ " UNION ALL  "
				+ " SELECT SUPPLIER_GSTIN,'B2CS' AS TAX_DOC_TYPE, (IFNULL(SUPPLIER_GSTIN, '') ||'|'|| IFNULL(RETURN_PERIOD, '') ||'|'||IFNULL(TRAN_TYPE, '') "
				+ " ||'|'||IFNULL(NEW_POS, 9999)||'|'||IFNULL(NEW_ECOM_GSTIN, '') ||'|'||IFNULL(NEW_RATE, 9999)) KEY, "
				+ " SUM(IFNULL(NEW_TAXABLE_VALUE, 0)+IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT, 0)) AS DOC_AMT, "
				+ " IFNULL(SUM(IGST_AMT), 0) AS IGST_AMT, "
				+ " IFNULL(SUM(CGST_AMT), 0) AS CGST_AMT, "
				+ " IFNULL(SUM(SGST_AMT), 0) AS SGST_AMT, "
				+ " IFNULL(SUM(NEW_TAXABLE_VALUE), 0) AS TAXABLE_VALUE, "
				+ " SUM(IFNULL(IGST_AMT, 0)+IFNULL(CGST_AMT, 0)+IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT, 0)) AS TAX_PAYABLE, "
				+ " IFNULL(SUM(CESS_AMT), 0) AS CESS "
				+ " FROM GSTR1_PROCESSED_B2CS " + " WHERE IS_DELETE = FALSE "
				+ " AND IS_AMENDMENT=FALSE "
				+ " AND SUPPLIER_GSTIN IN (:GSTIN) "
				+ " AND DERIVED_RET_PERIOD = :taxPeriod "
				+ " GROUP BY SUPPLIER_GSTIN, " + " RETURN_PERIOD, "
				+ " TRAN_TYPE, " + " NEW_POS, " + " NEW_ECOM_GSTIN, "
				+ " NEW_RATE) " + " GROUP BY SUPPLIER_GSTIN, " + " KEY, "
				+ " TAX_DOC_TYPE " + "  " + "  " + "  " + "  " + " UNION ALL  "
				+ " SELECT SUPPLIER_GSTIN, " + " COUNT(*) RECORD_COUNT, "
				+ " SUM(DOC_AMT) DOC_AMT, " + " SUM(IGST_AMT) IGST_AMT, "
				+ " SUM(CGST_AMT) CGST_AMT, " + " SUM(SGST_AMT) SGST_AMT, "
				+ " SUM(TAXABLE_VALUE) TAXABLE_VALUE, "
				+ " SUM(TAX_PAYABLE) TAX_PAYABLE, " + " SUM(CESS) CESS "
				+ " FROM " + " (SELECT SUPPLIER_GSTIN,'B2CSA' AS TAX_DOC_TYPE, "
				+ " SUM(IFNULL(NEW_TAXABLE_VALUE, 0)+IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT, 0)) AS DOC_AMT, "
				+ " IFNULL(SUM(IGST_AMT), 0) AS IGST_AMT, "
				+ " IFNULL(SUM(CGST_AMT), 0) AS CGST_AMT, "
				+ " IFNULL(SUM(SGST_AMT), 0) AS SGST_AMT, "
				+ " IFNULL(SUM(NEW_TAXABLE_VALUE), 0) AS TAXABLE_VALUE, "
				+ " SUM(IFNULL(IGST_AMT, 0)+IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT, 0)) AS TAX_PAYABLE, "
				+ " IFNULL(SUM(CESS_AMT), 0) AS CESS "
				+ " FROM GSTR1_PROCESSED_B2CS " + " WHERE IS_DELETE = FALSE "
				+ " AND IS_AMENDMENT=TRUE " + " AND SUPPLIER_GSTIN IN (:GSTIN) "
				+ " AND DERIVED_RET_PERIOD = :taxPeriod "
				+ " GROUP BY SUPPLIER_GSTIN, " + " RETURN_PERIOD, "
				+ " TRAN_TYPE, " + " MONTH, " + " NEW_POS, "
				+ " NEW_ECOM_GSTIN) " + " GROUP BY SUPPLIER_GSTIN " + "  "
				+ "  " + " UNION ALL SELECT SUPPLIER_GSTIN, "
				+ " SUM(RECORD_COUNT) RECORD_COUNT, "
				+ " SUM(DOC_AMT) DOC_AMT, " + " SUM(IGST_AMT) IGST_AMT, "
				+ " SUM(CGST_AMT) CGST_AMT, " + " SUM(SGST_AMT) SGST_AMT, "
				+ " SUM(TAXABLE_VALUE) TAXABLE_VALUE, "
				+ " SUM(TAX_PAYABLE) TAX_PAYABLE, " + " SUM(CESS) CESS "
				+ " FROM " + " (SELECT SUPPLIER_GSTIN,TAX_DOC_TYPE, "
				+ " COUNT(*) AS RECORD_COUNT, " + " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('DR', 'RDR') THEN DOC_AMT "
				+ " END), 0)- IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('CR', 'RCR') THEN DOC_AMT "
				+ " END), 0) AS DOC_AMT, " + " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('DR', 'RDR') THEN IGST_AMT "
				+ " END), 0)- IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('CR', 'RCR') THEN IGST_AMT "
				+ " END), 0) AS IGST_AMT, " + " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('DR', 'RDR') THEN CGST_AMT "
				+ " END), 0)- IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('CR', 'RCR') THEN CGST_AMT "
				+ " END), 0) AS CGST_AMT, " + " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('DR', 'RDR') THEN SGST_AMT "
				+ " END), 0)- IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('CR', 'RCR') THEN SGST_AMT "
				+ " END), 0) AS SGST_AMT, " + " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('DR', 'RDR') THEN TAXABLE_VALUE "
				+ " END), 0)- IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('CR', 'RCR') THEN TAXABLE_VALUE "
				+ " END), 0) AS TAXABLE_VALUE, " + " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('DR', 'RDR') THEN IFNULL(IGST_AMT, 0)+IFNULL(CGST_AMT, 0)+IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT_SPECIFIC, "
				+ " 0)+IFNULL(CESS_AMT_ADVALOREM, 0) "
				+ " END), 0)- IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('CR', 'RCR') THEN IFNULL(IGST_AMT, 0)+IFNULL(CGST_AMT, 0)+IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT_SPECIFIC, "
				+ " 0)+IFNULL(CESS_AMT_ADVALOREM, 0) "
				+ " END), 0) AS TAX_PAYABLE, " + " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('DR', 'RDR') THEN IFNULL(CESS_AMT_SPECIFIC, 0)+IFNULL(CESS_AMT_ADVALOREM, 0) "
				+ " END), 0)- IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('CR', 'RCR') THEN IFNULL(CESS_AMT_SPECIFIC, 0)+IFNULL(CESS_AMT_ADVALOREM, 0) "
				+ " END), 0) AS CESS " + " FROM ANX_OUTWARD_DOC_HEADER "
				+ " WHERE ASP_INVOICE_STATUS = 2 "
				+ " AND COMPLIANCE_APPLICABLE = TRUE "
				+ " AND IS_DELETE = FALSE " + " AND SUPPLY_TYPE <> 'CAN' "
				+ " AND RETURN_TYPE='GSTR1' " + " AND TAX_DOC_TYPE IN ('CDNR', "
				+ " 'CDNRA', " + " 'CDNUR', " + " 'CDNUR-EXPORTS', "
				+ " 'CDNUR-B2CL', " + " 'CDNURA') "
				+ " AND SUPPLIER_GSTIN IN (:GSTIN) "
				+ " AND DERIVED_RET_PERIOD =  :taxPeriod "
				+ " GROUP BY SUPPLIER_GSTIN,TAX_DOC_TYPE) "
				+ " GROUP BY SUPPLIER_GSTIN) " + " GROUP BY SUPPLIER_GSTIN  "
				+ "  " + " UNION ALL " + "  " + "  "
				+ " SELECT SUPPLIER_GSTIN, " + " SUM(IGST) IGST, "
				+ " SUM(CGST) CGST, " + " SUM(SGST) SGST, "
				+ " SUM(CESS) CESS, " + " SUM(TOTAL_TAX) TOTAL_TAX, "
				+ " SUM(GROSS_ADVANCE) TAXABLE_VALUE " + " FROM "
				+ " (SELECT SUPPLIER_GSTIN,TAX_DOC_TYPE, " + " DOC_TYPE, "
				+ " COUNT(*) RECORD_COUNT, " + " SUM(TOTAL_VALUE) TOTAL_VALUE, "
				+ " SUM(GROSS_ADVANCE) GROSS_ADVANCE, "
				+ " SUM(TOTAL_TAX) TOTAL_TAX, " + " SUM(IGST) IGST, "
				+ " SUM(CGST) CGST, " + " SUM(SGST) SGST, " + " SUM(CESS) CESS "
				+ " FROM "
				+ " (SELECT SUPPLIER_GSTIN,'ADV REC' AS TAX_DOC_TYPE, "
				+ " 'RV' AS DOC_TYPE, "
				+ " SUM(IFNULL(NEW_GROSS_ADV_RECEIVED, 0)+IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT, 0)) AS TOTAL_VALUE, "
				+ " IFNULL(SUM(NEW_GROSS_ADV_RECEIVED), 0) AS GROSS_ADVANCE, "
				+ " SUM(IFNULL(IGST_AMT, 0)+IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT, 0)) AS TOTAL_TAX, "
				+ " IFNULL(SUM(IGST_AMT), 0) AS IGST, "
				+ " IFNULL(SUM(CGST_AMT), 0) AS CGST, "
				+ " IFNULL(SUM(SGST_AMT), 0) AS SGST, "
				+ " IFNULL(SUM(CESS_AMT), 0) AS CESS "
				+ " FROM GSTR1_PROCESSED_ADV_RECEIVED "
				+ " WHERE IS_DELETE = FALSE " + " AND IS_AMENDMENT=FALSE "
				+ " AND SUPPLIER_GSTIN IN (:GSTIN) "
				+ " AND DERIVED_RET_PERIOD = :taxPeriod "
				+ " GROUP BY SUPPLIER_GSTIN, " + " RETURN_PERIOD, (CASE "
				+ " WHEN TRAN_TYPE IN ('ZL65', " + "   'L65', " + "   'zl65', "
				+ "   'l65', " + "   'zL65', " + "   'Zl65') THEN 'L65' "
				+ " WHEN (TRAN_TYPE IN ('Z', " + "    'N', " + "    '', "
				+ "    'z', " + "    'n') " + " OR TRAN_TYPE IS NULL) THEN 'N' "
				+ " END),NEW_POS) " + " GROUP BY SUPPLIER_GSTIN,TAX_DOC_TYPE, "
				+ " DOC_TYPE " + "  " + "  "
				+ " UNION ALL SELECT SUPPLIER_GSTIN,TAX_DOC_TYPE, "
				+ " DOC_TYPE, " + " COUNT(*) RECORD_COUNT, "
				+ " SUM(TOTAL_VALUE) TOTAL_VALUE, "
				+ " SUM(GROSS_ADVANCE) GROSS_ADVANCE, "
				+ " SUM(TOTAL_TAX) TOTAL_TAX, " + " SUM(IGST) IGST, "
				+ " SUM(CGST) CGST, " + " SUM(SGST) SGST, " + " SUM(CESS) CESS "
				+ " FROM "
				+ " (SELECT SUPPLIER_GSTIN,'ADV REC-A' AS TAX_DOC_TYPE, "
				+ " 'RRV' AS DOC_TYPE, "
				+ " SUM(IFNULL(NEW_GROSS_ADV_RECEIVED, 0)+IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT, 0)) AS TOTAL_VALUE, "
				+ " IFNULL(SUM(NEW_GROSS_ADV_RECEIVED), 0) AS GROSS_ADVANCE, "
				+ " SUM(IFNULL(IGST_AMT, 0)+IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT, 0)) AS TOTAL_TAX, "
				+ " IFNULL(SUM(IGST_AMT), 0) AS IGST, "
				+ " IFNULL(SUM(CGST_AMT), 0) AS CGST, "
				+ " IFNULL(SUM(SGST_AMT), 0) AS SGST, "
				+ " IFNULL(SUM(CESS_AMT), 0) AS CESS "
				+ " FROM GSTR1_PROCESSED_ADV_RECEIVED "
				+ " WHERE IS_DELETE = FALSE " + " AND IS_AMENDMENT=TRUE "
				+ " AND SUPPLIER_GSTIN IN (:GSTIN) "
				+ " AND DERIVED_RET_PERIOD = :taxPeriod "
				+ " GROUP BY SUPPLIER_GSTIN, " + " RETURN_PERIOD, (CASE "
				+ " WHEN TRAN_TYPE IN ('ZL65', " + "   'L65', " + "   'zl65', "
				+ "   'l65', " + "   'zL65', " + "   'Zl65') THEN 'L65' "
				+ " WHEN (TRAN_TYPE IN ('Z', " + "    'N', " + "    '', "
				+ "    'z', " + "    'n') " + " OR TRAN_TYPE IS NULL) THEN 'N' "
				+ " END), MONTH, " + " NEW_POS) "
				+ " GROUP BY SUPPLIER_GSTIN,TAX_DOC_TYPE, " + " DOC_TYPE "
				+ "  " + "  "
				+ " UNION ALL SELECT SUPPLIER_GSTIN,TAX_DOC_TYPE, "
				+ " DOC_TYPE, " + " COUNT(*) RECORD_COUNT, "
				+ " SUM(TOTAL_VALUE)*-1 TOTAL_VALUE, "
				+ " SUM(GROSS_ADVANCE)*-1 GROSS_ADVANCE, "
				+ " SUM(TOTAL_TAX)*-1 TOTAL_TAX, " + " SUM(IGST)*-1 IGST, "
				+ " SUM(CGST)*-1 CGST, " + " SUM(SGST)*-1 SGST, "
				+ " SUM(CESS)*-1 CESS " + " FROM "
				+ " (SELECT SUPPLIER_GSTIN,'ADV ADJ' AS TAX_DOC_TYPE, "
				+ " 'AV' AS DOC_TYPE, "
				+ " SUM(IFNULL(NEW_GROSS_ADV_ADJUSTED, 0)+IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT, 0)) AS TOTAL_VALUE, "
				+ " IFNULL(SUM(NEW_GROSS_ADV_ADJUSTED), 0) AS GROSS_ADVANCE, "
				+ " SUM(IFNULL(IGST_AMT, 0)+IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT, 0)) AS TOTAL_TAX, "
				+ " IFNULL(SUM(IGST_AMT), 0) AS IGST, "
				+ " IFNULL(SUM(CGST_AMT), 0) AS CGST, "
				+ " IFNULL(SUM(SGST_AMT), 0) AS SGST, "
				+ " IFNULL(SUM(CESS_AMT), 0) AS CESS "
				+ " FROM GSTR1_PROCESSED_ADV_ADJUSTMENT "
				+ " WHERE IS_DELETE = FALSE " + " AND IS_AMENDMENT=FALSE "
				+ " AND SUPPLIER_GSTIN IN (:GSTIN) "
				+ " AND DERIVED_RET_PERIOD =:taxPeriod "
				+ " GROUP BY SUPPLIER_GSTIN, " + " RETURN_PERIOD, (CASE "
				+ " WHEN TRAN_TYPE IN ('ZL65', " + "   'L65', " + "   'zl65', "
				+ "   'l65', " + "   'zL65', " + "   'Zl65') THEN 'L65' "
				+ " WHEN (TRAN_TYPE IN ('Z', " + "    'N', " + "    '', "
				+ "    'z', " + "    'n') " + " OR TRAN_TYPE IS NULL) THEN 'N' "
				+ " END),NEW_POS) " + " GROUP BY SUPPLIER_GSTIN,TAX_DOC_TYPE, "
				+ " DOC_TYPE " + "  "
				+ " UNION ALL SELECT SUPPLIER_GSTIN,TAX_DOC_TYPE, "
				+ " DOC_TYPE, " + " COUNT(*) RECORD_COUNT, "
				+ " SUM(TOTAL_VALUE) TOTAL_VALUE, "
				+ " SUM(GROSS_ADVANCE) GROSS_ADVANCE, "
				+ " SUM(TOTAL_TAX) TOTAL_TAX, " + " SUM(IGST) IGST, "
				+ " SUM(CGST) CGST, " + " SUM(SGST) SGST, " + " SUM(CESS) CESS "
				+ " FROM "
				+ " (SELECT SUPPLIER_GSTIN,'ADV ADJ-A' AS TAX_DOC_TYPE, "
				+ " 'RAV' AS DOC_TYPE, "
				+ " SUM(IFNULL(NEW_GROSS_ADV_ADJUSTED, 0)+IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT, 0)) AS TOTAL_VALUE, "
				+ " IFNULL(SUM(NEW_GROSS_ADV_ADJUSTED), 0) AS GROSS_ADVANCE, "
				+ " SUM(IFNULL(IGST_AMT, 0)+IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT, 0)) AS TOTAL_TAX, "
				+ " IFNULL(SUM(IGST_AMT), 0) AS IGST, "
				+ " IFNULL(SUM(CGST_AMT), 0) AS CGST, "
				+ " IFNULL(SUM(SGST_AMT), 0) AS SGST, "
				+ " IFNULL(SUM(CESS_AMT), 0) AS CESS "
				+ " FROM GSTR1_PROCESSED_ADV_ADJUSTMENT "
				+ " WHERE IS_DELETE = FALSE " + " AND IS_AMENDMENT=TRUE "
				+ " AND SUPPLIER_GSTIN IN (:GSTIN) "
				+ " AND DERIVED_RET_PERIOD = :taxPeriod "
				+ " GROUP BY SUPPLIER_GSTIN, " + " RETURN_PERIOD, (CASE "
				+ " WHEN TRAN_TYPE IN ('ZL65', " + "   'L65', " + "   'zl65', "
				+ "   'l65', " + "   'zL65', " + "   'Zl65') THEN 'L65' "
				+ " WHEN (TRAN_TYPE IN ('Z', " + "    'N', " + "    '', "
				+ "    'z', " + "    'n') " + " OR TRAN_TYPE IS NULL) THEN 'N' "
				+ " END),MONTH, " + " NEW_POS) "
				+ " GROUP BY SUPPLIER_GSTIN,TAX_DOC_TYPE, " + " DOC_TYPE) "
				+ " GROUP BY SUPPLIER_GSTIN " + " UNION ALL " + "  "
				+ " SELECT SUPPLIER_GSTIN, " + " 0 AS  IGST, " + " 0 AS CGST, "
				+ " 0 AS SGST, " + " 0 AS CESS, " + " 0 AS TOTAL_TAX, "
				+ " SUM(IFNULL(NIL_RATED_SUPPLIES,0)+IFNULL(EXMPTED_SUPPLIES,0) +IFNULL(NON_GST_SUPPLIES,0)) AS TAXABLE_VALUE "
				+ " FROM " + " (SELECT (CASE "
				+ " WHEN TABLE_SECTION='8A' THEN 'Inter-State Supplies to Registered Person' "
				+ " WHEN TABLE_SECTION='8B' THEN 'Intra-State Supplies to Registered Person' "
				+ " WHEN TABLE_SECTION='8C' THEN 'Inter-State Supplies to UnRegistered Person' "
				+ " WHEN TABLE_SECTION='8D' THEN 'Intra-State Supplies to UnRegistered Person' "
				+ " END) DESCRIPTION, "
				+ " (SUPPLIER_GSTIN||'|'||RETURN_PERIOD||'|'||TABLE_SECTION) AS DOC_KEY, "
				+ " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('INV', 'DR', 'BOS') "
				+ " AND SUPPLY_TYPE='NIL' THEN TAXABLE_VALUE "
				+ " END), 0) - IFNULL(SUM(CASE " + " WHEN DOC_TYPE='CR' "
				+ " AND SUPPLY_TYPE='NIL' THEN TAXABLE_VALUE "
				+ " END), 0) AS NIL_RATED_SUPPLIES, " + " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('INV', 'DR', 'BOS') "
				+ " AND SUPPLY_TYPE='EXT' THEN TAXABLE_VALUE "
				+ " END), 0) - IFNULL(SUM(CASE " + " WHEN DOC_TYPE='CR' "
				+ " AND SUPPLY_TYPE='EXT' THEN TAXABLE_VALUE "
				+ " END), 0) AS EXMPTED_SUPPLIES, " + " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('INV', 'DR', 'BOS') "
				+ " AND SUPPLY_TYPE IN ('NON', 'SCH3') THEN TAXABLE_VALUE "
				+ " END), 0) - IFNULL(SUM(CASE " + " WHEN DOC_TYPE='CR' "
				+ " AND SUPPLY_TYPE IN ('NON', 'SCH3') THEN TAXABLE_VALUE "
				+ " END), 0) AS NON_GST_SUPPLIES, " + " RETURN_PERIOD, "
				+ " DERIVED_RET_PERIOD, " + " SUPPLIER_GSTIN, "
				+ " TABLE_SECTION AS DESCRIPTION_KEY, " + " COUNT(ID) CNT "
				+ " FROM ANX_OUTWARD_DOC_HEADER "
				+ " WHERE TAX_DOC_TYPE IN ('NILEXTNON') "
				+ " AND ASP_INVOICE_STATUS = 2 "
				+ " AND COMPLIANCE_APPLICABLE=TRUE " + " AND IS_DELETE = FALSE "
				+ " AND SUPPLY_TYPE <> 'CAN' " + " AND RETURN_TYPE='GSTR1' "
				+ " AND SUPPLIER_GSTIN  IN (:GSTIN) "
				+ " AND DERIVED_RET_PERIOD = :taxPeriod "
				+ " GROUP BY RETURN_PERIOD, " + " DERIVED_RET_PERIOD, "
				+ " SUPPLIER_GSTIN, " + " TABLE_SECTION        "
				+ " UNION ALL  " + " SELECT 'A' DESCRIPTION, "
				+ " 'A' DOC_KEY, "
				+ " SUM(NIL_RATED_SUPPLIES) NIL_RATED_SUPPLIES, "
				+ " SUM(EXMPTED_SUPPLIES) EXMPTED_SUPPLIES, "
				+ " SUM(NON_GST_SUPPLIES) NON_GST_SUPPLIES, "
				+ " RETURN_PERIOD, " + " DERIVED_RET_PERIOD, "
				+ " SUPPLIER_GSTIN, " + " 'A' DESCRIPTION_KEY, "
				+ " COUNT(DISTINCT AS_PROCESSED_ID) CNT " + " FROM "
				+ " (SELECT (CASE "
				+ " WHEN TABLE_SECTION='8A' THEN 'Inter-State Supplies to Registered Person' "
				+ " WHEN TABLE_SECTION='8B' THEN 'Intra-State Supplies to Registered Person' "
				+ " WHEN TABLE_SECTION='8C' THEN 'Inter-State Supplies to UnRegistered Person' "
				+ " WHEN TABLE_SECTION='8D' THEN 'Intra-State Supplies to UnRegistered Person' "
				+ " END) DESCRIPTION, "
				+ " (SUPPLIER_GSTIN||'|'||RETURN_PERIOD||'|'||TABLE_SECTION) AS DOC_KEY, "
				+ " CASE "
				+ " WHEN SUPPLY_TYPE='NIL' THEN IFNULL(TAXABLE_VALUE, 0) "
				+ " END AS NIL_RATED_SUPPLIES, " + " CASE "
				+ " WHEN SUPPLY_TYPE='EXT' THEN IFNULL(TAXABLE_VALUE, 0) "
				+ " END AS EXMPTED_SUPPLIES, " + " CASE "
				+ " WHEN SUPPLY_TYPE IN ('NON') THEN IFNULL(TAXABLE_VALUE, 0) "
				+ " END AS NON_GST_SUPPLIES, " + " RETURN_PERIOD, "
				+ " DERIVED_RET_PERIOD, " + " SUPPLIER_GSTIN, "
				+ " TABLE_SECTION AS DESCRIPTION_KEY, " + " AS_PROCESSED_ID "
				+ " FROM GSTR1_SUMMARY_NILEXTNON " + " WHERE IS_DELETE = FALSE "
				+ " AND SUPPLIER_GSTIN  IN (:GSTIN) "
				+ " AND DERIVED_RET_PERIOD = :taxPeriod " + " ) "
				+ " GROUP BY RETURN_PERIOD, " + " DERIVED_RET_PERIOD, "
				+ " SUPPLIER_GSTIN) A " + " INNER JOIN  " + " ( " + "  "
				+ " SELECT GSTIN_COLUMN	,DERIVED_RET_PERIOD	,IS_NIL_UI FROM  "
				+ " ( "
				+ " SELECT *,RANK() OVER(PARTITION BY GSTIN_COLUMN,DERIVED_RET_PERIOD ORDER BY ID DESC ) AS RANK "
				+ " FROM( "
				+ " SELECT ID,:GSTIN AS GSTIN_COLUMN,gst.DERIVED_RET_PERIOD,IS_NIL_UI   "
				+ " FROM GSTN_USER_REQUEST gst "
				+ " WHERE RETURN_TYPE='GSTR1'  " + " AND REQUEST_TYPE='SAVE' "
				+ " AND gst.DERIVED_RET_PERIOD =:taxPeriod "
				+ " AND GSTIN IN (:GSTIN) " + "  " + " ) " + " )WHERE RANK=1 "
				+ "  " + " ) UI " + " ON A.SUPPLIER_GSTIN =UI.GSTIN_COLUMN "
				+ " AND A.DERIVED_RET_PERIOD = :taxPeriod "
				+ " AND IS_NIL_UI=FALSE " + " GROUP BY SUPPLIER_GSTIN " + "  "
				+ " UNION ALL " + "  " + " SELECT SUPPLIER_GSTIN, "
				+ " 0 AS  IGST, " + " 0 AS CGST, " + " 0 AS SGST, "
				+ " 0 AS CESS, " + " 0 AS TOTAL_TAX, "
				+ " SUM(IFNULL(NIL_RATED_SUPPLIES,0)+IFNULL(EXMPTED_SUPPLIES,0) +IFNULL(NON_GST_SUPPLIES,0)) AS TAXABLE_VALUE "
				+ " FROM "
				+ " (SELECT SUPPLIER_GSTIN,IFNULL(SUM(NIL_RATED_SUPPLIES), 0) AS NIL_RATED_SUPPLIES, "
				+ " IFNULL(SUM(EXMPTED_SUPPLIES), 0) AS EXMPTED_SUPPLIES, "
				+ " IFNULL(SUM(NON_GST_SUPPLIES), 0) AS NON_GST_SUPPLIES, "
				+ " COUNT(CNT_ID) CNT " + " FROM " + " (SELECT (CASE "
				+ " WHEN DESCRIPTION_KEY='8A' THEN 'Inter-State Supplies to Registered Person' "
				+ " WHEN DESCRIPTION_KEY='8B' THEN 'Intra-State Supplies to Registered Person' "
				+ " WHEN DESCRIPTION_KEY='8C' THEN 'Inter-State Supplies to UnRegistered Person' "
				+ " WHEN DESCRIPTION_KEY='8D' THEN 'Intra-State Supplies to UnRegistered Person' "
				+ " END) DESCRIPTION, " + " DOC_KEY, "
				+ " IFNULL(SUM(NIL_RATED_SUPPLIES), 0) NIL_RATED_SUPPLIES, "
				+ " IFNULL(SUM(EXMPTED_SUPPLIES), 0) EXMPTED_SUPPLIES, "
				+ " IFNULL(SUM(NON_GST_SUPPLIES), 0) NON_GST_SUPPLIES, "
				+ " RETURN_PERIOD, " + " DERIVED_RET_PERIOD, "
				+ " SUPPLIER_GSTIN, " + " DESCRIPTION_KEY, "
				+ " count(ID) CNT_ID " + " FROM GSTR1_USERINPUT_NILEXTNON "
				+ " WHERE IS_DELETE=FALSE "
				+ " AND SUPPLIER_GSTIN  IN (:GSTIN) "
				+ " AND DERIVED_RET_PERIOD =:taxPeriod " + " GROUP BY ID, "
				+ " RETURN_PERIOD, " + " DESCRIPTION_KEY, "
				+ " SUPPLIER_GSTIN, " + " DERIVED_RET_PERIOD, " + " DOC_KEY) B "
				+ " INNER JOIN " + " (    "
				+ " SELECT GSTIN_COLUMN	,DERIVED_RET_PERIOD	,IS_NIL_UI FROM  "
				+ " ( "
				+ " SELECT *,RANK() OVER(PARTITION BY GSTIN_COLUMN,DERIVED_RET_PERIOD ORDER BY ID DESC ) AS RANK "
				+ " FROM( "
				+ " SELECT ID,:GSTIN AS GSTIN_COLUMN,gst.DERIVED_RET_PERIOD,IS_NIL_UI   "
				+ " FROM GSTN_USER_REQUEST gst "
				+ " WHERE RETURN_TYPE='GSTR1'  " + " AND REQUEST_TYPE='SAVE' "
				+ " AND gst.DERIVED_RET_PERIOD =:taxPeriod "
				+ " AND GSTIN IN (:GSTIN) " + " ) " + " )WHERE RANK=1 "
				+ " )UI " + " ON B.SUPPLIER_GSTIN =UI.GSTIN_COLUMN "
				+ " AND B.DERIVED_RET_PERIOD = :taxPeriod "
				+ " AND IS_NIL_UI=TRUE    " + " group by SUPPLIER_GSTIN) "
				+ " group by SUPPLIER_GSTIN  " + "  " + " UNION ALL  " + "  "
				+ " SELECT GI.GSTIN AS SUPPLIER_GSTIN , "
				+ " SUM(IGST_AMT) AS IGST, " + " SUM(CGST_AMT) AS CGST, "
				+ " SUM(SGST_AMT) AS SGST, " + " 0 		 AS CESS, "
				+ " SUM( IFNULL(CGST_AMT,0)    + IFNULL(IGST_AMT,0)    + IFNULL(SGST_AMT,0)  ) AS  TOTAL_TAX, "
				+ " SUM(NEW_GROSS_AMT ) AS TAXABLE_VALUE "
				+ " FROM GSTR7_PROCESSED_TDS TDS  "
				+ " INNER JOIN GSTIN_INFO GI ON TDS.TDS_DEDUCTOR_GSTIN=GI.GSTIN AND  TDS.is_delete=FALSE  AND REG_TYPE='TDS' AND TDS.TABLE_NUM='Table-3' "
				+ " AND TDS.DERIVED_RET_PERIOD    =:taxPeriod  "
				+ " WHERE TDS.TDS_DEDUCTOR_GSTIN IN (:GSTIN)  "
				+ " GROUP BY GI.GSTIN  " + "  " + " UNION ALL "
				+ " SELECT  GI.GSTIN AS SUPPLIER_GSTIN,   " + "  "
				+ " SUM(IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.CGST_AMT_AS_IGST,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.CGST_AMT_AS_IGST,0)  "
				+ " ELSE 0  END,0)  "
				+ " + IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.IGST_AMT_AS_IGST,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.IGST_AMT_AS_IGST,0)  "
				+ " ELSE 0 END ,0)  "
				+ " + IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.SGST_AMT_AS_IGST,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.SGST_AMT_AS_IGST,0)  "
				+ " ELSE 0 END ,0)            " + " ) AS IGST, " + "  "
				+ " SUM(IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.CGST_AMT_AS_CGST,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.CGST_AMT_AS_CGST,0)  "
				+ " ELSE 0  END,0)  "
				+ " + IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.IGST_AMT_AS_CGST,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.IGST_AMT_AS_CGST,0)  "
				+ " ELSE 0 END ,0)  " + "  " + " ) AS CGST, " + "  " + "  "
				+ " SUM(IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.IGST_AMT_AS_SGST,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.IGST_AMT_AS_SGST,0)  "
				+ " ELSE 0  END,0)  "
				+ " + IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.SGST_AMT_AS_SGST,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.SGST_AMT_AS_SGST,0)  "
				+ " ELSE 0 END ,0)  " + "  " + " ) AS SGST, " + "  " + "  "
				+ " SUM(IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.CESS_AMT,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.CESS_AMT,0)  "
				+ " ELSE 0  END,0)            " + " ) AS CESS, " + "  " + "  "
				+ " SUM(IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.CESS_AMT,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.CESS_AMT,0)  "
				+ " ELSE 0  END,0)            "
				+ " +IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.IGST_AMT_AS_SGST,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.IGST_AMT_AS_SGST,0)  "
				+ " ELSE 0  END,0)  "
				+ " + IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.SGST_AMT_AS_SGST,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.SGST_AMT_AS_SGST,0)  "
				+ " ELSE 0 END ,0)  " + "  "
				+ " +IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.CGST_AMT_AS_CGST,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.CGST_AMT_AS_CGST,0)  "
				+ " ELSE 0  END,0)  "
				+ " + IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.IGST_AMT_AS_CGST,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.IGST_AMT_AS_CGST,0)  "
				+ " ELSE 0 END ,0)  " + "  "
				+ " +IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.CGST_AMT_AS_IGST,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.CGST_AMT_AS_IGST,0)  "
				+ " ELSE 0  END,0)  "
				+ " + IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.IGST_AMT_AS_IGST,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.IGST_AMT_AS_IGST,0)  "
				+ " ELSE 0 END ,0)  "
				+ " + IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.SGST_AMT_AS_IGST,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.SGST_AMT_AS_IGST,0)  "
				+ " ELSE 0 END ,0)            " + " ) AS TOAL_TAX, " + "  "
				+ " 0 AS TAXABLE_VALUE " + " FROM  GSTR6_ISD_DISTRIBUTION ISD "
				+ " INNER JOIN GSTIN_INFO GI ON ISD.ISD_GSTIN=GI.GSTIN AND  ISD.IS_DELETE=FALSE  AND REG_TYPE='ISD' "
				+ " AND ISD.DERIVED_RET_PERIOD    =:taxPeriod  "
				+ " WHERE ISD.ISD_GSTIN IN (:GSTIN)  " + " GROUP BY GI.GSTIN  "
				+ " )GROUP BY SUPPLIER_GSTIN " + " ) B  "
				+ " ON GT.GSTIN=B.SUPPLIER_GSTIN " + "  " + "  "
				+ " LEFT OUTER JOIN ( " + " SELECT GI.GSTIN , "
				+ " SUM( IFNULL(CASH_IGST_TOT_BAL,0)    + IFNULL(CASH_CGST_TOT_BAL,0)    + IFNULL(CASH_CESS_TOT_BAL,0) + IFNULL(CASH_SGST_TOT_BAL,0)  ) AS CASH_LEDGER, "
				+ " SUM( IFNULL(ITC_CGST_TOT_BAL,0)    + IFNULL(ITC_SGST_TOT_BAL,0)    + IFNULL(ITC_IGST_TOT_BAL,0) + IFNULL(ITC_CESS_TOT_BAL,0)  ) AS CREDIT_LEDGER, "
				+ " SUM( IFNULL(LIB_IGST_TOT_BAL,0)    + IFNULL(LIB_CGST_TOT_BAL,0)    + IFNULL(LIB_SGST_TOT_BAL,0) + IFNULL(LIB_CESS_TOT_BAL,0)  ) AS LIABILITY_LEDGER  "
				+ " FROM GET_LEDGER_SUMMARIZED_BALANCE LEDGER  "
				+ " INNER JOIN GSTIN_INFO GI ON LEDGER.GSTIN=GI.GSTIN AND UPPER(LEDGER.REFRESH_STATUS)='ACTIVE'  "
				+ " AND CONCAT(RIGHT(LEDGER.TAX_PERIOD,4), LEFT(LEDGER.TAX_PERIOD,2))    =:taxPeriod  "
				+ " WHERE LEDGER.GSTIN IN (:GSTIN)  " + " GROUP BY GI.GSTIN  "
				+ " )LGR ON GT.GSTIN=LGR.GSTIN "
				+ " WHERE GT.GSTIN IN (:GSTIN) ";
		return query;
	}

	@Override
	public DashboardHOOutwardSupplyDto getDashBoardOutwardStatus(Long entityId,
			String taxPeriod, List<String> listofgstins) {
		try {

			/*
			 * Map<String, List<String>> dataSecurityAttrMap = new HashMap<>();
			 * Map<String, String> outwardSecurityAttributeMap =
			 * DataSecurityAttributeUtil .getOutwardSecurityAttributeMap();
			 * dataSecurityAttrMap = DataSecurityAttributeUtil
			 * .dataSecurityAttrMapForQuery(Arrays.asList(entityId),
			 * outwardSecurityAttributeMap); List<String> listofgstins =
			 * dataSecurityAttrMap .get(OnboardingConstant.GSTIN);
			 */
			if (CollectionUtils.isEmpty(listofgstins))
				throw new AppException("User Does not have any gstin");

			String derivedTaxPeriod = taxPeriod.substring(2)
					.concat(taxPeriod.substring(0, 2));

			LandingDashboardBatchRefreshEntity batchId = ldRepo
					.getCompletedLatestBatchId(derivedTaxPeriod, entityId);
			LandingDashboardGstr1VsGstr3BEntity outwardSuppEntity = new LandingDashboardGstr1VsGstr3BEntity();

			if (batchId != null) {
				outwardSuppEntity = outSupRepo
						.getBatchIdData(batchId.getBatchId());
			} else {
				outwardSuppEntity = outSupRepo
						.getentityIdtaxPeriodData(entityId, derivedTaxPeriod);
			}
			DashboardHOOutwardSupplyDto objDto = convertToOutwardSupplyStatusDto(
					outwardSuppEntity == null
							? new LandingDashboardGstr1VsGstr3BEntity()
							: outwardSuppEntity);
			// System.out.println(retList);
			return objDto;
		} catch (NoResultException ex) {
			return new DashboardHOOutwardSupplyDto();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException(
					"Exception Occured in " + "DashboardHODaoImpl");
		}
	}

	@SuppressWarnings("unused")
	private String createDashBoardOutwardSupplyStatus(Long entityId,
			String taxPeriod, List<String> GSTIN) {

		String query = " SELECT IFNULL(G1_TAXABLE_VALUE, 0) AS G1_TAXABLE_VALUE , "
				+ " IFNULL(G1_TOTAL_TAX, 0) AS G1_TOTAL_TAX , "
				+ " IFNULL(G3_TAXABLE_VALUE, 0) AS G3_TAXABLE_VALUE , "
				+ " IFNULL(G3_TOTAL_TAX, 0) AS G3_TOTAL_TAX , "
				+ " ABS(IFNULL(G1_TAXABLE_VALUE, 0)-IFNULL(G3_TAXABLE_VALUE, 0)) AS DIFF_TAXABLE_VALUE , "
				+ " ABS(IFNULL(G1_TOTAL_TAX, 0)-IFNULL(G3_TOTAL_TAX, 0)) DIFF_TOTAL_TAX "
				+ " FROM " + " ( " + "  " + "  " + " SELECT ENTITY_ID, "
				+ " SUM(TAXABLE_VALUE) AS G1_TAXABLE_VALUE , "
				+ " SUM(TOTAL_TAX) AS G1_TOTAL_TAX " + " FROM " + " ( " + "  "
				+ "  " + " SELECT ENTITY_ID,SUPPLIER_GSTIN, "
				+ " SUM(IGST_AMT) IGST_AMT, " + " SUM(CGST_AMT) CGST_AMT, "
				+ " SUM(SGST_AMT) SGST_AMT, " + " SUM(CESS) CESS, "
				+ " SUM(TAX_PAYABLE) TOTAL_TAX, "
				+ " SUM(TAXABLE_VALUE) TAXABLE_VALUE " + "  " + "  " + " FROM "
				+ " (SELECT ENTITY_ID,SUPPLIER_GSTIN, "
				+ " SUM(RECORD_COUNT) RECORD_COUNT, " + " SUM(DOC_AMT)DOC_AMT, "
				+ " SUM(IGST_AMT) IGST_AMT, " + " SUM(CGST_AMT) CGST_AMT, "
				+ " SUM(SGST_AMT) SGST_AMT, "
				+ " SUM(TAXABLE_VALUE) TAXABLE_VALUE, "
				+ " SUM(TAX_PAYABLE) TAX_PAYABLE, " + " SUM(CESS) CESS "
				+ " FROM " + " (SELECT ENTITY_ID,SUPPLIER_GSTIN , "
				+ " COUNT(DISTINCT HD.ID) AS RECORD_COUNT, "
				+ " IFNULL(SUM(DOC_AMT), 0) AS DOC_AMT, "
				+ " IFNULL(SUM(IGST_AMT), 0) AS IGST_AMT, "
				+ " IFNULL(SUM(CGST_AMT), 0) AS CGST_AMT, "
				+ " IFNULL(SUM(SGST_AMT), 0) AS SGST_AMT, "
				+ " IFNULL(SUM(TAXABLE_VALUE), 0) AS TAXABLE_VALUE, "
				+ " SUM(IFNULL(IGST_AMT, 0)+IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT_SPECIFIC, 0)+ IFNULL(CESS_AMT_ADVALOREM, 0)) AS TAX_PAYABLE, "
				+ " SUM(IFNULL(CESS_AMT_SPECIFIC, 0)+ IFNULL(CESS_AMT_ADVALOREM, 0)) AS CESS "
				+ " FROM ANX_OUTWARD_DOC_HEADER HD " + "  "
				+ " INNER JOIN GSTIN_INFO GI ON GI.GSTIN=HD.SUPPLIER_GSTIN "
				+ " AND GI.IS_DELETE=FALSE " + " WHERE GI.ENTITY_ID=:entityId "
				+ "  " + "  " + " AND ASP_INVOICE_STATUS = 2 "
				+ " AND COMPLIANCE_APPLICABLE = TRUE "
				+ " AND HD.IS_DELETE = FALSE " + " AND SUPPLY_TYPE <> 'CAN' "
				+ " AND RETURN_TYPE='GSTR1' " + " AND TAX_DOC_TYPE IN ('B2B', "
				+ " 'B2BA', " + " 'B2CL', " + " 'B2CLA', " + " 'EXPORTS', "
				+ " 'EXPORTS-A') " + " AND DERIVED_RET_PERIOD = :taxPeriod "
				+ " GROUP BY ENTITY_ID,SUPPLIER_GSTIN) "
				+ " GROUP BY ENTITY_ID,SUPPLIER_GSTIN " + "  " + " UNION ALL  "
				+ " SELECT ENTITY_ID,SUPPLIER_GSTIN, "
				+ " COUNT(DISTINCT KEY) RECORD_COUNT, "
				+ " SUM(DOC_AMT) DOC_AMT, " + " SUM(IGST_AMT) IGST_AMT, "
				+ " SUM(CGST_AMT)CGST_AMT, " + " SUM(SGST_AMT)SGST_AMT, "
				+ " SUM(TAXABLE_VALUE)TAXABLE_VALUE, "
				+ " SUM(TAX_PAYABLE) TAX_PAYABLE, " + " SUM(CESS) CESS "
				+ " FROM "
				+ " (SELECT ENTITY_ID,SUPPLIER_GSTIN,HDR.TAX_DOC_TYPE AS TAX_DOC_TYPE, (IFNULL(SUPPLIER_GSTIN, '') ||'|'|| IFNULL(HDR.RETURN_PERIOD, '') "
				+ " ||'|'||IFNULL(HDR.DIFF_PERCENT, '') ||'|'||IFNULL(HDR.POS, 9999)||'|'||IFNULL(HDR.ECOM_GSTIN, '') ||'|'||IFNULL(TAX_RATE, 9999)) KEY, "
				+ " 																																					  "
				+ " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('INV') THEN ONB_LINE_ITEM_AMT "
				+ " END), 0)+ IFNULL(SUM(CASE "
				+ " 	   WHEN DOC_TYPE IN ('DR') THEN ONB_LINE_ITEM_AMT "
				+ "    END), 0)- IFNULL(SUM(CASE "
				+ " 							WHEN DOC_TYPE IN ('CR') THEN ONB_LINE_ITEM_AMT "
				+ " 						END), 0) AS DOC_AMT, "
				+ " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('INV') THEN ITM.IGST_AMT "
				+ " END), 0)+ IFNULL(SUM(CASE "
				+ " 	   WHEN DOC_TYPE IN ('DR') THEN ITM.IGST_AMT "
				+ "    END), 0)- IFNULL(SUM(CASE "
				+ " 							WHEN DOC_TYPE IN ('CR') THEN ITM.IGST_AMT "
				+ " 						END), 0) AS IGST_AMT, "
				+ " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('INV') THEN ITM.CGST_AMT "
				+ " END), 0)+ IFNULL(SUM(CASE "
				+ " 	   WHEN DOC_TYPE IN ('DR') THEN ITM.CGST_AMT "
				+ "    END), 0)- IFNULL(SUM(CASE "
				+ " 							WHEN DOC_TYPE IN ('CR') THEN ITM.CGST_AMT "
				+ " 						END), 0) AS CGST_AMT, "
				+ " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('INV') THEN ITM.SGST_AMT "
				+ " END), 0)+ IFNULL(SUM(CASE "
				+ " 	   WHEN DOC_TYPE IN ('DR') THEN ITM.SGST_AMT "
				+ "    END), 0)- IFNULL(SUM(CASE "
				+ " 							WHEN DOC_TYPE IN ('CR') THEN ITM.SGST_AMT "
				+ " 						END), 0) AS SGST_AMT, "
				+ " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('INV') THEN ITM.TAXABLE_VALUE "
				+ " END), 0)+ IFNULL(SUM(CASE "
				+ " 	   WHEN DOC_TYPE IN ('DR') THEN ITM.TAXABLE_VALUE "
				+ "    END), 0)- IFNULL(SUM(CASE "
				+ " 							WHEN DOC_TYPE IN ('CR') THEN ITM.TAXABLE_VALUE "
				+ " 						END), 0) AS TAXABLE_VALUE, "
				+ " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('INV') THEN IFNULL(ITM.IGST_AMT, 0)+IFNULL(ITM.CGST_AMT, 0)+IFNULL(ITM.SGST_AMT, 0)+ IFNULL(ITM.CESS_AMT_SPECIFIC, "
				+ " 0)+IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
				+ " END), 0)+ IFNULL(SUM(CASE "
				+ " 	   WHEN DOC_TYPE IN ('DR') THEN IFNULL(ITM.IGST_AMT, 0)+IFNULL(ITM.CGST_AMT, 0)+IFNULL(ITM.SGST_AMT, 0)+ IFNULL(ITM.CESS_AMT_SPECIFIC, "
				+ " 0)+IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
				+ "    END), 0)- IFNULL(SUM(CASE "
				+ " 							WHEN DOC_TYPE IN ('CR') THEN IFNULL(ITM.IGST_AMT, 0)+IFNULL(ITM.CGST_AMT, 0)+IFNULL(ITM.SGST_AMT, 0)+ "
				+ " IFNULL(ITM.CESS_AMT_SPECIFIC, 0)+IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
				+ " 						END), 0) AS TAX_PAYABLE, "
				+ " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('INV') THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0)+IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
				+ " END), 0)+ IFNULL(SUM(CASE "
				+ " 	   WHEN DOC_TYPE IN ('DR') THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0)+IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
				+ "    END), 0)- IFNULL(SUM(CASE "
				+ " 							WHEN DOC_TYPE IN ('CR') THEN IFNULL(ITM.CESS_AMT_SPECIFIC, 0)+IFNULL(ITM.CESS_AMT_ADVALOREM, 0) "
				+ " 						END), 0) AS CESS "
				+ " FROM ANX_OUTWARD_DOC_HEADER HDR "
				+ " INNER JOIN ANX_OUTWARD_DOC_ITEM ITM ON HDR.ID=ITM.DOC_HEADER_ID "
				+ " INNER JOIN GSTIN_INFO GI ON GI.GSTIN=HDR.SUPPLIER_GSTIN "
				+ " AND GI.IS_DELETE=FALSE "
				+ " WHERE GI.ENTITY_ID=:entityId   "
				+ " AND ASP_INVOICE_STATUS = 2  " + " AND RETURN_TYPE='GSTR1' "
				+ " AND HDR.IS_DELETE = FALSE "
				+ " AND HDR.SUPPLY_TYPE <> 'CAN' "
				+ " AND HDR.TAX_DOC_TYPE IN ('B2CS') "
				+ " AND HDR.DERIVED_RET_PERIOD  = :taxPeriod "
				+ " GROUP BY ENTITY_ID, " + " SUPPLIER_GSTIN, "
				+ " HDR.TAX_DOC_TYPE, " + " SUPPLIER_GSTIN, "
				+ " HDR.RETURN_PERIOD, " + " HDR.POS, " + " HDR.ECOM_GSTIN, "
				+ " HDR.DIFF_PERCENT, " + " ITM.TAX_RATE " + "  "
				+ " UNION ALL  "
				+ " SELECT ENTITY_ID,SUPPLIER_GSTIN,'B2CS' AS TAX_DOC_TYPE, (IFNULL(SUPPLIER_GSTIN, '') ||'|'|| IFNULL(RETURN_PERIOD, '') ||'|'||IFNULL(TRAN_TYPE, '') "
				+ " ||'|'||IFNULL(NEW_POS, 9999)||'|'||IFNULL(NEW_ECOM_GSTIN, '') ||'|'||IFNULL(NEW_RATE, 9999)) KEY, "
				+ " SUM(IFNULL(NEW_TAXABLE_VALUE, 0)+IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT, 0)) AS DOC_AMT, "
				+ " IFNULL(SUM(IGST_AMT), 0) AS IGST_AMT, "
				+ " IFNULL(SUM(CGST_AMT), 0) AS CGST_AMT, "
				+ " IFNULL(SUM(SGST_AMT), 0) AS SGST_AMT, "
				+ " IFNULL(SUM(NEW_TAXABLE_VALUE), 0) AS TAXABLE_VALUE, "
				+ " SUM(IFNULL(IGST_AMT, 0)+IFNULL(CGST_AMT, 0)+IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT, 0)) AS TAX_PAYABLE, "
				+ " IFNULL(SUM(CESS_AMT), 0) AS CESS "
				+ " FROM GSTR1_PROCESSED_B2CS B2CS "
				+ " INNER JOIN GSTIN_INFO GI ON GI.GSTIN=B2CS.SUPPLIER_GSTIN "
				+ " AND GI.IS_DELETE=FALSE " + " WHERE GI.ENTITY_ID=:entityId "
				+ " AND B2CS.IS_DELETE = FALSE " + " AND IS_AMENDMENT=FALSE "
				+ " AND DERIVED_RET_PERIOD = :taxPeriod "
				+ " GROUP BY ENTITY_ID,SUPPLIER_GSTIN, " + " RETURN_PERIOD, "
				+ " TRAN_TYPE, " + " NEW_POS, " + " NEW_ECOM_GSTIN, "
				+ " NEW_RATE) " + " GROUP BY ENTITY_ID,SUPPLIER_GSTIN, "
				+ " KEY, " + " TAX_DOC_TYPE " + "  " + " UNION ALL  "
				+ " SELECT ENTITY_ID,SUPPLIER_GSTIN, "
				+ " COUNT(*) RECORD_COUNT, " + " SUM(DOC_AMT) DOC_AMT, "
				+ " SUM(IGST_AMT) IGST_AMT, " + " SUM(CGST_AMT) CGST_AMT, "
				+ " SUM(SGST_AMT) SGST_AMT, "
				+ " SUM(TAXABLE_VALUE) TAXABLE_VALUE, "
				+ " SUM(TAX_PAYABLE) TAX_PAYABLE, " + " SUM(CESS) CESS "
				+ " FROM "
				+ " (SELECT ENTITY_ID,SUPPLIER_GSTIN,'B2CSA' AS TAX_DOC_TYPE, "
				+ " SUM(IFNULL(NEW_TAXABLE_VALUE, 0)+IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT, 0)) AS DOC_AMT, "
				+ " IFNULL(SUM(IGST_AMT), 0) AS IGST_AMT, "
				+ " IFNULL(SUM(CGST_AMT), 0) AS CGST_AMT, "
				+ " IFNULL(SUM(SGST_AMT), 0) AS SGST_AMT, "
				+ " IFNULL(SUM(NEW_TAXABLE_VALUE), 0) AS TAXABLE_VALUE, "
				+ " SUM(IFNULL(IGST_AMT, 0)+IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT, 0)) AS TAX_PAYABLE, "
				+ " IFNULL(SUM(CESS_AMT), 0) AS CESS "
				+ " FROM GSTR1_PROCESSED_B2CS B2C "
				+ " INNER JOIN GSTIN_INFO GI ON GI.GSTIN=B2C.SUPPLIER_GSTIN "
				+ " AND GI.IS_DELETE=FALSE " + " WHERE GI.ENTITY_ID=:entityId "
				+ " AND B2C.IS_DELETE = FALSE " + " AND IS_AMENDMENT=TRUE "
				+ " AND DERIVED_RET_PERIOD = :taxPeriod "
				+ " GROUP BY ENTITY_ID,SUPPLIER_GSTIN, " + " RETURN_PERIOD, "
				+ " TRAN_TYPE, " + " MONTH, " + " NEW_POS, "
				+ " NEW_ECOM_GSTIN) " + " GROUP BY ENTITY_ID,SUPPLIER_GSTIN "
				+ "  " + "  " + " UNION ALL SELECT ENTITY_ID,SUPPLIER_GSTIN, "
				+ " SUM(RECORD_COUNT) RECORD_COUNT, "
				+ " SUM(DOC_AMT) DOC_AMT, " + " SUM(IGST_AMT) IGST_AMT, "
				+ " SUM(CGST_AMT) CGST_AMT, " + " SUM(SGST_AMT) SGST_AMT, "
				+ " SUM(TAXABLE_VALUE) TAXABLE_VALUE, "
				+ " SUM(TAX_PAYABLE) TAX_PAYABLE, " + " SUM(CESS) CESS "
				+ " FROM " + " (SELECT ENTITY_ID,SUPPLIER_GSTIN,TAX_DOC_TYPE, "
				+ " COUNT(*) AS RECORD_COUNT, " + " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('DR', 'RDR') THEN DOC_AMT "
				+ " END), 0)- IFNULL(SUM(CASE "
				+ " 			 WHEN DOC_TYPE IN ('CR', 'RCR') THEN DOC_AMT "
				+ " 		 END), 0) AS DOC_AMT, " + " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('DR', 'RDR') THEN IGST_AMT "
				+ " END), 0)- IFNULL(SUM(CASE "
				+ " 			 WHEN DOC_TYPE IN ('CR', 'RCR') THEN IGST_AMT "
				+ " 		 END), 0) AS IGST_AMT, " + " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('DR', 'RDR') THEN CGST_AMT "
				+ " END), 0)- IFNULL(SUM(CASE "
				+ " 			 WHEN DOC_TYPE IN ('CR', 'RCR') THEN CGST_AMT "
				+ " 		 END), 0) AS CGST_AMT, " + " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('DR', 'RDR') THEN SGST_AMT "
				+ " END), 0)- IFNULL(SUM(CASE "
				+ " 			 WHEN DOC_TYPE IN ('CR', 'RCR') THEN SGST_AMT "
				+ " 		 END), 0) AS SGST_AMT, " + " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('DR', 'RDR') THEN TAXABLE_VALUE "
				+ " END), 0)- IFNULL(SUM(CASE "
				+ " 			 WHEN DOC_TYPE IN ('CR', 'RCR') THEN TAXABLE_VALUE "
				+ " 		 END), 0) AS TAXABLE_VALUE, " + " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('DR', 'RDR') THEN IFNULL(IGST_AMT, 0)+IFNULL(CGST_AMT, 0)+IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT_SPECIFIC, "
				+ " 0)+IFNULL(CESS_AMT_ADVALOREM, 0) "
				+ " END), 0)- IFNULL(SUM(CASE "
				+ " 			 WHEN DOC_TYPE IN ('CR', 'RCR') THEN IFNULL(IGST_AMT, 0)+IFNULL(CGST_AMT, 0)+IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT_SPECIFIC, "
				+ " 0)+IFNULL(CESS_AMT_ADVALOREM, 0) "
				+ " 		 END), 0) AS TAX_PAYABLE, " + " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('DR', 'RDR') THEN IFNULL(CESS_AMT_SPECIFIC, 0)+IFNULL(CESS_AMT_ADVALOREM, 0) "
				+ " END), 0)- IFNULL(SUM(CASE "
				+ " 			 WHEN DOC_TYPE IN ('CR', 'RCR') THEN IFNULL(CESS_AMT_SPECIFIC, 0)+IFNULL(CESS_AMT_ADVALOREM, 0) "
				+ " 		 END), 0) AS CESS "
				+ " FROM ANX_OUTWARD_DOC_HEADER HD "
				+ " INNER JOIN GSTIN_INFO GI ON GI.GSTIN=HD.SUPPLIER_GSTIN "
				+ " AND GI.IS_DELETE=FALSE "
				+ " WHERE GI.ENTITY_ID=:entityId	   "
				+ " AND ASP_INVOICE_STATUS = 2 "
				+ " AND COMPLIANCE_APPLICABLE = TRUE "
				+ " AND HD.IS_DELETE = FALSE " + " AND SUPPLY_TYPE <> 'CAN' "
				+ " AND RETURN_TYPE='GSTR1' " + " AND TAX_DOC_TYPE IN ('CDNR', "
				+ " 'CDNRA', " + " 'CDNUR', " + " 'CDNUR-EXPORTS', "
				+ " 'CDNUR-B2CL', " + " 'CDNURA') " + "  "
				+ " AND DERIVED_RET_PERIOD =  :taxPeriod "
				+ " GROUP BY ENTITY_ID,SUPPLIER_GSTIN,TAX_DOC_TYPE) "
				+ " GROUP BY ENTITY_ID,SUPPLIER_GSTIN) "
				+ " GROUP BY ENTITY_ID,SUPPLIER_GSTIN  " + "  " + "  "
				+ " UNION ALL " + "  " + "  " + "  "
				+ " SELECT ENTITY_ID,SUPPLIER_GSTIN, " + " SUM(IGST) IGST, "
				+ " SUM(CGST) CGST, " + " SUM(SGST) SGST, "
				+ " SUM(CESS) CESS, " + " SUM(TOTAL_TAX) TOTAL_TAX, "
				+ " SUM(GROSS_ADVANCE) TAXABLE_VALUE " + " FROM "
				+ " (SELECT ENTITY_ID,SUPPLIER_GSTIN,TAX_DOC_TYPE, "
				+ " DOC_TYPE, " + " COUNT(*) RECORD_COUNT, "
				+ " SUM(TOTAL_VALUE) TOTAL_VALUE, "
				+ " SUM(GROSS_ADVANCE) GROSS_ADVANCE, "
				+ " SUM(TOTAL_TAX) TOTAL_TAX, " + " SUM(IGST) IGST, "
				+ " SUM(CGST) CGST, " + " SUM(SGST) SGST, " + " SUM(CESS) CESS "
				+ " FROM "
				+ " (SELECT ENTITY_ID,SUPPLIER_GSTIN,'ADV REC' AS TAX_DOC_TYPE, "
				+ " 'RV' AS DOC_TYPE, "
				+ " SUM(IFNULL(NEW_GROSS_ADV_RECEIVED, 0)+IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT, 0)) AS TOTAL_VALUE, "
				+ " IFNULL(SUM(NEW_GROSS_ADV_RECEIVED), 0) AS GROSS_ADVANCE, "
				+ " SUM(IFNULL(IGST_AMT, 0)+IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT, 0)) AS TOTAL_TAX, "
				+ " IFNULL(SUM(IGST_AMT), 0) AS IGST, "
				+ " IFNULL(SUM(CGST_AMT), 0) AS CGST, "
				+ " IFNULL(SUM(SGST_AMT), 0) AS SGST, "
				+ " IFNULL(SUM(CESS_AMT), 0) AS CESS "
				+ " FROM GSTR1_PROCESSED_ADV_RECEIVED HD "
				+ " INNER JOIN GSTIN_INFO GI ON GI.GSTIN=HD.SUPPLIER_GSTIN "
				+ " AND GI.IS_DELETE=FALSE " + " WHERE GI.ENTITY_ID=:entityId "
				+ " AND HD.IS_DELETE = FALSE " + " AND IS_AMENDMENT=FALSE "
				+ "  " + " AND DERIVED_RET_PERIOD = :taxPeriod "
				+ " GROUP BY ENTITY_ID,SUPPLIER_GSTIN, "
				+ " RETURN_PERIOD, (CASE " + " WHEN TRAN_TYPE IN ('ZL65', "
				+ " 				  'L65', " + " 				  'zl65', "
				+ " 				  'l65', " + " 				  'zL65', "
				+ " 				  'Zl65') THEN 'L65' "
				+ " WHEN (TRAN_TYPE IN ('Z', " + " 				   'N', "
				+ " 				   '', " + " 				   'z', "
				+ " 				   'n') "
				+ " 	 OR TRAN_TYPE IS NULL) THEN 'N' " + " END),NEW_POS) "
				+ " GROUP BY ENTITY_ID,SUPPLIER_GSTIN,TAX_DOC_TYPE, "
				+ " DOC_TYPE " + "  " + "  "
				+ " UNION ALL SELECT ENTITY_ID,SUPPLIER_GSTIN,TAX_DOC_TYPE, "
				+ " DOC_TYPE, " + " COUNT(*) RECORD_COUNT, "
				+ " SUM(TOTAL_VALUE) TOTAL_VALUE, "
				+ " SUM(GROSS_ADVANCE) GROSS_ADVANCE, "
				+ " SUM(TOTAL_TAX) TOTAL_TAX, " + " SUM(IGST) IGST, "
				+ " SUM(CGST) CGST, " + " SUM(SGST) SGST, " + " SUM(CESS) CESS "
				+ " FROM "
				+ " (SELECT ENTITY_ID,SUPPLIER_GSTIN,'ADV REC-A' AS TAX_DOC_TYPE, "
				+ " 'RRV' AS DOC_TYPE, "
				+ " SUM(IFNULL(NEW_GROSS_ADV_RECEIVED, 0)+IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT, 0)) AS TOTAL_VALUE, "
				+ " IFNULL(SUM(NEW_GROSS_ADV_RECEIVED), 0) AS GROSS_ADVANCE, "
				+ " SUM(IFNULL(IGST_AMT, 0)+IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT, 0)) AS TOTAL_TAX, "
				+ " IFNULL(SUM(IGST_AMT), 0) AS IGST, "
				+ " IFNULL(SUM(CGST_AMT), 0) AS CGST, "
				+ " IFNULL(SUM(SGST_AMT), 0) AS SGST, "
				+ " IFNULL(SUM(CESS_AMT), 0) AS CESS "
				+ " FROM GSTR1_PROCESSED_ADV_RECEIVED REC "
				+ " INNER JOIN GSTIN_INFO GI ON GI.GSTIN=REC.SUPPLIER_GSTIN "
				+ " AND GI.IS_DELETE=FALSE " + " WHERE GI.ENTITY_ID=:entityId "
				+ " AND REC.IS_DELETE = FALSE " + " AND IS_AMENDMENT=TRUE "
				+ "  " + " AND DERIVED_RET_PERIOD = :taxPeriod "
				+ " GROUP BY ENTITY_ID,SUPPLIER_GSTIN, "
				+ " RETURN_PERIOD, (CASE " + " WHEN TRAN_TYPE IN ('ZL65', "
				+ " 				  'L65', " + " 				  'zl65', "
				+ " 				  'l65', " + " 				  'zL65', "
				+ " 				  'Zl65') THEN 'L65' "
				+ " WHEN (TRAN_TYPE IN ('Z', " + " 				   'N', "
				+ " 				   '', " + " 				   'z', "
				+ " 				   'n') "
				+ " 	 OR TRAN_TYPE IS NULL) THEN 'N' " + " END), MONTH, "
				+ "  NEW_POS) "
				+ " GROUP BY ENTITY_ID,SUPPLIER_GSTIN,TAX_DOC_TYPE, "
				+ " DOC_TYPE " + "  " + "  "
				+ " UNION ALL SELECT ENTITY_ID,SUPPLIER_GSTIN,TAX_DOC_TYPE, "
				+ " DOC_TYPE, " + " COUNT(*) RECORD_COUNT, "
				+ " SUM(TOTAL_VALUE)*-1 TOTAL_VALUE, "
				+ " SUM(GROSS_ADVANCE)*-1 GROSS_ADVANCE, "
				+ " SUM(TOTAL_TAX)*-1 TOTAL_TAX, " + " SUM(IGST)*-1 IGST, "
				+ " SUM(CGST)*-1 CGST, " + " SUM(SGST)*-1 SGST, "
				+ " SUM(CESS)*-1 CESS " + " FROM "
				+ " (SELECT ENTITY_ID,SUPPLIER_GSTIN,'ADV ADJ' AS TAX_DOC_TYPE, "
				+ " 'AV' AS DOC_TYPE, "
				+ " SUM(IFNULL(NEW_GROSS_ADV_ADJUSTED, 0)+IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+IFNULL(SGST_AMT, 0)+ IFNULL(CESS_AMT, 0)) AS TOTAL_VALUE, "
				+ " IFNULL(SUM(NEW_GROSS_ADV_ADJUSTED), 0) AS GROSS_ADVANCE, "
				+ " SUM(IFNULL(IGST_AMT, 0)+IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT, 0)) AS TOTAL_TAX, "
				+ " IFNULL(SUM(IGST_AMT), 0) AS IGST, "
				+ " IFNULL(SUM(CGST_AMT), 0) AS CGST, "
				+ " IFNULL(SUM(SGST_AMT), 0) AS SGST, "
				+ " IFNULL(SUM(CESS_AMT), 0) AS CESS "
				+ " FROM GSTR1_PROCESSED_ADV_ADJUSTMENT ADJ "
				+ " INNER JOIN GSTIN_INFO GI ON GI.GSTIN=ADJ.SUPPLIER_GSTIN "
				+ " AND GI.IS_DELETE=FALSE " + " WHERE GI.ENTITY_ID=:entityId "
				+ " AND ADJ.IS_DELETE = FALSE " + " AND IS_AMENDMENT=FALSE "
				+ "  " + " AND DERIVED_RET_PERIOD =:taxPeriod "
				+ " GROUP BY ENTITY_ID,SUPPLIER_GSTIN, "
				+ " RETURN_PERIOD, (CASE " + " WHEN TRAN_TYPE IN ('ZL65', "
				+ " 				  'L65', " + " 				  'zl65', "
				+ " 				  'l65', " + " 				  'zL65', "
				+ " 				  'Zl65') THEN 'L65' "
				+ " WHEN (TRAN_TYPE IN ('Z', " + " 				   'N', "
				+ " 				   '', " + " 				   'z', "
				+ " 				   'n') "
				+ " 	 OR TRAN_TYPE IS NULL) THEN 'N' " + " END),NEW_POS) "
				+ " GROUP BY ENTITY_ID,SUPPLIER_GSTIN,TAX_DOC_TYPE, "
				+ " DOC_TYPE " + "  "
				+ " UNION ALL SELECT ENTITY_ID,SUPPLIER_GSTIN,TAX_DOC_TYPE, "
				+ " DOC_TYPE, " + " COUNT(*) RECORD_COUNT, "
				+ " SUM(TOTAL_VALUE) TOTAL_VALUE, "
				+ " SUM(GROSS_ADVANCE) GROSS_ADVANCE, "
				+ " SUM(TOTAL_TAX) TOTAL_TAX, " + " SUM(IGST) IGST, "
				+ " SUM(CGST) CGST, " + " SUM(SGST) SGST, " + " SUM(CESS) CESS "
				+ " FROM "
				+ " (SELECT ENTITY_ID,SUPPLIER_GSTIN,'ADV ADJ-A' AS TAX_DOC_TYPE, "
				+ " 'RAV' AS DOC_TYPE, "
				+ " SUM(IFNULL(NEW_GROSS_ADV_ADJUSTED, 0)+IFNULL(IGST_AMT, 0)+ IFNULL(CGST_AMT, 0)+IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT, 0)) AS TOTAL_VALUE, "
				+ " IFNULL(SUM(NEW_GROSS_ADV_ADJUSTED), 0) AS GROSS_ADVANCE, "
				+ " SUM(IFNULL(IGST_AMT, 0)+IFNULL(CGST_AMT, 0)+ IFNULL(SGST_AMT, 0)+IFNULL(CESS_AMT, 0)) AS TOTAL_TAX, "
				+ " IFNULL(SUM(IGST_AMT), 0) AS IGST, "
				+ " IFNULL(SUM(CGST_AMT), 0) AS CGST, "
				+ " IFNULL(SUM(SGST_AMT), 0) AS SGST, "
				+ " IFNULL(SUM(CESS_AMT), 0) AS CESS "
				+ " FROM GSTR1_PROCESSED_ADV_ADJUSTMENT ADV   "
				+ " INNER JOIN GSTIN_INFO GI ON GI.GSTIN=ADV.SUPPLIER_GSTIN "
				+ " AND GI.IS_DELETE=FALSE " + " WHERE GI.ENTITY_ID=:entityId "
				+ " AND ADV.IS_DELETE = FALSE " + " AND IS_AMENDMENT=TRUE "
				+ " AND DERIVED_RET_PERIOD = :taxPeriod "
				+ " GROUP BY ENTITY_ID,SUPPLIER_GSTIN, "
				+ " RETURN_PERIOD, (CASE " + " WHEN TRAN_TYPE IN ('ZL65', "
				+ " 				  'L65', " + " 				  'zl65', "
				+ " 				  'l65', " + " 				  'zL65', "
				+ " 				  'Zl65') THEN 'L65' "
				+ " WHEN (TRAN_TYPE IN ('Z', " + " 				   'N', "
				+ " 				   '', " + " 				   'z', "
				+ " 				   'n') "
				+ " 	 OR TRAN_TYPE IS NULL) THEN 'N' " + " END),MONTH, "
				+ " NEW_POS) "
				+ " GROUP BY ENTITY_ID,SUPPLIER_GSTIN,TAX_DOC_TYPE, "
				+ " DOC_TYPE) " + " GROUP BY ENTITY_ID,SUPPLIER_GSTIN " + "  "
				+ "  " + " UNION ALL " + "  "
				+ " SELECT ENTITY_ID,SUPPLIER_GSTIN, " + " 0 AS  IGST, "
				+ " 0 AS CGST, " + " 0 AS SGST, " + " 0 AS CESS, "
				+ " 0 AS TOTAL_TAX, "
				+ " SUM(IFNULL(NIL_RATED_SUPPLIES,0)+IFNULL(EXMPTED_SUPPLIES,0) +IFNULL(NON_GST_SUPPLIES,0)) AS TAXABLE_VALUE "
				+ " FROM " + " (SELECT ENTITY_ID,(CASE "
				+ " WHEN TABLE_SECTION='8A' THEN 'Inter-State Supplies to Registered Person' "
				+ " WHEN TABLE_SECTION='8B' THEN 'Intra-State Supplies to Registered Person' "
				+ " WHEN TABLE_SECTION='8C' THEN 'Inter-State Supplies to UnRegistered Person' "
				+ " WHEN TABLE_SECTION='8D' THEN 'Intra-State Supplies to UnRegistered Person' "
				+ " END) DESCRIPTION, "
				+ " (SUPPLIER_GSTIN||'|'||RETURN_PERIOD||'|'||TABLE_SECTION) AS DOC_KEY, "
				+ " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('INV', 'DR', 'BOS') "
				+ " AND SUPPLY_TYPE='NIL' THEN TAXABLE_VALUE "
				+ " END), 0) - IFNULL(SUM(CASE "
				+ " 		   WHEN DOC_TYPE='CR' "
				+ " 				AND SUPPLY_TYPE='NIL' THEN TAXABLE_VALUE "
				+ " 	   END), 0) AS NIL_RATED_SUPPLIES, "
				+ " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('INV', 'DR', 'BOS') "
				+ " AND SUPPLY_TYPE='EXT' THEN TAXABLE_VALUE "
				+ " END), 0) - IFNULL(SUM(CASE "
				+ " 		   WHEN DOC_TYPE='CR' "
				+ " 				AND SUPPLY_TYPE='EXT' THEN TAXABLE_VALUE "
				+ " 	   END), 0) AS EXMPTED_SUPPLIES, " + " IFNULL(SUM(CASE "
				+ " WHEN DOC_TYPE IN ('INV', 'DR', 'BOS') "
				+ " AND SUPPLY_TYPE IN ('NON', 'SCH3') THEN TAXABLE_VALUE "
				+ " END), 0) - IFNULL(SUM(CASE "
				+ " 		   WHEN DOC_TYPE='CR' "
				+ " 				AND SUPPLY_TYPE IN ('NON', 'SCH3') THEN TAXABLE_VALUE "
				+ " 	   END), 0) AS NON_GST_SUPPLIES, " + " RETURN_PERIOD, "
				+ " DERIVED_RET_PERIOD, " + " SUPPLIER_GSTIN, "
				+ " TABLE_SECTION AS DESCRIPTION_KEY, " + " COUNT(HD.ID) CNT "
				+ " FROM ANX_OUTWARD_DOC_HEADER HD "
				+ " INNER JOIN GSTIN_INFO GI ON GI.GSTIN=HD.SUPPLIER_GSTIN "
				+ " AND GI.IS_DELETE=FALSE " + " WHERE GI.ENTITY_ID=:entityId  "
				+ " AND TAX_DOC_TYPE IN ('NILEXTNON') "
				+ " AND ASP_INVOICE_STATUS = 2 "
				+ " AND COMPLIANCE_APPLICABLE=TRUE "
				+ " AND HD.IS_DELETE = FALSE " + " AND SUPPLY_TYPE <> 'CAN' "
				+ " AND RETURN_TYPE='GSTR1' "
				+ " AND DERIVED_RET_PERIOD = :taxPeriod "
				+ " GROUP BY ENTITY_ID,RETURN_PERIOD, "
				+ " DERIVED_RET_PERIOD, " + " SUPPLIER_GSTIN, "
				+ " TABLE_SECTION  			 " + " UNION ALL  "
				+ " SELECT ENTITY_ID,'A' DESCRIPTION, " + " 'A' DOC_KEY, "
				+ " SUM(NIL_RATED_SUPPLIES) NIL_RATED_SUPPLIES, "
				+ " SUM(EXMPTED_SUPPLIES) EXMPTED_SUPPLIES, "
				+ " SUM(NON_GST_SUPPLIES) NON_GST_SUPPLIES, "
				+ " RETURN_PERIOD, " + " DERIVED_RET_PERIOD, "
				+ " SUPPLIER_GSTIN, " + " 'A' DESCRIPTION_KEY, "
				+ " COUNT(DISTINCT AS_PROCESSED_ID) CNT " + " FROM "
				+ " (SELECT ENTITY_ID,(CASE "
				+ " WHEN TABLE_SECTION='8A' THEN 'Inter-State Supplies to Registered Person' "
				+ " WHEN TABLE_SECTION='8B' THEN 'Intra-State Supplies to Registered Person' "
				+ " WHEN TABLE_SECTION='8C' THEN 'Inter-State Supplies to UnRegistered Person' "
				+ " WHEN TABLE_SECTION='8D' THEN 'Intra-State Supplies to UnRegistered Person' "
				+ " END) DESCRIPTION, "
				+ " (SUPPLIER_GSTIN||'|'||RETURN_PERIOD||'|'||TABLE_SECTION) AS DOC_KEY, "
				+ " CASE "
				+ " WHEN SUPPLY_TYPE='NIL' THEN IFNULL(TAXABLE_VALUE, 0) "
				+ " END AS NIL_RATED_SUPPLIES, " + " CASE "
				+ " WHEN SUPPLY_TYPE='EXT' THEN IFNULL(TAXABLE_VALUE, 0) "
				+ " END AS EXMPTED_SUPPLIES, " + " CASE "
				+ " WHEN SUPPLY_TYPE IN ('NON') THEN IFNULL(TAXABLE_VALUE, 0) "
				+ " END AS NON_GST_SUPPLIES, " + " RETURN_PERIOD, "
				+ " DERIVED_RET_PERIOD, " + " SUPPLIER_GSTIN, "
				+ " TABLE_SECTION AS DESCRIPTION_KEY, " + " AS_PROCESSED_ID "
				+ " FROM GSTR1_SUMMARY_NILEXTNON NIL "
				+ " INNER JOIN GSTIN_INFO GI ON GI.GSTIN=NIL.SUPPLIER_GSTIN "
				+ " AND GI.IS_DELETE=FALSE " + " WHERE GI.ENTITY_ID=:entityId "
				+ " AND NIL.IS_DELETE = FALSE "
				+ " AND DERIVED_RET_PERIOD = :taxPeriod " + " ) "
				+ " GROUP BY ENTITY_ID,RETURN_PERIOD, "
				+ " DERIVED_RET_PERIOD, " + " SUPPLIER_GSTIN) A "
				+ " INNER JOIN  " + " ( " + "  "
				+ " SELECT GSTIN_COLUMN	,DERIVED_RET_PERIOD	,IS_NIL_UI FROM  "
				+ " ( "
				+ " SELECT *,RANK() OVER(PARTITION BY GSTIN_COLUMN,DERIVED_RET_PERIOD ORDER BY ID DESC ) AS RANK "
				+ " FROM( "
				+ " SELECT ID,:GSTIN AS GSTIN_COLUMN,gst.DERIVED_RET_PERIOD,IS_NIL_UI   "
				+ " FROM GSTN_USER_REQUEST gst "
				+ " WHERE RETURN_TYPE='GSTR1'  " + " AND REQUEST_TYPE='SAVE' "
				+ " AND gst.DERIVED_RET_PERIOD =:taxPeriod "
				+ " AND GSTIN IN (:GSTIN) " + "  " + " ) " + " )WHERE RANK=1 "
				+ "  " + " ) UI " + " ON A.SUPPLIER_GSTIN =UI.GSTIN_COLUMN "
				+ " AND A.DERIVED_RET_PERIOD = :taxPeriod "
				+ " AND IS_NIL_UI=FALSE "
				+ " GROUP BY ENTITY_ID,SUPPLIER_GSTIN " + "  " + " UNION ALL "
				+ "  " + " SELECT ENTITY_ID,SUPPLIER_GSTIN, " + " 0 AS  IGST, "
				+ " 0 AS CGST, " + " 0 AS SGST, " + " 0 AS CESS, "
				+ " 0 AS TOTAL_TAX, "
				+ " SUM(IFNULL(NIL_RATED_SUPPLIES,0)+IFNULL(EXMPTED_SUPPLIES,0) +IFNULL(NON_GST_SUPPLIES,0)) AS TAXABLE_VALUE "
				+ " FROM "
				+ " (SELECT ENTITY_ID,SUPPLIER_GSTIN,IFNULL(SUM(NIL_RATED_SUPPLIES), 0) AS NIL_RATED_SUPPLIES, "
				+ " IFNULL(SUM(EXMPTED_SUPPLIES), 0) AS EXMPTED_SUPPLIES, "
				+ " IFNULL(SUM(NON_GST_SUPPLIES), 0) AS NON_GST_SUPPLIES, "
				+ " COUNT(CNT_ID) CNT " + " FROM " + " (SELECT ENTITY_ID,(CASE "
				+ " WHEN DESCRIPTION_KEY='8A' THEN 'Inter-State Supplies to Registered Person' "
				+ " WHEN DESCRIPTION_KEY='8B' THEN 'Intra-State Supplies to Registered Person' "
				+ " WHEN DESCRIPTION_KEY='8C' THEN 'Inter-State Supplies to UnRegistered Person' "
				+ " WHEN DESCRIPTION_KEY='8D' THEN 'Intra-State Supplies to UnRegistered Person' "
				+ " END) DESCRIPTION, " + " DOC_KEY, "
				+ " IFNULL(SUM(NIL_RATED_SUPPLIES), 0) NIL_RATED_SUPPLIES, "
				+ " IFNULL(SUM(EXMPTED_SUPPLIES), 0) EXMPTED_SUPPLIES, "
				+ " IFNULL(SUM(NON_GST_SUPPLIES), 0) NON_GST_SUPPLIES, "
				+ " RETURN_PERIOD, " + " DERIVED_RET_PERIOD, "
				+ " SUPPLIER_GSTIN, " + " DESCRIPTION_KEY, "
				+ " count(NIL2.ID) CNT_ID "
				+ " FROM GSTR1_USERINPUT_NILEXTNON NIL2 "
				+ " INNER JOIN GSTIN_INFO GI ON GI.GSTIN=NIL2.SUPPLIER_GSTIN "
				+ " AND GI.IS_DELETE=FALSE "
				+ " WHERE GI.ENTITY_ID=:entityId	   "
				+ " AND NIL2.IS_DELETE=FALSE "
				+ " AND DERIVED_RET_PERIOD =:taxPeriod "
				+ " GROUP BY ENTITY_ID,NIL2.ID, " + " RETURN_PERIOD, "
				+ " DESCRIPTION_KEY, " + " SUPPLIER_GSTIN, "
				+ " DERIVED_RET_PERIOD, " + " DOC_KEY) B " + " INNER JOIN "
				+ " (    "
				+ " SELECT GSTIN_COLUMN	,DERIVED_RET_PERIOD	,IS_NIL_UI FROM  "
				+ " ( "
				+ " SELECT *,RANK() OVER(PARTITION BY GSTIN_COLUMN,DERIVED_RET_PERIOD ORDER BY ID DESC ) AS RANK "
				+ " FROM( "
				+ " SELECT ID,:GSTIN AS GSTIN_COLUMN,gst.DERIVED_RET_PERIOD,IS_NIL_UI   "
				+ " FROM GSTN_USER_REQUEST gst "
				+ " WHERE RETURN_TYPE='GSTR1'  " + " AND REQUEST_TYPE='SAVE' "
				+ " AND gst.DERIVED_RET_PERIOD =:taxPeriod "
				+ " AND GSTIN IN (:GSTIN) " + " ) " + " )WHERE RANK=1 "
				+ " )UI " + " ON B.SUPPLIER_GSTIN =UI.GSTIN_COLUMN "
				+ " AND B.DERIVED_RET_PERIOD = :taxPeriod "
				+ " AND IS_NIL_UI=TRUE    "
				+ " group by ENTITY_ID,SUPPLIER_GSTIN) "
				+ " group by ENTITY_ID,SUPPLIER_GSTIN  " + "  " + " UNION ALL  "
				+ "  " + " SELECT ENTITY_ID,GI.GSTIN AS SUPPLIER_GSTIN , "
				+ " SUM(IGST_AMT) AS IGST, " + " SUM(CGST_AMT) AS CGST, "
				+ " SUM(SGST_AMT) AS SGST, " + " 0 		 AS CESS, "
				+ " SUM( IFNULL(CGST_AMT,0)    + IFNULL(IGST_AMT,0)    + IFNULL(SGST_AMT,0)  ) AS  TOTAL_TAX, "
				+ " SUM(NEW_GROSS_AMT ) AS TAXABLE_VALUE "
				+ " FROM GSTR7_PROCESSED_TDS TDS  "
				+ " INNER JOIN GSTIN_INFO GI ON TDS.TDS_DEDUCTOR_GSTIN=GI.GSTIN AND  TDS.is_delete=FALSE  AND REG_TYPE='TDS' AND TDS.TABLE_NUM='Table-3' "
				+ " AND TDS.DERIVED_RET_PERIOD    =:taxPeriod  "
				+ " WHERE GI.ENTITY_ID=:entityId " + " AND GI.IS_DELETE=FALSE "
				+ " GROUP BY ENTITY_ID,GI.GSTIN  " + "  " + "  " + " UNION ALL "
				+ " SELECT  ENTITY_ID,GI.GSTIN AS SUPPLIER_GSTIN,   " + "  "
				+ " SUM(IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.CGST_AMT_AS_IGST,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.CGST_AMT_AS_IGST,0)  "
				+ " ELSE 0  END,0)  "
				+ " + IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.IGST_AMT_AS_IGST,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.IGST_AMT_AS_IGST,0)  "
				+ " ELSE 0 END ,0)  "
				+ " + IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.SGST_AMT_AS_IGST,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.SGST_AMT_AS_IGST,0)  "
				+ " ELSE 0 END ,0)            " + " ) AS IGST, " + "  "
				+ " SUM(IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.CGST_AMT_AS_CGST,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.CGST_AMT_AS_CGST,0)  "
				+ " ELSE 0  END,0)  "
				+ " + IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.IGST_AMT_AS_CGST,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.IGST_AMT_AS_CGST,0)  "
				+ " ELSE 0 END ,0)  " + "  " + " ) AS CGST, " + "  " + "  "
				+ " SUM(IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.IGST_AMT_AS_SGST,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.IGST_AMT_AS_SGST,0)  "
				+ " ELSE 0  END,0)  "
				+ " + IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.SGST_AMT_AS_SGST,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.SGST_AMT_AS_SGST,0)  "
				+ " ELSE 0 END ,0)  " + "  " + " ) AS SGST, " + "  " + "  "
				+ " SUM(IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.CESS_AMT,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.CESS_AMT,0)  "
				+ " ELSE 0  END,0)            " + " ) AS CESS, " + "  " + "  "
				+ " SUM(IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.CESS_AMT,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.CESS_AMT,0)  "
				+ " ELSE 0  END,0)            "
				+ " +IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.IGST_AMT_AS_SGST,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.IGST_AMT_AS_SGST,0)  "
				+ " ELSE 0  END,0)  "
				+ " + IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.SGST_AMT_AS_SGST,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.SGST_AMT_AS_SGST,0)  "
				+ " ELSE 0 END ,0)  " + "  "
				+ " +IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.CGST_AMT_AS_CGST,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.CGST_AMT_AS_CGST,0)  "
				+ " ELSE 0  END,0)  "
				+ " + IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.IGST_AMT_AS_CGST,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.IGST_AMT_AS_CGST,0)  "
				+ " ELSE 0 END ,0)  " + "  "
				+ " +IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.CGST_AMT_AS_IGST,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.CGST_AMT_AS_IGST,0)  "
				+ " ELSE 0  END,0)  "
				+ " + IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.IGST_AMT_AS_IGST,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.IGST_AMT_AS_IGST,0)  "
				+ " ELSE 0 END ,0)  "
				+ " + IFNULL(CASE WHEN ISD.DOC_TYPE IN ('CR','RCR') THEN -IFNULL(ISD.SGST_AMT_AS_IGST,0)   "
				+ " WHEN ISD.DOC_TYPE IN ('INV','RNV','DR','RDR') THEN IFNULL(ISD.SGST_AMT_AS_IGST,0)  "
				+ " ELSE 0 END ,0)            " + " ) AS TOAL_TAX, " + "  "
				+ " 0 AS TAXABLE_VALUE " + " FROM  GSTR6_ISD_DISTRIBUTION ISD "
				+ " INNER JOIN GSTIN_INFO GI ON ISD.ISD_GSTIN=GI.GSTIN AND  ISD.IS_DELETE=FALSE  AND REG_TYPE='ISD' "
				+ " AND ISD.DERIVED_RET_PERIOD    =:taxPeriod  "
				+ " WHERE GI.ENTITY_ID=:entityId " + " AND GI.IS_DELETE=FALSE "
				+ " GROUP BY ENTITY_ID,GI.GSTIN  " + " )A "
				+ " GROUP BY ENTITY_ID " + "  " + "  " + " )G1 "
				+ " FULL OUTER JOIN " + " (SELECT GI.ENTITY_ID , "
				+ " SUM(IFNULL(G3.TAXABLE_VALUE, 0)) AS G3_TAXABLE_VALUE , "
				+ " SUM(IFNULL(G3.IGST, 0) + IFNULL(G3.CGST, 0) + IFNULL(G3.SGST, 0) + IFNULL(G3.CESS, 0)) AS G3_TOTAL_TAX "
				+ " FROM GSTR3B_ASP_USER G3 "
				+ " INNER JOIN GSTIN_INFO GI ON GI.GSTIN=G3.GSTIN "
				+ " AND G3.IS_ACTIVE=TRUE " + " AND GI.IS_DELETE=FALSE "
				+ " WHERE GI.ENTITY_ID=:entityId "
				+ " AND CONCAT(RIGHT(G3.TAX_PERIOD,4),LEFT(G3.TAX_PERIOD,2))=:taxPeriod "
				+ " AND G3.SECTION_NAME IN ('3.1(a)', " + " '3.1(b)', "
				+ " '3.1(c)', " + " '3.1(e)') "
				+ " GROUP BY GI.ENTITY_ID) G3 ON G1.ENTITY_ID=G3.ENTITY_ID";

		return query;
	}

	@Override
	public DashboardHOReconSummaryDto getDashBoardReconSummary(Long entityId,
			String taxPeriod) {
		try {

			/*
			 * String queryString = createDashBoardReconSummaryQuery(entityId,
			 * taxPeriod); Query q =
			 * entityManager.createNativeQuery(queryString);
			 * q.setParameter("entityId", entityId); q.setParameter("taxPeriod",
			 * taxPeriod);
			 */

			// applied spc
			LOGGER.info("Invoking  USP_RECON_SUMMARY Stored Proc");
			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery("USP_RECON_SUMMARY");

			storedProc.registerStoredProcedureParameter("VAR_ENTITY_ID",
					String.class, ParameterMode.IN);
			storedProc.registerStoredProcedureParameter("VAR_TAX_PERIOD",
					String.class, ParameterMode.IN);

			storedProc.setParameter("VAR_ENTITY_ID", entityId.toString());
			storedProc.setParameter("VAR_TAX_PERIOD", taxPeriod);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Executing USP_RECON_SUMMARY proc with  %s,"
								+ " entity Id %d," + " taxPeriod %s",
						"", entityId, taxPeriod);
				LOGGER.debug(msg);
			}

			Object[] dbObj = (Object[]) storedProc.getSingleResult();
			DashboardHOReconSummaryDto uiDto = converToReconSummaryDto(dbObj);
			return uiDto;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException(
					"Exception Occured in " + "DashboardReconDetailsDaoImpl");
		}
	}

	private DashboardHOReconSummaryDto converToReconSummaryDto(Object[] dbObj) {
		DashboardHOReconSummaryDto outDto = new DashboardHOReconSummaryDto();

		outDto.setExactMatch((BigDecimal) dbObj[0]);
		outDto.setMatchWithTolerance((BigDecimal) dbObj[1]);
		outDto.setValueMismatch((BigDecimal) dbObj[2]);
		outDto.setPosMismatch((BigDecimal) dbObj[3]);
		outDto.setDocumentDateMismatch((BigDecimal) dbObj[4]);
		outDto.setDocumentTypeMismatch((BigDecimal) dbObj[5]);
		outDto.setDocumentNumberMismatch1((BigDecimal) dbObj[6]);
		outDto.setMultiMismatch((BigDecimal) dbObj[7]);
		outDto.setPotential1((BigDecimal) dbObj[8]);
		outDto.setDocumentNumberMismatch2((BigDecimal) dbObj[9]);
		outDto.setDocumentNumberDocDateMismatch((BigDecimal) dbObj[10]);
		outDto.setPotential2((BigDecimal) dbObj[11]);
		outDto.setLogicalMatch((BigDecimal) dbObj[12]);
		outDto.setForcedMatch((BigDecimal) dbObj[13]);
		outDto.setAdditionalInPR((BigDecimal) dbObj[14]);
		outDto.setAdditionalIn2A((BigDecimal) dbObj[15]);
		outDto.setExactMatchImpg((BigDecimal) dbObj[16]);
		outDto.setMisMatchImpg((BigDecimal) dbObj[17]);
		outDto.setAdditionInPRImports((BigDecimal) dbObj[18]);
		outDto.setAdditionIn2AImports((BigDecimal) dbObj[19]);
		outDto.setReconPerformed(GenUtil.getBigInteger(dbObj[20]));
		outDto.setTotalGstns(GenUtil.getBigInteger(dbObj[21]));
		outDto.setTotalTaxPR((BigDecimal) dbObj[22]);
		outDto.setTotalTaxA2((BigDecimal) dbObj[23]);

		return outDto;
	}

	@Override
	public DashboardHOReconSummary2bprDto getDashBoardReconSummary2bpr(
			Long entityId, String taxPeriod) {
		try {

			LOGGER.info(
					"Invoking  USP_2BPR_RECON_SUMMARY_HOMESCREEN Stored Proc");
			StoredProcedureQuery storedProc = entityManager
					.createStoredProcedureQuery(
							"USP_2BPR_RECON_SUMMARY_HOMESCREEN");

			storedProc.registerStoredProcedureParameter("VAR_ENTITY_ID",
					String.class, ParameterMode.IN);
			storedProc.registerStoredProcedureParameter("VAR_TAX_PERIOD",
					String.class, ParameterMode.IN);

			storedProc.setParameter("VAR_ENTITY_ID", entityId.toString());
			storedProc.setParameter("VAR_TAX_PERIOD", taxPeriod);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Executing USP_2BPR_RECON_SUMMARY_HOMESCREEN proc with  %s,"
								+ " entity Id %d," + " taxPeriod %s",
						"", entityId, taxPeriod);
				LOGGER.debug(msg);
			}

			Object[] dbObj = (Object[]) storedProc.getSingleResult();
			DashboardHOReconSummary2bprDto uiDto = converToReconSummary2bprDto(
					dbObj);
			return uiDto;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException(
					"Exception Occured in " + "DashboardReconDetailsDaoImpl");
		}
	}

	private DashboardHOReconSummary2bprDto converToReconSummary2bprDto(
			Object[] dbObj) {
		DashboardHOReconSummary2bprDto outDto = new DashboardHOReconSummary2bprDto();

		outDto.setExactMatch((BigDecimal) dbObj[0]);
		outDto.setMatchWithTolerance((BigDecimal) dbObj[1]);
		outDto.setValueMismatch((BigDecimal) dbObj[2]);
		outDto.setPosMismatch((BigDecimal) dbObj[3]);
		outDto.setDocumentDateMismatch((BigDecimal) dbObj[4]);
		outDto.setDocumentTypeMismatch((BigDecimal) dbObj[5]);
		outDto.setDocumentNumberMismatch1((BigDecimal) dbObj[6]);
		outDto.setMultiMismatch((BigDecimal) dbObj[7]);
		outDto.setPotential1((BigDecimal) dbObj[8]);
		outDto.setDocumentNumberMismatch2((BigDecimal) dbObj[9]);
		outDto.setDocumentNumberDocDateMismatch((BigDecimal) dbObj[10]);
		outDto.setPotential2((BigDecimal) dbObj[11]);
		outDto.setLogicalMatch((BigDecimal) dbObj[12]);
		outDto.setForcedMatch((BigDecimal) dbObj[13]);
		outDto.setAdditionalInPR((BigDecimal) dbObj[14]);
		outDto.setAdditionalIn2B((BigDecimal) dbObj[15]);
		outDto.setExactMatchImpg((BigDecimal) dbObj[16]);
		outDto.setMisMatchImpg((BigDecimal) dbObj[17]);
		outDto.setAdditionInPRImports((BigDecimal) dbObj[18]);
		outDto.setAdditionIn2BImports((BigDecimal) dbObj[19]);
		outDto.setReconPerformed(GenUtil.getBigInteger(dbObj[20]));
		outDto.setTotalGstns(GenUtil.getBigInteger(dbObj[21]));
		outDto.setTotalTaxPR((BigDecimal) dbObj[22]);
		outDto.setTotalTaxA2((BigDecimal) dbObj[23]);

		return outDto;
	}
	
}
