package se.grupp4.minbusskompis.parsebuss;

/**
 * Created by Marcus on 10/4/2015.
 */
public class BussSyncMessengerProvider {
    private BussParseSyncMessenger messenger;

    private static BussSyncMessengerProvider instance = new BussSyncMessengerProvider();

    public static BussSyncMessengerProvider getInstance() {
        return instance;
    }

    public void setSyncMessenger(BussParseSyncMessenger messenger){
        this.messenger = messenger;
    }

    public void removeMessenger(){
        messenger = null;
    }

    public boolean hasMessenger(){
        return messenger != null;
    }

    public BussParseSyncMessenger getSyncMessenger() throws NoMessengerPresentException {
        if(hasMessenger()){
            return messenger;
        }
        else
            throw new NoMessengerPresentException("No SyncMessenger present");
    }
}
