package com.ey.advisory.admin.data.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.EInvoiceSupplyTypeEntity;


/**
 * 
 * @author Siva.Nandam
 *
 *Repository for EInvoiceSupplyTypeRepository class
 */
@Repository("EInvoiceSupplyTypeRepository")
public interface EInvoiceSupplyTypeRepository extends 
JpaRepository<EInvoiceSupplyTypeEntity, Long> , 
JpaSpecificationExecutor<EInvoiceSupplyTypeEntity>{


	
	@Query("SELECT g FROM EInvoiceSupplyTypeEntity g")	
	List<EInvoiceSupplyTypeEntity> findAll();
	
	
}
