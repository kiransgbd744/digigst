/**
 * 
 */
package com.ey.advisory.common;

import java.util.List;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.core.dto.EInvoiceDocSearchReqDto;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface EInvoiceOutwardDocSearchPredicateBuilder {
	public abstract List<Predicate> build(EInvoiceDocSearchReqDto criteria,
			Root<OutwardTransDocument> root, CriteriaBuilder criteriaBuilder);
}
