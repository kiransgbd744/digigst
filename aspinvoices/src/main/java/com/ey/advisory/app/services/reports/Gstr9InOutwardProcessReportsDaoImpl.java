/**
 * 
 */
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
@Component("Gstr9InOutwardProcessReportsDaoImpl")
@Slf4j
public class Gstr9InOutwardProcessReportsDaoImpl
		implements Gstr1VerticalReportsDao {

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

		@SuppressWarnings("unchecked")
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
		Gstr9InOutwardVerticalDto gstr9InOut = new Gstr9InOutwardVerticalDto();
		if ((GSTConstants.INWARD_OUTWARD).equalsIgnoreCase(fileType)) {

			gstr9InOut.setGstin(arr[0] != null ? arr[0].toString() : null);
			gstr9InOut.setFy(arr[1] != null ? arr[1].toString() : null);
			String tableNum = arr[2] != null ? arr[2].toString() : null;
			gstr9InOut.setTableNo(tableNum);
			gstr9InOut
					.setNatureOfSup(arr[3] != null ? arr[3].toString() : null);
			if (tableNum != null && tableNum.equalsIgnoreCase("9")) {
				gstr9InOut.setTaxableVal(
						arr[4] != null ? arr[4].toString() : null);
			} else {
				gstr9InOut.setTaxableVal(
						arr[15] != null ? arr[15].toString() : null);
			}
			gstr9InOut.setIgst(arr[5] != null ? arr[5].toString() : null);
			gstr9InOut.setCgst(arr[6] != null ? arr[6].toString() : null);
			gstr9InOut.setSgst(arr[7] != null ? arr[7].toString() : null);
			gstr9InOut.setCess(arr[8] != null ? arr[8].toString() : null);
			gstr9InOut.setInterst(arr[9] != null ? arr[9].toString() : null);
			gstr9InOut.setLateFee(arr[10] != null ? arr[10].toString() : null);
			gstr9InOut.setPenalty(arr[11] != null ? arr[11].toString() : null);
			gstr9InOut.setOthers(arr[12] != null ? arr[12].toString() : null);
			gstr9InOut.setAspInformationID(
					arr[13] != null ? arr[13].toString() : null);
			gstr9InOut.setAspInformationDescription(
					arr[14] != null ? arr[14].toString() : null);
			obj = gstr9InOut;
		}
		return obj;
	}

	private String createVerticalErrorQueryString(String buildQuery) {

		StringBuilder builder = new StringBuilder();

		if ((GSTConstants.INWARD_OUTWARD).equalsIgnoreCase(fileType)) {

			builder.append(
					"SELECT GSTIN,FY,SUBSECTION,NATUREOFSUPPLIES,TXPYBLE,IGST,CGST,SGST,CESS,INTR,FEE,PEN,OTH, ");
			builder.append(
					"TRIM(', ' FROM IFNULL(INFO_ERROR_CODE_ASP,'') )  AS INFO_ERROR_CODE_ASP, ");
			builder.append(
					"TRIM(', ' FROM IFNULL(INFO_ERROR_DESCRIPTION_ASP,'') ) AS INFO_ERROR_DESCRIPTION_ASP,TAXABLEVALUE ");
			builder.append("FROM  TBL_GSTR9_USER_INPUT HI LEFT OUTER JOIN  ");
			builder.append("TF_VERTICAL_ERROR_INFO() ERR ON  ");
			builder.append(
					"HI.AS_ENTERED_ID= ERR.COMMON_ID AND HI.FILE_ID =  ");
			builder.append("ERR.FILE_ID LEFT OUTER JOIN  FILE_STATUS FIL  ");
			builder.append("ON HI.FILE_ID = FIL.ID AND ERR.FILE_ID = FIL.ID  ");
			builder.append("AND ERR.INV_KEY=HI.DOC_KEY  WHERE ");
			builder.append(buildQuery);
		}
		return builder.toString();
	}

}
