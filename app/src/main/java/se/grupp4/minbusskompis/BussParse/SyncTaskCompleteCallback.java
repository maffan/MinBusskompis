package se.grupp4.minbusskompis.BussParse;

/**
 * Created by Marcus on 10/1/2015.
 */
public interface SyncTaskCompleteCallback {
    void onSyncTaskComplete(boolean success, String installationId);
}
