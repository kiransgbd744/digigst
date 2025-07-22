package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.util.List;
import java.util.Set;

import com.ey.advisory.common.ProcessingContext;

/**
 * 
 * @author Hemasundar.J
 *
 */

public interface SaveToGstnQueryBuilder {

	public String buildGstr1CancelledQuery(String gstin, String returnPeriod,
			String supplyType, List<Long> docIds, ProcessingContext context);

	public String buildGstr1B2bB2baQuery(String gstin, String returnPeriod,
			String docType, String tableType, Set<Long> orgDocIds,
			List<Long> docIds, ProcessingContext context);

	public String buildGstr1B2clB2claQuery(String gstin, String returnPeriod,
			String docType, String tableType, Set<Long> orgDocIds,
			List<Long> docIds, ProcessingContext context);

	public String buildGstr1ExpExpaQuery(String gstin, String returnPeriod,
			String docType, String tableType, Set<Long> orgDocIds,
			List<Long> docIds, ProcessingContext context);

	public String buildGstr1CdnQuery(String gstin, String returnPeriod,
			String docType, String tableType, Set<Long> orgDocIds,
			List<Long> docIds, ProcessingContext context);

	public String buildGstr1B2csB2csaQuery(String gstin, String returnPeriod,
			String docType, String tableType, ProcessingContext context);

	public String buildGstr1AtAtaQuery(String gstin, String returnPeriod,
			String docType, String tableType, ProcessingContext context );

	public String buildGstr1TxpTxpaQuery(String gstin, String returnPeriod,
			String docType, String tableType, ProcessingContext context);

	public String buildGstr1NilQuery(String gstin, String returnPeriod,
			String docType, String tableType, ProcessingContext context);

	public String buildGstr1HsnSumQuery(String gstin, String returnPeriod,
			String docType, String tableType, ProcessingContext context);

	public String buildGstr1DocIssuedQuery(String gstin, String returnPeriod,
			String docType, String tableType, ProcessingContext context);

	public String buildGstr1SupEcomQuery(String gstin, String returnPeriod,
			String docType, String tableType, ProcessingContext context);

	public String buildGstr1EcomQuery(String gstin, String returnPeriod,
			String docType, String tableType, Set<Long> orgDocIds,
			List<Long> docIds, ProcessingContext context);

	public String buildGstr1EcomSumQuery(String gstin, String retPeriod,
			String docType, String tableType, ProcessingContext context);

	// --------------------GSTR1 Dependent Retry ----------------------//

	public String buildGstr1DependentRetryB2bB2baQuery(Long batchId,
			Set<String> errorCodes);

	public String buildGstr1DependentRetryB2clB2claQuery(Long batchId,
			Set<String> errorCodes);

	public String buildGstr1DependentRetryExpExpaQuery(Long batchId,
			Set<String> errorCodes);

	public String buildGstr1DependentRetryCdnQuery(Long batchId,
			Set<String> errorCodes);

	// --------------------ANX1 ----------------------//
	public String buildAnx1CancelledQuery(String gstin, String returnPeriod,
			String supplyType);

	public String buildAnx1B2cQuery(String gstin, String retPeriod,
			String docType, String tableType, List<Long> docIds);

	public String buildAnx1B2bQuery(String gstin, String retPeriod,
			String docType, String tableType, List<Long> docIds);

	public String buildAnx1ExpwpAndExpwopQuery(String gstin, String retPeriod,
			String docType, String tableType, List<Long> docIds);

	public String buildAnx1SezwpAndSezwopQuery(String gstin, String retPeriod,
			String docType, String tableType, List<Long> docIds);

	public String buildAnx1DeemedExportsQuery(String gstin, String retPeriod,
			String docType, String tableType, List<Long> docIds);

	public String buildAnx1RevDataQuery(String gstin, String retPeriod,
			String docType, String tableType, List<Long> docIds);

	public String buildAnx1ImpsQuery(String gstin, String retPeriod,
			String docType, String tableType, List<Long> docIds);

	public String buildAnx1EcomQuery(String gstin, String retPeriod,
			String docType, String tableType, List<Long> docIds);

	public String buildAnx1ImpgAndImpgSezQuery(String gstin, String retPeriod,
			String docType, String tableType, List<Long> docIds);

