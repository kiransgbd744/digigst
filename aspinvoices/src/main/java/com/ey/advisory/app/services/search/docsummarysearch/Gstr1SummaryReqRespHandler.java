package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1SummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryReqDto;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.search.SearchResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

@Service("Gstr1ReqRespHandler")
public class Gstr1SummaryReqRespHandler {
	
	@Autowired
	@Qualifier("docSummarySearchService")
	private DocSummarySearchService service;
	
	/*@Autowired
	@Qualifier("GetB2BGstnService")
	GetB2BGstnService b2bGstnService;*/
	
	@Autowired
	@Qualifier("Gstr1B2BRespHandler")
	private Gstr1B2BRespHandler gstr1B2BRespHandler;
	
	@Autowired
	@Qualifier("Gstr1B2CLRespHandler")
	private Gstr1B2CLRespHandler gstr1B2CLRespHandler;
	
	@Autowired
	@Qualifier("Gstr1B2CSRespHandler")
	private Gstr1B2CSRespHandler gstr1B2CSRespHandler;
	
	@Autowired
	@Qualifier("GstrCDNRRespHandler")
	private GstrCDNRRespHandler gstrCDNRRespHandler;
	
	@Autowired
	@Qualifier("GstrB2BARespHandler")
	private GstrB2BARespHandler gstrB2BARespHandler;
	
	@Autowired
	@Qualifier("GstrCDNRARespHandler")
	private GstrCDNRARespHandler gstrCDNRARespHandler;
	
	@Autowired
	@Qualifier("GstrCDNURRespHandler")
	private GstrCDNURRespHandler gstrCDNURRespHandler;
	
	@Autowired
	@Qualifier("GstrHSNRespHandler")
	private GstrHSNRespHandler gstrHSNRespHandler;
	
	@Autowired
	@Qualifier("GstrB2CLARespHandler")
	private GstrB2CLARespHandler gstrB2CLARespHandler;
	
	@Autowired
	@Qualifier("GstrEXPRespHandler")
	private GstrEXPRespHandler gstrEXPRespHandler;
	
	@Autowired
	@Qualifier("GstrEXPAMDRespHandler")
	private GstrEXPAMDRespHandler gstrEXPAMDRespHandler;
	
	@Autowired
	@Qualifier("GstrCDNURARespHandler")
	private GstrCDNURARespHandler gstrCDNURARespHandler;

	@Autowired
	@Qualifier("GstrB2CSARespHandler")
	private GstrB2CSARespHandler gstrB2CSARespHandler;

	@Autowired
	@Qualifier("GstrB2CLARespHandler")
	private GstrB2CLARespHandler gstrB2claRespHandler;

	@Autowired
	@Qualifier("GstrATRespHandler")
	private GstrATRespHandler gstrATRespHandler;

	@Autowired
	@Qualifier("GstrATARespHandler")
	private GstrATARespHandler gstrATARespHandler;

	@Autowired
	@Qualifier("GstrTXPDRespHandler")
	private GstrTXPDRespHandler gstrTXPDRespHandler;

	@Autowired
	@Qualifier("GstrTXPDARespHandler")
	private GstrTXPDARespHandler gstrTXPDARespHandler;

	@Autowired
	@Qualifier("GstrDOCRespHandler")
	private GstrDOCRespHandler gstrDOCRespHandler;
	
	@Autowired
	@Qualifier("GstrNilRespHandler")
	private GstrNilRespHandler gstrNilRespHandler;
	
	@Autowired
	@Qualifier("Gstr1SEZWRespHandler")
	private Gstr1SEZWRespHandler gstrSezwRespHandler;
	
	@Autowired
	@Qualifier("Gstr1SEZWORespHandler")
	private Gstr1SEZWORespHandler gstrSezwoRespHandler;
	
