package com.ey.advisory.app.dashboard.apiCall;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.IrnStatusMaster;
import com.ey.advisory.core.api.GSTNAPIUtil;
import com.ey.advisory.core.dto.GetCallDto;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.core.dto.InitiateGetCallDto;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.google.common.base.Strings;

public class Test {

	public static void main(String[] args) {
		
		
		Gson gson1 = GsonUtil.gsonInstanceWithExpose();
		String a = "{ \"req\":[{\"vbeln\":\"0090000000\",\"fkart\":\"F2\",\"fktyp\":\"L\",\"vbtyp\":\"M\",\"waerk\":\"INR\",\"vkorg\":\"GS01\",\"vtweg\":\"30\",\"kalsm\":\"ZDGST1\",\"knumv\":\"0000003174\",\"vsbed\":\"01\"},{\"vbeln\":\"0090000001\",\"fkart\":\"F2\",\"fktyp\":\"L\",\"vbtyp\":\"M\",\"waerk\":\"INR\",\"vkorg\":\"GS01\",\"vtweg\":\"30\",\"kalsm\":\"ZDGST1\",\"knumv\":\"0000003177\",\"vsbed\":\"01\"},{\"vbeln\":\"0090000002\",\"fkart\":\"G2\",\"fktyp\":\"A\",\"vbtyp\":\"O\",\"waerk\":\"INR\",\"vkorg\":\"GS01\",\"vtweg\":\"30\",\"kalsm\":\"ZDGST1\",\"knumv\":\"0000003179\",\"vsbed\":\"01\"},{\"vbeln\":\"0090000003\",\"fkart\":\"L2\",\"fktyp\":\"A\",\"vbtyp\":\"P\",\"waerk\":\"INR\",\"vkorg\":\"GS01\",\"vtweg\":\"30\",\"kalsm\":\"ZDGST1\",\"knumv\":\"0000003181\",\"vsbed\":\"01\"},{\"vbeln\":\"0090000004\",\"fkart\":\"F2\",\"fktyp\":\"L\",\"vbtyp\":\"M\",\"waerk\":\"INR\",\"vkorg\":\"GS01\",\"vtweg\":\"30\",\"kalsm\":\"ZDGST1\",\"knumv\":\"0000003183\",\"vsbed\":\"01\"},{\"vbeln\":\"0090000005\",\"fkart\":\"RE\",\"fktyp\":\"L\",\"vbtyp\":\"O\",\"waerk\":\"INR\",\"vkorg\":\"GS01\",\"vtweg\":\"30\",\"kalsm\":\"ZDGST1\",\"knumv\":\"0000003185\",\"vsbed\":\"01\"},{\"vbeln\":\"0090000006\",\"fkart\":\"F2\",\"fktyp\":\"L\",\"vbtyp\":\"M\",\"waerk\":\"INR\",\"vkorg\":\"GS01\",\"vtweg\":\"30\",\"kalsm\":\"ZDGST1\",\"knumv\":\"0000003188\",\"vsbed\":\"01\"},{\"vbeln\":\"0090000007\",\"fkart\":\"F2\",\"fktyp\":\"L\",\"vbtyp\":\"M\",\"waerk\":\"INR\",\"vkorg\":\"GS01\",\"vtweg\":\"30\",\"kalsm\":\"ZDGST1\",\"knumv\":\"0000003190\",\"vsbed\":\"01\"},{\"vbeln\":\"0090000008\",\"fkart\":\"F2\",\"fktyp\":\"L\",\"vbtyp\":\"M\",\"waerk\":\"INR\",\"vkorg\":\"GS01\",\"vtweg\":\"30\",\"kalsm\":\"ZDGST1\",\"knumv\":\"0000003195\",\"vsbed\":\"01\"},{\"vbeln\":\"0090000009\",\"fkart\":\"F2\",\"fktyp\":\"L\",\"vbtyp\":\"M\",\"waerk\":\"INR\",\"vkorg\":\"GS01\",\"vtweg\":\"30\",\"kalsm\":\"ZDGST1\",\"knumv\":\"0000003197\",\"vsbed\":\"01\"},{\"vbeln\":\"0090000010\",\"fkart\":\"FAZ\",\"fktyp\":\"P\",\"vbtyp\":\"M\",\"waerk\":\"INR\",\"vkorg\":\"GS01\",\"vtweg\":\"30\",\"kalsm\":\"ZDGST1\",\"knumv\":\"0000003199\",\"vsbed\":\"01\"},{\"vbeln\":\"0090000011\",\"fkart\":\"FAS\",\"fktyp\":\"P\",\"vbtyp\":\"N\",\"waerk\":\"INR\",\"vkorg\":\"GS01\",\"vtweg\":\"30\",\"kalsm\":\"ZDGST1\",\"knumv\":\"0000003200\",\"vsbed\":\"01\"},{\"vbeln\":\"0090000012\",\"fkart\":\"FAZ\",\"fktyp\":\"P\",\"vbtyp\":\"M\",\"waerk\":\"INR\",\"vkorg\":\"GS01\",\"vtweg\":\"30\",\"kalsm\":\"ZDGST1\",\"knumv\":\"0000003201\",\"vsbed\":\"01\"},{\"vbeln\":\"0090000013\",\"fkart\":\"F2\",\"fktyp\":\"D\",\"vbtyp\":\"M\",\"waerk\":\"INR\",\"vkorg\":\"GS01\",\"vtweg\":\"30\",\"kalsm\":\"ZDGST1\",\"knumv\":\"0000003202\",\"vsbed\":\"01\"},{\"vbeln\":\"0090000014\",\"fkart\":\"F2\",\"fktyp\":\"D\",\"vbtyp\":\"M\",\"waerk\":\"INR\",\"vkorg\":\"GS01\",\"vtweg\":\"30\",\"kalsm\":\"ZDGST1\",\"knumv\":\"0000003203\",\"vsbed\":\"01\"},{\"vbeln\":\"0090000015\",\"fkart\":\"S1\",\"fktyp\":\"D\",\"vbtyp\":\"N\",\"waerk\":\"INR\",\"vkorg\":\"GS01\",\"vtweg\":\"30\",\"kalsm\":\"ZDGST1\",\"knumv\":\"0000003233\",\"vsbed\":\"01\"},{\"vbeln\":\"0090000016\",\"fkart\":\"F2\",\"fktyp\":\"D\",\"vbtyp\":\"M\",\"waerk\":\"INR\",\"vkorg\":\"GS01\",\"vtweg\":\"30\",\"kalsm\":\"ZDGST1\",\"knumv\":\"0000003234\",\"vsbed\":\"01\"},{\"vbeln\":\"0090000017\",\"fkart\":\"S1\",\"fktyp\":\"D\",\"vbtyp\":\"N\",\"waerk\":\"INR\",\"vkorg\":\"GS01\",\"vtweg\":\"30\",\"kalsm\":\"ZDGST1\",\"knumv\":\"0000003235\",\"vsbed\":\"01\"}]}";
		//a = a.replace("\\", "");
		System.out.println(a);
		
		JsonObject resp = new JsonObject();
		JsonObject response = (new JsonParser().parse(a))
				.getAsJsonObject();
		JsonElement respBody = gson1.toJsonTree(response);
		System.out.println(respBody);
		
		
		Integer NA = 1;
		String irn = "irn";
		String irnResp = null;
		
		String irnNo = (IrnStatusMaster.NOT_APPLICABLE.getIrnStatusMaster() == NA) ? "N/A" :
			(Strings.isNullOrEmpty(irnResp) ? irn : irnResp);
		
		System.out.println(irnNo);
		
		
		Triplet<String, String, String> obj = null;
				//new Triplet<String, String, String>();
		
		System.out.println(obj.getValue0()+obj.getValue1()+obj.getValue2());
		
		String jobStatus = "ZIP_COMPLETED";
		String startMonth = "04";
		String endMonth = "03";
		String appendMonthYear = null;
		String appendMonthYear1 = null;
		String financialPeriod = "2020-21";
		if (financialPeriod != null && !financialPeriod.isEmpty()) {
			String[] arrOfStr = financialPeriod.split("-", 2);
			appendMonthYear = arrOfStr[0] + startMonth;
			appendMonthYear1 = "20" + arrOfStr[1] + endMonth;
		}
		int derivedStartPeriod = Integer.parseInt(appendMonthYear);

		int derivedEndPeriod = Integer.parseInt(appendMonthYear1);
		
		System.out.println(derivedStartPeriod +"-"+derivedEndPeriod);
		
		//System.out.println(GenUtil.extractTaxPeriodsFromFY("2017-18"));
		
		
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		String reqJson = "{\"req\":[{\"gstin\":\"33GSPTN0481G1ZA\",\"taxPeriod\":[\"042019\",\"052019\",\"062019\",\"072019\",\"082019\",\"092019\",\"102019\",\"112019\",\"122019\",\"012020\",\"022020\",\"032020\"]}],\"returnType\":\"GSTR1\"}";
		
		GetCallDto dto = gson
		.fromJson(reqJson, GetCallDto.class);
		
		List<InitiateGetCallDto> gstinTaxPeiordList = dto.getGstinTaxPeiordList();
		String returnType = dto.getReturnType();
		
				
		System.out.println(returnType + gstinTaxPeiordList);
		List<List<Pair<String, String>>> gstinReturnPeriodList = gstinTaxPeiordList
				.stream().map(o -> GstinPair(o)).collect(Collectors.toList());
		
		List<Pair<String, String>> list = gstinReturnPeriodList
				.stream().collect(ArrayList::new, List::addAll, List::addAll);

		
		System.out.println(list);
		
		for (int i = 0; i < list.size(); i++) {
			
			String gstin = list.get(i).getValue0();
			String taxPeriod = list.get(i).getValue1();

				List<Gstr1GetInvoicesReqDto> dtos = new ArrayList<>();
				Gstr1GetInvoicesReqDto reqDto = new Gstr1GetInvoicesReqDto();
				
				reqDto.setGstin(gstin);
				reqDto.setReturnPeriod(taxPeriod);
				reqDto.setIsFailed(false);

				//GSTR1 get call
			
			}

	}
	
	private static List<Pair<String, String>> GstinPair(InitiateGetCallDto o) {
		
		return o.getTaxPeriodList().stream().map(x -> 
		new Pair<String, String>(o.getGstin(), x)).collect(Collectors.toList());
	}

}
