package se.grupp4.minbusskompis.parsebuss;

import java.util.Random;

/**
 * This class generates a random numeric code of a specified length that can later be accessed.
 */
public class BussSyncCodeGenerator implements CodeGenerator {
    private static final String CHARACTERS = "0123456789";
    private String syncCode;

    public BussSyncCodeGenerator(int length){
        syncCode = generateSyncCode(length);
    }

    private String generateSyncCode(int length){
        Random random = new Random();
        StringBuilder builder = new StringBuilder();

        while(builder.length() < length)
            builder.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        return builder.toString();
    }

    public String getCode() {
        return syncCode;
    }
}