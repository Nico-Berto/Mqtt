package com.example.mqtt;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {
    MqttAndroidClient client;

    Switch Switch1;
    String Switch1estado;
    Switch Switch3;
    String Switch3estado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Inicio Card1
        Switch1 = (Switch) findViewById(R.id.Switch1);
        Switch3 = (Switch) findViewById(R.id.Switch2);

        Switch1.setChecked(false);
        Switch3.setChecked(false);

        //Fin Card1

        String clientId = MqttClient.generateClientId();
        client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://192.168.0.20:1883",
                        clientId);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // Si mqtt conectó
                    Toast.makeText(MainActivity.this, "CONECTADO MQTT", Toast.LENGTH_LONG).show();
                    suscripcionTopics();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(MainActivity.this, "ERROR DE CONEXIÓN", Toast.LENGTH_LONG).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                //Aqui cuando los mensajes lleguen.

                if(topic.matches("esp_mini/frdmkl46z/LED_SW1")){
                    Switch1estado = new String(message.getPayload());

                    if(Switch1estado.matches("true")){
                        Switch1.setChecked(true);
                    }else{
                        Switch1.setChecked(false);
                    }
                }

                if(topic.matches("esp_mini/frdmkl46z/LED_SW3")){
                    Switch3estado = new String(message.getPayload());

                    if(Switch3estado.matches("true")){
                        Switch3.setChecked(true);
                    }else{
                        Switch3.setChecked(false);
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
    private void suscripcionTopics(){

        try{
            client.subscribe("esp_mini/frdmkl46z/LED_SW1",0);
            client.subscribe("esp_mini/frdmkl46z/LED_SW3",0);
            client.subscribe("IoT/Temp",0);
            client.subscribe("IoT/Led1",0);
            client.subscribe("IoT/Led2",0);

        }catch (MqttException e){
            e.printStackTrace();
        }

    }

}