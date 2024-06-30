package com.example.AndroidProgrammeren.MQTT;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.AndroidProgrammeren.LedData;
import com.example.AndroidProgrammeren.MainActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MQTT_Connections {
    //MQTT broker connection data

    private final String MQTT_URL = "tcp://broker.hivemq.com:1883";
    private final String MQTT_USERNAME = "AndroidProgramming-B2:Broker";
    private final String MQTT_PASSWORD = "AndroidProgramming-B2:Broker";

    private final String MQTT_CLIENT_ID = "AndroidProgramming-B2:Software-" +
                                            UUID.randomUUID().toString();
    private static final int QUALITY_OF_SERVICE = 0;

    //application data
    MainActivity mainActivity;

    //miscellaneous setup
    private final String LOGTAG  = "MQTT_Connections";
    private final Context CONTEXT;
    private MqttAndroidClient mqttAndroidClient;
    private LedData ledData;

    public MQTT_Connections(Context Context, MainActivity mainActivity) {
        this.CONTEXT = Context;
        mqttAndroidClient = new MqttAndroidClient(CONTEXT, MQTT_URL, MQTT_CLIENT_ID);
        this.mainActivity = mainActivity;
        ledData = new LedData();
    }



    public void setCallback(){
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d(LOGTAG, "connection with broker was was lost due to: " + cause.getLocalizedMessage());
                Toast toast = Toast.makeText(CONTEXT, "Connection with Broker lost ; cause : " +
                        cause.getLocalizedMessage(), Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("subscription", message.toString());
                Toast toast = Toast.makeText(CONTEXT, "Message arrived : " + message, Toast.LENGTH_SHORT);
                toast.show();
                ledData.updateData(message.toString());
                mainActivity.updateLEDText(
                        ledData.isRedLED(),
                        ledData.isYellowLED(),
                        ledData.isGreenLED(),
                        ledData.isBlueLED()
                );
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Toast toast = Toast.makeText(CONTEXT, "Delivery complete", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public void connectToBroker(){
        MqttConnectOptions connectionOptions = new MqttConnectOptions();
        connectionOptions.setAutomaticReconnect(true);
        connectionOptions.setCleanSession(false);
        connectionOptions.setUserName(MQTT_USERNAME);
        connectionOptions.setPassword(MQTT_PASSWORD.toCharArray());
        connectionOptions.setConnectionTimeout(60);
        connectionOptions.setKeepAliveInterval(30);


        try{
            IMqttToken token = mqttAndroidClient.connect(connectionOptions);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i(LOGTAG, "MQTT client is now connected to MQTT broker");
                    Toast toast = Toast.makeText(CONTEXT,
                            "MQTT client is now connected to MQTT broker", Toast.LENGTH_LONG);
                    toast.show();

                    subscribeToTopic(getTopic(Topic.HARDWARE_LEDS));
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(LOGTAG, "MQTT client failed to connect to MQTT broker");
                    Toast toast = Toast.makeText(CONTEXT,
                            "MQTT client failed to connect to MQTT broker", Toast.LENGTH_SHORT);
                    toast.show();

                    connectToBroker();
                }
            });
        } catch (MqttException ex) {
            Log.e(MainActivity.class.getName(), "MQTT exception while connecting to MQTT broker, reason: " +
                    ex.getReasonCode() + ", msg: " + ex.getMessage() + ", cause: " + ex.getCause());
        }

    }

    public void publishMessage(String topic, String msg){
        byte[] payload;
        try {
            payload = msg.getBytes(StandardCharsets.UTF_8);
            MqttMessage message = new MqttMessage(payload);
            message.setQos(QUALITY_OF_SERVICE);
            message.setRetained(false);
            mqttAndroidClient.publish(topic,message);
        }catch (MqttException e){
            Log.e(LOGTAG, "MQTT exception while publishing topic to MQTT broker, msg: " + e.getMessage() +
                    ", cause: " + e.getCause());
        }
    }

    public void subscribeToTopic(String topic){
        try{
            IMqttToken token = mqttAndroidClient.subscribe(topic, QUALITY_OF_SERVICE);
            token.setActionCallback(new MQTT_ActionListener(
                    "MQTT client subscribed to: " + topic,
                    "MQTT client failed to subscribe to: " + topic,
                    CONTEXT
            ));
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    public String getTopic(Topic topic){
        switch (topic){
            case BUTTON_1:
                return "Software/Button/Red";
            case BUTTON_2:
                return "Software/Button/Yellow";
            case BUTTON_3:
                return "Software/Button/Green";
            case BUTTON_4:
                return "Software/Button/Blue";
            case HARDWARE_LEDS:
                return "Hardware/LED/all";
            default:
                return "";

        }
    }
}
