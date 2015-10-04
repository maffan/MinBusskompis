package se.grupp4.minbusskompis.BussParse;

import android.os.AsyncTask;

/**
 * Created by Marcus on 10/4/2015.
 */
public class BussSyncer {
    private SyncMessenger messenger;

    public BussSyncer(SyncMessenger messenger) {
        this.messenger = messenger;
    }

    public void syncWithSyncCode(String syncCode, SyncTaskCompleteCallback callback){
        SyncRequestTask task = new SyncRequestTask(callback);
        task.execute(syncCode);
    }

    private class SyncRequestTask extends AsyncTask<String,Void,Boolean> {
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

    public void waitForSync(CodeGenerator generator, SyncTaskCompleteCallback callback){
        String code = generator.getCode();
        WaitForSyncTask task = new WaitForSyncTask(callback);
        task.execute(code);
    }

    private class WaitForSyncTask extends AsyncTask<String, Void, Boolean>{
        private SyncTaskCompleteCallback callback;

        public WaitForSyncTask(SyncTaskCompleteCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String syncId = params[0];
            if(messenger.waitForSyncRequest(syncId)){
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
