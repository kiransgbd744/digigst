package com.ey.advisory.app.services.jobs.erp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.returns.compliance.service.CompienceHistoryHelperService;
import com.ey.advisory.app.data.returns.compliance.service.ComplainceHistoryGstr1SumDto;
import com.ey.advisory.app.data.returns.compliance.service.ComplainceHistoryGstr9SumDto;
import com.ey.advisory.app.data.returns.compliance.service.ComplainceHistoryItc04SumDto;
import com.ey.advisory.app.data.returns.compliance.service.ComplainceHistoryRevIntItemDto;
import com.ey.advisory.app.data.returns.compliance.service.ComplienceSummeryRespDto;
import com.ey.advisory.app.data.returns.compliance.service.ComplienceSummeryService;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.core.dto.Gstr2aProcessedDataRecordsReqDto;

/**
 * 
 * @author Umesha.M
 *
 */
@Service("ComplainceHistoryRevIntServiceImpl")
public class ComplainceHistoryRevIntServiceImpl
		implements ComplainceHistoryRevIntService {

	@Autowired
	@Qualifier("CompienceHistoryHelperService")
	private CompienceHistoryHelperService compienceHistoryHelperService;

	@Autowired
	@Qualifier("Itc04ComplianceServiceImpl")
	private ComplienceSummeryService complienceSummeryService;

	@Autowired
	@Qualifier("Gstr9ComplianceServiceImpl")
	private ComplienceSummeryService compSumGstr9Service;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstNDetailRepository;

	/**
	 * 
	 * @param entityName
	 * @param gstin
	 * @return
	 */
	public List<ComplainceHistoryRevIntItemDto> getComplainceHistory(
			String entityName, String gstin, String panNo, Long entityId,
			String regType, String finYear, String section) {
		List<ComplainceHistoryRevIntItemDto> itemDtos = new ArrayList<>();
		ComplainceHistoryRevIntItemDto itemDto = new ComplainceHistoryRevIntItemDto();
		itemDto.setGstn(gstin);
		itemDto.setEntityPan(panNo);
		itemDto.setEntityName(entityName);
		itemDto.setStatus(regType);

		// Setting Request Compliance history
		Gstr2aProcessedDataRecordsReqDto gstr2AProcDataRecReqDto = new Gstr2aProcessedDataRecordsReqDto();
		gstr2AProcDataRecReqDto.setEntity(String.valueOf(entityId));

		Map<String, List<String>> dataSecAttrs = new HashMap<>();
		List<String> gstins = new ArrayList<>();
		gstins.add(gstin);
		dataSecAttrs.put(OnboardingConstant.GSTIN, gstins);
		gstr2AProcDataRecReqDto.setDataSecAttrs(dataSecAttrs);

		// Find a GSTR1 Compliance History

		gstr2AProcDataRecReqDto.setFinancialYear(finYear);
		gstr2AProcDataRecReqDto.setDataSecAttrs(dataSecAttrs);

		// GSTR1 Compliance history

		gstr2AProcDataRecReqDto.setReturnType("GSTR1");
		List<ComplienceSummeryRespDto> respDtos = compienceHistoryHelperService
				.findcomplienceSummeryRecords(gstr2AProcDataRecReqDto);
		List<String> gstr1RegTypes = new ArrayList<>();
		gstr1RegTypes.add(gstin);
		gstr1RegTypes = gstNDetailRepository
				.filterGstinBasedOnRegType(gstr1RegTypes);
		ComplainceHistoryGstr1SumDto gstr1SumDto = new ComplainceHistoryGstr1SumDto();
		if (respDtos != null && !respDtos.isEmpty() && gstr1RegTypes != null
				&& !gstr1RegTypes.isEmpty()) {
			respDtos.forEach(respDto -> {
				gstr1SumDto.setGstn(respDto.getGstin());
				gstr1SumDto.setState(respDto.getState());
				gstr1SumDto.setStatus(regType);
				gstr1SumDto.setLastUpdatedDate(respDto.getInitiateTime() + " "
						+ respDto.getInitiatestatus());
				StringBuilder january = new StringBuilder();
				january.append(respDto.getJanStatus());
				if (respDto.getJanTimestamp() != null) {
					january.append(" ");
					january.append(respDto.getJanStatus());
				}
				gstr1SumDto.setJanuary(january.toString());

				StringBuilder february = new StringBuilder();
				february.append(respDto.getFebStatus());
				if (respDto.getFebTimeStamp() != null) {
					february.append(" ");
					february.append(respDto.getFebTimeStamp());
				}
				gstr1SumDto.setFebruary(february.toString());
				StringBuilder march = new StringBuilder();
				march.append(respDto.getMarchStatus());
				if (respDto.getMarchTimestamp() != null) {
					march.append(" ");
					march.append(respDto.getMarchTimestamp());
				}
				gstr1SumDto.setMarch(march.toString());

				StringBuilder april = new StringBuilder();
				april.append(respDto.getAprilStatus());

				if (respDto.getApriltimestamp() != null) {
					april.append(" ");
					april.append(respDto.getApriltimestamp());
				}
				gstr1SumDto.setApril(april.toString());

				StringBuilder may = new StringBuilder();
				may.append(respDto.getMayStatus());
				if (respDto.getMayTimeStamp() != null) {
					may.append(" ");
					may.append(respDto.getMayTimeStamp());
				}
				gstr1SumDto.setMay(may.toString());

				StringBuilder june = new StringBuilder();
				june.append(respDto.getJuneStatus());
				if (respDto.getJuneTimeStamp() != null) {
					june.append(" ");
					june.append(respDto.getJuneTimeStamp());
				}
				gstr1SumDto.setJune(june.toString());

				StringBuilder july = new StringBuilder();
				july.append(respDto.getJulyStatus());
				if (respDto.getJulyTimestamp() != null) {
					july.append(" ");
					july.append(respDto.getJulyTimestamp());
				}
				gstr1SumDto.setJuly(july.toString());

				StringBuilder august = new StringBuilder();
				august.append(respDto.getAugStatus());

				if (respDto.getAugTimeStamp() != null) {
					august.append(" ");
					august.append(respDto.getAugTimeStamp());
				}
				gstr1SumDto.setAugust(august.toString());

				StringBuilder sep = new StringBuilder();
				sep.append(respDto.getSepStatus());
				if (respDto.getSepTimeStamp() != null) {
					sep.append(" ");
					sep.append(respDto.getSepTimeStamp());
				}
				gstr1SumDto.setSeptember(sep.toString());

				StringBuilder oct = new StringBuilder();
				oct.append(respDto.getOctStatus());
				if (respDto.getOctTimestamp() != null) {
					oct.append(" ");
					oct.append(respDto.getOctTimestamp());
				}

				gstr1SumDto.setOctober(oct.toString());

				StringBuilder nov = new StringBuilder();
				nov.append(respDto.getNovStatus());
				if (respDto.getNovTimeStamp() != null) {
					nov.append(" ");
					nov.append(respDto.getNovTimeStamp());
				}
				gstr1SumDto.setNovember(nov.toString());

				StringBuilder dec = new StringBuilder();
				dec.append(respDto.getDecStatus());
				if (respDto.getDecTimestamp() != null) {
					dec.append(" ");
					dec.append(respDto.getDecTimestamp());
				}
				gstr1SumDto.setDecember(dec.toString());
			});
		}
		itemDto.setGstr1SumDto(gstr1SumDto);

		// GSTR3B Compliance history

		gstr2AProcDataRecReqDto.setReturnType("GSTR3B");
		List<ComplienceSummeryRespDto> respGstr3bDtos = compienceHistoryHelperService
				.findcomplienceSummeryRecords(gstr2AProcDataRecReqDto);
		ComplainceHistoryGstr1SumDto gstr3bSumDto = new ComplainceHistoryGstr1SumDto();
		if (respGstr3bDtos != null && !respGstr3bDtos.isEmpty()
				&& gstr1RegTypes != null && !gstr1RegTypes.isEmpty()) {
			respGstr3bDtos.forEach(respGstr3bDto -> {
				gstr3bSumDto.setGstn(respGstr3bDto.getGstin());
				gstr3bSumDto.setState(respGstr3bDto.getState());
				gstr3bSumDto.setStatus(regType);
				gstr3bSumDto.setLastUpdatedDate(respGstr3bDto.getInitiateTime()
						+ " " + respGstr3bDto.getInitiatestatus());
				StringBuilder january = new StringBuilder();
				january.append(respGstr3bDto.getJanStatus());
				if (respGstr3bDto.getJanTimestamp() != null) {
					january.append(" ");
					january.append(respGstr3bDto.getJanTimestamp());
				}
				gstr3bSumDto.setJanuary(january.toString());

				StringBuilder february = new StringBuilder();
				february.append(respGstr3bDto.getFebStatus());
				if (respGstr3bDto.getFebTimeStamp() != null) {
					february.append(" ");
					february.append(respGstr3bDto.getFebTimeStamp());
				}
				gstr3bSumDto.setFebruary(february.toString());
				StringBuilder march = new StringBuilder();
				march.append(respGstr3bDto.getMarchStatus());
				if (respGstr3bDto.getMarchTimestamp() != null) {
					march.append(" ");
					march.append(respGstr3bDto.getMarchTimestamp());
				}
				gstr3bSumDto.setMarch(march.toString());

				StringBuilder april = new StringBuilder();
				april.append(respGstr3bDto.getAprilStatus());

				if (respGstr3bDto.getApriltimestamp() != null) {
					april.append(" ");
					april.append(respGstr3bDto.getApriltimestamp());
				}
				gstr3bSumDto.setApril(april.toString());

				StringBuilder may = new StringBuilder();
				may.append(respGstr3bDto.getMayStatus());
				if (respGstr3bDto.getMayTimeStamp() != null) {
					may.append(" ");
					may.append(respGstr3bDto.getMayTimeStamp());
				}
				gstr3bSumDto.setMay(may.toString());

				StringBuilder june = new StringBuilder();
				june.append(respGstr3bDto.getJuneStatus());
				if (respGstr3bDto.getJuneTimeStamp() != null) {
					june.append(" ");
					june.append(respGstr3bDto.getJuneTimeStamp());
				}
				gstr3bSumDto.setJune(june.toString());

				StringBuilder july = new StringBuilder();
				july.append(respGstr3bDto.getJulyStatus());
				if (respGstr3bDto.getJulyTimestamp() != null) {
					july.append(" ");
					july.append(respGstr3bDto.getJulyTimestamp());
				}
				gstr3bSumDto.setJuly(july.toString());

				StringBuilder august = new StringBuilder();
				august.append(respGstr3bDto.getAugStatus());

				if (respGstr3bDto.getAugTimeStamp() != null) {
					august.append(" ");
					august.append(respGstr3bDto.getAugTimeStamp());
				}
				gstr3bSumDto.setAugust(august.toString());

				StringBuilder sep = new StringBuilder();
				sep.append(respGstr3bDto.getSepStatus());
				if (respGstr3bDto.getSepTimeStamp() != null) {
					sep.append(" ");
					sep.append(respGstr3bDto.getSepTimeStamp());
				}
				gstr3bSumDto.setSeptember(sep.toString());

				StringBuilder oct = new StringBuilder();
				oct.append(respGstr3bDto.getOctStatus());
				if (respGstr3bDto.getOctTimestamp() != null) {
					oct.append(" ");
					oct.append(respGstr3bDto.getOctTimestamp());
				}

				gstr3bSumDto.setOctober(oct.toString());

				StringBuilder nov = new StringBuilder();
				nov.append(respGstr3bDto.getNovStatus());
				if (respGstr3bDto.getNovTimeStamp() != null) {
					nov.append(" ");
					nov.append(respGstr3bDto.getNovTimeStamp());
				}
				gstr3bSumDto.setNovember(nov.toString());

				StringBuilder dec = new StringBuilder();
				dec.append(respGstr3bDto.getDecStatus());
				if (respGstr3bDto.getDecTimestamp() != null) {
					dec.append(" ");
					dec.append(respGstr3bDto.getDecTimestamp());
				}
				gstr3bSumDto.setDecember(dec.toString());
			});
		}
		itemDto.setGstr3bSumm(gstr3bSumDto);

		// GSTR6 Compliance history

		gstr2AProcDataRecReqDto.setReturnType("GSTR6");
		List<ComplienceSummeryRespDto> respGstr6Dtos = compienceHistoryHelperService
				.findcomplienceSummeryRecords(gstr2AProcDataRecReqDto);
		List<String> isdGstr6Gstin = new ArrayList<>();
		isdGstr6Gstin.add(gstin);
		isdGstr6Gstin = gstNDetailRepository.getGstinRegTypeISD(isdGstr6Gstin);
		ComplainceHistoryGstr1SumDto gstr6SumDto = new ComplainceHistoryGstr1SumDto();

		if (respGstr6Dtos != null && !respGstr6Dtos.isEmpty()
				&& isdGstr6Gstin != null && !isdGstr6Gstin.isEmpty()) {
			respGstr6Dtos.forEach(respGstr6Dto -> {
				gstr6SumDto.setGstn(respGstr6Dto.getGstin());
				gstr6SumDto.setState(respGstr6Dto.getState());
				gstr6SumDto.setStatus(regType);
				gstr6SumDto.setLastUpdatedDate(respGstr6Dto.getInitiateTime()
						+ " " + respGstr6Dto.getInitiatestatus());
				StringBuilder january = new StringBuilder();
				january.append(respGstr6Dto.getJanStatus());
				if (respGstr6Dto.getJanTimestamp() != null) {
					january.append(" ");
					january.append(respGstr6Dto.getJanTimestamp());
				}
				gstr6SumDto.setJanuary(january.toString());

				StringBuilder february = new StringBuilder();
				february.append(respGstr6Dto.getFebStatus());
				if (respGstr6Dto.getFebTimeStamp() != null) {
					february.append(" ");
					february.append(respGstr6Dto.getFebTimeStamp());
				}
				gstr6SumDto.setFebruary(february.toString());
				StringBuilder march = new StringBuilder();
				march.append(respGstr6Dto.getMarchStatus());
				if (respGstr6Dto.getMarchTimestamp() != null) {
					march.append(" ");
					march.append(respGstr6Dto.getMarchTimestamp());
				}
				gstr6SumDto.setMarch(march.toString());

				StringBuilder april = new StringBuilder();
				april.append(respGstr6Dto.getAprilStatus());

				if (respGstr6Dto.getApriltimestamp() != null) {
					april.append(" ");
					april.append(respGstr6Dto.getApriltimestamp());
				}
				gstr6SumDto.setApril(april.toString());

				StringBuilder may = new StringBuilder();
				may.append(respGstr6Dto.getMayStatus());
				if (respGstr6Dto.getMayTimeStamp() != null) {
					may.append(" ");
					may.append(respGstr6Dto.getMayTimeStamp());
				}
				gstr6SumDto.setMay(may.toString());

				StringBuilder june = new StringBuilder();
				june.append(respGstr6Dto.getJuneStatus());
				if (respGstr6Dto.getJuneTimeStamp() != null) {
					june.append(" ");
					june.append(respGstr6Dto.getJuneTimeStamp());
				}
				gstr6SumDto.setJune(june.toString());

				StringBuilder july = new StringBuilder();
				july.append(respGstr6Dto.getJulyStatus());
				if (respGstr6Dto.getJulyTimestamp() != null) {
					july.append(" ");
					july.append(respGstr6Dto.getJulyTimestamp());
				}
				gstr6SumDto.setJuly(july.toString());

				StringBuilder august = new StringBuilder();
				august.append(respGstr6Dto.getAugStatus());

				if (respGstr6Dto.getAugTimeStamp() != null) {
					august.append(" ");
					august.append(respGstr6Dto.getAugTimeStamp());
				}
				gstr6SumDto.setAugust(august.toString());

				StringBuilder sep = new StringBuilder();
				sep.append(respGstr6Dto.getSepStatus());
				if (respGstr6Dto.getSepTimeStamp() != null) {
					sep.append(" ");
					sep.append(respGstr6Dto.getSepTimeStamp());
				}
				gstr6SumDto.setSeptember(sep.toString());

				StringBuilder oct = new StringBuilder();
				oct.append(respGstr6Dto.getOctStatus());
				if (respGstr6Dto.getOctTimestamp() != null) {
					oct.append(" ");
					oct.append(respGstr6Dto.getOctTimestamp());
				}

				gstr6SumDto.setOctober(oct.toString());

				StringBuilder nov = new StringBuilder();
				nov.append(respGstr6Dto.getNovStatus());
				if (respGstr6Dto.getNovTimeStamp() != null) {
					nov.append(" ");
					nov.append(respGstr6Dto.getNovTimeStamp());
				}
				gstr6SumDto.setNovember(nov.toString());

				StringBuilder dec = new StringBuilder();
				dec.append(respGstr6Dto.getDecStatus());
				if (respGstr6Dto.getDecTimestamp() != null) {
					dec.append(" ");
					dec.append(respGstr6Dto.getDecTimestamp());
				}
				gstr6SumDto.setDecember(dec.toString());
			});
		}
		itemDto.setGstr6Summ(gstr6SumDto);

		// Gstr7 Compliance history

		gstr2AProcDataRecReqDto.setReturnType("GSTR7");
		List<ComplienceSummeryRespDto> respGstr7Dtos = compienceHistoryHelperService
				.findcomplienceSummeryRecords(gstr2AProcDataRecReqDto);
		List<String> tdsGstr7s = new ArrayList<>();
		tdsGstr7s.add(gstin);
		tdsGstr7s = gstNDetailRepository.getGstinRegTypeTDS(tdsGstr7s);
		ComplainceHistoryGstr1SumDto gstr7SumDto = new ComplainceHistoryGstr1SumDto();
		if (respGstr7Dtos != null && !respGstr7Dtos.isEmpty()
				&& tdsGstr7s != null && !tdsGstr7s.isEmpty()) {
			respGstr7Dtos.forEach(respGstr7Dto -> {
				gstr7SumDto.setGstn(respGstr7Dto.getGstin());
				gstr7SumDto.setState(respGstr7Dto.getState());
				gstr7SumDto.setStatus(regType);

				gstr7SumDto.setLastUpdatedDate(respGstr7Dto.getInitiateTime()
						+ " " + respGstr7Dto.getInitiatestatus());

				StringBuilder january = new StringBuilder();
				january.append(respGstr7Dto.getJanStatus());
				if (respGstr7Dto.getJanTimestamp() != null) {
					january.append(" ");
					january.append(respGstr7Dto.getJanTimestamp());
				}
				gstr7SumDto.setJanuary(january.toString());

				StringBuilder february = new StringBuilder();
				february.append(respGstr7Dto.getFebStatus());
				if (respGstr7Dto.getFebTimeStamp() != null) {
					february.append(" ");
					february.append(respGstr7Dto.getFebTimeStamp());
				}
				gstr7SumDto.setFebruary(february.toString());
				StringBuilder march = new StringBuilder();
				march.append(respGstr7Dto.getMarchStatus());
				if (respGstr7Dto.getMarchTimestamp() != null) {
					march.append(" ");
					march.append(respGstr7Dto.getMarchTimestamp());
				}
				gstr7SumDto.setMarch(march.toString());

				StringBuilder april = new StringBuilder();
				april.append(respGstr7Dto.getAprilStatus());

				if (respGstr7Dto.getApriltimestamp() != null) {
					april.append(" ");
					april.append(respGstr7Dto.getApriltimestamp());
				}
				gstr7SumDto.setApril(april.toString());

				StringBuilder may = new StringBuilder();
				may.append(respGstr7Dto.getMayStatus());
				if (respGstr7Dto.getMayTimeStamp() != null) {
					may.append(" ");
					may.append(respGstr7Dto.getMayTimeStamp());
				}
				gstr7SumDto.setMay(may.toString());

				StringBuilder june = new StringBuilder();
				june.append(respGstr7Dto.getJuneStatus());
				if (respGstr7Dto.getJuneTimeStamp() != null) {
					june.append(" ");
					june.append(respGstr7Dto.getJuneTimeStamp());
				}
				gstr7SumDto.setJune(june.toString());

				StringBuilder july = new StringBuilder();
				july.append(respGstr7Dto.getJulyStatus());
				if (respGstr7Dto.getJulyTimestamp() != null) {
					july.append(" ");
					july.append(respGstr7Dto.getJulyTimestamp());
				}
				gstr7SumDto.setJuly(july.toString());

				StringBuilder august = new StringBuilder();
				august.append(respGstr7Dto.getAugStatus());

				if (respGstr7Dto.getAugTimeStamp() != null) {
					august.append(" ");
					august.append(respGstr7Dto.getAugTimeStamp());
				}
				gstr7SumDto.setAugust(august.toString());

				StringBuilder sep = new StringBuilder();
				sep.append(respGstr7Dto.getSepStatus());
				if (respGstr7Dto.getSepTimeStamp() != null) {
					sep.append(" ");
					sep.append(respGstr7Dto.getSepTimeStamp());
				}
				gstr7SumDto.setSeptember(sep.toString());

				StringBuilder oct = new StringBuilder();
				oct.append(respGstr7Dto.getOctStatus());
				if (respGstr7Dto.getOctTimestamp() != null) {
					oct.append(" ");
					oct.append(respGstr7Dto.getOctTimestamp());
				}

				gstr7SumDto.setOctober(oct.toString());

				StringBuilder nov = new StringBuilder();
				nov.append(respGstr7Dto.getNovStatus());
				if (respGstr7Dto.getNovTimeStamp() != null) {
					nov.append(" ");
					nov.append(respGstr7Dto.getNovTimeStamp());
				}
				gstr7SumDto.setNovember(nov.toString());

				StringBuilder dec = new StringBuilder();
				dec.append(respGstr7Dto.getDecStatus());
				if (respGstr7Dto.getDecTimestamp() != null) {
					dec.append(" ");
					dec.append(respGstr7Dto.getDecTimestamp());
				}
				gstr7SumDto.setDecember(dec.toString());
			});
		}
		itemDto.setGstr7Summ(gstr7SumDto);

		// Gstr9 Compliance History Status and TimeStamp.

		gstr2AProcDataRecReqDto.setReturnType("GSTR9");
		ComplainceHistoryGstr9SumDto gstr9SumDto = new ComplainceHistoryGstr9SumDto();
		List<ComplienceSummeryRespDto> respGstr9Dtos = compSumGstr9Service
				.findcomplienceSummeryRecords(gstr2AProcDataRecReqDto);
		if (respGstr9Dtos != null && !respGstr9Dtos.isEmpty()
				&& gstr1RegTypes != null && !gstr1RegTypes.isEmpty()) {
			respGstr9Dtos.forEach(respGstr9Dto -> {
				gstr9SumDto.setLastUpdatedDate(respGstr9Dto.getInitiateTime()
						+ " " + respGstr9Dto.getInitiatestatus());
				gstr9SumDto.setAckNo(respGstr9Dto.getAckNo());
				gstr9SumDto.setGstn(respGstr9Dto.getGstin());
				gstr9SumDto.setState(respGstr9Dto.getState());
				gstr9SumDto.setStatus(regType);
				gstr9SumDto.setFileStatus(respGstr9Dto.getFilingStatus());
				gstr9SumDto.setDdate(respGstr9Dto.getDate());
				gstr9SumDto.setTtime(respGstr9Dto.getTime());
			});
		}
		itemDto.setGstr9SumDto(gstr9SumDto);

		// Itc04 Compliance History summary history

		gstr2AProcDataRecReqDto.setReturnType("ITC04");
		List<ComplienceSummeryRespDto> respItc04Dtos = complienceSummeryService
				.findcomplienceSummeryRecords(gstr2AProcDataRecReqDto);
		ComplainceHistoryItc04SumDto itc04SumDto = new ComplainceHistoryItc04SumDto();
		if (respItc04Dtos != null && !respItc04Dtos.isEmpty()
				&& gstr1RegTypes != null && !gstr1RegTypes.isEmpty()) {
			respItc04Dtos.forEach(respItc04Dto -> {
				itc04SumDto.setGstn(gstin);
				itc04SumDto.setState(respItc04Dto.getState());
				itc04SumDto.setStatus(regType);
				itc04SumDto.setLastUpdatedDate(respItc04Dto.getInitiateTime()
						+ " " + respItc04Dto.getInitiatestatus());
				StringBuilder q1aprJun = new StringBuilder();
				q1aprJun.append(respItc04Dto.getQ1Status());
				if (respItc04Dto.getQ1Timestamp() != null) {
					q1aprJun.append(" ");
					q1aprJun.append(respItc04Dto.getQ1Timestamp());
				}
				itc04SumDto.setQ1aprJun(q1aprJun.toString());

				StringBuilder q2julSep = new StringBuilder();
				q2julSep.append(respItc04Dto.getQ2Status());
				if (respItc04Dto.getQ2Timestamp() != null) {
					q2julSep.append(" ");
					q2julSep.append(respItc04Dto.getQ2Timestamp());
				}
				itc04SumDto.setQ2julSep(q2julSep.toString());

				StringBuilder q3octDec = new StringBuilder();
				q3octDec.append(respItc04Dto.getQ3Status());
				if (respItc04Dto.getQ3Timestamp() != null) {
					q3octDec.append(" ");
					q3octDec.append(respItc04Dto.getQ3Timestamp());
				}
				itc04SumDto.setQ3octDec(q3octDec.toString());

				StringBuilder q4janMar = new StringBuilder();
				q4janMar.append(respItc04Dto.getQ4Status());
				if (respItc04Dto.getQ4Timestamp() != null) {
					q4janMar.append(" ");
					q4janMar.append(respItc04Dto.getQ4Timestamp());
				}
				itc04SumDto.setQ4janMar(q4janMar.toString());
			});
		}
		itemDto.setItc04SumDto(itc04SumDto);

		itemDto.setFiYear(finYear.toString());
		itemDtos.add(itemDto);
		return itemDtos;
	}
}
