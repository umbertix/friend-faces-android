package com.umbertix.friendfaces.friendfaces;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.pusher.client.AuthorizationFailureException;
import com.pusher.client.Authorizer;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PrivateChannel;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;

public class MainActivity extends AppCompatActivity {

    private String app_id;
    private String app_key;
    private String app_secret;
    private String user_name;
    private String effect = "wipe";
    public static final String STORAGE_NAME = "FriendFacesPreferences";
    private Pusher pusher;
    private PusherOptions options;
    private PrivateChannel channel;
    private String event_name = "client-hello-friendly-face";
    private String cluster = "mt1";
    private String channel_name = "private-friend-faces-communication";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.loadSettings();
        this.initialize_pusher();
    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        RadioGroup radio_group = (RadioGroup) findViewById(R.id.radioGroup);
        int selectedId = radio_group.getCheckedRadioButtonId();

        // Check which radio button was clicked
        switch(selectedId) {
            case R.id.radio_wipe:
                    this.effect = "wipe";
                    break;
            case R.id.radio_chase:
                    this.effect = "chase";
                    break;
            case R.id.radio_flash:
                    this.effect = "flash";
                    break;
            case R.id.radio_rainbow:
                    this.effect = "rainbow";
                    break;
        }

        try {
            this.channel.trigger(this.event_name, "{\"sender\": \"" + this.user_name + "\",\"effect\": \"" + this.effect + "\"}");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Unable to send. Try again", Toast.LENGTH_SHORT).show();
        }
    }

    public void initialize_pusher() {
        if(
            !this.app_id.equalsIgnoreCase("") &&
            !this.app_key.equalsIgnoreCase("") &&
            !this.app_secret.equalsIgnoreCase("") &&
            !this.user_name.equalsIgnoreCase("")
        ){

            Authorizer authorizer = new Authorizer() {
                @Override
                public String authorize(String channelName, String socketId) throws AuthorizationFailureException {
                    return "{ key: 'fffff' }";
                }
            };
//            HttpAuthorizer auth = new HttpAuthorizer().authorize();
            this.options = new PusherOptions().setCluster(this.cluster).setAuthorizer(authorizer);
            this.pusher = new Pusher(this.app_key, this.options);
            this.pusher.connect(new ConnectionEventListener() {
                @Override
                public void onConnectionStateChange(ConnectionStateChange change) {
                    System.out.println("State changed to " + change.getCurrentState() +
                            " from " + change.getPreviousState());
                }

                @Override
                public void onError(String message, String code, Exception e) {
                    System.out.println("There was a problem connecting!");
                }
            }, ConnectionState.ALL);

            // Subscribe to a channel
            this.channel = this.pusher.subscribePrivate(this.channel_name,
                    new PrivateChannelEventListener() {
                        @Override
                        public void onEvent(String channelName, String eventName, String data) {
                            System.out.println(
                                    String.format("Authentication EVENT due to [%s], exception was [%s]", eventName, data)
                            );
                        }

                        @Override
                        public void onSubscriptionSucceeded(String channelName) {
                            System.out.println("Subscribed!");
                        }

                        @Override
                        public void onAuthenticationFailure(String message, Exception e) {
                            System.out.println(
                                    String.format("Authentication failure due to [%s], exception was [%s]", message, e)
                            );
                        }
                    });
            this.pusher.connect();

        } else {
            // INFORM TO FULLFILL SETTINGS
            Toast.makeText(getApplicationContext(), "One of the settings details is missing!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Saves all setting in a permanent way
     * @param view View
     */
    public void saveSetting(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);

        EditText app_id_element = findViewById(R.id.app_id);
        String app_id_value = app_id_element.getText().toString();

        EditText app_key_element = findViewById(R.id.app_key);
        String app_key_value = app_key_element.getText().toString();

        EditText app_secret_element = findViewById(R.id.app_secret);
        String app_secret_value = app_secret_element.getText().toString();

        EditText user_name_element = findViewById(R.id.user_name);
        String user_name_value = user_name_element.getText().toString();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("APP_ID", app_id_value);
        editor.putString("APP_KEY", app_key_value);
        editor.putString("APP_SECRET", app_secret_value);
        editor.putString("USER_NAME", user_name_value);

        editor.apply();
        Toast.makeText(getApplicationContext(), "You have been saved!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Loads all saved settings
     */
    public void loadSettings() {
        SharedPreferences preferences = getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        this.app_id = preferences.getString("APP_ID", "");
        this.app_key = preferences.getString("APP_KEY", "");
        this.app_secret = preferences.getString("APP_SECRET", "");
        this.user_name = preferences.getString("USER_NAME", "");

        ((EditText)findViewById(R.id.app_id)).setText(app_id);
        ((EditText)findViewById(R.id.app_key)).setText(app_key);
        ((EditText)findViewById(R.id.app_secret)).setText(app_secret);
        ((EditText)findViewById(R.id.user_name)).setText(user_name);
    }
}
