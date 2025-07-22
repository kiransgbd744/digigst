package com.ey.advisory.app.services.manageauthtoken;


import com.ey.advisory.app.docs.dto.TableNoDto;

public interface TableNoService {

	TableNoDto getTableNumbersById(Long docId);

}
