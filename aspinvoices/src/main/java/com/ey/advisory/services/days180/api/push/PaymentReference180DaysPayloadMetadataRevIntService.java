package com.ey.advisory.services.days180.api.push;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.repositories.client.PaymentReferencePayloadRepository;
import com.ey.advisory.app.data.repositories.client.Reversal180DaysErrorRepository;
import com.ey.advisory.app.data.repositories.client.Reversal180DaysPSDRepository;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.service.days.revarsal180.Reversal180DaysErrorEntity;
import com.ey.advisory.service.days.revarsal180.Reversal180DaysPSDEntity;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */
@Slf4j
@Service("PaymentReference180DaysPayloadMetadataRevIntService")
public class PaymentReference180DaysPayloadMetadataRevIntService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("PaymentReferencePayloadRepository")
	private PaymentReferencePayloadRepository payloadRepository;

	@Autowired
	@Qualifier("Reversal180DaysPSDRepository")
	private Reversal180DaysPSDRepository psdRepo;

	@Autowired
	@Qualifier("Reversal180DaysErrorRepository")
	private Reversal180DaysErrorRepository errRepo;

	public PayloadMetaDataXMLDto payloadErrorInfoMsg(String payloadId) {

		PayloadMetaDataXMLDto metaDataDto = new PayloadMetaDataXMLDto();
		PayloadMetaDataRespDto respObj = new PayloadMetaDataRespDto();
		PayloadMetaDataHeaderXmlDto busMsgDto = getPayloadMetaData(payloadId);

		List<PayloadMetaDataItemDto> respList = getObjectPayload(payloadId);

		respObj.setDockeyItems(respList);

		metaDataDto.setHdrDto(busMsgDto);

		metaDataDto.setResp(respObj);

		return metaDataDto;
	}

	private PayloadMetaDataHeaderXmlDto getPayloadMetaData(String payloadId) {
		PayloadMetaDataHeaderXmlDto msgDto = new PayloadMetaDataHeaderXmlDto();

		PaymentReferencePayloadEntity entity = payloadRepository
				.getPayload(payloadId);

		if (entity != null) {
			msgDto.setErrorCount(
					entity.getErrorCount() != null ? entity.getErrorCount()
							: 0);
			msgDto.setModifiedOn(
					EYDateUtil.toISTDateTimeFromUTC(entity.getModifiedOn()));
			msgDto.setPayloadId(entity.getPayloadId());
			msgDto.setProcessCount((entity.getTotalCount() != null
					? entity.getTotalCount()
					: 0)
					- (entity.getErrorCount() != null ? entity.getErrorCount()
							: 0));
			msgDto.setStatus(entity.getStatus());
			msgDto.setTotalCount(entity.getTotalCount());
			if (entity.getStatus().equalsIgnoreCase(APIConstants.P)
					|| entity.getStatus().equalsIgnoreCase(APIConstants.PE)) {
				LOGGER.debug("MessageInfo Not Req");
			} else {
				msgDto.setMessageInfo(entity.getJsonErrorResponse());
			}
			msgDto.setCompanyCode(entity.getCompanyCode());
			msgDto.setPushType(entity.getPushType());
		}

		return msgDto;
	}

	private List<PayloadMetaDataItemDto> getObjectPayload(String payloadId) {

		List<PayloadMetaDataItemDto> list = new ArrayList<>();
		List<PayloadMetaDataItemDto> psdReconds = new ArrayList<>();
		List<PayloadMetaDataItemDto> errReconds = new ArrayList<>();

		List<Reversal180DaysPSDEntity> psdEntity = psdRepo
				.findByPayloadIdAndIsPsdFalse(payloadId);

		List<Reversal180DaysErrorEntity> errEntity = errRepo
				.findByPayloadId(payloadId);

		if (!psdEntity.isEmpty()) {

			psdReconds = psdEntity.stream().map(o -> convertPsdDto(o))
					.collect(Collectors.toList());

			list.addAll(psdReconds);
		}

		if (!errEntity.isEmpty()) {
			errReconds = errEntity.stream().map(o -> convertError(o))
					.collect(Collectors.toList());
			list.addAll(errReconds);
		}

		return list;
	}

	private PayloadMetaDataItemDto convertError(
			Reversal180DaysErrorEntity dto) {

		PayloadMetaDataItemDto docDto = new PayloadMetaDataItemDto();
		PayloadMetaDataErrorItemDto errItemObj = new PayloadMetaDataErrorItemDto();

		List<ErrorItem> errorList = new ArrayList<>();

		docDto.setCustomerGSTIN(dto.getCustomerGSTIN());
		docDto.setDocumentDate(
				dto.getDocumentDate() != null ? dto.getDocumentDate().toString()
						: null);
		docDto.setDocumentNumber(dto.getDocumentNumber());
		docDto.setDocumentType(dto.getDocumentType());

		int finYear = 0;
		if (dto.getDocumentDate() != null) {
			try {
			 finYear = (DateUtil.parseObjToDate(dto.getDocumentDate())).getYear();
			}catch(Exception ex) {
				LOGGER.error("error in finYear {}",dto.getDocumentDate());
			}
			 
		}

		docDto.setFiscalYear(Integer.toString(finYear));
		docDto.setPayReferenceDate(dto.getPaymentReferenceDate() != null
				? dto.getPaymentReferenceDate().toString()
				: null);
		docDto.setPayReferenceNo(dto.getPaymentReferenceNumber());
		docDto.setSupplierGSTIN(dto.getSupplierGSTIN());

		String[] errorCodes = dto.getErrorCode().split(",");
		String[] errorDesc = dto.getErrorDesc().split(",");

		int i = 0;
		for (String errCode : errorCodes) {
			ErrorItem errDto = new ErrorItem();

			errDto.setErrorCode(errCode);
			errDto.setErrorDesc(errorDesc[i]);
			errorList.add(errDto);
			i++;
		}

		errItemObj.setErrorItems(errorList);
		docDto.setErrorDetails(errItemObj);
		return docDto;

	}

	private PayloadMetaDataItemDto convertPsdDto(Reversal180DaysPSDEntity dto) {

		PayloadMetaDataItemDto docDto = new PayloadMetaDataItemDto();
		PayloadMetaDataErrorItemDto errItemObj = new PayloadMetaDataErrorItemDto();

		List<ErrorItem> errorList = new ArrayList<>();

		docDto.setCustomerGSTIN(dto.getCustomerGSTIN());
		docDto.setDocumentDate(
				dto.getDocumentDate() != null ? dto.getDocumentDate().toString()
						: null);
		docDto.setDocumentNumber(dto.getDocumentNumber());
		docDto.setDocumentType(dto.getDocumentType());

		int finYear = 0;
		if (dto.getDocumentDate() != null) {
			try {
			 finYear = (dto.getDocumentDate()).getYear();
			}catch(Exception ex) {
				LOGGER.error("error in finYear {}",dto.getDocumentDate());
			}
		}

		docDto.setFiscalYear(Integer.toString(finYear));
		docDto.setPayReferenceDate(dto.getPaymentReferenceDate() != null
				? dto.getPaymentReferenceDate().toString()
				: null);
		docDto.setPayReferenceNo(dto.getPaymentReferenceNumber());
		docDto.setSupplierGSTIN(dto.getSupplierGSTIN());

		String[] errorCodes = dto.getErrorCode().split(",");
		String[] errorDesc = dto.getErrorDesc().split(",");

		int i = 0;
		for (String errCode : errorCodes) {
			ErrorItem errDto = new ErrorItem();

			errDto.setErrorCode(errCode);
			errDto.setErrorDesc(errorDesc[i]);
			errorList.add(errDto);
			i++;
		}

		errItemObj.setErrorItems(errorList);
		docDto.setErrorDetails(errItemObj);
		return docDto;

	}
	
}
