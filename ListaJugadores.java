package com.example.racingdeleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ListaJugadores extends Activity
{
    Context ctx;
    private String TAG = ListaJugadores.class.getSimpleName();
    ArrayList<String> jugadoresList;
    ListView listViewJugadores;
    Button buttonInsertar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_jugadores);

        ctx = getApplicationContext();

        listViewJugadores = this.findViewById(R.id.listViewJugadores);


        AdapterView.OnItemClickListener listenerJugadores = new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(ctx, Detalles.class);
                TextView textView = (TextView) view;
                String jugador = textView.getText().toString();
                intent.putExtra("id", Integer.toString(position+1));
                intent.putExtra("jugador", jugador);
                startActivity(intent);
            }
        };
        listViewJugadores.setOnItemClickListener(listenerJugadores);

        SharedPreferences prefs = getSharedPreferences("datos", Context.MODE_PRIVATE);
        String entrenador = prefs.getString("entrenador","");

        buttonInsertar = this.findViewById(R.id.insertarJugador);

        if(entrenador.equals("0"))
        {
            buttonInsertar.setVisibility(View.GONE);
        }

        View.OnClickListener listenerInsertar = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ctx, NuevoJugador.class);
                startActivity(intent);
            }
        };
        buttonInsertar.setOnClickListener(listenerInsertar);

        jugadoresList = new ArrayList<>();

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
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            HttpHandler sh = new HttpHandler();

            //trabajo
            //String url = "http://10.245.97.193/api/v1/listaJugadores";

            //casa
            String url = "http://192.168.0.30/api/v1/listaJugadores";

            //clase DAM
            //String url = "http://169.254.134.3/api/v1/listaJugadores";

            //clase AF
            //String url = "http://180.180.0.10/api/v1/listaJugadores";

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
                                Toast.makeText(ctx,"ERROR DE AUTENTIFICACIÓN" , Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else
                    {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray competiciones = jsonObj.getJSONArray("listaJugadores");

                        for (int i = 0; i < competiciones.length(); i++)
                        {
                            JSONObject c = competiciones.getJSONObject(i);
                            String name = c.getString("Nombre");

                            jugadoresList.add(name);
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
                            Toast.makeText(ctx,"Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            else
            {
                Log.e(TAG, "No se pudo obtener el json del servidor");
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
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ListaJugadores.this, android.R.layout.simple_list_item_1, jugadoresList);
            listViewJugadores.setAdapter(arrayAdapter);
        }
    }
}
