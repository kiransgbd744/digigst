package com.ey.advisory.app.services.daos.initiaterecon;

import java.util.List;

import com.ey.advisory.app.docs.dto.EntityIRDto;
import com.ey.advisory.core.dto.ReconEntity;

public interface Anx2InitiateReconDao {

List<ReconEntity> anx2InitiateRecon(EntityIRDto criteria);

}
