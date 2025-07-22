package com.ey.advisory.admin.data.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.CurrencyCodeEntity;



/**
 * @author Siva.Nandam
 *
 */
@Repository("CurrencyCodeRepository")
public interface CurrencyCodeRepository 
         extends JpaRepository <CurrencyCodeEntity, Long> {
	
	 @Query("SELECT r FROM CurrencyCodeEntity r")
	List<CurrencyCodeEntity> FindAll();
	
}
