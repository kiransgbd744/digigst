package com.ey.advisory.app.services.search.simplified.docsummary;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.anx1.Anx1GetSectionSummaryData;
import com.ey.advisory.app.docs.dto.simplified.Annexure1BasicEcomSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1BasicSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionEcomDto;
import com.ey.advisory.app.services.jobs.anx1.Anx1SummaryDataAtGstn;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
import com.ey.advisory.core.dto.Anx1GetInvoicesReqDto;
import com.ey.advisory.core.search.PageRequest;
import com.ey.advisory.core.search.SearchCriteria;
import com.ey.advisory.core.search.SearchResult;
import com.ey.advisory.core.search.SearchService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service("Anx1GetGstnDataSearchService")
public class Anx1GetGstnDataSearchService implements SearchService {

	@Autowired
	@Qualifier("Anx1SummaryDataAtGstnImpl")
	private Anx1SummaryDataAtGstn anx1SummaryDataAtGstn;
	
	public String gstnApi(Anx1GetInvoicesReqDto dto, String groupCode) {
		String apiResp = null;
		try {
			apiResp = anx1SummaryDataAtGstn.getAnx1Summary(dto, groupCode);
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

		String groupCode = TenantContext.getTenantId();
		Annexure1SummaryReqDto request = (Annexure1SummaryReqDto) criteria;
		String gstin = null;
		List<String> gstinList = null;
		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();
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
			}
		}

				Anx1GetInvoicesReqDto reqDto;
				String apiResp = null;
				if (gstinList != null && gstinList.size() > 0) {
					for (String gstn : gstinList) {

						reqDto = new Anx1GetInvoicesReqDto();
						reqDto.setGstin(gstn);
						reqDto.setReturnPeriod(request.getTaxPeriod());
						 apiResp = gstnApi(reqDto, groupCode);
					}
				}
		
	
	
