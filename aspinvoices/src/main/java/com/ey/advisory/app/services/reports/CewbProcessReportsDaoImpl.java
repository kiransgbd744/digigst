/**
 * 
 */
package com.ey.advisory.app.services.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.CewbVerticalDto;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;
import com.ey.advisory.common.GSTConstants;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("CewbProcessReportsDaoImpl")
@Slf4j
public class CewbProcessReportsDaoImpl implements Gstr1VerticalReportsDao {

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

		CewbVerticalDto cewbProcess = new CewbVerticalDto();

		if ((GSTConstants.CEWB).equalsIgnoreCase(fileType)) {

			cewbProcess.setSNo(arr[0] != null ? arr[0].toString() : null);
			cewbProcess.setEwbNo(arr[1] != null ? arr[1].toString() : null);
			cewbProcess.setFromPlcae(arr[2] != null ? arr[2].toString() : null);
			cewbProcess.setFromState(arr[3] != null ? arr[3].toString() : null);
			cewbProcess.setVehicleNo(arr[4] != null ? arr[4].toString() : null);
			cewbProcess.setTransMode(arr[5] != null ? arr[5].toString() : null);
			cewbProcess
					.setTransDocNo(arr[6] != null ? arr[6].toString() : null);
			cewbProcess
					.setTransDocDate(arr[7] != null ? arr[7].toString() : null);
			cewbProcess.setGstin(arr[8] != null ? arr[8].toString() : null);
			cewbProcess.setAspInformationID(
					arr[9] != null ? arr[9].toString() : null);
			cewbProcess.setAspInformationDescription(
					arr[10] != null ? arr[10].toString() : null);
			obj = cewbProcess;
		}

		return obj;
	}

	private String createVerticalErrorQueryString(String buildQuery) {

		StringBuilder builder = new StringBuilder();

		if ((GSTConstants.CEWB).equalsIgnoreCase(fileType)) {
			builder.append(
					"SELECT SL_NO,EWB_NO,FROM_PLACE,FROM_STATE,VEHICLE_NO, ");
			builder.append("TRANS_MODE,TRANS_DOC_NUM,TRANS_DOC_DATE,GSTIN, ");
			builder.append(
					"TRIM(', ' FROM IFNULL(INFO_ERROR_CODE_ASP,'') )  AS INFO_ERROR_CODE_ASP, ");
			builder.append(
					"TRIM(', ' FROM IFNULL(INFO_ERROR_DESCRIPTION_ASP,'') ) AS INFO_ERROR_DESCRIPTION_ASP ");
			builder.append(" FROM  CEWB_PROCESSED HI LEFT OUTER JOIN ");
			builder.append("TF_VERTICAL_ERROR_INFO() ERR ON ");
			builder.append("HI.AS_ENTERED_ID= ERR.COMMON_ID AND ");
			builder.append(
					" HI.FILE_ID = ERR.FILE_ID LEFT OUTER JOIN  FILE_STATUS FIL ");
			builder.append("ON HI.FILE_ID = FIL.ID AND ERR.FILE_ID = FIL.ID ");
			builder.append("AND ERR.INV_KEY=HI.DOC_KEY  WHERE ");
			builder.append(buildQuery);
		}
		return builder.toString();
	}

}
