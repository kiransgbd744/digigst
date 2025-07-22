package com.ey.advisory.app.gstr3b;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;
import com.ey.advisory.app.data.entities.client.Gstr2Avs3bReconEntity;
import com.ey.advisory.app.data.entities.client.Gstr2a2bVs3BbReqIdWiseDataDto;
import com.ey.advisory.app.data.repositories.client.Gstr2Avs3BReconRepository;
import com.ey.advisory.app.gstr1.einv.Gstr1EinvRequesIdWiseDownloadTabDto;
import com.ey.advisory.app.gstr1.einv.Gstr2Avs3BReqIdWiseDaoImpl;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconReqDto;
import com.ey.advisory.core.dto.Gstr2InitiateReconRequestIdsDto;
import com.google.gson.JsonObject;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * 
 * @author Sasidhar reddy
 *
 */
@Service("Gstr2avs3bProcProcessServiceImpl")
public class Gstr2avs3bProcProcessServiceImpl {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr1vs3bProcProcessServiceImpl.class);
	@Autowired
	@Qualifier(value = "Gstr2avs3bProcProcessDaoImpl")
	private Gstr2avs3bProcProcessDaoImpl daoImpl;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Gstr2Avs3BReconRepository")
	private Gstr2Avs3BReconRepository gstr2Avs3BReconRepository;

	@Autowired
	@Qualifier("Gstr2Avs3BReqIdWiseDaoImpl")
	private Gstr2Avs3BReqIdWiseDaoImpl gstr2Avs3BReqIdWiseDaoImpl;
	

	@Autowired
	private UserCreationRepository userRepo;

	public String fetchgstr2avs3bProc(
			final Gstr1VsGstr3bProcessSummaryReqDto criteria) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside InitiateReconserviceimpl" + " method";
			LOGGER.debug(msg);
		}
		try {

			String userName = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";

			LocalDateTime now = LocalDateTime.now();
			Integer derFromTaxPeriod;
			Integer derToTaxPeriod;

			Gstr2Avs3bReconEntity entity = new Gstr2Avs3bReconEntity();
			derFromTaxPeriod = GenUtil
					.convertTaxPeriodToInt(criteria.getTaxPeriodFrom());
			derToTaxPeriod = GenUtil
					.convertTaxPeriodToInt(criteria.getTaxPeriodTo());
			Set<String> gstinSet = new HashSet<>(
					criteria.getDataSecAttrs().get("GSTIN"));
			List<String> emailList = new ArrayList<>();
			List<Object[]> userRepo1 = userRepo.getUserInfoByEntityId(criteria.getEntityId().get(0));
			Map<String, String> userMap = new HashMap<>();

			for (Object[] userInfo : userRepo1) {
			    String username = (String) userInfo[0];
			    String email = (String) userInfo[1];
			    userMap.put(username, email);
			}
			//userName="P2001353321";
			String emailaddress = userMap.get(userName);
			entity.setGstins(GenUtil
					.convertStringToClob(convertToQueryFormat(gstinSet)));
			if (criteria.getEntityId() != null
					&& !criteria.getEntityId().isEmpty()) {
				entity.setEntityId(criteria.getEntityId().get(0));
			}
			entity.setNoOfGstin((long) gstinSet.size());
			entity.setInitiatedOn(now);
			entity.setInitiatedBy(userName);
			entity.setFromTaxPeriod(criteria.getTaxPeriodFrom());
			entity.setToTaxPeriod(criteria.getTaxPeriodTo());
			entity.setDerivedFromTaxPeriod(derFromTaxPeriod);
			entity.setDerivedToTaxPeriod(derToTaxPeriod);
			entity.setReconType(criteria.getReconType());
			entity.setEntityId(criteria.getEntityId().get(0));
			entity.setInitiatedByEmailId(emailaddress);
			entity.setStatus(ReconStatusConstants.RECON_INITIATED);
			gstr2Avs3BReconRepository.save(entity);
			Long requestId = entity.getRequestId();
			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("requestId", requestId);
		//	gSTR2aVs3BInitiateReconProcessorTest.execute(requestId);
			String groupCode = TenantContext.getTenantId();
			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR2A_VS_3b_INITIATE_RECON,
					jsonParams.toString(),
					userName, 1L, null, null);

		} catch (Exception ex) {
			String msg = "Exception occured in Gstr2a VS 3b InitiatRecon";
			LOGGER.error(msg, ex);
			return "failure";
		}

		return "Success";

	}

	private String convertToQueryFormat(Set<String> gstinset) {

		List<String> list = new ArrayList<String>();
		list.addAll(gstinset);

		String queryData = null;
		if (list == null || list.isEmpty())
			return null;

		queryData = list.get(0);
		for (int i = 1; i < list.size(); i++) {
			queryData += "," + list.get(i);
		}

		return queryData;
	}

	public List<Gstr2a2bVs3BbReqIdWiseDataDto> get2a2bVs3BbReqIdWiseScreenData(
			Gstr2InitiateReconReqDto reqDto, String userName) {

		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin get2a2bVs3BbReqIdWiseScreenData"
					+ ".getReportRequestStatus ,"
					+ "inside the get2a2bVs3BbReqIdWiseScreenData() method";
			LOGGER.debug(msg);
		}

		List<Gstr2a2bVs3BbReqIdWiseDataDto> response = gstr2Avs3BReqIdWiseDaoImpl
				.get2a2bvs3aReqIdWiseStatus(reqDto, userName);

		response.sort(
				Comparator
						.comparing(Gstr2a2bVs3BbReqIdWiseDataDto::getRequestId)
						.reversed());

		return response;
	}

	public List<Gstr1EinvRequesIdWiseDownloadTabDto> getGstr2a2bVs3bData(
			Long requestId) {

		Optional<Gstr2Avs3bReconEntity> findById = gstr2Avs3BReconRepository
				.findById(requestId);
		Gstr1EinvRequesIdWiseDownloadTabDto gstr1ReqIdWiseDownloDto = new Gstr1EinvRequesIdWiseDownloadTabDto();
		gstr1ReqIdWiseDownloDto.setFlag(false);
		gstr1ReqIdWiseDownloDto.setDocId(findById.get().getDocId());
		gstr1ReqIdWiseDownloDto.setReportName("2A2Bvs3B");
		if (gstr1ReqIdWiseDownloDto.getDocId() != null)
			gstr1ReqIdWiseDownloDto.setFlag(true);

		List<Gstr1EinvRequesIdWiseDownloadTabDto> responseList = new ArrayList<>();
		responseList.add(gstr1ReqIdWiseDownloDto);
		return responseList;
	}

	public List<Gstr2InitiateReconRequestIdsDto> getRequestIds(String userName,
			Long entityId, Gstr2InitiateReconReqDto reqDto) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin Gstr2avs3bProcProcessServiceImpl"
					+ ".getRequestIds ," + "inside the getRequestIds() method";
			LOGGER.debug(msg);
		}

		List<BigInteger> response = getGSTR2AVS3B2bVS3bRequestIds(userName,
				entityId, reqDto);

		return response.stream().map(o -> convertToRequestIdDto(o))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public List<BigInteger> getGSTR2AVS3B2bVS3bRequestIds(String userName,
			Long entityId,
			Gstr2InitiateReconReqDto reqDto)
			throws AppException {
		try {
			String initiationDateFrom = reqDto.getInitiationDateFrom();
			String initiationDateTo = reqDto.getInitiationDateTo();

			/*
			 * String queryString = "SELECT RRC.REQUEST_ID AS REQUEST_ID " +
			 * " FROM TBL_RECON_GSTR2A2B_VS_3B RRC " +
			 * " WHERE RRC.ENTITY_ID =:entityId AND " +
			 * " TO_VARCHAR(RRC.CREATED_DATE,'YYYY-MM-DD') " +
			 * " BETWEEN :initiationDateFrom  AND :initiationDateTo " +
			 * " ORDER BY 1 DESC";
			 */

			String queryString = "SELECT RRC.REQUEST_ID AS REQUEST_ID "
					+ "FROM TBL_RECON_GSTR2A2B_VS_3B RRC "
					+ "WHERE RRC.ENTITY_ID = :entityId "
					+ "AND TO_VARCHAR(RRC.CREATED_DATE, 'YYYY-MM-DD') BETWEEN :initiationDateFrom AND :initiationDateTo "
					+ "AND RRC.RECON_TYPE = 'GSTR2AVS3B' "
					+ "ORDER BY 1 DESC";

			Query q = entityManager.createNativeQuery(queryString);
			q.setParameter("entityId", entityId);
			q.setParameter("initiationDateFrom", initiationDateFrom);
			q.setParameter("initiationDateTo", initiationDateTo);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("executing query to get the data of RequestIds "
						+ ", entityId " + entityId + "and query = "
						+ queryString);
			}
			@SuppressWarnings("unchecked")
			List<BigInteger> list = q.getResultList();
			return list;
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new AppException("Exception Occured in "
					+ "Gstr2InitiateReconReportRequestStatusDaoImpl.getRequestIds");
		}
	}

	private Gstr2InitiateReconRequestIdsDto convertToRequestIdDto(
			BigInteger requestId) {
		Gstr2InitiateReconRequestIdsDto dto = new Gstr2InitiateReconRequestIdsDto();
		dto.setRequestId(requestId);
		return dto;
	}

}
