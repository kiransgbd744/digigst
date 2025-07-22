package com.ey.advisory.common;

import java.awt.Image;

/**
 * @author Arun KA
 *
 */
public interface QRcodeGenerator {
	
	/**
	 * 
	 * @param qrCode
	 * @return
	 */
	public abstract Image getImage(String qrCode);
}
