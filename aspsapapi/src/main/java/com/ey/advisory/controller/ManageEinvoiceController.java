package com.ey.advisory.controller;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.services.einvoice.CancelIrnService;
import com.ey.advisory.app.data.services.einvoice.EinvoiceAsyncService;
import com.ey.advisory.app.data.services.einvoice.GenerateEinvoiceService;
import com.ey.advisory.app.data.services.einvoice.GetSyncGSTINDetailsService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.LoggerIdContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.common.service.ERPReqRespLogHelper;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.einv.app.api.APIIdentifiers;
import com.ey.advisory.einv.common.EInvSynchCountConstant;
import com.ey.advisory.einv.dto.CancelIrnReqDto;
import com.ey.advisory.einv.dto.CancelIrnReqList;
import com.ey.advisory.einv.dto.EInvEWBUIResponseDto;
import com.ey.advisory.einv.dto.EinvoiceRequestDto;
import com.ey.advisory.einv.dto.GenerateEInvUIReqDto;
import com.ey.advisory.einv.dto.GenerateEWBByIrnResponseDto;
import com.ey.advisory.einv.dto.GetSyncGSTINDetailsResponseDto;
import com.ey.advisory.einv.dto.GetSyncGstinUIResponseDto;
import com.ey.advisory.einv.dto.PrecDocument;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun K.A
 *
 */
@Slf4j
@RestController
public class ManageEinvoiceController {

