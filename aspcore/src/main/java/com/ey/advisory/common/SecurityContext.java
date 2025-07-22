package com.ey.advisory.common;

import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.User;

/**
 * 
 * @author Sai.Pakanati
 *
 */
@Component
public class SecurityContext {
	
	/**
	 * Make the class non-instantiable.
	 */
	private SecurityContext() {}
	
	private static final ThreadLocal<User> context = new ThreadLocal<>();	
	
	public static void setUser(User user) {
		context.set(user);
	}

	public static User getUser() {

		return context.get();
	}
	
	public static void clearTenant() {
		context.remove();
	}
	
}
