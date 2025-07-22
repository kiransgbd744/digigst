/**
 * 
 */
package com.ey.advisory.admin.data.erp;

/**
 * @author Laxmi.Salukuti
 *
 */
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.ErpInfoEntityRepository;
import com.ey.advisory.core.dto.ErpEntityInfoDto;

@Component("ERPRegistrationServiceImpl")
public class ERPRegistrationServiceImpl implements ERPRegistrationService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ERPRegistrationServiceImpl.class);
	@Autowired
	@Qualifier("ErpInfoEntityRepository")
	private ErpInfoEntityRepository erpInfoEntityRepository;

	@Override
	public void deleteErpInfoDetails(List<ErpEntityInfoDto> erpEntitydto) {
		LOGGER.error("ERPRegistrationServiceImpl deleteErpInfoDetails Begin");
		if (!erpEntitydto.isEmpty()) {
			erpEntitydto.forEach(erpRegistrationReqDto -> {
				if (erpRegistrationReqDto.getId() != null
						&& erpRegistrationReqDto.getId() > 0) {
					erpInfoEntityRepository
							.deleterecord(erpRegistrationReqDto.getId());
				}
			});
		}
		LOGGER.error("ERPRegistrationServiceImpl deleteErpInfoDetails End");
	}

}
