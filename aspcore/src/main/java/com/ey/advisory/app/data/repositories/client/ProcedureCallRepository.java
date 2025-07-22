package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDate;
import java.util.List;

import com.ey.advisory.common.ProcessingContext;

/**
 * 
 * @author Hemasundar.J
 *
 */
public interface ProcedureCallRepository {

	public void saveGstr1ProcCall(String sgstin, String retPeriod, String section, String groupcode, Long userRequestId,
			Integer derivedRetPeriod, boolean isNilUserInput, boolean isHsnUserInput, ProcessingContext context);

	public void saveAnx1ProcCall(String gstin, String retPeriod, String section, String groupcode,
			Integer derivedRetPeriod);

	public void saveAnx2ProcCall(String gstin, String section, Integer derivedRetPeriod);

	public List<Object[]> fileStatuProcCall(final String dataType, final String fileType,
			final LocalDate recieveFromDate, final LocalDate recieveToDate);

	public void saveRetProcCall(String gstin, String retPeriod, String section, String groupcode,
			Integer derivedRetPeriod);

	public void getAnx1ProcCall(String gstin, String retPeriod, String section);

	public void saveGstr7ProcCall(String sgstin, String retPeriod, String section, String groupcode, Long userRequestId,
			Integer derivedRetPeriod);

	public void saveGstr8ProcCall(String sgstin, String retPeriod, String section, String groupcode, Long userRequestId,
			Integer derivedRetPeriod);

	public void getGstr2AErpProcCall(String gstin, String retPeriod, String section, Long getBatchId);

	public void canGstr7ProcCall(String sgstin, String retPeriod, String section, String groupcode, Long userRequestId,
			Integer derivedRetPeriod);
	
	public void canGstr8ProcCall(String sgstin, String retPeriod, String section, String groupcode, Long userRequestId,
			Integer derivedRetPeriod);


	public String gstr6CalTurnOverGstnProcCall(String endResultGstins, Integer convertTaxPeriodToInt,
			Integer convertTaxPeriodToInt2, Long entityId, String tableType, String taxPeriod,
			String endResultIsdGstins, Long batchId);

	public void gstr6CalTurnOverDigiGstProcCall(String endResultGstins, Integer convertTaxPeriodToInt,
			Integer convertTaxPeriodToInt2, Long entityId, String tableType, String taxPeriod,
			String endResultRegGstins);

	public void gstr6ComputeCreditDistributionProcCall(String endResultGstins, String taxPeriod, String tableType,
			Long entityId,String answer);

	public void gstr6ComputeTurnOverUserInputProcCall(String endResultGstins, Long entityId, String taxPeriod,
			String userInput, String ids, String toPeriod);

	/**
	 * This procedure will insert the data from GET2a Staging tables to GET2A
	 * Original tables.
	 */
	public void getGstr2aProcCall(String gstin, String retPeriod, String section, Long getBatchId, boolean isFromParamGet);
	
	public void getGstr6aProcCall(String gstin, String retPeriod, String section, Long getBatchId, boolean isFromParamGet);
}
