package com.ey.advisory.app.inward.einvoice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */
@Slf4j
@Component("InwardEinvoiceGstinLevelServiceImpl")
public class InwardEinvoiceGstinLevelServiceImpl
		implements InwardEinvoiceGstinLevelService {

	@Autowired
	@Qualifier("InwardEinvoiceGstinLevelDaoImpl")
	private InwardEinvoiceGstinLevelDao dao;

	private static final List<String> supplyList = Arrays.asList("B2B", "SEZWP",
			"SEZWOP", "DXP", "EXPWP", "EXPWOP");

	@Override
	public InwardEinvoiceGstinLevelResponseDto getGstinLevelData(
			InwardEinvoiceGstinLevelReqDto criteria) {

		InwardEinvoiceGstinLevelResponseDto obj = new InwardEinvoiceGstinLevelResponseDto();

		if (LOGGER.isDebugEnabled()) {
			String str = "Inside InwardEinvoiceGstinLevelServiceImpl"
					+ ".getGstinLevelData()";
			LOGGER.debug(str);
		}

		try {

			List<GstinLevelInnerDto> innerList = dao
					.findGstinLevelData(criteria);

			if (LOGGER.isDebugEnabled()) {
				String str = String.format(
						"invoked db resp "
								+ " InwardEinvoiceGstinLevelServiceImpl"
								+ ".getGstinLevelData() innerList {}",
						innerList);
				LOGGER.debug(str);
			}

			// convert inmnerList to list
			Map<String, List<GstinLevelInnerDto>> docTypeMap = innerList
					.stream()
					.collect(Collectors.groupingBy(o -> o.getDocType()));

			for (GstinLevelInnerDto dto : innerList) {

				if (dto.getDocType().equalsIgnoreCase("INV")) {

					List<GstinLevelInnerDto> inv = setDefaultSupply(docTypeMap,
							"INV", dto);
					obj.setInvoice(inv);

				} else if (dto.getDocType().equalsIgnoreCase("CR")) {

					List<GstinLevelInnerDto> cr = setDefaultSupply(docTypeMap,
							"CR", dto);
					obj.setCredit(cr);

				} else if (dto.getDocType().equalsIgnoreCase("DR")) {

					List<GstinLevelInnerDto> dr = setDefaultSupply(docTypeMap,
							"DR", dto);
					obj.setDebit(dr);

				}
			}
			if (LOGGER.isDebugEnabled()) {
				String str = String.format("before returning resp "
						+ " InwardEinvoiceGstinLevelServiceImpl"
						+ ".getGstinLevelData() obj {}", obj);
				LOGGER.debug(str);
			}
			if (obj.getCredit().isEmpty()) {

				List<GstinLevelInnerDto> crList = supplyList.stream()
						.map(o -> setDefault(o, "CR", new GstinLevelInnerDto()))
						.collect(Collectors.toCollection(ArrayList::new));
				
				Collections.sort(crList, Comparator.comparing(GstinLevelInnerDto ::getOrder));

				obj.setCredit(crList);
			}
			if (obj.getDebit().isEmpty()) {

				List<GstinLevelInnerDto> drList = supplyList.stream()
						.map(o -> setDefault(o, "DR", new GstinLevelInnerDto()))
						.collect(Collectors.toCollection(ArrayList::new));

				Collections.sort(drList, Comparator.comparing(GstinLevelInnerDto ::getOrder));
				
				obj.setDebit(drList);
			}
			if (obj.getInvoice().isEmpty()) {

				List<GstinLevelInnerDto> invList = supplyList.stream().map(
						o -> setDefault(o, "INV", new GstinLevelInnerDto()))
						.collect(Collectors.toCollection(ArrayList::new));

				Collections.sort(invList, Comparator.comparing(GstinLevelInnerDto ::getOrder));
				obj.setInvoice(invList);
			}

		} catch (Exception e) {
			LOGGER.error(
					"InwardEinvoiceGstinLevelServiceImpl.getGstinLevelData:"
							+ " Error occuerd {} ",
					e);
			throw new AppException(e);
		}
		
		return obj;

	}

	/**
	 * @param docTypeMap
	 * @param supplyType
	 * @param dto
	 * @return
	 */
	private List<GstinLevelInnerDto> setDefaultSupply(
			Map<String, List<GstinLevelInnerDto>> docTypeMap, String suppType,
			GstinLevelInnerDto dto) {
		
		List<GstinLevelInnerDto> inv = docTypeMap.get(dto.getDocType());

		
		for (GstinLevelInnerDto invDto : inv)
		{
			switch(invDto.getSupplyType())
			{
			case "B2B":
				invDto.setOrder("A");
				break;
			case "SEZWP":
				invDto.setOrder("B");
				break;
			case "SEZWOP":
				invDto.setOrder("C");
				break;
			case "DXP":
				invDto.setOrder("D");
				break;
			case "EXPWP":
				invDto.setOrder("E");
				break;
			case "EXPWOP":
				invDto.setOrder("F");
				break;
			}

		}
		
		List<String> supplyList = new ArrayList<>
		(Arrays.asList("B2B", "SEZWP", "SEZWOP", "DXP", "EXPWP", "EXPWOP"));


		ArrayList<String> invSupType = inv.stream().map(o -> o.getSupplyType())
				.collect(Collectors.toCollection(ArrayList::new));
		
		supplyList.removeAll(invSupType);

		List<GstinLevelInnerDto> invDefault = supplyList.stream()
				.map(o -> setDefault(o, suppType, new GstinLevelInnerDto()))
				.collect(Collectors.toCollection(ArrayList::new));

		inv.addAll(invDefault);
		
		Collections.sort(inv, Comparator.comparing(GstinLevelInnerDto ::getOrder));
		
		return inv;
	}

	private GstinLevelInnerDto setDefault(String supplyType, String docType,
			GstinLevelInnerDto obj) {
		BigDecimal zero = BigDecimal.ZERO;
		obj.setCess(zero);
		obj.setCgst(zero);
		obj.setCount(0);
		obj.setDocType(docType);
		obj.setIgst(zero);
		obj.setSgst(zero);
		obj.setSupplyType(supplyType);
		obj.setTaxableVal(zero);
		obj.setTotalTax(zero);
		obj.setTotInvVal(zero);
		switch(supplyType)
		{
		case "B2B":
			obj.setOrder("A");
			break;
		case "SEZWP":
			obj.setOrder("B");
			break;
		case "SEZWOP":
			obj.setOrder("C");
			break;
		case "DXP":
			obj.setOrder("D");
			break;
		case "EXPWP":
			obj.setOrder("E");
			break;
		case "EXPWOP":
			obj.setOrder("F");
			break;
		}

		return obj;
	}

}
