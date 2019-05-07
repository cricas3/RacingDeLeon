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

public class Registrarse extends Activity
{
    Context ctx;
    EditText editTextNombre;
    EditText editTextApellido1;
    EditText editTextApellido2;
    EditText editTextUsuario;
    EditText editTextCorreo;
    EditText editTextPassword;
    String nombre;
    String apellido1;
    String apellido2;
    String correo;
    String usuario;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrarse);

        ctx = getApplicationContext();

        Button buttonRegistrarse = this.findViewById(R.id.botonRegistro);
        editTextNombre = this.findViewById(R.id.editTextNombre);
        editTextApellido1 = this.findViewById(R.id.editTextPrimerApellido);
        editTextApellido2 = this.findViewById(R.id.editTextSegundoApellido);
        editTextUsuario = this.findViewById(R.id.editTextNombreUsuarioRegistro);
        editTextCorreo = this.findViewById(R.id.editTextCorreo);
        editTextPassword = this.findViewById(R.id.editTextPassWordRegistro);

        View.OnClickListener listenerRegistrarse = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                nombre = editTextNombre.getText().toString();
                apellido1 = editTextApellido1.getText().toString();
                apellido2 = editTextApellido2.getText().toString();
                correo = editTextCorreo.getText().toString();
                usuario = editTextUsuario.getText().toString();
                password = editTextPassword.getText().toString();

                if (nombre.equals("") || apellido1.equals("") || apellido1.equals("") || correo.equals("") || usuario.equals("") || password.equals(""))
                {
                    Toast.makeText(ctx, "ERROR tienen que estar rellenos todos los campos", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(ctx, "Usuario insertado correctamente", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ctx, Login.class);
                    startActivity(intent);
                }

            }
        };
        buttonRegistrarse.setOnClickListener(listenerRegistrarse);
    }

    public class SendRequest extends AsyncTask<String, Void, String>
    {
        protected void onPreExecute(){}

        protected String doInBackground(String... arg0)
        {
            try
            {
                //trabajo
                //URL url = new URL("http://10.245.97.193/api/v1/registrar");

                //casa
                URL url = new URL("http://192.168.0.30/api/v1/registrar");

                //clase DAM
                //URL url = new URL ("http://169.254.134.3/api/v1/registrar");

                //clase AF
                //URL url = new URL ("http://180.180.15.128/api/v1/registrar");

                JSONObject postDataParams = new JSONObject();

                postDataParams.put("nombre", nombre);
                postDataParams.put("apellido1", apellido1);
                postDataParams.put("apellido2", apellido2);
                postDataParams.put("correo", correo);
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

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK)
                {
                    BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line="";

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
                    return new String("false : "+responseCode);
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
                //toast registro con existo
            }
            else
            {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
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
            String key= itr.next();
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
