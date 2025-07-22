package com.ey.advisory.admin.data.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.HsnOrSacMasterEntity;


@Repository("HsnOrSacRepositoryMaster")
public interface HsnOrSacRepository extends 
                          JpaRepository <HsnOrSacMasterEntity, Long> {
 
    @Query("SELECT h FROM HsnOrSacMasterEntity h")
    List<HsnOrSacMasterEntity> findAll();
    
    @Query("SELECT distinct hsnSac FROM HsnOrSacMasterEntity")
    public List<String> findAllHsnCodes();
    
    @Query("SELECT COUNT(HSNENTITY) FROM HsnOrSacMasterEntity HSNENTITY WHERE "
    		+ "HSNENTITY.hsnSac LIKE :hsnOrSac%")
	int findByHsnOrSac(@Param("hsnOrSac") String hsnOrSac);
    
    @Query("SELECT HSNENTITY.description FROM HsnOrSacMasterEntity HSNENTITY WHERE "
    		+ "HSNENTITY.hsnSac LIKE :hsnOrSac% ORDER BY HSNENTITY.hsnSac ")
	List<String> findByHsnOrSacDesc(@Param("hsnOrSac") String hsnOrSac);
    
    
   public  HsnOrSacMasterEntity  findByHsnSac(String hsnSac);
    
   
   @Query("SELECT h.hsnSac,h.description FROM HsnOrSacMasterEntity h")
   List<Object[]> findHsnDesc();


}
