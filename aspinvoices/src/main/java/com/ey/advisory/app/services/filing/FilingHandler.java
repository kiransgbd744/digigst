package com.ey.advisory.app.services.filing;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.DocIssueDto;
import com.ey.advisory.app.data.daos.client.NilFillingDto;
import com.ey.advisory.app.docs.dto.B2BFilingDto;
import com.ey.advisory.app.docs.dto.B2CLFilingDto;
import com.ey.advisory.app.docs.dto.B2csFillingDto;
import com.ey.advisory.app.docs.dto.FilingReqDto;
import com.ey.advisory.app.docs.dto.Gstr1FilingDto;
import com.ey.advisory.app.docs.dto.HsnFillingDto;
import com.ey.advisory.common.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * 
 * @author Siva.Nandam
 *
 */
@Component("FilingHandler")
public class FilingHandler {

	@Autowired
	@Qualifier("NilFillingService")
	private FilingService nilFillingService;
	@Autowired
	@Qualifier("DocIssueService")
	private FilingService docIssueService;
	@Autowired
	@Qualifier("ExpaFillingService")
	private FilingService expaFillingService;
	@Autowired
	@Qualifier("ExpFillingService")
	private FilingService expFillingService;
	@Autowired
	@Qualifier("AtFillingService")
	private FilingService atFillingService;
	@Autowired
	@Qualifier("AtafillingService")
	private FilingService atafillingService;
	@Autowired
	@Qualifier("HsnFillingService")
	private FilingService hsnFillingService;
	@Autowired
	@Qualifier("TxpdFillingService")
	private FilingService txpdFillingService;
	@Autowired
	@Qualifier("TxpdaFillingService")
	private FilingService txpdaFillingService;
	@Autowired
	@Qualifier("B2csFillingService")
	private FilingService b2csFillingService;
	@Autowired
	@Qualifier("B2csaFillingService")
	private FilingService b2csaFillingService;
	@Autowired
	@Qualifier("B2baFilingService")
	private FilingService b2baFilingService;
	@Autowired
	@Qualifier("B2bFilingService")
	private FilingService b2bService;
	@Autowired
	@Qualifier("CDNRFilingService")
	private FilingService cdnrService;
	@Autowired
	@Qualifier("CDNRAFilingService")
	private FilingService CdnraFilingService;
	@Autowired
	@Qualifier("B2clFilingService")
	private FilingService b2clService;
	@Autowired
	@Qualifier("B2claFilingService")
	private FilingService b2claService;
	@Autowired
	@Qualifier("CDNURFilingService")
	private FilingService CdnurFilingService;
	@Autowired
	@Qualifier("CDNURAFilingService")
	private FilingService CdnuraFilingService;

	@SuppressWarnings("unchecked")
	public JsonElement handleFiling(FilingReqDto dto) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		B2BFilingDto b2b = (B2BFilingDto) b2bService
				.getFilingData(dto.getSgstins(), dto.getReturnPeriods());
		B2BFilingDto b2ba = (B2BFilingDto) b2baFilingService
				.getFilingData(dto.getSgstins(), dto.getReturnPeriods());
		B2BFilingDto cdnr = (B2BFilingDto) cdnrService
				.getFilingData(dto.getSgstins(), dto.getReturnPeriods());
		B2BFilingDto cdnra = (B2BFilingDto) CdnraFilingService
				.getFilingData(dto.getSgstins(), dto.getReturnPeriods());
		B2CLFilingDto b2cl = (B2CLFilingDto) b2clService
				.getFilingData(dto.getSgstins(), dto.getReturnPeriods());
		B2CLFilingDto b2cla = (B2CLFilingDto) b2claService
				.getFilingData(dto.getSgstins(), dto.getReturnPeriods());
		B2CLFilingDto cdnur = (B2CLFilingDto) CdnurFilingService
				.getFilingData(dto.getSgstins(), dto.getReturnPeriods());
		B2CLFilingDto cdnura = (B2CLFilingDto) CdnuraFilingService
				.getFilingData(dto.getSgstins(), dto.getReturnPeriods());
		HsnFillingDto hsn = (HsnFillingDto) hsnFillingService
				.getFilingData(dto.getSgstins(), dto.getReturnPeriods());
		B2csFillingDto b2cs = (B2csFillingDto) b2csFillingService
				.getFilingData(dto.getSgstins(), dto.getReturnPeriods());
		B2csFillingDto b2csa = (B2csFillingDto) b2csaFillingService
				.getFilingData(dto.getSgstins(), dto.getReturnPeriods());
		HsnFillingDto at = (HsnFillingDto) atFillingService
				.getFilingData(dto.getSgstins(), dto.getReturnPeriods());
		HsnFillingDto txpd = (HsnFillingDto) txpdFillingService
				.getFilingData(dto.getSgstins(), dto.getReturnPeriods());
		HsnFillingDto txpda = (HsnFillingDto) txpdaFillingService
				.getFilingData(dto.getSgstins(), dto.getReturnPeriods());
		ExpFillingDto exp = (ExpFillingDto) expFillingService
				.getFilingData(dto.getSgstins(), dto.getReturnPeriods());

		ExpFillingDto expa = (ExpFillingDto) expaFillingService
				.getFilingData(dto.getSgstins(), dto.getReturnPeriods());
		DocIssueDto invoice = (DocIssueDto) docIssueService
				.getFilingData(dto.getSgstins(), dto.getReturnPeriods());
		NilFillingDto nil = (NilFillingDto) nilFillingService
				.getFilingData(dto.getSgstins(), dto.getReturnPeriods());
		HsnFillingDto ata = (HsnFillingDto) atafillingService
				.getFilingData(dto.getSgstins(), dto.getReturnPeriods());
		List<Object> sectionFileList = new ArrayList<>();
		Gstr1FilingDto filingList = new Gstr1FilingDto();

		filingList.setGstin(dto.getSgstins());
		filingList.setRet_period(dto.getReturnPeriods());
		filingList.setSumm_typ("S");
		sectionFileList.add(b2b);
		sectionFileList.add(b2ba);
		sectionFileList.add(cdnr);
		sectionFileList.add(cdnra);
		sectionFileList.add(b2cl);
		sectionFileList.add(b2cla);
		sectionFileList.add(cdnur);
		sectionFileList.add(cdnura);
		sectionFileList.add(hsn);
		sectionFileList.add(at);
		sectionFileList.add(ata);
		sectionFileList.add(txpda);
		sectionFileList.add(txpd);
		sectionFileList.add(b2cs);
		sectionFileList.add(b2csa);
		sectionFileList.add(exp);
		sectionFileList.add(expa);
		sectionFileList.add(invoice);
		sectionFileList.add(nil);

		filingList.setSec_sum(sectionFileList);

		JsonElement respBody = gson.toJsonTree(filingList);

		return respBody;

	}

}
