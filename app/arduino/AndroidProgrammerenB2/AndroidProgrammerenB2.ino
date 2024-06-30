
// importing all libraries:

// for utilising wifi module in arduino
#include <WiFi.h>

// for connection and use of MQTT protocols 
#include <PubSubClient.h>

//to organise data in an easy to use and read fasion
#include <ArduinoJson.h>
#include <ArduinoJson.hpp>



//MQTT_______________________________________________

//Network login Data (utilises personalhotspot as network to ensure access everywhere)
const char* NETWORK_SSID = "David hotspot";
const char* NETWORK_PASSWORD = "20727271";

//MQTT connection data
const char* MQTT_URL = "broker.hivemq.com" ;
const int MQTT_PORT = 1883;
const char* MQTT_USERNAME = "AndroidProgramming-B2:Broker";
const char* MQTT_PASSWORD = "AndroidProgramming-B2:Broker";

const char* MQTT_CLIENT_NAME = "AndroidProgramming-B2:Hardware-00000";

//MQTT topics
const char* BUTTON_1_TOPIC = "Hardware/Buttons/1";
const char* BUTTON_2_TOPIC = "Hardware/Buttons/2";
const char* BUTTON_3_TOPIC = "Hardware/Buttons/3";
const char* BUTTON_4_TOPIC = "Hardware/Buttons/4";

const char* LED_ALL_TOPIC = "Hardware/LED/all";

const char* SOFTWARE_RED_TOPIC = "Software/Button/Red";
const char* SOFTWARE_YELLOW_TOPIC = "Software/Button/Yellow";
const char* SOFTWARE_GREEN_TOPIC = "Software/Button/Green";
const char* SOFTWARE_BLUE_TOPIC = "Software/Button/Blue";
const char* SOFTWARE_STARTUP_TOPIC = "Software/Startup";

void callback(char* topic, byte* payload, unsigned int length);

WiFiClient wifiClient;
PubSubClient mqttClient(wifiClient);

//Hardware_____________________________________________

//setting up an enum of sorts
const int RED = 1;
const int YELLOW = 2;
const int GREEN = 3;
const int BLUE = 4;

//setting (gpio)pin values
const int RED_LED_PIN = 23 ;
const int YELLOW_LED_PIN = 22;
const int GREEN_LED_PIN = 32;
const int BLUE_LED_PIN = 33;

const int BUTTON_1_PIN = 21;
const int BUTTON_2_PIN = 19;
const int BUTTON_3_PIN = 5;
const int BUTTON_4_PIN = 18;

//initialising boolean values
bool redLedOn;
bool yellowLedOn;
bool greenLedOn;
bool blueLedOn;

bool button1Pressed;
bool button2Pressed;
bool button3Pressed;
bool button4Pressed;

bool previousStateButton1;
bool previousStateButton2;
bool previousStateButton3;
bool previousStateButton4;

//connection helper methods______________________________________________________

void setupWiFiConnection(){

  WiFi.mode(WIFI_STA);
  WiFi.disconnect();
  Serial.print("connecting to : ");
  Serial.print(NETWORK_SSID);
  WiFi.begin(NETWORK_SSID, NETWORK_PASSWORD);
  Serial.print("Connecting");
  while (WiFi.status() != WL_CONNECTED){
    Serial.print(".");
    delay(100);
  }
  Serial.println("Connected!");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
}

void reconnect(){
  while (!mqttClient.connected()){
    Serial.println("(Re)Attempting connection......");

    MQTT_CLIENT_NAME = "AndroidProgramming-B2:Hardware-";
    MQTT_CLIENT_NAME += (random(0xfffff), HEX);

    if(mqttClient.connect(MQTT_CLIENT_NAME,MQTT_USERNAME, MQTT_PASSWORD)){
      Serial.println("(Re)Connected!");
      mqttClient.subscribe(SOFTWARE_RED_TOPIC);
      mqttClient.subscribe(SOFTWARE_YELLOW_TOPIC);
      mqttClient.subscribe(SOFTWARE_GREEN_TOPIC);
      mqttClient.subscribe(SOFTWARE_BLUE_TOPIC);
      mqttClient.subscribe(SOFTWARE_STARTUP_TOPIC);
      
    }else{
      Serial.print("failed, rc=");
      Serial.println(mqttClient.state());
      delay(5000);
    }

  }
}

//setup__________________________________________________________________________________

