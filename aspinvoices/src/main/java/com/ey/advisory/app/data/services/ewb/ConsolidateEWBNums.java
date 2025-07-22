/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.CewbEntity;
import com.ey.advisory.app.data.repositories.client.CewbRepository;

/**
 * @author Laxmi.Salukuti
 *
 */
@Service("ConsolidateEWBNums")
public class ConsolidateEWBNums {

	@Autowired
	@Qualifier("CewbRepository")
	private CewbRepository cewbRepository;

	public List<String> getCEWBNum(Long serialNo, Long fileId) {

		List<CewbEntity> cewbList = cewbRepository.getActiveData();

		List<CewbEntity> dtoList = cewbList.stream()
				.filter(dto -> dto.getSNo().equals(serialNo)
						&& dto.getFileId().equals(fileId))
				.collect(Collectors.toList());

		List<String> ewbNums = new ArrayList<>();
		Set<String> set = new LinkedHashSet<>();
		for (CewbEntity ewb : dtoList) {
			String ewbNo = ewb.getEwbNo();
			ewbNums.add(ewbNo);
		}
		set.addAll(ewbNums);
		ewbNums.clear();
		ewbNums.addAll(set);
		return ewbNums;
	}
}
