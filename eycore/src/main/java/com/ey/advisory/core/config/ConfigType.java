package com.ey.advisory.core.config;

/**
 * There are certain configurations that are secure. Such configurations are
 * called 'Sensitive Configurations' and the others are called 
 * 'Regular Configurations'. Sensitive configurations are encrypted and stored
 * in the DB/storage, thereby restricting access to only the people with keys
 * and the actual application. Examples of sensitive configurations are 
 * 'Username/password'.
 *  
 * @author Sai.Pakanati
 *
 */
public enum ConfigType {
	Sensitive,
	Regular
}
