package com.ey.advisory.admin.data.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.EInvoiceDocTypeEntity;


/**
 * 
 * @author Siva.Nandam
 *
 *Repository for EInvoiceDocTypeRepository class
 */
@Repository("EInvoiceDocTypeRepository")
public interface EInvoiceDocTypeRepository extends 
JpaRepository<EInvoiceDocTypeEntity, Long> , 
JpaSpecificationExecutor<EInvoiceDocTypeEntity>{


	
	@Query("SELECT g FROM EInvoiceDocTypeEntity g")	
	List<EInvoiceDocTypeEntity> findAll();
	
	
}
