
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
 * This action is used to upload an artifact to Nexus RAW repository.
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
	private String group;
	private String artifactid;
	private String baseversion;
	private String sha1;
	private String md5;
	private String targetFolder;
	private String fileName;
	private String classifier;
	private String extension;



	public RetrieveMavenArtifactAction() {
		addOption(RAW_REPO, true, "Repository");
		addOption(GROUP, false, "Group name");
		addOption(ARTIFACT_ID, false, "artifact id of the component");
		addOption(BASE_VERSION, false, "Base Version");
		addOption(SHA1, false, "SHA 1");
		addOption(MD5, false, "MD5");
		addOption(TARGET_FOLDER, false, "Target Folder");
		addOption(FILE_NAME, true, "Name of the file");
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

		group = getOptionValue(GROUP);
		artifactid = getOptionValue(ARTIFACT_ID);
		baseversion = getOptionValue(BASE_VERSION);
		sha1 = getOptionValue(SHA1);
		md5 = getOptionValue(MD5);
		targetFolder = getOptionValue(TARGET_FOLDER);
		
		classifier = getOptionValue(CLASSIFIER);
		extension = getOptionValue(EXTENSION);
		
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

		if (CommonUtil.checkNotEmpty(group)) {
			webResource = webResource.queryParam("maven.groupId", group);
		}
		if (CommonUtil.checkNotEmpty(artifactid)) {
			webResource = webResource.queryParam("maven.artifactId", artifactid);
		}
		if (CommonUtil.checkNotEmpty(baseversion)) {
			webResource = webResource.queryParam("maven.baseVersion", baseversion);
		}

		if (CommonUtil.checkNotEmpty(md5)) {
			webResource = webResource.queryParam("md5", md5);
		}

		if (CommonUtil.checkNotEmpty(sha1)) {
			webResource = webResource.queryParam("sha1", sha1);
		}

		if (CommonUtil.checkNotEmpty(extension)) {
			webResource = webResource.queryParam("maven.extension", extension);
		}
		
		if (CommonUtil.checkNotEmpty(classifier)) {
			webResource = webResource.queryParam("maven.classifier", classifier);
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
