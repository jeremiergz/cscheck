package com.jeremierodriguez.services;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.concurrent.Task;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.time.Duration;


/**
 * This {@code Task} class hashes given String using the selected algorithm and returns an integer
 * to indicate if hashes are equal or not. Integer 0 is returned if hashes are different, 1 if
 * equal or -1 if task was interrupted before end of its process.
 *
 * @author Jeremie Rodriguez
 */
public class HashTask extends Task<Integer> {

    private static final Logger LOGGER = LogManager.getLogger(HashTask.class);
    private static final String PROVIDER = "BC";
    private MessageDigest digest = null;
    private File file = null;
    private Algs algo = null;
    private Integer hashEquals = 0;
    private boolean isInterrupted = false;
    private String hash = null;
    private ReadOnlyStringWrapper readOnlyGenHash = new ReadOnlyStringWrapper();

    /**
     * Constructor needing parameters as seen below.
     *
     * @param hash the one that is going to be compared to the generated one
     * @param file {@code File} object that is going to be hashed
     * @param algo {@code Algs} enumeration representing the desired hash algorithm
     */
    public HashTask(String hash, File file, Algs algo) {
        this.file = file;
        this.algo = algo;
        this.hash = hash;
    }

    @Override
    protected Integer call() throws Exception {

        return digestFile(file, algo);
    }

    private Integer digestFile(File file, Algs algo) {

        Long fileLength = file.length();

        try {
            digest = MessageDigest.getInstance(algo.toString(), PROVIDER);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e1) {
            LOGGER.log(Level.ERROR, e1.getMessage(), e1);
        }

        hashEquals = 0;

        LOGGER.log(Level.INFO, "File length: " + fileLength + " bytes");

        int bufferSize;

        // less than 10 Mb
        if (fileLength < 10485760) {
            bufferSize = 1024;

            // less than 500 Mb
        } else if (fileLength <= 524288000) {
            bufferSize = 102400;

            // more than 500 Mb
        } else {
            bufferSize = 2048000;
        }

        LOGGER.log(Level.INFO, "Buffer size: " + bufferSize + " bytes");

        long startTime = System.currentTimeMillis();

        try (FileChannel channel = FileChannel.open(file.toPath())) {

            int read = 0;
            long readLength = 0;

            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

            while (read != -1 && !isInterrupted) {

                read = channel.read(buffer);
                buffer.flip();

                if (read != -1) {
                    digest.update(buffer.array(), 0, read);
                    readLength += read;

                    double progress = (readLength * 100 / Double.valueOf(fileLength)) / 100;
                    updateProgress(progress, 1);
                }
                buffer.clear();
            }

            byte[] hashBytes = digest.digest();
            String generatedHash = DatatypeConverter.printHexBinary(hashBytes).toLowerCase();
            readOnlyGenHash.set(generatedHash);

            Duration duration = Duration.ofMillis(System.currentTimeMillis() - startTime);

            this.updateTitle("Bytes read: " + readLength + " / " + fileLength);

            hashEquals = compareHash(hash, generatedHash);

            LOGGER.log(Level.INFO, "Given hash: " + hash);
            LOGGER.log(Level.INFO, "Gen " + algo + ": " + generatedHash);
            LOGGER.log(Level.INFO, "Bytes read: " + readLength + " / " + fileLength);

            String message = "Operation achieved in ";

            if (isInterrupted) {
                this.updateMessage("Operation interrupted");
                hashEquals = -1;

            } else {
                long minutes = duration.toMinutes();
                long seconds = duration.minusMinutes(minutes).getSeconds();
                long millis = duration.minusMinutes(minutes).minusSeconds(seconds).toMillis();

                if (minutes > 0) {
                    message += minutes + "min" + seconds + "s";

                } else {
                    message += seconds + "." + millis + "s";
                }

                this.updateMessage(message);
                LOGGER.log(Level.INFO, message);
            }

        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e.getMessage(), e);
        }

        return hashEquals;
    }

    /**
     * Compares given hash and generated hash.
     *
     * @param hash          User provided hash
     * @param generatedHash Programatically generated hash
     * @return 1 if hashes match, 0 if don't and -1 if process was interrupted
     */
    private Integer compareHash(String hash, String generatedHash) {

        if (hash.equals(generatedHash)) {
            hashEquals = 1;
            this.set(hashEquals);

        } else {
            this.set(hashEquals);
        }

        return hashEquals;
    }

    /**
     * Used to interrupt process of this task.
     */
    public void interrupt() {
        this.isInterrupted = true;
    }

    /**
     * Returns generated hash as an observable.
     *
     * @return ReadOnlyStringWrapper object
     */
    public ReadOnlyStringWrapper getReadOnlyGenHash() {
        return readOnlyGenHash;
    }

}
