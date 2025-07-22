package com.ey.advisory.service.gstr1.sales.register;

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

import com.ey.advisory.app.data.repositories.client.SalesRegisterDownloadReconReportsRepository;
import com.ey.advisory.core.dto.Gstr2RequesIdWiseDownloadTabDto;

/**
 * @author Shashikant.Shukla
 *
 */

@Component("SalesRegisterRequesIdWiseDownloadTabServiceImpl")
public class SalesRegisterRequesIdWiseDownloadTabServiceImpl
		implements SalesRegisterRequesIdWiseDownloadTabService {

	@Autowired
	@Qualifier("SalesRegisterDownloadReconReportsRepository")
	SalesRegisterDownloadReconReportsRepository reconDownlRepo;

	@Override
	public List<Gstr2RequesIdWiseDownloadTabDto> getDownloadData(Long configId) {

		List<Gstr2RequesIdWiseDownloadTabDto> newRespList = new ArrayList<>();

		List<SalesRegisterReconDownloadReportsEntity> entity = reconDownlRepo
					.getDataList(configId);
		
		List<Gstr2RequesIdWiseDownloadTabDto> respList = new ArrayList<>();

		entity.forEach( en -> {
			Gstr2RequesIdWiseDownloadTabDto dto = new Gstr2RequesIdWiseDownloadTabDto();
			dto.setFlag(en.getIsDownloadable());
				dto.setReportName(en.getReportType());
				
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

			List<String> desirList = Arrays.asList("Consolidated SRVSDIGI Report");
					
			for (String repType : desirList) {

				if (respMap.containsKey(repType)) {
					newRespList.add(respMap.get(repType));}
			}
			
			return newRespList;
	}

	
}