	public String buildAnx1MisQuery(String gstin, String retPeriod,
			String docType, String tableType, List<Long> docIds);

	// ---------------------------- ANX2 ------------------------//
	public String buildAnx2CancelledQuery(String gstin, String returnPeriod,
			String supplyType);

	/*
	 * public String buildAnx2B2bB2baQuery(String gstin, String retPeriod,
	 * String docType, String tableType, List<Long> docIds);
	 * 
	 * public String buildAnx2SezwpSezwpaQuery(String gstin, String retPeriod,
	 * String docType, String tableType, List<Long> docIds);
	 * 
	 * public String buildAnx2SezwopSezwopaQuery(String gstin, String retPeriod,
	 * String docType, String tableType, List<Long> docIds);
	 * 
	 * public String buildAnx2DeemedExportsQuery(String gstin, String retPeriod,
	 * String docType, String tableType, List<Long> docIds);
	 */

	public String buildAnx2SaveQuery(String gstin, String retPeriod,
			String docType, String tableType, List<Long> docIds);

	// ---------------------GSTR6-----------------------------------//

	public String buildGstr6B2bSaveQuery(String gstin, String retPeriod,
			String docType, String returnType, String optionOpted);

	public String buildGstr6IsdSaveQuery(String gstin, String retPeriod,
			String isdDocType);

	public String buildGstr6IsdaSaveQuery(String gstin, String retPeriod,
			String isdDocType);

	public String buildGstr6B2baSaveQuery(String gstin, String retPeriod,
			String docType, String returnType, String optionOpted);

	public String buildGstr6CdnSaveQuery(String gstin, String retPeriod,
			String docType, String returnType, String optionOpted);

	public String buildGstr6CdnaSaveQuery(String gstin, String retPeriod,
			String docType, String returnType, String optionOpted);

	// ---------------------GSTR7-----------------------------------//

	public String buildGstr7TDSQuery(String gstin, String retPeriod,
			String docType);

	public String buildGstr7CanQuery(String gstin, String retPeriod,
			String tableName);

	public String buildGstr7TDSQuery(String gstin, String returnPeriod,
			String docType, Set<Long> orgDocIds, ProcessingContext context);

	public String buildGstr7TDSAQuery(String gstin, String returnPeriod,
			String docType, Set<Long> orgDocIds, ProcessingContext context);

	public String buildGstr7TransCancelledQuery(String gstin, String returnPeriod,
			String supplyType, ProcessingContext context);
	

	// ---------------------GSTR8-----------------------------------//

	public String buildGstr8SummQuery(String gstin, String retPeriod,
			String docType);

	public String buildGstr8CanSummQuery(String gstin, String retPeriod,
			String tableName);

	// --------------------------------GSTR6CAN-----------------------------//

	public String buildGstr6CanQuery(String gstin, String retPeriod,
			String docType, String returnType);

	public String buildGstr6CanIsdQuery(String gstin, String retPeriod,
			String isdDocType);

	public String buildGstr6CanIsdaQuery(String gstin, String retPeriod,
			String isdDocType);

	// ------------------------------ITC04------------------------//

	public String buildItc04M2jwSaveQuery(String gstin, String retPeriod);

	public String buildItc04Table5aSaveQuery(String gstin, String retPeriod);

	public String buildItc04Table5bSaveQuery(String gstin, String retPeriod);

	public String buildItc04Table5cSaveQuery(String gstin, String retPeriod);

	public String buildItc04M2jwCanQuery(String gstin, String retPeriod);

	public String buildItc04Table5aCanQuery(String gstin, String retPeriod);

	public String buildItc04Table5bCanQuery(String gstin, String retPeriod);

	public String buildItc04Table5cCanQuery(String gstin, String retPeriod);

	// -----------------------GSTR2X SAVE---------------------------//

	public String buildGstr2xTdsSaveQuery(String gstin, String retPeriod);

	public String buildGstr2xTdsaSaveQuery(String gstin, String retPeriod);

	public String buildGstr2xTcsSaveQuery(String gstin, String retPeriod);

	public String buildGstr2xTcsaSaveQuery(String gstin, String retPeriod);

}
