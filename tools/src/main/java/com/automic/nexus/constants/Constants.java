package com.automic.nexus.constants;

/**
 * @author sumitsamson
 *
 */

/**
 * Constants class contains all the constants.
 *
 */
public final class Constants {

  
    
    public static final String ENV_CONNECTION_TIMEOUT = "ENV_CONNECTION_TIMEOUT";
    public static final String ENV_READ_TIMEOUT = "ENV_READ_TIMEOUT";
    public static final String ENV_PASSWORD = "UC4_DECRYPTED_PWD";

    // Cli Constants
    public static final String ACTION = "action";
    public static final String HELP = "help";
    public static final int CONNECTION_TIMEOUT = 30000;
    public static final int READ_TIMEOUT = 60000;
    public static final int MAX_ITEMS_DEFAULT = 1000;
    public static final int IO_BUFFER_SIZE = 4 * 1024;

    public static final String BASE_URL = "baseurl";
    public static final String NEXUS_USERNAME = "username";
    public static final String SKIP_CERT_VALIDATION = "ssl";
    public static final String HTTPS = "https";

    public static final String YES = "YES";
    public static final String NO = "NO";
    public static final String TRUE = "TRUE";
    public static final String ONE = "1";
    
    private Constants() {
    }

}
