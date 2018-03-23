package com.automic.nexus.actions;

import java.net.URI;
import java.net.URISyntaxException;

import com.automic.nexus.config.HttpClientConfig;
import com.automic.nexus.constants.Constants;
import com.automic.nexus.constants.ExceptionConstants;
import com.automic.nexus.exception.AutomicException;
import com.automic.nexus.filter.GenericResponseFilter;
import com.automic.nexus.util.CommonUtil;
import com.automic.nexus.util.ConsoleWriter;
import com.automic.nexus.util.validator.NexusValidator;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

/**
 * This class defines the execution of any action.It provides some
 * initializations and validations on common inputs .The child actions will
 * implement its executeSpecific() method as per their own need.
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
	 * Option to skip validation
	 */
	private boolean skipCertValidation;

	/**
	 * Service end point
	 */
	private Client client;

	/**
	 * Check if the user is anonymous
	 */
	private boolean isAnonymous;

	public AbstractHttpAction() {
		addOption(Constants.BASE_URL, true, "Base URL of Nexus");
		addOption(Constants.NEXUS_USERNAME, false, "Username for Nexus Authentication");
		addOption(Constants.SKIP_CERT_VALIDATION, false, "Skip SSL validation");
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
		NexusValidator.checkNotEmpty(temp, "Nexus URL");

		this.username = getOptionValue(Constants.NEXUS_USERNAME);
		this.password = System.getenv(Constants.ENV_PASSWORD);
		boolean isProvided = CommonUtil.checkNotEmpty(this.username);
		if (isProvided) {
			NexusValidator.checkNotEmpty(password, "Password");
			isAnonymous = false;
		} else {
			if (CommonUtil.checkNotEmpty(this.password)) {
				throw new AutomicException("The Password parameter cannot be specified without specifying the Username.");
			} else {
				isAnonymous = true;
			}
		}
		this.skipCertValidation = CommonUtil.convert2Bool(getOptionValue(Constants.SKIP_CERT_VALIDATION));
		try {
			this.baseUrl = new URI(temp);
		} catch (URISyntaxException e) {
			ConsoleWriter.writeln(e);
			String msg = String.format(ExceptionConstants.INVALID_INPUT_PARAMETER, "URL", temp);
			throw new AutomicException(msg);
		}
	}

	/**
	 * Method to execute the action.
	 * 
	 * @throws AutomicException
	 */
	protected abstract void executeSpecific() throws AutomicException;

	/**
	 * Method to initialize Client instance.
	 * 
	 * @throws AutomicException
	 * 
	 */
	protected WebResource getClient() throws AutomicException {
		if (client == null) {
			client = HttpClientConfig.getClient(baseUrl.getScheme(), this.skipCertValidation);
			if (!isAnonymous) {
                client.addFilter(new HTTPBasicAuthFilter(username, password));
            }

			client.addFilter(new GenericResponseFilter());
		}
		return client.resource(baseUrl);
	}

}
