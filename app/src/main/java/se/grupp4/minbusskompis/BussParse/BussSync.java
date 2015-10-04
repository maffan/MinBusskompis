package se.grupp4.minbusskompis.BussParse;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.Random;

/**
 * Created by Marcus on 10/1/2015.
 */
public class BussSync {
    private String syncCode;
    private BussMessenger messenger;
    private BussData bussData;

    private String characters = "0123456789";

    public BussSync(BussMessenger messenger, BussData bussData){
        this.messenger = messenger;
        this.bussData = bussData;
    }

    public String getSyncCode() {
        if(syncCode != null)
            return syncCode;
        else
            return "";
    }

    public String generateAndGetSyncCode(){
        syncCode = generateRandomString();
        return syncCode;
    }

    @NonNull
    private String generateRandomString() {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();

        while(builder.length() < 4)
            builder.append(characters.charAt(random.nextInt(characters.length())));
        return builder.toString();
    }

    public void syncWithIdAsync(String id, SyncTaskCompleteCallback callback ){
        SyncRequestTask task = new SyncRequestTask(callback);
        task.execute(id);
    }

    public void waitForIdSyncAsync(String id, SyncTaskCompleteCallback callback){

    }

    private class SyncRequestTask extends AsyncTask<String,Void,Boolean>{
        private SyncTaskCompleteCallback callback;

        public SyncRequestTask(SyncTaskCompleteCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(String[] params) {
            String syncId = params[0];
            messenger.sendSyncRequest(syncId);
            if(messenger.waitForSyncResponse(syncId))
                return true;
            else
                return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            callback.onSyncTaskComplete(success);
        }
    }

    private class WaitForSyncTask extends AsyncTask<String, Void, Boolean>{
        private SyncTaskCompleteCallback callback;

        public WaitForSyncTask(SyncTaskCompleteCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String syncId = params[0];
            if(messenger.waitForSyncResponse(syncId)){
                messenger.sendSyncResponse(syncId);
                return true;
            }
            else
                return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            callback.onSyncTaskComplete(aBoolean);
        }
    }
}
