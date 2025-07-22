package com.ey.advisory.admin.data.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.UserActionMasterEntity;


@Repository("UserActionRepository")
public interface UserActionRepository extends 
                          JpaRepository <UserActionMasterEntity, Long> {
 
    @Query("SELECT h FROM UserActionMasterEntity h")
    List<UserActionMasterEntity> findAll();
    
   /* @Query("SELECT COUNT(HSNENTITY) FROM CategoryMasterEntity HSNENTITY WHERE "
    		+ "HSNENTITY.hsnSac LIKE :hsnOrSac%")
	int findByHsnOrSac(@Param("hsnOrSac") String hsnOrSac);
    
   public  TcsTdsCategoryCache  findByHsnSac(String hsnSac);*/
    

}
