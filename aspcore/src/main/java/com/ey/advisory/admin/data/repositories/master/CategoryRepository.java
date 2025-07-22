package com.ey.advisory.admin.data.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.CategoryMasterEntity;


@Repository("CategoryRepository")
public interface CategoryRepository extends 
                          JpaRepository <CategoryMasterEntity, Long> {
 
    @Query("SELECT h FROM CategoryMasterEntity h")
    List<CategoryMasterEntity> findAll();
    
   /* @Query("SELECT COUNT(HSNENTITY) FROM CategoryMasterEntity HSNENTITY WHERE "
    		+ "HSNENTITY.hsnSac LIKE :hsnOrSac%")
	int findByHsnOrSac(@Param("hsnOrSac") String hsnOrSac);
    
   public  TcsTdsCategoryCache  findByHsnSac(String hsnSac);*/
    

}
