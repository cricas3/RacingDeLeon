package com.example.racingdeleon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    Context ctx;
    ArrayList<String> jugadoresList;
    ListView listViewJugadores;
    Button buttonAniadir;
    Button buttonEliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ctx = getApplicationContext();

        SharedPreferences prefs = getSharedPreferences("datos", Context.MODE_PRIVATE);
        String entrenador = prefs.getString("entrenador","");

        buttonAniadir = this.findViewById(R.id.aniadirJugador);
        buttonEliminar = this.findViewById(R.id.borrarConvocatoria);

        if(entrenador.equals("0"))
        {
            buttonAniadir.setVisibility(View.GONE);
            buttonEliminar.setVisibility(View.GONE);
        } 

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                                        R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View.OnClickListener listenerAniadir = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ctx, Convocatoria.class);
                startActivity(intent);
            }
        };
        buttonAniadir.setOnClickListener(listenerAniadir);

        View.OnClickListener listenerEliminar = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
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
        };
        buttonEliminar.setOnClickListener(listenerEliminar);

        listViewJugadores = this.findViewById(R.id.listaJugadores);

        jugadoresList = new ArrayList<>();

        new GetContacts().execute();
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.competiciones)
        {
            Intent intent = new Intent(this, ListaCompeticiones.class);
            startActivity(intent);
        }
        else if (id == R.id.equipos)
        {
            Intent intent = new Intent(this, ListaEquipos.class);
            startActivity(intent);
        }
        else if (id == R.id.plantilla)
        {
            Intent intent = new Intent(this, ListaJugadores.class);
            startActivity(intent);
        }
        else if (id == R.id.cerrarSesion)
        {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
            //String url = "http://10.245.97.193/api/v1/listaConvocados";

            //casa
            String url = "http://192.168.0.30/api/v1/listaConvocados";

            //clase DAM
            //String url = "http://169.254.134.3/api/v1/listaConvocados";

            //clase AF
            //String url = "http://180.180.0.10/api/v1/listaConvocados";

            SharedPreferences prefs = getSharedPreferences("datos", Context.MODE_PRIVATE);
            String usuario = prefs.getString("usuario","");
            String password = prefs.getString("password","");
            url += "/" + usuario + "/" + password;

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
                            String name = c.getString("Jugador");

                            jugadoresList.add(name);
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
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, jugadoresList);
            listViewJugadores.setAdapter(arrayAdapter);
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
                //URL url = new URL("http://10.245.97.193/api/v1/borrarConvocatoria");

                //casa
                URL url = new URL("http://192.168.0.30/api/v1/borrarConvocatoria");

                //clase DAM
                //URL url = new URL ("http://169.254.134.3/api/v1/borrarConvocatoria");

                //clase AF
                //URL url = new URL ("http://180.180.0.10/api/v1/borrarConvocatoria");

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
                Toast.makeText(ctx, "Se ha borrado correctamente a la convocatoria", Toast.LENGTH_LONG).show();
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
