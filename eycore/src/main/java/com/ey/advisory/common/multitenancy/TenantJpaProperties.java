package com.ey.advisory.common.multitenancy;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.vendor.Database;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "tenant.spring.jpa")
public class TenantJpaProperties {

    private Map<String, String> properties = new HashMap<>();
    private String databasePlatform;
    private Database database;
    private boolean generateDdl = false;
    private boolean showSql = false;
    private Boolean openInView;
    
	public Map<String, String> getProperties() {
		return properties;
	}
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	public String getDatabasePlatform() {
		return databasePlatform;
	}
	public void setDatabasePlatform(String databasePlatform) {
		this.databasePlatform = databasePlatform;
	}
	public Database getDatabase() {
		return database;
	}
	public void setDatabase(Database database) {
		this.database = database;
	}
	public boolean isGenerateDdl() {
		return generateDdl;
	}
	public void setGenerateDdl(boolean generateDdl) {
		this.generateDdl = generateDdl;
	}
	public boolean isShowSql() {
		return showSql;
	}
	public void setShowSql(boolean showSql) {
		this.showSql = showSql;
	}
	public Boolean getOpenInView() {
		return openInView;
	}
	public void setOpenInView(Boolean openInView) {
		this.openInView = openInView;
	}    
}
