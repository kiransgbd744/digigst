package com.ey.advisory.app.services.aspreports;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.daos.client.MasterAspReportsDao;
import com.ey.advisory.app.data.entities.simplified.client.MasterAspReportsEntity;
import com.ey.advisory.app.services.aspreports.MasterAspReportsDetailsResponseDto.MasterAspReportsAttributesDto;
import com.ey.advisory.app.services.aspreports.MasterAspReportsDetailsResponseDto.MasterAspReportsDocTypeDto;
import com.ey.advisory.app.services.aspreports.MasterAspReportsDetailsResponseDto.MasterAspReportsSupplyTypeDto;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("MasterAspReportsServiceImpl")
public class MasterAspReportsServiceImpl implements MasterAspReportsService {
	public static final Logger LOGGER = LoggerFactory
			.getLogger(MasterAspReportsServiceImpl.class);

	@Autowired
	@Qualifier("MasterAspReportsDaoImpl")
	private MasterAspReportsDao masterAspReportsDao;

	@Override
	public MasterAspReportsResponseDto getMasterAspReports() throws Exception {
		List<MasterAspReportsEntity> reportList = masterAspReportsDao
				.getMasterAspReports();
		MasterAspReportsResponseDto aspReportsResponseDto = new MasterAspReportsResponseDto();

		Map<String, Set<String>> reportTransMap = new LinkedHashMap<>();
		reportList.stream().forEach(report -> {
			String reportName = report.getReports();
			String transType = report.getTransType();
			if (reportTransMap.containsKey(reportName)) {
				Set<String> transList = reportTransMap.get(reportName);
				transList.add(transType);
				reportTransMap.put(reportName, transList);
			} else {
				Set<String> transList = new LinkedHashSet<>();
				transList.add(transType);
				reportTransMap.put(reportName, transList);
			}
		});

		Map<String, Map<String, List<MasterAspReportsEntity>>> map = new LinkedHashMap<>();

		reportTransMap.keySet().forEach(reportName -> {
			Map<String, List<MasterAspReportsEntity>> itemsMap = new LinkedHashMap<>();
			Set<String> transSet = reportTransMap.get(reportName);
			transSet.forEach(transType -> {
				List<MasterAspReportsEntity> selectionList = reportList.stream()
						.filter(report -> (reportName
								.equals(report.getReports())
								&& transType.equals(report.getTransType())))
						.collect(Collectors.toList());
				itemsMap.put(transType, selectionList);
			});
			map.put(reportName, itemsMap);
		});

		List<MasterAspReportsParentDto> reportsParentDtos = new LinkedList<>();
		map.keySet().forEach(reportKey -> {
			MasterAspReportsParentDto parentDto = new MasterAspReportsParentDto();
			parentDto.setReportsName(reportKey);

			List<MasterAspReportsChildDto> childItems = new ArrayList<MasterAspReportsChildDto>();

			Map<String, List<MasterAspReportsEntity>> transMap = map
					.get(reportKey);
			transMap.keySet().forEach(transType -> {
				MasterAspReportsChildDto childDto = new MasterAspReportsChildDto();
				childDto.setReportsName(transType);

				List<MasterAspReportsChildItemDto> childItemDtos = new ArrayList<>();
				List<MasterAspReportsEntity> itemsList = transMap
						.get(transType);
				itemsList.forEach(item -> {
					MasterAspReportsChildItemDto childItemDto = new MasterAspReportsChildItemDto();
					childItemDto.setReportsKey(item.getReportsKey());
					childItemDto.setReportsName(item.getReportsType());
					childItemDtos.add(childItemDto);
				});
				childDto.setItems(childItemDtos);
				childItems.add(childDto);
			});

			parentDto.setItems(childItems);

			reportsParentDtos.add(parentDto);
		});
		aspReportsResponseDto.setList(reportsParentDtos);
		return aspReportsResponseDto;
	}

	@Override
	public MasterAspReportsDetailsResponseDto fetchDetailsReports(
			MasterAspReportsDetailsRequestDto masterDetailsReportsRequestDto) {
		String reportsKey = masterDetailsReportsRequestDto.getReportsKey();

		List<Object[]> supplyTypeList = masterAspReportsDao
				.loadSupplyTypeDetails(reportsKey);
		List<Object[]> documentTypeList = masterAspReportsDao
				.loadDocumentTypeDetails(reportsKey);
		List<Object[]> attributesList = masterAspReportsDao
				.loadAttributes(reportsKey);

		List<MasterAspReportsDocTypeDto> docTypeDtoList = new ArrayList<>();
		List<MasterAspReportsSupplyTypeDto> supplyTypeDtoList = new ArrayList<>();
		List<MasterAspReportsAttributesDto> attributesDtoList = new ArrayList<>();

		MasterAspReportsDetailsResponseDto aspReportsDetailsResponseDto = new MasterAspReportsDetailsResponseDto();
		if (!supplyTypeList.isEmpty() && supplyTypeList.size() > 0) {
			for (Object[] sup : supplyTypeList) {
				MasterAspReportsSupplyTypeDto aspReportsSupplyTypeDto = aspReportsDetailsResponseDto.new MasterAspReportsSupplyTypeDto();
				aspReportsSupplyTypeDto.setSupplyTypeKey((String) sup[0]);
				aspReportsSupplyTypeDto.setSupplyTypeName((String) sup[1]);
				supplyTypeDtoList.add(aspReportsSupplyTypeDto);
			}
		}

		if (!documentTypeList.isEmpty() && documentTypeList.size() > 0) {
			for (Object[] doc : documentTypeList) {
				MasterAspReportsDocTypeDto aspReportsSupplyTypeDto = aspReportsDetailsResponseDto.new MasterAspReportsDocTypeDto();
				aspReportsSupplyTypeDto.setDocTypeKey((String) doc[0]);
				aspReportsSupplyTypeDto.setDocTypeName((String) doc[1]);
				docTypeDtoList.add(aspReportsSupplyTypeDto);
			}
		}

		if (!attributesList.isEmpty() && attributesList.size() > 0) {
			for (Object att : attributesList) {
				MasterAspReportsAttributesDto aspReportsAttributesDto = aspReportsDetailsResponseDto.new MasterAspReportsAttributesDto();
				aspReportsAttributesDto.setAttributesName((String) att);
				attributesDtoList.add(aspReportsAttributesDto);
			}
		}

		aspReportsDetailsResponseDto.setDocType(docTypeDtoList);
		aspReportsDetailsResponseDto.setSupplyType(supplyTypeDtoList);
		aspReportsDetailsResponseDto.setAttributes(attributesDtoList);

		return aspReportsDetailsResponseDto;
	}

}
