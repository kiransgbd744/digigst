package com.ey.advisory.app.asprecon.gstr2.initiaterecon;

import java.util.List;

import com.ey.advisory.core.dto.ReconEntity;

public interface Gstr2InitiateReconDao {
	
	List<ReconEntity> gstr2InitiateRecon(Gstr2InitiateReconDto request);

}
