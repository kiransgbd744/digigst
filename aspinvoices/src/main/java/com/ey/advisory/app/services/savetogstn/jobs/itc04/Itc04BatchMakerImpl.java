package com.ey.advisory.app.services.savetogstn.jobs.itc04;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author SriBhavya
 *
 */
@Service("Itc04BatchMakerImpl")
@Slf4j
public class Itc04BatchMakerImpl implements Itc04BatchMaker {

	@Autowired
	@Qualifier("Itc04SaveBatchProcessImpl")
	private Itc04SectionWiseSaveBatchProcess saveBatchProcess;

	@Autowired
	@Qualifier("Itc04RateDataToM2jwConverter")
	private RateDataToItc04Converter m2jwItc04Converter;

	@Autowired
	@Qualifier("Itc04RateDataToTable5AConverter")
	private RateDataToItc04Converter table5AItc04Converter;

	@Autowired
	@Qualifier("Itc04RateDataToTable5BConverter")
	private RateDataToItc04Converter table5BItc04Converter;

	@Autowired
	@Qualifier("Itc04RateDataToTable5CConverter")
	private RateDataToItc04Converter table5CItc04Converter;

	@Override
	public List<SaveToGstnBatchRefIds> saveItc04Data(String groupCode, String section, List<Object[]> docs,
			SaveToGstnOprtnType operationType, Long userRequestId) {
		String taxDocType = null;
		if (operationType != null) {
			taxDocType = operationType.toString();
		}
		List<SaveToGstnBatchRefIds> respList = new ArrayList<>();
		try {
			if (docs != null && !docs.isEmpty()) {
				List<SaveToGstnBatchRefIds> list = new ArrayList<>();

				if (APIConstants.M2JW.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = m2jwItc04Converter.convertToItc04Object(docs, section, groupCode,
							taxDocType);
					if (batchDto != null)
						list = saveBatchProcess.execute(batchDto, groupCode, section, userRequestId, taxDocType);
				} else if (APIConstants.TABLE5A.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = table5AItc04Converter.convertToItc04Object(docs, section, groupCode,
							taxDocType);
					if (batchDto != null)
						list = saveBatchProcess.execute(batchDto, groupCode, section, userRequestId, taxDocType);
				} else if (APIConstants.TABLE5B.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = table5BItc04Converter.convertToItc04Object(docs, section, groupCode,
							taxDocType);
					if (batchDto != null)
						list = saveBatchProcess.execute(batchDto, groupCode, section, userRequestId, taxDocType);
				} else if (APIConstants.TABLE5C.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = table5CItc04Converter.convertToItc04Object(docs, section, groupCode,
							taxDocType);
					if (batchDto != null)
						list = saveBatchProcess.execute(batchDto, groupCode, section, userRequestId, taxDocType);
				} 

				if (!list.isEmpty()) {
					respList.addAll(list);
				}
			}

		} catch (Exception ex) {
			String msg = "Unexpected error while saving {} docs to Gstn {}";
			LOGGER.error(msg, section, ex);
			throw new APIException(msg, ex);
		}
		return respList;
	}

}
