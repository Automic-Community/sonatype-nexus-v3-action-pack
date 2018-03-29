
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

public class RetrieveRawArtifactAction extends AbstractHttpAction {

	private static final String RAW_REPO = "repository";
	private static final String GROUP = "group";
	private static final String NAME = "name";
	private static final String VERSION = "version";
	private static final String SHA1 = "sha1";
	private static final String MD5 = "md5";
	private static final String TARGET_FOLDER = "target";
	private static final String FILE_NAME = "filename";

	private String repository;
	private String group;
	private String name;
	private String version;
	private String sha1;
	private String md5;
	private String targetFolder;
	private String fileName;



	public RetrieveRawArtifactAction() {
		addOption(RAW_REPO, true, "Repository");
		addOption(GROUP, false, "Group name");
		addOption(NAME, false, "Name of the component");
		addOption(VERSION, false, "Version");
		addOption(SHA1, false, "SHA 1");
		addOption(MD5, false, "MD5");
		addOption(TARGET_FOLDER, false, "Target Folder");
		addOption(FILE_NAME, true, "Name of the file");
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
		name = getOptionValue(NAME);
		version = getOptionValue(VERSION);
		sha1 = getOptionValue(SHA1);
		md5 = getOptionValue(MD5);
		targetFolder = getOptionValue(TARGET_FOLDER);
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
			webResource = webResource.queryParam("group", group);
		}
		if (CommonUtil.checkNotEmpty(name)) {
			webResource = webResource.queryParam("name", name);
		}
		if (CommonUtil.checkNotEmpty(version)) {
			webResource = webResource.queryParam("version", version);
		}

		if (CommonUtil.checkNotEmpty(md5)) {
			webResource = webResource.queryParam("md5", md5);
		}

		if (CommonUtil.checkNotEmpty(sha1)) {
			webResource = webResource.queryParam("sha1", sha1);
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
