package com.ey.advisory.app.data.repositories.client;

import java.util.List;
import java.util.Set;

import org.javatuples.Pair;

import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.core.dto.Gstr1SaveToGstnReqDto;

/**
 * 
 * @author Hemasundar.J
 * 
 *         This interface provides the customization of Spring data repository.
 *         Customization is required in order to avoid the entity mapping
 *         between the classes OutwardTransDocument and DocRateSummary
 *
 */
public interface DocSearchForSaveCustomRepository {

	// ------------------------- Gstr1 -----------------------//
	public List<Object[]> findGstr1CancelledData(String gstin, String retPeriod,
			String docType, List<Long> docIds, ProcessingContext context);

	public List<Object[]> findGstr1InvoiceLevelData(String gstin,
			String retPeriod, String docType, Set<Long> orgDocIds,
			List<Long> docIds,ProcessingContext context);

	public List<Object[]> findGstr1SummaryLevelData(String gstin,
			String retPeriod, String docType, ProcessingContext context);

	public List<Pair<String, String>> getGstr1SgstinRetPerds(
			Gstr1SaveToGstnReqDto dto);

	public List<Object[]> findGstr1DependentRetryInvoiceLevelData(
			String docType, Long batchId, Set<String> errorCodes);

	// ------------------------- Anx1 -----------------------//

	public List<Object[]> findAnx1InvLevelData(String gstin, String retPeriod,
			String docType, List<Long> docIds);

	public List<Object[]> findAnx1SumLevelData(String gstin, String retPeriod,
			String docType);

	public List<Object[]> findAnx1CancelledData(String gstin, String retPeriod,
			String docType);

	// ------------------------- Anx2 -----------------------//

	public List<Object[]> findAnx2InvLevelData(String gstin, String retPeriod,
			String docType, List<Long> docIds);

	public List<Object[]> findAnx2CancelledData(String gstin, String retPeriod,
			String docType);

	// ------------------------- Gstr3B -----------------------//
	public List<Object[]> findGstr3BInvoiceLevelData(String gstin,
			String retPeriod);

	public List<Object[]> findGstr3BInvoiceGstnLevelData(String gstin,
			String retPeriod);

	// ------------------------- Ret -----------------------//

	/*
	 * public List<Object[]> findRetInvLevelData(String gstin, String retPeriod,
	 * String docType, List<Long> docIds);
	 */

	public List<Object[]> findRetSumLevelData(String gstin, String retPeriod,
			String docType);

	public List<Object[]> findRetCancelledData(String gstin, String retPeriod,
			String docType);

	// ----------------------GSTR6-----------------------------------//
	public List<Object[]> findGstr6InvLevelData(String gstin, String retPeriod,
			String docType, String isdDocType);

	public List<Object[]> findGstr6CanInvLevelData(String gstin,
			String retPeriod, String docType, String isdDocType);

	// ----------------------GSTR7-----------------------------------//
	public List<Object[]> findGstr7InvLevelData(String gstin, String retPeriod,
			String docType);

	public List<Object[]> findGstr7CancelledData(String gstin, String retPeriod,
			String docType);
	
	public List<Object[]> findGstr7TransCancelledData(String gstin,
			String retPeriod, String supplyType, ProcessingContext context);

	public List<Object[]> findGstr7TransInvLevelData(String gstin,
			String retPeriod, String docType, Set<Long> orgDocIds,
			 ProcessingContext context);

	// ----------------------GSTR8-----------------------------------//
	public List<Object[]> findGstr8SummaryData(String gstin, String retPeriod,
			String section);

	public List<Object[]> findGstr8CanSummaryData(String gstin, String retPeriod,
			String section);

	// -------------------------------ITC04-------------------------------//
	public List<Object[]> findItc04InvLevelData(String gstin, String retPeriod,
			String docType);

	public List<Object[]> findItc04CanInvLevelData(String gstin,
			String retPeriod, String docType);

	// -------------------------------GSTR2X----------------------------//
	public List<Object[]> findGstr2XInvLevelData(String gstin, String retPeriod,
			String docType);

	// ----------------------------GSTR7ENTITYSAVE-------------------------//
	public List<Pair<String, String>> getGstr7SgstinRetPerds(
			Gstr1SaveToGstnReqDto dto);

	// ----------------------------ITC04ENTITYSAVE-------------------------//
	public List<Pair<String, String>> getItc04SgstinRetPerds(
			Gstr1SaveToGstnReqDto dto);
	
	//---------------------------GSTR1A-----------------------------------//
		public List<Pair<String, String>> getGstr1ASgstinRetPerds(
				Gstr1SaveToGstnReqDto dto);
}