	@SuppressWarnings("unchecked")
	public JsonElement handleGstr1ReqAndResp(
			Gstr1SummaryReqDto gstr1SummaryRequest) {
		SearchResult<Gstr1SummaryDto> summaryResult = service
				.<Gstr1SummaryDto>find(gstr1SummaryRequest, null,
						Gstr1SummaryDto.class);
		
		List<? extends Gstr1SummaryDto> list = summaryResult.getResult();

		JsonElement b2bRespBody = gstr1B2BRespHandler.handleB2BResp(list,
				gstr1SummaryRequest);
	//	JsonElement b2bRespBody = gstr1B2BRespHandler.handleB2BResp(list);
				
		JsonElement b2clRespBody = gstr1B2CLRespHandler.handleB2CLResp(list,
				gstr1SummaryRequest);	
		JsonElement b2claRespBody = gstrB2claRespHandler.handleB2CLAResp(list,
				gstr1SummaryRequest);
				
		JsonElement b2csRespBody = gstr1B2CSRespHandler.handleB2CSResp(list,
				gstr1SummaryRequest);
				
		JsonElement b2csaRespBody = gstrB2CSARespHandler.handleB2CSAResp(list,
				gstr1SummaryRequest);
		JsonElement b2baRespBody = gstrB2BARespHandler.handleB2BAResp(list,
				gstr1SummaryRequest);
				
		JsonElement cdnrRespBody = gstrCDNRRespHandler.handleCDNRResp(list,
				gstr1SummaryRequest);
		JsonElement cdnraRespBody = gstrCDNRARespHandler.handleCDNRAResp(list,
				gstr1SummaryRequest);
				
		JsonElement cdnuraRespBody = gstrCDNURARespHandler
				.handleCDNURAResp(list, gstr1SummaryRequest);
		JsonElement hsnRespBody = gstrHSNRespHandler.handleHSNResp(list,
				gstr1SummaryRequest);
		JsonElement expRespBody = gstrEXPRespHandler.handleEXPResp(list,
				gstr1SummaryRequest);
		JsonElement expAmdRespBody = gstrEXPAMDRespHandler.handleEXPAResp(list,
				gstr1SummaryRequest);
				
		JsonElement cdnurRespBody = gstrCDNURRespHandler.handleCDNURResp(list,
				gstr1SummaryRequest);
		JsonElement docRespBody = gstrDOCRespHandler.handleDocResp(list,
				gstr1SummaryRequest);
		JsonElement nilRespBody = gstrNilRespHandler.handleNilResp(list,
				gstr1SummaryRequest);
		JsonElement sezwRespBody = gstrSezwRespHandler.handleSezwResp(list);
		JsonElement sezwoRespBody = gstrSezwoRespHandler.handleSezwoResp(list);
		/*
		 *  for below Sections Views are not available 
		 */
		JsonElement atRespBody = gstrATRespHandler.handleATResp(list,
				gstr1SummaryRequest);
		JsonElement ataRespBody = gstrATARespHandler.handleATAResp(list,
				gstr1SummaryRequest);
		JsonElement txpdRespBody = gstrTXPDRespHandler.handleTXPDResp(list,
				gstr1SummaryRequest);
		JsonElement txpdaRespBody = gstrTXPDARespHandler.handleTXPDAResp(list,
				gstr1SummaryRequest);
		
		Map<String,JsonElement> combinedMap = new HashMap<>();
		
		combinedMap.put("b2b", b2bRespBody);
		combinedMap.put("b2cl", b2clRespBody);
		combinedMap.put("b2ba", b2baRespBody);
		combinedMap.put("b2cs", b2csRespBody);
		combinedMap.put("cdnr", cdnrRespBody);
		combinedMap.put("cdnra", cdnraRespBody);
		combinedMap.put("hsn", hsnRespBody);
		combinedMap.put("exp", expRespBody);
		combinedMap.put("b2csa", b2csaRespBody);
		combinedMap.put("expa", expAmdRespBody);
		combinedMap.put("cdnura", cdnuraRespBody);
		combinedMap.put("b2cla", b2claRespBody);
		combinedMap.put("at", atRespBody);
		combinedMap.put("ata", ataRespBody);
		combinedMap.put("txpd", txpdRespBody);
		combinedMap.put("txpda", txpdaRespBody);
		
		combinedMap.put("cdnur", cdnurRespBody);
		combinedMap.put("docIssued", docRespBody);
		combinedMap.put("nil", nilRespBody);
		combinedMap.put("sezwp", sezwRespBody);
		combinedMap.put("sezwop", sezwoRespBody);
		
		
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonElement summaryRespbody = gson.toJsonTree(combinedMap);
		
		
		return summaryRespbody;
}

}
