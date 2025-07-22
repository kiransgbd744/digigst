package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.LinkA2PREntity;

@Repository("LinkA2PRRepository")
@Transactional(value = "clientTransactionManager", 
			propagation = Propagation.REQUIRED, readOnly = false)

public interface LinkA2PRRepository extends JpaRepository<LinkA2PREntity, Long>,
		JpaSpecificationExecutor<LinkA2PREntity> {

	public LinkA2PREntity findByA2InvoiceKeyAndPrInvoiceKeyAndReconReportConfigId(
			@Param("a2InvoiceKey") String a2InvoiceKey,
			@Param("prInvoiceKey") String prInvoiceKey,
			@Param("reconReportConfigId") Long reconReportConfigId);

	@Modifying
	@Query("UPDATE LinkA2PREntity ap SET ap.forcedMatchFalg = TRUE, "
			+ " ap.bucType = 'Force Match', ap.currentReportType = 'Force Match' "
			+ " WHERE ap.a2InvoiceKey =:a2InvoiceKey OR ap.prInvoiceKey "
			+ "=:prInvoiceKey AND ap.taxPeriod =:taxPeriod AND "
			+ "ap.bucType IN('Addition in ANX-2','Addition in PR')")
	public int updateReconReportType(@Param("a2InvoiceKey") List<String> 
	a2InvoiceKey, @Param("prInvoiceKey") List<String> prInvoiceKey, 
	@Param("taxPeriod") String taxPeriod);

}
