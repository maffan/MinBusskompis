package se.grupp4.minbusskompis.bussparse;

import android.support.annotation.NonNull;

import java.util.Random;

/**
 * Created by Marcus on 10/1/2015.
 */
public class BussSyncCodeGenerator implements CodeGenerator {
    public static final String CHARACTERS = "0123456789";
    private String syncCode;

    public BussSyncCodeGenerator(int length){
        syncCode = generateSyncCode(length);
    }

    public String getCode() {
        return syncCode;
    }

    private String generateSyncCode(int length){
        return generateRandomString(length);
    }

    @NonNull
    private String generateRandomString(int length) {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();

        while(builder.length() < length)
            builder.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        return builder.toString();
    }




}
