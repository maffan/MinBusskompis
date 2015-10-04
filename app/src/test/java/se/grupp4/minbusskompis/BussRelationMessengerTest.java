package se.grupp4.minbusskompis;

import com.parse.Parse;
import com.parse.ParsePush;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import se.grupp4.minbusskompis.BussParse.BussRelationMessenger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by Marcus on 9/23/2015.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Parse.class,ParsePush.class})
public class BussRelationMessengerTest {
    BussRelationMessenger bussRelationMessenger;

    @Before
    public void setUp(){
        PowerMockito.mockStatic(ParsePush.class);
        bussRelationMessenger = BussRelationMessenger.getInstance();
    }

    @After
    public void tearDown(){
        bussRelationMessenger = null;
    }





}