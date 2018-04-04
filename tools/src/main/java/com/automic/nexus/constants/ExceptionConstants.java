package com.automic.nexus.constants;

/**
 * @author yogitadalal
 *
 */
public final class ExceptionConstants {

    public static final String GENERIC_ERROR_MSG = "System Error occured";

    public static final String INVALID_ARGS = "Improper Args. Possible cause : %s";
    public static final String UNABLE_TO_CLOSE_STREAM = "Error while closing stream";
    public static final String UNABLE_TO_FLUSH_STREAM = "Error while flushing stream";
    public static final String INVALID_INPUT_PARAMETER_PATTERN = "Invalid value for parameter [%s] "
            + ": [%s]\n Required pattern=[%s]";
    public static final String UNABLE_TO_WRITEFILE = "Error writing to file: [%s]. Please provide valid file path.";

    public static final String INVALID_INPUT_PARAMETER = "Invalid value for parameter [%s] : [%s]";
    public static final String INVALID_BASE_URL = "Invalid Base URL [%s]";
    public static final String SYSTEM_ERROR = "System Error";

    // Certificate errors
    public static final String INVALID_KEYSTORE = "Invalid KeyStore.";
    public static final String SSLCONTEXT_ERROR = "Unable to build secured context.";
    public static final String FILENAME_ERROR = "File name not found in response.";
    public static final String ERROR_SKIPPING_CERT = "Error skipping the certificate validation";

    private ExceptionConstants() {
    }

}
