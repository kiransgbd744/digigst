package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr3bProcedureEntity;

@Repository("Gstr3bProcedureRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr3bProcedureRepository
		extends JpaRepository<Gstr3bProcedureEntity, Long>,
		JpaSpecificationExecutor<Gstr3bProcedureEntity> {

	//need query to get all the active SEQ_SUP_A and SEQ_SUP_B
	@Query("SELECT g FROM Gstr3bProcedureEntity g WHERE g.isActive = true")
	List<Gstr3bProcedureEntity> findAllActiveProcedures();

}
