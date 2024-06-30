package com.example.AndroidProgrammeren.ledfunctions;

import android.util.Log;
import android.widget.TextView;

import com.example.AndroidProgrammeren.MainActivity;
import com.example.AndroidProgrammeren.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class RedLed implements Led{

    private boolean ledState;
    private MainActivity mainActivity;
    private String LOGTAG;

    public RedLed(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        ledState = false;
        this.LOGTAG = "RedLed";
    }

    @Override
    public void update(String MQTT_message) {
        try {
            JSONObject jsonData = new JSONObject(MQTT_message);
            ledState = jsonData.getBoolean("RedLED");
            Log.i(LOGTAG, " red: " + this.ledState);
        } catch (JSONException e) {
            Log.e("Json" + LOGTAG, Objects.requireNonNull(e.getMessage()));
        }

        TextView led = mainActivity.findViewById(R.id.redLedInformation);
        if (ledState)
            led.setText(R.string.on);
        else
            led.setText(R.string.off);
    }

}
