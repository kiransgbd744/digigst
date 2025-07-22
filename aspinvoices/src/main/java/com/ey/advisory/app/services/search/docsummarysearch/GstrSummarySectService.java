package com.ey.advisory.app.services.search.docsummarysearch;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.Gstr1SummaryDocIssuedEntity;
import com.ey.advisory.app.data.entities.client.Gstr1SummaryNilEntity;
import com.ey.advisory.app.data.entities.client.Gstr1SummaryRateEntity;
import com.ey.advisory.app.docs.dto.Gstr1BasicSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1BasicSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1DocIssuedBasicSummary;
import com.ey.advisory.app.docs.dto.Gstr1DocIssuedSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1NilRatedSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1NillratedBasicSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryReqDto;
import com.ey.advisory.app.services.jobs.gstr1.Gstr1SummaryDataAtGstn;
import com.ey.advisory.app.services.jobs.gstr1.Gstr1SummaryDataParser;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service("GstrSummarySectService")
public class GstrSummarySectService implements SearchService {

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository gstinInfoRepository;
	
	@Autowired
	@Qualifier("GstnDateCalculation")
	GstnDateCalculation gstnDateCalculation;
	
	@Autowired
	@Qualifier("gstr1SummaryDataAtGstnImpl")
	Gstr1SummaryDataAtGstn gstr1SummaryDataAtGstn;
	
	@Autowired
	@Qualifier("gstr1SummaryDataParserImpl")
	Gstr1SummaryDataParser gstr1SummaryDataParser;
	
	
	public String gstnApi(Gstr1GetInvoicesReqDto dto, String groupCode){
		String apiResp = null ;
		try {
		APIResponse respp = gstr1SummaryDataAtGstn.findSummaryDataAtGstn(dto,
				groupCode);
		apiResp = respp != null ? respp.getResponse() : null;
		} catch (java.lang.UnsupportedOperationException JsonObject) {
			apiResp = null ;
			return apiResp;
		} catch (Exception JsonObject) {
			apiResp = null ;
			return apiResp;
		}
		return apiResp;
	}

	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		// TODO Auto-generated method stub
		String groupCode = TenantContext.getTenantId();
		
		Gstr1SummaryReqDto request = (Gstr1SummaryReqDto) criteria;
		List<String> sgstins = request.getSgstins();
		String fromTaxPeriod = request.getFromTaxPeriod();
		String toTaxPeriod = request.getToTaxPeriod();
		
		if (null != request.getEntityId()
				&& !request.getEntityId().isEmpty()) {
			//List<String> sgstins = request.getSgstins();
			if (sgstins == null || sgstins.size() == 0) {
				sgstins = gstinInfoRepository
						.findByEntityId(request.getEntityId());
				request.setSgstins(sgstins);
			}
		}
		if(null ==request.getEntityId() && request.getEntityId().isEmpty()){
			if (sgstins == null || sgstins.size() == 0) {
			List<String> gstins=gstinInfoRepository.findByGroupCode(groupCode);
			request.setSgstins(gstins);
			}
		}

		String taxPeriodFrom = "01" + fromTaxPeriod;
		String taxPeriodTo = "01" + toTaxPeriod;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");

		LocalDate startDate = LocalDate.parse(taxPeriodFrom, formatter);
		LocalDate endDate = LocalDate.parse(taxPeriodTo, formatter);
		

		List<String> ret_Period = gstnDateCalculation
				.findingReturnPeriods(startDate, endDate);
	/*	List<String> ret_Period = getDatesBetweenUsingJava8(startDate, endDate);*/

		List<Gstr1GetInvoicesReqDto> listReqDto = new ArrayList<>();
		Gstr1GetInvoicesReqDto reqDto = null;
		
		if (sgstins != null && !sgstins.isEmpty()) {
			for (String gstn : sgstins) {
				for (String returnPeriod : ret_Period) {
					reqDto = new Gstr1GetInvoicesReqDto();
					
					reqDto.setGstin(gstn);
					reqDto.setReturnPeriod(returnPeriod);
					listReqDto.add(reqDto);
				}
			}

		} else {
			
			// error			
		}

