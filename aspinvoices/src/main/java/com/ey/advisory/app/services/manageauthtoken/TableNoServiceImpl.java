package com.ey.advisory.app.services.manageauthtoken;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.TableNoDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("TableNoServiceImpl")
public class TableNoServiceImpl implements TableNoService {

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public TableNoDto getTableNumbersById(Long docId) {

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format("Inside TableNoServiceImpl "
					+ "Service Class for following ids %s ", docId);
			LOGGER.debug(msg);
		}
		String queryString = "SELECT DISTINCT ITM_TABLE_SECTION, ITM_TAX_DOC_TYPE "
				+ " FROM ANX_OUTWARD_DOC_ITEM "
				+ " WHERE DOC_HEADER_ID IN (:id) LIMIT 5 ";

		Query q = entityManager.createNativeQuery(queryString);

		q.setParameter("id", docId);

		@SuppressWarnings("unchecked")
		List<Object[]> list = q.getResultList();

		List<String> results = list.stream()
				.map(o -> convertToTableSectionDto(o).getTableNo())
				.filter(t -> t != null && !t.isEmpty())
				.collect(Collectors.toList());

		TableNoDto dto = new TableNoDto();
		dto.setTableNoList(results);
		
		return dto;

	}

	private TableNoDto convertToTableSectionDto(Object[] arr) {
		TableNoDto dto = new TableNoDto();

		String tableSection = (String) arr[0];
		String docCategory = (String) arr[1];
		String tableNumber = null;
		if ((tableSection != null && !tableSection.isEmpty())
				&& (docCategory != null && !docCategory.isEmpty())) {
			tableNumber = tableSection.concat("-").concat(docCategory);
		}
		dto.setTableNo(
				tableNumber != null ? String.valueOf(tableNumber) : null);

		return dto;
	}
}
