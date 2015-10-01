package se.grupp4.minbusskompis;

import android.content.Context;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePush;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.verify;

/**
 * Created by Marcus on 9/23/2015.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Parse.class,ParsePush.class})
public class BussMessengerTest {
    BussMessenger bussMessenger;

    @Before
    public void setUp(){
        PowerMockito.mockStatic(ParsePush.class);
        bussMessenger = BussMessenger.getInstance();
    }

    @After
    public void tearDown(){
        bussMessenger = null;
    }





}