	/*	BigDecimal igst = new BigDecimal(0.0);
		BigDecimal sgst = new BigDecimal(0.0);
		BigDecimal cgst = new BigDecimal(0.0);
		BigDecimal cess = new BigDecimal(0.0);
		BigDecimal intValue = new BigDecimal(0.0);
		BigDecimal taxableValue = new BigDecimal(0.0);
		BigDecimal taxPayble = new BigDecimal(0.0);
		int records = 0;
*/
		Gstr1SummaryDto b2bdto1 =new Gstr1SummaryDto();
		
		Gstr1BasicSummaryDto b2bdto2=new Gstr1BasicSummaryDto();
		Gstr1BasicSummaryDto b2csdto2=new Gstr1BasicSummaryDto();
		Gstr1BasicSummaryDto b2csadto2=new Gstr1BasicSummaryDto();
		Gstr1BasicSummaryDto b2badto2=new Gstr1BasicSummaryDto();
		Gstr1BasicSummaryDto b2cldto2=new Gstr1BasicSummaryDto();
		Gstr1BasicSummaryDto b2cladto2=new Gstr1BasicSummaryDto();
		Gstr1BasicSummaryDto cdnrdto2=new Gstr1BasicSummaryDto();
		Gstr1BasicSummaryDto cdnradto2=new Gstr1BasicSummaryDto();
		Gstr1BasicSummaryDto cdnurdto2=new Gstr1BasicSummaryDto();
		Gstr1BasicSummaryDto cdnuradto2=new Gstr1BasicSummaryDto();
		Gstr1BasicSummaryDto hsndto2=new Gstr1BasicSummaryDto();
		Gstr1BasicSummaryDto expdto2=new Gstr1BasicSummaryDto();
		Gstr1BasicSummaryDto expadto2=new Gstr1BasicSummaryDto();
		Gstr1BasicSummaryDto atdto2=new Gstr1BasicSummaryDto();
		Gstr1BasicSummaryDto atadto2=new Gstr1BasicSummaryDto();
		Gstr1BasicSummaryDto txpddto2=new Gstr1BasicSummaryDto();
		Gstr1BasicSummaryDto txpdadto2=new Gstr1BasicSummaryDto();
		Gstr1DocIssuedBasicSummary docdto2 = 
				new Gstr1DocIssuedBasicSummary();
		Gstr1NillratedBasicSummaryDto nildto2 = 
				new Gstr1NillratedBasicSummaryDto();
	//	Gstr1BasicSummaryDto nildto2=new Gstr1BasicSummaryDto();
		
		List<Gstr1BasicSummarySectionDto> listb2bdto3 = 
				new ArrayList<Gstr1BasicSummarySectionDto>();
		List<Gstr1BasicSummarySectionDto> listb2badto3 = 
				new ArrayList<Gstr1BasicSummarySectionDto>();
		List<Gstr1BasicSummarySectionDto> listb2csdto3 = 
				new ArrayList<Gstr1BasicSummarySectionDto>();
		List<Gstr1BasicSummarySectionDto> listb2csadto3 = 
				new ArrayList<Gstr1BasicSummarySectionDto>();
		List<Gstr1BasicSummarySectionDto> listb2cldto3 = 
				new ArrayList<Gstr1BasicSummarySectionDto>();
		List<Gstr1BasicSummarySectionDto> listb2cladto3 = 
				new ArrayList<Gstr1BasicSummarySectionDto>();
		List<Gstr1BasicSummarySectionDto> listcdnrdto3 = 
				new ArrayList<Gstr1BasicSummarySectionDto>();
		List<Gstr1BasicSummarySectionDto> listcdnradto3 = 
				new ArrayList<Gstr1BasicSummarySectionDto>();
		List<Gstr1BasicSummarySectionDto> listcdnurdto3 = 
				new ArrayList<Gstr1BasicSummarySectionDto>();
		List<Gstr1BasicSummarySectionDto> listcdnuradto3 = 
				new ArrayList<Gstr1BasicSummarySectionDto>();
		List<Gstr1BasicSummarySectionDto> listhsndto3 = 
				new ArrayList<Gstr1BasicSummarySectionDto>();
		List<Gstr1BasicSummarySectionDto> listexpdto3 = 
				new ArrayList<Gstr1BasicSummarySectionDto>();
		List<Gstr1BasicSummarySectionDto> listexpadto3 = 
				new ArrayList<Gstr1BasicSummarySectionDto>();
		List<Gstr1BasicSummarySectionDto> listatdto3 = 
				new ArrayList<Gstr1BasicSummarySectionDto>();
		List<Gstr1BasicSummarySectionDto> listatadto3 = 
				new ArrayList<Gstr1BasicSummarySectionDto>();
		List<Gstr1BasicSummarySectionDto> listtxpddto3 = 
				new ArrayList<Gstr1BasicSummarySectionDto>();
		List<Gstr1BasicSummarySectionDto> listtxpdadto3 = 
				new ArrayList<Gstr1BasicSummarySectionDto>();
		List<Gstr1DocIssuedSummarySectionDto> listdocadto3 =
				new ArrayList<Gstr1DocIssuedSummarySectionDto>();
		List<Gstr1NilRatedSummarySectionDto> listnildto3 = 
				new ArrayList<Gstr1NilRatedSummarySectionDto>();
	//	List<Gstr1BasicSummarySectionDto> listnildto3 = 
	//	new ArrayList<Gstr1BasicSummarySectionDto>();
		
		
//		List<GetGstr1SummaryEntity> response = new ArrayList<GetGstr1SummaryEntity>();
		
