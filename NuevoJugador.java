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
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import javax.net.ssl.HttpsURLConnection;

public class NuevoJugador extends Activity
{
    Context ctx;
    String nombre;
    String apellido1;
    String apellido2;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nuevo_jugador);

        ctx = getApplicationContext();

        final EditText editTextNombre = this.findViewById(R.id.editTextNombreNuevo);
        final EditText editTextApellido1 = this.findViewById(R.id.editTextApellido1Nuevo);
        final EditText editTextApellido2 = this.findViewById(R.id.editTextApellido2Nuevo);
        Button buttonInsertar = this.findViewById(R.id.insertarNuevo);

        View.OnClickListener listener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                nombre = editTextNombre.getText().toString();
                apellido1 = editTextApellido1.getText().toString();
                apellido2 = editTextApellido2.getText().toString();

                if(nombre.equals("") || apellido1.equals("") || apellido2.equals(""))
                {
                    Toast.makeText(ctx, "ERROR: no se ha insertado debido a que no puede haber campos vacios", Toast.LENGTH_LONG).show();
                }
                else
                {
                    try
                    {
                        new SendRequest().execute().get();
                    }
                    catch (ExecutionException e)
                    {
                        e.printStackTrace();
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(ctx, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        buttonInsertar.setOnClickListener(listener);
    }

    public class SendRequest extends AsyncTask<String, Void, String>
    {
        protected void onPreExecute(){}

        protected String doInBackground(String... arg0)
        {
            try
            {
                //trabajo
                //URL url = new URL("http://10.245.97.193/api/v1/insertarNuevo");

                //casa
                URL url = new URL("http://192.168.0.30/api/v1/insertarNuevo");

                //clase DAM
                //URL url = new URL ("http://169.254.134.3/api/v1/insertarNuevo");

                //clase AF
                //URL url = new URL ("http://180.180.0.10/api/v1/insertarNuevo");

                JSONObject postDataParams = new JSONObject();

                postDataParams.put("nombre", nombre);
                postDataParams.put("apellido1", apellido1);
                postDataParams.put("apellido2", apellido2);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

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
                return new String("Exception: " +e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            if (result.contains("OK"))
            {
                Toast.makeText(ctx, "El nuevo jugador se ha insertado correctamente", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
            }
        }
    }

    public String getPostDataString(JSONObject params) throws Exception
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext())
        {
            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return result.toString();
    }
}
