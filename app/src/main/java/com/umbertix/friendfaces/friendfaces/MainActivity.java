package com.umbertix.friendfaces.friendfaces;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import com.pusher.client.Pusher;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        Pusher pusher = new Pusher("APP_ID", "APP_KEY", "APP_SECRET");
        pusher.setCluster("APP_CLUSTER");

        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_pirates:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.radio_ninjas:
                if (checked)
                    // Ninjas rule
                    break;
        }
        //{"sender": "' + self.cfg.get('PUSHER', 'SENDER_NAME') + '", "effect": "' + self.cfg.get('PUSHER', 'SENDER_EFFECT') + '"}
        pusher.trigger("friend-faces-communication", " hello-friendly-face, Collections.singletonMap("message", "Hello World"));
    }
}
