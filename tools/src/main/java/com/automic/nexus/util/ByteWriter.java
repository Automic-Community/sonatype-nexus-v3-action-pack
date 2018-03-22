package com.automic.nexus.util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.automic.nexus.constants.Constants;
import com.automic.nexus.constants.ExceptionConstants;
import com.automic.nexus.exception.AutomicException;

/**
 *
 * Utility class to write bytes to a Stream
 *
 */
public class ByteWriter {

    private static final Logger LOGGER = LogManager.getLogger(ByteWriter.class);

    private BufferedOutputStream bos = null;

    public ByteWriter(OutputStream output) {
        bos = new BufferedOutputStream(output, Constants.IO_BUFFER_SIZE);
    }

    /**
     * Method to get associated output stream.
     */
    public OutputStream getStream() {
        return bos;
    }

    /**
     * Method to write bytes to Stream
     *
     * @param bytes
     * @throws AutomicException
     */
    public void write(byte[] bytes) throws AutomicException {
        write(bytes, 0, bytes.length);
    }

    /**
     * Method to write specific part of byte array to Stream
     *
     * @param bytes
     * @param offset
     * @param length
     * @throws AutomicException
     */
    public void write(byte[] bytes, int offset, int length) throws AutomicException {
        try {
            bos.write(bytes, offset, length);
        } catch (IOException e) {
            LOGGER.error(ExceptionConstants.UNABLE_TO_WRITEFILE, e);
            throw new AutomicException(ExceptionConstants.UNABLE_TO_WRITEFILE, e);
        }
    }

    /**
     * Method to write a String to stream
     *
     * @param field
     * @throws AutomicException
     */
    public void write(String field) throws AutomicException {
        write(field.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Method to write a new line character to stream
     *
     * @throws AutomicException
     */
    public void writeNewLine() throws AutomicException {
        write(System.lineSeparator());
    }

    /**
     * Close the underlying stream
     *
     * @throws AutomicException
     */
    public void close() throws AutomicException {
        try {
            if (bos != null) {
                bos.close();
            } else {
                LOGGER.error("Stream null!! Unable to close stream");
                throw new AutomicException(ExceptionConstants.UNABLE_TO_CLOSE_STREAM);
            }

        } catch (IOException e) {
            LOGGER.error(ExceptionConstants.UNABLE_TO_CLOSE_STREAM, e);
            throw new AutomicException(ExceptionConstants.UNABLE_TO_CLOSE_STREAM, e);
        }
    }

    /**
     * Method to flush to stream
     *
     * @throws AutomicException
     */
    public void flush() throws AutomicException {
        if (bos != null) {
            try {
                bos.flush();
            } catch (IOException e) {
                LOGGER.error(ExceptionConstants.UNABLE_TO_FLUSH_STREAM, e);
                throw new AutomicException(ExceptionConstants.UNABLE_TO_FLUSH_STREAM, e);
            }
        }
    }

}
