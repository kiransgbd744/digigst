/**
 * 
 */
package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryEcomResp1Dto;

/**
 * @author BalaKrishna S
 *
 */
@Service("anx1EcomEYFinalStructure")
public class Anx1EcomEYFinalStructure {
	

	public List<Annexure1SummaryEcomResp1Dto> getEcomEyList(
			List<Annexure1SummaryEcomResp1Dto> ecomEYList,
			List<Annexure1SummaryEcomResp1Dto> eySummaryListFromView) {

		List<Annexure1SummaryEcomResp1Dto> viewTable4Filtered = eySummaryListFromView
				.stream().filter(p -> "4".equalsIgnoreCase(p.getTableSection()))
				.collect(Collectors.toList());
		
		
		// If Table 4 filtered list is not null
		if (viewTable4Filtered != null & viewTable4Filtered.size() > 0) {
			// then filter default List for 4A
			List<Annexure1SummaryEcomResp1Dto> defaultTable4Filtered = ecomEYList
					.stream()
					.filter(p -> "4".equalsIgnoreCase(p.getTableSection()))
					.collect(Collectors.toList());

			// If the default filtered list is not null
			defaultTable4Filtered.forEach(defaultTable4 -> {
				// then remove it from List
				ecomEYList.remove(defaultTable4);
			});

			viewTable4Filtered.forEach(viewTable4 -> {
				Annexure1SummaryEcomResp1Dto summaryRespDto = 
						new Annexure1SummaryEcomResp1Dto();
				summaryRespDto.setTableSection(viewTable4.getTableSection());
				summaryRespDto.setEyCount(viewTable4.getEyCount());
				summaryRespDto.setEySupplyMade(viewTable4.getEySupplyMade());
				summaryRespDto.setEySupplyReturn(viewTable4.getEySupplyReturn());
				summaryRespDto.setEyTaxPayble(viewTable4.getEyTaxPayble());
				summaryRespDto.setEyNetSupply(viewTable4.getEyNetSupply());
				summaryRespDto.setEyIgst(viewTable4.getEyIgst());
				summaryRespDto.setEySgst(viewTable4.getEySgst());
				summaryRespDto.setEyCgst(viewTable4.getEyCgst());
				summaryRespDto.setEyCess(viewTable4.getEyCess());
				summaryRespDto.setGstnCount(viewTable4.getGstnCount());
				summaryRespDto.setGstnSupplyMade(viewTable4.getGstnSupplyMade());
				summaryRespDto.setGstnSupplyReturn(viewTable4.getGstnSupplyReturn());
				summaryRespDto.setGstnTaxPayble(viewTable4.getGstnTaxPayble());
				summaryRespDto.setGstnNetSupply(viewTable4.getGstnNetSupply());
				summaryRespDto.setGstnIgst(viewTable4.getGstnIgst());
				summaryRespDto.setGstnSgst(viewTable4.getGstnSgst());
				summaryRespDto.setGstnCgst(viewTable4.getGstnCgst());
				summaryRespDto.setGstnCess(viewTable4.getGstnCess());
				summaryRespDto.setDiffCount(viewTable4.getDiffCount());
				summaryRespDto.setDiffSupplyMade(viewTable4.getDiffSupplyMade());
				summaryRespDto.setDiffSupplyReturn(viewTable4.getDiffSupplyReturn());
				summaryRespDto.setDiffTaxPayble(viewTable4.getDiffTaxPayble());
				summaryRespDto.setGstnNetSupply(viewTable4.getGstnNetSupply());
				summaryRespDto.setDiffIgst(viewTable4.getDiffIgst());
				summaryRespDto.setDiffSgst(viewTable4.getDiffSgst());
				summaryRespDto.setDiffCgst(viewTable4.getDiffCgst());
				summaryRespDto.setDiffCess(viewTable4.getDiffCess());
				summaryRespDto.setMemoCount(viewTable4.getMemoCount());
				summaryRespDto.setMemoSupplyMade(viewTable4.getMemoSupplyMade());
				summaryRespDto.setMemoSupplyReturn(viewTable4.getMemoSupplyReturn());
				summaryRespDto.setMemoTaxPayble(viewTable4.getMemoTaxPayble());
				summaryRespDto.setMemoNetSupply(viewTable4.getMemoNetSupply());
				summaryRespDto.setMemoIgst(viewTable4.getMemoIgst());
				summaryRespDto.setMemoSgst(viewTable4.getMemoSgst());
				summaryRespDto.setMemoCgst(viewTable4.getMemoCgst());
				summaryRespDto.setMemoCess(viewTable4.getMemoCess());
				
				ecomEYList.add(summaryRespDto);
			});
		}
         return ecomEYList;

	}
}
