package com.automic.nexus.util;

import com.automic.nexus.constants.Constants;

/**
 * Common Utility class contains basic function(s) required by Nexus actions.
 *
 */
public final class CommonUtil {

    private CommonUtil() {
    }

    /**
     * Method to format error message in the format "ERROR | message"
     *
     * @param message
     * @return formatted message
     */
    public static String formatErrorMessage(final String message) {
        final StringBuilder sb = new StringBuilder();
        sb.append("ERROR").append(" | ").append(message);
        return sb.toString();
    }

    /**
     *
     * Method to parse String containing numeric integer value. If string is not valid, then it returns the default
     * value as specified.
     *
     * @param value
     * @param defaultValue
     * @return numeric value
     */
    public static int parseStringValue(final String value, int defaultValue) {
        int i = defaultValue;
        if (checkNotEmpty(value)) {
            try {
                i = Integer.parseInt(value);
            } catch (final NumberFormatException nfe) {
                i = defaultValue;
            }
        }
        return i;
    }

    /**
     * Method to check if a String is not empty
     *
     * @param field
     * @return true if String is not empty else false
     */
    public static boolean checkNotEmpty(String field) {
        return field != null && !field.isEmpty();
    }

    /**
     * Method to check if an Object is null
     *
     * @param field
     * @return true or false
     */
    public static boolean checkNotNull(Object field) {
        return field != null;
    }

    /**
    *
    * Method to read the value as defined in environment. If value is not valid integer, then it returns the default
    * value as specified.
    *
    * @param paramName
    * @param defaultValue
    * @return parameter value
    */
   public static final int getEnvParameter(final String paramName, int defaultValue) {
       String val = System.getenv(paramName);
       int i;
       if (val != null) {
           try {
               i = Integer.parseInt(val);
           } catch (final NumberFormatException nfe) {
               i = defaultValue;
           }
       } else {
           i = defaultValue;
       }
       return i;
   }

   /**
    * Method to convert YES/NO values to boolean true or false
    * 
    * @param value
    * @return true if YES, 1
    */
   public static final boolean convert2Bool(String value) {
       boolean ret = false;
       if (checkNotEmpty(value)) {
           ret = Constants.YES.equalsIgnoreCase(value) || Constants.TRUE.equalsIgnoreCase(value)
                   || Constants.ONE.equalsIgnoreCase(value);
       }
       return ret;
   }
}