	/*	Annexure1SummaryReqDto req = (Annexure1SummaryReqDto) criteria;

		String apiResp = "{'gstin':'19AHLPP7361BMZJ','rtnprd':'092019','summtyp':'H',"
				+ "'chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'secsum':[" + "{'cptysum':[{'ctin':'19AHLPP7361BOZH',"
				+ "'chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':1,'ttlval':9000,'ttligst':675,'ttlcgst':0,"
				+ "'ttlsgst':0,'ttlcess':9000,'nettax':9000},"
				+ "{'ctin':'19AHLPP7361BNZI',"
				+ "'chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':3,'ttlval':0,'ttligst':3510,'ttlcgst':0,"
				+ "'ttlsgst':0,'ttlcess':0,'nettax':30000}],'actionsum':["
				+ "{'action':'A','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':1,'ttlval':9000,'ttligst':675,'ttlcgst':0,'ttlsgst':0,'ttlcess':9000,'nettax':9000},"
				+ "{'action':'C','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':3,'ttlval':0,'ttligst':3510,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':30000}],"
				+ "'doctypsum':[{'action':'C','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':1,'ttlval':0,'ttligst':0,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':30000},"
				+ "{'action':'D','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':1,'ttlval':0,'ttligst':0,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':30000},"
				+ "{'action':'I','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':2,'ttlval':9000,'ttligst':4185,'ttlcgst':0,'ttlsgst':0,'ttlcess':9000,'nettax':39000}],"
				+ "'secnm':'B2B','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279','ttldoc':4,"
				+ "'ttlval':9000,'ttligst':4185,'ttlcgst':0,'ttlsgst':0,'ttlcess':9000,'nettax':39000},"
				+ "{'secnm':'B2C','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':4,'ttlval':0,'ttligst':130675,'ttlcgst':0,'ttlsgst':0,'ttlcess':104000,'nettax':99100},"
				+ "{'cptysum':["
				+ "{'ctin':'19AHLPP7361BNZI','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':4,'ttlval':9000,'ttligst':255,'ttlcgst':97.5,'ttlsgst':97.5,'ttlcess':1000,'nettax':19000}],"
				+ "'actionsum':[{'action':'A','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':4,'ttlval':9000,'ttligst':255,'ttlcgst':97.5,'ttlsgst':97.5,'ttlcess':1000,'nettax':19000}],"
				+ "'doctypsum':[{'action':'C','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':1,'ttlval':0,'ttligst':195,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':10000},"
				+ "{'action':'D','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':2,'ttlval':9000,'ttligst':450,'ttlcgst':0,'ttlsgst':0,'ttlcess':1000,'nettax':19000},"
				+ "{'action':'I','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':1,'ttlval':0,'ttligst':0,'ttlcgst':97.5,'ttlsgst':97.5,'ttlcess':0,'nettax':10000}],"
				+ "'secnm':'DE','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':4,'ttlval':9000,'ttligst':255,'ttlcgst':97.5,'ttlsgst':97.5,'ttlcess':1000,'nettax':19000},"
				+ "{'cptysum':[],'secnm':'ECOM','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':1,'ttlval':0,'ttligst':12,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':0},{'doctypsum':["
				+ "{'action':'C','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':1,'ttlval':0,'ttligst':0,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':65000},"
				+ "{'action':'D','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':1,'ttlval':0,'ttligst':0,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':65000},"
				+ "{'action':'I','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':1,'ttlval':0,'ttligst':0,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':65000}],"
				+ "'secnm':'EXPWOP','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':3,'ttlval':0,'ttligst':0,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':65000},"
				+ "{'doctypsum':[{'action':'C','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':1,'ttlval':0,'ttligst':1950,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':65000},"
				+ "{'action':'D','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':1,'ttlval':0,'ttligst':1950,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':65000},"
				+ "{'action':'I','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':2,'ttlval':9000,'ttligst':2475,'ttlcgst':0,'ttlsgst':0,'ttlcess':90000,'nettax':72000}],"
				+ "'secnm':'EXPWP','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':4,'ttlval':9000,'ttligst':2475,'ttlcgst':0,'ttlsgst':0,'ttlcess':90000,'nettax':72000},"
				+ "{'secnm':'IMPG','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':3,'ttlval':0,'ttligst':1620,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':162000},"
				+ "{'cptysum':[{'ctin':'33AACCA0121EAZG','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':3,'ttlval':1000,'ttligst':360,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':36000}],"
				+ "'secnm':'IMPGSEZ','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':3,'ttlval':1000,'ttligst':360,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':36000},"
				+ "{'secnm':'IMPS','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':2,'ttlval':0,'ttligst':1012,'ttlcgst':0,'ttlsgst':0,'ttlcess':1000,'nettax':65121},"
				+ "{'secnm':'MIS','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':0,'ttlval':0,'ttligst':0,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':0},"
				+ "{'cptysum':[],'secnm':'REV','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':2,'ttlval':0,'ttligst':1068.25,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':651200},"
				+ "{'cptysum':[{'ctin':'33AACCA0121EAZG','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':17,'ttlval':7000,'ttligst':0,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':7000}],"
				+ "'actionsum':[{'action':'C','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':17,'ttlval':7000,'ttligst':0,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':7000}],"
				+ "'doctypsum':[{'action':'C','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':5,'ttlval':5000,'ttligst':0,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':5000},"
				+ "{'action':'D','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':6,'ttlval':6000,'ttligst':0,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':6000},"
				+ "{'action':'I','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':6,'ttlval':6000,'ttligst':0,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':6000}],"
				+ "'secnm':'SEZWOP','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':17,'ttlval':7000,'ttligst':0,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':7000},"
				+ "{'cptysum':[{'ctin':'33AACCA0121EAZG','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':17,'ttlval':7000,'ttligst':136.5,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':7000}],"
				+ "'actionsum':[{'action':'A','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':17,'ttlval':7000,'ttligst':136.5,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':7000}],"
				+ "'doctypsum':[{'action':'C','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':5,'ttlval':5000,'ttligst':97.5,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':5000},"
				+ "{'action':'D','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':6,'ttlval':6000,'ttligst':117,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':6000},"
				+ "{'action':'I','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':6,'ttlval':6000,'ttligst':117,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':6000}],"
				+ "'secnm':'SEZWP','chksum':'0dbdc754fbb79bf3eba1207a9bd0e21d27d3bf0935eaf8d58a29f0db32cef279',"
				+ "'ttldoc':17,'ttlval':7000,'ttligst':136.5,'ttlcgst':0,'ttlsgst':0,'ttlcess':0,'nettax':7000}]}";
*/
		Annexure1SummaryDto b2bdto1 = new Annexure1SummaryDto();
		Annexure1BasicSummaryDto b2bdto2 = new Annexure1BasicSummaryDto();
		Annexure1BasicSummaryDto b2cdto2 = new Annexure1BasicSummaryDto();
		Annexure1BasicSummaryDto expwtdto2 = new Annexure1BasicSummaryDto();
		Annexure1BasicSummaryDto expwotdto2 = new Annexure1BasicSummaryDto();
		Annexure1BasicSummaryDto sezwtdto2 = new Annexure1BasicSummaryDto();
		Annexure1BasicSummaryDto sezwotdto2 = new Annexure1BasicSummaryDto();
		Annexure1BasicSummaryDto dedto2 = new Annexure1BasicSummaryDto();
		Annexure1BasicSummaryDto revdto2 = new Annexure1BasicSummaryDto();
		Annexure1BasicSummaryDto impsdto2 = new Annexure1BasicSummaryDto();
		Annexure1BasicSummaryDto impgdto2 = new Annexure1BasicSummaryDto();
		Annexure1BasicSummaryDto impgsezdto2 = new Annexure1BasicSummaryDto();
		Annexure1BasicEcomSummaryDto ecomdto2 = new Annexure1BasicEcomSummaryDto();
		
