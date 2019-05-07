package com.example.racingdeleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Equipos extends Activity
{
    Context ctx;
    ListView listViewEquipos;
    ArrayList<String> equiposList;
    String id;
    TextView textView;
    private String TAG = Equipos.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.equipos);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        String competicion = intent.getStringExtra("competicion");

        ctx = getApplicationContext();

        textView = this.findViewById(R.id.textViewCompeticion);
        textView.setText(competicion);

        listViewEquipos = this.findViewById(R.id.listaEquipos);

        equiposList = new ArrayList<>();

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

    private class GetContacts extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            Toast.makeText(Equipos.this,"Json Data is downloading",Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            HttpHandler sh = new HttpHandler();

            //trabajo
            String url = "http://10.245.97.193/api/v1/equipos/"+id;

            //casa
            //String url = "http://192.168.0.30/api/v1/equipos/"+id;

            //clase DAM
            //String url = "http://169.254.134.3/api/v1/equipos/"+id;

            //clase AF
            //String url = "http://180.180.15.128/api/v1/equipos/"+id;

            SharedPreferences prefs = getSharedPreferences("datos", Context.MODE_PRIVATE);
            String usuario = prefs.getString("usuario","");
            String password = prefs.getString("password","");
            url += "/" + usuario + "/" + password;

            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
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
                                Toast.makeText(getApplicationContext(),"ERROR DE AUTENTIFICACIÓN" , Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else
                    {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray competiciones = jsonObj.getJSONArray("equipos");

                        for (int i = 0; i < competiciones.length(); i++)
                        {
                            JSONObject c = competiciones.getJSONObject(i);
                            String name = c.getString("Nombre");

                            equiposList.add(name);
                        }
                    }
                }
                catch (final JSONException e)
                {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(getApplicationContext(),"Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            else
            {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(getApplicationContext(),"Couldn't get json from server. Check LogCat for possible errors!", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Equipos.this, android.R.layout.simple_list_item_1, equiposList);
            listViewEquipos.setAdapter(arrayAdapter);
        }
    }
}
