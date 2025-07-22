package com.ey.advisory.app.anx2.initiaterecon;

import static com.ey.advisory.common.CollectionAlignmentUtil.alignListsAndTransform;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.gstnapi.services.GSTNAuthTokenService;
import com.ey.advisory.gstr2.userdetails.EntityService;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Anx2ReconResponseServiceImpl")
public class Anx2ReconResponseServiceImpl implements Anx2ReconResponseService {

	@Autowired
	@Qualifier("Anx2ReconResponseDaoImpl")
	Anx2ReconResponseDao anx2ReconResponseDao;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	GSTNAuthTokenService authTokenService;

	@Autowired
	@Qualifier("EntityServiceImpl")
	EntityService entityService;

	String userResponses[] = { "A1", "A2", "A3", "P1", "R1", "R1U1", "R1U2",
			"A1U1", "A1U2", "A4", "C1", "U1", "U2" };

	@Override
	public Anx2ReconResponseDTO getReconResponse(List<String> gstins,
			String userName, List<String> tableType, List<String> docType) {
		Anx2ReconResponseDTO dto = new Anx2ReconResponseDTO();
		List<Anx2ReconRespResultSetDataDTO> a2Result = null;
		List<Anx2ReconRespResultSetDataDTO> prResult = null;
		List<Anx2ReconRespResultSetDataDTO> praResult = null;

		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin Anx2ReconResponseServiceImpl.getReconResponse";
			LOGGER.debug(msg);
		}
		try {

			Map<String, String> authTokenStatusMap = authTokenService
					.getAuthTokenStatusForGstins(gstins);

			Map<String, String> stateNamesMap = entityService
					.getStateNames(gstins);

			a2Result = anx2ReconResponseDao.getA2ReconData(gstins, userName,
					tableType, docType, userResponses.toString());
			prResult = anx2ReconResponseDao.getPRReconData(gstins, userName,
					tableType, docType, userResponses.toString());
			praResult = anx2ReconResponseDao.getPRAReconData(gstins, userName,
					tableType, docType, userResponses.toString());

			if ((a2Result != null && a2Result.size() > 0)
					|| (prResult != null && prResult.size() > 0)
					|| (praResult != null && praResult.size() > 0)) {
				dto.setSummaryData(createSummaryResponse(a2Result, prResult,
						praResult, gstins));
				dto.setDetailsData(createDetailedResponse(a2Result, prResult,
						praResult, gstins));

			}
			
			
		
			dto = createEmptyResponse(dto, gstins);
			

			dto.setSummaryData(dto.getSummaryData().stream()
					.map(obj -> obj.copy(obj,
							authTokenStatusMap.get(obj.getGstin()),
							stateNamesMap.get(obj.getGstin())))
					.collect(Collectors.toList()));

			dto.getDetailsData().stream().forEach(obj -> {
				obj.setAuthStatus(authTokenStatusMap.get(obj.getGstin()));
				obj.setStateName(stateNamesMap.get(obj.getGstin()));
			});
		} catch (Exception e) {
			LOGGER.error("Error in Anx2ReconResponseServiceImpl", e);
		}

