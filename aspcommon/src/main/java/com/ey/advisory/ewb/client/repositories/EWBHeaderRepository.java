package com.ey.advisory.ewb.client.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.ewb.client.domain.EWBHeader;
@Repository("EWBHeaderRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface EWBHeaderRepository extends JpaRepository<EWBHeader, Long>,
		JpaSpecificationExecutor<EWBHeader> {

	/*@Query("SELECT * FROM EWBHeader ewb WHERE "
			+ "ewb.genGstin= :gstin AND ewb.ewbBillDate = :localDate AND "
			+ "ewb.isDelete = false")
	public List<EWBHeader> findAllByGstinAndDate(
			@Param("gstin") String string,
			@Param("localDate") LocalDateTime localdate);
			//@Param("batchSize") Integer batchSize);
*/
	public List<EWBHeader> findAllByGenGstinAndEwbBillDateAndIsDelete(
			String gstin, LocalDateTime atStartOfDay, boolean b);
	
	@Query("select e FROM EWBHeader e WHERE "
			+ "e.ewbNo in(:ewbList)")
	public List<EWBHeader> findAllByEwbNos(
			@Param("ewbList") List<String> ewbList);

}
