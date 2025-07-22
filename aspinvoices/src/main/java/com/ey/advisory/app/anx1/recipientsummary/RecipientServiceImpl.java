package com.ey.advisory.app.anx1.recipientsummary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.common.AppException;


/**
 * @author Arun KA
 *
 */

@Component("RecipientServiceImpl")
public class RecipientServiceImpl implements RecipientService {

	@Autowired
	@Qualifier("entityInfoRepository")
	EntityInfoRepository entityInfoRepo;

	@Autowired
	@Qualifier("RecipientDaoImpl")
	RecipientDao recipientDao;

	@Override
	public List<String> getCgstinsForEntity(Long entityId) {

		Objects.nonNull(entityId);
		Optional<EntityInfoEntity> optional = entityInfoRepo.findById(entityId);
		if (!optional.isPresent()) {
			String msg = String.format("Invalid Entity : %d", entityId);
			throw new AppException(msg);
		}

		EntityInfoEntity entity = optional.get();
		String sPan = entity.getPan();

		return recipientDao.getCgstinsForEntity(sPan);

	}

	@Override
	public Map<String, String> getCNamesForCPans(List<String> cPans) {

		return recipientDao.getCNamesForCPans(cPans);

	}

	
	public List<RecipientSummaryFilterDto> getCgstinsForGstins(
			List<String> gstins, String taxPeriod) {

		List<CgstinSgstinDto> cgstinSgstinDtoList = recipientDao
				.getCgstinsForGstins(gstins, taxPeriod);
		Map<String, List<CgstinSgstinDto>> cgstinSgstinMap = cgstinSgstinDtoList
				.stream().collect(Collectors.groupingBy(o -> o.getSGstin()));
		List<RecipientSummaryFilterDto> resList = new ArrayList<>();
		cgstinSgstinMap.forEach((k, v) -> {
			resList.add(new RecipientSummaryFilterDto(k, v));
		});

		return resList;

	}

}
