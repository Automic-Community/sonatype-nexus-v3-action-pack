/**
 *
 */
package com.automic.nexus.actions;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.logging.log4j.Logger;

import com.automic.nexus.config.HttpClientConfig;
import com.automic.nexus.constants.Constants;
import com.automic.nexus.constants.ExceptionConstants;
import com.automic.nexus.exception.AutomicException;
import com.automic.nexus.filter.GenericResponseFilter;
import com.automic.nexus.util.CommonUtil;
import com.automic.nexus.util.validator.NexusValidator;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

/**
 * This class defines the execution of any action.It provides some initializations and validations on common inputs .The
 * child actions will implement its executeSpecific() method as per their own need.
 */
public abstract class AbstractHttpAction extends AbstractAction {

    /**
     * Service end point
     */
    protected URI baseUrl;

    /**
     * Username to connect to Nexus
     */
    private String username;

    /**
     * Password to Nexus username
     */
    private String password;

    /**
     * Connection timeout in milliseconds
     */
    private int connectionTimeOut;

    /**
     * Read timeout in milliseconds
     */
    private int readTimeOut;

    /**
     * Service end point
     */
    private Client client;

    /**
     * Check if the user is anonymous
     */
    private boolean isAnonymous;

    public AbstractHttpAction() {
        addOption(Constants.READ_TIMEOUT, true, "Read timeout");
        addOption(Constants.CONNECTION_TIMEOUT, true, "connection timeout");
        addOption(Constants.BASE_URL, true, "Base URL of Nexus");
        addOption(Constants.NEXUS_USERNAME, true, "Username for Nexus Authentication");
        addOption(Constants.NEXUS_PASSWORD, true, "Password for Nexus user");
    }

    /**
     * This method initializes the arguments and calls the execute method.
     * 
     * @throws AutomicException
     *             exception while executing an action
     */
    public final void execute() throws AutomicException {
        prepareCommonInputs();
        try {
            executeSpecific();
        } finally {
            if (client != null) {
                client.destroy();
            }
        }
    }

    private void prepareCommonInputs() throws AutomicException {
        String temp = getOptionValue(Constants.BASE_URL);
        try {
            this.connectionTimeOut = CommonUtil.parseStringValue(getOptionValue(Constants.CONNECTION_TIMEOUT),
                    Constants.MINUS_ONE);
            NexusValidator.lessThan(connectionTimeOut, Constants.ZERO, "Connect Timeout");
            this.readTimeOut = CommonUtil.parseStringValue(getOptionValue(Constants.READ_TIMEOUT), Constants.MINUS_ONE);
            NexusValidator.lessThan(readTimeOut, Constants.ZERO, "Read Timeout");
            this.baseUrl = new URI(temp);
            this.username = getOptionValue(Constants.NEXUS_USERNAME);
            this.password = getOptionValue(Constants.NEXUS_PASSWORD);
            boolean isProvided = CommonUtil.checkNotEmpty(this.username);
            if (isProvided) {
                NexusValidator.checkNotEmpty(password, "Password");
                isAnonymous = false;
            } else {
                if (CommonUtil.checkNotEmpty(this.password)) {
                    String msg = "Invalid user name " + username;
                    getLogger().error(msg);
                    throw new AutomicException(msg);
                } else {
                    isAnonymous = true;
                }
            }
        } catch (AutomicException e) {
            getLogger().error(e.getMessage());
            throw e;
        } catch (URISyntaxException e) {
            String msg = String.format(ExceptionConstants.INVALID_INPUT_PARAMETER, "URL", temp);
            getLogger().error(msg, e);
            throw new AutomicException(msg, e);
        }
    }

    /**
     * Method to execute the action.
     * 
     * @throws AutomicException
     */
    protected abstract void executeSpecific() throws AutomicException;

    /**
     * Method to get Logger instance.
     * 
     */
    protected abstract Logger getLogger();

    /**
     * Method to initialize Client instance.
     * 
     * @throws AutomicException
     * 
     */
    protected WebResource getClient() throws AutomicException {
        if (client == null) {
            client = HttpClientConfig.getClient(baseUrl.getScheme(), this.connectionTimeOut, this.readTimeOut);
            if (!isAnonymous) {
                client.addFilter(new HTTPBasicAuthFilter(username, password));
            }
            client.addFilter(new GenericResponseFilter());
        }
        return client.resource(baseUrl);
    }

}
