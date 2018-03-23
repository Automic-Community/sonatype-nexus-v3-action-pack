package com.automic.nexus.util.validator;

import java.io.File;

import com.automic.nexus.constants.ExceptionConstants;
import com.automic.nexus.exception.AutomicException;
import com.automic.nexus.util.CommonUtil;

/**
 * This class provides common validations as required by action(s).
 *
 */

public final class NexusValidator {

    private NexusValidator() {
    }

    public static void checkNotEmpty(String parameter, String parameterName) throws AutomicException {
        if (!CommonUtil.checkNotEmpty(parameter)) {
            throw new AutomicException(String.format(ExceptionConstants.INVALID_INPUT_PARAMETER, parameterName,
                    parameter));
        }
    }

    public static void checkFileExists(File file, String parameterName) throws AutomicException {
        if (!(file.exists() && file.isFile())) {
            throw new AutomicException(String.format(ExceptionConstants.INVALID_INPUT_PARAMETER, parameterName, file));
        }
    }

    public static void lessThan(int value, int lessThan, String parameterName) throws AutomicException {
        if (value < lessThan) {
            String errMsg = String.format(ExceptionConstants.INVALID_INPUT_PARAMETER, parameterName, value);
            throw new AutomicException(errMsg);
        }
    }

    public static void checkDirectoryExists(File filePath, String parameterName) throws AutomicException {
        if (!(filePath.exists() && !filePath.isFile())) {
            throw new AutomicException(String.format(ExceptionConstants.INVALID_INPUT_PARAMETER, parameterName,
                    filePath));
        }
    }
    
    /**
     * This method validate Parameter Name for provided RegEx
     * 
     * @param parameterName
     *            {@code String} object
     * @param value
     *            {@code String} object
     * @param regEx
     *            {@code String} object
     * @param message
     *            {@code String} object
     * @throws AutomicException
     *             Invalid value for parameter
     */
    public static final void matchingPattern(String parameterName, String value, String regEx, String message)
            throws AutomicException {
        if (!CommonUtil.checkNotEmpty(value) || !value.matches(regEx)) {
            throw new AutomicException(String.format(ExceptionConstants.INVALID_INPUT_PARAMETER_PATTERN, parameterName,
                    value, message));
        }
    }

}
