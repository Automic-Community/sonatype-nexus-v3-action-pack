/**
 *
 */
package com.automic.nexus.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automic.nexus.actions.AbstractAction;
import com.automic.nexus.cli.Cli;
import com.automic.nexus.cli.CliOptions;
import com.automic.nexus.constants.Constants;
import com.automic.nexus.constants.ExceptionConstants;
import com.automic.nexus.exception.AutomicException;

/**
 * Helper class to delegate request to specific Action based on input arguments .
 * */
public final class ClientHelper {

    private static final Logger LOGGER = LogManager.getLogger(ClientHelper.class);
    private static final String ABSOLUTEPATH = "com.automic.nexus.actions";

    private ClientHelper() {
    }

    /**
     * Method to delegate parameters to an instance of {@link AbstractAction} based on the value of Action parameter.
     *
     * @param actionParameters
     *            of options with key as option name and value is option value
     * @throws AutomicException
     */

    public static void executeAction(String[] actionParameters) throws AutomicException {
        String actionName = new Cli(new CliOptions(), actionParameters).getOptionValue(Constants.ACTION);
        LOGGER.info("Execution starts for action [" + actionName + "]...");

        AbstractAction action = null;
        try {
            Class<?> classDefinition = Class.forName(getCanonicalName(actionName));
            action = (AbstractAction) classDefinition.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            String msg = String.format(ExceptionConstants.INVALID_INPUT_PARAMETER, Constants.ACTION, actionName);
            LOGGER.error(msg, e);
            throw new AutomicException(msg);
        }
        action.executeAction(actionParameters);
    }

    private static String getCanonicalName(String clsName) {
        return ABSOLUTEPATH + "." + clsName;
    }
}
