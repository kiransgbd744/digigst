package com.ey.advisory.blob.storage;

import java.io.InputStream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Storage {
	
	private String path;
	private String fileName	;
	private InputStream inputStream;

}