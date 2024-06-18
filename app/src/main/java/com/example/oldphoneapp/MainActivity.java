package com.example.oldphoneapp;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.oldphoneapp.MQTT.MQTT_Connections;
import com.example.oldphoneapp.MQTT.Topic;
import com.example.oldphoneapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private MQTT_Connections mqttConnections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initialiseRedButton();
        initialiseYellowButton();
        initialiseGreenButton();
        initialiseBlueButton();


        mqttConnections = new MQTT_Connections(getApplicationContext());
        mqttConnections.setCallback();
        mqttConnections.connectToBroker();

        if (mqttConnections.getMqttAndroidClient().isConnected()){
            mqttConnections.subscribeToTopic(mqttConnections.getTopic(Topic.HARDWARE_LED_RED));
            mqttConnections.subscribeToTopic(mqttConnections.getTopic(Topic.HARDWARE_LED_YELLOW));
            mqttConnections.subscribeToTopic(mqttConnections.getTopic(Topic.HARDWARE_LED_GREEN));
            mqttConnections.subscribeToTopic(mqttConnections.getTopic(Topic.HARDWARE_LED_BLUE));
        }
    }

    public void initialiseRedButton(){
        View publishButton = findViewById(R.id.redLedButton);
        publishButton.setOnClickListener(v -> {
            String topic = mqttConnections.getTopic(Topic.BUTTON_1);
            mqttConnections.publishMessage(topic, "red:toggle");

        });
    }
    public void initialiseYellowButton(){
        View publishButton = findViewById(R.id.yellowLedButton);
        publishButton.setOnClickListener(v -> {
            String topic = mqttConnections.getTopic(Topic.BUTTON_2);
            mqttConnections.publishMessage(topic, "yellow:toggle");

        });
    }
    public void initialiseGreenButton(){
        View publishButton = findViewById(R.id.greenLedButton);
        publishButton.setOnClickListener(v -> {
            String topic = mqttConnections.getTopic(Topic.BUTTON_3);
            mqttConnections.publishMessage(topic, "green:toggle");

        });
    }
    public void initialiseBlueButton(){
        View publishButton = findViewById(R.id.blueLedButton);
        publishButton.setOnClickListener(v -> {
            String topic = mqttConnections.getTopic(Topic.BUTTON_4);
            mqttConnections.publishMessage(topic, "blue:toggle");

        });
    }


}