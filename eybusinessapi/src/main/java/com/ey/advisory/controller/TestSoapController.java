package com.ey.advisory.controller;

import com.ey.advisory.app.data.entities.client.ReverseIntegrationHdrRq;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;

//@WebService(targetNamespace = "http://www.ey.com/digigst/wsapi", name = "TestSoapService")
//@HandlerChain(file = "handlertest-chain.xml")
public class TestSoapController {

	@WebMethod
	public @WebResult(name = "test-resp") String testSoapService(
			@WebParam(name = "test-req") ReverseIntegrationHdrRq req1) {
		return "Success";
	}

}
