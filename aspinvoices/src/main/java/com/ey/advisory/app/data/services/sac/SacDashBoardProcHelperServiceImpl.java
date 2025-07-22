package com.ey.advisory.app.data.services.sac;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.repositories.client.SacGroupConfigRepository;
import com.ey.advisory.app.sac.SacGroupConfigEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("SacDashBoardProcHelperServiceImpl")
public class SacDashBoardProcHelperServiceImpl
		implements SacDashBoardProcHelperService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("SacGroupConfigRepository")
	private SacGroupConfigRepository repo;

	@Override
	@Transactional(value = "clientTransactionManager")

	public void procHelper(List<SacGroupConfigEntity> entityList) {

		List<SacGroupConfigEntity> outwardEntityList = getProcList(entityList,
				"Outward Report");

		List<SacGroupConfigEntity> inwardEntityList = getProcList(entityList,
				"Inward Report");

		String procName = null;

		if (outwardEntityList != null && outwardEntityList.size() > 0) {
			// Executing Outward Report procs
			for (SacGroupConfigEntity entity : outwardEntityList) {
				procName = entity.getSpName();
				if (Strings.isNullOrEmpty(procName)) {
					LOGGER.error(
							"Proc Name is Null, Hence Continuing to the Next one");
					continue;
				}
				try {
					executeProcedure(procName, entity);
				} catch (Exception ex) {
					String msg = String
							.format("Exception Occure while executing Outward Report.");
					LOGGER.error(msg, ex);
					break;
				}
			}
		} else {
			LOGGER.error(
					"No Outward Proc found, Hence Continuing to the Inward Proc.");
		}

		// Executing Inword Report procs
		if (outwardEntityList != null && outwardEntityList.size() > 0) {
			for (SacGroupConfigEntity entity : inwardEntityList) {
				procName = entity.getSpName();
				if (Strings.isNullOrEmpty(procName)) {
					LOGGER.error(
							"Proc Name is Null, Hence Continuing to the Next one");
					continue;
				}
				try {
					executeProcedure(procName, entity);

				} catch (Exception ex) {
					String msg = String
							.format("Exception Occure while executing Inword Report.");
					LOGGER.error(msg, ex);
					break;
				}
			}
		} else {
			LOGGER.error("No Inward Proc found.");
		}
	}

	private void executeProcedure(String procName, SacGroupConfigEntity entity)
			throws Exception {
		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery(procName);
		int rescount = 0;
		try {

			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Before executing SP-{}, entity-{}, Group-{}  ",
						entity.getSpName(), entity.getEntityId(),
						TenantContext.getTenantId());

			long dbLoadStTime = System.currentTimeMillis();
			rescount = storedProc.executeUpdate();
			long dbLoadEndTime = System.currentTimeMillis();
			long dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Total Time taken to excecute the Proc :'%s'"
								+ " with response count :'%s' from DB is '%d' millisecs.",
						procName, rescount, dbLoadTimeDiff);
				LOGGER.debug(msg);

				LOGGER.debug("After executing SP-{}, entity-{}, Group-{}  ",
						entity.getSpName(), entity.getEntityId(),
						TenantContext.getTenantId());
			}

		} catch (Exception ex) {
			LOGGER.error(
					"Exeption while executing the SAC SP-{},"
							+ " entity-{}, Group-{}",
					entity.getSpName(), entity.getEntityId(),
					TenantContext.getTenantId(), ex);
			throw new AppException(ex);
		}
	}

	private List<SacGroupConfigEntity> getProcList(
			List<SacGroupConfigEntity> entityList, String configValue) {
		return entityList.stream()
				.filter(entity -> entity.getConfigValue() != null
						&& entity.getConfigValue().contains(configValue))
				.collect(Collectors.toList());
	}

}
