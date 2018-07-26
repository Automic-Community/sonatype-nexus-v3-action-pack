package com.automic.nexus.actions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.automic.nexus.exception.AutomicException;
import com.automic.nexus.util.ConsoleWriter;
import com.automic.nexus.util.validator.NexusValidator;
import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class RetrieveRawArtifactInfoAction extends AbstractHttpAction {
	
	private static final String SPLITTER = "=::=";
    private static final String SLASH = "/";
    private static final String DOT = ".";
	private static final String COLON = ":";

	private static final String RAW_REPO = "repository";
	private static final String GROUP = "group";
	private static final String NAME = "name";
	private static final String TARGET_FOLDER = "target";
	
	private static final String DOWNLOAD_CHECKSUM = "downloadchecksum";
	private static final String CHECKSUM_TYPE = "checksumtype";

	private String repository;
	private String artifactgroup;
	private String artifactname;
	private String targetFolder;
	
	private String downloadChecksum;
	private String checksumType;

	public RetrieveRawArtifactInfoAction() {
		addOption(RAW_REPO, true, "Repository Name");
		addOption(GROUP, true, "Group ID");
		addOption(NAME, true, "Name");
		addOption(TARGET_FOLDER, true, "Target Folder");
		
		addOption(DOWNLOAD_CHECKSUM, true, "Download and record checksum of downloaded file");
		addOption(CHECKSUM_TYPE, false, "Select Sha1 or Md5");
	}

	/**
	 * Validate and initialize input.
	 * 
	 * @throws AutomicException
	 */
	private void prepareInputParameters() throws AutomicException {
		repository = getOptionValue(RAW_REPO);
		NexusValidator.checkNotEmpty(repository, "Repository");
		artifactgroup = getOptionValue(GROUP);
		NexusValidator.checkNotEmpty(artifactgroup, "Group");
		artifactname = getOptionValue(NAME);
		NexusValidator.checkNotEmpty(artifactname, "Name");
		targetFolder = getOptionValue(TARGET_FOLDER);
		NexusValidator.checkNotEmpty(targetFolder, "Target Folder");
		downloadChecksum = getOptionValue(DOWNLOAD_CHECKSUM);
        NexusValidator.checkNotEmpty(downloadChecksum, "Download Checksum");
        checksumType = getOptionValue(CHECKSUM_TYPE);

	}

	/**
	 * Execute Retrieve MAVEN artifact action.
	 * 
	 * @throws AutomicException
	 */
	@Override
	protected void executeSpecific() throws AutomicException {
		prepareInputParameters();
		WebResource webResource = getClient();
		ClientResponse response = null;

		webResource = webResource.path("service").path("rest").path("beta").path("search").path("assets")
				.queryParam("repository", repository).queryParam("group", artifactgroup)
				.queryParam("name", artifactname);

		ConsoleWriter.writeln("Calling url " + webResource.getURI());
		response = webResource.get(ClientResponse.class);
		prepareOutput(response);
	}

	private void prepareOutput(ClientResponse response) throws AutomicException {
        if (response.getStatus() != 200) {
 		   throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
 		}
        String output = response.getEntity(String.class);
		ResponseData responseData = new Gson().fromJson(output, ResponseData.class);;
		List<RawItem> artifactInfo = responseData.getItems();
		for (RawItem mavenItem : artifactInfo) {
			String log = mavenItem.getFileName() + SPLITTER + mavenItem.getDownloadUrl();
			if ("YES".equalsIgnoreCase(downloadChecksum)) {
				try {
					log += SPLITTER + checksumType + COLON +  mavenItem.getChecksum(checksumType);
					buildChecksumFile(mavenItem);
				} catch (IOException e) {
					ConsoleWriter.writeln(e);
					throw new AutomicException(e.getMessage());
				}
			}
			ConsoleWriter.writeln(log);
		}
    }
    
    private void buildChecksumFile(RawItem item) throws IOException {
    	File file = new File(targetFolder + SLASH + item.getFileName() + DOT + checksumType);
    	Path path = Files.createFile(file.toPath());
    	FileUtils.writeStringToFile(path.toFile(), item.getChecksum(checksumType));
    }
    
    private static class ResponseData {
    	private List<RawItem> items;

		public List<RawItem> getItems() {
			return items;
		}
    }
    
    private static class RawItem {
    	private String downloadUrl;
    	private Checksum checksum;
		
		public String getDownloadUrl() {
			return downloadUrl;
		}
		
		public String getChecksum(String checksumType) {
			String checksumValue = checksum.getSha1();
			if (!Checksum.DEFAULT_CHECKSUM_TYPE.equalsIgnoreCase(checksumType)) {
				checksumValue = checksum.getMd5();
			}
			return checksumValue;
		}

		public String getFileName() {
			String[] paths = downloadUrl.split(SLASH);
			return paths[paths.length - 1];
		}
    }
    
    private static class Checksum {
    	public static final String DEFAULT_CHECKSUM_TYPE = "sha1";
    	
    	private String sha1;
    	private String md5;
    	
		public String getSha1() {
			return sha1;
		}
		
		public String getMd5() {
			return md5;
		}
    }

}
