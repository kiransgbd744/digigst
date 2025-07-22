package com.ey.advisory.admin.services.onboarding;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.ELExtractEntity;
import com.ey.advisory.admin.data.repositories.client.ELExtractRepository;

@Component("ELExtractService")
public class ELExtractService {

	@Autowired
	@Qualifier("ELExtractRepository")
	ELExtractRepository elExtractRepository;

	public List<ELExtractEntity> saveELExtractDetail(List<ELExtractEntity> request) {

	    List<ELExtractEntity> list = new ArrayList<>();
	    for (ELExtractEntity entity : request) {
	        // If Id exists, update operation will execute, else save operation.
	        if (entity.getElId() != null && entity.getElId().toString().length() > 0) {
	            Optional<ELExtractEntity> resp = elExtractRepository.findById(entity.getElId());
	            if (resp.isPresent()) { // Use Optional's isPresent() to check for a value
	                entity.setElId(resp.get().getElId());
	            }
	        }
	        // Save the entity (either new or updated)
	        ELExtractEntity response = elExtractRepository.save(entity);
	        list.add(response);
	    }

	    return list;
	}

	public List<ELExtractEntity> getELExtractDetail() {
		List<ELExtractEntity> response = elExtractRepository.findDetails();
		return response;
	}
	
	public void deleteRecord(List<ELExtractEntity> request) {
		for (ELExtractEntity entity : request) {
			// if Id exists update operation will execute Else Save operation.
			if (entity.getElId() != null
					&& entity.getElId().toString().length() > 0) {

				elExtractRepository.deleterecord(entity.getElId());
			}
		}
	}


}
