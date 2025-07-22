package com.ey.advisory.app.common;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.ey.advisory.app.data.entities.client.GstinValidatorEntity;
import com.ey.advisory.app.data.repositories.client.GstinValidatorConfigRepository;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Repository("TaxPayerConfigDetailsDaoImpl")
public class TaxPayerConfigDetailsDaoImpl implements TaxPayerConfigDetailsDao {

	@Autowired
	@Qualifier(value = "GstinValidatorConfigRepository")
	GstinValidatorConfigRepository gstinValidRepo;
	
	
	@Override
	public List<GstinValidatorEntity> 
	     getTaxPayerConfigDetailsFromDb(boolean einvApplicable) {
	   if(LOGGER.isDebugEnabled()) {
		   String msg = "Getting Data From GstinValidatorConfigRepository for"
		   		+ " all taxPayer Config Details  ";
		   LOGGER.debug(msg);
	   }
		
	   List<GstinValidatorEntity> configDetails = gstinValidRepo
			   .findByEinvApplicable(einvApplicable);
	   return configDetails;
	}
	
	

}
