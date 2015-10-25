package se.grupp4.minbusskompis.parsebuss;

/**
 * Simple functor to use as callback on asynchronous tasks.
 */
public interface AsyncTaskCompleteCallback {
    void done();
}
