/**
 * 
 */
package com.ey.advisory.app.services.jobs.erp;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Gstr1SummaryDocIssuedEntity;
import com.ey.advisory.app.data.entities.client.Gstr1SummaryNilEntity;
import com.ey.advisory.app.data.entities.client.Gstr1SummaryRateEntity;
import com.ey.advisory.app.docs.dto.Gstr1SummaryCDSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryDocSectionDto;
import com.ey.advisory.app.docs.dto.Gstr1SummaryNilSectionDto;
import com.ey.advisory.app.docs.dto.erp.Gstr1ErpReviewSummaryDto;
import com.ey.advisory.app.services.jobs.gstr1.Gstr1SummaryDataAtGstn;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author Umesh
 *
 */
@Service("GstnSummaryErpSectionService")
public class GstnSummaryErpSectionService {

	@Autowired
	@Qualifier("gstr1SummaryDataAtGstnImpl")
	Gstr1SummaryDataAtGstn gstr1SummaryDataAtGstn;

	public String gstnApi(Gstr1GetInvoicesReqDto dto, String groupCode) {
		String apiResp = null;
		try {
			APIResponse respp = gstr1SummaryDataAtGstn
					.findSummaryDataAtGstn(dto, groupCode);
			apiResp = respp != null ? respp.getResponse() : null;
		} catch (java.lang.UnsupportedOperationException e) {
			return apiResp;
		} catch (Exception e) {
			return apiResp;
		}
		return apiResp;
	}

