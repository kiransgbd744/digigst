/**
 * 
 */
package com.ey.advisory.app.services.itc04;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.app.docs.dto.Itc04DocDetailsSaveRespDto;
import com.ey.advisory.app.docs.dto.Itc04DocErrorDetailsDto;
import com.ey.advisory.common.GSTConstants;

/**
 * @author Laxmi.Salukuti
 *
 */
@Service("Itc04DocSaveResp")
public class Itc04DocSaveResp {

	public List<Itc04DocDetailsSaveRespDto> createItc04SaveAPIResponse(
			List<Long> oldDocIds, List<Itc04HeaderEntity> savedDocs,
			Map<String, List<Itc04DocErrorDetailsDto>> errorMap) {

		List<Itc04DocDetailsSaveRespDto> docSaveRespDtos = new ArrayList<>();

		if (oldDocIds.isEmpty()) {// SCI
			savedDocs.forEach(savedDocument -> {
				List<Itc04DocErrorDetailsDto> errors = new ArrayList<>();
				Itc04DocDetailsSaveRespDto docSaveRespDto = new Itc04DocDetailsSaveRespDto();
				docSaveRespDto.setId(savedDocument.getId());
				docSaveRespDto
						.setSupplierGstin(savedDocument.getSupplierGstin());
				docSaveRespDto.setTableNumber(savedDocument.getTableNumber());
				docSaveRespDto.setRetPeriod(savedDocument.getRetPeriod());
				docSaveRespDto.setAccountingVoucherNumber(
						savedDocument.getAccountingVoucherNumber());
				docSaveRespDto.setCompanyCode(savedDocument.getCompanyCode());
				docSaveRespDto.setPayloadId(savedDocument.getPayloadId());

				if (savedDocument.getTableNumber() != null
						&& GSTConstants.TABLE_NUMBER_4.equalsIgnoreCase(
								savedDocument.getTableNumber())) {

					docSaveRespDto.setDeliveryChallanaNumber(
							savedDocument.getDeliveryChallanaNumber());
					docSaveRespDto.setFyDcDate(savedDocument.getFyDcDate());

				} else if ((savedDocument.getTableNumber() != null
						&& GSTConstants.TABLE_NUMBER_5A.equalsIgnoreCase(
								savedDocument.getTableNumber()))
						|| (savedDocument.getTableNumber() != null
								&& GSTConstants.TABLE_NUMBER_5B
										.equalsIgnoreCase(savedDocument
												.getTableNumber()))) {

					docSaveRespDto.setDeliveryChallanaNumber(
							savedDocument.getDeliveryChallanaNumber());
					docSaveRespDto.setFyDcDate(savedDocument.getFyDcDate());
					docSaveRespDto.setJwDeliveryChallanaNumber(
							savedDocument.getJwDeliveryChallanaNumber());
					docSaveRespDto.setFyjwDcDate(savedDocument.getFyjwDcDate());
					docSaveRespDto.setJobWorkerGstin(
							savedDocument.getJobWorkerGstin());
					docSaveRespDto.setJobWorkerStateCode(
							savedDocument.getJobWorkerStateCode());

				} else if (savedDocument.getTableNumber() != null
						&& GSTConstants.TABLE_NUMBER_5C.equalsIgnoreCase(
								savedDocument.getTableNumber())) {

					docSaveRespDto.setInvNumber(savedDocument.getInvNumber());
					docSaveRespDto.setFyInvDate(savedDocument.getFyInvDate());
					docSaveRespDto.setDeliveryChallanaNumber(
							savedDocument.getDeliveryChallanaNumber());
					docSaveRespDto.setFyDcDate(savedDocument.getFyDcDate());
					docSaveRespDto.setJobWorkerGstin(
							savedDocument.getJobWorkerGstin());
					docSaveRespDto.setJobWorkerStateCode(
							savedDocument.getJobWorkerStateCode());
				}

				if (errorMap.containsKey(savedDocument.getDocKey())) {
					List<Itc04DocErrorDetailsDto> errorDto = errorMap
							.get(savedDocument.getDocKey());
					errorDto.forEach(error -> {

						Itc04DocErrorDetailsDto docErrorDto = new Itc04DocErrorDetailsDto();
						docErrorDto.setErrorCode(error.getErrorCode());
						docErrorDto.setErrorDesc(error.getErrorDesc());
						docErrorDto.setErrorType(error.getErrorType());
						docErrorDto.setErrorFields(error.getErrorFields());
						errors.add(docErrorDto);
					});

				}
				docSaveRespDto.setErrors(errors);
				docSaveRespDtos.add(docSaveRespDto);
			});
		} else {// UI
			IntStream.range(0, savedDocs.size()).forEach(i -> {
				Itc04DocDetailsSaveRespDto docSaveRespDto = new Itc04DocDetailsSaveRespDto();
				Itc04HeaderEntity savedDocument = savedDocs.get(i);
				List<Itc04DocErrorDetailsDto> errors = new ArrayList<>();
				docSaveRespDto.setId(savedDocument.getId());
				docSaveRespDto.setOldId(oldDocIds.get(i));
				docSaveRespDto
						.setSupplierGstin(savedDocument.getSupplierGstin());
				docSaveRespDto.setTableNumber(savedDocument.getTableNumber());
				docSaveRespDto.setRetPeriod(savedDocument.getRetPeriod());
				docSaveRespDto.setAccountingVoucherNumber(
						savedDocument.getAccountingVoucherNumber());
				docSaveRespDto.setCompanyCode(savedDocument.getCompanyCode());
				docSaveRespDto.setPayloadId(savedDocument.getPayloadId());

				if (savedDocument.getTableNumber() != null
						&& GSTConstants.TABLE_NUMBER_4.equalsIgnoreCase(
								savedDocument.getTableNumber())) {

					docSaveRespDto.setDeliveryChallanaNumber(
							savedDocument.getDeliveryChallanaNumber());
					docSaveRespDto.setFyDcDate(savedDocument.getFyDcDate());

				} else if ((savedDocument.getTableNumber() != null
						&& GSTConstants.TABLE_NUMBER_5A.equalsIgnoreCase(
								savedDocument.getTableNumber()))
						|| (savedDocument.getTableNumber() != null
								&& GSTConstants.TABLE_NUMBER_5B
										.equalsIgnoreCase(savedDocument
												.getTableNumber()))) {

					docSaveRespDto.setDeliveryChallanaNumber(
							savedDocument.getDeliveryChallanaNumber());
					docSaveRespDto.setFyDcDate(savedDocument.getFyDcDate());
					docSaveRespDto.setJwDeliveryChallanaNumber(
							savedDocument.getJwDeliveryChallanaNumber());
					docSaveRespDto.setFyjwDcDate(savedDocument.getFyjwDcDate());
					docSaveRespDto.setJobWorkerGstin(
							savedDocument.getJobWorkerGstin());
					docSaveRespDto.setJobWorkerStateCode(
							savedDocument.getJobWorkerStateCode());

				} else if (savedDocument.getTableNumber() != null
						&& GSTConstants.TABLE_NUMBER_5C.equalsIgnoreCase(
								savedDocument.getTableNumber())) {

					docSaveRespDto.setInvNumber(savedDocument.getInvNumber());
					docSaveRespDto.setFyInvDate(savedDocument.getFyInvDate());
					docSaveRespDto.setDeliveryChallanaNumber(
							savedDocument.getDeliveryChallanaNumber());
					docSaveRespDto.setFyDcDate(savedDocument.getFyDcDate());
					docSaveRespDto.setJobWorkerGstin(
							savedDocument.getJobWorkerGstin());
					docSaveRespDto.setJobWorkerStateCode(
							savedDocument.getJobWorkerStateCode());
				}

				if (errorMap.containsKey(savedDocument.getDocKey())) {
					List<Itc04DocErrorDetailsDto> errorDto = errorMap
							.get(savedDocument.getDocKey());
					errorDto.forEach(error -> {

						Itc04DocErrorDetailsDto docErrorDto = new Itc04DocErrorDetailsDto();
						docErrorDto.setErrorCode(error.getErrorCode());
						docErrorDto.setErrorDesc(error.getErrorDesc());
						docErrorDto.setErrorType(error.getErrorType());
						docErrorDto.setErrorFields(error.getErrorFields());
						errors.add(docErrorDto);
					});

				}
				/*
				 * errorDto.forEach(error -> { Itc04DocErrorDetailsDto
				 * docErrorDto = new Itc04DocErrorDetailsDto();
				 * 
				 * docErrorDto.setErrorCode(error.getErrorCode());
				 * docErrorDto.setErrorDesc(error.getErrorDesc());
				 * docErrorDto.setErrorType(error.getErrorType());
				 * docErrorDto.setErrorFields(error.getErrorFields());
				 * errors.add(docErrorDto); });
				 */
				docSaveRespDto.setErrors(errors);
				docSaveRespDtos.add(docSaveRespDto);
			});
		}
		return docSaveRespDtos;
	}
}
