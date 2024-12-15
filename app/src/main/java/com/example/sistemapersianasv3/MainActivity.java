package com.example.sistemapersianasv3;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String WRITE_API_KEY = "H036ANY5KA6UT7J6";
    private static final String READ_API_KEY = "H51P9PCEPBUO0HJO";
    private static final String CHANNEL_ID = "2784054";

    private TextView tvSensorValue;
    private Button btnAbrir, btnCerrar;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSensorValue = findViewById(R.id.tvSensorValue);
        btnAbrir = findViewById(R.id.btnAbrir);
        btnCerrar = findViewById(R.id.btnCerrar);
        client = new OkHttpClient();

        // Botones para enviar comandos
        btnAbrir.setOnClickListener(v -> enviarComando(1)); // Abrir persiana
        btnCerrar.setOnClickListener(v -> enviarComando(2)); // Cerrar persiana

        // Actualizar el valor del sensor periódicamente
        actualizarSensor();
    }

    private void enviarComando(int comando) {
        String url = "https://api.thingspeak.com/update?api_key=" + WRITE_API_KEY + "&field2=" + comando;
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ThingSpeak", "Error al enviar comando", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("ThingSpeak", "Comando enviado correctamente");
                } else {
                    Log.d("ThingSpeak", "Error al enviar comando: Código " + response.code());
                }
            }
        });
    }

    private void actualizarSensor() {
        String url = "https://api.thingspeak.com/channels/" + CHANNEL_ID + "/fields/1/last?api_key=" + READ_API_KEY;
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ThingSpeak", "Error al obtener datos del sensor", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String sensorValue = response.body().string();
                    runOnUiThread(() -> tvSensorValue.setText("Sensor de Luz: " + sensorValue));
                }
            }
        });
    }
}
