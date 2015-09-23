package se.grupp4.minbusskompis;

import android.content.Context;

import com.parse.Parse;
import com.parse.ParsePush;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import org.mockito.internal.stubbing.answers.DoesNothing;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by Marcus on 9/23/2015.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Parse.class)
public class BussParseTest {
    Context context;

    @Before
    public void setUp(){
        context = mock(Context.class);
        PowerMockito.mockStatic(Parse.class);
    }

    @After
    public void tearDown(){
        context = null;
        BussParse.ourInstance = null;
    }

    @Test
    public void shouldInitParse(){
        BussParse.getInstance(context);

        PowerMockito.verifyStatic();

        Parse.initialize(context);
    }

    @Test
    public void shouldSendData() throws Exception{
        ParsePush push = mock(ParsePush.class);
        JSONObject json = mock(JSONObject.class);
        BussParse bussParse = BussParse.getInstance(context);
        BussParse spy = spy(bussParse);
        stub(spy.getParsePush()).toReturn(push);
        stub(spy.getJsonObject()).toReturn(json);
        spy.setSendingChannel("testingChannel");
        spy.sendData("testData");
        verify(push).setChannel("testingChannel");
        verify(json).put("data","testData");
        verify(push).setData(json);
        verify(push).sendInBackground(null);
    }



}