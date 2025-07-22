package com.ey.advisory.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.business.dto.PayloadReqDto;
import com.ey.advisory.domain.client.ERPRequestLogEntity;
import com.ey.advisory.repositories.client.LoggerAdviceRepository;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("PayloadCommUtility")
public class PayloadCommUtility {

	@Autowired
	private LoggerAdviceRepository logAdvRepo;

	public static final String GENEINV = "GENEINV";

	public static final String CANEINV = "CANEINV";

	public static final String GENEWB_IRN = "GENEWBBYIRN";

	public static final String GENEWB = "GENEWAYBILL";

	public static final String CANEWB = "CANEWB";

	public Optional<ERPRequestLogEntity> payloadService(PayloadReqDto hdr) {

		try {
			String apiType = hdr.getApiType();
			if (Strings.isNullOrEmpty(apiType)) {
				LOGGER.error("API Is Empty");
				throw new AppException("APIType is Mandatory");
			}

			String docNum = hdr.getDocNum();
			String docType = hdr.getDocType();
			String irnNo = hdr.getIrn();
			String ewbNo = hdr.getEwbNo();
			Optional<ERPRequestLogEntity> ent = null;

			List<String> apiTypeList = new ArrayList<>();
			if (apiType.contains(GENEINV) || apiType.contains(GENEWB)) {
				if (Strings.isNullOrEmpty(docNum)
						|| Strings.isNullOrEmpty(docType)) {
					String errMsg = String.format(
							"DocNo and DocType are mandatory for API Type %s",
							apiType);
					LOGGER.error(errMsg);
					throw new AppException(errMsg);
				}
				if (apiType.contains(GENEINV)) {
					apiTypeList.add(GENEINV);
					apiTypeList.add(GENEINV + "-V3");
				} else if (apiType.contains(GENEWB)) {
					apiTypeList.add(GENEWB);
					apiTypeList.add(GENEWB + "-V3");
				}

				ent = logAdvRepo
						.findTop1ByApiTypeInAndDocNumAndDocTypeAndNicStatusFalseOrderByIdDesc(
								apiTypeList, docNum, docType);
			} else if (apiType.contains(CANEINV)
					|| apiType.contains(GENEWB_IRN)) {
				if (Strings.isNullOrEmpty(irnNo)) {
					String errMsg = String.format(
							"Irn Num is mandatory for API Type %s", apiType);
					LOGGER.error(errMsg);
					throw new AppException(errMsg);
				}

				if (apiType.contains(CANEINV)) {
					apiTypeList.add(CANEINV);
					apiTypeList.add(CANEINV + "-V3");
				} else if (apiType.contains(GENEWB_IRN)) {
					apiTypeList.add(GENEWB_IRN);
					apiTypeList.add(GENEWB_IRN + "-V3");
				}
				ent = logAdvRepo
						.findTop1ByApiTypeInAndIrnNumAndNicStatusFalseOrderByIdDesc(
								apiTypeList, irnNo);

			} else if (apiType.contains(CANEWB)) {
				if (Strings.isNullOrEmpty(ewbNo)) {
					String errMsg = String.format(
							"EwayBill Num is mandatory for API Type %s",
							apiType);
					LOGGER.error(errMsg);
					throw new AppException(errMsg);
				}
				if (apiType.contains(CANEWB)) {
					apiTypeList.add(CANEWB);
					apiTypeList.add(CANEWB + "-V3");
				}
				ent = logAdvRepo
						.findTop1ByApiTypeInAndEwbNoAndNicStatusFalseOrderByIdDesc(
								apiTypeList, ewbNo);
			} else {
				String msg = String.format("Invalid API Type %s ", apiType);
				LOGGER.error(msg);
				throw new AppException(msg);
			}

			return ent;
		} catch (Exception e) {
			String errMsg = String
					.format("Error while retriving the documents");
			LOGGER.error(errMsg);
			throw new AppException(errMsg);
		}

	}
}
