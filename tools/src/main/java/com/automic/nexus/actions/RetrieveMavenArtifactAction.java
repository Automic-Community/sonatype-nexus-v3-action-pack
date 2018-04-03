
package com.automic.nexus.actions;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.automic.nexus.constants.ExceptionConstants;
import com.automic.nexus.exception.AutomicException;
import com.automic.nexus.util.CommonUtil;
import com.automic.nexus.util.ConsoleWriter;
import com.automic.nexus.util.validator.NexusValidator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * This action is used to retrieve an artifact from Nexus MAVEN repository.
 * 
 * @author Karanvir Attli
 *
 */

public class RetrieveMavenArtifactAction extends AbstractHttpAction {

	private static final String RAW_REPO = "repository";
	private static final String GROUP = "groupID";
	private static final String ARTIFACT_ID = "artifactid";
	private static final String BASE_VERSION = "baseversion";
	private static final String SHA1 = "sha1";
	private static final String MD5 = "md5";
	private static final String TARGET_FOLDER = "target";
	private static final String FILE_NAME = "filename";
	private static final String CLASSIFIER = "classifier";
	private static final String EXTENSION = "extension";
	
	private String repository;
	private String groupid;
	private String artifactid;
	private String baseversion;
	private String filesha1;
	private String filemd5;
	private String targetFolder;
	private String fileName;
	private String fileclassifier;
	private String fileExtension;



	public RetrieveMavenArtifactAction() {
		addOption(RAW_REPO, true, "Repository Name");
		addOption(GROUP, false, "Group ID");
		addOption(ARTIFACT_ID, false, "Artifact ID");
		addOption(BASE_VERSION, false, "Base Version");
		addOption(SHA1, false, "SHA 1");
		addOption(MD5, false, "MD5");
		addOption(TARGET_FOLDER, false, "Target Folder");
		addOption(FILE_NAME, true, "File Name");
		addOption(CLASSIFIER, false, "classifier");
		addOption(EXTENSION, false, "extension");
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
		artifactid = getOptionValue(ARTIFACT_ID);
		baseversion = getOptionValue(BASE_VERSION);
		filesha1 = getOptionValue(SHA1);
		filemd5 = getOptionValue(MD5);
		targetFolder = getOptionValue(TARGET_FOLDER);
		fileclassifier = getOptionValue(CLASSIFIER);
		fileExtension = getOptionValue(EXTENSION);
		fileName = getOptionValue(FILE_NAME);
		NexusValidator.checkNotEmpty(fileName, "File Name");

	}

	/**
	 * Execute Upload RAW artifact action.
	 * 
	 * @throws AutomicException
	 */
	@Override
	protected void executeSpecific() throws AutomicException {
		prepareInputParameters();
		WebResource webResource = getClient();
		ClientResponse response = null;

		webResource = webResource.path("service").path("rest").path("beta").path("search").path("assets")
				.path("download").queryParam("repository", repository);

		if (CommonUtil.checkNotEmpty(groupid)) {
			webResource = webResource.queryParam("maven.groupId", groupid);
		}
		if (CommonUtil.checkNotEmpty(artifactid)) {
			webResource = webResource.queryParam("maven.artifactId", artifactid);
		}
		if (CommonUtil.checkNotEmpty(baseversion)) {
			webResource = webResource.queryParam("maven.baseVersion", baseversion);
		}

		if (CommonUtil.checkNotEmpty(filemd5)) {
			webResource = webResource.queryParam("md5", filemd5);
		}

		if (CommonUtil.checkNotEmpty(filesha1)) {
			webResource = webResource.queryParam("sha1", filesha1);
		}

		if (CommonUtil.checkNotEmpty(fileExtension)) {
			webResource = webResource.queryParam("maven.extension", fileExtension);
		}
		
		if (CommonUtil.checkNotEmpty(fileclassifier)) {
			webResource = webResource.queryParam("maven.classifier", fileclassifier);
		}
		
		ConsoleWriter.writeln("Calling url " + webResource.getURI());
		response = webResource.get(ClientResponse.class);
		prepareOutput(response);
	}

	private void prepareOutput(ClientResponse response) throws AutomicException {

		Path storedLocation = Paths.get(targetFolder, fileName);
		try (InputStream is = response.getEntityInputStream()) {
			Files.copy(is, storedLocation, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new AutomicException(String.format(ExceptionConstants.UNABLE_TO_WRITEFILE, storedLocation));
		}
	}

}
