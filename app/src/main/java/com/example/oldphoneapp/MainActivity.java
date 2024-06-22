package com.example.oldphoneapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.oldphoneapp.MQTT.MQTT_Connections;
import com.example.oldphoneapp.MQTT.Topic;
import com.example.oldphoneapp.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.Objects;

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


        mqttConnections = new MQTT_Connections(getApplicationContext(), this);
        mqttConnections.connectToBroker();
        mqttConnections.setCallback();
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


    public void updateTextBox(String message) {
        TextView redText = findViewById(R.id.redLedInformation);
        TextView yellowText = findViewById(R.id.yellowLedInformation);
        TextView greenText = findViewById(R.id.greenLedInformation);
        TextView blueText = findViewById(R.id.blueLedInformation);

        try {
            JSONObject jsonData = new JSONObject(message);
            if (jsonData.getBoolean("RedLED")){
                redText.setText("on");
                redText.setBackgroundColor(0xFA0000);
            } else {
                redText.setText("off");
                redText.setBackgroundColor(0x4C4C4C);
            }

            if (jsonData.getBoolean("YellowLED")){
                yellowText.setText("on");
                yellowText.setBackgroundColor(0xFFE600);
            } else {
                yellowText.setText("off");
                yellowText.setBackgroundColor(0xA1A1A1);
            }

            if (jsonData.getBoolean("GreenLED")){
                greenText.setText("on");
                greenText.setBackgroundColor(0x3EFF09);
            } else {
                greenText.setText("off");
                greenText.setBackgroundColor(0xA1A1A1);
            }

            if (jsonData.getBoolean("BlueLED")){
                blueText.setText("on");
                blueText.setBackgroundColor(0x00C4FF);
            } else {
                blueText.setText("off");
                blueText.setBackgroundColor(0x4C4C4C);
            }


        } catch (JSONException e) {
            Log.d("Json", Objects.requireNonNull(e.getMessage()));
        }
    }
}