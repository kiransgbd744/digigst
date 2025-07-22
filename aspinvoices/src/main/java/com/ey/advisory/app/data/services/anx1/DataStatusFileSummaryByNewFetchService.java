package com.ey.advisory.app.data.services.anx1;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.javatuples.Ennead;
import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.DataStatusFileSummaryFinalRespDto;
import com.ey.advisory.app.docs.dto.DataStatusFileSummaryFirstRespDto;
import com.ey.advisory.app.docs.dto.DataStatusFileSummaryRespDto;
import com.ey.advisory.app.docs.dto.DataStatusFileSummarySecondCountRespDto;
import com.ey.advisory.app.docs.dto.anx1.DataStatusFilesummaryReqDto;

@Service("DataStatusFileSummaryByNewFetchService")
public class DataStatusFileSummaryByNewFetchService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DataStatusFileSummaryByNewFetchService.class);
	@Autowired
	@Qualifier("DataStatusFileSummaryFetchService")
	DataStatusFileSummaryFetchService dataStatusFileSummaryFetchService;

	public List<DataStatusFileSummaryFinalRespDto> findDataByFileStatus(
			DataStatusFilesummaryReqDto dataStatusApiSummaryReqDto) {
		List<DataStatusFileSummaryRespDto> finalDataResps = dataStatusFileSummaryFetchService
				.find(dataStatusApiSummaryReqDto);

		Map<String, Map<Triplet<String, String, String>, List<Ennead<String, String, BigInteger, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal>>>> attributeValueMap = finalDataResps
				.stream()
				.collect(Collectors.groupingBy(obj -> obj.getFileName(),
						Collectors.groupingBy(
								obj -> new Triplet<String, String, String>(
										obj.getAuthToken(), obj.getGstin(),
										obj.getReturnPeriod()),
								Collectors.mapping(
										obj -> new Ennead<String, String, BigInteger, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal>(
												obj.getReturnType(),
												obj.getReturnSection(),
												obj.getCount(),
												obj.getTaxableValue(),
												obj.getTotalTaxes(),
												obj.getIgst(), obj.getCgst(),
												obj.getSgst(), obj.getCess()),
										Collectors.toList()))));
		List<DataStatusFileSummaryFinalRespDto> finalRespDtos = new ArrayList<>();

		attributeValueMap.forEach((key, value) -> {
			DataStatusFileSummaryFinalRespDto finalRespDto = new DataStatusFileSummaryFinalRespDto();
			finalRespDto.setFileName(key);

			Map<Triplet<String, String, String>, List<Ennead<String, String, BigInteger, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal>>> gstinsAuthTokenMap = attributeValueMap
					.get(key);
			List<DataStatusFileSummaryFirstRespDto> gstinItems = new ArrayList<>();

			for (Map.Entry<Triplet<String, String, String>, List<Ennead<String, String, BigInteger, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal>>> gstinEntry : gstinsAuthTokenMap
					.entrySet()) {
				Triplet<String, String, String> gstinAuthPair = gstinEntry
						.getKey();
				BigInteger count = BigInteger.ZERO;
				BigDecimal taxableValue = BigDecimal.ZERO;
				BigDecimal totalTaxes = BigDecimal.ZERO;
				BigDecimal igst = BigDecimal.ZERO;
				BigDecimal cgst = BigDecimal.ZERO;
				BigDecimal sgst = BigDecimal.ZERO;
				BigDecimal cess = BigDecimal.ZERO;

				DataStatusFileSummaryFirstRespDto gstinItem = new DataStatusFileSummaryFirstRespDto();
				gstinItem.setAuthToken(gstinAuthPair.getValue0());
				gstinItem.setGstin(gstinAuthPair.getValue1());
				gstinItem.setReturnPeriod(gstinAuthPair.getValue2());
				List<DataStatusFileSummarySecondCountRespDto> seconCountRespDtos = new ArrayList<>();

				List<Ennead<String, String, BigInteger, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal>> listValues = gstinEntry
						.getValue();

				for (Ennead<String, String, BigInteger, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal> values : listValues) {
					DataStatusFileSummarySecondCountRespDto seconCountRespDto = new DataStatusFileSummarySecondCountRespDto();
					seconCountRespDto.setReturnType(values.getValue0());
					seconCountRespDto.setReturnSection(values.getValue1());

					seconCountRespDto.setCount(values.getValue2());
					seconCountRespDto.setTaxableValue(values.getValue3());
					seconCountRespDto.setTotalTaxes(values.getValue4());
					seconCountRespDto.setIgst(values.getValue5());
					seconCountRespDto.setCgst(values.getValue6());
					seconCountRespDto.setSgst(values.getValue7());
					seconCountRespDto.setCess(values.getValue8());
					seconCountRespDtos.add(seconCountRespDto);
					count = count.add(values.getValue2());
					taxableValue = taxableValue.add(values.getValue3());
					totalTaxes = totalTaxes.add(values.getValue4());
					igst = igst.add(values.getValue5());
					cgst = cgst.add(values.getValue6());
					sgst = sgst.add(values.getValue7());
					cess = cess.add(values.getValue8());
				}
				seconCountRespDtos.sort(Comparator.comparing(
						DataStatusFileSummarySecondCountRespDto::getReturnSection));
				gstinItem.setItems(seconCountRespDtos);
				gstinItem.setCount(count);
				gstinItem.setTaxableValue(taxableValue);
				gstinItem.setTotalTaxes(totalTaxes);
				gstinItem.setIgst(igst);
				gstinItem.setCgst(cgst);
				gstinItem.setSgst(sgst);
				gstinItem.setCess(cess);
				gstinItems.add(gstinItem);
			}
			finalRespDto.setItems(gstinItems);
			finalRespDtos.add(finalRespDto);
		});
		return finalRespDtos;
	}
}
