package com.ey.advisory.common;

import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;

/**
 * This interface is responsible for providing traverser 
 * methods to be implemented
 * @author Mohana.Dasari
 *
 */
public interface TraverserFactory {
	
	public TabularDataSourceTraverser getTraverser(String fileName);

}
