package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.RefundSummaryDto;
import com.ey.advisory.app.util.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Service("Ret1Refund8Structure")
public class Ret1Refund8Structure {

	@Autowired
	@Qualifier("Anx1DefaultStructureUtil")
	private Anx1DefaultSummaryStructureUtil defaultStructureUtil;

	public List<RefundSummaryDto> ret1Ref8Resp(List<RefundSummaryDto> listadd,
			String section, String desc, String table) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<RefundSummaryDto> defaultintEYList = getDefaultRef8EYStructure(
				section, desc, table);

		List<RefundSummaryDto> b2cEYList = getRefundList(defaultintEYList,
				listadd,table);

		List<RefundSummaryDto> retList = new ArrayList<>();
		retList.addAll(b2cEYList);
		// String json = gson.toJson(b2bTotal);

	//	JsonElement b2cRespbody = gson.toJsonTree(retList);

		return retList;

	}

	// Replacing Default Value To Normal Value
	private List<RefundSummaryDto> 
	getDefaultRef8EYStructure(String section,String desc,String table) {

		List<RefundSummaryDto> defaultB2CEY = new ArrayList<>();

		RefundSummaryDto b2cEy3AInv = new RefundSummaryDto();
		if(section != null){
		b2cEy3AInv.setSection(section);
		}
		b2cEy3AInv.setDesc(desc);
		b2cEy3AInv.setTable(table);
		
		b2cEy3AInv = defaultStructureUtil.ret1Refund8DefaultStructure(b2cEy3AInv);
		defaultB2CEY.add(b2cEy3AInv);
		return defaultB2CEY;
	}

	public List<RefundSummaryDto> getRefundList(
			List<RefundSummaryDto> b2cEYList,
			List<RefundSummaryDto> eySummaryListFromView,String table) {

		List<RefundSummaryDto> view3AFiltered = eySummaryListFromView.stream()
				.filter(p -> table.equalsIgnoreCase(p.getTable()))
				.collect(Collectors.toList());

		// If INV filtered list is not null
		if (view3AFiltered != null & view3AFiltered.size() > 0) {
			// then filter default List for 4A
			List<RefundSummaryDto> default3AInvFiltered = b2cEYList.stream()
					.filter(p -> table.equalsIgnoreCase(p.getTable()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3AInvFiltered.forEach(defaultInv -> {
				// then remove it from List
				b2cEYList.remove(defaultInv);
			});

			view3AFiltered.forEach(Summary -> {
				RefundSummaryDto summaryResp = new RefundSummaryDto();
				summaryResp.setTable(Summary.getTable());
				summaryResp.setSection(Summary.getSection());
				summaryResp.setDesc(Summary.getDesc());
				summaryResp.setUsrTax(Summary.getUsrTax());
				summaryResp.setUsrTotal(Summary.getUsrTotal());
				summaryResp.setUsrPenality(Summary.getUsrPenality());
				summaryResp.setUsrInterest(Summary.getUsrInterest());
				summaryResp.setUsrFee(Summary.getUsrFee());
				summaryResp.setUsrOther(Summary.getUsrOther());
				summaryResp.setGstnTax(Summary.getGstnTax());
				summaryResp.setGstnTotal(Summary.getGstnTotal());
				summaryResp.setGstnPenality(Summary.getGstnPenality());
				summaryResp.setGstnInterest(Summary.getGstnInterest());
				summaryResp.setGstnOther(Summary.getGstnOther());
				summaryResp.setGstnFee(Summary.getGstnFee());
				summaryResp.setDiffTax(Summary.getDiffTax());
				summaryResp.setDiffTotal(Summary.getDiffTotal());
				summaryResp.setDiffPenality(Summary.getDiffPenality());
				summaryResp.setDiffOther(Summary.getDiffOther());
				summaryResp.setDiffInterest(Summary.getDiffInterest());
				summaryResp.setDiffFee(Summary.getDiffFee());
				b2cEYList.add(summaryResp);
			});
		}

		Collections.sort(b2cEYList, new Comparator<RefundSummaryDto>() {
			@Override
			public int compare(RefundSummaryDto respDto1,
					RefundSummaryDto respDto2) {
				return respDto1.getTable().compareTo(respDto2.getTable());
			}
		});

		return b2cEYList;

	}
	
}
