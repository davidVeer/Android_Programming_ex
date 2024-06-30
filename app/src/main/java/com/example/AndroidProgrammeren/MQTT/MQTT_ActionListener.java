package com.example.AndroidProgrammeren.MQTT;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

public class MQTT_ActionListener implements IMqttActionListener {
    private final String ON_SUCCESS_MSG;
    private final String ON_FAILURE_MSG;
    private final String LOGTAG;
    private final Context CONTEXT;

    public MQTT_ActionListener(String onSuccessMsg, String onFailureMsg, Context context) {
        this.ON_SUCCESS_MSG = onSuccessMsg;
        this.ON_FAILURE_MSG = onFailureMsg;
        this.LOGTAG = "MQTT_ActionListener";
        this.CONTEXT = context;
    }

    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        Log.d(LOGTAG, ON_SUCCESS_MSG);
        Toast toast = Toast.makeText(CONTEXT,
                ON_SUCCESS_MSG, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        Log.e(LOGTAG, ON_FAILURE_MSG + " : " +
                exception.getLocalizedMessage());
        Toast toast = Toast.makeText(CONTEXT,
                "Failed to connect to MQTT broker", Toast.LENGTH_LONG);
        toast.show();
    }
}
