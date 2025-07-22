package com.ey.advisory.core.async.repositories.master;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.core.async.domain.master.DocRepoServiceEntity;

@Repository("DocRepoServRepository")
@Transactional
public interface DocRepoServRepository extends JpaRepository<DocRepoServiceEntity, Long> {
	
	@Modifying
	@Query("UPDATE DocRepoServiceEntity log SET log.idToken = :idToken, log.expiryTime = :expiryTime "
			+ "WHERE  log.serviceName=:serviceName ")
	void updateIdToken(@Param("idToken") String idToken,
			@Param("serviceName") String serviceName,@Param("expiryTime") LocalDateTime expiryTime);
	
	Optional<DocRepoServiceEntity> findByServiceNameAndIsActiveTrue(String groupCode);


}
