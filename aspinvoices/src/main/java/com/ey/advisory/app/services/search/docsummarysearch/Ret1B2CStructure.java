package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Ret1SummaryRespDto;
import com.ey.advisory.app.util.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Service("Ret1B2CStructure")
public class Ret1B2CStructure {

	@Autowired
	@Qualifier("Anx1DefaultStructureUtil")
	private Anx1DefaultSummaryStructureUtil defaultStructureUtil;

	public List<Ret1SummaryRespDto> ret1B2CResp(List<Ret1SummaryRespDto> b2cEySummary,
			String section, String table, String supply) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Ret1SummaryRespDto> defaultB2cEYList = getDefaultB2CEYStructure(
				section, table, supply);

		List<Ret1SummaryRespDto> b2cEYList = getB2CEyList(defaultB2cEYList,
				b2cEySummary, table);

		List<Ret1SummaryRespDto> retList = new ArrayList<>();
		retList.addAll(b2cEYList);
		// String json = gson.toJson(b2bTotal);

	//	JsonElement b2cRespbody = gson.toJsonTree(retList);

		return retList;

	}

	// Replacing Default Value To Normal Value
	private List<Ret1SummaryRespDto> getDefaultB2CEYStructure(String section,
			String table, String supply) {

		List<Ret1SummaryRespDto> defaultB2CEY = new ArrayList<>();

		Ret1SummaryRespDto b2cEy3AInv = new Ret1SummaryRespDto();
		if(section != null){
		b2cEy3AInv.setSection(section);
		}
		b2cEy3AInv.setSupplyType(supply);
		b2cEy3AInv.setTable(table);
		b2cEy3AInv = defaultStructureUtil.ret1DefaultStructure(b2cEy3AInv);
		defaultB2CEY.add(b2cEy3AInv);
		return defaultB2CEY;
	}

	public List<Ret1SummaryRespDto> getB2CEyList(
			List<Ret1SummaryRespDto> b2cEYList,
			List<Ret1SummaryRespDto> eySummaryListFromView, String table) {

		List<Ret1SummaryRespDto> view3AFiltered = eySummaryListFromView.stream()
				.filter(p -> table.equalsIgnoreCase(p.getTable()))
				.collect(Collectors.toList());

		// If INV filtered list is not null
		if (view3AFiltered != null & view3AFiltered.size() > 0) {
			// then filter default List for 4A
			List<Ret1SummaryRespDto> default3AInvFiltered = b2cEYList.stream()
					.filter(p -> table.equalsIgnoreCase(p.getTable()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			default3AInvFiltered.forEach(defaultInv -> {
				// then remove it from List
				b2cEYList.remove(defaultInv);
			});

			view3AFiltered.forEach(view3A -> {
				Ret1SummaryRespDto summaryRespDto = new Ret1SummaryRespDto();

				// Ey Resp
				summaryRespDto.setTable(view3A.getTable());
				summaryRespDto.setSection(view3A.getSection());
				summaryRespDto.setSupplyType(view3A.getSupplyType());
				summaryRespDto.setAspValue(view3A.getAspValue());
				summaryRespDto.setAspIgst(view3A.getAspIgst());
				summaryRespDto.setAspSgst(view3A.getAspSgst());
				summaryRespDto.setAspCgst(view3A.getAspCgst());
				summaryRespDto.setAspCess(view3A.getAspCess());
				// User
				summaryRespDto.setUsrValue(view3A.getUsrValue());
				summaryRespDto.setUsrIgst(view3A.getUsrIgst());
				summaryRespDto.setUsrSgst(view3A.getUsrSgst());
				summaryRespDto.setUsrCgst(view3A.getUsrCgst());
				summaryRespDto.setUsrCess(view3A.getUsrCess());
				
				// GSTN Resp
				summaryRespDto.setGstnValue(view3A.getGstnValue());
				summaryRespDto.setGstnIgst(view3A.getGstnIgst());
				summaryRespDto.setGstnSgst(view3A.getGstnSgst());
				summaryRespDto.setGstnCgst(view3A.getGstnCgst());
				summaryRespDto.setGstnCess(view3A.getGstnCess());
				// Diff
				summaryRespDto.setDiffValue(view3A.getDiffValue());
				summaryRespDto.setDiffIgst(view3A.getDiffIgst());
				summaryRespDto.setDiffSgst(view3A.getDiffSgst());
				summaryRespDto.setDiffCgst(view3A.getDiffCgst());
				summaryRespDto.setDiffCess(view3A.getDiffCess());

				b2cEYList.add(summaryRespDto);
			});
		}

		Collections.sort(b2cEYList, new Comparator<Ret1SummaryRespDto>() {
			@Override
			public int compare(Ret1SummaryRespDto respDto1,
					Ret1SummaryRespDto respDto2) {
				return respDto1.getTable().compareTo(respDto2.getTable());
			}
		});

		return b2cEYList;

	}

}
