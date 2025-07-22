package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.Gstr2Recon2BPRPatternEntity;

/**
 * @author Shashikant.Shukla
 *
 */

@Repository("Gstr2Recon2BPRPatternRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface Gstr2Recon2BPRPatternRepository
		extends JpaRepository<Gstr2Recon2BPRPatternEntity, Long>,
		JpaSpecificationExecutor<Gstr2Recon2BPRPatternEntity> {
	
	
/*	@Query("SELECT Gstr2Recon2BPRPatternEntity from Gstr2Recon2BPRPatternEntity WHERE "
			+ " patternCount >:patternCount and  isDelete=false")
	List<Gstr2Recon2BPRPatternEntity> findByPatternCountAndIsDelete(
			@Param("patternCount") Long patternCount);
	*/
	
	  @Query("SELECT ent from Gstr2Recon2BPRPatternEntity ent WHERE "
	            + " ent.patternCount >:patternCount and  ent.isDelete=false" )
	    List<Gstr2Recon2BPRPatternEntity> fnByPatCntAndIsDelete(
	            @Param("patternCount") Long patternCount);

}
