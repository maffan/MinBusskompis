package se.grupp4.minbusskompis.parsebuss;

/**
 * Simple functor to use as callback when syncing devices
 */
public interface SyncTaskCompleteCallback {
    /**
     * Called after a sync attempt has been performed
     * @param success Depicts if the sync was successful or not
     * @param installationId If success is true, contains the Parse Installation ID of the other device
     */
    void onSyncTaskComplete(boolean success, String installationId);
}
