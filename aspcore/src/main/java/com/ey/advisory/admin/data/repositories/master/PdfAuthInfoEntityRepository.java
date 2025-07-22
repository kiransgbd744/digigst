package com.ey.advisory.admin.data.repositories.master;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.master.PdfAuthInfoEntity;

@Repository("PdfAuthInfoEntityRepository")
@Transactional(value = "masterTransactionManager", propagation = Propagation.REQUIRED)

	public interface PdfAuthInfoEntityRepository extends 
	JpaRepository<PdfAuthInfoEntity, Long> , 
	JpaSpecificationExecutor<PdfAuthInfoEntity>{
			
		@Modifying
		@Query("UPDATE PdfAuthInfoEntity log SET log.idToken = :idToken, log.pdfTokenExpTime =:expiryTime, log.pdfTokenGenTime =:pdfTokenGenTime "
				+ "WHERE id =:id ")
		void updateIdToken(@Param("idToken") String idToken,
				@Param("id") Long id, @Param("expiryTime") LocalDateTime expiryTime,@Param("pdfTokenGenTime") LocalDateTime pdfTokenGenTime);
		
		@Query("SELECT g FROM PdfAuthInfoEntity g")
		public List<PdfAuthInfoEntity> findAll();
	}


