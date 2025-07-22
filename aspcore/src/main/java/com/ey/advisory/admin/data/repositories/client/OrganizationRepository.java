/**
 * 
 */
package com.ey.advisory.admin.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.EntityAtValueEntity;

/**
 * @author Sasidhar Reddy
 *
 */
@Component("OrganizationRepository")
public interface OrganizationRepository extends 
JpaRepository<EntityAtValueEntity,Long> ,
JpaSpecificationExecutor<EntityAtValueEntity> {

}
