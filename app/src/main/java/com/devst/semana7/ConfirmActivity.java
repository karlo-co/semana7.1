package com.devst.semana7;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/*
 * =============================================
 * EXPLÍCITO 3 (segunda parte): ConfirmActivity
 * =============================================
 *
 * Muestra los datos que llegaron desde FormActivity
 * y DEVUELVE un resultado con setResult():
 *
 *   - RESULT_OK        → el usuario confirmó
 *   - RESULT_CANCELED  → el usuario quiere corregir
 */
public class ConfirmActivity extends AppCompatActivity {

    // Clave del dato que devolvemos a FormActivity
    public static final String EXTRA_CODIGO = "extra_codigo";

    TextView txtDatos;
    Button btnConfirmar;
    Button btnCorregir;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_confirm);

        txtDatos = findViewById(R.id.txtDatos);
        btnConfirmar = findViewById(R.id.btnConfirmar);
        btnCorregir = findViewById(R.id.btnCorregir);


        // =========================================
        // LEER DATOS RECIBIDOS
        // =========================================

        String nombre = getIntent().getStringExtra(FormActivity.EXTRA_NOMBRE);
        String correo = getIntent().getStringExtra(FormActivity.EXTRA_CORREO);
        String mensaje = getIntent().getStringExtra(FormActivity.EXTRA_MENSAJE);


        // VALIDACIÓN: si faltan datos, no tiene sentido confirmar
        if (nombre == null || correo == null || mensaje == null) {

            Toast.makeText(
                    ConfirmActivity.this,
                    "No se recibieron los datos del formulario",
                    Toast.LENGTH_LONG
            ).show();

            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        txtDatos.setText(
                "👤 Nombre: " + nombre
                        + "\n\n📧 Correo: " + correo
                        + "\n\n💬 Mensaje: " + mensaje
        );


        // =========================================
        // CONFIRMAR → DEVOLVER RESULT_OK
        // =========================================

        btnConfirmar.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        // Generamos un "código de registro" simple
                        String codigo = "REG-" + (System.currentTimeMillis() % 100000);

                        // Intent de RESPUESTA (no abre ninguna pantalla,
                        // solo transporta datos de vuelta)
                        Intent respuesta = new Intent();
                        respuesta.putExtra(EXTRA_CODIGO, codigo);

                        setResult(RESULT_OK, respuesta);

                        // Cerramos y volvemos a FormActivity
                        finish();

                    }
                });


        // =========================================
        // CORREGIR → DEVOLVER RESULT_CANCELED
        // =========================================

        btnCorregir.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        setResult(RESULT_CANCELED);

                        finish();

                    }
                });

    }
}