void setup() {

  Serial.begin(115200);

  //setting up WiFi & MQTT
  setupWiFiConnection();


  //setting pinModes for all Hardware
  pinMode(RED_LED_PIN, OUTPUT);
  pinMode(YELLOW_LED_PIN, OUTPUT);
  pinMode(GREEN_LED_PIN, OUTPUT);
  pinMode(BLUE_LED_PIN, OUTPUT);

  pinMode(BUTTON_1_PIN, INPUT_PULLUP);
  pinMode(BUTTON_2_PIN, INPUT_PULLUP);
  pinMode(BUTTON_3_PIN, INPUT_PULLUP);
  pinMode(BUTTON_4_PIN, INPUT_PULLUP);

  //setting booleans for all Hardware
  redLedOn = false;
  yellowLedOn = false;
  greenLedOn = false;
  blueLedOn = false; 
  
  button1Pressed = digitalRead(BUTTON_1_PIN);
  button2Pressed = digitalRead(BUTTON_2_PIN);
  button3Pressed = digitalRead(BUTTON_3_PIN);
  button4Pressed = digitalRead(BUTTON_4_PIN);

  previousStateButton1 = button1Pressed;
  previousStateButton2 = button2Pressed;
  previousStateButton3 = button3Pressed;
  previousStateButton4 = button4Pressed;


  mqttClient.setServer(MQTT_URL, MQTT_PORT);
  mqttClient.setCallback(callback);
}

void callback(char* topic, byte* payload, unsigned int length){
  Serial.print("message recieved from : ");
  Serial.println(topic);

  String topicRed = "Software/Button/Red";
  String topicYellow = "Software/Button/Yellow";
  String topicGreen = "Software/Button/Green";
  String topicBlue = "Software/Button/Blue";
  String topicStartup = "Software/Startup";

  if (String(topic) == topicRed){
    switchLights(RED);
  }
  else if (String(topic) == topicYellow){
    switchLights(YELLOW);
  }
  else if (String(topic) == topicGreen){
    switchLights(GREEN); 
  }
  else if (String(topic) == topicBlue){
    switchLights(BLUE);
  }
  else if (String(topic) == topicStartup){
    redLedOn = true;
    yellowLedOn = true;
    greenLedOn = true;
    blueLedOn = true;


    digitalWrite(RED_LED_PIN, redLedOn);
    digitalWrite(YELLOW_LED_PIN, yellowLedOn);
    digitalWrite(GREEN_LED_PIN, greenLedOn);
    digitalWrite(BLUE_LED_PIN, blueLedOn);
  }
  else{
    Serial.println("unknown source");
  }

  publishData();
}

void publishData(){
  //setting up a Json document to send data in structurised form
    StaticJsonDocument<80> jsonDoc;
    char jsonOutput[80];

    jsonDoc["RedLED"] = redLedOn;
    jsonDoc["YellowLED"] = yellowLedOn;
    jsonDoc["GreenLED"] = greenLedOn;
    jsonDoc["BlueLED"] = blueLedOn;

    serializeJson(jsonDoc, jsonOutput);
    Serial.println(jsonOutput);

    //publishing Json document
    mqttClient.publish(LED_ALL_TOPIC, jsonOutput);
}

//hardware helper methods_________________________________________________

void switchLights(int ledColor){
  switch(ledColor){
    case 1:
      redLedOn = !redLedOn;
      digitalWrite(RED_LED_PIN, redLedOn);
      break;
    case 2:
      yellowLedOn = !yellowLedOn;
      digitalWrite(YELLOW_LED_PIN, yellowLedOn);
      break;
    case 3:
      greenLedOn = !greenLedOn;
      digitalWrite(GREEN_LED_PIN, greenLedOn);
      break;
    case 4:
      blueLedOn = !blueLedOn;
      digitalWrite(BLUE_LED_PIN, blueLedOn);
      break;      
  }
}

void readButtons(){
  button1Pressed = digitalRead(BUTTON_1_PIN);
  button2Pressed = digitalRead(BUTTON_2_PIN);
  button3Pressed = digitalRead(BUTTON_3_PIN);
  button4Pressed = digitalRead(BUTTON_4_PIN);
}

void updatePreviousButtonStates(){
  previousStateButton1 = button1Pressed;
  previousStateButton2 = button2Pressed;
  previousStateButton3 = button3Pressed;
  previousStateButton4 = button4Pressed;
}

void checkAndHandleButton1(){
  if (button1Pressed != previousStateButton1 && previousStateButton1){
    switchLights(RED);
    publishData();
  }
}

void checkAndHandleButton2(){
  if (button2Pressed != previousStateButton2 && previousStateButton2){
    switchLights(YELLOW);
    publishData();
  }
}


void checkAndHandleButton3(){
  if (button3Pressed != previousStateButton3 && previousStateButton3){
    switchLights(GREEN);
    publishData();
  }
}


void checkAndHandleButton4(){
  if (button4Pressed != previousStateButton4 && previousStateButton4){
    switchLights(BLUE);
    publishData();
  }
}




//Main loop___________________________________________________________________
void loop() {
  if (!mqttClient.connected()){
    reconnect();
  }
  mqttClient.loop();

  readButtons();

  checkAndHandleButton1();
  checkAndHandleButton2();
  checkAndHandleButton3();
  checkAndHandleButton4();

  updatePreviousButtonStates();
}
