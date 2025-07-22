package com.ey.advisory.app.services.savetogstn.jobs.gstr6;

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
 * @author Sri Bhavya
 *
 */

@Service("Gstr6BatchMakerImpl")
@Slf4j
public class Gstr6BatchMakerImpl implements Gstr6BatchMaker {

	@Autowired
	@Qualifier("Gstr6SaveBatchProcessImpl")
	private Gstr6SectionWiseSaveBatchProcess saveBatchProcess;

	@Autowired
	@Qualifier("Gstr6RateDataToB2bConverter")
	private RateDataToGstr6Converter b2bGstr6Converter;

	@Autowired
	@Qualifier("Gstr6RateDataToB2baConverter")
	private RateDataToGstr6Converter b2baGstr6Converter;

	@Autowired
	@Qualifier("Gstr6RateDataToCdnConverter")
	private RateDataToGstr6Converter cdnGstr6Converter;

	@Autowired
	@Qualifier("Gstr6RateDataToCdnaConverter")
	private RateDataToGstr6Converter cdnaGstr6Converter;

	@Autowired
	@Qualifier("Gstr6RateDataToIsdConverter")
	private RateDataToGstr6Converter isdGstr6Converter;

	@Autowired
	@Qualifier("Gstr6RateDataToIsdaConverter")
	private RateDataToGstr6Converter isdaGstr6Converter;

	@Override
	public List<SaveToGstnBatchRefIds> saveGstr6Data(String groupCode,
			String section, List<Object[]> docs,
			SaveToGstnOprtnType operationType,Long userRequestId) {
		String taxDocType = null;
		if (operationType != null) {
			taxDocType = operationType.toString();
		}
		List<SaveToGstnBatchRefIds> respList = new ArrayList<>();
		try {
			if (docs != null && !docs.isEmpty()) {
				List<SaveToGstnBatchRefIds> list = new ArrayList<>();

				if (APIConstants.B2B.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = b2bGstr6Converter
							.convertToGstr6Object(docs, section, groupCode,
									taxDocType);
					if (batchDto != null)
						list = saveBatchProcess.execute(batchDto, groupCode,
								section,userRequestId,taxDocType, null);
				} else if (APIConstants.B2BA.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = b2baGstr6Converter
							.convertToGstr6Object(docs, section, groupCode,
									taxDocType);
					if (batchDto != null)
						list = saveBatchProcess.execute(batchDto, groupCode,
								section,userRequestId,taxDocType, null);
				} else if (APIConstants.CDN.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = cdnGstr6Converter
							.convertToGstr6Object(docs, section, groupCode,
									taxDocType);
					if (batchDto != null)
						list = saveBatchProcess.execute(batchDto, groupCode,
								section,userRequestId,taxDocType, null);
				} else if (APIConstants.CDNA.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = cdnaGstr6Converter
							.convertToGstr6Object(docs, section, groupCode,
									taxDocType);
					if (batchDto != null)
						list = saveBatchProcess.execute(batchDto, groupCode,
								section,userRequestId,taxDocType, null);
				} else if (APIConstants.ISD.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = isdGstr6Converter
							.convertToGstr6Object(docs, section, groupCode,
									taxDocType);
					if (batchDto != null)
						list = saveBatchProcess.execute(batchDto, groupCode,
								section,userRequestId,taxDocType, null);
				} else if (APIConstants.ISDA.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = isdaGstr6Converter
							.convertToGstr6Object(docs, section, groupCode,
									taxDocType);
					if (batchDto != null)
						list = saveBatchProcess.execute(batchDto, groupCode,
								section,userRequestId,taxDocType, null);
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