	public Gstr1ErpReviewSummaryDto findSummaryGstinSaveApi(
			Gstr1GetInvoicesReqDto reqDto) {
		Gstr1ErpReviewSummaryDto revSumto = new Gstr1ErpReviewSummaryDto();
		String groupCode = TenantContext.getTenantId();
		Gson gson = GsonUtil.newSAPGsonInstance();
		String apiResp = gstnApi(reqDto, groupCode);
		if (apiResp != null) {
			JsonObject respObject = (new JsonParser()).parse(apiResp)
					.getAsJsonObject();
			JsonArray array = respObject.get("sec_sum").getAsJsonArray();
			for (JsonElement arr : array) {
				String secName = arr.getAsJsonObject().get("sec_nm")
						.getAsString();

				if (APIConstants.DOC_ISSUE.equalsIgnoreCase(secName)) {
					Gstr1SummaryDocIssuedEntity childEntity = gson.fromJson(
							arr.getAsJsonObject(),
							Gstr1SummaryDocIssuedEntity.class);

					Gstr1SummaryDocSectionDto gstr1SummaryDoc = new Gstr1SummaryDocSectionDto();
					gstr1SummaryDoc.setTotal(childEntity.getTtlRec());
					gstr1SummaryDoc.setNetIssued(childEntity.getNetDocIssued());
					gstr1SummaryDoc
							.setDocCancelled(childEntity.getTtlDocCancelled());
					revSumto.setGstr1SummaryDoc(gstr1SummaryDoc);

				}
				if (APIConstants.NIL.equalsIgnoreCase(secName)) {

					Gstr1SummaryNilEntity childEntity = gson.fromJson(
							arr.getAsJsonObject(), Gstr1SummaryNilEntity.class);
					Gstr1SummaryNilSectionDto gstr1SummaryNil = new Gstr1SummaryNilSectionDto();
					gstr1SummaryNil.setAspExempted(childEntity.getTtlExptAmt());
					gstr1SummaryNil
							.setAspNitRated(childEntity.getTtlNilsupAmt());
					gstr1SummaryNil.setAspNonGst(childEntity.getTtlNgsupAmt());
					revSumto.setGstr1SummaryNil(gstr1SummaryNil);
				}
				if (APIConstants.HSN.equalsIgnoreCase(secName)) {
					Gstr1SummaryRateEntity childEntity = gson.fromJson(
							arr.getAsJsonObject(),
							Gstr1SummaryRateEntity.class);
					Gstr1SummaryCDSectionDto cdHsnSecDto = new Gstr1SummaryCDSectionDto();
					cdHsnSecDto.setRecords(childEntity.getTtlRec());
					cdHsnSecDto.setIgst(childEntity.getTtlIgst() != null
							? childEntity.getTtlIgst() : BigDecimal.ZERO);
					cdHsnSecDto.setCgst(childEntity.getTtlCgst() != null
							? childEntity.getTtlCgst() : BigDecimal.ZERO);
					cdHsnSecDto.setSgst(childEntity.getTtlSgst() != null
							? childEntity.getTtlSgst() : BigDecimal.ZERO);
					cdHsnSecDto.setCess(childEntity.getTtlCess() != null
							? childEntity.getTtlCess() : BigDecimal.ZERO);
					cdHsnSecDto.setInvValue(childEntity.getTtlVal() != null
							? childEntity.getTtlVal() : BigDecimal.ZERO);
					cdHsnSecDto.setTaxableValue(childEntity.getTtlTax() != null
							? childEntity.getTtlTax() : BigDecimal.ZERO);
					cdHsnSecDto.setTaxPayable(cdHsnSecDto.getIgst()
							.add(cdHsnSecDto.getCgst()
									.add(cdHsnSecDto.getSgst())
									.add(cdHsnSecDto.getCess())));
					revSumto.setGstr1SummaryHSN(cdHsnSecDto);
				}
				if (APIConstants.AT.equalsIgnoreCase(secName)) {
					Gstr1SummaryRateEntity childEntity = gson.fromJson(
							arr.getAsJsonObject(),
							Gstr1SummaryRateEntity.class);
					Gstr1SummaryCDSectionDto cdAdvRecSecDto = new Gstr1SummaryCDSectionDto();
					cdAdvRecSecDto.setRecords(childEntity.getTtlRec());
					cdAdvRecSecDto.setIgst(childEntity.getTtlIgst() != null
							? childEntity.getTtlIgst() : BigDecimal.ZERO);
					cdAdvRecSecDto.setCgst(childEntity.getTtlCgst() != null
							? childEntity.getTtlCgst() : BigDecimal.ZERO);
					cdAdvRecSecDto.setSgst(childEntity.getTtlSgst() != null
							? childEntity.getTtlSgst() : BigDecimal.ZERO);
					cdAdvRecSecDto.setCess(childEntity.getTtlCess() != null
							? childEntity.getTtlCess() : BigDecimal.ZERO);
					cdAdvRecSecDto.setInvValue(childEntity.getTtlVal() != null
							? childEntity.getTtlVal() : BigDecimal.ZERO);
					cdAdvRecSecDto
							.setTaxableValue(childEntity.getTtlTax() != null
									? childEntity.getTtlTax()
									: BigDecimal.ZERO);
					cdAdvRecSecDto.setTaxPayable(cdAdvRecSecDto.getIgst()
							.add(cdAdvRecSecDto.getCgst()
									.add(cdAdvRecSecDto.getSgst())
									.add(cdAdvRecSecDto.getCess())));
					revSumto.setGstr1SummaryAdvRec(cdAdvRecSecDto);
				}
				if (APIConstants.ATA.equalsIgnoreCase(secName)) {
					Gstr1SummaryRateEntity childEntity = gson.fromJson(
							arr.getAsJsonObject(),
							Gstr1SummaryRateEntity.class);
					Gstr1SummaryCDSectionDto cdAdvRecAmeSecDto = new Gstr1SummaryCDSectionDto();
					cdAdvRecAmeSecDto.setRecords(childEntity.getTtlRec());
					cdAdvRecAmeSecDto.setIgst(childEntity.getTtlIgst() != null
							? childEntity.getTtlIgst() : BigDecimal.ZERO);
					cdAdvRecAmeSecDto.setCgst(childEntity.getTtlCgst() != null
							? childEntity.getTtlCgst() : BigDecimal.ZERO);
					cdAdvRecAmeSecDto.setSgst(childEntity.getTtlSgst() != null
							? childEntity.getTtlSgst() : BigDecimal.ZERO);
					cdAdvRecAmeSecDto.setCess(childEntity.getTtlCess() != null
							? childEntity.getTtlCess() : BigDecimal.ZERO);
					cdAdvRecAmeSecDto
							.setInvValue(childEntity.getTtlVal() != null
									? childEntity.getTtlVal()
									: BigDecimal.ZERO);
					cdAdvRecAmeSecDto
							.setTaxableValue(childEntity.getTtlTax() != null
									? childEntity.getTtlTax()
									: BigDecimal.ZERO);
					cdAdvRecAmeSecDto.setTaxPayable(cdAdvRecAmeSecDto.getIgst()
							.add(cdAdvRecAmeSecDto.getCgst()
									.add(cdAdvRecAmeSecDto.getSgst())
									.add(cdAdvRecAmeSecDto.getCess())));
					revSumto.setGstr1SummaryAdvRecAme(cdAdvRecAmeSecDto);
				}

				if (APIConstants.TXPD.equalsIgnoreCase(secName)) {
					Gstr1SummaryRateEntity childEntity = gson.fromJson(
							arr.getAsJsonObject(),
							Gstr1SummaryRateEntity.class);
					Gstr1SummaryCDSectionDto cdAdvAdjSecDto = new Gstr1SummaryCDSectionDto();
					cdAdvAdjSecDto.setRecords(childEntity.getTtlRec());
					cdAdvAdjSecDto.setIgst(childEntity.getTtlIgst() != null
							? childEntity.getTtlIgst() : BigDecimal.ZERO);
					cdAdvAdjSecDto.setCgst(childEntity.getTtlCgst() != null
							? childEntity.getTtlCgst() : BigDecimal.ZERO);
					cdAdvAdjSecDto.setSgst(childEntity.getTtlSgst() != null
							? childEntity.getTtlSgst() : BigDecimal.ZERO);
					cdAdvAdjSecDto.setCess(childEntity.getTtlCess() != null
							? childEntity.getTtlCess() : BigDecimal.ZERO);
					cdAdvAdjSecDto.setInvValue(childEntity.getTtlVal() != null
							? childEntity.getTtlVal() : BigDecimal.ZERO);
					cdAdvAdjSecDto
							.setTaxableValue(childEntity.getTtlTax() != null
									? childEntity.getTtlTax()
									: BigDecimal.ZERO);
					cdAdvAdjSecDto.setTaxPayable(cdAdvAdjSecDto.getIgst()
							.add(cdAdvAdjSecDto.getCgst()
									.add(cdAdvAdjSecDto.getSgst())
									.add(cdAdvAdjSecDto.getCess())));
					revSumto.setGstr1SummaryAdvAdj(cdAdvAdjSecDto);
				}
				if (APIConstants.TXPDA.equalsIgnoreCase(secName)) {
					Gstr1SummaryRateEntity childEntity = gson.fromJson(
							arr.getAsJsonObject(),
							Gstr1SummaryRateEntity.class);
					Gstr1SummaryCDSectionDto cdAdvAdjAmeSecDto = new Gstr1SummaryCDSectionDto();
					cdAdvAdjAmeSecDto.setRecords(childEntity.getTtlRec());
					cdAdvAdjAmeSecDto.setIgst(childEntity.getTtlIgst() != null
							? childEntity.getTtlIgst() : BigDecimal.ZERO);
					cdAdvAdjAmeSecDto.setCgst(childEntity.getTtlCgst() != null
							? childEntity.getTtlCgst() : BigDecimal.ZERO);
					cdAdvAdjAmeSecDto.setSgst(childEntity.getTtlSgst() != null
							? childEntity.getTtlSgst() : BigDecimal.ZERO);
					cdAdvAdjAmeSecDto.setCess(childEntity.getTtlCess() != null
							? childEntity.getTtlCess() : BigDecimal.ZERO);
					cdAdvAdjAmeSecDto
							.setInvValue(childEntity.getTtlVal() != null
									? childEntity.getTtlVal()
									: BigDecimal.ZERO);
					cdAdvAdjAmeSecDto
							.setTaxableValue(childEntity.getTtlTax() != null
									? childEntity.getTtlTax()
									: BigDecimal.ZERO);
					cdAdvAdjAmeSecDto.setTaxPayable(cdAdvAdjAmeSecDto.getIgst()
							.add(cdAdvAdjAmeSecDto.getCgst()
									.add(cdAdvAdjAmeSecDto.getSgst())
									.add(cdAdvAdjAmeSecDto.getCess())));
					revSumto.setGstr1SummaryAdvAdjAme(cdAdvAdjAmeSecDto);
				}

				Map<String, Gstr1SummaryCDSectionDto> gstr1SummaryOutward = new LinkedHashMap<>();
				Gstr1SummaryRateEntity childEntity = gson.fromJson(
						arr.getAsJsonObject(), Gstr1SummaryRateEntity.class);
				if (APIConstants.B2B.equalsIgnoreCase(secName)) {
					reviewOutwardSection(reqDto, gstr1SummaryOutward,
							childEntity, APIConstants.B2B);
				}
				if (APIConstants.B2BA.equalsIgnoreCase(secName)) {
					reviewOutwardSection(reqDto, gstr1SummaryOutward,
							childEntity, APIConstants.B2BA);
				}
				if (APIConstants.B2CS.equalsIgnoreCase(secName)) {
					reviewOutwardSection(reqDto, gstr1SummaryOutward,
							childEntity, APIConstants.B2CS);
				}
				if (APIConstants.B2CSA.equalsIgnoreCase(secName)) {
					reviewOutwardSection(reqDto, gstr1SummaryOutward,
							childEntity, APIConstants.B2CSA);
				}
				if (APIConstants.B2CL.equalsIgnoreCase(secName)) {
					reviewOutwardSection(reqDto, gstr1SummaryOutward,
							childEntity, APIConstants.B2CL);
				}
				if (APIConstants.B2CLA.equalsIgnoreCase(secName)) {
					reviewOutwardSection(reqDto, gstr1SummaryOutward,
							childEntity, APIConstants.B2CLA);
				}
				if (APIConstants.CDNR.equalsIgnoreCase(secName)) {
					reviewOutwardSection(reqDto, gstr1SummaryOutward,
							childEntity, APIConstants.CDNR);
				}
				
				if (APIConstants.CDNRA.equalsIgnoreCase(secName)) {
					reviewOutwardSection(reqDto, gstr1SummaryOutward,
							childEntity, APIConstants.CDNRA);
				}
				
				if (APIConstants.CDNUR.equalsIgnoreCase(secName)) {
					reviewOutwardSection(reqDto, gstr1SummaryOutward,
							childEntity, APIConstants.CDNUR);
				}
				if (APIConstants.CDNURA.equalsIgnoreCase(secName)) {
					reviewOutwardSection(reqDto, gstr1SummaryOutward,
							childEntity, APIConstants.CDNURA);
				}
				
				if (APIConstants.EXP.equalsIgnoreCase(secName)) {
					reviewOutwardSection(reqDto, gstr1SummaryOutward,
							childEntity, "Exports");
				}
				if (APIConstants.EXPA.equalsIgnoreCase(secName)) {
					reviewOutwardSection(reqDto, gstr1SummaryOutward,
							childEntity, "Exports-A");
				}
				
			}
		}
		return revSumto;
	}

