
package com.automic.nexus.actions;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.automic.nexus.constants.ExceptionConstants;
import com.automic.nexus.exception.AutomicException;
import com.automic.nexus.util.ConsoleWriter;
import com.automic.nexus.util.validator.NexusValidator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * This action is used to retrieve an artifact from Nexus RAW repository.
 * 
 * @author Karanvir Attli
 *
 */

public class RetrieveRawArtifactAction extends AbstractHttpAction {

	private static final String RAW_REPO = "repository";
	private static final String GROUP = "group";
	private static final String NAME = "name";
	private static final String TARGET_FOLDER = "target";

	private String repository;
	private String artifactgroup;
	private String artifactname;
	private String targetFolder;
	

	public RetrieveRawArtifactAction() {
		addOption(RAW_REPO, true, "Repository");
		addOption(GROUP, true, "Group");
		addOption(NAME, true, "Name");
		addOption(TARGET_FOLDER, true, "Target Folder");
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

	}

	/**
	 * Execute Retrieve RAW artifact action.
	 * 
	 * @throws AutomicException
	 */
	@Override
	protected void executeSpecific() throws AutomicException {
		prepareInputParameters();
		WebResource webResource = getClient();
		ClientResponse response = null;

		webResource = webResource.path("service").path("rest").path("beta").path("search").path("assets")
				.path("download").queryParam("repository", repository).queryParam("group", artifactgroup)
				.queryParam("name", artifactname);

		ConsoleWriter.writeln("Calling url " + webResource.getURI());
		response = webResource.get(ClientResponse.class);
		prepareOutput(response);
	}

	private void prepareOutput(ClientResponse response) throws AutomicException {
		int pos = targetFolder.lastIndexOf('\\');
		String fileName = targetFolder.substring(pos+1, targetFolder.length());
		String finalFolder = targetFolder.substring(0, pos);
		Path storedLocation = Paths.get(finalFolder, fileName);
		try (InputStream is = response.getEntityInputStream()) {
			Files.copy(is, storedLocation, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new AutomicException(String.format(ExceptionConstants.UNABLE_TO_WRITEFILE, storedLocation));
		}
	}

}
