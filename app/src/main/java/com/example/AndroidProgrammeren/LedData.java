package com.example.AndroidProgrammeren;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class LedData {
    private String LOGTAG;

    private boolean redLED;
    private boolean yellowLED;
    private boolean greenLED;
    private boolean blueLED;

    public LedData() {
        this.redLED = false;
        this.yellowLED = false;
        this.greenLED = false;
        this.blueLED = false;
        this.LOGTAG = "LedData";
    }

    public void updateData(String MQTT_message) {
        try {
            JSONObject jsonData = new JSONObject(MQTT_message);
            redLED = jsonData.getBoolean("RedLED");
            yellowLED = jsonData.getBoolean("YellowLED");
            greenLED = jsonData.getBoolean("GreenLED");
            blueLED = jsonData.getBoolean("BlueLED");

            Log.i(LOGTAG,
                    " red: " + this.redLED +
                        "\n yellow: " + this.yellowLED +
                        "\n green: " + this.greenLED +
                        "\n blue: " + this.blueLED);
        } catch (JSONException e) {
            Log.d("Json", Objects.requireNonNull(e.getMessage()));
        }
    }

    public boolean isRedLED() {
        return redLED;
    }

    public boolean isYellowLED() {
        return yellowLED;
    }

    public boolean isGreenLED() {
        return greenLED;
    }

    public boolean isBlueLED() {
        return blueLED;
    }
}
