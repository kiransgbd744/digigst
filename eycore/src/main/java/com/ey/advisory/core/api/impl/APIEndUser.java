package com.ey.advisory.core.api.impl;

/**
 * This is a tagging interface that represents the End User of the API. For
 * example, the GSTN APIs are invoked by a  GSTIN user registered with GSTN. So,
 * any information that's required for fetching the details of this user, for
 * invoking the API is encapsulated in this class. Since different API providers
 * will have different parameters for the End User, we cannot have a standard
 * set of properties here. Individual implementations are required to provide
 * the relevant details to be included as parameters for representing the End
 * User. Usually the APIs will contain details like username, password, 
 * mobile number, email id etc.
 * 
 * @author Sai.Pakanati
 *
 */
public interface APIEndUser {}
