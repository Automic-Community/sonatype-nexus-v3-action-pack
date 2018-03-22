/**
 *
 */
package com.automic.nexus.constants;

/**
 * @author sumitsamson
 *
 */
public final class ExceptionConstants {

    public static final String GENERIC_ERROR_MSG = "System Error occured";

    public static final String INVALID_ARGS = "Improper Args. Possible cause : %s";
    public static final String UNABLE_TO_CLOSE_STREAM = "Error while closing stream";
    public static final String UNABLE_TO_FLUSH_STREAM = "Error while flushing stream";

    public static final String UNABLE_TO_WRITEFILE = "Error writing file ";

    public static final String INVALID_INPUT_PARAMETER = "Invalid value for parameter [%s] : [%s]";
    public static final String INVALID_BASE_URL = "Invalid Base URL [%s]";
    public static final String SYSTEM_ERROR = "System Error";

    // Certificate errors
    public static final String INVALID_KEYSTORE = "Invalid KeyStore.";
    public static final String SSLCONTEXT_ERROR = "Unable to build secured context.";
    public static final String FILENAME_ERROR = "File name not found in response.";

    private ExceptionConstants() {
    }

}
