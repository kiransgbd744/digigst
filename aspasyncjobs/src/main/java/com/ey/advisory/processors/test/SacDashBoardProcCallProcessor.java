package com.ey.advisory.processors.test;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.SacGroupConfigRepository;
import com.ey.advisory.app.data.services.sac.SacDashBoardProcHelperService;
import com.ey.advisory.app.sac.SacGroupConfigEntity;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.async.domain.master.Group;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("SacDashBoardProcCallProcessor")
public class SacDashBoardProcCallProcessor
		extends DefaultMultiTenantTaskProcessor {
	@Autowired
	@Qualifier("SacGroupConfigRepository")
	private SacGroupConfigRepository repo;

	@Autowired
	@Qualifier("SacDashBoardProcHelperServiceImpl")
	private SacDashBoardProcHelperService procHelper;

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {

		try {
			List<SacGroupConfigEntity> entityList = repo.findAllActiveProc();
			if (entityList.isEmpty()) {
				LOGGER.error("There are no entities configured for Group {}",
						group.getGroupCode());
				return;
			}
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("There are {} entities configured for Group {}",
						entityList.size(), group.getGroupCode());
			
			if(entityList!=null && entityList.size()>0){
				procHelper.procHelper(entityList);
			}else{
				LOGGER.debug("No records found in TBL_GROUP_CONFIG table.");
			}
			
		} catch (Exception ex) {
			String msg = String.format(
					"Error Occured Inside SacDashBoardProcHelperService"
							+ " while executing Group %s",
					group.getGroupCode());
			LOGGER.error(msg, ex);
		}

	}

}
