package com.stomas.evafinal;


import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

//Librerias de MQTT y Formulario
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {
    //Variables de la conexion MQTT
    private static String mqttHost = "tcp://act-android-iot.cloud.shiftr.io:1883"; //Ip del servidor MQTT
    private static String IdUsuario = "My Application"; //Nombre del dispositivo que se conectara
    private static String Topico = "Mensaje"; //Topico al que se suscribira
    private static String User = "act-android-iot"; //Usuario
    private static String Pass = "UoQ9M0hZKlhAHghS"; //Conexion

    //Variable que se utilizara para imprimir los datos del sensor
    private TextView textView;
    private EditText editTextMessage;
    private Button botonEnvio;

    //Libreria MQTT
    private MqttClient mqttClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        editTextMessage = findViewById(R.id.txtMensaje);
        botonEnvio = findViewById(R.id.botonEnvioMensaje);
        try {
            mqttClient = new MqttClient(mqttHost, IdUsuario, null);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(User);
            options.setPassword(Pass.toCharArray());
            //Conexion al servidor MQTT
            mqttClient.connect(options);
            //Si se conecta imprime un mensaje de MQTT
            Toast.makeText(this, "Aplicación conectada al Servidor MQTT", Toast.LENGTH_LONG).show();
            //Manejo de entrega de datos y perdida de conexion
            mqttClient.setCallback(new MqttCallback() {
                //Metodo en caso de que la conexion se pierda
                @Override
                public void connectionLost(Throwable cause) {
                    Log.d("MQTT", "Conexion perdida");
                }
                //Metodo para enviar el mensaje MQTT
                @Override
                public void messageArrived(String topic, MqttMessage message){
                    String payload = new String(message.getPayload());
                    runOnUiThread(() -> textView.setText(payload));
                }
                //Metodo para verificar si el envio fue exitoso
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d("MQTT", "Entrega completa");
                }
            });
        }catch (MqttException e){
            e.printStackTrace();
        }

        //Al dar click en el boton enviar el mensaje del topico
        botonEnvio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Obtener el mensaje ingresado por el usuario
                String mensaje = editTextMessage.getText().toString();
                try{
                    //Verificar la conexion activa
                    if(mqttClient != null && mqttClient.isConnected()){
                        //Publicar el mensaje en el topico especifico
                        mqttClient.publish(Topico, mensaje.getBytes(), 0, false);
                        //Mostrar el mensaje enviado por el TextView
                        textView.append("\n - "+ mensaje);
                        Toast.makeText(MainActivity.this, "Mensaje enviado", Toast.LENGTH_LONG).show();
                    } else{
                        Toast.makeText(MainActivity.this, "Error: No se pudo enviar el mensaje. La conexion MQTT no esta activa", Toast.LENGTH_LONG).show();
                    }
                } catch (MqttException e){
                    e.printStackTrace();
                }
            }
        });
    }
    public void onClickVentana(View view){
        Intent intent = new Intent(this, formularioActivity.class);
        startActivity(intent);
    }
}