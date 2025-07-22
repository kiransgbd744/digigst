package com.ey.advisory.admin.services.onboarding;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.UserCreationEntity;
import com.ey.advisory.admin.data.repositories.client.UserCreationRepository;

/**
 * @author Umesha.M
 *
 */
@Component("UserCreationService")
public class UserCreationService {

	@Autowired
	@Qualifier("UserCreationRepository")
	UserCreationRepository userCreationRepository;
	
	
	/**
	 * @param request
	 * @return
	 */
	public List<UserCreationEntity> saveOrUpdate(List<UserCreationEntity> request) {

	    List<UserCreationEntity> list = new ArrayList<>();
	    for (UserCreationEntity entity : request) {
	        if (entity.getId() != null && entity.getId().toString().length() > 0) {
	            Optional<UserCreationEntity> resp = userCreationRepository.findById(entity.getId());
	            if (resp.isPresent()) { // Check if the value is present
	                entity.setId(resp.get().getId());
	            }
	        }
	        UserCreationEntity response = userCreationRepository.save(entity);
	        list.add(response);
	    }

	    return list;
	}

	
	/**
	 * @return
	 */
	public List<UserCreationEntity> findUserDetails()
	{
		
		return userCreationRepository.findDetails();
	}
	
	/**
	 * @param request
	 */
	public void deleteRecord(List<UserCreationEntity> request) {
		for (UserCreationEntity entity : request) {
			// if Id exists update operation will execute Else Save operation.
			if (entity.getId() != null
					&& entity.getId().toString().length() > 0) {

				userCreationRepository.deleterecord(entity.getId());
			}
		}
	}

	
}
