package com.ey.advisory.admin.services.onboarding;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;

/**
 * @author Umesha.M
 *
 */
@Component("GSTINDetailService")
public class GSTINDetailService {

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository gstinDetailRepository;

	/**
	 * @param request
	 * @return
	 */
	public List<GSTNDetailEntity> saveOrUpdate(List<GSTNDetailEntity> request) {

	    List<GSTNDetailEntity> list = new ArrayList<>();
	    for (GSTNDetailEntity entity : request) {
	        // If Id exists, update operation will execute, else save operation.
	        if (entity.getId() != null && entity.getId().toString().length() > 0) {
	            Optional<GSTNDetailEntity> resp = gstinDetailRepository.findById(entity.getId());
	            if (resp.isPresent()) { // Check if the value is present
	                entity.setId(resp.get().getId());
	            }
	        }
	        // Save the entity (either new or updated)
	        GSTNDetailEntity response = gstinDetailRepository.save(entity);
	        list.add(response);
	    }

	    return list;
	}

	/**
	 * GstinDetail API For Getting the Records
	 * 
	 * @return
	 */
	public List<GSTNDetailEntity> findGstinDetails() {
		return gstinDetailRepository.findDetails();
	}

	/**
	 * Deleting the Record for GSTINDetail
	 * 
	 * @param request
	 */

	public void deleteRecord(List<GSTNDetailEntity> request) {
		for (GSTNDetailEntity entity : request) {
			// if Id exists update operation will execute Else Save operation.
			if (entity.getId() != null
					&& entity.getId().toString().length() > 0) {

				gstinDetailRepository.deleterecord(entity.getId());
			}
		}
	}
}
