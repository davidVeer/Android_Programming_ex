package com.example.oldphoneapp.MQTT;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.oldphoneapp.MainActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
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

    //miscellaneous setup
    private final String LOGTAG  = MainActivity.class.getName();
    private final Context CONTEXT;
    private MqttAndroidClient mqttAndroidClient;

    public MQTT_Connections(Context Context) {
        this.CONTEXT = Context;
        mqttAndroidClient = new MqttAndroidClient(CONTEXT, MQTT_URL, MQTT_CLIENT_ID);


    }



    public void setCallback(){
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Toast toast = Toast.makeText(CONTEXT, "Connection with Broker lost ; cause : " +
                        cause.getLocalizedMessage(), Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Toast toast = Toast.makeText(CONTEXT, "Message arrived : " + message, Toast.LENGTH_LONG);
                toast.show();
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
            token.setActionCallback(new MQTT_ActionListener(
                    "MQTT client is now connected to MQTT broker",
                    "MQTT client failed to connect to MQTT broker",
                    LOGTAG,
                    CONTEXT
                    ));
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
                    LOGTAG,
                    CONTEXT
            ));
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    public void DisconnectFromBroker(){
        try{
            IMqttToken token = mqttAndroidClient.disconnect();
            token.setActionCallback(new MQTT_ActionListener(
                    "MQTT client is now disconnected from MQTT broker",
                    "MQTT client failed to disconnect from MQTT broker",
                    LOGTAG,
                    CONTEXT
            ));
        } catch (MqttException ex) {
            Log.e(MainActivity.class.getName(), "MQTT exception while disconnecting from MQTT broker, reason: " +
                    ex.getReasonCode() + ", msg: " + ex.getMessage() + ", cause: " + ex.getCause());
        }
    }

    public String getTopic(Topic topic){
        switch (topic){
            case BUTTON_1:
                return "Software/button/1";
            case BUTTON_2:
                return "Software/button/2";
            case BUTTON_3:
                return "Software/button/3";
            case BUTTON_4:
                return "Software/button/4";
            case HARDWARE_LED_RED:
                return "Hardware/LED/Red";
            case HARDWARE_LED_BLUE:
                return "Hardware/LED/Yellow";
            case HARDWARE_LED_GREEN:
                return "Hardware/LED/Green";
            case HARDWARE_LED_YELLOW:
                return "Hardware/LED/Blue";
            default:
                return "";

        }
    }


    public MqttAndroidClient getMqttAndroidClient() {
        return this.mqttAndroidClient;
    }
}
