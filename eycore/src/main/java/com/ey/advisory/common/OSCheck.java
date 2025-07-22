package com.ey.advisory.common;

/**
 * 
 * Code obtained from StackOverflow:
 * 
 * helper class to check the operating system this Java VM runs in.
 *
 * please keep the notes below as a pseudo-license
 *
 * http://stackoverflow.com/questions/228477/how-do-i-programmatically-
 * 		determine-operating-system-in-java
 * compare to http://svn.terracotta.org/svn/tc/dso/tags/2.6.4/code/base/
 * 		common/src/com/tc/util/runtime/Os.java
 * http://www.docjar.com/html/api/org/apache/commons/lang/
 * 		SystemUtils.java.html
 */
import java.util.Locale;

public final class OSCheck {

  // cached result of OS detection
  protected static OSType detectedOS;

  /**
   * Detect the operating system from the os.name System property and cache
   * the result
   * 
   * @returns - The operating system detected.
   */
	public static OSType getOSType() {
		if (detectedOS == null) {
			String osName = System.getProperty("os.name", "generic")
					.toLowerCase(Locale.ENGLISH);
			if ((osName.indexOf("mac") >= 0) || 
					(osName.indexOf("darwin") >= 0)) {
				detectedOS = OSType.MacOS;
			} else if (osName.indexOf("win") >= 0) {
				detectedOS = OSType.Windows;
			} else if (osName.indexOf("nux") >= 0) {
				detectedOS = OSType.Linux;
			} else {
				detectedOS = OSType.Other;
			}
		}
		return detectedOS;
	}
	
}