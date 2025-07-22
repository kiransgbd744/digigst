package com.ey.advisory.app.services.search.simplified.docsummary;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.daos.client.simplified.ITC04PopupSectionsDaoImpl;
import com.ey.advisory.app.docs.dto.simplified.ITC04PopupRespDto;
import com.ey.advisory.core.dto.ITC04RequestDto;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Service("Itc04PopupSummaryService")
public class Itc04PopupSummaryService {

	@Autowired
	@Qualifier("ITC04PopupSectionsDaoImpl")
	private ITC04PopupSectionsDaoImpl itc04PopupDao;
	
	public List<ITC04PopupRespDto> itc04PopupRecords(ITC04RequestDto req){
		
		List<ITC04PopupRespDto> loadBasicSummarySection = itc04PopupDao.loadBasicSummarySection(req);
		
		return loadBasicSummarySection;
		
		
		
	}
	
	
	
}
