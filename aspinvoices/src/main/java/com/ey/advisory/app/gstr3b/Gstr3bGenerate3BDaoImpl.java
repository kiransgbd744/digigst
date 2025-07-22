package com.ey.advisory.app.gstr3b;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.EntityConfigPrmtRepository;
import com.ey.advisory.app.data.entities.client.Gstr3bProcedureEntity;
import com.ey.advisory.app.data.repositories.client.Gstr3bDigiStatusRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3bProcedureRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.ReconStatusConstants;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr3bGenerate3BDaoImpl")
public class Gstr3bGenerate3BDaoImpl implements Gstr3bGenerate3BDao {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("Gstr3bDigiStatusRepository")
	private Gstr3bDigiStatusRepository gstr3bDigiStatusRepository;

	@Autowired
	@Qualifier("Gstr3bProcedureRepository")
	Gstr3bProcedureRepository procRepo;

	@Autowired
	@Qualifier("EntityConfigPrmtRepository")
	private EntityConfigPrmtRepository entityConfigPemtRepo;

	@Override
	public String getGstnDtoList(String gstin, String taxPeriod, Long id,
			Long entityId) {

		String status = null;
		try {
			DateFormat formatter = new SimpleDateFormat("MMyyyy");
			DateFormat dateFormat = new SimpleDateFormat("yyyyMM");
			Date date = (Date) formatter.parse(taxPeriod);
			String reqDate = dateFormat.format(date);

			Integer taxPeriodInt = Integer.parseInt(reqDate);

			String queryStringTotal = createQueryString();

			Query q1 = entityManager.createNativeQuery(queryStringTotal);
			
			LOGGER.debug("queryStringTotal {} ",queryStringTotal);
			Long batchId = Long.valueOf(q1.getSingleResult().toString());
			

			LOGGER.debug("batchId {} ",batchId);
			
			boolean optAns = onboardingQuestionOpted(entityId);

			LOGGER.debug("optAns {} entityId {} ",optAns,entityId);
			
			callProc(gstin, taxPeriodInt, batchId, id, optAns, null);

			status = "success";

		} catch (Exception ex) {

			String msg = "Exception while executing Stroed Proc : GSTR3B_COMPUTE";
			LOGGER.error(msg, ex);
			gstr3bDigiStatusRepository.updateRecordById(id, "Failed",
					LocalDateTime.now());
			// throw new AppException(msg, ex);
		}
		return status;
	}

	private void callProc(String gstin, Integer derivedTaxPeriod, Long batchId,
			Long id, boolean optAns, String procName) {

		procName = null;
		String response = null;
		try {
			List<Gstr3bProcedureEntity> procList = procRepo
					.findAllActiveProcedures();

			Map<Integer, String> procMap = new TreeMap<>();

			if (optAns &&  derivedTaxPeriod<202208 ) {
				procMap = procList.stream().filter(o->o.getSeqSupA()!=null)
						.collect(Collectors.toMap(o -> o.getSeqSupA(),
								o -> o.getProcedureName().toString()));

			} else {

				procMap = procList.stream().filter(o->o.getSeqSupB()!=null)
						.collect(Collectors.toMap(o -> o.getSeqSupB(),
								o -> o.getProcedureName().toString()));

			}
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Config Id is '%s', Procs going to "
								+ "Execute  are %s ",
						batchId.toString(), procMap.toString());
				LOGGER.error(msg);
			}
			for (Integer k : procMap.keySet())

			{
				procName = procMap.get(k);

				StoredProcedureQuery storedProc = procCall(gstin,
						derivedTaxPeriod, batchId, procName);

				response = (String) storedProc.getSingleResult();

				LOGGER.debug(procName + " :: " + response);

				if (!ReconStatusConstants.SUCCESS.equalsIgnoreCase(response)) {

					String msg = String.format("Config Id is '%s', Response "
							+ "from RECON_FAILED PROC %s did not "
							+ "return success," + " Hence updating to Failed",
							batchId.toString(), procName);
					LOGGER.error(msg);
					throw new AppException(msg);
				}
			}
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Config Id is '%s', After For loop "
								+ "Post proc Execution %s ",
						batchId.toString(), procMap.toString());
				LOGGER.error(msg);
			}

			gstr3bDigiStatusRepository.updateRecordById(id, "Success",
					LocalDateTime.now());

		} catch (Exception ex) {
			LOGGER.error(
					" Failed in failure proc at gstin level with config id {} and with exception {} ",
					batchId, ex);

			procCall(gstin, derivedTaxPeriod, batchId,
					"USP_RECON_3B_COMPUTE_FAILURE");

			gstr3bDigiStatusRepository.updateRecordById(id, "Failed",
					LocalDateTime.now());

			throw new AppException();

		}

	}

	private StoredProcedureQuery procCall(String gstin,
			Integer derivedTaxPeriod, Long batchId, String procName) {

		StoredProcedureQuery storedProc = entityManager
				.createStoredProcedureQuery(procName);

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"About to execute Recon Failed Proc with ConfigId :%s",
					batchId.toString());
			LOGGER.debug(msg);
		}

		storedProc.registerStoredProcedureParameter("VAR_BATCH_ID", Long.class,
				ParameterMode.IN);
		storedProc.setParameter("VAR_BATCH_ID", batchId);

		storedProc.registerStoredProcedureParameter("GSTIN", String.class,
				ParameterMode.IN);
		storedProc.setParameter("GSTIN", gstin);

		storedProc.registerStoredProcedureParameter("DERIVED_RET_PERIOD",
				Integer.class, ParameterMode.IN);
		
		storedProc.setParameter("DERIVED_RET_PERIOD", derivedTaxPeriod);

		return storedProc;
	}

	private boolean onboardingQuestionOpted(Long entityId) {
		String optAns = entityConfigPemtRepo.findByEntityGstr3b(entityId,
				"Bifurcation of transactions having combination of Supply Types \" NIL / NON / EXT / TAX / DTA\" in one document.");
		if ("A".equalsIgnoreCase(optAns))
			return true;
		else
			return false;
	}

	public String createQueryString() {
		return "SELECT IFNULL(MAX(BATCH_ID),0)+1 FROM GSTR3B_ASP_COMPUTE ";
	}
	
}
