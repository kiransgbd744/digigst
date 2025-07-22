package com.ey.advisory.app.data.repositories.client.gstr7trans;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.gstr7trans.Gstr7TransDocItemEntity;

@Repository("Gstr7TransDocItemRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr7TransDocItemRepository
		extends JpaRepository<Gstr7TransDocItemEntity, Long> {

}