		List<Annexure1SummarySectionDto> listb2bdto3 = new ArrayList<Annexure1SummarySectionDto>();
		List<Annexure1SummarySectionDto> listb2cdto3 = new ArrayList<Annexure1SummarySectionDto>();
		List<Annexure1SummarySectionDto> listexpwtdto3 = new ArrayList<Annexure1SummarySectionDto>();
		List<Annexure1SummarySectionDto> listexpwotdto3 = new ArrayList<Annexure1SummarySectionDto>();
		List<Annexure1SummarySectionDto> listsezwtdto3 = new ArrayList<Annexure1SummarySectionDto>();
		List<Annexure1SummarySectionDto> listsezwotdto3 = new ArrayList<Annexure1SummarySectionDto>();
		List<Annexure1SummarySectionDto> listDEdto3 = new ArrayList<Annexure1SummarySectionDto>();
		List<Annexure1SummarySectionDto> listRevdto3 = new ArrayList<Annexure1SummarySectionDto>();
		List<Annexure1SummarySectionDto> listImpsdto3 = new ArrayList<Annexure1SummarySectionDto>();
		List<Annexure1SummarySectionDto> listImpgdto3 = new ArrayList<Annexure1SummarySectionDto>();
		List<Annexure1SummarySectionDto> listImpgsezdto3 = new ArrayList<Annexure1SummarySectionDto>();
		List<Annexure1SummarySectionEcomDto> listEcomdto3 = new ArrayList<Annexure1SummarySectionEcomDto>();

		Annexure1SummarySectionDto gstr1SummaryB2B = hello();
		Annexure1SummarySectionDto gstr1SummaryB2C = hello();
		Annexure1SummarySectionDto gstr1SummaryExpwt = hello();
			Annexure1SummarySectionDto gstr1SummaryExpwot = hello();
		Annexure1SummarySectionDto gstr1SummarySezwt = hello();
		Annexure1SummarySectionDto gstr1SummarySezwot = hello();
		Annexure1SummarySectionDto gstr1SummaryDe = hello();
		Annexure1SummarySectionDto gstr1SummaryRev = hello();
		Annexure1SummarySectionDto gstr1SummaryImps = hello();
		Annexure1SummarySectionDto gstr1SummaryImpg = hello();
		Annexure1SummarySectionDto gstr1SummaryImpgsez = hello();
		Annexure1SummarySectionEcomDto gstr1SummaryEcom = helloEcomm();

