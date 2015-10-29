package se.grupp4.minbusskompis.parsebuss;

import android.os.AsyncTask;

import com.parse.ParseGeoPoint;
import com.parse.ParsePush;

/**
 * Created by Marcus on 10/4/2015.
 */
public class BussSyncer {
    private BussParseSyncMessenger messenger;


    public BussSyncer(BussParseSyncMessenger messenger) {
        this.messenger = messenger;
    }

    /**
     * Attempts to sync with a child device.
     * @param syncCode Code published by the child device
     * @param callback
     */
    public void syncWithSyncCode(String syncCode, SyncTaskCompleteCallback callback){
        new SyncRequestTask(syncCode, callback).execute();
    }

    private class SyncRequestTask extends AsyncTask<String,Void,Boolean> {

        private SyncTaskCompleteCallback callback;
        private String syncCode;
        private String remoteInstallationId;

        public SyncRequestTask(String syncCode,SyncTaskCompleteCallback callback) {
            this.syncCode = syncCode;
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(String[] params) {
            setupSyncMessageServices();
            messenger.sendSyncRequest(syncCode);
            boolean gotResponse = messenger.waitForSyncMessageAndReturnSuccess();
            if(gotResponse){
                remoteInstallationId = messenger.getSyncInstallationId();
            }
            tearDownMessageServices();
            return gotResponse;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            callback.onSyncTaskComplete(success, remoteInstallationId);
        }

    }
    private void setupSyncMessageServices() {
        BussSyncMessengerProvider.getInstance().setSyncMessenger(messenger);
    }

    private void tearDownMessageServices() {
        BussSyncMessengerProvider.getInstance().removeMessenger();
    }

    /**
     * Waits for a parent device so sync with this device.
     * @param generator
     * @param callback
     */
    public void waitForSyncRequest(CodeGenerator generator, SyncTaskCompleteCallback callback){
        String syncCode = generator.getCode();
        String legalSyncCode = BussParseSyncMessenger.getSyncCodeAsChannel(syncCode);
        new WaitForSyncTask(legalSyncCode, callback).execute();
    }

    private class WaitForSyncTask extends AsyncTask<String, Void, Boolean>{
        private SyncTaskCompleteCallback callback;
        private String syncCode;
        private String remoteInstallationId;

        public WaitForSyncTask(String syncCode,SyncTaskCompleteCallback callback) {
            this.syncCode = syncCode;
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            setupSyncMessageServicesAndSubscribe(syncCode);
            boolean gotRequest = messenger.waitForSyncMessageAndReturnSuccess();
            if(gotRequest) {
                remoteInstallationId = messenger.getSyncInstallationId();
                messenger.sendSyncResponse();
            }
            tearDownMessageServicesAndUnsubscribe(syncCode);
            return gotRequest;
        }

        @Override
        protected void onPostExecute(Boolean gotRequest) {
            callback.onSyncTaskComplete(gotRequest, remoteInstallationId);
        }
    }

    private void setupSyncMessageServicesAndSubscribe(String syncCode) {
        setupSyncMessageServices();
        ParsePush.subscribeInBackground(syncCode);
    }

    private void tearDownMessageServicesAndUnsubscribe(String syncCode) {
        tearDownMessageServices();
        ParsePush.unsubscribeInBackground(syncCode);
    }
}
