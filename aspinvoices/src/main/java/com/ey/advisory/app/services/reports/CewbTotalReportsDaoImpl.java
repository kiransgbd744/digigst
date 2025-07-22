package com.ey.advisory.app.services.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.views.client.CewbVerticalDto;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;
import com.ey.advisory.common.GSTConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("CewbTotalReportsDaoImpl")
@Slf4j
public class CewbTotalReportsDaoImpl implements Gstr1VerticalReportsDao {

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
		List<Object> retList = list.parallelStream()
				.map(o -> convertVerticalTotal(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	private Object convertVerticalTotal(Object[] arr) {

		Object obj = null;

		CewbVerticalDto cewbErr = new CewbVerticalDto();
		if ((GSTConstants.CEWB).equalsIgnoreCase(fileType)) {

			cewbErr.setSNo(arr[0] != null ? arr[0].toString() : null);
			cewbErr.setEwbNo(arr[1] != null ? arr[1].toString() : null);
			cewbErr.setFromPlcae(arr[2] != null ? arr[2].toString() : null);
			cewbErr.setFromState(arr[3] != null ? arr[3].toString() : null);
			cewbErr.setVehicleNo(arr[4] != null ? arr[4].toString() : null);
			cewbErr.setTransMode(arr[5] != null ? arr[5].toString() : null);
			cewbErr.setTransDocNo(arr[6] != null ? arr[6].toString() : null);
			cewbErr.setTransDocDate(arr[7] != null ? arr[7].toString() : null);
			cewbErr.setGstin(arr[8] != null ? arr[8].toString() : null);
			cewbErr.setAspErrorCode(arr[9] != null ? arr[9].toString() : null);
			cewbErr.setAspErrorDescription(
					arr[10] != null ? arr[10].toString() : null);
			cewbErr.setAspInformationID(
					arr[11] != null ? arr[11].toString() : null);
			cewbErr.setAspInformationDescription(
					arr[12] != null ? arr[12].toString() : null);
			obj = cewbErr;
		}
		return obj;
	}
	private String createVerticalTotalQueryString(String buildQuery) {
		StringBuilder builder = new StringBuilder();

		if ((GSTConstants.CEWB).equalsIgnoreCase(fileType)) {
			builder.append(
					"SELECT SL_NO,EWB_NO,FROM_PLACE,FROM_STATE,VEHICLE_NO,");
			builder.append("TRANS_MODE,TRANS_DOC_NUM,TRANS_DOC_DATE,GSTIN,");
			builder.append(
					"TRIM(', ' FROM IFNULL(ERROR_CODE_ASP,'') )  AS ERROR_CODE_ASP,");
			builder.append(
					"TRIM(', ' FROM IFNULL(ERROR_DESCRIPTION_ASP,'') )   AS ERROR_DESCRIPTION_ASP,");
			builder.append(
					"TRIM(', ' FROM IFNULL(INFO_ERROR_CODE_ASP,'') )  AS INFO_ERROR_CODE_ASP, ");
			builder.append(
					"TRIM(', ' FROM IFNULL(INFO_ERROR_DESCRIPTION_ASP,'') ) AS INFO_ERROR_DESCRIPTION_ASP  ");
			builder.append("FROM CEWB_AS_ENTERED HI ");
			builder.append(" LEFT OUTER JOIN TF_VERTICAL_ERROR_INFO() ERR ");
			builder.append(
					"ON HI.ID = ERR.COMMON_ID AND HI.FILE_ID=ERR.FILE_ID  ");
			builder.append("LEFT OUTER JOIN FILE_STATUS FIL ON ");
			builder.append("HI.FILE_ID=FIL.ID AND ERR.FILE_ID=FIL.ID ");
			builder.append("AND ERR.INV_KEY=HI.DOC_KEY WHERE  ");
			builder.append(buildQuery);

		}
		return builder.toString();
	}

}
