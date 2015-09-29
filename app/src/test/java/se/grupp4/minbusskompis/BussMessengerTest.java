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
    Context context;

    @Before
    public void setUp(){
        context = mock(Context.class);
        PowerMockito.mockStatic(Parse.class);
    }

    @After
    public void tearDown(){
        context = null;
        BussMessenger.ourInstance = null;
    }

    @Test
    public void shouldInitParse(){
        BussMessenger.getInstance(context);

        PowerMockito.verifyStatic();

        Parse.initialize(context);
    }

    @Test
    public void shouldSendData() throws Exception{
        ParsePush push = mock(ParsePush.class);
        JSONObject json = mock(JSONObject.class);
        BussMessenger spy = spy(BussMessenger.getInstance(context));
        stub(spy.getParsePush()).toReturn(push);
        stub(spy.getJsonObjectWithData(anyString())).toReturn(json);
        spy.setSendingChannel("testingChannel");
        spy.sendData("testData");
        verify(push).setChannel("testingChannel");
        verify(push).setData(json);
        verify(push).sendInBackground(null);
    }

    @Test
    public void shouldQueueData(){
        BussMessenger.getInstance(context).dataReceived("testData1");
        BussMessenger.getInstance(context).dataReceived("testData2");
        BussMessenger.getInstance(context).dataReceived("testData3");
        BussMessenger.getInstance(context).dataReceived("testData4");
        String data = BussMessenger.getInstance(context).getDataQueue().remove();
        assertEquals(data, "testData1");
        data = BussMessenger.getInstance(context).getDataQueue().remove();
        assertEquals(data, "testData2");
        data = BussMessenger.getInstance(context).getDataQueue().remove();
        assertEquals(data, "testData3");
        data = BussMessenger.getInstance(context).getDataQueue().remove();
        assertEquals(data, "testData4");
    }

    @Test
    public void shouldSetListeningChannel(){
        PowerMockito.mockStatic(ParsePush.class);

        ParseInstallation installation = mock(ParseInstallation.class);
        List<Object> channels = new ArrayList<>();
        channels.add("test1");
        channels.add("test2");
        channels.add("test3");
        stub(installation.getList("channels")).toReturn(channels);

        BussMessenger bussMessenger = mock(BussMessenger.class);
        doReturn(installation).when(bussMessenger).getCurrentInstallation();
        doCallRealMethod().when(bussMessenger).setListeningChannel(anyString());
        doCallRealMethod().when(bussMessenger).getListeningChannel();
        bussMessenger.setListeningChannel("testChannel");
        assertEquals(bussMessenger.getListeningChannel(),"testChannel");
    }



}