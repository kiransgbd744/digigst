package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Hemasundar.J
 *
 */
/*
 * @Repository
 * 
 * @Transactional(value= "clientTransactionManager", propagation =
 * Propagation.REQUIRED, readOnly = false)
 */
@Slf4j
public class ProcedureCallRepositoryImpl implements ProcedureCallRepository {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Autowired
	private GstnApi gstnApi;

	@Autowired
	@Qualifier("ConfigManagerImpl")
	ConfigManager configManager;

	@Transactional(value = "clientTransactionManager")
	@Override
	public void saveGstr1ProcCall(String sgstin, String retPeriod,
			String section, String groupcode, Long userRequestId,
			Integer derivedRetPeriod, boolean isNilUserInput,
			boolean isHsnUserInput, ProcessingContext context) {
		String proc = "";
		String paramGstin = "IP_SUPPLIER_GSTIN";
		String paramRetPeriod = "IP_RETURN_PERIOD";
		String paramGroupCode = "IP_GROUP_CODE";
		String paramDervdPeriod = "IP_DERIVED_RET_PERIOD";
		String paramUserRequestId = "IP_USER_REQUEST_ID";
		String paramUserInputData = "IS_USER_INPUT_DATA";

		String returnType = GenUtil.getReturnType(context);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Inside saveGstr1ProcCall method with args {},{},{},{}",
					sgstin, retPeriod, section, groupcode);
		}
		if (section.equals(APIConstants.B2CS)) {
			proc = returnType.equalsIgnoreCase(APIConstants.GSTR1A)
					? APIConstants.GSTR1A_B2CS_PROC : APIConstants.B2CS_PROC;
		} else if (section.equals(APIConstants.B2CSA)) {
			proc = returnType.equalsIgnoreCase(APIConstants.GSTR1A)
					? APIConstants.GSTR1A_B2CS_AMD_PROC
					: APIConstants.B2CS_AMD_PROC;
		} else if (section.equals(APIConstants.TXP)) {
			proc = returnType.equalsIgnoreCase(APIConstants.GSTR1A)
					? APIConstants.GSTR1A_TXP_PROC : APIConstants.TXP_PROC;
		} else if (section.equals(APIConstants.TXPA)) {
			proc = returnType.equalsIgnoreCase(APIConstants.GSTR1A)
					? APIConstants.GSTR1A_TXP_AMD_PROC
					: APIConstants.TXP_AMD_PROC;
		} else if (section.equals(APIConstants.AT)) {
			proc = returnType.equalsIgnoreCase(APIConstants.GSTR1A)
					? APIConstants.GSTR1A_AT_PROC : APIConstants.AT_PROC;
		} else if (section.equals(APIConstants.ATA)) {
			proc = returnType.equalsIgnoreCase(APIConstants.GSTR1A)
					? APIConstants.GSTR1A_AT_AMD_PROC
					: APIConstants.AT_AMD_PROC;
		} else if (section.equals(APIConstants.HSNSUM)) {
			if (gstnApi.isRateIncludedInHsn(retPeriod)) {
				proc = returnType.equalsIgnoreCase(APIConstants.GSTR1A)
						? APIConstants.GSTR1A_HSN_RATE_PROC
						: APIConstants.HSN_RATE_PROC;
			} else {
				proc = returnType.equalsIgnoreCase(APIConstants.GSTR1A)
						? APIConstants.GSTR1A_HSN_PROC : APIConstants.HSN_PROC;
			}
		} else if (section.equals(APIConstants.NIL)) {
			proc = returnType.equalsIgnoreCase(APIConstants.GSTR1A)
					? APIConstants.GSTR1A_NIL_PROC : APIConstants.NIL_PROC;
		} else if (section.equals(APIConstants.SUPECOM)) {
			proc = returnType.equalsIgnoreCase(APIConstants.GSTR1A)
					? APIConstants.GSTR1A_SUPECOM_PROC
					: APIConstants.SUPECOM_PROC;
		} else if (section.equals(APIConstants.ECOMSUPSUM)) {
			proc = returnType.equalsIgnoreCase(APIConstants.GSTR1A)
					? APIConstants.GSTR1A_ECOMSUPSUM_PROC
					: APIConstants.ECOMSUPSUM_PROC;

		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{} Procedure Call started", proc);
		}
		StoredProcedureQuery st = entityManager
				.createStoredProcedureQuery(proc);
		// set parameters
		st.registerStoredProcedureParameter(paramGstin, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramRetPeriod, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramGroupCode, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramUserRequestId, Long.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramDervdPeriod, Integer.class,
				ParameterMode.IN);
		if (section.equals(APIConstants.HSNSUM)
				|| section.equals(APIConstants.NIL)) {
			st.registerStoredProcedureParameter(paramUserInputData,
					Boolean.class, ParameterMode.IN);
		}

		st.setParameter(paramGstin, sgstin);
		st.setParameter(paramRetPeriod, retPeriod);
		st.setParameter(paramGroupCode, groupcode);
		st.setParameter(paramUserRequestId, userRequestId);
		st.setParameter(paramDervdPeriod, derivedRetPeriod);

		if (section.equals(APIConstants.HSNSUM)) {
			st.setParameter(paramUserInputData, isHsnUserInput);
		} else if (section.equals(APIConstants.NIL)) {
			st.setParameter(paramUserInputData, isNilUserInput);
		}

		st.executeUpdate();
	}

	@Transactional(value = "clientTransactionManager")
	@Override
	public void saveAnx1ProcCall(String gstin, String retPeriod, String section,
			String groupcode, Integer derivedRetPeriod) {
		String proc = "";
		String paramGstin = "";
		String paramRetPeriod = "IP_RETURN_PERIOD";
		String paramGroupCode = "IP_GROUP_CODE";
		String paramDervdPeriod = "IP_DERIVED_RET_PERIOD";

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inside saveAnx1ProcCall method with args {},{},{},{}",
					gstin, retPeriod, section, groupcode);
		}
		if (section.equals(APIConstants.B2C)) {
			proc = APIConstants.B2C_PROC;
			paramGstin = "IP_SUPPLIER_GSTIN";
			// paramRetPeriod = "IP_RETURN_PERIOD";
		} else if (section.equals(APIConstants.REV)) {
			proc = APIConstants.REV_PROC;
			paramGstin = "IP_CUST_GSTIN";
			// paramRetPeriod = "IP_RETURN_PERIOD";
		} else if (section.equals(APIConstants.IMPS)) {
			proc = APIConstants.IMPS_PROC;
			paramGstin = "IP_CUST_GSTIN";
			// paramRetPeriod = "IP_RETURN_PERIOD";
		} else if (section.equals(APIConstants.ECOM)) {
			proc = APIConstants.ECOM_PROC;
			paramGstin = "IP_SUPPLIER_GSTIN";
			// paramRetPeriod = "IP_RETURN_PERIOD";
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{} Procedure Call started", proc);
		}
		StoredProcedureQuery st = entityManager
				.createStoredProcedureQuery(proc);
		// set parameters
		st.registerStoredProcedureParameter(paramGstin, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramRetPeriod, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramGroupCode, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramDervdPeriod, Integer.class,
				ParameterMode.IN);
		st.setParameter(paramGstin, gstin);
		st.setParameter(paramRetPeriod, retPeriod);
		st.setParameter(paramGroupCode, groupcode);
		st.setParameter(paramDervdPeriod, derivedRetPeriod);

		st.executeUpdate();
	}

	@Override
	public void saveAnx2ProcCall(String gstin, String section,
			Integer derivedRetPeriod) {
		String proc = APIConstants.USP_ANX2SAVE_GSTN;

		String paramGstin = "IP_RECIPIENT_GSTIN";
		String paramA2Table = "IP_A2_TABLE";
		String paramDervdPeriod = "IP_DERIVED_RET_PERIOD";

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inside saveAnx2ProcCall method with args {},{},{}",
					gstin, section, derivedRetPeriod);
		}
		String a2Table = null;
		if (section.equals(APIConstants.B2B)) {
			a2Table = "GETANX2_B2B_HEADER";
		} else if (section.equals(APIConstants.DE)) {
			a2Table = "GETANX2_DE_HEADER";
		} else if (section.equals(APIConstants.SEZWP)) {
			a2Table = "GETANX2_SEZWP_HEADER";
		} else if (section.equals(APIConstants.SEZWOP)) {
			a2Table = "GETANX2_SEZWOP_HEADER";
		} else if (section.equals(APIConstants.B2BA)) {
			a2Table = "GETANX2_B2BA_HEADER";
		} else if (section.equals(APIConstants.DEA)) {
			a2Table = "GETANX2_DEA_HEADER";
		} else if (section.equals(APIConstants.SEZWPA)) {
			a2Table = "GETANX2_SEZWPA_HEADER";
		} else if (section.equals(APIConstants.SEZWOPA)) {
			a2Table = "GETANX2_SEZWOPA_HEADER";
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{} Procedure Call started", proc);
		}
		StoredProcedureQuery st = entityManager
				.createStoredProcedureQuery(proc);
		// set parameters
		st.registerStoredProcedureParameter(paramGstin, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramA2Table, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramDervdPeriod, Integer.class,
				ParameterMode.IN);
		st.setParameter(paramGstin, gstin);
		st.setParameter(paramA2Table, a2Table);
		st.setParameter(paramDervdPeriod, derivedRetPeriod);

		st.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> fileStatuProcCall(String dataType, String fileType,
			LocalDate recieveFromDate, LocalDate recieveToDate) {
		String proc = "";
		List<Object[]> list = new ArrayList<>();

		try {
			proc = "ANX_FILESTATUS_PROC";
			StoredProcedureQuery st = entityManager
					.createStoredProcedureQuery(proc);
			st.registerStoredProcedureParameter("IP_DATATYPE", String.class,
					ParameterMode.IN);
			st.registerStoredProcedureParameter("IP_FILETYPE", String.class,
					ParameterMode.IN);
			st.registerStoredProcedureParameter("IP_RCVDATE_FROM",
					LocalDate.class, ParameterMode.IN);
			st.registerStoredProcedureParameter("IP_RCVDATE_TO",
					LocalDate.class, ParameterMode.IN);
			st.setParameter("IP_DATATYPE", dataType);
			st.setParameter("IP_FILETYPE", fileType);
			st.setParameter("IP_RCVDATE_FROM", recieveFromDate);
			st.setParameter("IP_RCVDATE_TO", recieveToDate);
			list = st.getResultList();
		} catch (Exception e) {
			LOGGER.error("Error {} While Executing {} Procedure",
					e.getMessage(), proc);
		}
		return list;
	}

	@Transactional(value = "clientTransactionManager")
	@Override
	public void saveRetProcCall(String gstin, String retPeriod, String section,
			String groupcode, Integer derivedRetPeriod) {
		// Need to update it according to RET
		String proc = "";
		String paramGstin = "IP_SUPPLIER_GSTIN";
		String paramRetPeriod = "IP_RETURN_PERIOD";
		String paramGroupCode = "IP_GROUP_CODE";
		String paramDervdPeriod = "IP_DERIVED_RET_PERIOD";

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inside saveRetProcCall method with args {},{},{},{}",
					gstin, retPeriod, section, groupcode);
		}

		if (section.equals(APIConstants.TBL3A)) {
			proc = APIConstants.USP_RET1_SAVE_3A;
		} else if (section.equals(APIConstants.TBL3C)) {
			proc = APIConstants.USP_RET1_SAVE_3C;
		} else if (section.equals(APIConstants.TBL3D)) {
			proc = APIConstants.USP_RET1_SAVE_3D;
		} else if (section.equals(APIConstants.TBL4A)) {
			proc = APIConstants.USP_RET1_SAVE_4A;
		} else if (section.equals(APIConstants.TBL4B)) {
			proc = APIConstants.USP_RET1_SAVE_4B;
		} else if (section.equals(APIConstants.TBL4ITC)) {
			proc = APIConstants.USP_RET1_SAVE_4ITC;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{} Procedure Call started", proc);
		}
		StoredProcedureQuery st = entityManager
				.createStoredProcedureQuery(proc);
		// set parameters
		st.registerStoredProcedureParameter(paramGstin, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramRetPeriod, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramGroupCode, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramDervdPeriod, Integer.class,
				ParameterMode.IN);
		st.setParameter(paramGstin, gstin);
		st.setParameter(paramRetPeriod, retPeriod);
		st.setParameter(paramGroupCode, groupcode);
		st.setParameter(paramDervdPeriod, derivedRetPeriod);

		st.executeUpdate();
	}

	@Transactional(value = "clientTransactionManager")
	@Override
	public void getAnx1ProcCall(String gstin, String retPeriod,
			String section) {
		String proc = "";
		String paramGstin = "P_GSTIN";
		String paramRetPeriod = "P_TAX_PERIOD";

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inside getAnx1ProcCall method with args {},{},{}",
					gstin, retPeriod, section);
		}

		if (section.equals(APIConstants.B2C)) {
			proc = APIConstants.USP_GETANX1_B2C_DETAILED;
		} else if (section.equals(APIConstants.B2B)) {
			proc = APIConstants.USP_GETANX1_B2B_DETAILED;
		} else if (section.equals(APIConstants.B2BA)) {
			proc = APIConstants.USP_GETANX1_B2B_AMD_DETAILED;
		} else if (section.equals(APIConstants.EXPWP)) {
			proc = APIConstants.USP_GETANX1_EXPWP_DETAILED;
		} else if (section.equals(APIConstants.EXPWOP)) {
			proc = APIConstants.USP_GETANX1_EXPWOP_DETAILED;
		} else if (section.equals(APIConstants.SEZWP)) {
			proc = APIConstants.USP_GETANX1_SEZWP_DETAILED;
		} else if (section.equals(APIConstants.SEZWPA)) {
			proc = APIConstants.USP_GETANX1_SEZWP_AMD_DETAILED;
		} else if (section.equals(APIConstants.SEZWOP)) {
			proc = APIConstants.USP_GETANX1_SEZWOP_DETAILED;
		} else if (section.equals(APIConstants.SEZWOPA)) {
			proc = APIConstants.USP_GETANX1_SEZWOPA_DETAILED;
		} else if (section.equals(APIConstants.DE)) {
			proc = APIConstants.USP_GETANX1_DE_DETAILED;
		} else if (section.equals(APIConstants.DEA)) {
			proc = APIConstants.USP_GETANX1_DEA_DETAILED;
		} else if (section.equals(APIConstants.REV)) {
			proc = APIConstants.USP_GETANX1_REV_DETAILED;
		} else if (section.equals(APIConstants.IMPS)) {
			proc = APIConstants.USP_GETANX1_IMPS_DETAILED;
		} else if (section.equals(APIConstants.IMPG)) {
			proc = APIConstants.USP_GETANX1_IMPG_DETAILED;
		} else if (section.equals(APIConstants.IMPGSEZ)) {
			proc = APIConstants.USP_GETANX1_IMPGSEZ_DETAILED;
		} else if (section.equals(APIConstants.ECOM)) {
			proc = APIConstants.USP_GETANX1_ECOM_DETAILED;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{} Procedure Call started", proc);
		}
		StoredProcedureQuery st = entityManager
				.createStoredProcedureQuery(proc);
		// set parameters
		st.registerStoredProcedureParameter(paramGstin, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramRetPeriod, String.class,
				ParameterMode.IN);
		st.setParameter(paramGstin, gstin);
		st.setParameter(paramRetPeriod, retPeriod);

		st.executeUpdate();

	}

	// ------------------------------GSTR7----------------------//

	@Transactional(value = "clientTransactionManager")
	@Override
	public void saveGstr7ProcCall(String sgstin, String retPeriod,
			String section, String groupcode, Long userRequestId,
			Integer derivedRetPeriod) {
		String proc = "";
		String paramGstin = "IP_SUPPLIER_GSTIN";
		String paramRetPeriod = "IP_RETURN_PERIOD";
		String paramGroupCode = "IP_GROUP_CODE";
		String paramDervdPeriod = "IP_DERIVED_RET_PERIOD";
		String paramUserRequestId = "IP_USER_REQUEST_ID";

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Inside saveGstr7ProcCall method with args {},{},{},{}",
					sgstin, retPeriod, section, groupcode);
		}
		if (section.equals(APIConstants.TDS)) {
			proc = APIConstants.GSTR7_SAVE_TDS_PROC;
		} else if (section.equals(APIConstants.TDSA)) {
			proc = APIConstants.GSTR7_SAVE_TDSA_PROC;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{} Procedure Call started", proc);
		}
		StoredProcedureQuery st = entityManager
				.createStoredProcedureQuery(proc);

		st.registerStoredProcedureParameter(paramGstin, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramRetPeriod, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramGroupCode, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramUserRequestId, Long.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramDervdPeriod, Integer.class,
				ParameterMode.IN);

		st.setParameter(paramGstin, sgstin);
		st.setParameter(paramRetPeriod, retPeriod);
		st.setParameter(paramGroupCode, groupcode);
		st.setParameter(paramUserRequestId, userRequestId);
		st.setParameter(paramDervdPeriod, derivedRetPeriod);
		st.executeUpdate();
	}

	@Transactional(value = "clientTransactionManager")
	@Override
	public void saveGstr8ProcCall(String sgstin, String retPeriod,
			String section, String groupcode, Long userRequestId,
			Integer derivedRetPeriod) {
		String paramGstin = "IP_SUPPLIER_GSTIN";
		String paramRetPeriod = "IP_RETURN_PERIOD";
		String paramGroupCode = "IP_GROUP_CODE";
		String paramDervdPeriod = "IP_DERIVED_RET_PERIOD";
		String paramUserRequestId = "IP_USER_REQUEST_ID";
//		String paramSection = "IP_SECTION";

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Inside saveGstr7ProcCall method with args {},{},{},{}",
					sgstin, retPeriod, section, groupcode);
		}
		String proc = APIConstants.GSTR8_SAVE_GSTN_PROC;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{} Procedure Call started", proc);
		}
		StoredProcedureQuery st = entityManager
				.createStoredProcedureQuery(proc);
		// set parameters
		st.registerStoredProcedureParameter(paramGstin, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramRetPeriod, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramGroupCode, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramUserRequestId, Long.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramDervdPeriod, Integer.class,
				ParameterMode.IN);
