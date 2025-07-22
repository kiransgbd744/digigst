package com.ey.advisory.app.inward.einvoice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ey.advisory.common.JaxbXmlFormatter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * 
 * @author vishal.verma
 *
 */

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "IT_DATA")
@Service("VendorEInvoiceDTO")
public class VendorEInvoiceDTO implements JaxbXmlFormatter {

	@XmlElement(name = "item")
	private List<InwardEInvoiceErpPushDto> itData = new ArrayList<>();

	public void addItem(InwardEInvoiceErpPushDto item) {
		itData.add(item);
	}
}
