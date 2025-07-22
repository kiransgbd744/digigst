package com.ey.advisory.app.docs.service.gstr6;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.Gstr6UserInputEntity;
import com.ey.advisory.app.data.repositories.client.Gstr6UserInputRepo;
import com.ey.advisory.app.docs.dto.Anx1ReportSearchReqDto;
import com.ey.advisory.app.docs.dto.DocSeriesSDeleteReqDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6DeterminationResponseDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.search.SearchResult;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("Gstr6DeterminationServiceImpl")
@Slf4j
public class Gstr6DeterminationServiceImpl
		implements Gstr6DeterminationService {

	@Autowired
	@Qualifier("Gstr6DeterminationDaoImpl")
	private Gstr6DeterminationDao gstr6DeterminationDao;

	@Autowired
	@Qualifier("Gstr6TurnOverDaoImpl")
	private Gstr6DeterminationDao gstr6TurnoverDao;

	@Autowired
	@Qualifier("Gstr6UserInputRepo")
	private Gstr6UserInputRepo gstr6UserInputRepo;

	@Override
	public SearchResult<Gstr6DeterminationResponseDto> getGstr6Determinationvalues(
			Anx1ReportSearchReqDto reqDto) {

		try {
			LOGGER.debug(
					"Enter into service class of GSTR6 Determination class",
					Gstr6DeterminationServiceImpl.class);
			SearchResult<Gstr6DeterminationResponseDto> gstr6DataResponse = gstr6DeterminationDao
					.gstr6DeterminationDetails(reqDto);
			LOGGER.debug("End service class of GSTR6 Determination class",
					Gstr6DeterminationServiceImpl.class);

			return gstr6DataResponse;

		} catch (Exception e) {
			LOGGER.debug("Exception occuring in GSTR6 Service calss", e);
			throw new AppException(e);
		}

	}

	@Override
	public SearchResult<Gstr6DeterminationResponseDto> getGstr6TurnOvervalues(
			Anx1ReportSearchReqDto reqDto) {
		try {
			LOGGER.debug(
					"Enter into service class of GSTR6 Determination class",
					Gstr6DeterminationServiceImpl.class);
			SearchResult<Gstr6DeterminationResponseDto> gstr6DataResponse = gstr6TurnoverDao
					.gstr6TurnOverDetails(reqDto);
			LOGGER.debug("End service class of GSTR6 Determination class",
					Gstr6DeterminationServiceImpl.class);
			return gstr6DataResponse;

		} catch (Exception e) {
			LOGGER.debug("Exception occuring in GSTR6 Service calss", e);
			throw new AppException(e);
		}
	}

	@Override
	public void persistData(Gstr6DeterminationResponseDto dto) {
		try {
			Gstr6UserInputEntity gstr6UserInput = new Gstr6UserInputEntity();
			gstr6UserInput.setGstin(dto.getGstin());
			gstr6UserInput.setStateName(dto.getState());
			gstr6UserInput.setGetGstr1Status(dto.getGetGstr1Status());
			gstr6UserInput.setComputeDigiVal(dto.getTurnoverDigiGST());
			gstr6UserInput.setComputeGstnVal(dto.getTurnoverGstn());
			gstr6UserInput.setIsdGstin(dto.getIsdGstin());
			gstr6UserInput.setCurrentRetPer(dto.getCurrentRetPer());
			gstr6UserInput.setUserInput(dto.getTurnoverUserEdited());
			if (dto.getId() != null) {
				Optional<Gstr6UserInputEntity> findById = gstr6UserInputRepo
						.findById(Long.valueOf(dto.getId()));
				if (findById.isPresent()) {
					gstr6UserInput.setId(findById.get().getId());
					gstr6UserInputRepo.save(gstr6UserInput);
				}
			} else {
				gstr6UserInputRepo.save(gstr6UserInput);
			}
		} catch (Exception e) {
			LOGGER.debug(
					"Exception occuring while saving Data of GStr6 Turn Over",
					e);
			throw new AppException(e);
		}

	}

	@Override
	public void deleteData(DocSeriesSDeleteReqDto dtos) {

		List<Long> ids = dtos.getId();
		List<List<Long>> docIdChunks = Lists.partition(ids, 2000);
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName() : "SYSTEM";
		docIdChunks.forEach(docIdChunk -> {
			if (LOGGER.isDebugEnabled()) {
				String msg = String
						.format("List of DocIds which are about to get "
								+ "soft delete: %s", docIdChunk);
				LOGGER.debug(msg);
			}
			gstr6UserInputRepo.recordsDeletionByIds(docIdChunk,
					LocalDateTime.now(), userName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Documents soft deleted successfully");
			}
		});

	}
}
