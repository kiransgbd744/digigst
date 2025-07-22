package com.ey.advisory.app.docs.services.gstr6;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.StateCodeInfoEntity;
import com.ey.advisory.admin.data.repositories.master.StatecodeRepository;
import com.ey.advisory.app.data.statecode.dto.Gstr6DistributionStateCodeDetailsDto;

/**
 * 
 * @author Dibyakanta.S
 *
 */

@Component("Gstr6DistributedStateCodeSummaryDaoImpl")
public class Gstr6DistributedStateCodeSummaryDaoImpl
		implements Gstr6DistributedStateCodeSummaryDao {

	@Autowired
	@Qualifier("StatecodeRepositoryMaster")
	private StatecodeRepository stateCodeRespmaster;

	@Override
	public List<Gstr6DistributionStateCodeDetailsDto> findGetState() {
		List<Gstr6DistributionStateCodeDetailsDto> stateCodeDetails = new ArrayList<>();
		List<StateCodeInfoEntity> stateCodeRepMasters = stateCodeRespmaster
				.findAll();
		if (stateCodeRepMasters != null && !stateCodeRepMasters.isEmpty()) {
			stateCodeRepMasters.forEach(stateCodeRepMaster -> {
				Gstr6DistributionStateCodeDetailsDto stateCodeDetail = new Gstr6DistributionStateCodeDetailsDto();
				stateCodeDetail.setStateCode(stateCodeRepMaster.getStateCode());
				stateCodeDetail.setStateName(stateCodeRepMaster.getStateName());
				stateCodeDetails.add(stateCodeDetail);
			});
		}
		return stateCodeDetails;
	}

}
