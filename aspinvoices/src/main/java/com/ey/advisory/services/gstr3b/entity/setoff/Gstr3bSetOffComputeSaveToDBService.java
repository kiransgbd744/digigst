package com.ey.advisory.services.gstr3b.entity.setoff;

import java.util.List;

public interface Gstr3bSetOffComputeSaveToDBService {

	public String saveComputedtoDb(Gstr3BSetOffComputeSaveInnerDto reqDto,
			List<Gstr3BSetOffComputeSaveClosingBalDto> closingBals);

}
