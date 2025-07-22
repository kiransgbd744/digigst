/**
 * 
 */
package com.ey.advisory.app.data.gstr1A.repositories.client;

import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.core.dto.EInvoiceDocSearchReqDto;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface Gstr1AEInvoiceOutwardDocSearchPredicateBuilder {
	public abstract List<Predicate> build(EInvoiceDocSearchReqDto criteria,
			Root<Gstr1AOutwardTransDocument> root, CriteriaBuilder criteriaBuilder);
}
