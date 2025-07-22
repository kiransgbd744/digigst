/**
 * 
 */
package com.ey.advisory.admin.data.erp;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.ErpEventsScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.ErpScenarioPermissionRepository;
import com.ey.advisory.admin.data.repositories.client.SftpScenarioPermissionRepository;
import com.ey.advisory.core.dto.ErpPermissionDeleteDto;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("ERPPermissionServiceImpl")
public class ERPPermissionServiceImpl implements ERPPermissionService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ERPPermissionServiceImpl.class);
	@Autowired
	@Qualifier("ErpScenarioPermissionRepository")
	private ErpScenarioPermissionRepository erpScPermissionRepository;

	@Autowired
	@Qualifier("ErpEventsScenarioPermissionRepository")
	private ErpEventsScenarioPermissionRepository permRepo;

	@Autowired
	@Qualifier("SftpScenarioPermissionRepository")
	private SftpScenarioPermissionRepository sftpRepository;

	@Override
	public void deleteErpPermissionDetails(
			List<ErpPermissionDeleteDto> erpDelPerdto) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"ERPPermissionServiceImpl deleteErpPermissionDetails Begin");
		}
		List<ErpPermissionDeleteDto> saveErpdtoList = new ArrayList<>();
		List<ErpPermissionDeleteDto> saveEventsErpdtoList = new ArrayList<>();
		List<ErpPermissionDeleteDto> saveSftpErpdtoList = new ArrayList<>();
		for (ErpPermissionDeleteDto dto : erpDelPerdto) {
			if ("B".equalsIgnoreCase(dto.getJobType())) {
				List<Long> gstinIds = dto.getGstnsId();
				for (Long gstinId : gstinIds) {
					ErpPermissionDeleteDto delDeto = new ErpPermissionDeleteDto();
					delDeto.setGstnId(gstinId);
					delDeto.setEntityId(dto.getEntityId());
					delDeto.setErpId(dto.getErpId());
					delDeto.setScenarioId(dto.getScenarioId());
					saveErpdtoList.add(delDeto);
				}
			} else if ("E".equalsIgnoreCase(dto.getJobType())) {
				ErpPermissionDeleteDto delDeto = new ErpPermissionDeleteDto();
				delDeto.setErpId(dto.getErpId());
				delDeto.setScenarioId(dto.getScenarioId());
				delDeto.setDestName(dto.getDestName());
				saveEventsErpdtoList.add(delDeto);
			} else {
				ErpPermissionDeleteDto delDeto = new ErpPermissionDeleteDto();
				delDeto.setErpId(dto.getErpId());
				delDeto.setScenarioId(dto.getScenarioId());
				saveSftpErpdtoList.add(delDeto);
			}
		}
		if (!saveErpdtoList.isEmpty()) {
			saveErpdtoList.forEach(erpRegistrationReqDto -> {
				if (erpRegistrationReqDto.getEntityId() != null
						&& erpRegistrationReqDto.getEntityId() > 0
						&& erpRegistrationReqDto.getGstnId() != null
						&& erpRegistrationReqDto.getGstnId() > 0
						&& erpRegistrationReqDto.getErpId() != null
						&& erpRegistrationReqDto.getErpId() > 0
						&& erpRegistrationReqDto.getScenarioId() != null
						&& erpRegistrationReqDto.getScenarioId() > 0) {
					erpScPermissionRepository.deleterecord(
							erpRegistrationReqDto.getGstnId(),
							erpRegistrationReqDto.getErpId(),
							erpRegistrationReqDto.getEntityId(),
							erpRegistrationReqDto.getScenarioId());
				}
			});
		}
		if (!saveEventsErpdtoList.isEmpty()) {
			saveEventsErpdtoList.forEach(saveEventsErpdto -> {
				permRepo.updateEventsScenarioPerm(saveEventsErpdto.getErpId(),
						saveEventsErpdto.getScenarioId(),
						saveEventsErpdto.getDestName());
			});
		}
		if (!saveSftpErpdtoList.isEmpty()) {
			saveSftpErpdtoList.forEach(saveSftpErpdto -> {
				sftpRepository.updateSftpScenarioPerm(saveSftpErpdto.getErpId(),
						saveSftpErpdto.getScenarioId());
			});
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"ERPPermissionServiceImpl deleteErpPermissionDetails End");
		}
	}

}
