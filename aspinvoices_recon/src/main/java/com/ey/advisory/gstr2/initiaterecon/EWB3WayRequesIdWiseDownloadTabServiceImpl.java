package com.ey.advisory.gstr2.initiaterecon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.asprecon.EWB3WayReconDownloadReportsEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.EWB3WayDownloadReconReportsRepository;
import com.ey.advisory.core.dto.Gstr2RequesIdWiseDownloadTabDto;

/**
 * @author vishal.verma
 *
 */

@Component("EWB3WayRequesIdWiseDownloadTabServiceImpl")
public class EWB3WayRequesIdWiseDownloadTabServiceImpl
		implements EWB3WayRequesIdWiseDownloadTabService {

	@Autowired
	@Qualifier("EWB3WayDownloadReconReportsRepository")
	EWB3WayDownloadReconReportsRepository reconDownlRepo;

	@Override
	public List<Gstr2RequesIdWiseDownloadTabDto> getDownloadData(Long configId) {

		List<Gstr2RequesIdWiseDownloadTabDto> newRespList = new ArrayList<>();

		List<EWB3WayReconDownloadReportsEntity> entity = reconDownlRepo
					.getDataList(configId);
		
		List<Gstr2RequesIdWiseDownloadTabDto> respList = new ArrayList<>();

		entity.forEach( en -> {
			Gstr2RequesIdWiseDownloadTabDto dto = new Gstr2RequesIdWiseDownloadTabDto();
			dto.setFlag(en.getIsDownloadable());
			if(en.getReportType().equalsIgnoreCase("Consolidated Recon Report")){
				dto.setReportName("Consolidated Recon Report");
			} else if(en.getReportType().equalsIgnoreCase("Drop Out Records")){
				dto.setReportName("Drop Out Records");
			}
			dto.setPath(en.getPath());
			respList.add(dto);

		});

			Set<Boolean> flagSet = new HashSet<>();
			List<Gstr2RequesIdWiseDownloadTabDto> dtoList = new ArrayList<>();
			for( int i = 0; i < respList.size(); i++ ){
				Gstr2RequesIdWiseDownloadTabDto resp = respList.get( i );
				Boolean flag = resp.isFlag();
				flagSet.add(flag);
				if(resp.isFlag() == true){
					dtoList.add(resp);
				}
			}

			if (!flagSet.contains(true)) {
				return new ArrayList<>();
			}

			Map<String, Gstr2RequesIdWiseDownloadTabDto> respMap = dtoList
					.stream()
					.collect(Collectors.toMap(o -> o.getReportName(), o -> o));

			List<String> desirList = Arrays.asList("Drop Out Records","Consolidated Recon Report");
					
			for (String repType : desirList) {

				if (respMap.containsKey(repType)) {
					newRespList.add(respMap.get(repType));}
			}
			
			return newRespList;
	}

	
}
