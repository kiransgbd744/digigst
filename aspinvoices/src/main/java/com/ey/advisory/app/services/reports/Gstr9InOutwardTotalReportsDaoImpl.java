package com.ey.advisory.app.services.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.Gstr9InOutwardVerticalDto;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;
import com.ey.advisory.common.GSTConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("Gstr9InOutwardTotalReportsDaoImpl")
@Slf4j
public class Gstr9InOutwardTotalReportsDaoImpl implements Gstr1VerticalReportsDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	static String fileType = null;

	@Override
	public List<Object> getGstr1VerticalReports(Gstr1VerticalDownloadReportsReqDto request) {

		Long fileId = request.getFileId();
		fileType = request.getFileType();

		StringBuilder buildQuery = new StringBuilder();
		if (fileId != null) {
			buildQuery.append(" HI.FILE_ID= :fileId ");
		}
		String queryStr = createVerticalTotalQueryString(buildQuery.toString());
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
		List<Object> retList = list.parallelStream().map(o -> convertVerticalTotal(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	private Object convertVerticalTotal(Object[] arr) {

		Object obj = null;
		Gstr9InOutwardVerticalDto gstr9InOut = new Gstr9InOutwardVerticalDto();
		if ((GSTConstants.INWARD_OUTWARD).equalsIgnoreCase(fileType)) {

			gstr9InOut.setAspErrorCode(arr[0] != null ? arr[0].toString() : null);
			gstr9InOut.setAspErrorDescription(arr[1] != null ? arr[1].toString() : null);

			gstr9InOut.setGstin(arr[2] != null ? arr[2].toString() : null);
			gstr9InOut.setFy(arr[3] != null ? arr[3].toString() : null);
			gstr9InOut.setTableNo(arr[4] != null ? arr[4].toString() : null);
			gstr9InOut.setNatureOfSup(arr[5] != null ? arr[5].toString() : null);
			gstr9InOut.setTaxableVal(arr[6] != null ? arr[6].toString() : null);
			gstr9InOut.setIgst(arr[7] != null ? arr[7].toString() : null);
			gstr9InOut.setCgst(arr[8] != null ? arr[8].toString() : null);
			gstr9InOut.setSgst(arr[9] != null ? arr[9].toString() : null);
			gstr9InOut.setCess(arr[10] != null ? arr[10].toString() : null);
			gstr9InOut.setInterst(arr[11] != null ? arr[11].toString() : null);
			gstr9InOut.setLateFee(arr[12] != null ? arr[12].toString() : null);
			gstr9InOut.setPenalty(arr[13] != null ? arr[13].toString() : null);
			gstr9InOut.setOthers(arr[14] != null ? arr[14].toString() : null);
			obj = gstr9InOut;
		}
		return obj;
	}

	private String createVerticalTotalQueryString(String buildQuery) {
		StringBuilder builder = new StringBuilder();

		if ((GSTConstants.INWARD_OUTWARD).equalsIgnoreCase(fileType)) {

			builder.append(" SELECT TRIM(', ' FROM IFNULL(ERROR_CODE_ASP,'') )  AS ERROR_CODE_ASP, ");
			builder.append(" TRIM(', ' FROM IFNULL(ERROR_DESCRIPTION_ASP,'') )   AS ERROR_DESCRIPTION_ASP, ");

			builder.append(
					"GSTIN,FY,TABLE_NUMBER,NATURE_OF_SUPPLIES,TAXABLE_VALUE,IGST,CGST,SGST,CESS,INTEREST,LATE_FEE,PENALTY,OTHER ");

			builder.append("FROM GSTR9_OUTWARD_INWARD_AS_ENTERED HI ");
			builder.append("LEFT OUTER JOIN TF_VERTICAL_ERROR_INFO() ERR ");
			builder.append("ON HI.ID = ERR.COMMON_ID AND HI.FILE_ID=ERR.FILE_ID ");
			builder.append(" LEFT OUTER JOIN FILE_STATUS FIL ON  ");
			builder.append("HI.FILE_ID=FIL.ID AND ERR.FILE_ID=FIL.ID  ");
			builder.append(" AND ERR.INV_KEY=HI.DOC_KEY    WHERE ");
			builder.append(buildQuery);
		}
		return builder.toString();
	}

}