//		st.registerStoredProcedureParameter(paramSection, String.class,
//				ParameterMode.IN);

		st.setParameter(paramGstin, sgstin);
		st.setParameter(paramRetPeriod, retPeriod);
		st.setParameter(paramGroupCode, groupcode);
		st.setParameter(paramUserRequestId, userRequestId);
		st.setParameter(paramDervdPeriod, derivedRetPeriod);
//		st.setParameter(paramSection, section);
		st.executeUpdate();
	}

	@Transactional(value = "clientTransactionManager")
	@Override
	public void canGstr8ProcCall(String sgstin, String retPeriod,
			String section, String groupcode, Long userRequestId,
			Integer derivedRetPeriod) {
		String paramGstin = "IP_SUPPLIER_GSTIN";
		String paramRetPeriod = "IP_RETURN_PERIOD";
		String paramGroupCode = "IP_GROUP_CODE";
		String paramDervdPeriod = "IP_DERIVED_RET_PERIOD";
		String paramUserRequestId = "IP_USER_REQUEST_ID";
//		String paramSection = "IP_SECTION";

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Inside saveGstr7ProcCall method with args {},{},{},{}",
					sgstin, retPeriod, section, groupcode);
		}
		String proc = APIConstants.GSTR8_SAVE_CAN_GSTN_PROC;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{} Procedure Call started", proc);
		}
		StoredProcedureQuery st = entityManager
				.createStoredProcedureQuery(proc);
		// set parameters
		st.registerStoredProcedureParameter(paramGstin, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramRetPeriod, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramGroupCode, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramUserRequestId, Long.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramDervdPeriod, Integer.class,
				ParameterMode.IN);
