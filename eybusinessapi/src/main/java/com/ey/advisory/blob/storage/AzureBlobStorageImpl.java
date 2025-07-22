package com.ey.advisory.blob.storage;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobItem;
import com.google.common.base.Strings;

public class AzureBlobStorageImpl implements AzureBlobStorage {

	BlobContainerClient containerClient = null;

	@Override
	public String write(Storage storage) {
		String path = getPath(storage);
		System.out.println("Path : "+path);
		File file = new File(path);
		containerClient = BlobStoreUtility.getBlobContainerClient();
		BlobClient client = containerClient.getBlobClient(storage.getFileName());
		
		System.out.println("Client : "+client);
		
		
		
		 
		try {
			InputStream inputStream = new FileInputStream(path);
			BufferedInputStream bufferedStream = new BufferedInputStream(inputStream);
			
//			if (!inputStream.markSupported()) {
//			    inputStream = new BufferedInputStream(inputStream);
//			}
			System.out.println("Going to upload Blob.");
			client.upload(bufferedStream,file.length());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Blob is uploaded successfully.");
		
		return path;
	}

	@Override
	public String update(Storage storage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] read(Storage storage) {
		String path = getPath(storage);
		containerClient = BlobStoreUtility.getBlobContainerClient();
		BlobClient client = containerClient.getBlobClient(path);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		client.download(outputStream);
		
		byte[] byteArray = outputStream.toByteArray();
		
		return byteArray;
	}
	
	public String downloadBlob(Storage storage) throws Exception {
		try {
			System.out.println("Going to download Blob.");
			String path=getPath(storage);
			containerClient = BlobStoreUtility.getBlobContainerClient();
			BlobClient client = containerClient.getBlobClient(storage.getFileName());
			
			client.downloadToFile(path); // file.getAbsolutePath()
			// InputStream inputStream = new FileInputStream(file);
			return path;

		} catch (Exception e) {
			// LOGGER.error("Exception while downloading the file from folder ",
			// e);
			throw e;
		}
	}

	@Override
	public List<String> listFiles(Storage storage) {
		
		List<String> blobNamesList = new ArrayList<String>();
		try {
			containerClient = BlobStoreUtility.getBlobContainerClient();
			
			System.out.println("step1");
			PagedIterable<BlobItem> blobList = containerClient.listBlobs();
			System.out.println("blobList :"+blobList);
			System.out.println("step2");
//			PagedIterable<BlobItem> blobList = containerClient.listBlobsByHierarchy(storage.getPath()+"/");
			
			for (BlobItem blob : blobList) {
				blobNamesList.add(blob.getName());
			}
			System.out.println("step3");
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		
		
		return blobNamesList;
	}

	@Override
	public void delete(Storage storage) {
		String path = getPath(storage);
		containerClient = BlobStoreUtility.getBlobContainerClient();
		BlobClient client = containerClient.getBlobClient(path);
		client.delete();
		System.out.println("Blob is deleted successfully.");

	}

	
	private String getPath(Storage storage) {
		if (!Strings.isNullOrEmpty(storage.getPath())
				&& !Strings.isNullOrEmpty(storage.getFileName())) {
//			return storage.getPath() + "/" + storage.getFileName();
			return storage.getPath() + storage.getFileName();
		}
		return null;
	}

}