		Gstr1BasicSummarySectionDto gstr1SummaryHSN = hello();
		Gstr1BasicSummarySectionDto gstr1SummaryB2B = hello();
		Gstr1BasicSummarySectionDto gstr1SummaryB2BA = hello() ;
		Gstr1BasicSummarySectionDto gstr1SummaryB2CS = hello() ;
		Gstr1BasicSummarySectionDto gstr1SummaryB2CSA = hello();
		Gstr1BasicSummarySectionDto gstr1SummaryB2CL = hello();
		Gstr1BasicSummarySectionDto gstr1SummaryB2CLA = hello();
		Gstr1BasicSummarySectionDto gstr1SummaryCDNR = hello();
		Gstr1BasicSummarySectionDto gstr1SummaryCDNRA =hello();
		Gstr1BasicSummarySectionDto gstr1SummaryCDNUR = hello();
		Gstr1BasicSummarySectionDto gstr1SummaryCDNURA = hello();
		Gstr1BasicSummarySectionDto gstr1SummaryEXP = hello();
		Gstr1BasicSummarySectionDto gstr1SummaryEXPA = hello();
		Gstr1BasicSummarySectionDto gstr1SummaryAT = hello();
		Gstr1BasicSummarySectionDto gstr1SummaryATA = hello();
		Gstr1BasicSummarySectionDto gstr1SummaryTXPD = hello();
		Gstr1BasicSummarySectionDto gstr1SummaryTXPDA = hello();
		Gstr1DocIssuedSummarySectionDto gstr1SummaryDoc = docData();
		Gstr1NilRatedSummarySectionDto gstr1SummaryNil = nilData();
		
		
		
