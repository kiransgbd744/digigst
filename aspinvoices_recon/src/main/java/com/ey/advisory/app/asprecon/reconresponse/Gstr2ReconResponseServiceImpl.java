package com.ey.advisory.app.asprecon.reconresponse;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.app.asprecon.gstr2.recon.result.Gstr2ReconResultReqDto;
import com.ey.advisory.app.data.entities.client.asprecon.ReconResultsUIGstr2AprEntity;
import com.ey.advisory.app.data.entities.client.asprecon.ReconResultsUIGstr2BprEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.ReconResultsUIGstr2AprRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.ReconResultsUIGstr2BprRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author sakshi.jain
 *
 */
@Slf4j
@Service("Gstr2ReconResponseServiceImpl")
public class Gstr2ReconResponseServiceImpl
		implements Gstr2ReconResponseService {

	@Autowired
	@Qualifier("Gstr2APRReconResponseDaoImpl")
	private Gstr2APRReconResponseDao daoImpl2APR;

	@Autowired
	@Qualifier("Gstr2BPRReconResponseDaoImpl")
	private Gstr2BPRReconResponseDao daoImpl2BPR;

	@Autowired
	@Qualifier("Gstr2ReconResponseProcCallDaoImpl")
	private Gstr2ReconResponseProcCallDao procCallDaoImpl;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	ReconResultsUIGstr2BprRepository recon2bprUiRepo;

	@Autowired
	ReconResultsUIGstr2AprRepository recon2aprUiRepo;

	@Autowired
	private EntityConfigPrmtRepository entityConfigRepo;

	@Override
	public Pair<List<Gstr2ReconResponseDashboardDto>, Integer> getReconResponseDashboardData(
			Gstr2ReconResultReqDto reqDto, int pageNum, int pageSize) {
		try {
			Pair<List<Gstr2ReconResponseDashboardDto>, Integer> resp = null;
			if ("2A_PR".equalsIgnoreCase(reqDto.getReconType()))
				resp = daoImpl2APR.getReconResponseData(reqDto, pageNum,
						pageSize);
			else
				resp = daoImpl2BPR.getReconResponseData(reqDto, pageNum,
						pageSize);
			return resp;
		} catch (Exception ex) {
			LOGGER.error("Error occured while retriving dashboard data", ex);
			throw new AppException(ex);
		}
	}

	@Override
	public String getLastReconTimeStamp(Long entityId, String reconType) {

		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName();

		String queryString = createQueryString(reconType);
		Query q1 = entityManager.createNativeQuery(queryString);

		q1.setParameter("entityId", entityId);
		q1.setParameter("user", Arrays.asList("SYSTEM", userName));
		@SuppressWarnings("unchecked")
		List<Timestamp> list = q1.getResultList();
		if(list.isEmpty())
			return "";
		Timestamp time = list.get(0);
		if(time == null)
			return "";
		LocalDateTime dt = time.toLocalDateTime();
		String dateTime = EYDateUtil
				.fmtDate(EYDateUtil.toISTDateTimeFromUTC(dt));

		return dateTime;
	}

	private String createQueryString(String reconType) {
		String query = null;
		if ("2B_PR".equalsIgnoreCase(reconType)) {
			query = " select max(completed_on) from TBL_RECON_REPORT_CONFIG "
					+ " where status in ('RECON_COMPLETED','REPORT_GENERATED') "
					+ " AND ENTITY_ID =:entityId AND CREATED_BY in (:user) "
					+ " AND RECON_TYPE like '%2BPR%';";

		} else {
			query = " select max(completed_on) from TBL_RECON_REPORT_CONFIG "
					+ " where status in ('RECON_COMPLETED','REPORT_GENERATED') "
					+ " AND ENTITY_ID  =:entityId AND CREATED_BY in (:user) "
					+ " AND RECON_TYPE like '%2APR%';";
		}
		return query;

	}

	@Override
	public List<Gstr2ReconResponseButtonReqDto> validations(
			Gstr2ReconResponseButtonReqDto reqDto) {
		try {
			List<Gstr2ReconResponseButtonReqDto> resp = new ArrayList<>();
			Map<String, String> docNumberValMap = new HashMap<>();

			List<Gstr2ReconResponseDashboardDto> respList = reqDto
					.getRespList();

			for (Gstr2ReconResponseDashboardDto dto : respList) {

				List<String> errorList = new ArrayList<>();

				if ("2A/6A".equalsIgnoreCase(dto.getSource())) {
					//is2ACFSValid(dto.getCfs(), dto.getDocNumber(), errorList);
					is2BTaxPeriodValid(dto.getReturnPeriod(),
							reqDto.getTaxPeriodGstr3b(), dto.getDocNumber(),
							"2A/PR", errorList);

				} else if ("PR".equalsIgnoreCase(dto.getSource())) {

					// PR tax period
					is2BTaxPeriodValid(dto.getReturnPeriod(),
							reqDto.getTaxPeriodGstr3b(), dto.getDocNumber(),
							"PR", errorList);

					String optedOption = "A";
					optedOption = onbrdOptionOpted(
							Long.valueOf(reqDto.getEntityId()));
					if(reqDto.getReconType().equalsIgnoreCase("2A_PR"))
					{
						isRevChrgPRValid(dto.getRcmFlag(), dto.getDocNumber(),
								errorList);
					}
					else if ("A".equalsIgnoreCase(optedOption) || "C".equalsIgnoreCase(optedOption))
					// RCH PR vali
					{
						//bug 163716
						/*isRevChrgPRValid(dto.getRcmFlag(), dto.getDocNumber(),
								errorList);*/
					}
				} else {
					is2BTaxPeriodValid(dto.getReturnPeriod(),
							reqDto.getTaxPeriodGstr3b(), dto.getDocNumber(),
							reqDto.getReconType(), errorList);
				}

				if (errorList.isEmpty())
					continue;
				else
					docNumberValMap.put(
							dto.getDocNumber() + "_" + dto.getSource(),
							String.join(",", errorList));
			}

			for (Map.Entry<String, String> entry : docNumberValMap.entrySet()) {

				Gstr2ReconResponseButtonReqDto errorDto = new Gstr2ReconResponseButtonReqDto();
				String[] arr = entry.getKey().split("_");
				errorDto.setType(arr[1]);
				errorDto.setDocNumberPR(arr[0]);

				errorDto.setErrorDesc(entry.getValue());
				resp.add(errorDto);
			}
			return resp;
		} catch (Exception ex) {
			LOGGER.error("Error occured while validation for 3B lock ", ex);
			throw new AppException(ex);
		}
	}

	private void isRevChrgPRValid(String revChrgPR, String docNumPr,
			List<String> resp) {
		if (Strings.isNullOrEmpty(revChrgPR)) {
			return;
		}
		if ("Y".equalsIgnoreCase(revChrgPR)) {
			resp.add(
					"Reverse Charge Flag(PR) - Y records cannot be locked under GSTR 3B");
			return;
		} else
			return;
	}

	private void is2ACFSValid(String revChrg2A, String docNum2A,
			List<String> resp) {
		if (Strings.isNullOrEmpty(revChrg2A)) {
			return;
		}
		if ("N".equalsIgnoreCase(revChrg2A)) {
			resp.add(
					"CFS Flag (2A) - N records cannot be locked under GSTR 3B");
			return;
		} else
			return;
	}

	private void is2BTaxPeriodValid(String taxPeriod2B2A, String taxPeriod3B,
			String docNumPr, String reconType, List<String> resp) {
		Integer taxPeriod2APR = Integer.parseInt(taxPeriod2B2A);

		Integer taxPeriod3BPR = Integer.parseInt(taxPeriod3B);

		if (taxPeriod2APR > taxPeriod3BPR) {
			resp.add(String.format(
					"Taxperiod %s cannot Be Beyond GSTR3B TaxPeriod",
					reconType));
			return;
		} else
			return;

	}

	@Override
	public List<Gstr2ReconResponseButtonReqDto> reconResponseLockProc(
			Gstr2ReconResponseButtonReqDto reqDto, Long batchId, Long fileId) {
		try {
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						" inside Gstr2ReconResultsServiceImpl for batchId %s",
						batchId.toString());
				LOGGER.debug(msg);
			}

			if (reqDto.getReconType().equalsIgnoreCase("2A_PR")) {
				List<ReconResultsUIGstr2AprEntity> recon2AprEntity = new ArrayList<>();
				List<Gstr2ReconResponseDashboardDto> respList = new ArrayList<>();

				respList = reqDto.getRespList();

				if (respList.isEmpty())
					return null;
				int identifier = 0;

				List<Gstr2ReconResponseDashboardDto> respList2A = respList
						.stream().filter(s -> s.getSource().equals("2A/6A"))
						.collect(Collectors.toList());

				List<Gstr2ReconResponseDashboardDto> respListPr = respList
						.stream().filter(s -> s.getSource().equals("PR"))
						.collect(Collectors.toList());

				if ((!respList2A.isEmpty() && respList2A.size() == 1
						&& !respListPr.isEmpty() && respListPr.size() == 1)
						|| (!respList2A.isEmpty() && respList2A.size() == 1
								&& respListPr.isEmpty())
						|| (respList2A.isEmpty() && !respListPr.isEmpty()
								&& respListPr.size() == 1)) {
					identifier = 1;
				}

				for (Gstr2ReconResponseDashboardDto dto : respList2A) {
					ReconResultsUIGstr2AprEntity entity2A = convertToEntity2A(
							reqDto, batchId, fileId,
							Long.valueOf(dto.getReconLinkId()), "2A",
							identifier);
					recon2AprEntity.add(entity2A);
				}

				for (Gstr2ReconResponseDashboardDto dto : respListPr) {
					ReconResultsUIGstr2AprEntity entityPR = convertToEntity2A(
							reqDto, batchId, fileId,
							Long.valueOf(dto.getReconLinkId()), "PR",
							identifier);

					recon2AprEntity.add(entityPR);
				}

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							" reconLinkidList %s for batchId %s",
							recon2AprEntity, batchId.toString());
					LOGGER.debug(msg);
				}

				List<List<ReconResultsUIGstr2AprEntity>> chunks = Lists
						.partition(recon2AprEntity, 2000);
				for (List<ReconResultsUIGstr2AprEntity> chunk : chunks) {
					recon2aprUiRepo.saveAll(chunk);
				}
			} else {
				
				List<Gstr2ReconResponseDashboardDto> respList = new ArrayList<>();
				
				respList = reqDto.getRespList();
				
				int identifier = 0;

				List<Gstr2ReconResponseDashboardDto> respList2B = respList
						.stream().filter(s -> s.getSource().equals("2B"))
						.collect(Collectors.toList());

				List<Gstr2ReconResponseDashboardDto> respListPr = respList
						.stream().filter(s -> s.getSource().equals("PR"))
						.collect(Collectors.toList());
				
				if ((!respList2B.isEmpty() && respList2B.size() == 1
						&& !respListPr.isEmpty() && respListPr.size() == 1)
						|| (!respList2B.isEmpty() && respList2B.size() == 1
								&& respListPr.isEmpty())
						|| (respList2B.isEmpty() && !respListPr.isEmpty()
								&& respListPr.size() == 1)) {
					identifier = 1;
				}

				List<ReconResultsUIGstr2BprEntity> recon2BprEntity = new ArrayList<>();

				for (Gstr2ReconResponseDashboardDto dto : respList2B) {
					ReconResultsUIGstr2BprEntity entity2A = convertToEntity2B(
							reqDto, batchId, fileId,
							Long.valueOf(dto.getReconLinkId()), "2B",
							identifier);
					recon2BprEntity.add(entity2A);
				}
				
				for (Gstr2ReconResponseDashboardDto dto : respListPr) {
					ReconResultsUIGstr2BprEntity entityPR = convertToEntity2B(
							reqDto, batchId, fileId,
							Long.valueOf(dto.getReconLinkId()), "PR",
							identifier);
					recon2BprEntity.add(entityPR);
				}
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							" reconLinkidList %s for batchId %s",
							recon2BprEntity, batchId.toString());
					LOGGER.debug(msg);
				}

				List<List<ReconResultsUIGstr2BprEntity>> chunks = Lists
						.partition(recon2BprEntity, 2000);
				for (List<ReconResultsUIGstr2BprEntity> chunk : chunks) {
					recon2bprUiRepo.saveAll(chunk);
				}
			}
			List<Gstr2ReconResponseButtonReqDto> resp = procCallDaoImpl
					.validateReconResponse(batchId, reqDto.getReconType(),
							fileId);
			return resp;

		} catch (Exception ex) {
			LOGGER.error("Error While proc call ", ex);
			throw new AppException(ex);
		}
	}

	private ReconResultsUIGstr2AprEntity convertToEntity2A(
			Gstr2ReconResponseButtonReqDto reqDto, Long batchId, Long fileId,
			Long reconLinkId, String source, int identifier) {
		ReconResultsUIGstr2AprEntity entity = new ReconResultsUIGstr2AprEntity();
		entity.setBatchId(batchId);
		entity.setReconLinkId(reconLinkId);
		entity.setRespRemarks(reqDto.getResponseRemarks() != null
				? StringUtils.truncate(reqDto.getResponseRemarks(), 500)
				: null);
		if (reqDto.getIndentifier().equalsIgnoreCase("Force")) {
			entity.setUserResp("Lock");
			entity.setFmResponse("Lock");
		} else if (reqDto.getIndentifier().equalsIgnoreCase("3B")) {
			entity.setUserResp(reqDto.getTaxPeriodGstr3b().substring(4,6)+reqDto.getTaxPeriodGstr3b().substring(0,4));
			entity.setRspTaxPeriod3B(reqDto.getTaxPeriodGstr3b().substring(4,6)+reqDto.getTaxPeriodGstr3b().substring(0,4));
		} /*
			 * entity.setRspTaxPeriod3B(reqDto.getTaxPeriodGstr3b() != null ?
			 * reqDto.getTaxPeriodGstr3b() : null);
			 */
		entity.setAvaiCgst(
				reqDto.getAvaiCgst() != null && !reqDto.getAvaiCgst().isEmpty()
						? new BigDecimal(reqDto.getAvaiCgst()) : null);
		entity.setAvaiSgst(
				reqDto.getAvaiSgst() != null && !reqDto.getAvaiSgst().isEmpty()
						? new BigDecimal(reqDto.getAvaiSgst()) : null);
		entity.setAvaiIgst(
				reqDto.getAvaiIgst() != null && !reqDto.getAvaiIgst().isEmpty()
						? new BigDecimal(reqDto.getAvaiIgst()) : null);
		entity.setAvaiCess(
				reqDto.getAvaiCess() != null && !reqDto.getAvaiCess().isEmpty()
						? new BigDecimal(reqDto.getAvaiCess()) : null);
		entity.setCreatedOn(LocalDateTime.now());
		entity.setSource(source);
		entity.setFileId(fileId);
		entity.setItcReversal(reqDto.getItcReversal());

		// identifier of multilock

		if (identifier == 0) {
			entity.setIdentifier("Multi");
		}
		return entity;

	}

	private ReconResultsUIGstr2BprEntity convertToEntity2B(
			Gstr2ReconResponseButtonReqDto reqDto, Long batchId, Long fileId,
			Long reconLinkId, String source, int identifier) {
		ReconResultsUIGstr2BprEntity entity = new ReconResultsUIGstr2BprEntity();
		entity.setBatchId(batchId);
		entity.setReconLinkId(reconLinkId);
		entity.setRespRemarks(reqDto.getResponseRemarks() != null
				? StringUtils.truncate(reqDto.getResponseRemarks(), 500)
				: null);
		if (reqDto.getIndentifier().equalsIgnoreCase("Force")) {
			entity.setUserResp("Lock");
			entity.setFmResponse("Lock");
		} else if (reqDto.getIndentifier().equalsIgnoreCase("3B")) {
			entity.setUserResp(reqDto.getTaxPeriodGstr3b().substring(4,6)+reqDto.getTaxPeriodGstr3b().substring(0,4));
			entity.setRspTaxPeriod3B(reqDto.getTaxPeriodGstr3b().substring(4,6)+reqDto.getTaxPeriodGstr3b().substring(0,4));
		} /*
			 * entity.setRspTaxPeriod3B(reqDto.getTaxPeriodGstr3b() != null ?
			 * reqDto.getTaxPeriodGstr3b() : null);
			 */
		entity.setAvaiCgst(
				reqDto.getAvaiCgst() != null && !reqDto.getAvaiCgst().isEmpty()
						? new BigDecimal(reqDto.getAvaiCgst()) : null);
		entity.setAvaiSgst(
				reqDto.getAvaiSgst() != null && !reqDto.getAvaiSgst().isEmpty()
						? new BigDecimal(reqDto.getAvaiSgst()) : null);
		entity.setAvaiIgst(
				reqDto.getAvaiIgst() != null && !reqDto.getAvaiIgst().isEmpty()
						? new BigDecimal(reqDto.getAvaiIgst()) : null);
		entity.setAvaiCess(
				reqDto.getAvaiCess() != null && !reqDto.getAvaiCess().isEmpty()
						? new BigDecimal(reqDto.getAvaiCess()) : null);
		entity.setCreatedOn(LocalDateTime.now());
		entity.setSource(source);
		entity.setFileId(fileId);
		entity.setItcReversal(reqDto.getItcReversal());

		if (identifier == 0) {
			entity.setIdentifier("Multi");
		}

		return entity;

	}

	private String onbrdOptionOpted(Long entityId) {
		String optAns = entityConfigRepo.findAnsbyQuestion(entityId,
				"What is the base for computing GSTR-3B values for Table 4- Eligible ITC");
		return optAns;
	}

}
