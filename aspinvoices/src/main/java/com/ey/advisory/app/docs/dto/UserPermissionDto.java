package com.ey.advisory.app.docs.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class UserPermissionDto {
	
	@Expose
	protected boolean opted;
	
	@Expose
	protected List<String> permissions = new ArrayList<>();
	
	@Expose
	protected Set<String> imsRoles = new HashSet<>();
	
}
