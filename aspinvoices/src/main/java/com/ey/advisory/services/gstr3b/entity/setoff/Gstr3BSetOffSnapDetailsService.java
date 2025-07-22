package com.ey.advisory.services.gstr3b.entity.setoff;

public interface Gstr3BSetOffSnapDetailsService {
	
	public String saveToDb(Gstr3BSetOffSnapSaveDto reqDto);
	
	public String saveRule86BToDb(Gstr3BSetRule86BSaveDto reqDto);

}
