package com.example.AndroidProgrammeren;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.AndroidProgrammeren.MQTT.MQTT_Connections;
import com.example.AndroidProgrammeren.MQTT.Topic;
import com.example.AndroidProgrammeren.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private MQTT_Connections mqttConnections;
    TextView redText;
    TextView yellowText;
    TextView greenText;
    TextView blueText;


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
        initialiseAllButton();

        redText = findViewById(R.id.redLedInformation);
        yellowText = findViewById(R.id.yellowLedInformation);
        greenText = findViewById(R.id.greenLedInformation);
        blueText = findViewById(R.id.blueLedInformation);

        mqttConnections = new MQTT_Connections(getApplicationContext(), this);
        mqttConnections.connectToBroker();
        mqttConnections.setCallback();

    }

    public void initialiseRedButton() {
        View publishButton = findViewById(R.id.redLedButton);
        publishButton.setOnClickListener(v -> {
            String topic = mqttConnections.getTopic(Topic.BUTTON_1);
            mqttConnections.publishMessage(topic, "red:toggle");

        });
    }

    public void initialiseYellowButton() {
        View publishButton = findViewById(R.id.yellowLedButton);
        publishButton.setOnClickListener(v -> {
            String topic = mqttConnections.getTopic(Topic.BUTTON_2);
            mqttConnections.publishMessage(topic, "yellow:toggle");

        });
    }

    public void initialiseGreenButton() {
        View publishButton = findViewById(R.id.greenLedButton);
        publishButton.setOnClickListener(v -> {
            String topic = mqttConnections.getTopic(Topic.BUTTON_3);
            mqttConnections.publishMessage(topic, "green:toggle");

        });
    }

    public void initialiseBlueButton() {
        View publishButton = findViewById(R.id.blueLedButton);
        publishButton.setOnClickListener(v -> {
            String topic = mqttConnections.getTopic(Topic.BUTTON_4);
            mqttConnections.publishMessage(topic, "blue:toggle");

        });
    }

    public void initialiseAllButton(){
        View publishButton = findViewById(R.id.allLedButton);
        publishButton.setOnClickListener(v -> {
            String topic = mqttConnections.getTopic(Topic.BUTTON_ALL);
            mqttConnections.publishMessage(topic, "all:toggle");

        });
    }

}