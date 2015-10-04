package se.grupp4.minbusskompis;

import android.support.annotation.NonNull;

import org.junit.*;
import org.mockito.Mockito;

import se.grupp4.minbusskompis.BussParse.BussData;
import se.grupp4.minbusskompis.BussParse.BussMessenger;
import se.grupp4.minbusskompis.BussParse.BussSync;

import static org.junit.Assert.*;

/**
 * Created by Marcus on 10/1/2015.
 */
public class BussSyncTest {
    BussSync bussSync;
    @Before
    public void setUp() {
        bussSync = new BussSync(Mockito.mock(BussMessenger.class),Mockito.mock(BussData.class));
    }

    @Test
    public void shouldGenerateRandomStrings() {
        String random1 = getRandomString();
        String random2 = getRandomString();
        assertFalse(random1.equals(random2));
    }

    @NonNull
    private String getRandomString() {
        String random = bussSync.generateAndGetSyncCode();
        assertFalse(random.isEmpty());
        return random;
    }

}