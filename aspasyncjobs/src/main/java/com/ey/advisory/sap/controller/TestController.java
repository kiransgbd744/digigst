/**
 * 
 */
package com.ey.advisory.sap.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mahesh.Golla
 *
 */
@RestController
public class TestController {
	
	@GetMapping(value = "/sayHelloAsync", produces = "application/json")
	public ResponseEntity<String> sayHello() {

		try {
			String str = "Hello " + TestController.staticTenantId();
			return new ResponseEntity<>(str, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public static String staticTenantId() {
		// return "ern00002";
		// return "shi00005";//QA -client2
		return "y8nvcqp4f9"; // DEV
		//return "uat8vi5uyx"; //QA -client1
	}

}
