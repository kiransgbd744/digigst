package com.ey.advisory.admin.data.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.CountryEntity;



/**
 * @author Siva.Nandam
 *
 */
@Repository("countryCodeRepository")
public interface countryCodeRepository 
         extends JpaRepository <CountryEntity, Long> {
	
	 @Query("SELECT r FROM CountryEntity r")
	List<CountryEntity> FindAll();
	
}