		if (LOGGER.isDebugEnabled()) {
			String msg = "Exit Anx2ReconResponseServiceImpl.getReconResponse";
			LOGGER.debug(msg);
		}
		return dto;

	}

	private Anx2ReconResponseDTO createEmptyResponse(Anx2ReconResponseDTO dto,
			List<String> gstins) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin Anx2ReconResponseServiceImpl.createEmptyResponse";
			LOGGER.debug(msg);
		}
		List<Anx2ReconResponseTableDTO> summaryData = dto.getSummaryData();
		List<Anx2ReconResponseDetailsDTO> detailsData = dto.getDetailsData();
		
		if ((summaryData != null && !summaryData.isEmpty())
				|| (detailsData != null && !detailsData.isEmpty())) {
			for (String gstin : gstins) {
				if (summaryData != null && !summaryData.isEmpty()) {
					boolean isSumPresent = summaryData.stream()
							.noneMatch(e -> e.getGstin().matches(gstin));
					if (isSumPresent) {
						summaryData.add(new Anx2ReconResponseTableDTO(gstin, "",
								BigDecimal.ZERO));
					}
				}
				if (detailsData != null && !detailsData.isEmpty()) {
					boolean isDetPresent = detailsData.stream()
							.noneMatch(e -> e.getGstin().matches(gstin));
					if (isDetPresent) {
						detailsData.add(new Anx2ReconResponseDetailsDTO(gstin,
								null, null, null));
					}
				}
			}
		}else{

		summaryData = gstins.stream().collect(Collectors.mapping(
				e -> new Anx2ReconResponseTableDTO(e, "", BigDecimal.ZERO),
				Collectors.toList()));

		detailsData = gstins.stream().collect(Collectors.mapping(
				e -> new Anx2ReconResponseDetailsDTO(e), Collectors.toList()));
		}

		dto.setSummaryData(summaryData);
		dto.setDetailsData(detailsData);
		if (LOGGER.isDebugEnabled()) {
			String msg = "Exit Anx2ReconResponseServiceImpl.createEmptyResponse";
			LOGGER.debug(msg);
		}
		return dto;
	}

	private List<Anx2ReconResponseDetailsDTO> createDetailedResponse(
			List<Anx2ReconRespResultSetDataDTO> a2Result,
			List<Anx2ReconRespResultSetDataDTO> prResult,
			List<Anx2ReconRespResultSetDataDTO> praResult,
			List<String> gstins) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin Anx2ReconResponseServiceImpl.createDetailedResponse";
			LOGGER.debug(msg);
		}
		return alignAndTransform(a2Result, prResult, praResult, this::merge);

	}

	private <T> List<T> alignAndTransform(
			List<Anx2ReconRespResultSetDataDTO> a2Result,
			List<Anx2ReconRespResultSetDataDTO> prResult,
			List<Anx2ReconRespResultSetDataDTO> praResult,
			Function<List<Anx2ReconResponseTableDTO>, T> operation) {

		List<Anx2ReconResponseTableDTO> a2Resp = transform(a2Result);
		List<Anx2ReconResponseTableDTO> prResp = transform(prResult);
		List<Anx2ReconResponseTableDTO> praResp = transform(praResult);

		List<List<Anx2ReconResponseTableDTO>> allLists = ImmutableList
				.of(a2Resp, prResp, praResp);

		List<T> finalList = alignListsAndTransform(allLists, e -> e.getGstin(),
				operation);
		if (LOGGER.isDebugEnabled()) {
		String msg = "Exit Anx2ReconResponseServiceImpl.alignAndTransform";
		LOGGER.debug(msg);
		}
		return finalList;

	}

	private List<Anx2ReconResponseTableDTO> createSummaryResponse(
			List<Anx2ReconRespResultSetDataDTO> a2Result,
			List<Anx2ReconRespResultSetDataDTO> prResult,
			List<Anx2ReconRespResultSetDataDTO> praResult,
			List<String> gstins) {

		return alignAndTransform(a2Result, prResult, praResult, this::choose);
	}

	private List<Anx2ReconResponseTableDTO> transform(
			List<Anx2ReconRespResultSetDataDTO> list) {
		return list.stream()
				.collect(Collectors.groupingBy(e -> e.getGstin(),
						Collectors.reducing(new Anx2ReconResponseTableDTO(),
								e -> new Anx2ReconResponseTableDTO(e.getGstin(),
										e.getUserResponse(), e.getTaxPayable()),
								(e1, e2) -> e1.add(e2))))
				.values().stream()
				.sorted(Comparator.comparing(e -> e.getGstin()))
				.collect(Collectors.toList());
	}

	private Anx2ReconResponseTableDTO choose(
			List<Anx2ReconResponseTableDTO> objs) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin Anx2ReconResponseServiceImpl.choose";
			LOGGER.debug(msg);
		}
		Anx2ReconResponseTableDTO a2Obj = objs.get(0);
		Anx2ReconResponseTableDTO prObj = objs.get(1);
		Anx2ReconResponseTableDTO praObj = objs.get(2);

		if (a2Obj == null && prObj == null & praObj == null)
			return null;
		String gstin = (a2Obj != null) ? a2Obj.getGstin()
				: ((prObj != null) ? prObj.getGstin() : praObj.getGstin());
		
		
		return new Anx2ReconResponseTableDTO(gstin,
				(a2Obj != null ? a2Obj.getA1() : BigDecimal.ZERO),
				(praObj != null ? praObj.getA2() : BigDecimal.ZERO),
				(prObj != null ? prObj.getA3() : BigDecimal.ZERO),
				(a2Obj != null ? a2Obj.getP1() : BigDecimal.ZERO),
				(a2Obj != null ? a2Obj.getR1() : BigDecimal.ZERO),
				(praObj != null ? praObj.getR1u1() : BigDecimal.ZERO),
				(prObj != null ? prObj.getR1u2() : BigDecimal.ZERO),
				(praObj != null ? praObj.getA1u1() : BigDecimal.ZERO),
				(prObj != null ? prObj.getA1u2() : BigDecimal.ZERO),
				(praObj != null ? praObj.getU1() : BigDecimal.ZERO),
				(prObj != null ? prObj.getU2() : BigDecimal.ZERO),
				(a2Obj != null ? a2Obj.getNaA2() : BigDecimal.ZERO),
				(prObj != null ? prObj.getNaPr() : BigDecimal.ZERO),
				(prObj != null ? prObj.getNaRc() : BigDecimal.ZERO));
	}

	private Anx2ReconResponseDetailsDTO merge(
			List<Anx2ReconResponseTableDTO> objs) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin Anx2ReconResponseServiceImpl.merge";
			LOGGER.debug(msg);
		}
		Anx2ReconResponseDetailsDTO dto = new Anx2ReconResponseDetailsDTO();

		Anx2ReconResponseTableDTO a2Obj = objs.get(0);
		Anx2ReconResponseTableDTO prObj = objs.get(1);
		Anx2ReconResponseTableDTO praObj = objs.get(2);

		String gstin = (a2Obj != null) ? a2Obj.getGstin()
				: ((prObj != null) ? prObj.getGstin() : praObj.getGstin());
		if (a2Obj == null)
			a2Obj = new Anx2ReconResponseTableDTO(gstin);
		if (prObj == null)
			prObj = new Anx2ReconResponseTableDTO(gstin);
		if (praObj == null)
			praObj = new Anx2ReconResponseTableDTO(gstin);
		dto.setGstin(gstin);
		dto.setA2(a2Obj);
		dto.setPr(prObj);
		dto.setPra(praObj);

		return dto;
	}

}