		if (apiResp != null) {
			JsonObject respObject = (new JsonParser()).parse(apiResp)
					.getAsJsonObject();

			JsonArray array = respObject.get("secsum").getAsJsonArray();

			for (JsonElement arr : array) {
				String secName = arr.getAsJsonObject().get("secnm")
						.getAsString();

				Gson gson = GsonUtil.newSAPGsonInstance();
				Anx1GetSectionSummaryData childEntity = gson.fromJson(
						arr.getAsJsonObject(), Anx1GetSectionSummaryData.class);

				if (APIConstants.B2B.equalsIgnoreCase(secName)) {

					Annexure1SummarySectionDto b2bdto3 = new Annexure1SummarySectionDto();

					gstr1SummaryB2B = gstr1SummaryDetails(b2bdto3,
							gstr1SummaryB2B, childEntity);
					listb2bdto3.clear();
					listb2bdto3.add(gstr1SummaryB2B);

				}
				if (APIConstants.B2C.equalsIgnoreCase(secName)) {

					Annexure1SummarySectionDto b2cdto3 = new Annexure1SummarySectionDto();

					gstr1SummaryB2C = gstr1SummaryDetails(b2cdto3,
							gstr1SummaryB2C, childEntity);
					listb2cdto3.clear();
					listb2cdto3.add(gstr1SummaryB2C);

				}
				if (APIConstants.EXPWP.equalsIgnoreCase(secName)) {

					Annexure1SummarySectionDto expwtdto3 = new Annexure1SummarySectionDto();

					gstr1SummaryExpwt = gstr1SummaryDetails(expwtdto3,
							gstr1SummaryExpwt, childEntity);
					listexpwtdto3.clear();
					listexpwtdto3.add(gstr1SummaryB2C);

				}
				if (APIConstants.EXPWOP.equalsIgnoreCase(secName)) {

					Annexure1SummarySectionDto expwotdto3 = new Annexure1SummarySectionDto();

					gstr1SummaryExpwot = gstr1SummaryDetails(expwotdto3,
							gstr1SummaryExpwot, childEntity);
					listexpwotdto3.clear();
					listexpwotdto3.add(gstr1SummaryB2C);

				}
				if (APIConstants.SEZWP.equalsIgnoreCase(secName)) {

					Annexure1SummarySectionDto sezwtdto3 = new Annexure1SummarySectionDto();

					gstr1SummarySezwt = gstr1SummaryDetails(sezwtdto3,
							gstr1SummarySezwt, childEntity);
					listsezwtdto3.clear();
					listsezwtdto3.add(gstr1SummarySezwt);

				}
				if (APIConstants.SEZWOP.equalsIgnoreCase(secName)) {

					Annexure1SummarySectionDto sezwotdto3 = new Annexure1SummarySectionDto();

					gstr1SummarySezwot = gstr1SummaryDetails(sezwotdto3,
							gstr1SummarySezwot, childEntity);
					listsezwotdto3.clear();
					listsezwotdto3.add(gstr1SummarySezwot);

				}
				if (APIConstants.DE.equalsIgnoreCase(secName)) {

					Annexure1SummarySectionDto dedto3 = new Annexure1SummarySectionDto();

					gstr1SummaryDe = gstr1SummaryDetails(dedto3,
							gstr1SummaryDe, childEntity);
					listDEdto3.clear();
					listDEdto3.add(gstr1SummarySezwot);

				}
				if (APIConstants.REV.equalsIgnoreCase(secName)) {

					Annexure1SummarySectionDto revdto3 = new Annexure1SummarySectionDto();

					gstr1SummaryRev = gstr1SummaryDetails(revdto3,
							gstr1SummaryRev, childEntity);
					listRevdto3.clear();
					listRevdto3.add(gstr1SummaryRev);

				}
				if (APIConstants.IMPS.equalsIgnoreCase(secName)) {

					Annexure1SummarySectionDto impsdto3 = new Annexure1SummarySectionDto();

					gstr1SummaryImps = gstr1SummaryDetails(impsdto3,
							gstr1SummaryImps, childEntity);
					listImpsdto3.clear();
					listImpsdto3.add(gstr1SummaryImps);

				}
				if (APIConstants.IMPG.equalsIgnoreCase(secName)) {

					Annexure1SummarySectionDto impgdto3 = new Annexure1SummarySectionDto();

					gstr1SummaryImpg = gstr1SummaryDetails(impgdto3,
							gstr1SummaryImpg, childEntity);
					listImpgdto3.clear();
					listImpgdto3.add(gstr1SummaryImpg);

				}
				if (APIConstants.IMPGSEZ.equalsIgnoreCase(secName)) {

					Annexure1SummarySectionDto impgsezdto3 = new Annexure1SummarySectionDto();

					gstr1SummaryImpgsez = gstr1SummaryDetails(impgsezdto3,
							gstr1SummaryImpgsez, childEntity);
					listImpgsezdto3.clear();
					listImpgsezdto3.add(gstr1SummaryImpgsez);

				}
				if (APIConstants.ECOM.equalsIgnoreCase(secName)) {

					Annexure1SummarySectionEcomDto ecomdto3 = new Annexure1SummarySectionEcomDto();

					gstr1SummaryEcom = gstr1SummaryDetailsEcom(ecomdto3,
							gstr1SummaryEcom, childEntity);
					listEcomdto3.clear();
					listEcomdto3.add(gstr1SummaryEcom);

				}
				
			}
		}
		b2bdto2.setGstnSummary(listb2bdto3);
		b2bdto1.setB2b(b2bdto2);
		b2cdto2.setGstnSummary(listb2cdto3);
		b2bdto1.setB2c(b2cdto2);
		expwtdto2.setGstnSummary(listexpwtdto3);
		b2bdto1.setExpwt(expwtdto2);
		expwotdto2.setGstnSummary(listexpwotdto3);
		b2bdto1.setExpt(expwotdto2);
		sezwtdto2.setGstnSummary(listsezwtdto3);
		b2bdto1.setSezwt(sezwtdto2);
		sezwotdto2.setGstnSummary(listsezwotdto3);
		b2bdto1.setSezt(sezwotdto2);
		dedto2.setGstnSummary(listDEdto3);
		b2bdto1.setDeemedExp(dedto2);
		
