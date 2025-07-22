package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryResp1Dto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionDto;
import com.ey.advisory.app.util.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Service("anx1ImpsStructure")
public class Anx1ImpsStructure {

	@Autowired
	@Qualifier("Anx1DefaultStructureUtil")
	private Anx1DefaultSummaryStructureUtil defaultStructureUtil;

	@Autowired
	@Qualifier("anx1ImpsEYFinalStructure")
	private Anx1ImpsEYFinalStructure anx1ImpsEYFinalStructure;

	@Autowired
	@Qualifier("summaryTotalCalculation")
	private SummaryTotalCalculation summaryTotalCalculation;
	
	@Autowired
	@Qualifier("Anx1GstnCalculation")
	private Anx1GstnCalculation gstnCalculation;


	public JsonElement anx1ImpsResp(
			List<Annexure1SummaryResp1Dto> impsEySummary) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Annexure1SummaryResp1Dto> defaultImpsIEYList = 
				getDefaultImpsITotalEYStructure();

		List<Annexure1SummaryResp1Dto> impsIEYList = anx1ImpsEYFinalStructure
				.getImpsEyList(defaultImpsIEYList, impsEySummary);

		/*Annexure1SummaryResp1Dto total = impsEySummary.stream()
				.filter(x -> "total".equals(x.getDocType())).findAny()
				.orElse(null);
*/
		
		
	/*	Annexure1SummaryResp1Dto slf = impsIEYList.stream()
				.filter(x -> "SLF".equals(x.getDocType())).findAny()
				.orElse(null);
		Annexure1SummaryResp1Dto cr = impsIEYList.stream()
				.filter(x -> "CR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto dr = impsIEYList.stream()
				.filter(x -> "DR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummarySectionDto gstnImpsResult = gstnCalculation
				.addGstnImpsData(gstnResult);
		Annexure1SummaryResp1Dto imps3ITotal = summaryTotalCalculation
				.totalThreeDocCalculation(slf, dr, cr,gstnImpsResult);
*/
		List<Annexure1SummaryResp1Dto> retList = new ArrayList<>();
	//	retList.add(total);
		retList.addAll(impsIEYList);

		JsonElement impsRespbody = gson.toJsonTree(retList);

		return impsRespbody;

	}

	/*
	 * Calculating Summary Total Section Wise Summary
	 */
	public Annexure1SummaryResp1Dto anx1SumImpsResp(
			List<Annexure1SummaryResp1Dto> impsEySummary,
			List<? extends Annexure1SummaryDto> gstnResult) {

		Gson gson = GsonUtil.newSAPGsonInstance();

		List<Annexure1SummaryResp1Dto> defaultImpsIEYList = 
				getDefaultImpsIEYStructure();

		List<Annexure1SummaryResp1Dto> impsIEYList = anx1ImpsEYFinalStructure
				.getImpsEyList(defaultImpsIEYList, impsEySummary);

		Annexure1SummaryResp1Dto slf = impsIEYList.stream()
				.filter(x -> "SLF".equals(x.getDocType())).findAny()
				.orElse(null);
		Annexure1SummaryResp1Dto cr = impsIEYList.stream()
				.filter(x -> "CR".equals(x.getDocType())).findAny()
				.orElse(null);

		Annexure1SummaryResp1Dto dr = impsIEYList.stream()
				.filter(x -> "DR".equals(x.getDocType())).findAny()
				.orElse(null);

		// Gstn Data From Get Summary API
		Annexure1SummarySectionDto gstnImpsResult = gstnCalculation
				.addGstnImpsData(gstnResult);
		Annexure1SummaryResp1Dto imps3ITotal = summaryTotalCalculation
				.totalThreeDocCalculation(slf, dr, cr,gstnImpsResult);

		return imps3ITotal;

	}

	private List<Annexure1SummaryResp1Dto> getDefaultImpsIEYStructure() {

		List<Annexure1SummaryResp1Dto> defaultImpsEY = new ArrayList<>();

		Annexure1SummaryResp1Dto impsIEy3ISLF = new Annexure1SummaryResp1Dto();
		impsIEy3ISLF.setTableSection("3I");
		impsIEy3ISLF.setDocType("SLF");
		impsIEy3ISLF.setIndex(1);
		impsIEy3ISLF = defaultStructureUtil.anx1DefaultStructure(impsIEy3ISLF);

		Annexure1SummaryResp1Dto revEy3ICr = new Annexure1SummaryResp1Dto();
		revEy3ICr.setTableSection("3I");
		revEy3ICr.setDocType("CR");
		revEy3ICr.setIndex(3);
		revEy3ICr = defaultStructureUtil.anx1DefaultStructure(revEy3ICr);

		Annexure1SummaryResp1Dto revEy3IDr = new Annexure1SummaryResp1Dto();
		revEy3IDr.setTableSection("3I");
		revEy3IDr.setDocType("DR");
		revEy3IDr.setIndex(2);
		revEy3IDr = defaultStructureUtil.anx1DefaultStructure(revEy3IDr);

		defaultImpsEY.add(impsIEy3ISLF);
		defaultImpsEY.add(revEy3ICr);
		defaultImpsEY.add(revEy3IDr);

		return defaultImpsEY;
	}
	
	private List<Annexure1SummaryResp1Dto> getDefaultImpsITotalEYStructure() {

		List<Annexure1SummaryResp1Dto> defaultImpsEY = new ArrayList<>();

		Annexure1SummaryResp1Dto impsIEy3ITotal = new Annexure1SummaryResp1Dto();
		impsIEy3ITotal.setTableSection("3I");
		impsIEy3ITotal.setDocType("total");
		impsIEy3ITotal.setIndex(0);
		impsIEy3ITotal = defaultStructureUtil.anx1DefaultStructure(impsIEy3ITotal);

		
		Annexure1SummaryResp1Dto impsIEy3ISLF = new Annexure1SummaryResp1Dto();
		impsIEy3ISLF.setTableSection("3I");
		impsIEy3ISLF.setDocType("SLF");
		impsIEy3ISLF.setIndex(1);
		impsIEy3ISLF = defaultStructureUtil.anx1DefaultStructure(impsIEy3ISLF);

		Annexure1SummaryResp1Dto revEy3ICr = new Annexure1SummaryResp1Dto();
		revEy3ICr.setTableSection("3I");
		revEy3ICr.setDocType("CR");
		revEy3ICr.setIndex(3);
		revEy3ICr = defaultStructureUtil.anx1DefaultStructure(revEy3ICr);

		Annexure1SummaryResp1Dto revEy3IDr = new Annexure1SummaryResp1Dto();
		revEy3IDr.setTableSection("3I");
		revEy3IDr.setDocType("DR");
		revEy3IDr.setIndex(2);
		revEy3IDr = defaultStructureUtil.anx1DefaultStructure(revEy3IDr);

		defaultImpsEY.add(impsIEy3ITotal);
		defaultImpsEY.add(impsIEy3ISLF);
		defaultImpsEY.add(revEy3ICr);
		defaultImpsEY.add(revEy3IDr);

		return defaultImpsEY;
	}

}
