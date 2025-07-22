package com.ey.advisory.admin.data.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.ReportCategoryMasterEntity;

@Repository("ReportCategoryRepository")
public interface ReportCategoryRepository extends 
                          JpaRepository <ReportCategoryMasterEntity, Long>, 
                          JpaSpecificationExecutor<ReportCategoryMasterEntity> {
 
@Query("select d from ReportCategoryMasterEntity d where dataType in (:dataType) AND isDelete= false")
public List<ReportCategoryMasterEntity> getBydT(@Param("dataType") List<String> dataType);
}