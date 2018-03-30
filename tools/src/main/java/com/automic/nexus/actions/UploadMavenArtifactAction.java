package com.automic.nexus.actions;

import java.io.File;

import javax.ws.rs.core.MediaType;

import com.automic.nexus.exception.AutomicException;
import com.automic.nexus.util.CommonUtil;
import com.automic.nexus.util.validator.NexusValidator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;

/**
 * This action is used to upload an artifact to Nexus MAVEN repository.
 * 
 * @author yogitadalal
 *
 */
public class UploadMavenArtifactAction extends AbstractHttpAction {
	
	private static final String MAVEN_REPO = "repository";
	private static final String GROUP_ID = "groupid";
	private static final String ARTIFACT_ID = "artifactid";
	private static final String MVN_VERSION = "version";
	private static final String MVN_PACKAGING = "packaging";
	private static final String MVN_CLASSIFIER = "classifier";
	private static final String MVN_EXTENSION = "extension";
	private static final String FILE = "filepath";
	private static final String GEN_POM = "generatepom";
	private static final String GENERATE_POM = "Generate POM";
	
    private String groupID;
    private String artifactID;
    private String version;
    private String repository;
    private String packaging;
    private String classifier;
    private String extension;
    private File filePath;
    private Boolean generatePOM = false;

    public UploadMavenArtifactAction() {
        addOption(GROUP_ID, true, "Group ID");
        addOption(ARTIFACT_ID, true, "Artifact ID");
        addOption(MVN_VERSION, true, "Version");
        addOption(MAVEN_REPO, true, "Repository");
        addOption(MVN_PACKAGING, false, "Packaging");
        addOption(MVN_CLASSIFIER, false, "Classifier");
        addOption(MVN_EXTENSION, true, "Extension");
        addOption(FILE, true, "File");
        addOption(GEN_POM, true, GENERATE_POM);

    }

    /**
     * Validate and initialize input.
     * 
     * @throws AutomicException
     */
    private void prepareInputParameters() throws AutomicException {
            String temp = getOptionValue(FILE);
            NexusValidator.checkNotEmpty(temp, "Artifact File Path");
            filePath = new File(temp);
            NexusValidator.checkFileExists(filePath, "Artifact File Path");

            groupID = getOptionValue(GROUP_ID);
            NexusValidator.checkNotEmpty(groupID, "Group ID");

            artifactID = getOptionValue(ARTIFACT_ID);
            NexusValidator.checkNotEmpty(artifactID, "Artifact ID");

            version = getOptionValue(MVN_VERSION);
            NexusValidator.checkNotEmpty(version, "Version");

            repository = getOptionValue(MAVEN_REPO);
            NexusValidator.checkNotEmpty(repository, "Repository");

            extension = getOptionValue(MVN_EXTENSION);   
            NexusValidator.checkNotEmpty(extension, "Extension");
            
            classifier = getOptionValue(MVN_CLASSIFIER);
            packaging = getOptionValue(MVN_PACKAGING);
            
            String gntPom = getOptionValue(GEN_POM);
            NexusValidator.checkNotEmpty(gntPom, GENERATE_POM);
            NexusValidator.matchingPattern(GENERATE_POM, gntPom, "(?i)Yes|No",
                    "Value of  Generate POM  can only be Yes or No.");
            generatePOM = CommonUtil.convert2Bool(gntPom);
            
    }

    @Override
    protected void executeSpecific() throws AutomicException {
        prepareInputParameters();
        WebResource webResource = getClient();
        webResource = webResource.queryParam("repository", repository).path("service").path("rest").path("beta").path("components");

        FileDataBodyPart fp = new FileDataBodyPart("maven2.asset1", filePath, MediaType.APPLICATION_OCTET_STREAM_TYPE);

        FormDataMultiPart part = new FormDataMultiPart();
		part.field("maven2.groupId", groupID).field("maven2.version", version).field("maven2.artifactId", artifactID)
		.field("maven2.generate-pom", generatePOM.toString()).field("maven2.asset1.extension", extension);

		if (CommonUtil.checkNotEmpty(packaging)){
			part.field("maven2.packaging", packaging);
		}
		if (CommonUtil.checkNotEmpty(classifier)){
			part.field("maven2.asset1.classifier", classifier);
		}
		part.bodyPart(fp);
        webResource.type(part.getMediaType()).post(ClientResponse.class, part);
        
    }

}
