/**
 *
 */
package com.automic.nexus.client;

import com.automic.nexus.exception.AutomicException;
import com.automic.nexus.exception.AutomicRuntimeException;
import com.automic.nexus.util.CommonUtil;
import com.automic.nexus.util.ConsoleWriter;

/**
 * Main Class is the insertion point of nexus interaction api when called from AE implementation. It delegates the
 * parameters to appropriate action and returns a response code based on output of action.
 *
 * Following response code are returned by java program 0 - Successful response from nexus API 1 - An exception
 * occurred/Error in response from nexus API 2 - Connection timeout while calling nexus API
 *
 * @author yogitadalal
 */
public final class Client {

    private static final int RESPONSE_NOT_OK = 1;
    private static final int RESPONSE_OK = 0;
    private static final String ERRORMSG = "Please check the input parameters.";

    /**
     * Main method which will start the execution of an action on z/OS. This method will call the ClientHelper class
     * which will trigger the execution of specific action and then if action fails this main method will handle the
     * failed scenario and print the error message and system will exit with the respective response code.
     * 
     * @param args
     */
    public static void main(String[] args) {
        int responseCode = RESPONSE_NOT_OK;
        try {
            ClientHelper.executeAction(args);
            responseCode = RESPONSE_OK;
        } catch (AutomicException | AutomicRuntimeException ex) {
            ConsoleWriter.writeln(CommonUtil.formatErrorMessage(ex.getMessage()));
            ConsoleWriter.writeln(CommonUtil.formatErrorMessage(ERRORMSG));
        } catch (Exception e) {
            ConsoleWriter.writeln(e);
            ConsoleWriter.writeln(CommonUtil.formatErrorMessage(ERRORMSG));
        }
        ConsoleWriter.writeln("****** Execution ends with response code : " + responseCode);
        ConsoleWriter.flush();
        System.exit(responseCode);
    }

}
