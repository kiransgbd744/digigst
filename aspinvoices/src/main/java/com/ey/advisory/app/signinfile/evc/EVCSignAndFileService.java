package com.ey.advisory.app.signinfile.evc;

import org.javatuples.Pair;

import com.ey.advisory.app.data.entities.client.SignAndFileEntity;

/**
 * 
 * @author Sakshi.jain
 *
 */
public interface EVCSignAndFileService {

	public Pair<Boolean, String> getOtpGstn(SignAndFileEntity entity);


}
