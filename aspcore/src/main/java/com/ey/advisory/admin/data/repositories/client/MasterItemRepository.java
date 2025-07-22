package com.ey.advisory.admin.data.repositories.client;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.client.MasterItemEntity;

import jakarta.transaction.Transactional;

@Repository("masterItemRepository")
public interface MasterItemRepository
		extends CrudRepository<MasterItemEntity, Long> {
	
	@Query("SELECT e FROM MasterItemEntity e WHERE e.isDelete=false ")
	public List<MasterItemEntity> getAllMasterItems();

	@Query("SELECT e FROM MasterItemEntity e WHERE e.isDelete=false "
			+ "AND entityId=:entityId ORDER BY id DESC")
	public List<MasterItemEntity> getAllMasterItem(
			@Param("entityId") final Long entityId);

	@Transactional
	@Modifying
	@Query("UPDATE MasterItemEntity SET isDelete = true WHERE "
			+ "id IN (:ids)")
	public void deleteMasterItem(@Param("ids") final List<Long> ids);

	/**
	 * @param hsnOrsac
	 * @return
	 */
	@Query("SELECT e FROM MasterItemEntity e  WHERE "
			+ "e.hsnOrSac=:hsnOrsac AND e.isDelete=false")
	public List<MasterItemEntity> selectByHsnOrSac(
			@Param("hsnOrsac") Integer hsnOrsac);

	@Query("SELECT e FROM MasterItemEntity e  WHERE "
			+ "e.hsnOrSac=:hsnOrsac AND e.rate=:rate AND e.isDelete=false")
	public List<MasterItemEntity> selectByHsnOrSacAndrate(
			@Param("hsnOrsac") Integer hsnOrsac,
			@Param("rate") BigDecimal rate);
	
	@Query("SELECT m FROM  MasterItemEntity m  WHERE "
			+ "m.rate=:rate AND m.isDelete=false")
	public List<MasterItemEntity> findByRate(@Param("rate") BigDecimal rate);

	@Transactional
	@Modifying
	@Query("UPDATE MasterItemEntity SET isDelete = true WHERE "
			+ " isDelete = false and entityId = :entityId")
	public void updateAllToDelete(@Param("entityId") final Long entityId);

}
