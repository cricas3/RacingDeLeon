package com.example.racingdeleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class Modificar extends Activity
{
    Context ctx;
    String nombre;
    String apellido1;
    String apellido2;
    String goles;
    String partidos;
    EditText editTextNombre;
    EditText editTextApellido1;
    EditText editTextApellido2;
    EditText editTextGoles;
    EditText editTextPartidos;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modificar);

        ctx = getApplicationContext();

        Intent intent = getIntent();
        String nombreDetalles = intent.getStringExtra("nombre");
        String apellido1Detalles = intent.getStringExtra("apellido1");
        String apellido2Detalles = intent.getStringExtra("apellido2");
        String golesDetalles = intent.getStringExtra("goles");
        String partidosDetalles = intent.getStringExtra("partidos");

        editTextNombre = this.findViewById(R.id.nombreModificar);
        editTextApellido1 = this.findViewById(R.id.apellido1Modificar);
        editTextApellido2 = this.findViewById(R.id.apellido2Modificar);
        editTextGoles = this.findViewById(R.id.golesModificar);
        editTextPartidos = this.findViewById(R.id.partidosModificar);

        Button buttonModificar = this.findViewById(R.id.modificarDatos);

        editTextNombre.setText(nombreDetalles);
        editTextApellido1.setText(apellido1Detalles);
        editTextApellido2.setText(apellido2Detalles);
        editTextGoles.setText(golesDetalles);
        editTextPartidos.setText(partidosDetalles);

        View.OnClickListener listenerModificar = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                nombre = editTextNombre.getText().toString();
                apellido1 = editTextApellido1.getText().toString();
                apellido2 = editTextApellido2.getText().toString();
                goles = editTextGoles.getText().toString();
                partidos = editTextPartidos.getText().toString();

                Intent intent = new Intent(ctx, Detalles.class);
                startActivity(intent);

                new SendRequest().execute();
            }
        };
        buttonModificar.setOnClickListener(listenerModificar);
    }

    public class SendRequest extends AsyncTask<String, Void, String>
    {
        protected void onPreExecute(){}

        protected String doInBackground(String... arg0)
        {
            try
            {
                //trabajo
                URL url = new URL("http://10.245.97.193/api/v1/modificarDetalles");

                //casa
                //URL url = new URL("http://192.168.0.30/api/v1/modificarDetalles");

                //clase DAM
                //URL url = new URL ("http://169.254.134.3/api/v1/modificarDetalles");

                //clase AF
                //URL url = new URL ("http://180.180.15.128/api/v1/modificarDetalles");

                JSONObject postDataParams = new JSONObject();

                postDataParams.put("nombre", nombre);
                postDataParams.put("apellido1", apellido1);
                postDataParams.put("apellido2", apellido2);
                postDataParams.put("goles", goles);
                postDataParams.put("partidos", partidos);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK)
                {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while((line = in.readLine()) != null)
                    {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    return sb.toString();
                }
                else
                {
                    return new String("false: "+responseCode);
                }
            }
            catch(Exception e)
            {
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            if (result.contains("OK"))
            {
                Toast.makeText(ctx, "Se han modificado correctamente los datos", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ctx, MainActivity.class);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
            }
        }
    }
}
