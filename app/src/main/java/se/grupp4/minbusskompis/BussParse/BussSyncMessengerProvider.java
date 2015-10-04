package se.grupp4.minbusskompis.BussParse;

/**
 * Created by Marcus on 10/4/2015.
 */
public class BussSyncMessengerProvider {
    private SyncMessenger messenger;

    private static BussSyncMessengerProvider instance = new BussSyncMessengerProvider();

    public static BussSyncMessengerProvider getInstance() {
        return instance;
    }

    public void setSyncMessenger(SyncMessenger messenger){
        this.messenger = messenger;
    }

    public void removeMessenger(){
        messenger = null;
    }

    public boolean hasMessenger(){
        return messenger != null;
    }

    public SyncMessenger getSyncMessenger() throws NoMessengerPresentException {
        if(hasMessenger()){
            return messenger;
        }
        else
            throw new NoMessengerPresentException("No SyncMessenger present");
    }

    class NoMessengerPresentException extends Exception {
        public NoMessengerPresentException(String s) {
            super(s);
        }
    }
}
