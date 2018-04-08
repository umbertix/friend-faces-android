package com.umbertix.friendfaces.friendfaces;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.pusher.client.Pusher;

public class MainActivity extends AppCompatActivity {

    private String app_id;
    private String app_key;
    private String app_secret;
    private String user_name;
    private String effect = "wipe";
    public static final String STORAGE_NAME = "FriendFacesPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.loadSettings();
    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        if(
                !this.app_id.equalsIgnoreCase("") &&
                !this.app_key.equalsIgnoreCase("") &&
                !this.app_secret.equalsIgnoreCase("") &&
                !this.user_name.equalsIgnoreCase("")
        ){
            Pusher pusher = new Pusher(this.app_id, this.app_key, this.app_secret);
            pusher.setCluster("mt1");

            boolean checked = ((RadioButton) view).isChecked();

            // Check which radio button was clicked
            switch(view.getId()) {
                case R.id.radio_wipe:
                    if (checked)
                        this.effect = "wipe";
                        break;
                case R.id.radio_chase:
                    if (checked)
                        this.effect = "chase";
                        break;
                case R.id.radio_flash:
                    if (checked)
                        this.effect = "flash";
                        break;
                case R.id.radio_rainbow:
                    if (checked)
                        this.effect = "rainbow";
                        break;
            }

            pusher.trigger("friend-faces-communication", " hello-friendly-face, {'sender': '" + this.user_name + "','effect': '" + this.effect + "'}");
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

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("APP_ID", app_id);
        editor.putString("APP_KEY", app_key);
        editor.putString("APP_SECRET", app_secret);
        editor.putString("USER_NAME", user_name);

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
