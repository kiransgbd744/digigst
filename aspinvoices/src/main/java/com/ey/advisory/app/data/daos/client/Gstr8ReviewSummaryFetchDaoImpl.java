package com.ey.advisory.app.data.daos.client;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.gstr8.Gstr8UploadPsdRepository;
import com.ey.advisory.app.data.views.client.Gstr8ReviewSummaryRespDto;
import com.ey.advisory.app.docs.dto.Gstr8ReviewSummaryItemsRespDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr8ReviewSummaryReqDto;
import com.ey.advisory.common.GenUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Component("Gstr8ReviewSummaryFetchDaoImpl")
public class Gstr8ReviewSummaryFetchDaoImpl {

	public static void main(String[] args) {
		Gstr8ReviewSummaryFetchDaoImpl s = new Gstr8ReviewSummaryFetchDaoImpl();
		StringBuilder query = s.createQueryString("27ASMPC5297P1C7", "022024");
		System.out.println(query.toString());

		System.out.println(
				s.createQueryForLineItems("27ASMPC5297P1C7", "022024", "TCS"));

		System.out.println("--------------------");
		System.out.println(
				s.createQueryForLineItems("27ASMPC5297P1C7", "022024", "TCSA"));

	}

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr8ReviewSummaryFetchDaoImpl.class);

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr8UploadPsdRepository")
	private Gstr8UploadPsdRepository gstr8UploadPsdRepository;

	public List<Gstr8ReviewSummaryRespDto> loadGstr8ReviewSummary(
			Gstr8ReviewSummaryReqDto reqDto) {

		String taxPeriod = reqDto.getTaxPeriod();
		String gstin = reqDto.getGstin();

		StringBuilder queryStr = createQueryString(gstin, taxPeriod);
		LOGGER.debug("outQueryStr-->" + queryStr);

		List<Gstr8ReviewSummaryRespDto> finalDtoList = new ArrayList<>();
		try {
			Query Q = entityManager.createNativeQuery(queryStr.toString());
			if (taxPeriod != null) {
				Q.setParameter("taxPeriod", taxPeriod);
			}
			if (gstin != null) {
				Q.setParameter("gstin", gstin);
			}

			@SuppressWarnings("unchecked")
			List<Object[]> Qlist = Q.getResultList();
			finalDtoList = convertGstr8RecordsIntoObject(Qlist, gstin,
					taxPeriod);

		} catch (Exception e) {
			LOGGER.error("Error in data process ->", e);
		}
		return finalDtoList;
	}

	private List<Gstr8ReviewSummaryRespDto> convertGstr8RecordsIntoObject(
			List<Object[]> objectDataList, String gstin, String taxPeriod) {
		List<Gstr8ReviewSummaryRespDto> summaryList = new ArrayList<Gstr8ReviewSummaryRespDto>();
		if (!objectDataList.isEmpty() && objectDataList.size() > 0) {
			for (Object[] data : objectDataList) {
				Gstr8ReviewSummaryRespDto dto = new Gstr8ReviewSummaryRespDto();
				String section = String.valueOf(data[0]);
				dto.setSection(section);
				dto.setAspGrossSuppliesMade(data[1] != null
						? (BigDecimal) data[1] : BigDecimal.ZERO);
				dto.setAspGrossSuppliesReturned(data[2] != null
						? (BigDecimal) data[2] : BigDecimal.ZERO);
				dto.setAspNetSupplies(data[3] != null ? (BigDecimal) data[3]
						: BigDecimal.ZERO);
				dto.setAspIgst(data[4] != null ? (BigDecimal) data[4]
						: BigDecimal.ZERO);
				dto.setAspCgst(data[5] != null ? (BigDecimal) data[5]
						: BigDecimal.ZERO);
				dto.setAspSgst(data[6] != null ? (BigDecimal) data[6]
						: BigDecimal.ZERO);

				dto.setGstnGrossSuppliesMade(data[7] != null
						? (BigDecimal) data[7] : BigDecimal.ZERO);
				dto.setGstnGrossSuppliesReturned(data[8] != null
						? (BigDecimal) data[8] : BigDecimal.ZERO);
				dto.setGstnNetSupplies(data[9] != null ? (BigDecimal) data[9]
						: BigDecimal.ZERO);
				dto.setGstnIgst(data[10] != null ? (BigDecimal) data[10]
						: BigDecimal.ZERO);
				dto.setGstnCgst(data[11] != null ? (BigDecimal) data[11]
						: BigDecimal.ZERO);
				dto.setGstnSgst(data[12] != null ? (BigDecimal) data[12]
						: BigDecimal.ZERO);
				dto.setAspCount(data[13] != null ? GenUtil.getBigInteger(data[13])
						: BigInteger.ZERO);
				dto.setGstnCount(data[14] != null ? GenUtil.getBigInteger(data[14])
						: BigInteger.ZERO);
				if (section != null && (section.equalsIgnoreCase("TCS")
						|| section.equalsIgnoreCase("TCSA"))) {
					StringBuilder queryStr = createQueryForLineItems(gstin,
							taxPeriod, section);
					Query Q = entityManager
							.createNativeQuery(queryStr.toString());
					if (taxPeriod != null) {
						Q.setParameter("taxPeriod", taxPeriod);
					}
					if (gstin != null) {
						Q.setParameter("gstin", gstin);
					}
					if (section != null) {
						Q.setParameter("section", section);
					}
					LOGGER.debug("inQueryStr-->" + queryStr);
					@SuppressWarnings("unchecked")
					List<Object[]> Qlist1 = Q.getResultList();
					List<Gstr8ReviewSummaryItemsRespDto> items = convertGstr8ItemsIntoObject(
							Qlist1, gstin, taxPeriod);
					dto.setItems(items);
				}
				summaryList.add(dto);
			}
		}
		return summaryList;

	}

	private List<Gstr8ReviewSummaryItemsRespDto> convertGstr8ItemsIntoObject(
			List<Object[]> objectDataList, String gstin, String taxPeriod) {
		List<Gstr8ReviewSummaryItemsRespDto> itemList = new ArrayList<Gstr8ReviewSummaryItemsRespDto>();
		if (!objectDataList.isEmpty() && objectDataList.size() > 0) {
			for (Object[] data : objectDataList) {
				Gstr8ReviewSummaryItemsRespDto dto = new Gstr8ReviewSummaryItemsRespDto();
				String section = String.valueOf(data[0]);
				dto.setSection(section);
				dto.setAspGrossSuppliesMade(data[1] != null
						? (BigDecimal) data[1] : BigDecimal.ZERO);
				dto.setAspGrossSuppliesReturned(data[2] != null
						? (BigDecimal) data[2] : BigDecimal.ZERO);
				itemList.add(dto);
			}
		}
		return itemList;

	}

	public StringBuilder createQueryString(String gstin, String taxPeriod) {

		StringBuilder queryBuilder = new StringBuilder();
		StringBuilder gstnSummaryBuilder = new StringBuilder();
		if (taxPeriod != null) {
			queryBuilder.append(" AND psd.RET_PERIOD IN (:taxPeriod)");
			gstnSummaryBuilder
					.append(" AND smry.RETURN_PERIOD IN (:taxPeriod)");

		}
		if (gstin != null) {
			queryBuilder.append(" AND psd.GSTIN IN (:gstin)");
			gstnSummaryBuilder.append(" AND smry.GSTIN IN (:gstin)");

		}

		String condition = queryBuilder.toString();
		StringBuilder bufferString = new StringBuilder();
		bufferString.append("SELECT 'TCS' AS SECTION, "
				+ "SUM(IFNULL(psd.SUPPLIES_TO_REGISTERED, 0)) + "
				+ "SUM(IFNULL(psd.SUPPLIES_TO_UNREGISTERED, 0)) AS GROSS_SUPPLIES_MADE,"
				+ " SUM(IFNULL(psd.RETURNS_FROM_REGISTERED, 0)) + SUM(IFNULL(psd.RETURNS_FROM_UNREGISTERED, 0)) AS GROSS_SUPPLIES_RETURNED,"
				+ " SUM(IFNULL(psd.NET_SUPPLIES, 0)) AS NET_SUPPLIES, SUM(IFNULL(psd.IGST_AMT, 0)) AS IGST_AMT, "
				+ "SUM(IFNULL(psd.CGST_AMT, 0)) AS CGST_AMT, SUM(IFNULL(psd.SGST_AMT, 0)) AS SGST_AMT, "
				+ "SUM( IFNULL(smry.GROSS_SUPP_MADE, 0) ) AS GROSS_SUPP_MADE, SUM( IFNULL(smry.GROSS_SUPP_RETURNED, 0) ) AS GROSS_SUPP_RETURNED, SUM( IFNULL(smry.TOT_AMT_DEDUCTED, 0) ) AS TOT_AMT_DEDUCTED, SUM( IFNULL(smry.TOT_IGST, 0) ) AS TOT_IGST, SUM( IFNULL(smry.TOT_CGST, 0) ) AS TOT_CGST, SUM( IFNULL(smry.TOT_SGST, 0) ) AS TOT_SGST, COUNT(psd.section) AS sectionCount, SUM(smry.TOT_RECORD_CNT) as gstnCount FROM TBL_GSTR8_UPLOAD_PSD psd LEFT JOIN GETGSTR8_SUMMARY smry "
				+ "ON psd.GSTIN = smry.GSTIN AND psd.RET_PERIOD = smry.RETURN_PERIOD "
				+ "AND psd.section = smry.section AND smry.IS_DELETE = false "
				+ "WHERE psd.SECTION = 'TCS' AND psd.IS_ACTIVE = TRUE "
				+ "AND psd.SUPPLY_TYPE = 'TAX'  " + condition);

		bufferString.append(" UNION ALL SELECT 'TCSA' AS SECTION, "
				+ "SUM(IFNULL(psd.SUPPLIES_TO_REGISTERED, 0)) + "
				+ "SUM(IFNULL(psd.SUPPLIES_TO_UNREGISTERED, 0)) AS GROSS_SUPPLIES_MADE,"
				+ " SUM(IFNULL(psd.RETURNS_FROM_REGISTERED, 0)) + SUM(IFNULL(psd.RETURNS_FROM_UNREGISTERED, 0)) AS GROSS_SUPPLIES_RETURNED,"
				+ " SUM(IFNULL(psd.NET_SUPPLIES, 0)) AS NET_SUPPLIES, SUM(IFNULL(psd.IGST_AMT, 0)) AS IGST_AMT, "
				+ "SUM(IFNULL(psd.CGST_AMT, 0)) AS CGST_AMT, SUM(IFNULL(psd.SGST_AMT, 0)) AS SGST_AMT, "
				+ "SUM( IFNULL(smry.GROSS_SUPP_MADE, 0) ) AS GROSS_SUPP_MADE, SUM( IFNULL(smry.GROSS_SUPP_RETURNED, 0) ) AS GROSS_SUPP_RETURNED, SUM( IFNULL(smry.TOT_AMT_DEDUCTED, 0) ) AS TOT_AMT_DEDUCTED, SUM( IFNULL(smry.TOT_IGST, 0) ) AS TOT_IGST, SUM( IFNULL(smry.TOT_CGST, 0) ) AS TOT_CGST, SUM( IFNULL(smry.TOT_SGST, 0) ) AS TOT_SGST, COUNT(psd.section) AS sectionCount, SUM(smry.TOT_RECORD_CNT) as gstnCount FROM TBL_GSTR8_UPLOAD_PSD psd LEFT JOIN GETGSTR8_SUMMARY smry "
				+ "ON psd.GSTIN = smry.GSTIN AND psd.RET_PERIOD = smry.RETURN_PERIOD AND psd.section = smry.section AND smry.IS_DELETE = false "
				+ "WHERE psd.SECTION = 'TCSA' AND psd.IS_ACTIVE = TRUE "
				+ "AND psd.SUPPLY_TYPE = 'TAX'  " + condition);

		bufferString.append(" UNION ALL SELECT 'URD' AS SECTION, "
				+ "SUM(IFNULL(psd.SUPPLIES_TO_REGISTERED, 0)) + "
				+ "SUM(IFNULL(psd.SUPPLIES_TO_UNREGISTERED, 0)) AS GROSS_SUPPLIES_MADE,"
				+ " SUM(IFNULL(psd.RETURNS_FROM_REGISTERED, 0)) + SUM(IFNULL(psd.RETURNS_FROM_UNREGISTERED, 0)) AS GROSS_SUPPLIES_RETURNED,"
				+ " SUM(IFNULL(psd.NET_SUPPLIES, 0)) AS NET_SUPPLIES, 0, 0, 0, "
				+ "SUM( IFNULL(smry.GROSS_SUPP_MADE, 0) ) AS GROSS_SUPP_MADE, SUM( IFNULL(smry.GROSS_SUPP_RETURNED, 0) ) AS GROSS_SUPP_RETURNED, "
				+ "SUM( IFNULL(smry.TOT_AMT_DEDUCTED, 0) ) AS TOT_AMT_DEDUCTED, "
				+ "0, 0, 0, COUNT(psd.section) AS sectionCount, SUM(smry.TOT_RECORD_CNT) as gstnCount "
				+ " FROM TBL_GSTR8_UPLOAD_PSD psd LEFT JOIN GETGSTR8_SUMMARY smry "
				+ "ON psd.GSTIN = smry.GSTIN AND psd.RET_PERIOD = smry.RETURN_PERIOD AND psd.section = smry.section AND smry.IS_DELETE = false "
				+ "WHERE psd.SECTION = 'URD' AND psd.IS_ACTIVE = TRUE "
				+ "AND psd.SUPPLY_TYPE = 'TAX'  " + condition);
		bufferString.append(" UNION ALL SELECT 'URDA' AS SECTION, "
				+ "SUM(IFNULL(psd.SUPPLIES_TO_REGISTERED, 0)) + "
				+ "SUM(IFNULL(psd.SUPPLIES_TO_UNREGISTERED, 0)) AS GROSS_SUPPLIES_MADE,"
				+ " SUM(IFNULL(psd.RETURNS_FROM_REGISTERED, 0)) + SUM(IFNULL(psd.RETURNS_FROM_UNREGISTERED, 0)) AS GROSS_SUPPLIES_RETURNED,"
				+ " SUM(IFNULL(psd.NET_SUPPLIES, 0)) AS NET_SUPPLIES, 0, "
				+ "0, 0, "
				+ "SUM( IFNULL(smry.GROSS_SUPP_MADE, 0) ) AS GROSS_SUPP_MADE, SUM( IFNULL(smry.GROSS_SUPP_RETURNED, 0) ) AS GROSS_SUPP_RETURNED, SUM( IFNULL(smry.TOT_AMT_DEDUCTED, 0) ) AS TOT_AMT_DEDUCTED, "
				+ "0, 0, 0, COUNT(psd.section) AS sectionCount, SUM(smry.TOT_RECORD_CNT) as gstnCount "
				+ " FROM TBL_GSTR8_UPLOAD_PSD psd LEFT JOIN GETGSTR8_SUMMARY smry "
				+ "ON psd.GSTIN = smry.GSTIN AND psd.RET_PERIOD = smry.RETURN_PERIOD AND psd.section = smry.section AND smry.IS_DELETE = false "
				+ "WHERE psd.SECTION = 'URDA' AND psd.IS_ACTIVE = TRUE "
				+ "AND psd.SUPPLY_TYPE = 'TAX' " + condition);

		return bufferString;
	}

	public StringBuilder createQueryForLineItems(String gstin, String taxPeriod,
			String section) {

		StringBuilder queryBuilder = new StringBuilder();
		if (taxPeriod != null) {
			queryBuilder.append(" AND RET_PERIOD IN (:taxPeriod)");
		}

		if (gstin != null) {
			queryBuilder.append(" AND GSTIN IN (:gstin)");
		}

		if (section != null) {
			queryBuilder.append(" AND SECTION IN (:section)");
		}

		String condition = queryBuilder.toString();
		StringBuilder bufferString = new StringBuilder();
		bufferString.append(" SELECT 'Supplies to Registered' AS SECTION, "
				+ "SUM(IFNULL(SUPPLIES_TO_REGISTERED,0)) AS GROSS_SUPPLIES_MADE, "
				+ "SUM(IFNULL(RETURNS_FROM_REGISTERED,0)) AS GROSS_SUPPLIES_RETURNED "
				+ "FROM TBL_GSTR8_UPLOAD_PSD WHERE IS_ACTIVE=TRUE AND SUPPLY_TYPE = 'TAX' ");
		if (!condition.equals("")) {
			bufferString.append(condition);
		}
		bufferString
				.append(" UNION ALL SELECT 'Supplies to Unregistered' AS SECTION, "
						+ "SUM(IFNULL(SUPPLIES_TO_UNREGISTERED,0)) AS SUPPLIES_TO_UNREGISTERED, "
						+ "SUM(IFNULL(RETURNS_FROM_UNREGISTERED,0)) AS GROSS_SUPPLIES_RETURNED "
						+ "FROM TBL_GSTR8_UPLOAD_PSD WHERE IS_ACTIVE=TRUE AND SUPPLY_TYPE = 'TAX' ");
		if (!condition.equals("")) {
			bufferString.append(condition);
		}
		bufferString.append(" ORDER BY SECTION ASC ");
		return bufferString;
	}

}