//		st.registerStoredProcedureParameter(paramSection, String.class,
//				ParameterMode.IN);

		st.setParameter(paramGstin, sgstin);
		st.setParameter(paramRetPeriod, retPeriod);
		st.setParameter(paramGroupCode, groupcode);
		st.setParameter(paramUserRequestId, userRequestId);
		st.setParameter(paramDervdPeriod, derivedRetPeriod);
//		st.setParameter(paramSection, section);
		st.executeUpdate();
	}

	@Transactional(value = "clientTransactionManager")
	@Override
	public void getGstr2AErpProcCall(String gstin, String retPeriod,
			String section, Long getBatchId) {
		String proc = "";
		String paramGstin = "IP_SUPPLIER_GSTIN";
		String paramRetPeriod = "IP_RETURN_PERIOD";
		String paramGetBatchId = "IP_GET_BATCH_ID";
		String chunkVal = "IP_CHUNK_VAL";
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Inside getGstr2AErpProcCall method with args {},{},{}",
					gstin, retPeriod, section);
		}

		if (section.equalsIgnoreCase(APIConstants.B2B)) {
			proc = APIConstants.USP_GETGSTR2A_B2B_ERP_CONSOLIDATED;
		} else if (section.equalsIgnoreCase(APIConstants.B2BA)) {
			proc = APIConstants.USP_GETGSTR2A_B2BA_ERP_CONSOLIDATED;
		} else if (section.equalsIgnoreCase(APIConstants.CDN)) {
			proc = APIConstants.USP_GETGSTR2A_CDN_ERP_CONSOLIDATED;
		} else if (section.equalsIgnoreCase(APIConstants.CDNA)) {
			proc = APIConstants.USP_GETGSTR2A_CDNA_ERP_CONSOLIDATED;
		} else if (section.equalsIgnoreCase(APIConstants.ISD)) {
			proc = APIConstants.USP_GETGSTR2A_ISD_ERP_CONSOLIDATED;
		} else {
			return;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{} Procedure Call started", proc);
		}
		StoredProcedureQuery st = entityManager
				.createStoredProcedureQuery(proc);
		// set parameters
		st.registerStoredProcedureParameter(paramGstin, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramRetPeriod, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramGetBatchId, Long.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(chunkVal, Integer.class,
				ParameterMode.IN);
		st.setParameter(paramGstin, gstin);
		st.setParameter(paramRetPeriod, retPeriod);
		st.setParameter(paramGetBatchId, getBatchId);

		Map<String, Config> configMap = configManager.getConfigs("NALCOREVINTG",
				"nalco.rev.intg.chunk.limit");

		String chunkSize = configMap.get("nalco.rev.intg.chunk.limit") == null
				? "1000"
				: String.valueOf(
						configMap.get("nalco.rev.intg.chunk.limit").getValue());

		st.setParameter(chunkVal, Integer.parseInt(chunkSize));

		st.executeUpdate();
	}

	// -------------------------CAN-GSTR7----------------------//

	@Transactional(value = "clientTransactionManager")
	@Override
	public void canGstr7ProcCall(String sgstin, String retPeriod,
			String section, String groupcode, Long userRequestId,
			Integer derivedRetPeriod) {
		String proc = "";
		String paramGstin = "IP_SUPPLIER_GSTIN";
		String paramRetPeriod = "IP_RETURN_PERIOD";
		String paramGroupCode = "IP_GROUP_CODE";
		String paramDervdPeriod = "IP_DERIVED_RET_PERIOD";
		String paramUserRequestId = "IP_USER_REQUEST_ID";
		// try {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Inside saveGstr7ProcCall method with args {},{},{},{}",
					sgstin, retPeriod, section, groupcode);
		}
		if (section.equals(APIConstants.TDS)) {
			proc = APIConstants.GSTR7_CAN_TDS_PROC;
		} else if (section.equals(APIConstants.TDSA)) {
			proc = APIConstants.GSTR7_CAN_TDSA_PROC;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{} Procedure Call started", proc);
		}
		StoredProcedureQuery st = entityManager
				.createStoredProcedureQuery(proc);
		// set parameters
		st.registerStoredProcedureParameter(paramGstin, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramRetPeriod, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramGroupCode, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramUserRequestId, Long.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramDervdPeriod, Integer.class,
				ParameterMode.IN);

		st.setParameter(paramGstin, sgstin);
		st.setParameter(paramRetPeriod, retPeriod);
		st.setParameter(paramGroupCode, groupcode);
		st.setParameter(paramUserRequestId, userRequestId);
		st.setParameter(paramDervdPeriod, derivedRetPeriod);
		st.executeUpdate();
	}

	@Transactional(value = "clientTransactionManager")
	@Override
	public String gstr6CalTurnOverGstnProcCall(String endResultGstins,
			Integer fromReturnPeriod, Integer toReturnPeriod, Long entityId,
			String tableType, String taxPeriod, String endResultIsdGstins, Long batchId) {
		String proc = "";
		String paramGstin = "GSTIN";
		String paramFromRetPeriod = "FROM_DERIVED_RET_PERIOD";
		String paramToRetPeriod = "TO_DERIVED_RET_PERIOD";
		String paramEntityId = "ENTITY_ID";
		String paramTableType = "TABLE_TYPE";
		String paramTaxPeriod = "TAX_PERIOD";
		String paramIsdGstin = "ISD_GSTIN";
		String paramBatchId = "BATCH_ID";
		String status ="";
		

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Inside gstr6CalTurnOverDigiGstProcCall method with args {},{},{},{},{},{},{},{}",
					endResultGstins, fromReturnPeriod, toReturnPeriod, entityId,
					tableType, taxPeriod, endResultIsdGstins, paramBatchId);
		}

		proc = APIConstants.COMPUTE_GSTR6_TURN_OVER_GSTIN_PROC;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{} Procedure Call started", proc);
		}
		
		try{
		StoredProcedureQuery st = entityManager
				.createStoredProcedureQuery(proc);
		// set parameters
		st.registerStoredProcedureParameter(paramGstin, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramFromRetPeriod, Integer.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramToRetPeriod, Integer.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramEntityId, Long.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramTableType, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramTaxPeriod, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramIsdGstin, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramBatchId, Long.class,
				ParameterMode.IN);

		st.setParameter(paramGstin, endResultGstins);
		st.setParameter(paramFromRetPeriod, fromReturnPeriod);
		st.setParameter(paramToRetPeriod, toReturnPeriod);
		st.setParameter(paramEntityId, entityId);
		st.setParameter(paramTableType, tableType);
		st.setParameter(paramTaxPeriod, taxPeriod);
		st.setParameter(paramIsdGstin, endResultIsdGstins);
		st.setParameter(paramBatchId, batchId);
		
		long dbLoadStTime = System.currentTimeMillis();
		status = (String) st.getSingleResult();
		long dbLoadEndTime = System.currentTimeMillis();
		long dbLoadTimeDiff = (dbLoadEndTime - dbLoadStTime);
		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Total Time taken to excecute the Proc :'%s'"
							+ " with staus :'%s' from DB is '%d' millisecs.",
							proc, status, dbLoadTimeDiff);
			LOGGER.debug(msg);
		}
		
		
		}catch (Exception e) {
			String msg = String.format(
					"Exception occured while calling proc :'%s' for Batch Id '%s'",
					proc, batchId);
			LOGGER.debug(msg);
			LOGGER.error(msg,e);
			throw new AppException(msg);
		}
		
		return status;

	}

	@Transactional(value = "clientTransactionManager")
	@Override
	public void gstr6CalTurnOverDigiGstProcCall(String endResultGstins,
			Integer fromReturnPeriod, Integer toReturnPeriod, Long entityId,
			String tableType, String taxPeriod, String endResultRegGstins) {
		String proc = "";
		String paramGstin = "GSTIN";
		String paramFromRetPeriod = "FROM_DERIVED_RET_PERIOD";
		String paramToRetPeriod = "TO_DERIVED_RET_PERIOD";
		String paramEntityId = "ENTITY_ID";
		String paramTableType = "TABLE_TYPE";
		String paramTaxPeriod = "TAX_PERIOD";
		String paramRegGstin = "REG_GSTIN";
		// try {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Inside gstr6CalTurnOverDigiGstProcCall method with args {},{},{},{},{},{}",
					endResultGstins, fromReturnPeriod, toReturnPeriod,
					tableType, taxPeriod, endResultRegGstins);
		}

		proc = APIConstants.COMPUTE_GSTR6_TURN_OVER_DIGIGST_PROC;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{} Procedure Call started", proc);
		}
		StoredProcedureQuery st = entityManager
				.createStoredProcedureQuery(proc);
		// set parameters
		st.registerStoredProcedureParameter(paramGstin, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramFromRetPeriod, Integer.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramToRetPeriod, Integer.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramEntityId, Long.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramTableType, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramTaxPeriod, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramRegGstin, String.class,
				ParameterMode.IN);

		st.setParameter(paramGstin, endResultGstins);
		st.setParameter(paramFromRetPeriod, fromReturnPeriod);
		st.setParameter(paramToRetPeriod, toReturnPeriod);
		st.setParameter(paramEntityId, entityId);
		st.setParameter(paramTableType, tableType);
		st.setParameter(paramTaxPeriod, taxPeriod);
		st.setParameter(paramRegGstin, endResultRegGstins);
		st.executeUpdate();
	}

	@Transactional(value = "clientTransactionManager")
	@Override
	public void gstr6ComputeCreditDistributionProcCall(String endResultGstins,
			String taxPeriod, String tableType, Long entityId, String answer) {
		String proc = "";
		String paramGstin = "GSTIN";
		String paramTaxPeriod = "TAX_PERIOD";
		String paramTableType = "TABLE_TYPE";
		String paramEntityId = "ENTITY_ID";

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Inside gstr6ComputeCreditDistributionProcCall method with args {},{},{},{}",
					endResultGstins, tableType, taxPeriod, entityId);
		}
		if ("A".equalsIgnoreCase(answer))
			proc = APIConstants.COMPUTE_GSTR6_CREDIT_DISTRIBUTION_PROC;
		if ("B".equalsIgnoreCase(answer))
			proc = APIConstants.COMPUTE_GSTR6_CREDIT_DISTRIBUTION_SPECIFIC_PROC;
		if ("C".equalsIgnoreCase(answer))
			proc = APIConstants.COMPUTE_GSTR6_CREDIT_DISTRIBUTION_LOCK;
		if ("D".equalsIgnoreCase(answer))
			proc = APIConstants.COMPUTE_GSTR6_CREDIT_DISTRIBUTION_LOCK_SPECIFIC;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{} Procedure Call started", proc);
		}
		StoredProcedureQuery st = entityManager
				.createStoredProcedureQuery(proc);
		// set parameters
		st.registerStoredProcedureParameter(paramGstin, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramTaxPeriod, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramTableType, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramEntityId, Long.class,
				ParameterMode.IN);

		st.setParameter(paramGstin, endResultGstins);
		st.setParameter(paramTaxPeriod, taxPeriod);
		st.setParameter(paramTableType, tableType);
		st.setParameter(paramEntityId, entityId);
		st.executeUpdate();

	}

	@Transactional(value = "clientTransactionManager")
	@Override
	public void gstr6ComputeTurnOverUserInputProcCall(String endResultGstins,
			Long entityId, String taxPeriod, String userInput, String ids,
			String fromPeriod) {
		String proc = "";
		String paramGstin = "GSTIN";
		String paramEntityId = "ENTITY_ID";
		String paramTaxPeriod = "TAX_PERIOD";
		String paramUserInput = "USER_INPUT";
		String paramId = "IN_ID";
		String paramFromPeriod = "FROM_PERIOD";
		// try {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"Inside gstr6ComputeTurnOverUserInputProcCall method with args {},{},{},{},{}",
					endResultGstins, entityId, taxPeriod, userInput, ids);
		}

		proc = APIConstants.COMPUTE_GSTR6_TURN_OVER_USER_INPUT_PROC;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{} Procedure Call started", proc);
		}
		StoredProcedureQuery st = entityManager
				.createStoredProcedureQuery(proc);
		// set parameters
		st.registerStoredProcedureParameter(paramGstin, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramEntityId, Long.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramTaxPeriod, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramUserInput, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramId, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramFromPeriod, String.class,
				ParameterMode.IN);

		st.setParameter(paramGstin, endResultGstins);
		st.setParameter(paramEntityId, entityId);
		st.setParameter(paramTaxPeriod, taxPeriod);
		st.setParameter(paramUserInput, userInput);
		st.setParameter(paramId, ids);
		st.setParameter(paramFromPeriod,
				(fromPeriod != null && !fromPeriod.isEmpty()) ? fromPeriod
						: '0');

		st.executeUpdate();

	}

	/**
	 * This procedure will insert the data from GET2a Staging tables to GET2A
	 * Original tables.
	 */
	@Transactional(value = "clientTransactionManager")
	@Override
	public void getGstr2aProcCall(String gstin, String retPeriod,
			String section, Long getBatchId, boolean isFromParamGet) {
		String proc = "";
		String paramGstin = "IP_SUPPLIER_GSTIN";
		String paramRetPeriod = "IP_RETURN_PERIOD";
		String paramGetBatchId = "IP_BATCH_ID";
		String paramfromDateGet = "IP_IS_FROM_GET";
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inside getGstr2aProcCall method with args {},{},{}",
					gstin, retPeriod, section);
		}

		if (section.equalsIgnoreCase(APIConstants.B2B)) {
			proc = APIConstants.USP_GETGSTR2A_B2B_DUP_CHK;
		} else if (section.equalsIgnoreCase(APIConstants.B2BA)) {
			proc = APIConstants.USP_GETGSTR2A_B2BA_DUP_CHK;
		} else if (section.equalsIgnoreCase(APIConstants.CDN)) {
			proc = APIConstants.USP_GETGSTR2A_CDN_DUP_CHK;
		} else if (section.equalsIgnoreCase(APIConstants.CDNA)) {
			proc = APIConstants.USP_GETGSTR2A_CDNA_DUP_CHK;
		} else if (section.equalsIgnoreCase(APIConstants.ISD)) {
			proc = APIConstants.USP_GETGSTR2A_ISD_DUP_CHK;
		} else if (section.equalsIgnoreCase(APIConstants.ISDA)) {
			proc = APIConstants.USP_GETGSTR2A_ISDA_DUP_CHK;
		} else if (section.equalsIgnoreCase(APIConstants.AMDHIST)) {
			proc = APIConstants.USP_GETGSTR2A_AMDHIST_DUP_CHK;
		} else if (section.equalsIgnoreCase(APIConstants.IMPG)) {
			proc = APIConstants.USP_GETGSTR2A_IMPG_DUP_CHK;
		} else if (section.equalsIgnoreCase(APIConstants.IMPGSEZ)) {
			proc = APIConstants.USP_GETGSTR2A_IMPGSEZ_DUP_CHK;
		} else if (section.equalsIgnoreCase(APIConstants.TDS)) {
			proc = APIConstants.USP_GETGSTR2A_TDS_DUP_CHK;
		} else if (section.equalsIgnoreCase(APIConstants.TDSA)) {
			proc = APIConstants.USP_GETGSTR2A_TDSA_DUP_CHK;
		} else if (section.equalsIgnoreCase(APIConstants.TCS)) {
			proc = APIConstants.USP_GETGSTR2A_TCS_DUP_CHK;
		} else if (section.equalsIgnoreCase(APIConstants.ECOM)) {
			proc = APIConstants.USP_GETGSTR2A_ECOM_DUP_CHK;
		} else if (section.equalsIgnoreCase(APIConstants.ECOMA)) {
			proc = APIConstants.USP_GETGSTR2A_ECOMA_DUP_CHK;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{} Procedure Call started", proc);
		}
		StoredProcedureQuery st = entityManager
				.createStoredProcedureQuery(proc);
		// set parameters
		st.registerStoredProcedureParameter(paramGstin, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramRetPeriod, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramGetBatchId, Long.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramfromDateGet, Boolean.class,
				ParameterMode.IN);
		st.setParameter(paramGstin, gstin);
		st.setParameter(paramRetPeriod, retPeriod);
		st.setParameter(paramGetBatchId, getBatchId);
		st.setParameter(paramfromDateGet, isFromParamGet);

		st.executeUpdate();

	}

	@Transactional(value = "clientTransactionManager")
	@Override
	public void getGstr6aProcCall(String gstin, String retPeriod,
			String section, Long getBatchId, boolean isFromParamGet) {
		String proc = "";
		String paramGstin = "IP_SUPPLIER_GSTIN";
		String paramRetPeriod = "IP_RETURN_PERIOD";
		String paramGetBatchId = "IP_BATCH_ID";
		String paramfromDateGet = "IP_IS_FROM_GET";
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Inside getGstr2aProcCall method with args {},{},{}",
					gstin, retPeriod, section);
		}

		if (section.equalsIgnoreCase(APIConstants.B2B)) {
			proc = APIConstants.USP_GETGSTR6A_B2B_DUP_CHK;
		} else if (section.equalsIgnoreCase(APIConstants.B2BA)) {
			proc = APIConstants.USP_GETGSTR6A_B2BA_DUP_CHK;
		} else if (section.equalsIgnoreCase(APIConstants.CDN)) {
			proc = APIConstants.USP_GETGSTR6A_CDN_DUP_CHK;
		} else if (section.equalsIgnoreCase(APIConstants.CDNA)) {
			proc = APIConstants.USP_GETGSTR6A_CDNA_DUP_CHK;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("{} Procedure Call started", proc);
		}
		StoredProcedureQuery st = entityManager
				.createStoredProcedureQuery(proc);
		// set parameters
		st.registerStoredProcedureParameter(paramGstin, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramRetPeriod, String.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramGetBatchId, Long.class,
				ParameterMode.IN);
		st.registerStoredProcedureParameter(paramfromDateGet, Boolean.class,
				ParameterMode.IN);
		st.setParameter(paramGstin, gstin);
		st.setParameter(paramRetPeriod, retPeriod);
		st.setParameter(paramGetBatchId, getBatchId);
		st.setParameter(paramfromDateGet, isFromParamGet);

		st.executeUpdate();

	}
}
