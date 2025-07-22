/**
 * @author kiran s
 
 
 */
package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.glrecon.GlReconSrprStgEntity;

@Repository("GlReconSrprStgRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface GlReconSrprStgRepository extends JpaRepository<GlReconSrprStgEntity, Long>,
        JpaSpecificationExecutor<GlReconSrprStgEntity> {



}