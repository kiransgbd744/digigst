/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import java.awt.Image;

/**
 * @author Sujith.Nanga
 *
 */
public interface QRCodeDynamicGenerator {
	
	/**
	 * 
	 * @param qrCode
	 * @return
	 */
	public abstract Image getImage(String qrCode);
}

