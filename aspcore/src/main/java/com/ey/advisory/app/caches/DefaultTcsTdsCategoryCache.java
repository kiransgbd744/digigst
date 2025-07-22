package com.ey.advisory.app.caches;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.CategoryMasterEntity;
import com.ey.advisory.admin.data.repositories.master.CategoryRepository;
import com.ey.advisory.common.AppException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Mahesh.Golla
 *
 */
@Slf4j
@Component("DefaultTcsTdsCategoryCache")
public class DefaultTcsTdsCategoryCache implements TcsTdsCategoryCache {

	@Autowired
	@Qualifier("CategoryRepository")
	private CategoryRepository categoryRepository;

	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<String, CategoryMasterEntity> category = new HashMap<>();

	@PostConstruct
	private void init() {
		// Access the StateRepository and load all
		category = loadAllNatureDocTypes();
	}

	private Map<String, CategoryMasterEntity> loadAllNatureDocTypes() {
		try {
			// From the repository load all states and add to the map.
			List<CategoryMasterEntity> findAll = categoryRepository.findAll();
			if (findAll != null && !findAll.isEmpty()) {
				for (CategoryMasterEntity stateCode : findAll) {
					String natureOfDoc = stateCode.getCategory()
							.replaceAll("\\s", "").toUpperCase();
					category.put(natureOfDoc, stateCode);
				}
			}
			return category;
		} catch (Exception ex) {
			String msg = "Error occurred while loading the list of states. "
					+ "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	@Override
	public int findCategory(String CategoryValue) {
		return category.containsKey(CategoryValue.toUpperCase()) ? 1 : 0;
	}

}