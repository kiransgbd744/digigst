package com.ey.advisory.app.docs.service.gstr6;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.ey.advisory.admin.data.entities.client.Gstr6DistributionEntity;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6DistributedSummaryScreenRequestDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6DistributedSummaryScreenResponseDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * 
 * @author Dibyakanta.Sahoo
 *
 */

public interface Gstr6DistributedSummaryService {

	List<Gstr6DistributedSummaryScreenResponseDto> getGstr6DistributedEliSummaryList(
			Annexure1SummaryReqDto reqDto);
	

	List<Gstr6DistributedSummaryScreenResponseDto> getGstr6DistributedInEliSummaryList(
			Annexure1SummaryReqDto reqDto);

	List<Gstr6DistributedSummaryScreenResponseDto> getGstr6ReDistributedSummaryList(
			Annexure1SummaryReqDto reqDto);

	List<Gstr6DistributedSummaryScreenResponseDto> getGstr6ReDistributedInEligibleSummaryList(
			Annexure1SummaryReqDto reqDto);

	public void saveGstr6DistributedSummaryData(
			List<Gstr6DistributedSummaryScreenRequestDto> dtos);

	public void deleteEntity(
			final List<Gstr6DistributedSummaryScreenRequestDto> dtos);
	
	List<Gstr6DistributedSummaryScreenResponseDto> getGstr6DistributedEliSummaryListPagination(
			Annexure1SummaryReqDto reqDto, Pageable pageReq);


	List<Gstr6DistributedSummaryScreenResponseDto> getGstr6DistributedInEliSummaryListPagination(
			Annexure1SummaryReqDto reqDto, Pageable pageReq);


	List<Gstr6DistributedSummaryScreenResponseDto> getGstr6ReDistributedSummaryListPagination(
			Annexure1SummaryReqDto reqDto, Pageable pageReq);


	List<Gstr6DistributedSummaryScreenResponseDto> getGstr6ReDistributedInEligibleSummaryListPagination(
			Annexure1SummaryReqDto reqDto, Pageable pageReq);
	
	int getGstr6DistributedEliSummaryListCount(
			Annexure1SummaryReqDto reqDto);
	
	int getGstr6DistributedInEliSummaryListCount(
			Annexure1SummaryReqDto reqDto);
	
	int getGstr6ReDistributedSummaryListCount(
			Annexure1SummaryReqDto reqDto);
	
	int getGstr6ReDistributedInEligibleSummaryListCount(
			Annexure1SummaryReqDto reqDto);

}
