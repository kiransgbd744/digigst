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

import com.ey.advisory.app.data.views.client.NilNonProcessedRecordsDto;
import com.ey.advisory.app.docs.dto.Gstr1VerticalDownloadReportsReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Balakrishna.S
 *
 * 
 */
@Slf4j
@Component("NilNonVerticalProcessedInfoReportsDaoImpl")
public class NilNonVerticalProcessedInfoReportsDaoImpl implements NilNonVerticalDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public List<Object> getNilnonVerticalReports(Gstr1VerticalDownloadReportsReqDto request) {

		Long fileId = request.getFileId();
String dataType = request.getDataType();
		StringBuilder buildQuery = new StringBuilder();
		if (fileId != null) {
			buildQuery.append(" AND HI.FILE_ID= :fileId ");
		}
		String queryStr = null;
		if (dataType != null && "GSTR1A".equalsIgnoreCase(dataType))
		 queryStr = createGstr1aVerticalTotalQueryString(buildQuery.toString());
		else
			queryStr = createVerticalTotalQueryString(buildQuery.toString());
		Query q = entityManager.createNativeQuery(queryStr);

		LOGGER.debug("Reading query " + queryStr);

		if (fileId != null) {
			q.setParameter("fileId", fileId);
		}

		List<Object[]> list = q.getResultList();

		LOGGER.debug("Reading Resultset" + list);
		List<Object> retList = list.parallelStream().map(o -> convertVerticalTotal(o))
				.collect(Collectors.toCollection(ArrayList::new));
		return retList;
	}

	private NilNonProcessedRecordsDto convertVerticalTotal(Object[] arr) {
		NilNonProcessedRecordsDto obj = new NilNonProcessedRecordsDto();

		obj.setgSTIN(arr[0] != null ? arr[0].toString() : null);
		obj.setReturnPeriod(arr[1] != null ? arr[1].toString() : null);
		obj.setnILInterStateRegistered(arr[2] != null ? arr[2].toString() : null);
		obj.setnILIntraStateRegistered(arr[3] != null ? arr[3].toString() : null);
		obj.setnILInterStateUnRegistered(arr[4] != null ? arr[4].toString() : null);
		obj.setnILIntraStateUnRegistered(arr[5] != null ? arr[5].toString() : null);
		obj.setExtInterStateRegistered(arr[6] != null ? arr[6].toString() : null);
		obj.seteXTIntraStateRegistered(arr[7] != null ? arr[7].toString() : null);
		obj.seteXTInterStateUnRegistered(arr[8] != null ? arr[8].toString() : null);
		obj.seteXTIntraStateUnRegistered(arr[9] != null ? arr[9].toString() : null);
		obj.setnONInterStateRegistered(arr[10] != null ? arr[10].toString() : null);
		obj.setnONIntraStateRegistered(arr[11] != null ? arr[11].toString() : null);
		obj.setnONInterStateUnRegistered(arr[12] != null ? arr[12].toString() : null);
		obj.setnONIntraStateUnRegistered(arr[13] != null ? arr[13].toString() : null);
		obj.setHsn(arr[14] != null ? arr[14].toString() : null);
		obj.setDescription(arr[15] != null ? arr[15].toString() : null);
		obj.setUqc(arr[16] != null ? arr[16].toString() : null);
		obj.setQuantity(arr[17] != null ? arr[17].toString() : null);
		
	//	obj.setaSPErrorCode(arr[18] != null ? arr[18].toString() : null);
	//	obj.setaSPErrorDescription(arr[19] != null ? arr[19].toString() : null);
		 
		obj.setaSPinformationCode(arr[18] != null ? arr[18].toString() : null);
		obj.setaSPinformationDescription(arr[19] != null ? arr[19].toString() : null);

		return obj;
	}

	private String createVerticalTotalQueryString(String buildQuery) {
		return "select SUPPLIER_GSTIN,RETURN_PERIOD,NIL_INTERSTATE_REG," 
				+ "NIL_INTRASTATE_REG,NIL_INTERSTATE_UNREG,"
				+ "NIL_INTRASTATE_UNREG,EXT_INTERSTATE_REG ," 
				+ "EXT_INTRASTATE_REG ,EXT_INTERSTATE_UNREG,"
				+ "EXT_INTRASTATE_UNREG, NON_INTERSTATE_REG  ," 
				+ "NON_INTRASTATE_REG ,NON_INTERSTATE_UNREG ,"
				+ "NON_INTRASTATE_UNREG,ITM_HSNSAC," 
				+ "ITM_DESCRIPTION,ITM_UQC,ITM_QTY," 
			//	+ "ERR.ERROR_CODE_ASP,"
			//	+ "ERR.ERROR_DESCRIPTION_ASP,"
				+ "ERR.INFO_ERROR_CODE_ASP,"
				+ "ERR.INFO_ERROR_DESCRIPTION_ASP " 
				+ "FROM GSTR1_AS_ENTERED_NILEXTNON HI "
				+ "LEFT OUTER JOIN TF_GSTR1_ERROR_INFO() ERR "
				+ "ON HI.ID = ERR.COMMON_ID AND  HI.FILE_ID = ERR.FILE_ID " 
				+ "WHERE IS_DELETE = FALSE  AND INFO_ERROR_CODE_ASP is not null AND HI.IS_ERROR = false "
				+ buildQuery;

	}
	
	private String createGstr1aVerticalTotalQueryString(String buildQuery) {
		return "select SUPPLIER_GSTIN,RETURN_PERIOD,NIL_INTERSTATE_REG," 
				+ "NIL_INTRASTATE_REG,NIL_INTERSTATE_UNREG,"
				+ "NIL_INTRASTATE_UNREG,EXT_INTERSTATE_REG ," 
				+ "EXT_INTRASTATE_REG ,EXT_INTERSTATE_UNREG,"
				+ "EXT_INTRASTATE_UNREG, NON_INTERSTATE_REG  ," 
				+ "NON_INTRASTATE_REG ,NON_INTERSTATE_UNREG ,"
				+ "NON_INTRASTATE_UNREG,ITM_HSNSAC," 
				+ "ITM_DESCRIPTION,ITM_UQC,ITM_QTY," 
			//	+ "ERR.ERROR_CODE_ASP,"
			//	+ "ERR.ERROR_DESCRIPTION_ASP,"
				+ "ERR.INFO_ERROR_CODE_ASP,"
				+ "ERR.INFO_ERROR_DESCRIPTION_ASP " 
				+ "FROM GSTR1A_AS_ENTERED_NILEXTNON HI "
				+ "LEFT OUTER JOIN TF_GSTR1A_ERROR_INFO() ERR "
				+ "ON HI.ID = ERR.COMMON_ID AND  HI.FILE_ID = ERR.FILE_ID " 
				+ "WHERE IS_DELETE = FALSE  AND INFO_ERROR_CODE_ASP is not null AND HI.IS_ERROR = false "
				+ buildQuery;

	}
}
