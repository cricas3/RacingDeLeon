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

public class Modificar extends Activity
{
    Context ctx;
    String nombre;
    String apellido1;
    String apellido2;
    String goles;
    String partidos;
    String id;
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
        id = intent.getStringExtra("id");

        editTextNombre = this.findViewById(R.id.nombreModificar);
        editTextApellido1 = this.findViewById(R.id.apellido1Modificar);
        editTextApellido2 = this.findViewById(R.id.apellido2Modificar);
        editTextGoles = this.findViewById(R.id.golesModificar);
        editTextPartidos = this.findViewById(R.id.partidosModificar);

        SharedPreferences prefs = getSharedPreferences("datos", Context.MODE_PRIVATE);
        String entrenador = prefs.getString("entrenador","");

        Button buttonModificar = this.findViewById(R.id.modificarDatos);

        if(entrenador.equals("0"))
        {
            buttonModificar.setVisibility(View.GONE);
        }

        editTextNombre.setText(nombreDetalles);
        editTextApellido1.setText(apellido1Detalles);
        editTextApellido2.setText(apellido2Detalles);
        editTextGoles.setText(golesDetalles);
        editTextPartidos.setText(partidosDetalles);

        final String nombreInicial = editTextNombre.getText().toString();
        final String apellido1Inicial = editTextApellido1.getText().toString();
        final String apellido2Inicial = editTextApellido2.getText().toString();
        final String golesInicial = editTextGoles.getText().toString();
        final String partidosInicial = editTextPartidos.getText().toString();

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

                if (nombre.equals("") || apellido1.equals("") || apellido2.equals("") || goles.equals("") || partidos.equals(""))
                {
                    Toast.makeText(ctx, "ERROR: no se puede modificar, todos los campos deben tener contenido", Toast.LENGTH_LONG).show();
                }
                else
                {
                    if(nombre.equals(nombreInicial) && apellido1.equals(apellido1Inicial) && apellido2.equals(apellido2Inicial) && goles.equals(golesInicial) && partidos.equals(partidosInicial))
                    {
                        Toast.makeText(ctx, "ERROR: no se han modificado los campos", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        new SendRequest().execute();
                        Intent intent = new Intent(ctx, ListaJugadores.class);
                        startActivity(intent);
                    }
                }
            }
        };
        buttonModificar.setOnClickListener(listenerModificar);
    }

    public class SendRequest extends AsyncTask<String, Void, String>
    {
        protected void onPreExecute(){}

        protected String doInBackground(String... arg0)
        {
            String msg = "OK";

            SharedPreferences prefs = getSharedPreferences("datos", Context.MODE_PRIVATE);
            String usuario = prefs.getString("usuario","");
            String password = prefs.getString("password","");

            String urlstr = "http://192.168.0.30/api/v1/modificarDetalles/" + usuario + "/" + password + "/" + nombre + "/" + apellido1 + "/" + apellido2 + "/" + goles + "/" + partidos + "/" + id;
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(urlstr);

            if (jsonStr != null)
            {
                if (jsonStr.contains("ERROR"))
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(ctx,"ERROR AL ACTUALIZAR" , Toast.LENGTH_LONG).show();
                        }
                    });
                    msg = "ERROR AL ACTUALIZAR";
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
            return msg;
        }

        @Override
        protected void onPostExecute(String result)
        {
            if (result.contains("OK"))
            {
                Toast.makeText(ctx, "Se han modificado correctamente los datos", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
            }
        }
    }
}
