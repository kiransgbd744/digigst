package com.ey.advisory.app.data.repositories.client.pdfreader;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.pdfreader.PDFResponseLineItemEntity;

/**
 * 
 * @author Siva.Reddy
 *
 */

@Repository("PDFResponseLineItemRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface PDFResponseLineItemRepo
		extends JpaRepository<PDFResponseLineItemEntity, Long> {

	Optional<PDFResponseLineItemEntity> findById(Long id);
	
	List<PDFResponseLineItemEntity> findAllByPdfResTblId(Long id);
}