	public static void main(String[] args) {
		try {
			String str = "{\n\"data\":{\"AckNo\":182416262974479,\"AckDt\":\"2024-05-22 13:10:00\",\"Irn\":\"79892ffe2c854524aca7f81971afee71ee4f30ac5eee50f0977af13944159e38\",\"Version\":\"1.03\",\"TranDtls\":{\"TaxSch\":\"GST\",\"SupTyp\":\"B2B\",\"RegRev\":\"N\"},\"DocDtls\":{\"Typ\":\"INV\",\"No\":\"T0QS11524900347\",\"Dt\":\"22/05/2024\"},\"SellerDtls\":{\"Gstin\":\"19AACCM4684P1ZO\",\"LglNm\":\"Reliance Retail Ltd\",\"Addr1\":\"322 BHARAKHOLA EAST JADHAVPUR EAST\",\"Loc\":\"SONARPUR\",\"Pin\":700099,\"Stcd\":\"19\"},\"BuyerDtls\":{\"Gstin\":\"19AAACI6487L1ZX\",\"LglNm\":\"IFB AGRO INDUSTRIES LTD\",\"TrdNm\":\"IFB AGRO INDUSTRIES LTD\",\"Pos\":\"19\",\"Addr1\":\"IND5 SEC1 \",\"Addr2\":\"700107 WEST BENGAL \",\"Loc\":\"South 24 Parganas\",\"Pin\":700107,\"Stcd\":\"19\"},\"DispDtls\":{\"Nm\":\"Reliance Retail Ltd\",\"Addr1\":\"322 BHARAKHOLA EAST JADHAVPUR EAST\",\"Addr2\":\"JADHAVPUR South Twenty Four Parganas\",\"Loc\":\"West Bengal\",\"Pin\":700099,\"Stcd\":\"19\"},\"ShipDtls\":{\"Gstin\":\"19AAACI6487L1ZX\",\"LglNm\":\"IFB AGRO INDUSTRIES LTD\",\"TrdNm\":\"IFB AGRO INDUSTRIES LTD\",\"Addr1\":\"IND5 SEC1 \",\"Addr2\":\"700107 WEST BENGAL \",\"Loc\":\" South 24 Parganas\",\"Pin\":700107,\"Stcd\":\"19\"},\"ItemList\":[{\"ItemNo\":0,\"SlNo\":\"1\",\"IsServc\":\"N\",\"PrdDesc\":\"FL C FOLD E150S PK20\",\"HsnCd\":\"48189000\",\"Qty\":1.0,\"Unit\":\"BAG\",\"UnitPrice\":1227.94,\"TotAmt\":1227.94,\"AssAmt\":1227.94,\"GstRt\":18.0,\"IgstAmt\":0.0,\"CgstAmt\":110.52,\"SgstAmt\":110.52,\"CesRt\":0.0,\"CesAmt\":0.0,\"CesNonAdvlAmt\":0.0,\"StateCesRt\":0.0,\"StateCesAmt\":0.0,\"StateCesNonAdvlAmt\":0.0,\"OthChrg\":0.0,\"TotItemVal\":1448.98},{\"ItemNo\":0,\"SlNo\":\"2\",\"IsServc\":\"N\",\"PrdDesc\":\"FL C FOLD E150S PK20\",\"HsnCd\":\"48189000\",\"Qty\":1.0,\"Unit\":\"BAG\",\"UnitPrice\":1227.94,\"TotAmt\":1227.94,\"AssAmt\":1227.94,\"GstRt\":18.0,\"IgstAmt\":0.0,\"CgstAmt\":110.52,\"SgstAmt\":110.52,\"CesRt\":0.0,\"CesAmt\":0.0,\"CesNonAdvlAmt\":0.0,\"StateCesRt\":0.0,\"StateCesAmt\":0.0,\"StateCesNonAdvlAmt\":0.0,\"OthChrg\":0.0,\"TotItemVal\":1448.98},{\"ItemNo\":0,\"SlNo\":\"3\",\"IsServc\":\"N\",\"PrdDesc\":\"FL C FOLD E150S PK20\",\"HsnCd\":\"48189000\",\"Qty\":1.0,\"Unit\":\"BAG\",\"UnitPrice\":1227.94,\"TotAmt\":1227.94,\"AssAmt\":1227.94,\"GstRt\":18.0,\"IgstAmt\":0.0,\"CgstAmt\":110.52,\"SgstAmt\":110.52,\"CesRt\":0.0,\"CesAmt\":0.0,\"CesNonAdvlAmt\":0.0,\"StateCesRt\":0.0,\"StateCesAmt\":0.0,\"StateCesNonAdvlAmt\":0.0,\"OthChrg\":0.0,\"TotItemVal\":1448.98},{\"ItemNo\":0,\"SlNo\":\"4\",\"IsServc\":\"N\",\"PrdDesc\":\"LIZOL DFC FLORAL 5L\",\"HsnCd\":\"38089400\",\"Qty\":1.0,\"Unit\":\"BAG\",\"UnitPrice\":546.88,\"TotAmt\":546.88,\"AssAmt\":546.88,\"GstRt\":18.0,\"IgstAmt\":0.0,\"CgstAmt\":49.22,\"SgstAmt\":49.22,\"CesRt\":0.0,\"CesAmt\":0.0,\"CesNonAdvlAmt\":0.0,\"StateCesRt\":0.0,\"StateCesAmt\":0.0,\"StateCesNonAdvlAmt\":0.0,\"OthChrg\":0.0,\"TotItemVal\":645.32},{\"ItemNo\":0,\"SlNo\":\"5\",\"IsServc\":\"N\",\"PrdDesc\":\"LIZOL DFC FLORAL 5L\",\"HsnCd\":\"38089400\",\"Qty\":1.0,\"Unit\":\"BAG\",\"UnitPrice\":546.88,\"TotAmt\":546.88,\"AssAmt\":546.88,\"GstRt\":18.0,\"IgstAmt\":0.0,\"CgstAmt\":49.22,\"SgstAmt\":49.22,\"CesRt\":0.0,\"CesAmt\":0.0,\"CesNonAdvlAmt\":0.0,\"StateCesRt\":0.0,\"StateCesAmt\":0.0,\"StateCesNonAdvlAmt\":0.0,\"OthChrg\":0.0,\"TotItemVal\":645.32},{\"ItemNo\":0,\"SlNo\":\"6\",\"IsServc\":\"N\",\"PrdDesc\":\"VIM LIQUID 5 LTR\",\"HsnCd\":\"38089400\",\"Qty\":1.0,\"Unit\":\"BAG\",\"UnitPrice\":551.69,\"TotAmt\":551.69,\"AssAmt\":551.69,\"GstRt\":18.0,\"IgstAmt\":0.0,\"CgstAmt\":49.65,\"SgstAmt\":49.65,\"CesRt\":0.0,\"CesAmt\":0.0,\"CesNonAdvlAmt\":0.0,\"StateCesRt\":0.0,\"StateCesAmt\":0.0,\"StateCesNonAdvlAmt\":0.0,\"OthChrg\":0.0,\"TotItemVal\":650.99},{\"ItemNo\":0,\"SlNo\":\"7\",\"IsServc\":\"N\",\"PrdDesc\":\"VIM LIQUID 5 LTR\",\"HsnCd\":\"38089400\",\"Qty\":1.0,\"Unit\":\"BAG\",\"UnitPrice\":551.69,\"TotAmt\":551.69,\"AssAmt\":551.69,\"GstRt\":18.0,\"IgstAmt\":0.0,\"CgstAmt\":49.65,\"SgstAmt\":49.65,\"CesRt\":0.0,\"CesAmt\":0.0,\"CesNonAdvlAmt\":0.0,\"StateCesRt\":0.0,\"StateCesAmt\":0.0,\"StateCesNonAdvlAmt\":0.0,\"OthChrg\":0.0,\"TotItemVal\":650.99},{\"ItemNo\":0,\"SlNo\":\"8\",\"IsServc\":\"N\",\"PrdDesc\":\"HPC TLT CLN5L\",\"HsnCd\":\"38089400\",\"Qty\":1.0,\"Unit\":\"BAG\",\"UnitPrice\":508.83,\"TotAmt\":508.83,\"AssAmt\":508.83,\"GstRt\":18.0,\"IgstAmt\":0.0,\"CgstAmt\":45.8,\"SgstAmt\":45.8,\"CesRt\":0.0,\"CesAmt\":0.0,\"CesNonAdvlAmt\":0.0,\"StateCesRt\":0.0,\"StateCesAmt\":0.0,\"StateCesNonAdvlAmt\":0.0,\"OthChrg\":0.0,\"TotItemVal\":600.43},{\"ItemNo\":0,\"SlNo\":\"9\",\"IsServc\":\"N\",\"PrdDesc\":\"CLIN PUMP 500 ml BTL\",\"HsnCd\":\"34022090\",\"Qty\":12.0,\"Unit\":\"BAG\",\"UnitPrice\":80.36,\"TotAmt\":964.38,\"AssAmt\":964.38,\"GstRt\":18.0,\"IgstAmt\":0.0,\"CgstAmt\":86.79,\"SgstAmt\":86.79,\"CesRt\":0.0,\"CesAmt\":0.0,\"CesNonAdvlAmt\":0.0,\"StateCesRt\":0.0,\"StateCesAmt\":0.0,\"StateCesNonAdvlAmt\":0.0,\"OthChrg\":0.0,\"TotItemVal\":1137.96},{\"ItemNo\":0,\"SlNo\":\"10\",\"IsServc\":\"N\",\"PrdDesc\":\"LIFEBUOY TML 1 1 PCH\",\"HsnCd\":\"34013019\",\"Qty\":5.0,\"Unit\":\"BAG\",\"UnitPrice\":151.93,\"TotAmt\":759.64,\"AssAmt\":759.64,\"GstRt\":18.0,\"IgstAmt\":0.0,\"CgstAmt\":68.37,\"SgstAmt\":68.37,\"CesRt\":0.0,\"CesAmt\":0.0,\"CesNonAdvlAmt\":0.0,\"StateCesRt\":0.0,\"StateCesAmt\":0.0,\"StateCesNonAdvlAmt\":0.0,\"OthChrg\":0.0,\"TotItemVal\":896.38},{\"ItemNo\":0,\"SlNo\":\"11\",\"IsServc\":\"N\",\"PrdDesc\":\"FL NAPHTHABALLS 400g\",\"HsnCd\":\"29029040\",\"Qty\":1.0,\"Unit\":\"BAG\",\"UnitPrice\":83.89,\"TotAmt\":83.89,\"AssAmt\":83.89,\"GstRt\":18.0,\"IgstAmt\":0.0,\"CgstAmt\":7.55,\"SgstAmt\":7.55,\"CesRt\":0.0,\"CesAmt\":0.0,\"CesNonAdvlAmt\":0.0,\"StateCesRt\":0.0,\"StateCesAmt\":0.0,\"StateCesNonAdvlAmt\":0.0,\"OthChrg\":0.0,\"TotItemVal\":98.99},{\"ItemNo\":0,\"SlNo\":\"12\",\"IsServc\":\"N\",\"PrdDesc\":\"FL NAPHTHABALLS 400g\",\"HsnCd\":\"29029040\",\"Qty\":1.0,\"Unit\":\"BAG\",\"UnitPrice\":83.89,\"TotAmt\":83.89,\"AssAmt\":83.89,\"GstRt\":18.0,\"IgstAmt\":0.0,\"CgstAmt\":7.55,\"SgstAmt\":7.55,\"CesRt\":0.0,\"CesAmt\":0.0,\"CesNonAdvlAmt\":0.0,\"StateCesRt\":0.0,\"StateCesAmt\":0.0,\"StateCesNonAdvlAmt\":0.0,\"OthChrg\":0.0,\"TotItemVal\":98.99},{\"ItemNo\":0,\"SlNo\":\"13\",\"IsServc\":\"N\",\"PrdDesc\":\"GOOD KNIGHL 50 g CBD\",\"HsnCd\":\"38089191\",\"Qty\":6.0,\"Unit\":\"BAG\",\"UnitPrice\":18.34,\"TotAmt\":110.04,\"AssAmt\":110.04,\"GstRt\":18.0,\"IgstAmt\":0.0,\"CgstAmt\":9.9,\"SgstAmt\":9.9,\"CesRt\":0.0,\"CesAmt\":0.0,\"CesNonAdvlAmt\":0.0,\"StateCesRt\":0.0,\"StateCesAmt\":0.0,\"StateCesNonAdvlAmt\":0.0,\"OthChrg\":0.0,\"TotItemVal\":129.84},{\"ItemNo\":0,\"SlNo\":\"14\",\"IsServc\":\"N\",\"PrdDesc\":\"SANTOOR ORANGE 43G\",\"HsnCd\":\"34011190\",\"Qty\":1.0,\"Unit\":\"BAG\",\"UnitPrice\":31.68,\"TotAmt\":31.68,\"AssAmt\":31.68,\"GstRt\":18.0,\"IgstAmt\":0.0,\"CgstAmt\":2.85,\"SgstAmt\":2.85,\"CesRt\":0.0,\"CesAmt\":0.0,\"CesNonAdvlAmt\":0.0,\"StateCesRt\":0.0,\"StateCesAmt\":0.0,\"StateCesNonAdvlAmt\":0.0,\"OthChrg\":0.0,\"TotItemVal\":37.38},{\"ItemNo\":0,\"SlNo\":\"15\",\"IsServc\":\"N\",\"PrdDesc\":\"SANTOOR ORANGE 43G\",\"HsnCd\":\"34011190\",\"Qty\":1.0,\"Unit\":\"BAG\",\"UnitPrice\":31.68,\"TotAmt\":31.68,\"AssAmt\":31.68,\"GstRt\":18.0,\"IgstAmt\":0.0,\"CgstAmt\":2.85,\"SgstAmt\":2.85,\"CesRt\":0.0,\"CesAmt\":0.0,\"CesNonAdvlAmt\":0.0,\"StateCesRt\":0.0,\"StateCesAmt\":0.0,\"StateCesNonAdvlAmt\":0.0,\"OthChrg\":0.0,\"TotItemVal\":37.38},{\"ItemNo\":0,\"SlNo\":\"16\",\"IsServc\":\"N\",\"PrdDesc\":\"SANTOOR ORANGE 43G\",\"HsnCd\":\"34011190\",\"Qty\":1.0,\"Unit\":\"BAG\",\"UnitPrice\":31.68,\"TotAmt\":31.68,\"AssAmt\":31.68,\"GstRt\":18.0,\"IgstAmt\":0.0,\"CgstAmt\":2.85,\"SgstAmt\":2.85,\"CesRt\":0.0,\"CesAmt\":0.0,\"CesNonAdvlAmt\":0.0,\"StateCesRt\":0.0,\"StateCesAmt\":0.0,\"StateCesNonAdvlAmt\":0.0,\"OthChrg\":0.0,\"TotItemVal\":37.38},{\"ItemNo\":0,\"SlNo\":\"17\",\"IsServc\":\"N\",\"PrdDesc\":\"GK ACTIVE 33 PK 4\",\"HsnCd\":\"38089191\",\"Qty\":1.0,\"Unit\":\"BAG\",\"UnitPrice\":230.36,\"TotAmt\":230.36,\"AssAmt\":230.36,\"GstRt\":18.0,\"IgstAmt\":0.0,\"CgstAmt\":20.73,\"SgstAmt\":20.73,\"CesRt\":0.0,\"CesAmt\":0.0,\"CesNonAdvlAmt\":0.0,\"StateCesRt\":0.0,\"StateCesAmt\":0.0,\"StateCesNonAdvlAmt\":0.0,\"OthChrg\":0.0,\"TotItemVal\":271.82},{\"ItemNo\":0,\"SlNo\":\"18\",\"IsServc\":\"N\",\"PrdDesc\":\"GK ACTIVE 33 PK 4\",\"HsnCd\":\"38089191\",\"Qty\":1.0,\"Unit\":\"BAG\",\"UnitPrice\":230.36,\"TotAmt\":230.36,\"AssAmt\":230.36,\"GstRt\":18.0,\"IgstAmt\":0.0,\"CgstAmt\":20.73,\"SgstAmt\":20.73,\"CesRt\":0.0,\"CesAmt\":0.0,\"CesNonAdvlAmt\":0.0,\"StateCesRt\":0.0,\"StateCesAmt\":0.0,\"StateCesNonAdvlAmt\":0.0,\"OthChrg\":0.0,\"TotItemVal\":271.82},{\"ItemNo\":0,\"SlNo\":\"19\",\"IsServc\":\"N\",\"PrdDesc\":\"GK ACTIVE 33 PK 4\",\"HsnCd\":\"38089191\",\"Qty\":1.0,\"Unit\":\"BAG\",\"UnitPrice\":230.36,\"TotAmt\":230.36,\"AssAmt\":230.36,\"GstRt\":18.0,\"IgstAmt\":0.0,\"CgstAmt\":20.73,\"SgstAmt\":20.73,\"CesRt\":0.0,\"CesAmt\":0.0,\"CesNonAdvlAmt\":0.0,\"StateCesRt\":0.0,\"StateCesAmt\":0.0,\"StateCesNonAdvlAmt\":0.0,\"OthChrg\":0.0,\"TotItemVal\":271.82},{\"ItemNo\":0,\"SlNo\":\"20\",\"IsServc\":\"N\",\"PrdDesc\":\"FINE LIFE AIRFRSNR B\",\"HsnCd\":\"33071010\",\"Qty\":24.0,\"Unit\":\"BAG\",\"UnitPrice\":24.58,\"TotAmt\":589.84,\"AssAmt\":589.84,\"GstRt\":18.0,\"IgstAmt\":0.0,\"CgstAmt\":53.08,\"SgstAmt\":53.08,\"CesRt\":0.0,\"CesAmt\":0.0,\"CesNonAdvlAmt\":0.0,\"StateCesRt\":0.0,\"StateCesAmt\":0.0,\"StateCesNonAdvlAmt\":0.0,\"OthChrg\":0.0,\"TotItemVal\":696.0},{\"ItemNo\":0,\"SlNo\":\"21\",\"IsServc\":\"N\",\"PrdDesc\":\"MORTEIN 2IY600ml CAN\",\"HsnCd\":\"38089199\",\"Qty\":1.0,\"Unit\":\"BAG\",\"UnitPrice\":171.62,\"TotAmt\":171.62,\"AssAmt\":171.62,\"GstRt\":18.0,\"IgstAmt\":0.0,\"CgstAmt\":15.44,\"SgstAmt\":15.44,\"CesRt\":0.0,\"CesAmt\":0.0,\"CesNonAdvlAmt\":0.0,\"StateCesRt\":0.0,\"StateCesAmt\":0.0,\"StateCesNonAdvlAmt\":0.0,\"OthChrg\":0.0,\"TotItemVal\":202.5},{\"ItemNo\":0,\"SlNo\":\"22\",\"IsServc\":\"N\",\"PrdDesc\":\"MORTEIN 2IY600ml CAN\",\"HsnCd\":\"38089199\",\"Qty\":1.0,\"Unit\":\"BAG\",\"UnitPrice\":171.62,\"TotAmt\":171.62,\"AssAmt\":171.62,\"GstRt\":18.0,\"IgstAmt\":0.0,\"CgstAmt\":15.44,\"SgstAmt\":15.44,\"CesRt\":0.0,\"CesAmt\":0.0,\"CesNonAdvlAmt\":0.0,\"StateCesRt\":0.0,\"StateCesAmt\":0.0,\"StateCesNonAdvlAmt\":0.0,\"OthChrg\":0.0,\"TotItemVal\":202.5},{\"ItemNo\":0,\"SlNo\":\"23\",\"IsServc\":\"N\",\"PrdDesc\":\"ODNIL ARSL CTRS220ml\",\"HsnCd\":\"33071010\",\"Qty\":10.0,\"Unit\":\"BAG\",\"UnitPrice\":105.22,\"TotAmt\":1052.2,\"AssAmt\":1052.2,\"GstRt\":18.0,\"IgstAmt\":0.0,\"CgstAmt\":94.7,\"SgstAmt\":94.7,\"CesRt\":0.0,\"CesAmt\":0.0,\"CesNonAdvlAmt\":0.0,\"StateCesRt\":0.0,\"StateCesAmt\":0.0,\"StateCesNonAdvlAmt\":0.0,\"OthChrg\":0.0,\"TotItemVal\":1241.6},{\"ItemNo\":0,\"SlNo\":\"24\",\"IsServc\":\"N\",\"PrdDesc\":\"SB SILVER 5.5 6 S PP\",\"HsnCd\":\"96039000\",\"Qty\":9.0,\"Unit\":\"BAG\",\"UnitPrice\":147.71,\"TotAmt\":1329.4,\"AssAmt\":1329.4,\"GstRt\":18.0,\"IgstAmt\":0.0,\"CgstAmt\":119.65,\"SgstAmt\":119.65,\"CesRt\":0.0,\"CesAmt\":0.0,\"CesNonAdvlAmt\":0.0,\"StateCesRt\":0.0,\"StateCesAmt\":0.0,\"StateCesNonAdvlAmt\":0.0,\"OthChrg\":0.0,\"TotItemVal\":1568.7}],\"ValDtls\":{\"AssVal\":12492.43,\"CgstVal\":1124.31,\"SgstVal\":1124.31,\"IgstVal\":0.0,\"CesVal\":0.0,\"StCesVal\":0.0,\"TotInvVal\":14741.05},\"RefDtls\":{\"PrecDocDtls\":[null]}},\n\"iss\":\"NIC\"\n}";

			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

			JsonObject requestObject = JsonParser.parseString(str)
					.getAsJsonObject().get("data").getAsJsonObject();
			EinvoiceRequestDto respDto = gson.fromJson(requestObject,
					EinvoiceRequestDto.class);
		
			System.out.println("------> "+respDto.getRefDtls());
			if (respDto.getRefDtls() != null) {
				System.out.println("------> "+respDto.getRefDtls().getPrecDocDtls());
//				if (respDto.getRefDtls().getPrecDocDtls() != null) {
					for (PrecDocument nestedItem : respDto.getRefDtls()
						.getPrecDocDtls()) {
					if (nestedItem != null) {
						nestedItem.getInvNo();
						System.out.println("null HI 4");
					}
					// }
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Autowired
	@Qualifier("GenerateEinvoiceServiceImpl")
	private GenerateEinvoiceService generateEinvoiceService;

	@Autowired
	@Qualifier("CancelIrnServiceImpl")
	private CancelIrnService cancelIrnService;

	@Autowired
	@Qualifier("EinvoiceAsyncServiceImpl")
	private EinvoiceAsyncService einvoiceService;

	@Autowired
	private LoggerAdviceHelper loggerAdviceHelper;

	@Autowired
	private ERPReqRespLogHelper reqLogHelper;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docrepo;

	@Autowired
	@Qualifier("GetSyncGSTINDetailsServiceImpl")
	private GetSyncGSTINDetailsService getSyncGSTINDetailsService;

	@PostMapping(value = "/ui/generateEinvoice", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> generateEinvoiceUi(
			@RequestBody String jsonString) {
		JsonObject resp = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gson ewbGson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			String isEWBReq = obj.get("req").getAsJsonObject().get("isEWBReq")
					.getAsString();
			GenerateEInvUIReqDto req = gson.fromJson(obj.get("req"),
					GenerateEInvUIReqDto.class);
			List<Long> docIdList = req.getDocIdList();
			List<EInvEWBUIResponseDto> respList = new ArrayList<>();
			if (EInvSynchCountConstant.GENEINV > docIdList.size()) {
				docIdList.forEach(o -> {
					loggerAdviceHelper.createReqLog(String.valueOf(o),
							APIIdentifiers.GENERATE_EINV);
					EInvEWBUIResponseDto respDto = null;
					try {
						String genResp = einvoiceService.generateIrn(o,
								isEWBReq);
						Optional<OutwardTransDocument> doc = docrepo
								.findById(o);

						respDto = new EInvEWBUIResponseDto(o,
								doc.get().getDocNo(), genResp);

					} catch (Exception e) {
						LOGGER.error(
								"ERROR IN  Generate E Invoice Iteration Id's",
								e);
						respDto = new EInvEWBUIResponseDto(o, e.getMessage());
						reqLogHelper.updateResponsePayload(e.getMessage(),
								false);
					} finally {
						reqLogHelper.saveLogEntity();
					}
					respList.add(respDto);
				});
				resp.add("resp", ewbGson.toJsonTree(respList));
			} else {
				String asynStr = einvoiceService.generateIrnAsync(docIdList,
						isEWBReq);
				resp.add("resp", ewbGson.toJsonTree(asynStr));
			}

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception in Generate E Invoice with request ", ex);
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", ewbGson.toJsonTree(
					"Error While Generating E Invoice, Please Contact HelpDesk"));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		}
	}

	private ResponseEntity<String> cancelEinvoice(String jsonString) {
		JsonObject jsonObj = new JsonObject();
		Gson gson = GsonUtil.newSAPGsonInstance();
		try {
			JsonObject resp = new JsonObject();
			CancelIrnReqList dtoList = gson.fromJson(jsonString,
					CancelIrnReqList.class);
			List<CancelIrnReqDto> reqList = dtoList.getReqList();
			List<EInvEWBUIResponseDto> respList = new ArrayList<>();
			if (EInvSynchCountConstant.CANEINV > dtoList.getReqList().size()) {
				reqList.forEach(o -> {
					loggerAdviceHelper.createReqLog(
							String.valueOf(o.getDocHeaderId()),
							APIIdentifiers.CANCEL_EINV);
					EInvEWBUIResponseDto respDto = null;
					try {
						String canResp = einvoiceService.cancelEInvSync(o);
						respDto = new EInvEWBUIResponseDto(o.getDocHeaderId(),
								canResp);
					} catch (Exception e) {
						LOGGER.error(
								"ERROR IN  Cancel E Invoice Iteration Id's", e);
						respDto = new EInvEWBUIResponseDto(o.getDocHeaderId(),
								e.getMessage());
						reqLogHelper.updateResponsePayload(e.getMessage(),
								false);
					} finally {
						reqLogHelper.saveLogEntity();
					}
					respList.add(respDto);
				});
				resp.add("resp", gson.toJsonTree(respList));
			} else {
				LOGGER.debug(
						"Since there are more than 5 requests, Inside Async Mode");
				String asynStr = einvoiceService.cancelEInvASync(dtoList);
				resp.add("resp", gson.toJsonTree(asynStr));
			}

			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception occured while Canceling E-Invoice ", ex);
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			jsonObj.add("resp",
					gson.toJsonTree(
							"Excpeion while Processing the Cancel Einvoice Req "
									+ ex.getMessage()));
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} finally {
			TenantContext.clearTenant();
		}
	}

	@PostMapping(value = "ui/cancelEinvoice", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> cancelEinvoiceUi(
			@RequestBody String jsonString) {
		return cancelEinvoice(jsonString);
	}

	@PostMapping(value = "ui/generateEWBByIrn", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> generateEWBByIrnUi(
			@RequestBody String jsonString) {
		return generateEWBByIrn(jsonString);
	}

	private ResponseEntity<String> generateEWBByIrn(String jsonString) {
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		JsonObject resp = new JsonObject();
		Gson ewbGson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			GenerateEInvUIReqDto req = gson.fromJson(obj.get("req"),
					GenerateEInvUIReqDto.class);
			List<Long> docIdList = req.getDocIdList();
			List<EInvEWBUIResponseDto> respList = new ArrayList<>();
			if (LOGGER.isDebugEnabled()) {
				String msg = "End of General Eway Bill Irn";
				LOGGER.debug(msg);
			}
			docIdList.forEach(o -> {
				EInvEWBUIResponseDto respDto = null;
				try {
					String genEWBByIrnResp = einvoiceService
							.generateEWBByIrn(o);
					respDto = new EInvEWBUIResponseDto(o, genEWBByIrnResp);
				} catch (Exception e) {
					LOGGER.error(
							"ERROR IN  Generate  EWB By Irn Iteration Id's", e);
					respDto = new EInvEWBUIResponseDto(o, e.getMessage());
				} finally {
					reqLogHelper.saveLogEntity();
				}
				respList.add(respDto);
			});
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", ewbGson.toJsonTree(respList));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			GenerateEWBByIrnResponseDto response = new GenerateEWBByIrnResponseDto();
			LOGGER.error("Exception while Generating EWB by Irn ", ex);
			response.setErrorCode("");
			response.setErrorMessage(ex.getMessage());
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} finally {
			TenantContext.clearTenant();
			LoggerIdContext.clearLoggerId();
		}
	}

	@PostMapping(value = "/ui/getSyncDetails", consumes = {
			MediaType.APPLICATION_JSON_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getSyncDetailsapi(
			@RequestBody String jsonString, HttpServletRequest req) {
		return getSyncDetails(jsonString);
	}

	private ResponseEntity<String> getSyncDetails(String jsonString) {
		JsonObject jsonObj = new JsonObject();
		JsonObject respObj = new JsonObject();
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		List<GetSyncGstinUIResponseDto> respList = new ArrayList<>();
		try {
			JsonObject obj = new JsonParser().parse(jsonString)
					.getAsJsonObject();
			JsonObject req = obj.get("req").getAsJsonObject();
			String docHeaderId = req.get("docHeaderId").getAsString();
			if (Strings.isNullOrEmpty(docHeaderId))
				throw new AppException("docHeaderId is mandatory.");
			Optional<OutwardTransDocument> doc = docrepo
					.findById(Long.valueOf(docHeaderId));
			if (!doc.isPresent()) {
				String errMsg = "No Invoice Found For Requested Params";
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			OutwardTransDocument transDocuments = doc.get();
			List<String> syncGstinList = new ArrayList<>();
			String shipToGstin = transDocuments.getShipToGstin();
			String custGstin = transDocuments.getCgstin();
			String suppGstin = transDocuments.getSgstin();
			if (!Strings.isNullOrEmpty(shipToGstin)) {
				syncGstinList.add(shipToGstin);
			}
			if (!Strings.isNullOrEmpty(custGstin)) {
				syncGstinList.add(custGstin);
			}
			for (int i = 0; i < syncGstinList.size(); i++) {
				GetSyncGSTINDetailsResponseDto response = null;
				GetSyncGstinUIResponseDto respDto = null;
				boolean nicStatus = false;
				String errStr = "";
				req.addProperty("syncGstin", syncGstinList.get(i));
				loggerAdviceHelper.createReqLog(req.toString(),
						APIIdentifiers.GET_SYNCGSTINFROMCP);
				try {
					response = getSyncGSTINDetailsService
							.getSyncDetails(syncGstinList.get(i), suppGstin);
					if (!Strings.isNullOrEmpty(response.getGstin())) {
						respObj.add("hdr", gson
								.toJsonTree(APIRespDto.createSuccessResp()));
						nicStatus = true;
						errStr = "GSTIN Sync API call is Successful";
					} else {
						respObj.add("hdr",
								gson.toJsonTree(APIRespDto.creatErrorResp()));
						errStr = response.getErrorMessage();
					}
					respDto = new GetSyncGstinUIResponseDto(suppGstin,
							syncGstinList.get(i), errStr);
					respObj.add("resp", gson.toJsonTree(response));
					reqLogHelper.updateResponsePayload(respObj.toString(),
							nicStatus);
				} catch (Exception e) {
					LOGGER.error("ERROR IN  Sync Gstin Iteration Id's", e);
					respDto = new GetSyncGstinUIResponseDto(suppGstin,
							e.getMessage());
					reqLogHelper.updateResponsePayload(e.getMessage(),
							nicStatus);
				} finally {
					reqLogHelper.saveLogEntity();
				}
				respList.add(respDto);
			}
			jsonObj.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			if (!respList.isEmpty()) {
				jsonObj.add("resp", gson.toJsonTree(respList));
			} else {
				String errMsg = "No GSTIN's Found To Sync For Requested Params";
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}
			if (LOGGER.isDebugEnabled()) {
				String msg = "End of Get Sync Details";
				LOGGER.debug(msg);
			}
			return new ResponseEntity<>(jsonObj.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			LOGGER.error("Exception occured while Get Sync Details", ex);
			JsonObject resp = new JsonObject();
			GetSyncGSTINDetailsResponseDto response = new GetSyncGSTINDetailsResponseDto();
			response.setErrorCode("INTERNALEXP");
			if (ex instanceof SocketException) {
				response.setErrorMessage(
						"NIC Response Timedout. Please Try again");
			} else {
				response.setErrorMessage(ex.getMessage());
			}
			resp.add("hdr", gson.toJsonTree(APIRespDto.creatErrorResp()));
			resp.add("resp", gson.toJsonTree(response));
			reqLogHelper.updateResponsePayload(resp.toString(), false);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} finally {
			reqLogHelper.saveLogEntity();
		}
	}

	@GetMapping("/status")
	public String handler() {
		return "{\"msg\": \"App is UP and RUNNING\"}";
	}

}
