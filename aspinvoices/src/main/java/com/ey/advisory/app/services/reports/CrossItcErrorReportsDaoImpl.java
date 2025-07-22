package com.ey.advisory.app.services.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.CrossItcVerticalDto;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;
import com.ey.advisory.common.GSTConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahesh.Golla
 *
 */
@Component("CrossItcErrorReportsDaoImpl")
@Slf4j
public class CrossItcErrorReportsDaoImpl implements Gstr1VerticalReportsDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	static String fileType = null;

	@Override
	public List<Object> getGstr1VerticalReports(
			Gstr1VerticalDownloadReportsReqDto request) {

		Long fileId = request.getFileId();
		fileType = request.getFileType();

		StringBuilder buildQuery = new StringBuilder();
		if (fileId != null) {
			buildQuery.append(" HI.FILE_ID= :fileId ");
		}
		String queryStr = createVerticalErrorQueryString(buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Reading query " + queryStr);
		}

		if (fileId != null) {
			q.setParameter("fileId", fileId);
		}

		List<Object[]> list = q.getResultList();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Reading Resultset" + list);
		}
		List<Object> retList = list.parallelStream()
				.map(o -> convertVerticalError(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	private Object convertVerticalError(Object[] arr) {

		Object obj = null;

		CrossItcVerticalDto crossItc = new CrossItcVerticalDto();
		if ((GSTConstants.CROSS_ITC).equalsIgnoreCase(fileType)) {

			crossItc.setIsdGstin(arr[0] != null ? arr[0].toString() : null);
			crossItc.setTaxPeriod(arr[1] != null ? arr[1].toString() : null);
			crossItc.setIgstUsedAsIgst(
					arr[2] != null ? arr[2].toString() : null);
			crossItc.setSgstUsedAsIgst(
					arr[3] != null ? arr[3].toString() : null);
			crossItc.setCgstUsedAsIgst(
					arr[4] != null ? arr[4].toString() : null);
			crossItc.setSgstUsedAsSgst(
					arr[5] != null ? arr[5].toString() : null);
			crossItc.setIgstUsedAsSgst(
					arr[6] != null ? arr[6].toString() : null);
			crossItc.setCgstUsedAsCgst(
					arr[7] != null ? arr[7].toString() : null);
			crossItc.setIgstUsedAsCgst(
					arr[8] != null ? arr[8].toString() : null);
			crossItc.setCessUsedAsCess(
					arr[9] != null ? arr[9].toString() : null);
			crossItc.setAspErrorCode(
					arr[10] != null ? arr[10].toString() : null);
			crossItc.setAspErrorDescription(
					arr[11] != null ? arr[11].toString() : null);
			crossItc.setAspInformationID(
					arr[12] != null ? arr[12].toString() : null);
			crossItc.setAspInformationDescription(
					arr[13] != null ? arr[13].toString() : null);
			obj = crossItc;
		}
		return obj;
	}

	private String createVerticalErrorQueryString(String buildQuery) {
		StringBuilder builder = new StringBuilder();

		if ((GSTConstants.CROSS_ITC).equalsIgnoreCase(fileType)) {

			builder.append("SELECT ISD_GSTIN,RETURN_PERIOD,IGST_USED_AS_IGST,");
			builder.append(
					"SGST_USED_AS_IGST,CGST_USED_AS_IGST,SGST_USED_AS_SGST,");
			builder.append(
					"IGST_USED_AS_SGST,CGST_USED_AS_CGST,IGST_USED_AS_CGST,");
			builder.append("CESS_USED_AS_CESS,");
			builder.append(
					"TRIM(', ' FROM IFNULL(ERROR_CODE_ASP,'') ) AS ERROR_CODE_ASP,");
			builder.append(
					"TRIM(', ' FROM IFNULL(ERROR_DESCRIPTION_ASP,'') )  AS ERROR_DESCRIPTION_ASP,");
			builder.append(
					"TRIM(', ' FROM IFNULL(INFO_ERROR_CODE_ASP,'') )  AS INFO_ERROR_CODE_ASP,");
			builder.append(
					"TRIM(', ' FROM IFNULL(INFO_ERROR_DESCRIPTION_ASP,'') )  AS INFO_ERROR_DESCRIPTION_ASP ");
			builder.append("FROM CROSS_ITC_AS_ENTERED HI LEFT OUTER JOIN  ");
			builder.append("TF_VERTICAL_ERROR_INFO() ERR ON  ");
			builder.append(
					"HI.ID = ERR.COMMON_ID  AND HI.FILE_ID=ERR.FILE_ID ");
			builder.append(
					"LEFT OUTER JOIN FILE_STATUS FIL	ON HI.FILE_ID=FIL.ID ");
			builder.append(
					"AND ERR.FILE_ID=FIL.ID AND ERR.INV_KEY=HI.DOC_KEY ");
			builder.append("WHERE HI.IS_ERROR = TRUE  AND  ");
			builder.append(buildQuery);
		}
		return builder.toString();
	}

}