		for (Gstr1GetInvoicesReqDto dto : listReqDto) {
			Gson gson = GsonUtil.newSAPGsonInstance();
			String  apiResp = gstnApi(dto,groupCode);
			
			if(apiResp != null){
			JsonObject respObject = (new JsonParser()).parse(apiResp)
					.getAsJsonObject();
			
			JsonArray array = respObject.get("sec_sum").getAsJsonArray();
			for (JsonElement arr : array) {
				String secName = arr.getAsJsonObject().get("sec_nm")
						.getAsString();
				if (APIConstants.DOC_ISSUE.equals(secName)) {
					Gstr1SummaryDocIssuedEntity childEntity = gson.fromJson(
							arr.getAsJsonObject(),
							Gstr1SummaryDocIssuedEntity.class);
					
				//	Gstr1DocIssuedBasicSummary
					Gstr1DocIssuedSummarySectionDto docdto3 = 
				       new Gstr1DocIssuedSummarySectionDto();
					 gstr1SummaryDoc = gstr1SummaryDocDetails(docdto3,gstr1SummaryDoc,childEntity);
					listdocadto3.clear();
					listdocadto3.add(gstr1SummaryDoc);

				} else if (APIConstants.NIL.equals(secName)) {
					Gstr1SummaryNilEntity childEntity = gson.fromJson(
							arr.getAsJsonObject(), Gstr1SummaryNilEntity.class);
					Gstr1NilRatedSummarySectionDto nildto3 = 
				  new Gstr1NilRatedSummarySectionDto();
					gstr1SummaryNil = gstr1SummaryNilDetails(
							nildto3,gstr1SummaryNil, childEntity);
					listnildto3.clear();
					listnildto3.add(nildto3);
				} else {

					Gstr1SummaryRateEntity childEntity = gson.fromJson(
							arr.getAsJsonObject(),
							Gstr1SummaryRateEntity.class);
					
					
				//	Gstr1BasicSummarySectionDto gstr1SummaryB2B = hello();
					if (APIConstants.B2B.equals(secName)) {
						Gstr1BasicSummarySectionDto b2bdto3 = 
								new Gstr1BasicSummarySectionDto();

						 gstr1SummaryB2B = 
								gstr1SummaryDetails(
								b2bdto3,gstr1SummaryB2B,childEntity);
						 listb2bdto3.clear();
						listb2bdto3.add(gstr1SummaryB2B);
					}
				//	Gstr1BasicSummarySectionDto gstr1SummaryB2BA = hello() ;
					if (APIConstants.B2BA.equals(secName)) {
						Gstr1BasicSummarySectionDto b2badto3 = 
								new Gstr1BasicSummarySectionDto();
						gstr1SummaryB2BA = 
								gstr1SummaryDetails(b2badto3, gstr1SummaryB2BA,
										childEntity);
						listb2badto3.clear();
						listb2badto3.add(gstr1SummaryB2BA);
					}
				//	Gstr1BasicSummarySectionDto gstr1SummaryB2CS = hello() ;
					if (APIConstants.B2CS.equals(secName)) {
						Gstr1BasicSummarySectionDto b2csdto3 = 
								new Gstr1BasicSummarySectionDto();
						gstr1SummaryB2CS = gstr1SummaryDetails(b2csdto3,
								gstr1SummaryB2CS, childEntity);
						 listb2csdto3.clear();
						listb2csdto3.add(gstr1SummaryB2CS);
					}
				//	Gstr1BasicSummarySectionDto gstr1SummaryB2CSA = hello();
					if (APIConstants.B2CSA.equals(secName)) {
						Gstr1BasicSummarySectionDto b2csadto3 = 
								new Gstr1BasicSummarySectionDto();
						gstr1SummaryB2CSA = gstr1SummaryDetails(b2csadto3,
								gstr1SummaryB2CSA, childEntity);
						listb2csadto3.clear();
						listb2csadto3.add(gstr1SummaryB2CSA);
					}
				//	Gstr1BasicSummarySectionDto gstr1SummaryB2CL = hello();
					if (APIConstants.B2CL.equals(secName)) {
						Gstr1BasicSummarySectionDto b2cldto3 = 
								new Gstr1BasicSummarySectionDto();
						 
						gstr1SummaryB2CL = gstr1SummaryDetails(b2cldto3,
								gstr1SummaryB2CL, childEntity);
						listb2cldto3.clear();
						listb2cldto3.add(gstr1SummaryB2CL);
					}
				//	Gstr1BasicSummarySectionDto gstr1SummaryB2CLA = hello();
					if (APIConstants.B2CLA.equals(secName)) {
						Gstr1BasicSummarySectionDto b2cladto3 = 
								new Gstr1BasicSummarySectionDto();
						gstr1SummaryB2CLA = gstr1SummaryDetails(b2cladto3,
								gstr1SummaryB2CLA, childEntity);
						listb2cladto3.clear();
						listb2cladto3.add(gstr1SummaryB2CLA);
					}
				//	Gstr1BasicSummarySectionDto gstr1SummaryCDNR = hello();
					if (APIConstants.CDNR.equals(secName)) {
						Gstr1BasicSummarySectionDto cdnrdto3 = 
								new Gstr1BasicSummarySectionDto();
						 
						gstr1SummaryCDNR = gstr1SummaryDetails(cdnrdto3,
								gstr1SummaryCDNR, childEntity);
						listcdnrdto3.clear();
						listcdnrdto3.add(gstr1SummaryCDNR);
					}
				//	Gstr1BasicSummarySectionDto gstr1SummaryCDNRA =hello();
					if (APIConstants.CDNRA.equals(secName)) {
						Gstr1BasicSummarySectionDto cdnradto3 = 
								new Gstr1BasicSummarySectionDto();
						 
						gstr1SummaryCDNRA = gstr1SummaryDetails(cdnradto3,
								gstr1SummaryCDNRA, childEntity);
						listcdnradto3.clear();
						listcdnradto3.add(gstr1SummaryCDNRA);
					}
				//	Gstr1BasicSummarySectionDto gstr1SummaryCDNUR = hello();
					if (APIConstants.CDNUR.equals(secName)) {
						Gstr1BasicSummarySectionDto cdnurdto3 = 
								new Gstr1BasicSummarySectionDto();
						 
						gstr1SummaryCDNUR = gstr1SummaryDetails(cdnurdto3,
								gstr1SummaryCDNUR, childEntity);
						listcdnurdto3.clear();
						listcdnurdto3.add(gstr1SummaryCDNUR);
					}
				//	Gstr1BasicSummarySectionDto gstr1SummaryCDNURA = hello();
					if (APIConstants.CDNURA.equals(secName)) {
						Gstr1BasicSummarySectionDto cdnuradto3 = 
								new Gstr1BasicSummarySectionDto();
						 
						gstr1SummaryCDNURA = gstr1SummaryDetails(cdnuradto3,
								gstr1SummaryCDNURA, childEntity);
						listcdnuradto3.clear();
						listcdnuradto3.add(gstr1SummaryCDNURA);
					}
//					Gstr1BasicSummarySectionDto gstr1SummaryHSN = hello();
					if (APIConstants.HSN.equals(secName)) {
						Gstr1BasicSummarySectionDto hsndto3 = 
								new Gstr1BasicSummarySectionDto();
						 
						gstr1SummaryHSN = gstr1SummaryDetails(hsndto3,
								gstr1SummaryHSN, childEntity);
						listhsndto3.clear();
						listhsndto3.add(gstr1SummaryHSN);
					}
				//	Gstr1BasicSummarySectionDto gstr1SummaryEXP = hello();
					if (APIConstants.EXP.equals(secName)) {
						Gstr1BasicSummarySectionDto expdto3 = 
								new Gstr1BasicSummarySectionDto();
						 
						gstr1SummaryEXP = gstr1SummaryDetails(expdto3,
								gstr1SummaryEXP, childEntity);
						listexpdto3.clear();
						listexpdto3.add(gstr1SummaryEXP);
					}
				//	Gstr1BasicSummarySectionDto gstr1SummaryEXPA = hello();
					if (APIConstants.EXPA.equals(secName)) {
						Gstr1BasicSummarySectionDto expadto3 = 
								new Gstr1BasicSummarySectionDto();
						 
						gstr1SummaryEXPA = gstr1SummaryDetails(expadto3,
								gstr1SummaryEXPA, childEntity);
						listexpadto3.clear();
						listexpadto3.add(gstr1SummaryEXPA);
					}
				//	Gstr1BasicSummarySectionDto gstr1SummaryAT = hello();
					if (APIConstants.AT.equals(secName)) {
						Gstr1BasicSummarySectionDto atdto3 = 
								new Gstr1BasicSummarySectionDto();
						 
						gstr1SummaryAT = gstr1SummaryDetails(atdto3,
								gstr1SummaryAT, childEntity);
						listatdto3.clear();
						listatdto3.add(gstr1SummaryAT);
					}
			//		Gstr1BasicSummarySectionDto gstr1SummaryATA = hello();
					if (APIConstants.ATA.equals(secName)) {
						Gstr1BasicSummarySectionDto atadto3 = 
								new Gstr1BasicSummarySectionDto();
						 
						gstr1SummaryATA = gstr1SummaryDetails(atadto3,
								gstr1SummaryATA, childEntity);
						listatadto3.clear();
						listatadto3.add(gstr1SummaryATA);
					}
			//		Gstr1BasicSummarySectionDto gstr1SummaryTXPD = hello();
					if (APIConstants.TXPD.equals(secName)) {
						Gstr1BasicSummarySectionDto txpddto3 = 
								new Gstr1BasicSummarySectionDto();
						 
						gstr1SummaryTXPD = gstr1SummaryDetails(txpddto3,
								gstr1SummaryTXPD, childEntity);
						listtxpddto3.clear();
						listtxpddto3.add(gstr1SummaryTXPD);
					}
			//		Gstr1BasicSummarySectionDto gstr1SummaryTXPDA = hello();
					if (APIConstants.TXPDA.equals(secName)) {
						Gstr1BasicSummarySectionDto txpdadto3 = 
								new Gstr1BasicSummarySectionDto();
						 
						gstr1SummaryTXPDA = gstr1SummaryDetails(txpdadto3,
								gstr1SummaryTXPDA, childEntity);
						listtxpdadto3.clear();
						listtxpdadto3.add(gstr1SummaryTXPDA);
					}

				}

			
			}
		}
		}
		b2bdto2.setGstnSummary(listb2bdto3);
		b2bdto1.setB2b(b2bdto2);
		b2badto2.setGstnSummary(listb2badto3);
		b2bdto1.setB2ba(b2badto2);
		b2csdto2.setGstnSummary(listb2csdto3);
		b2bdto1.setB2cs(b2csdto2);
		b2cldto2.setGstnSummary(listb2cldto3);
		b2bdto1.setB2cl(b2cldto2);
		b2cladto2.setGstnSummary(listb2cladto3);
		b2bdto1.setBcla(b2cladto2);
		b2csadto2.setGstnSummary(listb2csadto3);
		b2bdto1.setB2csa(b2csadto2);
		cdnrdto2.setGstnSummary(listcdnrdto3);
		b2bdto1.setCdnr(cdnrdto2);
		cdnradto2.setGstnSummary(listcdnradto3);
		b2bdto1.setCdnra(cdnradto2);
		cdnurdto2.setGstnSummary(listcdnurdto3);
		b2bdto1.setCdnur(cdnurdto2);
		cdnuradto2.setGstnSummary(listcdnuradto3);
		b2bdto1.setCdnura(cdnuradto2);
		hsndto2.setGstnSummary(listhsndto3);
		b2bdto1.setHsn(hsndto2);
		expdto2.setGstnSummary(listexpdto3);
		b2bdto1.setExp(expdto2);
		expadto2.setGstnSummary(listexpadto3);
		b2bdto1.setExpa(expadto2);
		atdto2.setGstnSummary(listatdto3);
		b2bdto1.setAt(atdto2);
		atadto2.setGstnSummary(listatadto3);
		b2bdto1.setAta(atadto2);
		txpddto2.setGstnSummary(listtxpddto3);
		b2bdto1.setTxpd(txpddto2);
		txpdadto2.setGstnSummary(listtxpdadto3);
		b2bdto1.setTxpda(txpdadto2);
		docdto2.setGstnSummary(listdocadto3);
		b2bdto1.setDocIssued(docdto2);
		
