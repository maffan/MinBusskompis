package se.grupp4.minbusskompis.BussParse;

import android.support.annotation.NonNull;

import java.util.Random;

/**
 * Created by Marcus on 10/1/2015.
 */
public class BussSyncCodeGenerator implements CodeGenerator {
    private String syncCode;
    private BussParseSyncMessenger messenger;

    private String characters = "0123456789";

    public BussSyncCodeGenerator(int length){
        syncCode = generateSyncCode(length);
    }

    public String getCode() {
        return syncCode;
    }

    private String generateSyncCode(int length){
        String syncCode = generateRandomString(length);
        return syncCode;
    }

    @NonNull
    private String generateRandomString(int length) {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();

        while(builder.length() < length)
            builder.append(characters.charAt(random.nextInt(characters.length())));
        return builder.toString();
    }




}
