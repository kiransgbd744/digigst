/**
 * 
 */
package com.ey.advisory.common;

import java.security.PublicKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

/**
 * @author Khalid1.Khan
 *
 */
public class JwtParserUtility {
	private JwtParserUtility() {
	}
	
	/**
	 * The Below Method will accept jwt token and return the claim object 
	 * without verifying its signature,hence the authentication of the claim 
	 * cannot be trusted.
	 * */
	public static Claims getJwtBodyWithoutSignature(String jwt) {
		int lastIndex = jwt.lastIndexOf('.');
		String unsignedString = jwt.substring(0, lastIndex + 1);
		return Jwts.parser()
				.parseClaimsJwt(unsignedString)
				.getBody();

	}
	
	/**
	 * The Below Method will accept JWT token and  Public key
	 * and then it will verify the signature with the given public key and
	 *  then parse it to return the Claim(Body) of the JWT.
	 * */
	public static Claims getJwtBodyWithSignature(String jwtString,
			PublicKey publicKey) {
		return Jwts.parser()
				.setSigningKey(publicKey)
				.parseClaimsJws(jwtString)
				.getBody();

	}

}