		revdto2.setGstnSummary(listRevdto3);
		b2bdto1.setRev(revdto2);
		impgdto2.setGstnSummary(listImpgdto3);
		b2bdto1.setImpg(impgdto2);
		impsdto2.setGstnSummary(listImpsdto3);
		b2bdto1.setImps(impsdto2);
		impgsezdto2.setGstnSummary(listImpgsezdto3);
		b2bdto1.setImpgSez(impgsezdto2);
		
		ecomdto2.setGstnSummary(listEcomdto3);
		b2bdto1.setTable4(ecomdto2);
		
		return (SearchResult<R>) new SearchResult<Annexure1SummaryDto>(
				b2bdto1);

	}

	private Annexure1SummarySectionDto hello() {
		Annexure1SummarySectionDto prevObject = new Annexure1SummarySectionDto();
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
	
	private Annexure1SummarySectionEcomDto helloEcomm() {
		Annexure1SummarySectionEcomDto prevObject = new Annexure1SummarySectionEcomDto();
		prevObject.setCess(new BigDecimal(0.0));
		prevObject.setCgst(new BigDecimal(0.0));
		prevObject.setIgst(new BigDecimal(0.0));
		prevObject.setSgst(new BigDecimal(0.0));
		
		prevObject.setRecords(0);
		
		prevObject.setNetSupply(new BigDecimal(0.0));
		prevObject.setTaxPayble(new BigDecimal(0.0));
		prevObject.setSupplyMade(new BigDecimal(0.0));
		prevObject.setSupplyReturn(new BigDecimal(0.0));
		return prevObject;

	}

	
	private Annexure1SummarySectionDto gstr1SummaryDetails(
			Annexure1SummarySectionDto object,
			Annexure1SummarySectionDto prevObject,
			Anx1GetSectionSummaryData childEntity) {

		if (childEntity.getTotalCgst() == null) {
			childEntity.setTotalCgst(new BigDecimal(0.0));
		}
		if (childEntity.getTotalSgst() == null) {
			childEntity.setTotalSgst(new BigDecimal(0.0));
		}
		if (childEntity.getTotalIgst() == null) {
			childEntity.setTotalIgst(new BigDecimal(0.0));
		}
		if (childEntity.getTotalCess() == null) {
			childEntity.setTotalCess(new BigDecimal(0.0));
		}
		if (childEntity.getNetTax() == null) {
			childEntity.setNetTax(new BigDecimal(0.0));
		}

		// BigDecimal.
		object.setCess(childEntity.getTotalCess().add(prevObject.getCess()));
		object.setCgst(childEntity.getTotalCgst().add(prevObject.getCgst()));
		object.setIgst(childEntity.getTotalIgst().add(prevObject.getIgst()));
		object.setSgst(childEntity.getTotalSgst().add(prevObject.getSgst()));
		object.setTaxableValue(
				childEntity.getNetTax().add(prevObject.getTaxableValue()));
		object.setInvValue(
				(new BigDecimal(Integer.toString(childEntity.getTotalVal()))
						.add(prevObject.getInvValue())));
		object.setTaxPayble((childEntity.getTotalIgst())
				.add(childEntity.getTotalSgst()).add(childEntity.getTotalCgst())
				.add(childEntity.getTotalCess())
				.add(prevObject.getTaxPayble()));

		object.setRecords(childEntity.getTotalDoc() + prevObject.getRecords());

		return object;

	}
	private Annexure1SummarySectionEcomDto gstr1SummaryDetailsEcom(
			Annexure1SummarySectionEcomDto object,
			Annexure1SummarySectionEcomDto prevObject,
			Anx1GetSectionSummaryData childEntity) {

		if (childEntity.getTotalCgst() == null) {
			childEntity.setTotalCgst(new BigDecimal(0.0));
		}
		if (childEntity.getTotalSgst() == null) {
			childEntity.setTotalSgst(new BigDecimal(0.0));
		}
		if (childEntity.getTotalIgst() == null) {
			childEntity.setTotalIgst(new BigDecimal(0.0));
		}
		if (childEntity.getTotalCess() == null) {
			childEntity.setTotalCess(new BigDecimal(0.0));
		}
		if (childEntity.getNetTax() == null) {
			childEntity.setNetTax(new BigDecimal(0.0));
		}

		// BigDecimal.
		object.setCess(childEntity.getTotalCess().add(prevObject.getCess()));
		object.setCgst(childEntity.getTotalCgst().add(prevObject.getCgst()));
		object.setIgst(childEntity.getTotalIgst().add(prevObject.getIgst()));
		object.setSgst(childEntity.getTotalSgst().add(prevObject.getSgst()));
		/*object.setTaxableValue(
				childEntity.getNetTax().add(prevObject.getTaxableValue()));
		object.setInvValue(
				(new BigDecimal(Integer.toString(childEntity.getTotalVal()))
						.add(prevObject.getInvValue())));*/
		object.setNetSupply(childEntity.getNetTax().add(prevObject.getNetSupply()));
		object.setTaxPayble((childEntity.getTotalIgst())
				.add(childEntity.getTotalSgst()).add(childEntity.getTotalCgst())
				.add(childEntity.getTotalCess())
				.add(prevObject.getTaxPayble()));

		object.setSupplyMade(new BigDecimal(0.0));
		object.setSupplyReturn(new BigDecimal(0.0));
		object.setRecords(childEntity.getTotalDoc() + prevObject.getRecords());

		return object;

	}

	
	
	@Override
	public <R> Stream<R> find(SearchCriteria criteria,
			Class<? extends R> retType) {
		// TODO Auto-generated method stub
		return null;
	}

}