	private void reviewOutwardSection(Gstr1GetInvoicesReqDto reqDto,
			Map<String, Gstr1SummaryCDSectionDto> gstr1SummaryOutward,
			Gstr1SummaryRateEntity childEntity, String type) {
		StringBuilder key = new StringBuilder();
		Gstr1SummaryCDSectionDto gstr1SummaryB2BSecDto = new Gstr1SummaryCDSectionDto();
		key.append(reqDto.getGstin());
		key.append("_");
		key.append(reqDto.getReturnPeriod());
		key.append("_");
		key.append(type);
		String docKey = key.toString();
		gstr1SummaryB2BSecDto.setRecords(childEntity.getTtlRec());
		gstr1SummaryB2BSecDto.setIgst(childEntity.getTtlIgst() != null
				? childEntity.getTtlIgst() : BigDecimal.ZERO);
		gstr1SummaryB2BSecDto.setCgst(childEntity.getTtlCgst() != null
				? childEntity.getTtlCgst() : BigDecimal.ZERO);
		gstr1SummaryB2BSecDto.setSgst(childEntity.getTtlSgst() != null
				? childEntity.getTtlSgst() : BigDecimal.ZERO);
		gstr1SummaryB2BSecDto.setCess(childEntity.getTtlCess() != null
				? childEntity.getTtlCess() : BigDecimal.ZERO);
		gstr1SummaryB2BSecDto.setInvValue(childEntity.getTtlVal() != null
				? childEntity.getTtlVal() : BigDecimal.ZERO);
		gstr1SummaryB2BSecDto.setTaxableValue(childEntity.getTtlTax() != null
				? childEntity.getTtlTax() : BigDecimal.ZERO);
		gstr1SummaryB2BSecDto.setTaxPayable(gstr1SummaryB2BSecDto.getIgst()
				.add(gstr1SummaryB2BSecDto.getCgst()
						.add(gstr1SummaryB2BSecDto.getSgst())
						.add(gstr1SummaryB2BSecDto.getCess())));
		gstr1SummaryOutward.put(docKey, gstr1SummaryB2BSecDto);
	}
}