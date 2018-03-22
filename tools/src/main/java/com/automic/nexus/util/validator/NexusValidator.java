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

}
