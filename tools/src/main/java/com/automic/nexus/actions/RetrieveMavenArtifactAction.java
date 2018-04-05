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
	private static final String TARGET_FOLDER = "target";
	private static final String CLASSIFIER = "classifier";
	private static final String EXTENSION = "extension";

	private String repository;
	private String groupid;
	private String artifactid;
	private String baseversion;
	private String targetFolder;
	private String fileclassifier;
	private String fileExtension;

	public RetrieveMavenArtifactAction() {
		addOption(RAW_REPO, true, "Repository Name");
		addOption(GROUP, true, "Group ID");
		addOption(ARTIFACT_ID, true, "Artifact ID");
		addOption(BASE_VERSION, true, "Base Version");
		addOption(TARGET_FOLDER, true, "Target Folder");
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
		NexusValidator.checkNotEmpty(groupid, "Group ID");
		artifactid = getOptionValue(ARTIFACT_ID);
		NexusValidator.checkNotEmpty(artifactid, "Artifact ID");
		baseversion = getOptionValue(BASE_VERSION);
		NexusValidator.checkNotEmpty(baseversion, "Base Version");
		targetFolder = getOptionValue(TARGET_FOLDER);
		NexusValidator.checkNotEmpty(targetFolder, "Target Folder");
		fileclassifier = getOptionValue(CLASSIFIER);
		fileExtension = getOptionValue(EXTENSION);

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
				.path("download").queryParam("repository", repository).queryParam("maven.groupId", groupid)
				.queryParam("maven.artifactId", artifactid).queryParam("maven.baseVersion", baseversion);

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

		Path storedLocation = Paths.get(targetFolder);
		try (InputStream is = response.getEntityInputStream()) {
			Files.copy(is, storedLocation, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new AutomicException(String.format(ExceptionConstants.UNABLE_TO_WRITEFILE, storedLocation));
		}
	}

}
