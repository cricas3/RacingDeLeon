package com.example.racingdeleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    String usuario;
    String password;
    EditText editTextNombre;
    EditText editTextApellido1;
    EditText editTextApellido2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nuevo_jugador);

        ctx = getApplicationContext();

        editTextNombre = this.findViewById(R.id.editTextNombreNuevo);
        editTextApellido1 = this.findViewById(R.id.editTextApellido1Nuevo);
        editTextApellido2 = this.findViewById(R.id.editTextApellido2Nuevo);
        Button buttonInsertar = this.findViewById(R.id.insertarNuevo);
        Button buttonVer = this.findViewById(R.id.verJugadores);

        View.OnClickListener listenerVer = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ctx, ListaJugadores.class);
                startActivity(intent);
                finish();
            }
        };
        buttonVer.setOnClickListener(listenerVer);

        SharedPreferences prefs = getSharedPreferences("datos", Context.MODE_PRIVATE);
        usuario = prefs.getString("usuario","");
        password = prefs.getString("password","");

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
                    Toast.makeText(ctx, "ERROR: el jugador no se ha insertado ya que todos los campos tienen que estar completos", Toast.LENGTH_LONG).show();
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

                postDataParams.put("usuario", usuario);
                postDataParams.put("password", password);

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
                Toast.makeText(ctx, "El jugador se ha insertado correctamente", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(ctx, "ERROR: el jugador ya existe, no se puede insertar", Toast.LENGTH_LONG).show();
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
