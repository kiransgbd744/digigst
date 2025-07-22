package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Ret1LateFeeSummaryDto;
import com.ey.advisory.app.util.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("Ret1IntAndLateFeeStructure")
public class Ret1IntAndLateFeeStructure {

	@Autowired
	@Qualifier("Anx1DefaultStructureUtil")
	private Anx1DefaultSummaryStructureUtil defaultStructureUtil;

	
	public List<Ret1LateFeeSummaryDto> ret1int6Resp(List<Ret1LateFeeSummaryDto> 
	listadd,String section,String desc,String table) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Ret1LateFeeSummaryDto> defaultintEYList = 
				getDefaultint6EYStructure(section,desc,table);

		List<Ret1LateFeeSummaryDto> inteANdLateFeeEYList =
				getInterestAndLateFeeEyList(defaultintEYList,listadd,table);
  
		List<Ret1LateFeeSummaryDto> retList = new ArrayList<>();
		retList.addAll(inteANdLateFeeEYList);
		// String json = gson.toJson(b2bTotal);

	//	JsonElement b2cRespbody = gson.toJsonTree(retList);

		return retList;

	}

	// Replacing Default Value To Normal Value
	private List<Ret1LateFeeSummaryDto> 
	getDefaultint6EYStructure(String section,String desc,String table) {

		List<Ret1LateFeeSummaryDto> defaultInteAnLateFeeEY = new ArrayList<>();

		Ret1LateFeeSummaryDto inteAndLateFeeEyInv = new Ret1LateFeeSummaryDto();
		inteAndLateFeeEyInv.setSection(section);
		inteAndLateFeeEyInv.setDesc(desc);
		inteAndLateFeeEyInv.setTable(table);
		
		inteAndLateFeeEyInv = 
		defaultStructureUtil.ret1Int6DefaultStructure(inteAndLateFeeEyInv);
		defaultInteAnLateFeeEY.add(inteAndLateFeeEyInv);
		return defaultInteAnLateFeeEY;
	}

	public List<Ret1LateFeeSummaryDto> getInterestAndLateFeeEyList(
			List<Ret1LateFeeSummaryDto> interestAndLatefeeEYList,
			List<Ret1LateFeeSummaryDto> eySummaryListFromView,String table) {

		List<Ret1LateFeeSummaryDto> view3AFiltered = 
				eySummaryListFromView.stream()
				.filter(p -> table.equalsIgnoreCase(p.getTable()))
				.collect(Collectors.toList());

		// If INV filtered list is not null
		if (view3AFiltered != null & view3AFiltered.size() > 0) {
			// then filter default List for 4A
			List<Ret1LateFeeSummaryDto> default3AInvFiltered = 
					interestAndLatefeeEYList.stream()
					.filter(p -> table.equalsIgnoreCase(p.getTable()))
					.collect(Collectors.toList());

			// If the default filtered list is not null 
			default3AInvFiltered.forEach(defaultInv -> {
				// then remove it from List
				interestAndLatefeeEYList.remove(defaultInv);
			});

			view3AFiltered.forEach(view3A -> {
				Ret1LateFeeSummaryDto summaryRespDto = new Ret1LateFeeSummaryDto();
				summaryRespDto.setSection(view3A.getSection());
				summaryRespDto.setDesc(view3A.getDesc());
				summaryRespDto.setTable(view3A.getTable());
				summaryRespDto.setUsrIgst(view3A.getUsrIgst());
				summaryRespDto.setUsrCgst(view3A.getUsrCgst());
				summaryRespDto.setUsrSgst(view3A.getUsrSgst());
				summaryRespDto.setUsrCess(view3A.getUsrCess());
				summaryRespDto.setUsrLateFeeCgst(view3A.getUsrLateFeeCgst());
				summaryRespDto.setUsrLateFeeSgst(view3A.getUsrLateFeeSgst());
				summaryRespDto.setGstnIgst(view3A.getGstnIgst());
				summaryRespDto.setGstnCgst(view3A.getGstnCgst());
				summaryRespDto.setGstnSgst(view3A.getGstnSgst());
				summaryRespDto.setGstnCess(view3A.getGstnCess());
				summaryRespDto.setGstnLateFeeCgst(view3A.getGstnLateFeeCgst());
				summaryRespDto.setGstnLateFeeSgst(view3A.getGstnLateFeeSgst());
				summaryRespDto.setDiffIgst(view3A.getDiffIgst());
				summaryRespDto.setDiffCgst(view3A.getDiffCgst());
				summaryRespDto.setDiffSgst(view3A.getDiffSgst());
				summaryRespDto.setDiffCess(view3A.getDiffCess());
				summaryRespDto.setDiffLateFeeCgst(view3A.getDiffLateFeeCgst());
				summaryRespDto.setDiffLateFeeSgst(view3A.getDiffLateFeeSgst());
				interestAndLatefeeEYList.add(summaryRespDto);
			});
		}

		Collections.sort(interestAndLatefeeEYList, new Comparator<Ret1LateFeeSummaryDto>() {
			@Override
			public int compare(Ret1LateFeeSummaryDto respDto1,
					Ret1LateFeeSummaryDto respDto2) {
				return respDto1.getTable().compareTo(respDto2.getTable());
			}
		});

		return interestAndLatefeeEYList;

	}

}
