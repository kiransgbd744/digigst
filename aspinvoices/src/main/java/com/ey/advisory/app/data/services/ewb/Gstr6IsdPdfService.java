package com.ey.advisory.app.data.services.ewb;

import java.io.IOException;
import java.util.List;

import com.ey.advisory.app.docs.dto.Gstr6DistributionDto;

public interface Gstr6IsdPdfService {
	
	public String getGstr6PdfPrint(List<Gstr6DistributionDto> reqDto,Long id) throws IOException;
}
