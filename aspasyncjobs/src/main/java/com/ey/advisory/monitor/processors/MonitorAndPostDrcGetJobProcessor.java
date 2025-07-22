package com.ey.advisory.monitor.processors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GetAutoJobDtlsEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.admin.data.repositories.client.GetAutoJobDtlsRepo;
import com.ey.advisory.app.data.repositories.client.EntityInfoRepository;
import com.ey.advisory.app.data.services.drc.DrcGstnService;
import com.ey.advisory.app.data.services.drc01c.Drc01cGstnService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.ErrMsgEnhancementStrategy;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy This class is responsible for Posting DRC Get Calls.
 */
@Slf4j
@Component("MonitorAndPostDrcGetJobProcessor")
public class MonitorAndPostDrcGetJobProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	private GSTNDetailRepository gstinDetailRepo;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Autowired
	private DrcGstnService drcGstnService;

	@Autowired
	@Qualifier("Drc01cGstnServiceImpl")
	private Drc01cGstnService drc01cGstnService;

	@Autowired
	private CommonUtility commonUtility;

	@Autowired
	private GetAutoJobDtlsRepo jobDtlsRepo;

	@Autowired
	private EntityInfoRepository entityInfoRepo;
	
	public static final String CONF_KEY = "drc.comm.calendar.date";

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {

		try {
			String groupCode = group.getGroupCode();

			List<Long> entityIds = entityInfoRepo
					.findEntityIdsByGroupCode(groupCode);

			Pair<String, String> taxPeriods = GenUtil
					.getCurrentAndPrevTaxPeriod();
			
			Map<String, Config> configMap = configManager
					.getConfigs("DRC01COMM", CONF_KEY, "DEFAULT");
			Integer todayDate = configMap.get(CONF_KEY) == null
					? Integer.valueOf(21)
					: Integer.valueOf(
							configMap.get(CONF_KEY).getValue().toString());
					
			LocalDate istDate = EYDateUtil
					.toISTDateTimeFromUTC(LocalDateTime.now()).toLocalDate();
			int currentDayOfMonth = LocalDate.now().getDayOfMonth();

			for (Long entityId : entityIds) {

				Optional<GetAutoJobDtlsEntity> isDrc01bJobPosted = jobDtlsRepo
						.findByEntityIdAndPostedDateAndReturnType(entityId,
								istDate, APIConstants.DRC01B.toUpperCase());

				Optional<GetAutoJobDtlsEntity> isDrc01cJobPosted = jobDtlsRepo
						.findByEntityIdAndPostedDateAndReturnType(entityId,
								istDate, APIConstants.DRC01C.toUpperCase());

				List<String> gstin = gstinDetailRepo
						.findgstinByEntityIdWithRegTypeForGstr1(entityId);
				String ansfromques = "B";
				ansfromques = commonUtility.getAnsFromQue(entityId,
						"Do you want to enable Auto-Email alert functionality to client users from DigiGST?");
				if (ansfromques.equalsIgnoreCase("A")
						&& currentDayOfMonth == todayDate) {
					if (!isDrc01bJobPosted.isPresent()) {
						drcGstnService.getDrcRetComList(gstin,
								taxPeriods.getValue1(), true, entityId);
						persistInJobDtlsForDrc01(entityId,
								APIConstants.DRC01B.toUpperCase());
					}
					if (!isDrc01cJobPosted.isPresent()) {
						drc01cGstnService.getDrcRetComList(gstin,
								taxPeriods.getValue1(), true, entityId);
						persistInJobDtlsForDrc01(entityId,
								APIConstants.DRC01C.toUpperCase());
					}

				}

			}

		} catch (Exception ex) {
			// Throw the App Exception here. If the exception obtained is
			// AppException, then propagate it. Otherwise, create a new
			// app exception. This particular constructor of the AppException
			// will extract the nested exception and attach the message to the
			// specified string. (This way the person who monitors the
			// EY_JOB_DETAILS database will come to know the root cause of the
			// exception).
			throw new AppException(ex,
					ErrMsgEnhancementStrategy.APPEND_FIRST_NON_APP_EXCEPTION);

		}
	}

	private void persistInJobDtlsForDrc01(Long entityId, String returnType) {
		GetAutoJobDtlsEntity jobDtlsEnt = new GetAutoJobDtlsEntity();
		jobDtlsEnt.setEntityId(entityId);
		jobDtlsEnt.setCreatedOn(LocalDateTime.now());
		jobDtlsEnt.setCreatedBy("SYSTEM");
		jobDtlsEnt.setPostedDate(EYDateUtil
				.toISTDateTimeFromUTC(LocalDateTime.now()).toLocalDate());
		jobDtlsEnt.setReturnType(returnType);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("GetAutoJobDtlsEntity PostedDate for {} is {}",
					returnType, EYDateUtil.toISTDateTimeFromUTC(
							LocalDateTime.now().toLocalDate()));
		}
		jobDtlsRepo.save(jobDtlsEnt);
	}

}
