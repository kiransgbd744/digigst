package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.service.ims.ImsInvoiceHeaderECOMEntity;

/**
 * 
 * @author Sakshi.jain
 *
 */
@Repository("ImsInvoiceHeaderECOMepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ImsInvoiceHeaderECOMRepository
		extends JpaRepository<ImsInvoiceHeaderECOMEntity, Long>,
		JpaSpecificationExecutor<ImsInvoiceHeaderECOMEntity> {

	@Query("SELECT id FROM ImsInvoiceHeaderECOMEntity  WHERE id IN (:id) and isDelete=false")
	List<Long> findActiveIds(@Param("id")List<Long> id);
	

	@Query("SELECT e.id, e.recipientGstin, e.supplierGstin, "
			+ "e.supplierLegalName, e.supplierTradeName, e.invoiceNumber, "
			+ "e.invoiceType, e.invoiceDate, e.action, "
			+ "e.isPendingActionBlocked, e.formType, "
			+ "e.returnPeriod, e.derivedRetPeriod, e.filingStatus, "
			+ "e.invoiceValue, e.taxableValue, e.igstAmt, e.cgstAmt, "
			+ "e.sgstAmt, e.cessAmt, e.pos, e.chksum, e.docKey, "
			+ "e.createdOn, 'ECOM' as tableType, '' as orgInvNum, "
			+ "'' as orgInvDate, e.gstnInvType, e.lnkingDocKey  FROM ImsInvoiceHeaderECOMEntity "
			+ "e WHERE e.id IN :ids")
	List<Object[]> findAllById(@Param("ids") List<Long> ids);
}
