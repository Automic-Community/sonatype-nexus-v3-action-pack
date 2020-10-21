package com.automic.nexus.actions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.automic.nexus.exception.AutomicException;
import com.automic.nexus.util.CommonUtil;
import com.automic.nexus.util.ConsoleWriter;
import com.automic.nexus.util.validator.NexusValidator;
import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class RetrieveMavenArtifactInfoAction extends AbstractHttpAction {
	
	private static final String SPLITTER = "=::=";
    private static final String SLASH = "/";
    private static final String DOT = ".";
	private static final String COLON = ":";

	private static final String RAW_REPO = "repository";
	private static final String GROUP = "groupID";
	private static final String ARTIFACT_ID = "artifactid";
	private static final String BASE_VERSION = "baseversion";
	private static final String TARGET_FOLDER = "target";
	private static final String CLASSIFIER = "classifier";
	private static final String EXTENSION = "extension";
	private static final String DOWNLOAD_CHECKSUM = "downloadchecksum";
	private static final String CHECKSUM_TYPE = "checksumtype";
	private static final String PATH = "path";

	private String repository;
	private String groupid;
	private String artifactid;
	private String baseversion;
	private String targetFolder;
	private String fileclassifier;
	private String fileExtension;
	
	private String downloadChecksum;
	private String checksumType;
	private String path;

	public RetrieveMavenArtifactInfoAction() {
		addOption(RAW_REPO, true, "Repository Name");
		addOption(GROUP, true, "Group ID");
		addOption(ARTIFACT_ID, true, "Artifact ID");
		addOption(BASE_VERSION, true, "Base Version");
		addOption(TARGET_FOLDER, true, "Target Folder");
		addOption(CLASSIFIER, false, "classifier");
		addOption(EXTENSION, false, "extension");
		
		addOption(DOWNLOAD_CHECKSUM, true, "Download and record checksum of downloaded file");
		addOption(CHECKSUM_TYPE, false, "Select Sha1 or Md5");
		addOption(PATH, false, "path");
	}

	/**
	 * Validate and initialize input.
	 * 
	 * @throws AutomicException
	 */
	private void prepareInputParameters() throws AutomicException {
		repository = getOptionValue(RAW_REPO);
		NexusValidator.checkNotEmpty(repository, "Repository");
		groupid = getOptionValue(GROUP);
		NexusValidator.checkNotEmpty(groupid, "Group ID");
		artifactid = getOptionValue(ARTIFACT_ID);
		NexusValidator.checkNotEmpty(artifactid, "Artifact ID");
		baseversion = getOptionValue(BASE_VERSION);
		NexusValidator.checkNotEmpty(baseversion, "Base Version");
		targetFolder = getOptionValue(TARGET_FOLDER);
		NexusValidator.checkNotEmpty(targetFolder, "Target Folder");
		fileclassifier = getOptionValue(CLASSIFIER);
		fileExtension = getOptionValue(EXTENSION);
		downloadChecksum = getOptionValue(DOWNLOAD_CHECKSUM);
        NexusValidator.checkNotEmpty(downloadChecksum, "Download Checksum");
        checksumType = getOptionValue(CHECKSUM_TYPE);
        path=getOptionValue(PATH);

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

		webResource = webResource.path("service").path("rest").path(apiVersion).path("search").path("assets").queryParam("repository", repository)
                .queryParam("maven.groupId", groupid).queryParam("maven.baseVersion", baseversion)
                .queryParam("maven.artifactId", artifactid);

		if (CommonUtil.checkNotEmpty(fileExtension)) {
			webResource = webResource.queryParam("maven.extension", fileExtension);
		}

		if (CommonUtil.checkNotEmpty(fileclassifier)) {
			webResource = webResource.queryParam("maven.classifier", fileclassifier);
		}
		
		if (CommonUtil.checkNotEmpty(path)) {
			path="\""+path+"\"";
			webResource = webResource.queryParam("q", path );
		}

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
		List<MavenItem> artifactInfo = responseData.getItems();
		for (MavenItem mavenItem : artifactInfo) {
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
    
    private void buildChecksumFile(MavenItem item) throws IOException {
    	File file = new File(targetFolder + SLASH + item.getFileName() + DOT + checksumType);
    	Path path = Files.createFile(file.toPath());
    	FileUtils.writeStringToFile(path.toFile(), item.getChecksum(checksumType));
    }
    
    private static class ResponseData {
    	private List<MavenItem> items;

		public List<MavenItem> getItems() {
			return items;
		}
    }
    
    private static class MavenItem {
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
