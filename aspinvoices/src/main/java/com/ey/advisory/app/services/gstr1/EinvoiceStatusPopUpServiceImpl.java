package com.ey.advisory.app.services.gstr1;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
/**
 * @author Balakrishna.S
 */
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.beust.jcommander.internal.Lists;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.daos.client.Gstr1PopUpRecordsDao;
import com.ey.advisory.common.GenUtil;
import com.google.common.collect.Sets;

@Component("EinvoiceStatusPopUpServiceImpl")
public class EinvoiceStatusPopUpServiceImpl {
	
	
	@Autowired
	@Qualifier("EinvoicePopUpStatusRecordsDaoImpl")
	private Gstr1PopUpRecordsDao gstr1PopUpRecordsDao;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnDetailRepository;

	private String taxPeriodFrom;

	public Gstr1PopScreenRecordsFinalResponseDto fetchRec(
			Gstr1PopScreenRecordsRequestDto dto) throws Exception {

		List<String> reqGstins = dto.getGstins();
		if (CollectionUtils.isNotEmpty(reqGstins)) {
			Gstr1PopScreenRecordsFinalResponseDto finalResponseDto = gstr1PopUpRecordsDao
					.fetchProcessStatusData(dto, Lists.newArrayList());
			return filterDataByGstins(dto.getGstins(), finalResponseDto,dto);
		} else {
			List<String> gstins = gstnDetailRepository
					.findgstinByEntityId(Long.parseLong(dto.getEntityId()));
			Gstr1PopScreenRecordsFinalResponseDto finalResponseDto = gstr1PopUpRecordsDao
					.fetchProcessStatusData(dto, gstins);
			return filterDataByGstins(gstins, finalResponseDto,dto);
		}
	}

	private Gstr1PopScreenRecordsFinalResponseDto filterDataByGstins(
			List<String> gstins,
			Gstr1PopScreenRecordsFinalResponseDto finalDto,Gstr1PopScreenRecordsRequestDto reqDto) {
		Set<String> gstinSet = Sets.newHashSet(gstins);
		Gstr1PopScreenRecordsFinalResponseDto finalResponseDto = new Gstr1PopScreenRecordsFinalResponseDto();
		if (!CollectionUtils.isEmpty(gstinSet)) {
			List<Gstr1PopScreenRecordsResponseDto> lastCallList = finalDto
					.getLastCall();
			List<Gstr1PopScreenRecordsResponseDto> lastSuccessList = finalDto
					.getLastSuccess();
			defaultValues(lastCallList, lastSuccessList, gstinSet, reqDto);
			
			Collections.sort(lastCallList, new Comparator<Gstr1PopScreenRecordsResponseDto>() {
				@Override
				public int compare(Gstr1PopScreenRecordsResponseDto respDto1,
						Gstr1PopScreenRecordsResponseDto respDto2) {
					return GenUtil.convertTaxPeriodToInt(respDto1.getTaxPeriod())
							.compareTo(GenUtil.convertTaxPeriodToInt(respDto2.getTaxPeriod()));
				}
			});
			
			Collections.sort(lastSuccessList, new Comparator<Gstr1PopScreenRecordsResponseDto>() {
				@Override
				public int compare(Gstr1PopScreenRecordsResponseDto respDto1,
						Gstr1PopScreenRecordsResponseDto respDto2) {
					return GenUtil.convertTaxPeriodToInt(respDto1.getTaxPeriod())
							.compareTo(GenUtil.convertTaxPeriodToInt(respDto2.getTaxPeriod()));
				}
			});
			
			finalResponseDto.setLastCall(lastCallList);
			finalResponseDto.setLastSuccess(lastSuccessList);
		}
		return finalResponseDto;
	}

