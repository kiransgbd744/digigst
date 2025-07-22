package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.client.HsnOrSacMasterEntityClient;

@Repository("hsnOrSacRepository")
public interface HsnOrSacRepository extends 
                          JpaRepository <HsnOrSacMasterEntityClient, Long> {
	
	
    @Query("SELECT COUNT(HSNENTITY) FROM HsnOrSacMasterEntity HSNENTITY WHERE "
    		+ "HSNENTITY.hsnSac LIKE :hsnOrSac%")
	int findByHsnOrSac(@Param("hsnOrSac") String hsnOrSac);
    
    @Query("SELECT COUNT(HSNENTITY) FROM HsnOrSacMasterEntity HSNENTITY WHERE "
    		+ "HSNENTITY.hsnSac = :hsnOrSac")
	int findcountByHsnOrSac(@Param("hsnOrSac") String hsnOrSac);
    
    @Query("SELECT COUNT(HSNENTITY) FROM HsnOrSacMasterEntity HSNENTITY WHERE "
    		+ "HSNENTITY.hsnSac = :hsnOrSac")

	HsnOrSacMasterEntityClient findByHsnOrSacs(@Param("hsnOrSac") String hsnOrSac);
    
    @Query("SELECT HSNENTITY FROM HsnOrSacMasterEntity HSNENTITY WHERE "
    		+ "HSNENTITY.hsnSac = :hsnOrSac")

	List<HsnOrSacMasterEntityClient> getByHsnOrSacs(@Param("hsnOrSac") 
	                                                   String hsnOrSac);
    
    @Query("SELECT h FROM HsnOrSacMasterEntity h")
    List<HsnOrSacMasterEntityClient> findAll();

}
