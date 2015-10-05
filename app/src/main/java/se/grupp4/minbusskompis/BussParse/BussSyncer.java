package se.grupp4.minbusskompis.BussParse;

import android.os.AsyncTask;

import com.parse.ParsePush;

/**
 * Created by Marcus on 10/4/2015.
 */
public class BussSyncer {
    private BussParseSyncMessenger messenger;


    public BussSyncer(BussParseSyncMessenger messenger) {
        this.messenger = messenger;
    }

    public void syncWithSyncCode(String syncCode, SyncTaskCompleteCallback callback){
        startSyncTask(syncCode, callback);
    }

    private void startSyncTask(String syncCode, SyncTaskCompleteCallback callback) {
        SyncRequestTask task = new SyncRequestTask(syncCode,callback);
        task.execute();
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
            boolean gotResponse = messenger.waitForSyncMessage();
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

    public void waitForSync(CodeGenerator generator, SyncTaskCompleteCallback callback){
        String syncCode = generator.getCode();
        WaitForSyncTask task = new WaitForSyncTask(syncCode, callback);
        task.execute();
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
            boolean gotRequest = messenger.waitForSyncMessage();
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