	private static Gstr1PopScreenRecordsResponseDto buildDummyRespDto(
			final String gstin,String taxPeriod) {
		Gstr1PopScreenRecordsResponseDto ret1ProcessedRecordsResponseDto = new Gstr1PopScreenRecordsResponseDto();
		ret1ProcessedRecordsResponseDto.setTaxPeriod(taxPeriod);
		ret1ProcessedRecordsResponseDto.setGstin(gstin);
		ret1ProcessedRecordsResponseDto.setB2bTimeStamp("");
		ret1ProcessedRecordsResponseDto.setB2bStatus("");
		ret1ProcessedRecordsResponseDto.setCdnrTimeStamp("");
		ret1ProcessedRecordsResponseDto.setCdnrStatus("");
		ret1ProcessedRecordsResponseDto.setCdnurTimeStamp("");
		ret1ProcessedRecordsResponseDto.setCdnurStatus("");
		ret1ProcessedRecordsResponseDto.setExportsTimestamp("");
		ret1ProcessedRecordsResponseDto.setExportStatus("");
		return ret1ProcessedRecordsResponseDto;
	}
	private static void defaultValues(List<Gstr1PopScreenRecordsResponseDto> lastCallList,
			List<Gstr1PopScreenRecordsResponseDto> lastSuccessList,
			Set<String> gstinSet,Gstr1PopScreenRecordsRequestDto reqDto){
		for (String gstin : gstinSet) {
			List<String> thetaxPeriods = getThetaxPeriods(reqDto.getTaxPeriodFrom(), reqDto.getTaxPeriodTo());
		if (lastCallList == null || lastCallList.isEmpty()) {
				for (String taxPeriod : thetaxPeriods) {
					lastCallList.add(buildDummyRespDto(gstin, taxPeriod));
				}
			}else{
				List<Gstr1PopScreenRecordsResponseDto> lastCallnewList=new ArrayList<>();
				for(Gstr1PopScreenRecordsResponseDto dto:lastCallList){
					if(thetaxPeriods.contains(dto.getTaxPeriod())) continue;
					lastCallnewList.add(buildDummyRespDto(gstin, dto.getTaxPeriod()));
				}
				lastCallList.addAll(lastCallnewList);
			}
		if (lastSuccessList == null || lastSuccessList.isEmpty()) {
				for (String taxPeriod : thetaxPeriods) {
					lastSuccessList.add(buildDummyRespDto(gstin, taxPeriod));
				}
			}else{

				List<Gstr1PopScreenRecordsResponseDto> lastSuccessnewList=new ArrayList<>();
				for(Gstr1PopScreenRecordsResponseDto dto:lastSuccessList){
					if(thetaxPeriods.contains(dto.getTaxPeriod())) continue;
					lastSuccessList.add(buildDummyRespDto(gstin, dto.getTaxPeriod()));
				}
				lastSuccessList.addAll(lastSuccessnewList);
			
			}
		}
	}
	private static List<String> getThetaxPeriods(String fromTaxPeriod,String toTaxPeriod){

		List<String> taxPeriodList = new LinkedList<>();
		LocalDate startDate = LocalDate.of(Integer.parseInt(fromTaxPeriod.substring(2)),
				Integer.parseInt(fromTaxPeriod.substring(0, 2)), 01);
		LocalDate endDate = LocalDate.of(Integer.parseInt(toTaxPeriod.substring(2)),
				Integer.parseInt(toTaxPeriod.substring(0, 2)), 01);
		long numOfMonths = ChronoUnit.MONTHS.between(startDate, endDate) + 1;
		if (numOfMonths > 0) {
			List<LocalDate> listOfDates = Stream.iterate(startDate, date -> date.plusMonths(1)).limit(numOfMonths)
					.collect(Collectors.toList());
			listOfDates.forEach(localDate -> taxPeriodList
					.add(localDate.getMonthValue() < 10 ? "0" + localDate.getMonthValue() + "" + localDate.getYear()
							: localDate.getMonthValue() + "" + localDate.getYear()));
	}

		return taxPeriodList;
		
	}
}
