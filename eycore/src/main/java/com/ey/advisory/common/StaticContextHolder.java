package com.ey.advisory.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Service;

@Service
/**
 * This class stores a static reference to the spring 
 * Application/Web Context at the time of initialization. One of the
 * overloaded versions of the getBean method can be used to retrieve
 * a bean from the application context. 
 * 
 * This class is useful when a non-managed POJO class requires access
 * to a Spring bean. In this case, autowiring is not possible as the
 * POJO is not managed by spring. Note that this class itself is managed
 * by spring.
 * 
 * @author Hareesh.Ravindran
 *
 */
public class StaticContextHolder implements BeanFactoryAware {

	private static final Logger LOGGER = 
			LoggerFactory.getLogger(StaticContextHolder.class);
	
    public static BeanFactory CONTEXT;

    public StaticContextHolder() {
    }

    public static Object getBean(String s) throws BeansException {
        return CONTEXT.getBean(s);
    }

    public static <T> T getBean(
    		String s, Class<T> tClass) throws BeansException {
        return CONTEXT.getBean(s, tClass);
    }

    public static <T> T getBean(Class<T> tClass) throws BeansException {
        return CONTEXT.getBean(tClass);
    }

    public static Object getBean(
    		String s, Object... objects) throws BeansException {
        return CONTEXT.getBean(s, objects);
    }

    public static boolean containsBean(String s) {
        return CONTEXT.containsBean(s);
    }

    @Override
    /**
     * This method should only be called once, by the spring container, at the
     * time of initialization of the Spring context. If in any scenario, this
     * method is called more than once, then log an error (indicating that 
     * something is wrong with the Spring Initialization process).
     */
    public void setBeanFactory(
    			BeanFactory applicationContext) throws BeansException {
    	if(LOGGER.isInfoEnabled()) {
    		LOGGER.info("Setting the application context "
    				+ "in the StaticContextHolder instance.");
    	}
        // Issue error message if this method is invoked more than once during
    	// the life cycle of the application.
    	if (CONTEXT != null) {
    		LOGGER.warn("CONTEXT is not null. "
    				+ "Double Spring context creation?");
    	}
    	
        CONTEXT = applicationContext;
        
    	if(LOGGER.isInfoEnabled()) {
    		LOGGER.info("Successfully set the application context "
    				+ "in the StaticContexgtHolder instance");
    	}        
    }    
}
