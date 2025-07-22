package com.ey.advisory.app.data.services.einvoice;

import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.services.ewb.EwbDbUtilService;
import com.ey.advisory.app.data.services.ewb.EwbRequestConverter;
import com.ey.advisory.common.EwbProcessingStatus;
import com.ey.advisory.common.EwbStatusNew;
import com.ey.advisory.common.IrnStatusMaster;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.einv.app.api.APIIdentifiers;
import com.ey.advisory.einv.dto.GenerateIrnResponseXmlDto;
import com.ey.advisory.ewb.dto.EwayBillRequestDto;
import com.ey.advisory.ewb.dto.EwbResponseDto;
import com.google.common.base.Strings;

@Component("GeneEInvUtil")
public class GeneEInvUtil {

	@Autowired
	@Qualifier("EwbRequestConverter")
	EwbRequestConverter ewbReqConverter;

	@Autowired
	@Qualifier("EwbDbUtilServiceImpl")
	private EwbDbUtilService ewbDbService;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docrepo;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;
	
	public void dbPersistanceGenEInv(GenerateIrnResponseXmlDto resp,
			String isEwbReq, OutwardTransDocument doc, Long id) {

		if (isEwbReq.equalsIgnoreCase("true")) {
			if (!Strings.isNullOrEmpty(resp.getEwbNo())) {

				// save to ewb
				EwayBillRequestDto ewbReqDto = ewbReqConverter.convert(doc);

				EwbResponseDto ewbRespDto = new EwbResponseDto();
				ewbRespDto.setEwayBillDate(resp.getEwbDt());
				ewbRespDto.setEwayBillNo(resp.getEwbNo());
				ewbRespDto.setValidUpto(resp.getEwbValidTill());

				ewbDbService.generateEwbDbUpdate(doc, ewbReqDto, ewbRespDto,
						APIIdentifiers.GENERATE_EINV);

				docrepo.updateIrnDetails(id, resp.getAckNo().toString(),
						resp.getAckDt(), resp.getIrn(),
						IrnStatusMaster.GENERATED.getIrnStatusMaster());

			}

			// if einvoice is success and ewb is failed
			else {
				boolean ewbAlreadyGenerated = 
						(resp.getInfoErrorCode().contains("604")) ? true : false;
				
				docrepo.updateDocHeaderForEWBFailure(id,
						resp.getAckNo().toString(), resp.getAckDt(),
						resp.getIrn(),
						IrnStatusMaster.GENERATED.getIrnStatusMaster(), null,
						null, EwbStatusNew.PENDING.getEwbNewStatusCode(),
						ewbAlreadyGenerated ? EwbProcessingStatus.ERROR_CANCELLATION
								.getEwbProcessingStatusCode() :
												EwbProcessingStatus.GENERATION_ERROR
								.getEwbProcessingStatusCode(),
						resp.getInfoErrorCode(), resp.getInfoErrorMessage());
			}

		}
		else {
			docrepo.updateEinvoiceDocHeader(id, resp.getAckNo().toString(),
					resp.getAckDt(), resp.getIrn(),
					IrnStatusMaster.GENERATED.getIrnStatusMaster());
		}
	}
	
	public boolean isAddressSuppressRequired(OutwardTransDocument hdr) {

		if (Strings.isNullOrEmpty(hdr.getDocCategory())) {
			return false;
		}

		Map<String, Config> configMap = configManager.getConfigs("EINV",
				"einv.address", TenantContext.getTenantId());

		boolean docCategorySuppresReq = configMap
				.get("einv.address.suppresreq") == null ? Boolean.FALSE
						: Boolean.valueOf(configMap
								.get("einv.address.suppresreq").getValue());

		if (docCategorySuppresReq
				&& ("EXP".equalsIgnoreCase(hdr.getSubSupplyType())
						|| Stream.of("EXPT", "EXPWT").anyMatch(
								hdr.getSupplyType()::equalsIgnoreCase))) {
			return false;
		}
		return docCategorySuppresReq;
	}


}
