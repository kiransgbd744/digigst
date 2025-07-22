package com.ey.advisory.app.processors.handler;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.Gstr6DistributionRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.services.jobs.anx1.ClientResetUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1SaveToGstnReqDto;

import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("Gstr6SaveToGstnResetHandler")
public class Gstr6SaveToGstnResetHandler {

	@Autowired
	private ClientResetUtil clientResetUtil;

	@Autowired
	private Gstr6DistributionRepository distributionRepo;

	@Autowired
	private InwardTransDocRepository inwardDocRepo;

	public void resetAspTableData(Pair<String, String> pair,
			Gstr1SaveToGstnReqDto dto, String resetType) {

		List<String> sections = dto.getTableSections();
		if (sections != null && !Collections.isEmpty(sections)) {
			clientResetUtil.createResetEntries(pair, dto, resetType,
					APIConstants.GSTR6.toUpperCase());
		}
		for (String section : sections) {
			if (APIConstants.ISD.equalsIgnoreCase(section.toLowerCase())
					|| APIConstants.ISDA
							.equalsIgnoreCase(section.toLowerCase())) {
				// Summary sections are not required update/reset
				distributionRepo.resetSaveGstr6AuditColumns(pair.getValue0(),
						pair.getValue1());
			} else {
				ArrayList<String> bifurcations = new ArrayList<String>();
				bifurcations.add(section.toUpperCase());
				inwardDocRepo.resetSaveGstr6AuditColumns(pair.getValue0(),
						pair.getValue1(), bifurcations);
			}

		}

	}

}
