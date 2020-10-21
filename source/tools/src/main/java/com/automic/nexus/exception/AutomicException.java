/**
 *
 */
package com.automic.nexus.exception;

/**
 * Exception class thrown to indicate that error has occurred while processing request. It could either be <li>
 * <ul>
 * Business exception for invalid inputs to Actions
 * </ul>
 * <ul>
 * Technical exception to denote errors while communicating with openstack API
 * </ul>
 * </li>
 *
 */
public class AutomicException extends Exception {

    private static final long serialVersionUID = -3274150618150755200L;

    /**
     * Constructor that takes an error message
     *
     * @param message
     */
    public AutomicException(String message) {
        super(message);
    }

    public AutomicException(String message, Exception ex) {
        super(message, ex);
    }

}
