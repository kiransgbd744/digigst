/**
 * 
 */
package com.ey.advisory.app.data.services.savetogstn.gstr8;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.gstr8.GetGstr8SummaryEntity;
import com.ey.advisory.app.data.repositories.client.gstr8.Gstr8GetSummaryRepository;
import com.ey.advisory.app.docs.dto.anx2.Anx2GetInvoicesReqDto;
import com.ey.advisory.app.docs.dto.gstr8.Gstr8GetSectionSummaryDto;
import com.ey.advisory.app.docs.dto.gstr8.Gstr8GetSummaryDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Component("Gstr8SummaryDataParserImpl")
@Slf4j
public class Gstr8SummaryDataParserImpl {

	@Autowired
	@Qualifier("Gstr8GetSummaryRepository")
	private Gstr8GetSummaryRepository gstr8GetSummaryRepo;

	public void parsegstr8SummaryData(Anx2GetInvoicesReqDto dto, String apiResp,
			Long batchId) {
		String userName = (SecurityContext.getUser() != null
				&& SecurityContext.getUser().getUserPrincipalName() != null)
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM";

		try {
			Gson gson = GsonUtil.newSAPGsonInstance();
			Gstr8GetSummaryDto jsonSummaryData = gson.fromJson(apiResp,
					Gstr8GetSummaryDto.class);
			String gstin = jsonSummaryData.getGstin();
			String returnPeriod = jsonSummaryData.getFp();
			int derivedRetPeriod = GenUtil.convertTaxPeriodToInt(returnPeriod);
			Gstr8GetSectionSummaryDto tcsDtails = jsonSummaryData.getTcs();
			Gstr8GetSectionSummaryDto tcsaDtails = jsonSummaryData.getTcsa();
			Gstr8GetSectionSummaryDto urdDetails = jsonSummaryData.getUrd();
			Gstr8GetSectionSummaryDto urdaDtails = jsonSummaryData.getUrda();

			gstr8GetSummaryRepo.softlyDeleteActiveRecords(gstin, returnPeriod);

			if (tcsDtails != null) {
				GetGstr8SummaryEntity tcs = new GetGstr8SummaryEntity();
				tcs.setGstIn(gstin);
				tcs.setBatchId(batchId.toString());
				tcs.setTaxperiod(returnPeriod);
				tcs.setDeriviedReturnPeriod(derivedRetPeriod);
				tcs.setCreatedBy(userName);
				tcs.setCreatedOn(LocalDateTime.now());
				tcs.setModifiedBy(userName);
				tcs.setModifiedOn(LocalDateTime.now());
				tcs.setSection(GSTConstants.TCS);
				tcs.setTotAmtDeducted(tcsDtails.getTtlAmtDed());
				tcs.setTotalRecord(tcsDtails.getTtlRec());
				tcs.setTtcgst(tcsDtails.getTtlCamt());
				tcs.setTtigst(tcsDtails.getTtlIamt());
				tcs.setTtsgst(tcsDtails.getTtlSamt());
				tcs.setGrossSupMade(tcsDtails.getGSuppMade());
				tcs.setGrossSupRet(tcsDtails.getGSuppRtn());
				tcs.setChkSum(tcsaDtails.getChksum());
				gstr8GetSummaryRepo.save(tcs);
			}
			if (tcsaDtails != null) {
				GetGstr8SummaryEntity tcsAmd = new GetGstr8SummaryEntity();
				tcsAmd.setGstIn(gstin);
				tcsAmd.setBatchId(batchId.toString());
				tcsAmd.setTaxperiod(returnPeriod);
				tcsAmd.setDeriviedReturnPeriod(derivedRetPeriod);
				tcsAmd.setCreatedBy(userName);
				tcsAmd.setCreatedOn(LocalDateTime.now());
				tcsAmd.setModifiedBy(userName);
				tcsAmd.setModifiedOn(LocalDateTime.now());
				tcsAmd.setSection(GSTConstants.TCSA);
				tcsAmd.setTotAmtDeducted(tcsaDtails.getTtlAmtDed());
				tcsAmd.setTotalRecord(tcsaDtails.getTtlRec());
				tcsAmd.setTtcgst(tcsaDtails.getTtlCamt());
				tcsAmd.setTtigst(tcsaDtails.getTtlIamt());
				tcsAmd.setTtsgst(tcsaDtails.getTtlSamt());
				tcsAmd.setGrossSupMade(tcsaDtails.getGSuppMade());
				tcsAmd.setGrossSupRet(tcsaDtails.getGSuppRtn());
				tcsAmd.setChkSum(tcsaDtails.getChksum());
				gstr8GetSummaryRepo.save(tcsAmd);
			}

			if (urdDetails != null) {
				GetGstr8SummaryEntity urd = new GetGstr8SummaryEntity();
				urd.setGstIn(gstin);
				urd.setBatchId(batchId.toString());
				urd.setTaxperiod(returnPeriod);
				urd.setDeriviedReturnPeriod(derivedRetPeriod);
				urd.setCreatedBy(userName);
				urd.setCreatedOn(LocalDateTime.now());
				urd.setModifiedBy(userName);
				urd.setModifiedOn(LocalDateTime.now());
				urd.setSection(GSTConstants.URD);
				urd.setTotAmtDeducted(urdDetails.getTtlAmtDed());
				urd.setTotalRecord(urdDetails.getTtlRec());
				urd.setGrossSupMade(urdDetails.getGSuppMade());
				urd.setGrossSupRet(urdDetails.getGSuppRtn());
				urd.setChkSum(urdDetails.getChksum());
				gstr8GetSummaryRepo.save(urd);
			}

			if (urdaDtails != null) {
				GetGstr8SummaryEntity urdAmd = new GetGstr8SummaryEntity();
				urdAmd.setGstIn(gstin);
				urdAmd.setBatchId(batchId.toString());
				urdAmd.setTaxperiod(returnPeriod);
				urdAmd.setDeriviedReturnPeriod(derivedRetPeriod);
				urdAmd.setCreatedBy(userName);
				urdAmd.setCreatedOn(LocalDateTime.now());
				urdAmd.setModifiedBy(userName);
				urdAmd.setModifiedOn(LocalDateTime.now());
				urdAmd.setSection(GSTConstants.URDA);
				urdAmd.setTotAmtDeducted(urdaDtails.getTtlAmtDed());
				urdAmd.setTotalRecord(urdaDtails.getTtlRec());
				urdAmd.setTtcgst(urdaDtails.getTtlCamt());
				urdAmd.setTtigst(urdaDtails.getTtlIamt());
				urdAmd.setTtsgst(urdaDtails.getTtlSamt());
				urdAmd.setGrossSupMade(urdaDtails.getGSuppMade());
				urdAmd.setGrossSupRet(urdaDtails.getGSuppRtn());
				urdAmd.setChkSum(urdaDtails.getChksum());
				gstr8GetSummaryRepo.save(urdAmd);
			}

		} catch (Exception e) {
			String msg = "failed to parse gstr8 summary response";
			LOGGER.error(msg, e);
			throw new AppException(msg);
		}

	}

}