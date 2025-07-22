package com.ey.advisory.admin.data.repositories.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ey.advisory.admin.data.entities.master.TcsTdsRemarksMasterEntity;


@Repository("TcsTdsRemarksMasterRepository")
public interface TcsTdsRemarksMasterRepository extends 
                          JpaRepository <TcsTdsRemarksMasterEntity, Long> {
 
    @Query("SELECT h FROM TcsTdsRemarksMasterEntity h")
    List<TcsTdsRemarksMasterEntity> findAll();
    

}
