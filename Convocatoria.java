package com.example.racingdeleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import javax.net.ssl.HttpsURLConnection;

public class Convocatoria extends Activity
{
    Context ctx;
    String jugador;
    ListView listView;
    ArrayList<String> convocados;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.convocatoria);

        ctx = getApplicationContext();

        listView = this.findViewById(R.id.listViewConvocatoria);
        Button button = this.findViewById(R.id.comprobarConvocatoria);

        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                TextView textView = (TextView) view;
                jugador = textView.getText().toString();

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
        };
        listView.setOnItemClickListener(listener);

        View.OnClickListener listener1 = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ctx, MainActivity.class);
                startActivity(intent);
            }
        };
        button.setOnClickListener(listener1);

        convocados = new ArrayList<>();

        try
        {
            new GetContacts().execute().get();
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

    public class SendRequest extends AsyncTask<String, Void, String>
    {
        protected void onPreExecute(){}

        protected String doInBackground(String... arg0)
        {
            try
            {
                //trabajo
                URL url = new URL("http://10.245.97.193/api/v1/convocatoria");

                //casa
                //URL url = new URL("http://192.168.0.30/api/v1/convocatoria");

                //clase DAM
                //URL url = new URL ("http://169.254.134.3/api/v1/convocatoria");

                //clase AF
                //URL url = new URL ("http://180.180.0.10/api/v1/convocatoria");

                JSONObject postDataParams = new JSONObject();

                postDataParams.put("jugador", jugador);

                /*SharedPreferences prefs = getSharedPreferences("datos", Context.MODE_PRIVATE);
                String usuario = prefs.getString("usuario","");
                String password = prefs.getString("password","");
                postDataParams.put("usuario", usuario);
                postDataParams.put("password", password);*/

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
                Toast.makeText(ctx, "Se ha añadido correctamente a la convocatoria", Toast.LENGTH_LONG).show();
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

    private class GetContacts extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            HttpHandler sh = new HttpHandler();

            //trabajo
            String url = "http://10.245.97.193/api/v1/disponibles";

            //casa
            //String url = "http://192.168.0.30/api/v1/disponibles";

            //clase DAM
            //String url = "http://169.254.134.3/api/v1/disponibles";

            //clase AF
            //String url = "http://180.180.0.10/api/v1/disponibles";

            /*SharedPreferences prefs = getSharedPreferences("datos", Context.MODE_PRIVATE);
            String usuario = prefs.getString("usuario","");
            String password = prefs.getString("password","");
            url += "/" + usuario + "/" + password;*/

            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null)
            {
                try
                {
                    if (jsonStr.contains("ERROR"))
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(ctx,"ERROR DE AUTENTIFICACIÓN" , Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else
                    {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray equipos = jsonObj.getJSONArray("lista");

                        for (int i = 0; i < equipos.length(); i++)
                        {
                            JSONObject c = equipos.getJSONObject(i);
                            String name = c.getString("Nombre");

                            convocados.add(name);
                        }
                    }
                }
                catch (final JSONException e)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(ctx,"Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            else
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(ctx,"No se pudo obtener el json del servidor. Compruebe el Logcat para posibles errores", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Convocatoria.this, android.R.layout.simple_list_item_1, convocados);
            listView.setAdapter(arrayAdapter);

        }
    }
}
