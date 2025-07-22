package com.ey.advisory.admin.data.repositories.client;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.client.MasterProductEntity;

import jakarta.transaction.Transactional;

@Repository("masterProductRepository")
public interface MasterProductRepository
		extends CrudRepository<MasterProductEntity, Long> {

	@Query("SELECT e FROM MasterProductEntity e WHERE e.isDelete=false ")
	public List<MasterProductEntity> getAllMasterProducts();
	
	@Query("SELECT e FROM MasterProductEntity e WHERE e.isDelete=false "
			+ "AND entityId=:entityId ORDER BY id DESC")
	public List<MasterProductEntity> getAllMasterProductDetails(
			@Param("entityId") final Long entityId);

	@Transactional
	@Modifying
	@Query("UPDATE MasterProductEntity SET isDelete = true WHERE "
			+ "id IN (:ids)")
	public void deleteMasterProduct(@Param("ids") final List<Long> ids);

	@Query("SELECT m FROM  MasterProductEntity m  WHERE "
			+ "m.rate=:rate and m.isDelete=false")
	public List<MasterProductEntity> findByRate(@Param("rate") BigDecimal rate);

	/**
	 * @param hsnOrsac
	 * @return
	 */
	@Query("SELECT e FROM MasterProductEntity e  WHERE "
			+ "e.hsnOrSac=:hsnOrsac AND e.isDelete=false")
	public List<MasterProductEntity> selectByHsnOrSac(
			@Param("hsnOrsac") Integer hsnOrsac);

	@Transactional
	@Modifying
	@Query("UPDATE MasterProductEntity SET isDelete = true WHERE "
			+ " isDelete = false and entityId = :entityId")
	public void updateAllToDelete(@Param("entityId") final Long entityId);
	
	@Query("SELECT e FROM MasterProductEntity e  WHERE "
			+ "e.hsnOrSac=:hsnOrsac AND e.rate=:rate AND e.isDelete=false")
	public List<MasterProductEntity> selectByHsnOrSacAndrate(
			@Param("hsnOrsac") Integer hsnOrsac,
			@Param("rate") BigDecimal rate);

}
