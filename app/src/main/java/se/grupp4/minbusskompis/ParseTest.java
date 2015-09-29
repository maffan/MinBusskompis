package se.grupp4.minbusskompis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;

public class ParseTest extends AppCompatActivity {
    private BussMessenger bussMessenger;
    private EditText  sendingChannelEditText;
    private EditText  listeningChannelEditText;
    private EditText  messageEditText;
    private ListView messagesListView;

    private String sendingChannel;
    private String listeningChannel;
    private ArrayList<String> messageList;
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parse_test);

        bussMessenger = BussMessenger.getInstance();
        sendingChannelEditText = (EditText) findViewById(R.id.sending_channel_edit_text);
        listeningChannelEditText = (EditText) findViewById(R.id.listening_channel_edit_text);
        messageEditText = (EditText) findViewById(R.id.message_edit_text);
        messagesListView = (ListView) findViewById(R.id.messagesListView);
        messageList = new ArrayList<>();

        sendingChannelEditText.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                sendingChannel = s.toString();
            }
        });
        listeningChannelEditText.addTextChangedListener(new MyTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                listeningChannel = s.toString();
                bussMessenger.setListeningChannel(listeningChannel);
            }
        });

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString();
                bussMessenger.setSendingChannel(sendingChannel);
                bussMessenger.sendData(message);
                messageEditText.setText("");
            }
        });
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, messageList);
        messagesListView.setAdapter(adapter);

        bussMessenger.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                Queue<String> messageQueue = bussMessenger.getDataQueue();
                while(!messageQueue.isEmpty()){
                    adapter.add(messageQueue.remove());
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_parse_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private abstract class MyTextWatcher implements TextWatcher {
        public MyTextWatcher() {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
    }
}
