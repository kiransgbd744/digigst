package com.ey.advisory.app.data.daos.client;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.caches.DefaultStateCache;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.docs.dto.anx1.Gstr2aProcessedDataRecordsRespDto;
import com.ey.advisory.app.services.daos.get2a.GetGstr2aDetailStatusService;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.dto.Gstr2aProcessedDataRecordsReqDto;
import com.ey.advisory.gstnapi.repositories.master.GstinAPIAuthInfoRepository;
import com.ey.advisory.gstnapi.services.DefaultGSTNAuthTokenService;
import com.google.common.base.Strings;

/**
 * 
 * @author Anand3.M
 *
 */
@Component
public class Gstr2aProcessedDataRecordsCommonUtil {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr2aProcessedDataRecordsCommonUtil.class);

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("DefaultGSTNAuthTokenService")
	private DefaultGSTNAuthTokenService defaultGSTNAuthTokenService;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	@Qualifier("GstinAPIAuthInfoRepository")
	private GstinAPIAuthInfoRepository gstinAPIAuthInfoRepository;

	@Autowired
	@Qualifier("GetGstr2aDetailStatusService")
	private GetGstr2aDetailStatusService getGstr2aDetailStatusService;

	@Autowired
	DefaultStateCache defaultStateCache;

	private Map<String, String> regTypeMap = new HashMap<String, String>();

	public List<Gstr2aProcessedDataRecordsRespDto> convertGstr2aRecordsIntoObjesct(
			List<Object[]> outDataArray,
			Gstr2aProcessedDataRecordsReqDto gstr2aPRReqDto,
			List<String> gstinList) throws Exception {
		getGstnMaps();
		List<Gstr2aProcessedDataRecordsRespDto> outList = new ArrayList<Gstr2aProcessedDataRecordsRespDto>();
		List<String> gstinDtls = new ArrayList<String>(
				getAllGstinMap().keySet());
		Map<String, String> gstinAuthMap = defaultGSTNAuthTokenService
				.getAuthTokenStatusForGstins(gstinDtls);
		if (!outDataArray.isEmpty()) {
			for (Object obj[] : outDataArray) {
				Gstr2aProcessedDataRecordsRespDto dto = new Gstr2aProcessedDataRecordsRespDto();
				String gstin = String.valueOf(obj[0]);
				dto.setGstin(gstin);
				String stateCode = gstin.substring(0, 2);
				String stateName = defaultStateCache.getStateName(stateCode);
				dto.setState(stateName);
				String regTypeName = getRegTypeName(gstin);
				if (!Strings.isNullOrEmpty(regTypeName)) {
					dto.setRegType(regTypeName.toUpperCase());
				} else {
					dto.setRegType("");
				}
				// Checking GSTIN Active Status.
				if (gstinAuthMap != null) {
					String gstnAct = gstinAuthMap.get(gstin);
					if (gstnAct.equalsIgnoreCase("A")) {
						dto.setAuthToken("Active");
					} else {
						dto.setAuthToken("Inactive");
					}
				} else {
					dto.setAuthToken("Inactive");
				}

				dto.setJanStatus((String) obj[2]);
				if (obj[3] != null) {
					Timestamp timeStamp = (Timestamp) obj[3];
					LocalDateTime localDT = timeStamp.toLocalDateTime();
					LocalDateTime convertref = EYDateUtil
							.toISTDateTimeFromUTC(localDT);
					dto.setJanTimestamp(convertref.toString());
				} else {
					dto.setJanTimestamp(null);
				}

				dto.setFebStatus((String) obj[4]);
				if (obj[5] != null) {
					Timestamp timeStamp = (Timestamp) obj[5];
					LocalDateTime localDT = timeStamp.toLocalDateTime();
					LocalDateTime convertref = EYDateUtil
							.toISTDateTimeFromUTC(localDT);
					dto.setFebTimeStamp(convertref.toString());
				} else {
					dto.setFebTimeStamp(null);
				}

				dto.setMarchStatus((String) obj[6]);
				if (obj[7] != null) {
					Timestamp timeStamp = (Timestamp) obj[7];
					LocalDateTime localDT = timeStamp.toLocalDateTime();
					LocalDateTime convertref = EYDateUtil
							.toISTDateTimeFromUTC(localDT);
					dto.setMarchTimestamp(convertref.toString());
				} else {
					dto.setMarchTimestamp(null);
				}

				dto.setAprilStatus((String) obj[8]);
				if (obj[9] != null) {
					Timestamp timeStamp = (Timestamp) obj[9];
					LocalDateTime localDT = timeStamp.toLocalDateTime();
					LocalDateTime convertref = EYDateUtil
							.toISTDateTimeFromUTC(localDT);
					dto.setApriltimestamp(convertref.toString());
				} else {
					dto.setApriltimestamp(null);
				}

				dto.setMayStatus((String) obj[10]);
				if (obj[11] != null) {
					Timestamp timeStamp = (Timestamp) obj[11];
					LocalDateTime localDT = timeStamp.toLocalDateTime();
					LocalDateTime convertref = EYDateUtil
							.toISTDateTimeFromUTC(localDT);
					dto.setMayTimeStamp(convertref.toString());
				} else {
					dto.setMayTimeStamp(null);
				}

				dto.setJuneStatus((String) obj[12]);
				if (obj[13] != null) {
					Timestamp timeStamp = (Timestamp) obj[13];
					LocalDateTime localDT = timeStamp.toLocalDateTime();
					LocalDateTime convertref = EYDateUtil
							.toISTDateTimeFromUTC(localDT);
					dto.setJuneTimeStamp(convertref.toString());
				} else {
					dto.setJuneTimeStamp(null);
				}

				dto.setJulyStatus((String) obj[14]);
				if (obj[15] != null) {
					Timestamp timeStamp = (Timestamp) obj[15];
					LocalDateTime localDT = timeStamp.toLocalDateTime();
					LocalDateTime convertref = EYDateUtil
							.toISTDateTimeFromUTC(localDT);
					dto.setJulyTimestamp(convertref.toString());
				} else {
					dto.setJulyTimestamp(null);
				}

				dto.setAugStatus((String) obj[16]);
				if (obj[17] != null) {
					Timestamp timeStamp = (Timestamp) obj[17];
					LocalDateTime localDT = timeStamp.toLocalDateTime();
					LocalDateTime convertref = EYDateUtil
							.toISTDateTimeFromUTC(localDT);
					dto.setAugTimeStamp(convertref.toString());
				} else {
					dto.setAugTimeStamp(null);
				}

				dto.setSepStatus((String) obj[18]);
				if (obj[19] != null) {
					Timestamp timeStamp = (Timestamp) obj[19];
					LocalDateTime localDT = timeStamp.toLocalDateTime();
					LocalDateTime convertref = EYDateUtil
							.toISTDateTimeFromUTC(localDT);
					dto.setSepTimeStamp(convertref.toString());
				} else {
					dto.setSepTimeStamp(null);
				}

				dto.setOctStatus((String) obj[20]);
				if (obj[21] != null) {
					Timestamp timeStamp = (Timestamp) obj[21];
					LocalDateTime localDT = timeStamp.toLocalDateTime();
					LocalDateTime convertref = EYDateUtil
							.toISTDateTimeFromUTC(localDT);
					dto.setOctTimestamp(convertref.toString());
				} else {
					dto.setOctTimestamp(null);
				}

				dto.setNovStatus((String) obj[22]);
				if (obj[23] != null) {
					Timestamp timeStamp = (Timestamp) obj[23];
					LocalDateTime localDT = timeStamp.toLocalDateTime();
					LocalDateTime convertref = EYDateUtil
							.toISTDateTimeFromUTC(localDT);
					dto.setNovTimeStamp(convertref.toString());
				} else {
					dto.setNovTimeStamp(null);
				}

				dto.setDecStatus((String) obj[24]);

				if (obj[25] != null) {
					Timestamp timeStamp = (Timestamp) obj[25];
					LocalDateTime localDT = timeStamp.toLocalDateTime();
					LocalDateTime convertref = EYDateUtil
							.toISTDateTimeFromUTC(localDT);
					dto.setDecTimestamp(convertref.toString());
				} else {
					dto.setDecTimestamp(null);
				}

				outList.add(dto);
			}
		}
		return outList;

	}

	public void fillTheDataFromDataSecAttr(
			Gstr2aProcessedDataRecordsReqDto gstr2aPRReqDto,
			List<Gstr2aProcessedDataRecordsRespDto> outwardFinalList,
			List<String> gstinList) {

		List<String> dataGstinList = new ArrayList<>();
		outwardFinalList.forEach(dto -> dataGstinList.add(dto.getGstin()));
		dataGstinList.forEach(gstin -> gstinList.remove(gstin));

		for (String gstin : gstinList) {
			Gstr2aProcessedDataRecordsRespDto dummy = new Gstr2aProcessedDataRecordsRespDto();
			dummy.setGstin(gstin);
			String stateCode = gstin.substring(0, 2);
			String stateName = statecodeRepository
					.findStateNameByCode(stateCode);
			dummy.setState(stateName);
			String gstintoken = defaultGSTNAuthTokenService
					.getAuthTokenStatusForGstin(gstin);
			if (gstintoken != null) {
				if ("A".equalsIgnoreCase(gstintoken)) {
					dummy.setAuthToken("Active");
				} else {
					dummy.setAuthToken("Inactive");
				}
			} else {
				dummy.setAuthToken("Inactive");
			}

			List<String> regName = gSTNDetailRepository
					.findRegTypeByGstinForGstr2PR(gstin);
			if (regName != null && regName.size() > 0) {
				String regTypeName = regName.get(0);
				if (regTypeName == null
						|| regTypeName.equalsIgnoreCase("normal"))
				// || regTypeName.equalsIgnoreCase("regular"))
				{
					dummy.setRegType("");
				} else {
					dummy.setRegType(regTypeName.toUpperCase());
				}
			} else {
				dummy.setRegType("");
			}
			dummy.setJanStatus("");
			dummy.setJanTimestamp("");
			dummy.setFebStatus("");
			dummy.setFebTimeStamp("");
			dummy.setMarchStatus("");
			dummy.setMarchTimestamp("");
			dummy.setAprilStatus("");
			dummy.setApriltimestamp("");
			dummy.setMayStatus("");
			dummy.setMayTimeStamp("");
			dummy.setJuneStatus("");
			dummy.setJuneTimeStamp("");
			dummy.setJulyStatus("");
			dummy.setJulyTimestamp("");
			dummy.setAugStatus("");
			dummy.setAugTimeStamp("");
			dummy.setSepStatus("");
			dummy.setSepTimeStamp("");
			dummy.setOctStatus("");
			dummy.setOctTimestamp("");
			dummy.setNovStatus("");
			dummy.setNovTimeStamp("");
			dummy.setDecStatus("");
			dummy.setDecTimestamp("");
			outwardFinalList.add(dummy);
		}
	}

	private void getGstnMaps() {
		try {
			List<GSTNDetailEntity> findAll = gSTNDetailRepository.findDetails();
			for (GSTNDetailEntity regCode : findAll) {
				regTypeMap.put(regCode.getGstin(),
						regCode.getRegistrationType());
			}
		} catch (Exception ex) {
			String msg = "Error occurred while loading the list of regTypes. "
					+ "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	public String getRegTypeName(String gstin) {
		return regTypeMap.get(gstin);
	}

	public Map<String, String> getAllGstinMap() {
		return regTypeMap;
	}

}
