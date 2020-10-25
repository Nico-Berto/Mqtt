package com.example.mqtt;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import pl.pawelkleczkowski.customgauge.CustomGauge;

public class MainActivity extends AppCompatActivity {
    MqttAndroidClient client;
    // Card 1
    Switch Switch1;
    String Switch1estado;
    Switch Switch3;
    String Switch3estado;

    // Card 2 3 y 4
    CustomGauge gaugeLuz;
    TextView txtgaugeLuz;

    CustomGauge gaugeAcel;
    TextView txtgaugeAcel;

    CustomGauge gaugeHumedad;
    TextView txtgaugeHumedad;

    //Card 5
    TextView txtTempRpi;
    //Card 6
    TextView txtTempSensor;

    Button ButtonOnVerde;
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

        //Inicio Card 2, 3 y 4
        gaugeLuz = (CustomGauge) findViewById(R.id.GaugeLuz);
        txtgaugeLuz = (TextView) findViewById(R.id.ValueGaugeLuz);

        gaugeAcel = (CustomGauge) findViewById(R.id.GaugeAceleracion);
        txtgaugeAcel = (TextView) findViewById(R.id.ValueGaugeAceleracion);

        gaugeHumedad = (CustomGauge) findViewById(R.id.GaugeHumedad);
        txtgaugeHumedad = (TextView) findViewById(R.id.ValueGaugeHumedad);

        //Fin Card 2, 3 y 4

        //Inicio Card5
        txtTempRpi = (TextView) findViewById(R.id.textTempRpi);
        //Fin Car5

        //Inicio Card6
        txtTempSensor = (TextView) findViewById(R.id.textTempSensor);
        //Fin Card7

        //Card 8, 9 y 10
        ButtonOnVerde = (Button) findViewById(R.id.EncenderLedVerde);
        //Fin card 8,9 y 10

        ButtonOnVerde.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String topic = "IoT/ledVerde";
                String payload = "ON";

                try{
                    client.publish(topic,payload.getBytes(),0, false);
                } catch (MqttException e){
                    e.printStackTrace();
                }
            }
        });

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://192.168.0.20:1883",
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

                // Card 2 y 3

                if(topic.matches("IoT/Luz")){
                    gaugeLuz.setValue(Integer.parseInt(new String(message.getPayload())));
                    txtgaugeLuz.setText(new String(message.getPayload()));
                }
                if(topic.matches("IoT/Aceleracion")){
                    gaugeAcel.setValue(Integer.parseInt(new String(message.getPayload())));
                    txtgaugeAcel.setText(new String(message.getPayload()));
                }

                if(topic.matches("IoT/Humedad")){
                    gaugeHumedad.setValue(Integer.parseInt(new String(message.getPayload())));
                    txtgaugeHumedad.setText(new String(message.getPayload()));
                }

                if(topic.matches("IoT/tempRpi")){
                    String tempRaspy = new String(message.getPayload());
                    txtTempRpi.setText(tempRaspy  + " ºC");
                }
                if(topic.matches("IoT/tempSensor")){
                    String tempsensor = new String(message.getPayload());
                    txtTempSensor.setText(tempsensor + " ºC");
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
            client.subscribe("IoT/Aceleracion",0);
            client.subscribe("IoT/Luz",0);
            client.subscribe("IoT/Humedad",0);
            client.subscribe("IoT/tempRpi",0);
            client.subscribe("IoT/tempSensor",0);

        }catch (MqttException e){
            e.printStackTrace();
        }

    }

}