package se.grupp4.minbusskompis;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.parse.Parse;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.stub;
/**
 * Created by Marcus on 9/23/2015.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Parse.class})
public class BussMessengerPushBroadcastReceiverTest {
    BussParsePushBroadcastReceiver receiver;
    Context context;

    @Before
    public void setUp() throws Exception {
        receiver = new BussParsePushBroadcastReceiver();
        context = mock(Context.class);
    }

    @After
    public void tearDown() throws Exception {
        receiver = null;
    }

    @Test
    public void shouldPassMessageToBussParse() throws Exception {
        PowerMockito.mockStatic(Parse.class);

        Intent intent = mock(Intent.class);
        JSONObject json = mock(JSONObject.class);

        stub(json.getString(anyString())).toReturn("testData");

        BussParsePushBroadcastReceiver spyReceiver = spy(receiver);
        doReturn(json).when(spyReceiver).getJsonObject(any(Bundle.class));

        spyReceiver.onPushReceive(context,intent);

        String data = BussMessenger.getInstance().getDataQueue().remove();

        assertEquals(data,"testData");
    }
}