package com.automic.nexus.actions;

import java.io.File;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automic.nexus.exception.AutomicException;
import com.automic.nexus.util.validator.NexusValidator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;

/**
 * This action is used to upload an artifact to Nexus RAW repository.
 * 
 * @author yogitadalal
 *
 */
public class UploadRawArtifactAction extends AbstractHttpAction {

	private static final Logger LOGGER = LogManager.getLogger(UploadRawArtifactAction.class);
	private static final String RAW_REPO = "repository";
	private static final String FILE_PATH = "filepath";
	private static final String FILE_NAME = "filename";
	private static final String DIR = "directory";
	
	private String repository;
	private File filePath;
	private String fileName;
	private String directory;

	public UploadRawArtifactAction() {
		addOption(RAW_REPO, true, "Repository in which you want to upload artifact.");
		addOption(FILE_PATH, true, "File you want to upload with full path.");
		addOption(FILE_NAME, true, "Name to your file.");
		addOption(DIR, true, "Destination directory in which file will be uploaded.");
	}

	/**
	 * Validate and initialize input.
	 * 
	 * @throws AutomicException
	 */
	private void prepareInputParameters() throws AutomicException {
		try {
			String temp = getOptionValue(FILE_PATH);
			NexusValidator.checkNotEmpty(temp, "File");
			filePath = new File(temp);
			NexusValidator.checkFileExists(filePath, "File");

			fileName = getOptionValue(FILE_NAME);
			NexusValidator.checkNotEmpty(fileName, "File Name");

			directory = getOptionValue(DIR);
			NexusValidator.checkNotEmpty(directory, "Directory");


			repository = getOptionValue(RAW_REPO);
			NexusValidator.checkNotEmpty(repository, "Repository");

		} catch (AutomicException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
	}

	/**
	 * Execute Upload RAW artifact action.
	 * @throws AutomicException
	 */
	@Override
	protected void executeSpecific() throws AutomicException {
		prepareInputParameters();
		WebResource webResource = getClient();
		webResource = webResource.queryParam(RAW_REPO, repository).path("service").path("rest").path("beta").path("components");
        
		FileDataBodyPart fp = new FileDataBodyPart("raw.assetN", filePath, MediaType.APPLICATION_OCTET_STREAM_TYPE);

		FormDataMultiPart part = new FormDataMultiPart();
		part.field("raw.assetN.filename", fileName).field("raw.directory", directory).bodyPart(fp);

		LOGGER.info("Calling url " + webResource.getURI());
		webResource.type(part.getMediaType()).post(ClientResponse.class, part);
	}

	@Override
	protected Logger getLogger() {
		return LOGGER;
	}

}
