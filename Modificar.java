package com.example.racingdeleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Modificar extends Activity
{
    Context ctx;
    String nombre;
    String apellido1;
    String apelldio2;
    String goles;
    String partidos;
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

        editTextNombre = this.findViewById(R.id.nombreModificar);
        editTextApellido1 = this.findViewById(R.id.apellido1Modificar);
        editTextApellido2 = this.findViewById(R.id.apellido2Modificar);
        editTextGoles = this.findViewById(R.id.golesModificar);
        editTextPartidos = this.findViewById(R.id.partidosModificar);
        Button buttonMOdificar = this.findViewById(R.id.modificarDatos);

        View.OnClickListener listenerModificar = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                nombre = editTextNombre.getText().toString();
                apellido1 = editTextApellido1.getText().toString();
                apelldio2 = editTextApellido2.getText().toString();
                goles = editTextGoles.getText().toString();
                partidos = editTextPartidos.getText().toString();

                Intent intent = new Intent(ctx, Detalles.class);
                startActivity(intent);
            }
        };
        buttonMOdificar.setOnClickListener(listenerModificar);




    }
}
