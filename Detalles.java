package com.example.racingdeleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class Detalles extends Activity
{
    Context ctx;
    ListView listViewDetalles;
    ArrayList<String> detallesList;
    String id;
    TextView textView;
    private String TAG = Jugadores.class.getSimpleName();
    String nombre;
    String apellido1;
    String apellido2;
    String goles;
    String partidos;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalles);

        ctx = getApplicationContext();

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        String jugador = intent.getStringExtra("jugador");

        textView = this.findViewById(R.id.textViewDetalles);
        textView.setText(jugador);

        SharedPreferences prefs = getSharedPreferences("datos", Context.MODE_PRIVATE);
        String entrenador = prefs.getString("entrenador","");

        Button buttonModificar = this.findViewById(R.id.botonModificar);

        if(entrenador.equals("0"))
        {
            buttonModificar.setVisibility(View.GONE);
        }

        View.OnClickListener listenerModificar = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ctx, Modificar.class);
                intent.putExtra("nombre", nombre);
                intent.putExtra("apellido1", apellido1);
                intent.putExtra("apellido2", apellido2);
                intent.putExtra("goles", goles);
                intent.putExtra("partidos", partidos);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        };
        buttonModificar.setOnClickListener(listenerModificar);

        listViewDetalles = this.findViewById(R.id.detallesJugador);

        detallesList = new ArrayList<>();

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
            //String url = "http://10.245.97.193/api/v1/detalles/"+id;

            //casa

            String url = "http://192.168.0.30/api/v1/detalles/"+id;

            //clase DAM
            //String url = "http://169.254.134.3/api/v1/detalles/"+id;

            //clase AF
            //String url = "http://180.180.0.10/api/v1/detalles/"+id;

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
                        JSONArray jugadores = jsonObj.getJSONArray("detalles");

                        for (int i = 0; i < jugadores.length(); i++)
                        {
                            JSONObject c = jugadores.getJSONObject(i);
                            nombre = c.getString("Nombre");
                            apellido1 = c.getString("PrimerApellido");
                            apellido2 = c.getString("SegundoApellido");
                            goles = c.getString("Goles");
                            partidos = c.getString("PartidosJugados");

                            detallesList.add("Nombre: " + nombre);
                            detallesList.add("Primer apellido: " + apellido1);
                            detallesList.add("Segundo apellido: " + apellido2);
                            detallesList.add("Goles: " + goles);
                            detallesList.add("Partidos jugados: " + partidos);
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
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Detalles.this, android.R.layout.simple_list_item_1, detallesList);
            listViewDetalles.setAdapter(arrayAdapter);
        }
    }
}
