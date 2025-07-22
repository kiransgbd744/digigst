package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ey.advisory.admin.data.repositories.client.GroupConfigPrmtRepository;
import com.ey.advisory.app.data.entities.client.BifurcationConstants;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.SaveToGstnQueryBuilder;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.SupplierGstinExtractorFromCriteria;
import com.ey.advisory.app.services.savetogstn.jobs.gstr3B.Gstr3BSaveToGstnQueryBuilderImpl;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.PerfUtil;
import com.ey.advisory.common.PerfamanceEventConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1SaveToGstnReqDto;
import com.google.common.base.Strings;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 * 
 *         This class is responsible for doing spring data jpa repository
 *         customization and to fetch the data by using JOIN Query to get the
 *         invoice rate level data along with invoice data.
 *
 */
@Slf4j
public class DocSearchForSaveCustomRepositoryImpl
		implements DocSearchForSaveCustomRepository {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("saveToGstnQueryBuilderImpl")
	private SaveToGstnQueryBuilder queryBuilder;

	@Autowired
	@Qualifier("supplierGstinExtractorFromCriteriaImpl")
	private SupplierGstinExtractorFromCriteria extractor;

	@Autowired
	@Qualifier("gstr3BSaveToGstnQueryBuilderImpl")
	private Gstr3BSaveToGstnQueryBuilderImpl gstr3BQueryBuilder;

	@Autowired
	@Qualifier("Gstr6DigiComputeRepository")
	private Gstr6DigiComputeRepository gstr6DigiComputeRepository;

	@Autowired
	@Qualifier("GroupConfigPrmtRepository")
	private GroupConfigPrmtRepository groupConfigPrmtRepository;

	@SuppressWarnings("unchecked")
	private List<Object[]> getGstr1CancelledResultSet(String sgstin,
			String retPeriod, String sql, String docType, List<Long> docIds) {
		List<Object[]> result = null;
		if (sql != null && sql.trim().length() > 0) {

			Query query = entityManager.createQuery(sql);
			/**
			 * setting the values based on condition the key values in the
			 * setParameter should be same as index names. index names are
			 * dynamically coming from the criteria.
			 */
			if (sgstin != null && retPeriod != null) {
				query.setParameter("c_Gstin", sgstin);
				query.setParameter("c_RetPeriod", retPeriod);
				query.setParameter("o_Gstin", sgstin);
				query.setParameter("o_RetPeriod", retPeriod);
			}
			if (docType != null) {
				query.setParameter("c_SupplyType", docType);
				query.setParameter("o_SupplyType", docType);
			}
			if (docIds != null && !docIds.isEmpty()) {
				query.setParameter("c_docIds", docIds);
			}

			result = query.getResultList();

		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> getGstr1HorizontalResultSet(String sgstin,
			String retPeriod, String sql, String docType, String tableType,
			Set<Long> orgDocIds, List<Long> docIds) {

		List<Object[]> result = null;
		if (sql != null && sql.trim().length() > 0) {

			Query query = entityManager.createQuery(sql);

			/**
			 * setting the values based on condition the key values in the
			 * setParameter should be same as index names. index names are
			 * dynamically coming from the criteria.
			 */
			if (sgstin != null && retPeriod != null) {
				query.setParameter("gstins", sgstin);
				query.setParameter("retPeriod", retPeriod);
			}
			if (docType != null && !docType.isEmpty()) {
				String[] array = null;
				if (docType.equalsIgnoreCase(GSTConstants.ECOM_SUP)) {
					array = docType.split(",");
				} else {
					array = docType.toUpperCase().split(",");
				}
				List<String> list = new ArrayList<>(Arrays.asList(array));
				query.setParameter("docType", list);
			}
		/*	if (tableType != null && !tableType.isEmpty()) {
				String[] array = tableType.split(",");
				List<String> list = new ArrayList<>(Arrays.asList(array));
				query.setParameter("tableType", list);
			}*/
			if (orgDocIds != null && !orgDocIds.isEmpty()) {
				query.setParameter("cdocIds", orgDocIds);
			}
			if (docIds != null && !docIds.isEmpty()) {
				query.setParameter("docIds", docIds);
			}

			result = query.getResultList();

		}
		return result;

	}

	@SuppressWarnings("unchecked")
	private List<Object[]> getGstr1SummaryResultSet(String sgstin,
			String retPeriod, String sql, String docType, String tableType) {

		List<Object[]> result = null;
		if (sql != null && sql.trim().length() > 0) {

			Query query = entityManager.createQuery(sql);
			if (sgstin != null && retPeriod != null && docType != null) {
				query.setParameter("gstins", sgstin);
				query.setParameter("retPeriod", retPeriod);
			}
			if (!docType.equalsIgnoreCase(APIConstants.HSNSUM)
					&& !docType.equalsIgnoreCase(APIConstants.NIL)
					&& !docType.equalsIgnoreCase(APIConstants.DOCISS)) {
				query.setParameter("docType", docType.toUpperCase());
			}

			result = query.getResultList();

		}
		return result;
	}

	@Override
	public List<Object[]> findGstr1CancelledData(String gstin, String retPeriod,
			String docType, List<Long> docIds, ProcessingContext context) {

		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.FIND_CAN_DATA_DB_START,
				PerfamanceEventConstants.DocSearchForSaveCustomRepositoryImpl,
				PerfamanceEventConstants.findGstr1CancelledData,
				PerfamanceEventConstants.GSTIN_Ret_Period_section.concat(":{")
						.concat(gstin).concat(",").concat(retPeriod)
						.concat(docType).concat("}"));
		List<Object[]> resultset = null;
		if (GSTConstants.CAN.equalsIgnoreCase(docType)) {
			String sql = queryBuilder.buildGstr1CancelledQuery(gstin, retPeriod,
					docType, docIds, context);
			resultset = getGstr1CancelledResultSet(gstin, retPeriod, sql,
					docType, docIds);
		}
		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.FIND_CAN_DATA_DB_END,
				PerfamanceEventConstants.DocSearchForSaveCustomRepositoryImpl,
				PerfamanceEventConstants.findGstr1CancelledData,
				PerfamanceEventConstants.GSTIN_Ret_Period_section.concat(":{")
						.concat(gstin).concat(",").concat(retPeriod)
						.concat(docType).concat("}"));
		return resultset;
	}

	@Override
	public List<Object[]> findGstr1InvoiceLevelData(String gstin,
			String retPeriod, String docType, Set<Long> orgDocIds,
			List<Long> docIds, ProcessingContext context) {

		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.FIND_INV_DATA_DB_START,
				PerfamanceEventConstants.DocSearchForSaveCustomRepositoryImpl,
				PerfamanceEventConstants.findGstr1InvoiceLevelData,
				PerfamanceEventConstants.GSTIN_Ret_Period_section.concat(":{")
						.concat(gstin).concat(",").concat(retPeriod)
						.concat(docType).concat("}"));
		
		//Handcoded to A since we will always consider the multi supply query.
		String multiSupplyTypeAns ="A";
		context.seAttribute("isMultiSupplyOpted",
				"A".equalsIgnoreCase(multiSupplyTypeAns) ? true : false);
		String sql = null;
		String tableType = "";
		if (docType.equalsIgnoreCase(APIConstants.B2B)
				|| docType.equalsIgnoreCase(APIConstants.B2BA)) {

			if (docType.equalsIgnoreCase(APIConstants.B2B)) {
				tableType = GSTConstants.GSTR1_4A.concat(",")
						.concat(GSTConstants.GSTR1_4B).concat(",")
						.concat(GSTConstants.GSTR1_6B).concat(",")
						.concat(GSTConstants.GSTR1_6C);
				docType = GSTConstants.GSTR1_B2B;
			} else {
				tableType = GSTConstants.GSTR1_9A;
				docType = GSTConstants.GSTR1_B2BA;
			}
			sql = queryBuilder.buildGstr1B2bB2baQuery(gstin, retPeriod, docType,
					tableType, orgDocIds, docIds, context);
		} else if (docType.equalsIgnoreCase(APIConstants.B2CL)
				|| docType.equalsIgnoreCase(APIConstants.B2CLA)) {

			if (docType.equalsIgnoreCase(APIConstants.B2CL)) {
				tableType = GSTConstants.GSTR1_5A;
				docType = GSTConstants.GSTR1_B2CL;
			} else {
				tableType = GSTConstants.GSTR1_9A;
				docType = GSTConstants.GSTR1_B2CLA;
			}
			sql = queryBuilder.buildGstr1B2clB2claQuery(gstin, retPeriod,
					docType, tableType, orgDocIds, docIds, context);
		} else if (docType.equalsIgnoreCase(APIConstants.EXP)
				|| docType.equalsIgnoreCase(APIConstants.EXPA)) {

			if (docType.equalsIgnoreCase(APIConstants.EXP)) {

				tableType = GSTConstants.GSTR1_6A;
				docType = GSTConstants.GSTR1_EXP;
			} else {

				tableType = GSTConstants.GSTR1_9A;
				docType = GSTConstants.GSTR1_EXPA;
			}
			sql = queryBuilder.buildGstr1ExpExpaQuery(gstin, retPeriod, docType,
					tableType, orgDocIds, docIds, context);
		} else if (docType.equalsIgnoreCase(APIConstants.CDNR)
				|| docType.equalsIgnoreCase(APIConstants.CDNRA)) {

			if (docType.equalsIgnoreCase(APIConstants.CDNR)) {
				tableType = GSTConstants.GSTR1_9B;
				docType = GSTConstants.CDNR;
			} else {
				tableType = GSTConstants.GSTR1_9C;
				docType = GSTConstants.GSTR1_CDNRA;
			}
			sql = queryBuilder.buildGstr1CdnQuery(gstin, retPeriod, docType,
					tableType, orgDocIds, docIds, context);
		} else if (docType.equalsIgnoreCase(APIConstants.CDNUR)
				|| docType.equalsIgnoreCase(APIConstants.CDNURA)) {

			if (docType.equalsIgnoreCase(APIConstants.CDNUR)) {

				tableType = GSTConstants.GSTR1_9B;
				// docType = GSTConstants.CDNUR;
				// CDNUR Distribution into EXPORTS and B2CL on 15-01-21
				docType = GSTConstants.CDNUR.concat(",")
						.concat(GSTConstants.CDNUR_EXPORTS.concat(","))
						.concat(GSTConstants.CDNUR_B2CL);
			} else {

				tableType = GSTConstants.GSTR1_9C;
				docType = GSTConstants.GSTR1_CDNURA;
			}
			sql = queryBuilder.buildGstr1CdnQuery(gstin, retPeriod, docType,
					tableType, orgDocIds, docIds, context);
		} else if (docType.equalsIgnoreCase(APIConstants.ECOMSUP)) {
			docType = GSTConstants.ECOM_SUP;
			tableType = GSTConstants.GSTR1_15I + "," + GSTConstants.GSTR1_15III;
			sql = queryBuilder.buildGstr1EcomQuery(gstin, retPeriod, docType,
					tableType, orgDocIds, docIds, context);
		}
		List<Object[]> resultset = getGstr1HorizontalResultSet(gstin, retPeriod,
				sql, docType, tableType, orgDocIds, docIds);

		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.FIND_INV_DATA_DB_END,
				PerfamanceEventConstants.DocSearchForSaveCustomRepositoryImpl,
				PerfamanceEventConstants.findGstr1InvoiceLevelData,
				PerfamanceEventConstants.GSTIN_Ret_Period_section.concat(":{")
						.concat(gstin).concat(",").concat(retPeriod)
						.concat(docType).concat("}"));
		return resultset;
	}

	@Override
	public List<Object[]> findGstr1SummaryLevelData(String gstin,
			String retPeriod, String docType, ProcessingContext context) {

		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.FIND_SUMM_DATA_DB_START,
				PerfamanceEventConstants.DocSearchForSaveCustomRepositoryImpl,
				PerfamanceEventConstants.findGstr1SummaryLevelData,
				PerfamanceEventConstants.GSTIN_Ret_Period_section.concat(":{")
						.concat(gstin).concat(",").concat(retPeriod)
						.concat(docType).concat("}"));

		String tableType = "";
		String sql = "";
		if (docType.equalsIgnoreCase(APIConstants.B2CS)
				|| docType.equalsIgnoreCase(APIConstants.B2CSA)) {
			if (docType.equalsIgnoreCase(APIConstants.B2CS)) {
				tableType = GSTConstants.GSTR1_7A1.concat(",")
						.concat(GSTConstants.GSTR1_7B1);
				// docType = GSTConstants.GSTR1_B2CS;
			} else {
				tableType = GSTConstants.GSTR1_10A.concat(",")
						.concat(GSTConstants.GSTR1_10B);
				// docType = GSTConstants.GSTR1_B2CSA;
			}
			sql = queryBuilder.buildGstr1B2csB2csaQuery(gstin, retPeriod,
					docType, tableType, context);
		} else if (docType.equalsIgnoreCase(APIConstants.TXP)
				|| docType.equalsIgnoreCase(APIConstants.TXPA)) {
			if (docType.equalsIgnoreCase(APIConstants.TXP)) {

				tableType = BifurcationConstants.SECTION_11_PART1_B1.concat(",")
						.concat(BifurcationConstants.SECTION_11_PART1_B2);
				// docType = BifurcationConstants.TAX_DOC_TYPE_ADV_ADJ;
			} else {
				tableType = BifurcationConstants.SECTION_11_PART2_B1.concat(",")
						.concat(BifurcationConstants.SECTION_11_PART2_B2);
				// docType = BifurcationConstants.TAX_DOC_TYPE_ADV_ADJ_A;
			}
			sql = queryBuilder.buildGstr1TxpTxpaQuery(gstin, retPeriod, docType,
					tableType, context);
		} else if (docType.equalsIgnoreCase(APIConstants.AT)
				|| docType.equalsIgnoreCase(APIConstants.ATA)) {
			if (docType.equalsIgnoreCase(APIConstants.AT)) {
				tableType = BifurcationConstants.SECTION_11_PART1_A1.concat(",")
						.concat(BifurcationConstants.SECTION_11_PART1_A2);
				// docType = BifurcationConstants.TAX_DOC_TYPE_ADV_REC;
			} else {
				tableType = BifurcationConstants.SECTION_11_PART2_A1.concat(",")
						.concat(BifurcationConstants.SECTION_11_PART2_A2);
				// docType = BifurcationConstants.TAX_DOC_TYPE_ADV_RECA;
			}
			sql = queryBuilder.buildGstr1AtAtaQuery(gstin, retPeriod, docType,
					tableType, context);
		}
		//HSN
		else if (docType.equalsIgnoreCase(APIConstants.HSNSUM)) {
			tableType = "";// 12
			sql = queryBuilder.buildGstr1HsnSumQuery(gstin, retPeriod, docType,
					tableType, context);
		}
		
		else if (docType.equalsIgnoreCase(APIConstants.NIL)) {
			tableType = GSTConstants.GSTR1_8A.concat(",")
					.concat(GSTConstants.GSTR1_8B).concat(",")
					.concat(GSTConstants.GSTR1_8C).concat(",")
					.concat(GSTConstants.GSTR1_8D);
			// docType = GSTConstants.NIL_EXT_NON;
			sql = queryBuilder.buildGstr1NilQuery(gstin, retPeriod, docType,
					tableType, context);
		} else if (docType.equalsIgnoreCase(APIConstants.DOCISS)) {
			tableType = "";// 13
			sql = queryBuilder.buildGstr1DocIssuedQuery(gstin, retPeriod,
					docType, tableType, context);
		} else if (docType.equalsIgnoreCase(APIConstants.SUPECOM)) {
			tableType = "";// 13
			sql = queryBuilder.buildGstr1SupEcomQuery(gstin, retPeriod, docType,
					tableType, context);
		} else if (docType.equalsIgnoreCase(APIConstants.ECOMSUPSUM)) {
			tableType = "";
			sql = queryBuilder.buildGstr1EcomSumQuery(gstin, retPeriod, docType,
					tableType, context);
		}
		List<Object[]> resultset = getGstr1SummaryResultSet(gstin, retPeriod,
				sql, docType, tableType);

		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.FIND_SUMM_DATA_DB_END,
				PerfamanceEventConstants.DocSearchForSaveCustomRepositoryImpl,
				PerfamanceEventConstants.findGstr1SummaryLevelData,
				PerfamanceEventConstants.GSTIN_Ret_Period_section.concat(":{")
						.concat(gstin).concat(",").concat(retPeriod)
						.concat(docType).concat("}"));

		return resultset;

	}

	@Override
	public List<Object[]> findGstr1DependentRetryInvoiceLevelData(
			String docType, Long batchId, Set<String> errorCodes) {

		String sql = null;

		if (docType.equalsIgnoreCase(APIConstants.B2B)
				|| docType.equalsIgnoreCase(APIConstants.B2BA)) {

			sql = queryBuilder.buildGstr1DependentRetryB2bB2baQuery(batchId,
					errorCodes);

		} else if (docType.equalsIgnoreCase(APIConstants.B2CL)
				|| docType.equalsIgnoreCase(APIConstants.B2CLA)) {

			sql = queryBuilder.buildGstr1DependentRetryB2clB2claQuery(batchId,
					errorCodes);

		} else if (docType.equalsIgnoreCase(APIConstants.EXP)
				|| docType.equalsIgnoreCase(APIConstants.EXPA)) {

			sql = queryBuilder.buildGstr1DependentRetryExpExpaQuery(batchId,
					errorCodes);

		} else if (docType.equalsIgnoreCase(APIConstants.CDNR)
				|| docType.equalsIgnoreCase(APIConstants.CDNRA)
				|| docType.equalsIgnoreCase(APIConstants.CDNUR)
				|| docType.equalsIgnoreCase(APIConstants.CDNURA)) {

			sql = queryBuilder.buildGstr1DependentRetryCdnQuery(batchId,
					errorCodes);
		}

		List<Object[]> result = null;
		if (sql != null && sql.trim().length() > 0) {

			Query query = entityManager.createQuery(sql);

			/**
			 * setting the values based on condition the key values in the
			 * setParameter should be same as index names. index names are
			 * dynamically coming from the criteria.
			 */
			if (batchId != null && errorCodes != null) {
				query.setParameter("batchId", batchId);
				query.setParameter("errorCodes", errorCodes);
			}

			result = query.getResultList();

		}
		return result;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Pair<String, String>> getGstr1SgstinRetPerds(
			Gstr1SaveToGstnReqDto dto) {

		String sql = extractor.buildSgstinRetPerdsQuery(dto);
		if (sql != null && sql.trim().length() > 0) {
			List<Object[]> resultList = new ArrayList<>();
			LocalDate firstDataRecv = dto.getDataRecvFrom();
			LocalDate secondDataRecv = dto.getDataRecvTo();
			LocalDate firstDocDate = dto.getDocDateFrom();
			LocalDate secondDocDate = dto.getDocDateTo();
			int firstDerRetPeriod = 0;
			int secondDerRetPeriod = 0;
			if (dto.getRetPeriodFrom() != null) {
				firstDerRetPeriod = GenUtil
						.convertTaxPeriodToInt(dto.getRetPeriodFrom());
				secondDerRetPeriod = GenUtil
						.convertTaxPeriodToInt(dto.getRetPeriodTo());
			}
			List<LocalDate> userDates = dto.getDates();
			// List<String> sgstins = new ArrayList<>();
			// List<String> retPeriods = new ArrayList<>();
			List<Pair<String, String>> listOfPairs = new ArrayList<>();

			Query query = entityManager.createQuery(sql);

			if (!userDates.isEmpty()) {// Mandatory value

				List<String> multipleGstins = dto.getSgstins();
				String returnPeriod = dto.getReturnPeriod();
				if (!userDates.isEmpty()) {
					query.setParameter("userDates", userDates);
				}
				if (firstDataRecv != null && secondDataRecv != null) {
					query.setParameter("firstDataRecv ", firstDataRecv);
					query.setParameter("secondDataRecv", secondDataRecv);
				} else if (firstDocDate != null && secondDocDate != null) {
					query.setParameter("firstDocDate", firstDocDate);
					query.setParameter("secondDocDate", secondDocDate);
				} else if (firstDerRetPeriod != 0 && secondDerRetPeriod != 0) {
					query.setParameter("firstDerRetPeriod", firstDerRetPeriod);
					query.setParameter("secondDerRetPeriod",
							secondDerRetPeriod);
				}
				if (multipleGstins != null &&  !multipleGstins.isEmpty()) {
					query.setParameter("gstins", multipleGstins);
				}
				resultList = query.getResultList();

				for (Object[] result : resultList) {
					listOfPairs.add(new Pair<>(
							result[0] != null ? String.valueOf(result[0])
									: null,
							result[1] != null ? String.valueOf(result[1])
									: null));
				}

			}
			return listOfPairs;

		}
		return null;
	}

	@Override
	public List<Object[]> findAnx1InvLevelData(String gstin, String retPeriod,
			String docType, List<Long> docIds) {

		String sql = null;
		String tableType = "";
		// Outward
		if (docType.equalsIgnoreCase(APIConstants.B2B)) {
			tableType = BifurcationConstants.SECTION_3B;
			docType = BifurcationConstants.TAX_DOC_TYPE_B2B;
			sql = queryBuilder.buildAnx1B2bQuery(gstin, retPeriod, docType,
					tableType, docIds);
		} else if (docType.equalsIgnoreCase(APIConstants.EXPWP)) {
			tableType = BifurcationConstants.SECTION_3C;
			docType = BifurcationConstants.TAX_DOC_TYPE_EXPT;
			sql = queryBuilder.buildAnx1ExpwpAndExpwopQuery(gstin, retPeriod,
					docType, tableType, docIds);

		} else if (docType.equalsIgnoreCase(APIConstants.EXPWOP)) {
			tableType = BifurcationConstants.SECTION_3D;
			docType = BifurcationConstants.TAX_DOC_TYPE_EXPWT;
			sql = queryBuilder.buildAnx1ExpwpAndExpwopQuery(gstin, retPeriod,
					docType, tableType, docIds);
		} else if (docType.equalsIgnoreCase(APIConstants.SEZWP)) {
			tableType = BifurcationConstants.SECTION_3E;
			docType = BifurcationConstants.TAX_DOC_TYPE_SEZT;
			sql = queryBuilder.buildAnx1SezwpAndSezwopQuery(gstin, retPeriod,
					docType, tableType, docIds);
		} else if (docType.equalsIgnoreCase(APIConstants.SEZWOP)) {
			tableType = BifurcationConstants.SECTION_3F;
			docType = BifurcationConstants.TAX_DOC_TYPE_SEZWT;
			sql = queryBuilder.buildAnx1SezwpAndSezwopQuery(gstin, retPeriod,
					docType, tableType, docIds);
		} else if (docType.equalsIgnoreCase(APIConstants.DE)) {
			tableType = BifurcationConstants.SECTION_3G;
			docType = BifurcationConstants.TAX_DOC_TYPE_DXP;
			sql = queryBuilder.buildAnx1DeemedExportsQuery(gstin, retPeriod,
					docType, tableType, docIds);
		}
		// Inward
		else if (docType.equalsIgnoreCase(APIConstants.IMPG)) {
			tableType = BifurcationConstants.SECTION_3J;
			docType = BifurcationConstants.TAX_DOC_TYPE_IMPG;
			sql = queryBuilder.buildAnx1ImpgAndImpgSezQuery(gstin, retPeriod,
					docType, tableType, docIds);
		} else if (docType.equalsIgnoreCase(APIConstants.IMPGSEZ)) {
			tableType = BifurcationConstants.SECTION_3K;
			docType = BifurcationConstants.TAX_DOC_TYPE_SEZG;
			sql = queryBuilder.buildAnx1ImpgAndImpgSezQuery(gstin, retPeriod,
					docType, tableType, docIds);
		} else if (docType.equalsIgnoreCase(APIConstants.MIS)) {

			sql = queryBuilder.buildAnx1MisQuery(gstin, retPeriod, docType,
					tableType, docIds);
		}

		return getAnx1InvoiceLevelResultSet(gstin, retPeriod, sql, docType,
				tableType, docIds);
	}

	@Override
	public List<Object[]> findAnx1SumLevelData(String gstin, String retPeriod,
			String docType) {

		String sql = null;
		String tableType = "";
		// Outward
		if (docType.equalsIgnoreCase(APIConstants.B2C)) {
			tableType = BifurcationConstants.SECTION_3A;
			docType = BifurcationConstants.TAX_DOC_TYPE_B2C;
			sql = queryBuilder.buildAnx1B2cQuery(gstin, retPeriod, docType,
					tableType, null);
		}
		// Inward
		else if (docType.equalsIgnoreCase(APIConstants.REV)) {
			tableType = BifurcationConstants.SECTION_3H;
			docType = BifurcationConstants.TAX_DOC_TYPE_RCM;
			sql = queryBuilder.buildAnx1RevDataQuery(gstin, retPeriod, docType,
					tableType, null);
		} else if (docType.equalsIgnoreCase(APIConstants.IMPS)) {
			tableType = BifurcationConstants.SECTION_3I;
			docType = BifurcationConstants.TAX_DOC_TYPE_IMPS;
			sql = queryBuilder.buildAnx1ImpsQuery(gstin, retPeriod, docType,
					tableType, null);
		} else if (docType.equalsIgnoreCase(APIConstants.ECOM)) {

			sql = queryBuilder.buildAnx1EcomQuery(gstin, retPeriod, docType,
					tableType, null);
		}

		return getAnx1SummResultSet(gstin, retPeriod, sql);
	}

	@Override
	public List<Object[]> findAnx1CancelledData(String gstin, String retPeriod,
			String docType) {
		if (GSTConstants.CAN.equalsIgnoreCase(docType)) {
			String sql = queryBuilder.buildAnx1CancelledQuery(gstin, retPeriod,
					docType);
			return getAnx1CancelledResultSet(gstin, retPeriod, sql, docType);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> getAnx1InvoiceLevelResultSet(String sgstin,
			String retPeriod, String sql, String docType, String tableType,
			List<Long> docIds) {

		List<Object[]> result = null;
		if (sql != null && sql.trim().length() > 0) {

			Query query = entityManager.createNativeQuery(sql);

			/**
			 * setting the values based on condition the key values in the
			 * setParameter should be same as index names. index names are
			 * dynamically coming from the criteria.
			 */

			if (sgstin != null && retPeriod != null) {
				query.setParameter("gstins", sgstin);
				query.setParameter("retPeriod", retPeriod);
			}
			if (docType != null && !docType.isEmpty()) {
				String[] array = docType.split(",");
				List<String> list = new ArrayList<>(Arrays.asList(array));
				query.setParameter("docType", list);
			}

			if (tableType != null && !tableType.isEmpty()) {
				String[] array = tableType.split(",");
				List<String> list = new ArrayList<>(Arrays.asList(array));
				query.setParameter("tableType", list);
			}
			if (docIds != null && !docIds.isEmpty()) {
				query.setParameter("docIds", docIds);

			}
			result = query.getResultList();
		}
		return result;

	}

	/*
	 * @SuppressWarnings("unchecked") private List<Object[]>
	 * getAnx1ResultSet(String sgstin, String retPeriod, String sql, String
	 * docType, String tableType, List<Long> docIds) {
	 * 
	 * List<Object[]> result = null; if (sql != null && sql.trim().length() > 0)
	 * {
	 * 
	 * Query query = entityManager.createNativeQuery(sql);
	 * 
	 *//**
		 * setting the values based on condition the key values in the
		 * setParameter should be same as index names. index names are
		 * dynamically coming from the criteria.
		 *//*
		 * 
		 * if (sgstin != null && retPeriod != null) {
		 * query.setParameter("gstins", sgstin); query.setParameter("retPeriod",
		 * retPeriod); } if (docType != null && !docType.isEmpty()) { String[]
		 * array = docType.split(","); List<String> list = new
		 * ArrayList<>(Arrays.asList(array)); query.setParameter("docType",
		 * list); }
		 * 
		 * if (tableType != null && !tableType.isEmpty()) { String[] array =
		 * tableType.split(","); List<String> list = new
		 * ArrayList<>(Arrays.asList(array)); query.setParameter("tableType",
		 * list); } if (docIds != null && !docIds.isEmpty()) {
		 * query.setParameter("docIds", docIds);
		 * 
		 * } result = query.getResultList();
		 * 
		 * } return result;
		 * 
		 * }
		 */

	@SuppressWarnings("unchecked")
	private List<Object[]> getAnx1CancelledResultSet(String sgstin,
			String retPeriod, String sql, String docType) {
		List<Object[]> result = null;
		if (sql != null && sql.trim().length() > 0) {
			Query query = entityManager.createQuery(sql);
			/**
			 * setting the values based on condition the key values in the
			 * setParameter should be same as index names. index names are
			 * dynamically coming from the criteria.
			 */
			if (sgstin != null && retPeriod != null) {
				query.setParameter("gstin", sgstin);
				query.setParameter("retPeriod", retPeriod);
			}
			if (docType != null) {
				query.setParameter("supplyType", docType);
			}
			result = query.getResultList();

		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> getAnx2InvoiceLevelResultSet(String sgstin,
			String retPeriod, String sql, String docType, String tableType,
			List<Long> docIds) {

		List<Object[]> result = null;
		if (sql != null && sql.trim().length() > 0) {

			Query query = entityManager.createNativeQuery(sql);

			/**
			 * setting the values based on condition the key values in the
			 * setParameter should be same as index names. index names are
			 * dynamically coming from the criteria.
			 */

			if (sgstin != null && retPeriod != null) {
				query.setParameter("gstins", sgstin);
				query.setParameter("retPeriod", retPeriod);
			}
			if (docType != null && !docType.isEmpty()) {
				String[] array = docType.split(",");
				List<String> list = new ArrayList<>(Arrays.asList(array));
				query.setParameter("docType", list);
			}

			if (docIds != null && !docIds.isEmpty()) {
				query.setParameter("docIds", docIds);

			}
			result = query.getResultList();

		}
		return result;

	}

	public List<Object[]> findAnx2InvLevelData(String gstin, String retPeriod,
			String docType, List<Long> docIds) {
		String sql = null;
		String tableType = "";
		// Inward
		if (docType.equalsIgnoreCase(APIConstants.B2B)) {
			tableType = BifurcationConstants.SECTION_3B;
			docType = BifurcationConstants.TAX_DOC_TYPE_B2B;

			sql = queryBuilder.buildAnx2SaveQuery(gstin, retPeriod, docType,
					tableType, docIds);
		} else if (docType.equalsIgnoreCase(APIConstants.B2BA)) {
			tableType = BifurcationConstants.SECTION_3B;
			docType = BifurcationConstants.TAX_DOC_TYPE_B2B;

			sql = queryBuilder.buildAnx2SaveQuery(gstin, retPeriod, docType,
					tableType, docIds);
		} else if (docType.equalsIgnoreCase(APIConstants.DE)) {
			tableType = BifurcationConstants.SECTION_3G;
			docType = BifurcationConstants.TAX_DOC_TYPE_DXP;

			sql = queryBuilder.buildAnx2SaveQuery(gstin, retPeriod, docType,
					tableType, docIds);
		} else if (docType.equalsIgnoreCase(APIConstants.DEA)) {
			tableType = BifurcationConstants.SECTION_3G;
			docType = BifurcationConstants.TAX_DOC_TYPE_DXP;

			sql = queryBuilder.buildAnx2SaveQuery(gstin, retPeriod, docType,
					tableType, docIds);
		} else if (docType.equalsIgnoreCase(APIConstants.SEZWP)) {
			tableType = BifurcationConstants.SECTION_3E;
			docType = BifurcationConstants.TAX_DOC_TYPE_SEZT;

			sql = queryBuilder.buildAnx2SaveQuery(gstin, retPeriod, docType,
					tableType, docIds);
		} else if (docType.equalsIgnoreCase(APIConstants.SEZWPA)) {
			tableType = BifurcationConstants.SECTION_3E;
			docType = BifurcationConstants.TAX_DOC_TYPE_SEZT;

			sql = queryBuilder.buildAnx2SaveQuery(gstin, retPeriod, docType,
					tableType, docIds);
		} else if (docType.equalsIgnoreCase(APIConstants.SEZWOP)) {
			tableType = BifurcationConstants.SECTION_3F;
			docType = BifurcationConstants.TAX_DOC_TYPE_SEZWT;

			sql = queryBuilder.buildAnx2SaveQuery(gstin, retPeriod, docType,
					tableType, docIds);
		} else if (docType.equalsIgnoreCase(APIConstants.SEZWOPA)) {
			tableType = BifurcationConstants.SECTION_3F;
			docType = BifurcationConstants.TAX_DOC_TYPE_SEZWT;

			sql = queryBuilder.buildAnx2SaveQuery(gstin, retPeriod, docType,
					tableType, docIds);
		}
		return getAnx2InvoiceLevelResultSet(gstin, retPeriod, sql, docType,
				tableType, docIds);
	}

	public List<Object[]> findAnx2CancelledData(String gstin, String retPeriod,
			String docType) {
		if (GSTConstants.CAN.equalsIgnoreCase(docType)) {
			String sql = queryBuilder.buildAnx2CancelledQuery(gstin, retPeriod,
					docType);
			return getAnx1CancelledResultSet(gstin, retPeriod, sql, docType);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> findGstr3BInvoiceLevelData(String gstin,
			String retPeriod) {

		String sql = null;
		/*
		 * if("osup_det") sql = gstr3BQueryBuilder.buildOsupDetailsQuery(gstin,
		 * retPeriod); else if("osup_zero")
		 */
		sql = gstr3BQueryBuilder.buildGstr3BQuery(gstin, retPeriod);
		List<Object[]> result = new ArrayList<>();
		if (sql != null && sql.trim().length() > 0) {
			// try {
			Query query = entityManager.createNativeQuery(sql);
			if (gstin != null && retPeriod != null) {
				query.setParameter("gstin", gstin);
				query.setParameter("retPeriod", retPeriod);
			}
			result = query.getResultList();
			/*
			 * } catch (Exception e) { LOGGER.error(
			 * "Erorr While Executing the Query for GSTR3B SAVE GSTN Data {}",
			 * e); e.printStackTrace(); } finally { try { if (entityManager !=
			 * null) { // entityManager.close(); } } catch (Exception ex) {
			 * LOGGER.error("Failed to close the EntityManager", ex); } }
			 */
		}
		return result;
		// return getHorizontalResultSet(gstin, retPeriod, sql, docType,
		// tableType, docIds);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> findGstr3BInvoiceGstnLevelData(String gstin,
			String retPeriod) {
		String sql = null;
		/*
		 * if("osup_det") sql = gstr3BQueryBuilder.buildOsupDetailsQuery(gstin,
		 * retPeriod); else if("osup_zero")
		 */
		sql = gstr3BQueryBuilder.buildGstr3BGstnQuery(gstin, retPeriod);
		List<Object[]> result = new ArrayList<>();
		if (sql != null && sql.trim().length() > 0) {
			// try {
			Query query = entityManager.createNativeQuery(sql);
			if (gstin != null && retPeriod != null) {
				query.setParameter("gstin", gstin);
				query.setParameter("retPeriod", retPeriod);
			}
			result = query.getResultList();
			/*
			 * } catch (Exception e) { LOGGER.error(
			 * "Erorr While Executing the Query for GSTR3B SAVE GSTN Data {}",
			 * e); e.printStackTrace(); } finally { try { if (entityManager !=
			 * null) { // entityManager.close(); } } catch (Exception ex) {
			 * LOGGER.error("Failed to close the EntityManager", ex); } }
			 */
		}
		return result;
		// return getHorizontalResultSet(gstin, retPeriod, sql, docType,
		// tableType, docIds);

	}

	// B2c Horizontal Query ResultSet Data
	@SuppressWarnings("unchecked")
	private List<Object[]> getAnx1SummResultSet(String sgstin, String retPeriod,
			String sql) {

		List<Object[]> result = null;
		if (sql != null && sql.trim().length() > 0) {

			Query query = entityManager.createNativeQuery(sql);

			/**
			 * setting the values based on condition the key values in the
			 * setParameter should be same as index names. index names are
			 * dynamically coming from the criteria.
			 */

			if (sgstin != null && retPeriod != null) {
				query.setParameter("gstins", sgstin);
				query.setParameter("retPeriod", retPeriod);
			}
			result = query.getResultList();

		}
		return result;

	}

	// ------------------------------- RET
	// ------------------------------------//

	@Override
	public List<Object[]> findRetSumLevelData(String gstin, String retPeriod,
			String docType) {

		String sql = null;
		String tableType = "";
		// Outward
		if (docType.equalsIgnoreCase(APIConstants.B2C)) {
			tableType = BifurcationConstants.SECTION_3A;
			docType = BifurcationConstants.TAX_DOC_TYPE_B2C;
			sql = queryBuilder.buildAnx1B2cQuery(gstin, retPeriod, docType,
					tableType, null);
		}
		// Inward
		else if (docType.equalsIgnoreCase(APIConstants.REV)) {
			tableType = BifurcationConstants.SECTION_3H;
			docType = BifurcationConstants.TAX_DOC_TYPE_RCM;
			sql = queryBuilder.buildAnx1RevDataQuery(gstin, retPeriod, docType,
					tableType, null);
		}

		return getAnx1SummResultSet(gstin, retPeriod, sql);
	}

	@Override
	public List<Object[]> findRetCancelledData(String gstin, String retPeriod,
			String docType) {
		if (GSTConstants.CAN.equalsIgnoreCase(docType)) {
			String sql = queryBuilder.buildAnx1CancelledQuery(gstin, retPeriod,
					docType);
			return getAnx1CancelledResultSet(gstin, retPeriod, sql, docType);
		}
		return null;
	}

	// --------------------------GSTR6-------------------------------//

	@Override
	public List<Object[]> findGstr6InvLevelData(String gstin, String retPeriod,
			String docType, String isdDocType) {
		String sql = null;
		String returnType = null;

		Optional<String> result = gstr6DigiComputeRepository
				.findOptionOpted(gstin, retPeriod);

		String optionOpted = result.isPresent() ? result.get() : "A";
		if (docType.equalsIgnoreCase(APIConstants.B2B)) {
			docType = BifurcationConstants.TAX_DOC_TYPE_B2B;
			returnType = BifurcationConstants.GSTR6_RETURN_TYPE;
			sql = queryBuilder.buildGstr6B2bSaveQuery(gstin, retPeriod, docType,
					returnType, optionOpted);
		} else if (docType.equalsIgnoreCase(APIConstants.B2BA)) {
			docType = BifurcationConstants.TAX_DOC_TYPE_B2BA;
			returnType = BifurcationConstants.GSTR6_RETURN_TYPE;
			sql = queryBuilder.buildGstr6B2baSaveQuery(gstin, retPeriod,
					docType, returnType, optionOpted);
		} else if (docType.equalsIgnoreCase(APIConstants.CDN)) {
			docType = BifurcationConstants.TAX_DOC_TYPE_CDN;
			returnType = BifurcationConstants.GSTR6_RETURN_TYPE;
			sql = queryBuilder.buildGstr6CdnSaveQuery(gstin, retPeriod, docType,
					returnType, optionOpted);
		} else if (docType.equalsIgnoreCase(APIConstants.CDNA)) {
			docType = BifurcationConstants.TAX_DOC_TYPE_CDNA;
			returnType = BifurcationConstants.GSTR6_RETURN_TYPE;
			sql = queryBuilder.buildGstr6CdnaSaveQuery(gstin, retPeriod,
					docType, returnType, optionOpted);
		} else if (docType.equalsIgnoreCase(APIConstants.ISD)) {
			docType = null;
			sql = queryBuilder.buildGstr6IsdSaveQuery(gstin, retPeriod,
					isdDocType);
		} else if (docType.equalsIgnoreCase(APIConstants.ISDA)) {
			docType = null;
			sql = queryBuilder.buildGstr6IsdaSaveQuery(gstin, retPeriod,
					isdDocType);
		}
		return getGstr6InvoiceResultSet(gstin, retPeriod, sql, docType,
				returnType, null, isdDocType);
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> getGstr6InvoiceResultSet(String gstin,
			String retPeriod, String sql, String docType, String returnType,
			List<Long> docIds, String isdDocType) {

		List<Object[]> result = null;
		if (sql != null && sql.trim().length() > 0) {
			Query query = entityManager.createNativeQuery(sql);
			if (gstin != null && retPeriod != null) {
				query.setParameter("gstin", gstin);
				query.setParameter("retPeriod", retPeriod);
			}
			if (docType != null && !docType.isEmpty()) {
				String[] array = docType.split(",");
				List<String> list = new ArrayList<>(Arrays.asList(array));
				query.setParameter("docType", list);
			}
			if (docIds != null && !docIds.isEmpty()) {
				query.setParameter("docIds", docIds);
			}
			if (returnType != null && !returnType.isEmpty()) {
				query.setParameter("returnType", returnType);
			}
			if (isdDocType != null && !isdDocType.isEmpty()) {
				query.setParameter("isdDocType", isdDocType);
			}
			result = query.getResultList();
		}
		return result;
	}

	// -----------------------------GSTR7------------------------------------//
	@Override
	public List<Object[]> findGstr7InvLevelData(String gstin, String retPeriod,
			String docType) {
		String sql = null;
		String TableName = null;
		if (docType.equalsIgnoreCase(APIConstants.TDS)) {
			TableName = "Table-3";
			sql = queryBuilder.buildGstr7TDSQuery(gstin, retPeriod, TableName);
		} else if (docType.equalsIgnoreCase(APIConstants.TDSA)) {
			TableName = "Table-4";
			sql = queryBuilder.buildGstr7TDSQuery(gstin, retPeriod, TableName);
		}
		return getGstr7InvoiceResultSet(gstin, retPeriod, sql, TableName);
	}

	private List<Object[]> getGstr7InvoiceResultSet(String gstin,
			String retPeriod, String sql, String docType) {
		List<Object[]> result = null;
		if (sql != null && sql.trim().length() > 0) {
			Query query = entityManager.createNativeQuery(sql);
			if (gstin != null && retPeriod != null) {
				query.setParameter("gstin", gstin);
				query.setParameter("retPeriod", retPeriod);
			}
			if (docType != null && !docType.isEmpty()) {
				String[] array = docType.split(",");
				List<String> list = new ArrayList<>(Arrays.asList(array));
				query.setParameter("docType", list);
			}
			result = query.getResultList();
		}
		return result;
	}

	@Override
	public List<Object[]> findGstr7CancelledData(String gstin, String retPeriod,
			String docType) {
		String sql = null;
		String TableName = null;
		if (docType.equalsIgnoreCase(APIConstants.TDS)) {
			TableName = "Table-3";
			sql = queryBuilder.buildGstr7CanQuery(gstin, retPeriod, TableName);
		} else if (docType.equalsIgnoreCase(APIConstants.TDSA)) {
			TableName = "Table-4";
			sql = queryBuilder.buildGstr7CanQuery(gstin, retPeriod, TableName);
		}
		return getGstr7InvoiceResultSet(gstin, retPeriod, sql, TableName);
	}

	// -----------------------------GSTR8------------------------------------//
	@Override
	public List<Object[]> findGstr8SummaryData(String gstin, String retPeriod,
			String section) {
		String sql = null;
		if (section.equalsIgnoreCase(APIConstants.TCS.toUpperCase())) {
			sql = queryBuilder.buildGstr8SummQuery(gstin, retPeriod, section);
		} else if (section.equalsIgnoreCase(APIConstants.TCSA.toUpperCase())) {
			sql = queryBuilder.buildGstr8SummQuery(gstin, retPeriod, section);
		} else if (section.equalsIgnoreCase(APIConstants.URD.toUpperCase())) {
			sql = queryBuilder.buildGstr8SummQuery(gstin, retPeriod, section);
		} else if (section.equalsIgnoreCase(APIConstants.URDA.toUpperCase())) {
			sql = queryBuilder.buildGstr8SummQuery(gstin, retPeriod, section);
		} else {

			LOGGER.error("Invalid Section {} ", section);
		}
		return getGstr8InvoiceResultSet(gstin, retPeriod, sql, section);
	}

	private List<Object[]> getGstr8InvoiceResultSet(String gstin,
			String retPeriod, String sql, String section) {
		List<Object[]> result = null;
		if (sql != null && sql.trim().length() > 0) {
			Query query = entityManager.createNativeQuery(sql);
			if (gstin != null && retPeriod != null) {
				query.setParameter("gstin", gstin);
				query.setParameter("retPeriod", retPeriod);
			}
			if (!Strings.isNullOrEmpty(section)) {
				query.setParameter("section", section);
			}
			result = query.getResultList();
		}
		return result;
	}

	@Override
	public List<Object[]> findGstr8CanSummaryData(String gstin,
			String retPeriod, String section) {
		String sql = null;
		String TableName = null;
		if (section.equalsIgnoreCase(APIConstants.TDS)) {
			TableName = "Table-3";
			sql = queryBuilder.buildGstr7CanQuery(gstin, retPeriod, TableName);
		} else if (section.equalsIgnoreCase(APIConstants.TDSA)) {
			TableName = "Table-4";
			sql = queryBuilder.buildGstr7CanQuery(gstin, retPeriod, TableName);
		}
		return getGstr8InvoiceResultSet(gstin, retPeriod, sql, TableName);
	}

	@Override
	public List<Object[]> findGstr6CanInvLevelData(String gstin,
			String retPeriod, String docType, String isdDocType) {
		String returnType = BifurcationConstants.GSTR6_RETURN_TYPE;
		String sql = null;
		if (docType.equalsIgnoreCase(APIConstants.B2B)) {
			docType = BifurcationConstants.TAX_DOC_TYPE_B2B;
			sql = queryBuilder.buildGstr6CanQuery(gstin, retPeriod, docType,
					returnType);
		} else if (docType.equalsIgnoreCase(APIConstants.B2BA)) {
			docType = BifurcationConstants.TAX_DOC_TYPE_B2BA;
			sql = queryBuilder.buildGstr6CanQuery(gstin, retPeriod, docType,
					returnType);
		} else if (docType.equalsIgnoreCase(APIConstants.CDN)) {
			docType = BifurcationConstants.TAX_DOC_TYPE_CDN;
			sql = queryBuilder.buildGstr6CanQuery(gstin, retPeriod, docType,
					returnType);
		} else if (docType.equalsIgnoreCase(APIConstants.CDNA)) {
			docType = BifurcationConstants.TAX_DOC_TYPE_CDNA;
			sql = queryBuilder.buildGstr6CanQuery(gstin, retPeriod, docType,
					returnType);
		} else if (docType.equalsIgnoreCase(APIConstants.ISD)) {
			docType = null;
			returnType = null;
			sql = queryBuilder.buildGstr6CanIsdQuery(gstin, retPeriod,
					isdDocType);
		} else if (docType.equalsIgnoreCase(APIConstants.ISDA)) {
			docType = null;
			returnType = null;
			sql = queryBuilder.buildGstr6CanIsdaQuery(gstin, retPeriod,
					isdDocType);
		}
		return getGstr6InvoiceResultSet(gstin, retPeriod, sql, docType,
				returnType, null, isdDocType);
	}

	// --------------------------ITC04 SAVE-------------------------------//

	@Override
	public List<Object[]> findItc04InvLevelData(String gstin, String retPeriod,
			String docType) {
		String sql = null;
		if (docType.equalsIgnoreCase(APIConstants.M2JW)) {
			sql = queryBuilder.buildItc04M2jwSaveQuery(gstin, retPeriod);
		} else if (docType.equalsIgnoreCase(APIConstants.TABLE5A)) {
			sql = queryBuilder.buildItc04Table5aSaveQuery(gstin, retPeriod);
		} else if (docType.equalsIgnoreCase(APIConstants.TABLE5B)) {
			sql = queryBuilder.buildItc04Table5bSaveQuery(gstin, retPeriod);
		} else if (docType.equalsIgnoreCase(APIConstants.TABLE5C)) {
			sql = queryBuilder.buildItc04Table5cSaveQuery(gstin, retPeriod);
		}
		return getItc04InvoiceResultSet(gstin, retPeriod, sql);
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> getItc04InvoiceResultSet(String gstin,
			String retPeriod, String sql) {
		List<Object[]> result = null;
		if (sql != null && sql.trim().length() > 0) {
			Query query = entityManager.createNativeQuery(sql);
			if (gstin != null && retPeriod != null) {
				query.setParameter("gstin", gstin);
				query.setParameter("retPeriod", retPeriod);
			}
			result = query.getResultList();
		}
		return result;
	}
	// -----------------------ITC04 CAN--------------------------//

	@Override
	public List<Object[]> findItc04CanInvLevelData(String gstin,
			String retPeriod, String docType) {
		String sql = null;
		if (docType.equalsIgnoreCase(APIConstants.M2JW)) {
			sql = queryBuilder.buildItc04M2jwCanQuery(gstin, retPeriod);
		} else if (docType.equalsIgnoreCase(APIConstants.TABLE5A)) {
			sql = queryBuilder.buildItc04Table5aCanQuery(gstin, retPeriod);
		} else if (docType.equalsIgnoreCase(APIConstants.TABLE5B)) {
			sql = queryBuilder.buildItc04Table5bCanQuery(gstin, retPeriod);
		} else if (docType.equalsIgnoreCase(APIConstants.TABLE5C)) {
			sql = queryBuilder.buildItc04Table5cCanQuery(gstin, retPeriod);
		}
		return getItc04InvoiceResultSet(gstin, retPeriod, sql);
	}

	// --------------------------GSTR2X SAVE----------------------------//
	@Override
	public List<Object[]> findGstr2XInvLevelData(String gstin, String retPeriod,
			String docType) {
		String sql = null;
		if (docType.equalsIgnoreCase(APIConstants.TDS)) {
			sql = queryBuilder.buildGstr2xTdsSaveQuery(gstin, retPeriod);
		} else if (docType.equalsIgnoreCase(APIConstants.TDSA)) {
			sql = queryBuilder.buildGstr2xTdsaSaveQuery(gstin, retPeriod);
		} else if (docType.equalsIgnoreCase(APIConstants.TCS)) {
			sql = queryBuilder.buildGstr2xTcsSaveQuery(gstin, retPeriod);
		} else if (docType.equalsIgnoreCase(APIConstants.TCSA)) {
			sql = queryBuilder.buildGstr2xTcsaSaveQuery(gstin, retPeriod);
		}
		return getItc04InvoiceResultSet(gstin, retPeriod, sql);
	}

	// ----------------------GSTR7EntityLevelSAVE-----------------------//

	@SuppressWarnings("unchecked")
	@Override
	public List<Pair<String, String>> getGstr7SgstinRetPerds(
			Gstr1SaveToGstnReqDto dto) {

		String sql = extractor.buildGStr7SgstinRetPerdsQuery(dto);
		if (sql != null && sql.trim().length() > 0) {
			List<Object[]> resultList = new ArrayList<>();

			List<Pair<String, String>> listOfPairs = new ArrayList<>();

			Query query = entityManager.createQuery(sql);
			List<String> multipleGstins = dto.getSgstins();
			String returnPeriod = dto.getReturnPeriod();

			if (multipleGstins != null && !multipleGstins.isEmpty()) {
				query.setParameter("gstins", multipleGstins);
			}
			resultList = query.getResultList();
			if (resultList != null && !resultList.isEmpty()) {
				for (Object[] result : resultList) {
					listOfPairs.add(new Pair<>(
							result[0] != null ? String.valueOf(result[0])
									: null,
							result[1] != null ? String.valueOf(result[1])
									: null));
				}
			}
			return listOfPairs;
		}
		return null;
	}

	// ----------------------------ITC04EntityLevelSave----------------------//

	@SuppressWarnings("unchecked")
	@Override
	public List<Pair<String, String>> getItc04SgstinRetPerds(
			Gstr1SaveToGstnReqDto dto) {

		String sql = extractor.buildItc04SgstinRetPerdsQuery(dto);
		if (sql != null && sql.trim().length() > 0) {
			List<Object[]> resultList = new ArrayList<>();

			List<Pair<String, String>> listOfPairs = new ArrayList<>();

			Query query = entityManager.createQuery(sql);
			List<String> multipleGstins = dto.getSgstins();
			String returnPeriod = dto.getReturnPeriod();

			if (multipleGstins != null && !multipleGstins.isEmpty()) {
				query.setParameter("gstins", multipleGstins);
			}
			resultList = query.getResultList();
			if (resultList != null && !resultList.isEmpty()) {
				for (Object[] result : resultList) {
					listOfPairs.add(new Pair<>(
							result[0] != null ? String.valueOf(result[0])
									: null,
							result[1] != null ? String.valueOf(result[1])
									: null));
				}
			}
			return listOfPairs;
		}
		return null;
	}

	////////////////////////// GSTR1A/////////////////////////////////////////////////////////////

	// TODO
	@SuppressWarnings("unchecked")
	@Override
	public List<Pair<String, String>> getGstr1ASgstinRetPerds(
			Gstr1SaveToGstnReqDto dto) {

		String sql = extractor.buildGstr1ASgstinRetPerdsQuery(dto);
		if (sql != null && sql.trim().length() > 0) {
			List<Object[]> resultList = new ArrayList<>();
			LocalDate firstDataRecv = dto.getDataRecvFrom();
			LocalDate secondDataRecv = dto.getDataRecvTo();
			LocalDate firstDocDate = dto.getDocDateFrom();
			LocalDate secondDocDate = dto.getDocDateTo();
			int firstDerRetPeriod = 0;
			int secondDerRetPeriod = 0;
			if (dto.getRetPeriodFrom() != null) {
				firstDerRetPeriod = GenUtil
						.convertTaxPeriodToInt(dto.getRetPeriodFrom());
				secondDerRetPeriod = GenUtil
						.convertTaxPeriodToInt(dto.getRetPeriodTo());
			}
			List<LocalDate> userDates = dto.getDates();
			// List<String> sgstins = new ArrayList<>();
			// List<String> retPeriods = new ArrayList<>();
			List<Pair<String, String>> listOfPairs = new ArrayList<>();

			Query query = entityManager.createQuery(sql);

			if (!userDates.isEmpty()) {// Mandatory value

				List<String> multipleGstins = dto.getSgstins();
				String returnPeriod = dto.getReturnPeriod();
				if (!userDates.isEmpty()) {
					query.setParameter("userDates", userDates);
				}
				if (firstDataRecv != null && secondDataRecv != null) {
					query.setParameter("firstDataRecv ", firstDataRecv);
					query.setParameter("secondDataRecv", secondDataRecv);
				} else if (firstDocDate != null && secondDocDate != null) {
					query.setParameter("firstDocDate", firstDocDate);
					query.setParameter("secondDocDate", secondDocDate);
				} else if (firstDerRetPeriod != 0 && secondDerRetPeriod != 0) {
					query.setParameter("firstDerRetPeriod", firstDerRetPeriod);
					query.setParameter("secondDerRetPeriod",
							secondDerRetPeriod);
				}
				if (multipleGstins != null && !multipleGstins.isEmpty()) {
					query.setParameter("gstins", multipleGstins);
				}
				resultList = query.getResultList();

				for (Object[] result : resultList) {
					listOfPairs.add(new Pair<>(
							result[0] != null ? String.valueOf(result[0])
									: null,
							result[1] != null ? String.valueOf(result[1])
									: null));
				}

			}
			return listOfPairs;

		}
		return null;
	}
	
	@Override
	public List<Object[]> findGstr7TransCancelledData(String gstin,
			String retPeriod, String supplyType, ProcessingContext context) {
		String sql = queryBuilder.buildGstr7TransCancelledQuery(gstin,
				retPeriod, supplyType, context);
		return getGstr7TransInvoiceResultSet(gstin, retPeriod, supplyType, sql);
	}

	private List<Object[]> getGstr7TransInvoiceResultSet(String gstin,
			String retPeriod, String supplyType, String sql) {
		List<Object[]> result = null;
		if (!Strings.isNullOrEmpty(sql)) {
			Query query = entityManager.createQuery(sql);
			if (gstin != null && retPeriod != null) {
				query.setParameter("c_DeductorGstin", gstin);
				query.setParameter("c_RetPeriod", retPeriod);
				query.setParameter("o_DeductorGstin", gstin);
				query.setParameter("o_RetPeriod", retPeriod);
			}
			if (supplyType != null) {
				query.setParameter("c_SupplyType", supplyType);
				query.setParameter("o_SupplyType", supplyType);
			}

			result = query.getResultList();

		}
		return result;
	}

	@Override
	public List<Object[]> findGstr7TransInvLevelData(String gstin,
			String retPeriod, String section, Set<Long> orgDocIds,
			ProcessingContext context) {

		LOGGER.info(
				"Entering findGstr7TransInvLevelData with GSTIN: {}, RetPeriod: {}, Section: {}, OrgDocIds: {}",
				gstin, retPeriod, section, orgDocIds);

		PerfUtil.logEventToFile(PerfamanceEventConstants.GSTR1_SAVE_TO_GSTN,
				PerfamanceEventConstants.FIND_INV_DATA_DB_START,
				PerfamanceEventConstants.DocSearchForSaveCustomRepositoryImpl,
				PerfamanceEventConstants.findGstr1InvoiceLevelData,
				PerfamanceEventConstants.GSTIN_Ret_Period_section.concat(":{")
						.concat(gstin).concat(",").concat(retPeriod)
						.concat(section).concat("}"));

		String sql = null;

		if (section.equalsIgnoreCase(APIConstants.TDS)) {
			LOGGER.debug("Building SQL for section TDS");
			section = APIConstants.TDS;
			sql = queryBuilder.buildGstr7TDSQuery(gstin, retPeriod, section,
					orgDocIds, context);

		} else if (section.equalsIgnoreCase(APIConstants.TDSA)) {
			LOGGER.debug("Building SQL for section TDSA");
			section = APIConstants.TDSA;
			sql = queryBuilder.buildGstr7TDSAQuery(gstin, retPeriod, section,
					orgDocIds, context);
		} else {
			LOGGER.warn("Section '{}' is not supported for GSTR7 processing",
					section);
		}

		LOGGER.debug("Generated GSTR7 SQL: {}", sql);

		if (sql == null) {
			LOGGER.error(
					"SQL is null for GSTIN: {}, RetPeriod: {}, Section: {}",
					gstin, retPeriod, section);
			return Collections.emptyList();
		}

		LOGGER.info("Executing GSTR7 SQL query");
		List<Object[]> resultset = getGstr7TransResultSet(gstin, retPeriod, sql,
				section, orgDocIds);

		LOGGER.debug("Fetched result set: {}", resultset);
		LOGGER.info("Exiting findGstr7TransInvLevelData with {} records",
				resultset != null ? resultset.size() : 0);

		return resultset;
	}

	private List<Object[]> getGstr7TransResultSet(String gstin,
			String retPeriod, String sql, String section, Set<Long> orgDocIds) {
		List<Object[]> result = null;
		if (!Strings.isNullOrEmpty(sql)) {
			LOGGER.debug("GSTR7 Transactional sql {} ", sql);
			Query query = entityManager.createQuery(sql);
			if (gstin != null && retPeriod != null) {
				query.setParameter("deductorGstins", gstin);
				query.setParameter("retPeriod", retPeriod);
			}
			if (section != null) {
				query.setParameter("section", section.toUpperCase());
			}

			if (orgDocIds != null && !orgDocIds.isEmpty()) {
				query.setParameter("cdocIds", orgDocIds);
			}
			result = query.getResultList();
		}
		return result;

	}



}
