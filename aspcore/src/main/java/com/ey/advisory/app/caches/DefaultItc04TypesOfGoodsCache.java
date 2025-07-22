/**
 * 
 */
package com.ey.advisory.app.caches;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.master.Itc04GoodsTypeEntity;
import com.ey.advisory.admin.data.repositories.master.Itc04GoodsTypeRepository;
import com.ey.advisory.common.AppException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@Slf4j
@Component("DefaultItc04TypesOfGoodsCache")
public class DefaultItc04TypesOfGoodsCache implements Itc04TypesOfGoodsCache {

	@Autowired
	@Qualifier("Itc04GoodsTypeRepository")
	private Itc04GoodsTypeRepository itc04GoodsTypeRepository;
	/**
	 * This map should be loaded at startup of the application. AFter the
	 * startup, only read wil happen from this Map.
	 */
	private Map<String, Itc04GoodsTypeEntity> goodsTypeMap = new HashMap<String, Itc04GoodsTypeEntity>();

	@PostConstruct
	private void init() {
		// Access the StateRepository and load all
		goodsTypeMap = loadAllStates();
	}

	private Map<String, Itc04GoodsTypeEntity> loadAllStates() {

		try {
			// From the repository load all states and add to the map.
			List<Itc04GoodsTypeEntity> findAll = itc04GoodsTypeRepository
					.findAll();
			if (findAll != null && !findAll.isEmpty()) {
				for (Itc04GoodsTypeEntity docTypeobj : findAll) {
					goodsTypeMap.put(docTypeobj.getGoodsType(), docTypeobj);

				}
			}
			return goodsTypeMap;
		} catch (Exception ex) {
			String msg = "Error occurred while loading the "
					+ "list of tableNumbers. " + "Aborting the startup.";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
	}

	@Override
	public int findTypesOfGoods(String goodsType) {

		return goodsTypeMap.containsKey(goodsType) ? 1 : 0;
	}

}
