/**
 * 
 */
package com.ey.advisory.app.anx.reconresult;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Nikhil.Duseja
 *
 */
@Slf4j
@Service("ReconResultDocSummaryServiceImpl")
public class ReconResultDocSummaryServiceImpl
		implements ReconResultDocSummaryService {

	@Autowired
	@Qualifier("ReconResultDocSummaryDaoImpl")
	ReconResultDocSummaryDao resultDocSummary;
	
	@Override
	public List<ReconResultDocSummaryRespDto> getReconResultDocSummDetails(
			ReconResultDocSummaryReqDto reqDto) {
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Calling  ReconResultDocSummaryDao Layer "
					+ "from ReconResultDocSummaryServiceImpl "
					+ " Where Request is %s", reqDto);
			LOGGER.debug(msg);
		}
		
		if (CollectionUtils.isEmpty(reqDto.getGstin()))
			throw new AppException("User Does not have any gstin");
		
		String validQuery = queryCondition(reqDto);

		return resultDocSummary
				.findReconResultDocSummaryDetails(reqDto,
						validQuery);
	}
	
	
	public static String queryCondition(ReconResultDocSummaryReqDto req) {

		if (LOGGER.isDebugEnabled()) {
			String msg = " Begin ReconResultDocSummary"
					+ ".queryCondition ";
			LOGGER.debug(msg);
		}

		StringBuilder condition1 = new StringBuilder();
		

		if (!CollectionUtils.isEmpty(req.getGstin())) {
			condition1.append(" (LT.A2_RECIPIENT_GSTIN IN(:gstin)"
					+ " OR LT.PR_RECIPIENT_GSTIN IN(:gstin)) ");
		}
		
		condition1.append(" AND (IS_INFORMATION_REPORT = 0 "
				+ " OR IS_INFORMATION_REPORT IS NULL) ");
		
		
		if (!CollectionUtils.isEmpty(req.getReportType())) {
			condition1.append(" AND LT.CURRENT_REPORT IN(:reportType) ");
		}
		
		if(!CollectionUtils.isEmpty(req.getTableType())) {
			condition1.append(" AND A2_TABLE_TYPE IN(:tableType) ");
		}
		
		if(!CollectionUtils.isEmpty(req.getDocType())) {
			condition1.append("  AND LT.lk_document_type IN(:docType)");
		}
		
		condition1.append(" AND (LT.TAX_PERIOD =:derivedRetPer) ");
		return condition1.toString();
	}	
}

