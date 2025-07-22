package com.ey.advisory.app.services.jobs.anx1;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.ClientResetEntity;
import com.ey.advisory.app.data.repositories.client.ClientResetRepository;
import com.ey.advisory.app.processors.handler.Gstr6SaveToGstnResetHandler;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1SaveToGstnReqDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("ClientResetUtil")
public class ClientResetUtil {

	@Autowired
	private ClientResetRepository clientResetRepo;
	
	public void createResetEntries(Pair<String, String> pair,
			Gstr1SaveToGstnReqDto dto, String resetType, String returnType) {

		String userName = SecurityContext.getUser().getUserPrincipalName();
		List<String> sections = dto.getTableSections();
		List<ClientResetEntity> entities = new ArrayList<>();
		for (String section : sections) {
			ClientResetEntity entity = new ClientResetEntity();
			entity.setGstin(pair.getValue0());
			entity.setTaxPeriod(pair.getValue1());
			entity.setResetType(resetType);
			entity.setSection(section);
			entity.setReturnType(returnType != null ? returnType.toUpperCase() : null);
			entity.setCreatedOn(
					EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			entity.setCreatedBy(userName);
			entities.add(entity);
		}
		clientResetRepo.saveAll(entities);

	}
}
