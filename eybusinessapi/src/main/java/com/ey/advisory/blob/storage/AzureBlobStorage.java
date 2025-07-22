package com.ey.advisory.blob.storage;

import java.util.List;

public interface AzureBlobStorage {
	
	public String write(Storage storage);
	public String update(Storage storage);
	public byte[] read(Storage storage);
	public List<String> listFiles(Storage storage);
	public void delete(Storage storage);
//	public void createContianer();
//	public void deleteContianer();

}