package com.ey.advisory.app.services.daos.get2a;

import java.util.List;

import com.ey.advisory.app.docs.dto.Anx2GetProcessedRequestDto;

public interface AnxGet2adatasecurityService {

List<String> getSecurityGstins(List<Long> entityIds);

}
