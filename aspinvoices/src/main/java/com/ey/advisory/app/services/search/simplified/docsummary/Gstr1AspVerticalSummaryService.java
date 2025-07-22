package com.ey.advisory.app.services.search.simplified.docsummary;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.daos.client.simplified.Gstr1AspVerticalSectionDaoImpl;
import com.ey.advisory.app.data.services.Gstr1A.Gstr1AAspVerticalSectionDaoImpl;
import com.ey.advisory.app.docs.dto.Gstr1VerticalSummaryRespDto;
import com.ey.advisory.app.docs.dto.simplified.Ret1AspVerticalSummaryDto;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Service("Gstr1AspVerticalSummaryService")
public class Gstr1AspVerticalSummaryService {

	@Autowired
	@Qualifier("Gstr1AspVerticalSectionDaoImpl")
	Gstr1AspVerticalSectionDaoImpl loadData;
	
	@Autowired
	@Qualifier("Gstr1AAspVerticalSectionDaoImpl")
	Gstr1AAspVerticalSectionDaoImpl loadDataGstr1A;
	
	
	public List<Ret1AspVerticalSummaryDto> find(Annexure1SummaryReqDto req) {
		// TODO Auto-generated method stub
		
		List<Ret1AspVerticalSummaryDto> aspvertical = new ArrayList<>();
		
		if(req.getIsGstr1a())
		{
			aspvertical = loadDataGstr1A.lateBasicSummarySection(req);
			
		}else
		{
		aspvertical = loadData
				.lateBasicSummarySection(req);
		}
		if(!aspvertical.isEmpty()){
			
			Ret1AspVerticalSummaryDto dto = new Ret1AspVerticalSummaryDto();
			dto.setCess(BigDecimal.ZERO);
			dto.setCgst(BigDecimal.ZERO);
			dto.setCount(0);
			dto.setIgst(BigDecimal.ZERO);
			dto.setInvoiceValue(BigDecimal.ZERO);
			dto.setRate(BigDecimal.ZERO);
			dto.setSgst(BigDecimal.ZERO);
			dto.setTaxableValue(BigDecimal.ZERO);
			
		}
		return aspvertical;
	}
	
	// For Gstn Data
		@SuppressWarnings("null")
		public List<Ret1AspVerticalSummaryDto> findgstnDetails(
				Annexure1SummaryReqDto req) {
			// TODO Auto-generated method stub
			
			List<Ret1AspVerticalSummaryDto> gstnvertical  = new ArrayList<>();
			
			if(req.getIsGstr1a())
			{
				
				 gstnvertical = loadDataGstr1A
							.gstnBasicSummarySection(req);
					
			}
			else
			{
			 gstnvertical = loadData
					.gstnBasicSummarySection(req);
			}
			if (gstnvertical.isEmpty() && gstnvertical.size()==0) {
				Ret1AspVerticalSummaryDto defaulVertList = new Ret1AspVerticalSummaryDto();
				defaulVertList.setTaxableValue(BigDecimal.ZERO);
				defaulVertList.setIgst(BigDecimal.ZERO);
				defaulVertList.setCgst(BigDecimal.ZERO);
				defaulVertList.setSgst(BigDecimal.ZERO);
				defaulVertList.setCess(BigDecimal.ZERO);
				gstnvertical.add(defaulVertList);
			}
			return gstnvertical;
		}
		public List<Gstr1VerticalSummaryRespDto> findVerticalDetails(
				Annexure1SummaryReqDto req) {
			// TODO Auto-generated method stub
			
			List<Gstr1VerticalSummaryRespDto> verticalData  = new ArrayList<>();
			
			if(req.getIsGstr1a())
			{
				
				verticalData = loadDataGstr1A
							.verticalBasicSummarySection(req);
					
			}else
			{
			 verticalData = loadData
					.verticalBasicSummarySection(req);
			
			}
			return verticalData;
		}

}
