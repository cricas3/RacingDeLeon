package com.example.racingdeleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

public class Login extends Activity
{
    Context ctx;
    EditText editTextUsuario;
    EditText editTextPassword;
    String usuario;
    String password;
    CheckBox checkBox;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        ctx = getApplicationContext();

        Button buttonRegistrarse = this.findViewById(R.id.botonRegistrarse);
        Button buttonLogin = this.findViewById(R.id.botonLogin);
        editTextUsuario = this.findViewById(R.id.editTextNombreUsuario);
        editTextPassword = this.findViewById(R.id.editTextPassWord);
        checkBox = this.findViewById(R.id.checkbox);

        prefs = getSharedPreferences("datos", Context.MODE_PRIVATE);
        editTextUsuario.setText(prefs.getString("usuario",""));
        editTextPassword.setText(prefs.getString("password",""));

        View.OnClickListener listenerRegistrarse = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ctx, Registrarse.class);
                startActivity(intent);
            }
        };
        buttonRegistrarse.setOnClickListener(listenerRegistrarse);

        View.OnClickListener listenerLogin = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                usuario = editTextUsuario.getText().toString();
                password = editTextPassword.getText().toString();

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

                if (checkBox.isChecked())
                {
                    prefs = getSharedPreferences("datos",Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("usuario", usuario);
                    editor.putString("password", password);
                    editor.commit();
                }
            }
        };
        buttonLogin.setOnClickListener(listenerLogin);
    }

    public class SendRequest extends AsyncTask<String, Void, String>
    {
        protected void onPreExecute(){}

        protected String doInBackground(String... arg0)
        {
            try
            {
                //trabajo
                //URL url = new URL("http://10.245.97.193/api/v1/login");

                //casa
                URL url = new URL("http://192.168.0.30/api/v1/login");

                //clase DAM
                //URL url = new URL ("http://169.254.134.3/api/v1/login");

                //clase AF
                //URL url = new URL ("http://180.180.0.10/api/v1/login");

                JSONObject postDataParams = new JSONObject();

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
                String entrenador = result.substring(result.length() -2, result.length() - 1);
                prefs = getSharedPreferences("datos",Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("entrenador", entrenador);
                editor.commit();

                Intent intent = new Intent(ctx, MainActivity.class);
                startActivity(intent);
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
