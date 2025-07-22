/**
 * 
 */
package com.ey.advisory.app.services.search.docsummarysearch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.Gstr1SummaryDocIssuedEntity;
import com.ey.advisory.app.data.entities.client.Gstr1SummaryNilEntity;
import com.ey.advisory.app.data.entities.client.Gstr1SummaryRateEntity;
import com.ey.advisory.app.docs.dto.Gstr1BasicCDSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1BasicDocSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1BasicNilSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1BasicSectionSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1CompleteSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryCDSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryDocSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryNilSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummarySectionDto;
import com.ey.advisory.app.services.jobs.gstr1.Gstr1SummaryAtGstn;
import com.ey.advisory.app.services.jobs.gstr1.Gstr1SummaryDataAtGstn;
import com.ey.advisory.app.services.jobs.gstr1.Gstr1SummaryDataParser;
import com.ey.advisory.app.services.search.simplified.docsummary.Gstr1SimpleDocGstnSummarySearchService;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
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

/**
 * @author BalaKrishna S
 *
 */
@Service("GstnSummarySectionService")
public class GstnSummarySectionService implements SearchService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GstnSummarySectionService.class);
	
	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository gstinInfoRepository;

	@Autowired
	@Qualifier("GstnDateCalculation")
	GstnDateCalculation gstnDateCalculation;

	@Autowired
	@Qualifier("gstr1SummaryAtGstnImpl")
	Gstr1SummaryAtGstn gstr1SummaryDataAtGstn;

	
	
	@Autowired
	@Qualifier("gstr1SummaryDataParserImpl")
	Gstr1SummaryDataParser gstr1SummaryDataParser;
	
	@Autowired
	@Qualifier("Gstr1SimpleDocGstnSummarySearchService")
	Gstr1SimpleDocGstnSummarySearchService tableSearchService;

	public String gstnApi(Gstr1GetInvoicesReqDto dto, String groupCode) {
		String apiResp = null;
		try {
			apiResp = gstr1SummaryDataAtGstn.getGstr1Summary(dto, groupCode);
		} catch (java.lang.UnsupportedOperationException JsonObject) {
			apiResp = null;
			return apiResp;
		} catch (Exception JsonObject) {
			apiResp = null;
			return apiResp;
		}
		return apiResp;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> SearchResult<R> find(SearchCriteria criteria,
			PageRequest pageReq, Class<? extends R> retType) {
		// TODO Auto-generated method stub
		String groupCode = TenantContext.getTenantId();

		Annexure1SummaryReqDto request = (Annexure1SummaryReqDto) criteria;
		String gstin = null;
		List<String> gstinList = null;
		int taxPeriod = GenUtil.convertTaxPeriodToInt(request.getTaxPeriod());
		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();
		Gstr1CompleteSummaryDto b2bdto1 = new Gstr1CompleteSummaryDto();
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {
				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}
				// Gstr1CompleteSummaryDto b2bdto1 = new
				// Gstr1CompleteSummaryDto();

				Gstr1BasicSectionSummaryDto b2bdto2 = new Gstr1BasicSectionSummaryDto();
				Gstr1BasicSectionSummaryDto b2badto2 = new Gstr1BasicSectionSummaryDto();
				Gstr1BasicSectionSummaryDto b2cldto2 = new Gstr1BasicSectionSummaryDto();
				Gstr1BasicSectionSummaryDto b2cladto2 = new Gstr1BasicSectionSummaryDto();
				Gstr1BasicSectionSummaryDto expdto2 = new Gstr1BasicSectionSummaryDto();
				Gstr1BasicSectionSummaryDto expadto2 = new Gstr1BasicSectionSummaryDto();
			//	Gstr1BasicCDSectionSummaryDto b2csdto2 = new Gstr1BasicCDSectionSummaryDto();
			//	Gstr1BasicCDSectionSummaryDto b2csadto2 = new Gstr1BasicCDSectionSummaryDto();
				Gstr1BasicSectionSummaryDto atdto2 = new Gstr1BasicSectionSummaryDto();
				Gstr1BasicSectionSummaryDto atadto2 = new Gstr1BasicSectionSummaryDto();
				Gstr1BasicSectionSummaryDto txpddto2 = new Gstr1BasicSectionSummaryDto();
				Gstr1BasicSectionSummaryDto txpdadto2 = new Gstr1BasicSectionSummaryDto();
				
				Gstr1BasicDocSectionSummaryDto docdto2 = new Gstr1BasicDocSectionSummaryDto();
				Gstr1BasicNilSectionSummaryDto nildto2 = new Gstr1BasicNilSectionSummaryDto();

			/*	Gstr1BasicCDSectionSummaryDto hsndto2 = new Gstr1BasicCDSectionSummaryDto();
				Gstr1BasicCDSectionSummaryDto cdnrdto2 = new Gstr1BasicCDSectionSummaryDto();
				Gstr1BasicCDSectionSummaryDto cdnradto2 = new Gstr1BasicCDSectionSummaryDto();
				Gstr1BasicCDSectionSummaryDto cdnurdto2 = new Gstr1BasicCDSectionSummaryDto();
				Gstr1BasicCDSectionSummaryDto cdnuradto2 = new Gstr1BasicCDSectionSummaryDto();
*/
				
				Gstr1BasicSectionSummaryDto gstnHsndto2 = new Gstr1BasicSectionSummaryDto();
				Gstr1BasicSectionSummaryDto gstnCdnrdto2 = new Gstr1BasicSectionSummaryDto();
				Gstr1BasicSectionSummaryDto gstnCdnradto2 = new Gstr1BasicSectionSummaryDto();
				Gstr1BasicSectionSummaryDto gstnCdnurdto2 = new Gstr1BasicSectionSummaryDto();
				Gstr1BasicSectionSummaryDto gstnCdnuradto2 = new Gstr1BasicSectionSummaryDto();
			/*	List<Gstr1SummaryCDSectionDto> lb2csdto3 = new ArrayList<Gstr1SummaryCDSectionDto>();
				List<Gstr1SummaryCDSectionDto> listb2csadto3 = new ArrayList<Gstr1SummaryCDSectionDto>();
*/
				Gstr1BasicSectionSummaryDto gstnB2csdto2 = new Gstr1BasicSectionSummaryDto();
				Gstr1BasicSectionSummaryDto gstnB2csadto2 = new Gstr1BasicSectionSummaryDto();
				
				List<Gstr1SummarySectionDto> listb2bdto3 = new ArrayList<Gstr1SummarySectionDto>();

				List<Gstr1SummarySectionDto> listb2badto3 = new ArrayList<Gstr1SummarySectionDto>();

				List<Gstr1SummarySectionDto> listb2cldto3 = new ArrayList<Gstr1SummarySectionDto>();

				List<Gstr1SummarySectionDto> listb2cladto3 = new ArrayList<Gstr1SummarySectionDto>();
				List<Gstr1SummarySectionDto> listexpdto3 = new ArrayList<Gstr1SummarySectionDto>();
				List<Gstr1SummarySectionDto> listexpadto3 = new ArrayList<Gstr1SummarySectionDto>();
			//	List<Gstr1SummaryCDSectionDto> listb2csdto3 = new ArrayList<Gstr1SummaryCDSectionDto>();
			//	List<Gstr1SummaryCDSectionDto> listb2csadto3 = new ArrayList<Gstr1SummaryCDSectionDto>();

				List<Gstr1SummarySectionDto> listatdto3 = new ArrayList<Gstr1SummarySectionDto>();
				List<Gstr1SummarySectionDto> listatadto3 = new ArrayList<Gstr1SummarySectionDto>();
				List<Gstr1SummarySectionDto> listtxpddto3 = new ArrayList<Gstr1SummarySectionDto>();
				List<Gstr1SummarySectionDto> listtxpdadto3 = new ArrayList<Gstr1SummarySectionDto>();
				List<Gstr1SummaryDocSectionDto> listdocdto3 = new ArrayList<Gstr1SummaryDocSectionDto>();
				

			/*	List<Gstr1SummaryCDSectionDto> listcdnrdto3 = new ArrayList<Gstr1SummaryCDSectionDto>();

				List<Gstr1SummaryCDSectionDto> listcdnradto3 = new ArrayList<Gstr1SummaryCDSectionDto>();

				List<Gstr1SummaryCDSectionDto> listcdnurdto3 = new ArrayList<Gstr1SummaryCDSectionDto>();

				List<Gstr1SummaryCDSectionDto> listcdnuradto3 = new ArrayList<Gstr1SummaryCDSectionDto>();

				List<Gstr1SummaryCDSectionDto> listhsndto3 = new ArrayList<Gstr1SummaryCDSectionDto>();
				
				
				
*/			
				
				
				List<Gstr1SummarySectionDto> listb2csdto3 = new ArrayList<Gstr1SummarySectionDto>();
				List<Gstr1SummarySectionDto> listb2csadto3 = new ArrayList<Gstr1SummarySectionDto>();
				List<Gstr1SummarySectionDto> listcdnrdto3 = new ArrayList<Gstr1SummarySectionDto>();
				List<Gstr1SummarySectionDto> listcdnradto3 = new ArrayList<Gstr1SummarySectionDto>();
				List<Gstr1SummarySectionDto> listcdnurdto3 = new ArrayList<Gstr1SummarySectionDto>();
				List<Gstr1SummarySectionDto> listcdnuradto3 = new ArrayList<Gstr1SummarySectionDto>();
				List<Gstr1SummarySectionDto> listhsndto3 = new ArrayList<Gstr1SummarySectionDto>();
				
				
				
				
				List<Gstr1SummaryNilSectionDto> listnildto3 = new ArrayList<Gstr1SummaryNilSectionDto>();

				Gstr1SummarySectionDto gstr1SummaryB2B = hello();
				Gstr1SummarySectionDto gstr1SummaryB2BA = hello();
				Gstr1SummarySectionDto gstr1SummaryB2CL = hello();
			//	Gstr1SummaryCDSectionDto gstr1SummaryB2CS = helloCD();
			//	Gstr1SummaryCDSectionDto gstr1SummaryB2CSA = helloCD();
				
				Gstr1SummarySectionDto gstr1SummaryB2CS = hello();
				Gstr1SummarySectionDto gstr1SummaryB2CSA = hello();
				
				Gstr1SummarySectionDto gstr1SummaryB2CLA = hello();
				Gstr1SummarySectionDto gstr1SummaryEXP = hello();
				Gstr1SummarySectionDto gstr1SummaryEXPA = hello();
				Gstr1SummarySectionDto gstr1SummaryAT = hello();
				Gstr1SummarySectionDto gstr1SummaryATA = hello();
				Gstr1SummarySectionDto gstr1SummaryTXPD = hello();
				Gstr1SummarySectionDto gstr1SummaryTXPDA = hello();
				

				Gstr1SummaryDocSectionDto gstr1SummaryDoc = helloDoc();
				Gstr1SummaryNilSectionDto gstr1SummaryNil = helloNil();
				
				Gstr1SummarySectionDto gstr1SummaryCDNR = hello();
				Gstr1SummarySectionDto gstr1SummaryCDNRA = hello();
				Gstr1SummarySectionDto gstr1SummaryCDNUR = hello();
				Gstr1SummarySectionDto gstr1SummaryCDNURA = hello();
				Gstr1SummarySectionDto gstr1SummaryHSN = hello();
			
				
			/*	Gstr1SummaryCDSectionDto gstr1SummaryHSN = helloCD();
				Gstr1SummaryCDSectionDto gstr1SummaryCDNR = helloCD();
				Gstr1SummaryCDSectionDto gstr1SummaryCDNRA = helloCD();
				Gstr1SummaryCDSectionDto gstr1SummaryCDNUR = helloCD();
				Gstr1SummaryCDSectionDto gstr1SummaryCDNURA = helloCD();
*/
				Gstr1GetInvoicesReqDto reqDto;

				if (gstinList != null && gstinList.size() > 0) {
					for (String gstn : gstinList) {

						reqDto = new Gstr1GetInvoicesReqDto();
						reqDto.setGstin(gstn);
						reqDto.setReturnPeriod(request.getTaxPeriod());

						Gson gson = GsonUtil.newSAPGsonInstance();
						String apiResp = gstnApi(reqDto, groupCode);
						
						if(LOGGER.isDebugEnabled()){
							LOGGER.debug("GSTN Live API For GSTR1 Summary "
									+ "Response is @@@ --> " + apiResp);
							}
						
						
						if (apiResp != null) {
							JsonObject respObject = (new JsonParser())
									.parse(apiResp).getAsJsonObject();

							JsonArray array = respObject.get("sec_sum")
									.getAsJsonArray();
							for (JsonElement arr : array) {
								String secName = arr.getAsJsonObject()
										.get("sec_nm").getAsString();
								if (APIConstants.DOC_ISSUE.equalsIgnoreCase(secName)) {
									Gstr1SummaryDocIssuedEntity childEntity = gson
											.fromJson(arr.getAsJsonObject(),
													Gstr1SummaryDocIssuedEntity.class);

									// Gstr1DocIssuedBasicSummary
									Gstr1SummaryDocSectionDto docdto3 = new Gstr1SummaryDocSectionDto();
									gstr1SummaryDoc = gstr1SummaryDocDetails(
											docdto3, gstr1SummaryDoc,
											childEntity);
									listdocdto3.clear();
									listdocdto3.add(gstr1SummaryDoc);

								} else if (APIConstants.NIL.equalsIgnoreCase(secName)) {
									
								  Gstr1SummaryNilEntity childEntity =
									  gson.fromJson( arr.getAsJsonObject(),
									  Gstr1SummaryNilEntity.class);
									  Gstr1SummaryNilSectionDto nildto3 =new Gstr1SummaryNilSectionDto();
									  gstr1SummaryNil = gstr1SummaryNilDetails(
									  nildto3,gstr1SummaryNil, childEntity);
									  listnildto3.clear();
									  listnildto3.add(nildto3);
									 
								} else {

									Gstr1SummaryRateEntity childEntity = gson
											.fromJson(arr.getAsJsonObject(),
													Gstr1SummaryRateEntity.class);

									if (APIConstants.B2B.equalsIgnoreCase(secName)) {
										Gstr1SummarySectionDto b2bdto3 = new Gstr1SummarySectionDto();

										gstr1SummaryB2B = gstr1SummaryDetails(
												b2bdto3, gstr1SummaryB2B,
												childEntity);
										listb2bdto3.clear();
										listb2bdto3.add(gstr1SummaryB2B);
									}
									if (APIConstants.B2BA.equalsIgnoreCase(secName)) {
										Gstr1SummarySectionDto b2badto3 = new Gstr1SummarySectionDto();
										gstr1SummaryB2BA = gstr1SummaryDetails(
												b2badto3, gstr1SummaryB2BA,
												childEntity);
										listb2badto3.clear();
										listb2badto3.add(gstr1SummaryB2BA);
									}
									// Gstr1BasicSummarySectionDto
									// gstr1SummaryB2CS = hello() ;
									if (APIConstants.B2CS.equalsIgnoreCase(secName)) {
										Gstr1SummarySectionDto b2csdto3 = new Gstr1SummarySectionDto();
										gstr1SummaryB2CS = gstr1SummaryDetails(
												b2csdto3, gstr1SummaryB2CS,
												childEntity);
										listb2csdto3.clear();
										listb2csdto3.add(gstr1SummaryB2CS);
									}
									// Gstr1BasicSummarySectionDto
									// gstr1SummaryB2CSA = hello();
									if (APIConstants.B2CSA.equalsIgnoreCase(secName)) {
										Gstr1SummarySectionDto b2csadto3 = new Gstr1SummarySectionDto();
										gstr1SummaryB2CSA = gstr1SummaryDetails(
												b2csadto3, gstr1SummaryB2CSA,
												childEntity);
										listb2csadto3.clear();
										listb2csadto3.add(gstr1SummaryB2CSA);
									}
									// Gstr1BasicSummarySectionDto
									// gstr1SummaryB2CL = hello();
									if (APIConstants.B2CL.equalsIgnoreCase(secName)) {
										Gstr1SummarySectionDto b2cldto3 = new Gstr1SummarySectionDto();

										gstr1SummaryB2CL = gstr1SummaryDetails(
												b2cldto3, gstr1SummaryB2CL,
												childEntity);
										listb2cldto3.clear();
										listb2cldto3.add(gstr1SummaryB2CL);
									}
									// Gstr1BasicSummarySectionDto
									// gstr1SummaryB2CLA = hello();
									if (APIConstants.B2CLA.equalsIgnoreCase(secName)) {
										Gstr1SummarySectionDto b2cladto3 = new Gstr1SummarySectionDto();
										gstr1SummaryB2CLA = gstr1SummaryDetails(
												b2cladto3, gstr1SummaryB2CLA,
												childEntity);
										listb2cladto3.clear();
										listb2cladto3.add(gstr1SummaryB2CLA);
									}
									// Gstr1BasicSummarySectionDto
									// gstr1SummaryCDNR = hello();
									if (APIConstants.CDNR.equalsIgnoreCase(secName)) {
										Gstr1SummarySectionDto cdnrdto3 = new Gstr1SummarySectionDto();

										gstr1SummaryCDNR = gstr1SummaryDetails(
												cdnrdto3, gstr1SummaryCDNR,
												childEntity);
										listcdnrdto3.clear();
										listcdnrdto3.add(gstr1SummaryCDNR);
									}
									// Gstr1BasicSummarySectionDto
									// gstr1SummaryCDNRA =hello();
									if (APIConstants.CDNRA.equalsIgnoreCase(secName)) {
										Gstr1SummarySectionDto cdnradto3 = new Gstr1SummarySectionDto();

										gstr1SummaryCDNRA = gstr1SummaryDetails(
												cdnradto3, gstr1SummaryCDNRA,
												childEntity);
										listcdnradto3.clear();
										listcdnradto3.add(gstr1SummaryCDNRA);
									}
									// Gstr1BasicSummarySectionDto
									// gstr1SummaryCDNUR = hello();
									if (APIConstants.CDNUR.equalsIgnoreCase(secName)) {
										Gstr1SummarySectionDto cdnurdto3 = new Gstr1SummarySectionDto();

										gstr1SummaryCDNUR = gstr1SummaryDetails(
												cdnurdto3, gstr1SummaryCDNUR,
												childEntity);
										listcdnurdto3.clear();
										listcdnurdto3.add(gstr1SummaryCDNUR);
									}
									// Gstr1BasicSummarySectionDto
									// gstr1SummaryCDNURA = hello();
									if (APIConstants.CDNURA.equalsIgnoreCase(secName)) {
										Gstr1SummarySectionDto cdnuradto3 = new Gstr1SummarySectionDto();

										gstr1SummaryCDNURA = gstr1SummaryDetails(
												cdnuradto3, gstr1SummaryCDNURA,
												childEntity);
										listcdnuradto3.clear();
										listcdnuradto3.add(gstr1SummaryCDNURA);
									}
									// Gstr1BasicSummarySectionDto
									// gstr1SummaryHSN = hello();
									if (APIConstants.HSN.equalsIgnoreCase(secName)) {
										Gstr1SummarySectionDto hsndto3 = new Gstr1SummarySectionDto();

										gstr1SummaryHSN = gstr1SummaryDetails(
												hsndto3, gstr1SummaryHSN,
												childEntity);
										listhsndto3.clear();
										listhsndto3.add(gstr1SummaryHSN);
									}
									// Gstr1BasicSummarySectionDto
									// gstr1SummaryEXP = hello();
									if (APIConstants.EXP.equalsIgnoreCase(secName)) {
										Gstr1SummarySectionDto expdto3 = new Gstr1SummarySectionDto();

										gstr1SummaryEXP = gstr1SummaryDetails(
												expdto3, gstr1SummaryEXP,
												childEntity);
										listexpdto3.clear();
										listexpdto3.add(gstr1SummaryEXP);
									}
									// Gstr1BasicSummarySectionDto
									// gstr1SummaryEXPA = hello();
									if (APIConstants.EXPA.equalsIgnoreCase(secName)) {
										Gstr1SummarySectionDto expadto3 = new Gstr1SummarySectionDto();

										gstr1SummaryEXPA = gstr1SummaryDetails(
												expadto3, gstr1SummaryEXPA,
												childEntity);
										listexpadto3.clear();
										listexpadto3.add(gstr1SummaryEXPA);
									}
									// Gstr1BasicSummarySectionDto
									// gstr1SummaryAT = hello();
									if (APIConstants.AT.equalsIgnoreCase(secName)) {
										Gstr1SummarySectionDto atdto3 = new Gstr1SummarySectionDto();

										gstr1SummaryAT = gstr1SummaryDetails(
												atdto3, gstr1SummaryAT,
												childEntity);
										listatdto3.clear();
										listatdto3.add(gstr1SummaryAT);
									}
									// Gstr1BasicSummarySectionDto
									// gstr1SummaryATA = hello();
									if (APIConstants.ATA.equalsIgnoreCase(secName)) {
										Gstr1SummarySectionDto atadto3 = new Gstr1SummarySectionDto();

										gstr1SummaryATA = gstr1SummaryDetails(
												atadto3, gstr1SummaryATA,
												childEntity);
										listatadto3.clear();
										listatadto3.add(gstr1SummaryATA);
									}
									// Gstr1BasicSummarySectionDto
									// gstr1SummaryTXPD = hello();
									if (APIConstants.TXPD.equalsIgnoreCase(secName)) {
										Gstr1SummarySectionDto txpddto3 = new Gstr1SummarySectionDto();

										gstr1SummaryTXPD = gstr1SummaryDetails(
												txpddto3, gstr1SummaryTXPD,
												childEntity);
										listtxpddto3.clear();
										listtxpddto3.add(gstr1SummaryTXPD);
									}
									// Gstr1BasicSummarySectionDto
									// gstr1SummaryTXPDA = hello();
									if (APIConstants.TXPDA.equalsIgnoreCase(secName)) {
										Gstr1SummarySectionDto txpdadto3 = new Gstr1SummarySectionDto();

										gstr1SummaryTXPDA = gstr1SummaryDetails(
												txpdadto3, gstr1SummaryTXPDA,
												childEntity);
										listtxpdadto3.clear();
										listtxpdadto3.add(gstr1SummaryTXPDA);
									}

								}
							}
						}/*else {
							 Gstr1CompleteSummaryDto find = tableSearchService.find(request);
						//	find.getResult();
							return (SearchResult<R>) new SearchResult<Gstr1CompleteSummaryDto>(
									find);
						}*/
					}
				}

				b2bdto2.setGstnSummary(listb2bdto3);
				b2bdto1.setB2b(b2bdto2);
				
				b2badto2.setGstnSummary(listb2badto3);
				b2bdto1.setB2ba(b2badto2);
			
				gstnB2csdto2.setGstnSummary(listb2csdto3);
				b2bdto1.setGstnB2cs(gstnB2csdto2);
				
				b2cldto2.setGstnSummary(listb2cldto3);
				b2bdto1.setB2cl(b2cldto2);
				
				b2cladto2.setGstnSummary(listb2cladto3);
				b2bdto1.setB2cla(b2cladto2);
				
				gstnB2csadto2.setGstnSummary(listb2csadto3);
				b2bdto1.setGstnB2csa(gstnB2csadto2);
				
				gstnCdnrdto2.setGstnSummary(listcdnrdto3);
				b2bdto1.setGstnCdnr(gstnCdnrdto2);
				
				gstnCdnradto2.setGstnSummary(listcdnradto3);
				b2bdto1.setGstnCdnra(gstnCdnradto2);
				
				gstnCdnurdto2.setGstnSummary(listcdnurdto3);
				b2bdto1.setGstnCdnur(gstnCdnurdto2);
				
				gstnCdnuradto2.setGstnSummary(listcdnuradto3);
				b2bdto1.setGstnCdnura(gstnCdnuradto2);
				
				gstnHsndto2.setGstnSummary(listhsndto3);
				b2bdto1.setGstnHsn(gstnHsndto2);
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
				docdto2.setGstnSummary(listdocdto3);
				b2bdto1.setDocIssues(docdto2);

				 nildto2.setGstnSummary(listnildto3);

				 b2bdto1.setNil(nildto2);
			}
		}
		return (SearchResult<R>) new SearchResult<Gstr1CompleteSummaryDto>(
				b2bdto1);

	}

	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		// TODO Auto-generated method stub
		return null;
	}

	private Gstr1SummarySectionDto gstr1SummaryDetails(
			Gstr1SummarySectionDto object, Gstr1SummarySectionDto prevObject,
			Gstr1SummaryRateEntity childEntity) {
		/*
		 * if(prevObject==null){ prevObject.setCess(new BigDecimal(0.0));
		 * prevObject.setCgst(new BigDecimal(0.0)); prevObject.setIgst(new
		 * BigDecimal(0.0)); prevObject.setInvValue(new BigDecimal(0.0));
		 * prevObject.setRecords(0); prevObject.setSgst(new BigDecimal(0.0));
		 * prevObject.setTaxableValue(new BigDecimal(0.0));
		 * prevObject.setTaxPayble(new BigDecimal(0.0)); }
		 */
		if (childEntity.getTtlCgst() == null) {
			childEntity.setTtlCgst(new BigDecimal(0.0));
		}
		if (childEntity.getTtlSgst() == null) {
			childEntity.setTtlSgst(new BigDecimal(0.0));
		}
		if (childEntity.getTtlIgst() == null){
			childEntity.setTtlIgst(new BigDecimal(0.0));
		}
		if (childEntity.getTtlCess() == null){
			childEntity.setTtlCess(new BigDecimal(0.0));
		}
		if (childEntity.getTtlTax() == null){
			childEntity.setTtlTax(new BigDecimal(0.0));
		}
		if (childEntity.getTtlVal() == null){
			childEntity.setTtlVal(new BigDecimal(0.0));
		}
		
		object.setCess(childEntity.getTtlCess().add(prevObject.getCess()));
		object.setCgst(childEntity.getTtlCgst().add(prevObject.getCgst()));
		object.setIgst(childEntity.getTtlIgst().add(prevObject.getIgst()));
		object.setSgst(childEntity.getTtlSgst().add(prevObject.getSgst()));
		object.setTaxableValue(
				childEntity.getTtlTax().add(prevObject.getTaxableValue()));
		/*object.setTaxPayable(childEntity.getTtlIgst()
				.add(childEntity.getTtlCess()).add(prevObject.getTaxPayable()));
		*/
		object.setTaxPayable((childEntity.getTtlIgst()).add(childEntity.getTtlSgst()).add(childEntity.getTtlCgst())
				.add(childEntity.getTtlCess()).add(prevObject.getTaxPayable()));
		object.setInvValue(
				childEntity.getTtlVal().add(prevObject.getInvValue()));
		object.setRecords(childEntity.getTtlRec() + prevObject.getRecords());
		
		return object;

	}

	private Gstr1SummaryCDSectionDto gstr1SummaryCDDetails(
			Gstr1SummaryCDSectionDto object,
			Gstr1SummaryCDSectionDto prevObject,
			Gstr1SummaryRateEntity childEntity) {
		/*
		 * if(prevObject==null){ prevObject.setCess(new BigDecimal(0.0));
		 * prevObject.setCgst(new BigDecimal(0.0)); prevObject.setIgst(new
		 * BigDecimal(0.0)); prevObject.setInvValue(new BigDecimal(0.0));
		 * prevObject.setRecords(0); prevObject.setSgst(new BigDecimal(0.0));
		 * prevObject.setTaxableValue(new BigDecimal(0.0));
		 * prevObject.setTaxPayble(new BigDecimal(0.0)); }
		 */
		if (childEntity.getTtlCgst() == null) {
			childEntity.setTtlCgst(new BigDecimal(0.0));
		}
		if (childEntity.getTtlSgst() == null) {
			childEntity.setTtlSgst(new BigDecimal(0.0));
		}
		if (childEntity.getTtlIgst() == null){
			childEntity.setTtlIgst(new BigDecimal(0.0));
		}
		if (childEntity.getTtlCess() == null){
			childEntity.setTtlCess(new BigDecimal(0.0));
		}
		if (childEntity.getTtlTax() == null){
			childEntity.setTtlTax(new BigDecimal(0.0));
		}
		if (childEntity.getTtlVal() == null){
			childEntity.setTtlVal(new BigDecimal(0.0));
		}
		
		object.setCess(childEntity.getTtlCess().add(prevObject.getCess()));
		object.setCgst(childEntity.getTtlCgst().add(prevObject.getCgst()));
		object.setIgst(childEntity.getTtlIgst().add(prevObject.getIgst()));
		object.setSgst(childEntity.getTtlSgst().add(prevObject.getSgst()));
		object.setTaxableValue(
				childEntity.getTtlTax().add(prevObject.getTaxableValue()));
		object.setTaxPayable((childEntity.getTtlIgst()).add(childEntity.getTtlSgst()).add(childEntity.getTtlCgst())
				.add(childEntity.getTtlCess()).add(prevObject.getTaxPayable()));
		object.setInvValue(
				childEntity.getTtlVal().add(prevObject.getInvValue()));
		object.setRecords(childEntity.getTtlRec() + prevObject.getRecords());
		return object;

	}

	private Gstr1SummarySectionDto hello() {
		Gstr1SummarySectionDto prevObject = new Gstr1SummarySectionDto();
		prevObject.setCess(new BigDecimal(0.0));
		prevObject.setCgst(new BigDecimal(0.0));
		prevObject.setIgst(new BigDecimal(0.0));
		prevObject.setInvValue(new BigDecimal(0.0));
		prevObject.setRecords(0);
		prevObject.setSgst(new BigDecimal(0.0));
		prevObject.setTaxableValue(new BigDecimal(0.0));
		prevObject.setTaxPayable(new BigDecimal(0.0));
		return prevObject;

	}

	private Gstr1SummaryDocSectionDto helloDoc() {
		Gstr1SummaryDocSectionDto prevObject = new Gstr1SummaryDocSectionDto();
		prevObject.setDocCancelled(0);
		prevObject.setNetIssued(0);
		prevObject.setTotal(0);
		return prevObject;

	}
	
	private Gstr1SummaryNilSectionDto helloNil() {
		Gstr1SummaryNilSectionDto prevObject = new Gstr1SummaryNilSectionDto();
		prevObject.setAspExempted(BigDecimal.ZERO);
		prevObject.setAspNitRated(BigDecimal.ZERO);
		prevObject.setAspNonGst(BigDecimal.ZERO);
		return prevObject;

	}

	private Gstr1SummaryCDSectionDto helloCD() {
		Gstr1SummaryCDSectionDto prevObject = new Gstr1SummaryCDSectionDto();
		prevObject.setCess(new BigDecimal(0.0));
		prevObject.setCgst(new BigDecimal(0.0));
		prevObject.setIgst(new BigDecimal(0.0));
		prevObject.setInvValue(new BigDecimal(0.0));
		prevObject.setRecords(0);
		prevObject.setSgst(new BigDecimal(0.0));
		prevObject.setTaxableValue(new BigDecimal(0.0));
		prevObject.setTaxPayable(new BigDecimal(0.0));
		return prevObject;
	}

	private Gstr1SummaryDocSectionDto gstr1SummaryDocDetails(
			Gstr1SummaryDocSectionDto object,
			Gstr1SummaryDocSectionDto prevObject,
			Gstr1SummaryDocIssuedEntity childEntity) {
		
		object.setTotal(childEntity.getTtlRec() + prevObject.getTotal());
		object.setNetIssued(
				childEntity.getTtlDocIssued() + prevObject.getNetIssued());
		object.setNetIssued(
				childEntity.getNetDocIssued() + prevObject.getNetIssued());
		object.setDocCancelled(childEntity.getTtlDocCancelled()
				+ prevObject.getDocCancelled());

		// object.setRecords(childEntity.getTtlRec());
		return object;

	}
	private Gstr1SummaryNilSectionDto gstr1SummaryNilDetails(
			Gstr1SummaryNilSectionDto object,
			Gstr1SummaryNilSectionDto prevObject,
			Gstr1SummaryNilEntity childEntity) {
		
		if(childEntity.getTtlExptAmt() == null ){
			childEntity.setTtlExptAmt(new BigDecimal(0.0));
		}
		if(childEntity.getTtlNilsupAmt() == null ){
			childEntity.setTtlNilsupAmt(new BigDecimal(0.0));
		}
		if(childEntity.getTtlNgsupAmt() == null ){
			childEntity.setTtlNgsupAmt(new BigDecimal(0.0));
		}
		object.setAspExempted(childEntity.getTtlExptAmt().add(prevObject.getAspExempted()));
		object.setAspNitRated(childEntity.getTtlNilsupAmt().add(prevObject.getAspNitRated()));
		object.setAspNonGst(childEntity.getTtlNgsupAmt().add(prevObject.getAspNonGst()));

		return object;

	}
}