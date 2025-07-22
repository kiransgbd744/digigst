/**
 * 
 */
package com.ey.advisory.admin.data.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.Itc04GoodsTypeEntity;

/**
 * @author Laxmi.Salukuti
 *
 */
@Repository("Itc04GoodsTypeRepository")
public interface Itc04GoodsTypeRepository
		extends JpaRepository<Itc04GoodsTypeEntity, Long>,
		JpaSpecificationExecutor<Itc04GoodsTypeEntity> {

	@Query("SELECT g FROM Itc04GoodsTypeEntity g")
	List<Itc04GoodsTypeEntity> findAll();

}