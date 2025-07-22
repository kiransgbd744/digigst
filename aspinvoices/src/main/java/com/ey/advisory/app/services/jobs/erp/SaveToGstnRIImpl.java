package com.ey.advisory.app.services.jobs.erp;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.DocRepository;

/**
 * 
 * @author Hemasundar.J
 *
 */
@Component("saveToGstnRIImpl")
public class SaveToGstnRIImpl implements SaveToGstnRI {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SaveToGstnRIImpl.class);
	
	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;
	
	@Override
	public ResponseEntity<String> getTaxDocsForRevIntegration() {
		
		List<Object[]> obj = docRepository.getTaxDocsForRevIntegration() ;
		
		//after that read the docs and update erpRefId batch id wise
		return null;
	}

}