		nildto2.setGstnSummary(listnildto3);
		
		b2bdto1.setNil(nildto2);
		
		
				
	//	return b2bdto1;
		return (SearchResult<R>) new SearchResult<Gstr1SummaryDto>(
				b2bdto1);

	
}

	private Gstr1BasicSummarySectionDto gstr1SummaryDetails(
			Gstr1BasicSummarySectionDto object,
			Gstr1BasicSummarySectionDto prevObject,
			Gstr1SummaryRateEntity childEntity) {
			/*if(prevObject==null){
				prevObject.setCess(new BigDecimal(0.0));
				prevObject.setCgst(new BigDecimal(0.0));
				prevObject.setIgst(new BigDecimal(0.0));
				prevObject.setInvValue(new BigDecimal(0.0));
				prevObject.setRecords(0);
				prevObject.setSgst(new BigDecimal(0.0));
				prevObject.setTaxableValue(new BigDecimal(0.0));
				prevObject.setTaxPayble(new BigDecimal(0.0));
			}*/
		if(childEntity.getTtlCgst()==null){
			childEntity.setTtlCgst(new BigDecimal(0.0));
		}
		if(childEntity.getTtlSgst()==null){
			childEntity.setTtlSgst(new BigDecimal(0.0));
		}
		object.setCess(childEntity.getTtlCess().add(prevObject.getCess()));
		object.setCgst(childEntity.getTtlCgst().add(prevObject.getCgst()));
		object.setIgst(childEntity.getTtlIgst().add(prevObject.getIgst()));
		object.setSgst(childEntity.getTtlSgst().add(prevObject.getSgst()));
		object.setTaxableValue(
				childEntity.getTtlTax().add(prevObject.getTaxableValue()));
		object.setTaxPayble(childEntity.getTtlIgst()
				.add(childEntity.getTtlCess()).add(prevObject.getTaxPayble()));
		object.setInvValue(childEntity.getTtlVal().add(prevObject.getInvValue()));
		object.setRecords(childEntity.getTtlRec()+ prevObject.getRecords());
		
		return object;
		
		
	}

	private Gstr1DocIssuedSummarySectionDto gstr1SummaryDocDetails(
			Gstr1DocIssuedSummarySectionDto object,
			Gstr1DocIssuedSummarySectionDto prevObject,
			Gstr1SummaryDocIssuedEntity childEntity) {
		object.setRecords(childEntity.getTtlRec() + 
				prevObject.getRecords());
		object.setTotalIssued(childEntity.getTtlDocIssued() + 
				prevObject.getTotalIssued());
		object.setNetIssued(childEntity.getNetDocIssued() + 
				prevObject.getNetIssued());
		object.setCancelled(childEntity.getTtlDocCancelled() + 
				prevObject.getCancelled());
		
		//object.setRecords(childEntity.getTtlRec());		
		return object;
		
		
	}
	private Gstr1NilRatedSummarySectionDto gstr1SummaryNilDetails(
			Gstr1NilRatedSummarySectionDto object,
			Gstr1NilRatedSummarySectionDto prevObject,
			Gstr1SummaryNilEntity childEntity) {
		object.setRecordCount(childEntity.getTtlRec() + 
				prevObject.getRecordCount());
		object.setTotalExempted(childEntity.getTtlExptAmt().
				add(prevObject.getTotalExempted()));
		object.setTotalNilRated(childEntity.getTtlNilsupAmt().
				add(prevObject.getTotalNilRated()));
		object.setTotalNonGST(childEntity.getTtlNgsupAmt().
				add(prevObject.getTotalNonGST()));
		return object;
		
	}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		// TODO Auto-generated method stub
		return null;
	}
	private  Gstr1BasicSummarySectionDto hello(){
		Gstr1BasicSummarySectionDto prevObject=new Gstr1BasicSummarySectionDto(); 
		prevObject.setCess(new BigDecimal(0.0));
		prevObject.setCgst(new BigDecimal(0.0));
		prevObject.setIgst(new BigDecimal(0.0));
		prevObject.setInvValue(new BigDecimal(0.0));
		prevObject.setRecords(0);
		prevObject.setSgst(new BigDecimal(0.0));
		prevObject.setTaxableValue(new BigDecimal(0.0));
		prevObject.setTaxPayble(new BigDecimal(0.0));
		return prevObject;
		
	}
	private  Gstr1DocIssuedSummarySectionDto docData(){
		Gstr1DocIssuedSummarySectionDto prevObject = new Gstr1DocIssuedSummarySectionDto();
		prevObject.setRecords(0);
		prevObject.setTotalIssued(0);
		prevObject.setNetIssued(0);
		prevObject.setCancelled(0);
		return prevObject;
		
	}
	private  Gstr1NilRatedSummarySectionDto nilData(){
		Gstr1NilRatedSummarySectionDto prevObject = new Gstr1NilRatedSummarySectionDto();
		prevObject.setRecordCount(0);
		prevObject.setTotalExempted(new BigDecimal(0));
		prevObject.setTotalNilRated(new BigDecimal(0));
		prevObject.setTotalNonGST(new BigDecimal(0));
		return prevObject;
		
	}
	
}
