package com.ey.advisory.core.async.domain.master;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name="EY_SENSITIVE_CONFIG")
public class EYSensitiveConfig implements Serializable {
	
	/**
	 * All the Sensitive configuration is stored in this table
	 * @author Hariram K
	 *
	 */
		private static final long serialVersionUID = 1L;

		@Id
		@Column(name = "ID") 
		@GeneratedValue(strategy=GenerationType.IDENTITY)
		protected Long id;
		
		@Column(name = "CONF_CATEG") 
		protected String category;
		
		@Column(name = "GROUP_CODE") 
		protected String groupCode;
		
		@Column(name = "CONF_KEY") 
		protected String key;
		
		@Column(name = "CONF_VALUE") 
		protected String value;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getGroupCode() {
			return groupCode;
		}

		public void setGroupCode(String groupCode) {
			this.groupCode = groupCode;
		}

	}

	

