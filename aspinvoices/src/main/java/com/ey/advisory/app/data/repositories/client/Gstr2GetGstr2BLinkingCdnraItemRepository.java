package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.gstr2b.Gstr2GetGstr2BLinkingCdnraItemEntity;

/**
 * @author Hema G M
 *
 */

	@Repository("Gstr2GetGstr2BLinkingCdnraItemRepository")
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public interface Gstr2GetGstr2BLinkingCdnraItemRepository
			extends CrudRepository<Gstr2GetGstr2BLinkingCdnraItemEntity, Long>,
			JpaSpecificationExecutor<Gstr2GetGstr2BLinkingCdnraItemEntity> {
}


