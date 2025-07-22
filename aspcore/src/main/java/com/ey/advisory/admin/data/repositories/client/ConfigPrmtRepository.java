package com.ey.advisory.admin.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.ConfigPrmtEntity;

/**
 * @author Umesha.M
 *
 */
@Repository("ConfigPrmtRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface ConfigPrmtRepository
        extends CrudRepository<ConfigPrmtEntity, Long> {

	@Query("SELECT c.paramtrKeyId, c.paramtrKey, c.paramtrValue, c.paramtrValuDesc "
	        + "FROM ConfigPrmtEntity c WHERE "
	        + "c.paramtrCategory=:paramtrCategory")
	public List<Object[]> getConfigPermit(
	        @Param("paramtrCategory") String paramtrCategory);

	@Query("SELECT c.paramtrKeyId, c.paramtrKey, c.paramtrValue, c.paramtrValuDesc "
	        + "FROM ConfigPrmtEntity c WHERE c.paramtrCategory IN "
	        + "(:inwardParamtrCategory,:outwardParamtrCategory)")
	public List<Object[]> getConfigPermitForInwardANDOutward(
	        @Param("inwardParamtrCategory") String inwardParamtrCategory,
	        @Param("outwardParamtrCategory") String outwardParamtrCategory);

	@Query("SELECT c.id FROM ConfigPrmtEntity c WHERE "
	        + "c.paramtrKeyId=:paramtrKeyId AND paramtrValue=:paramtrValue")
	public String getConfigParmterId(@Param("paramtrKeyId") String paramtrKeyId,
	        @Param("paramtrValue") String paramtrValue);

}
