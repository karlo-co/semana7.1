package com.devst.semana7;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/*
 * =============================================
 * EXPLÍCITO 1: MainActivity → DetalleActivity
 * =============================================
 *
 * Esta pantalla RECIBE los datos que MainActivity
 * envió con putExtra() y los muestra.
 */
public class DetalleActivity extends AppCompatActivity {

    // Claves (KEYS) de los extras.
    // Son "public static final" para usarlas desde MainActivity
    // y evitar errores de tipeo.
    public static final String EXTRA_TITULO = "extra_titulo";
    public static final String EXTRA_DESCRIPCION = "extra_descripcion";
    public static final String EXTRA_TIENE_UBICACION = "extra_tiene_ubicacion";
    public static final String EXTRA_LATITUD = "extra_latitud";
    public static final String EXTRA_LONGITUD = "extra_longitud";

    TextView txtTitulo;
    TextView txtDescripcion;
    TextView txtCoordenadas;
    Button btnVolver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detalle);

        txtTitulo = findViewById(R.id.txtTitulo);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        txtCoordenadas = findViewById(R.id.txtCoordenadas);
        btnVolver = findViewById(R.id.btnVolver);


        // =========================================
        // LEER LOS EXTRAS DEL INTENT
        // =========================================

        // getIntent() devuelve el Intent con el que se abrió esta pantalla
        Intent intent = getIntent();

        String titulo = intent.getStringExtra(EXTRA_TITULO);
        String descripcion = intent.getStringExtra(EXTRA_DESCRIPCION);

        // Para tipos primitivos se entrega un valor por defecto
        boolean tieneUbicacion = intent.getBooleanExtra(EXTRA_TIENE_UBICACION, false);
        double latitud = intent.getDoubleExtra(EXTRA_LATITUD, 0);
        double longitud = intent.getDoubleExtra(EXTRA_LONGITUD, 0);


        // =========================================
        // VALIDACIÓN: los extras podrían venir null
        // =========================================

        if (titulo == null || titulo.isEmpty()) {
            titulo = "Sin título";
        }

        if (descripcion == null || descripcion.isEmpty()) {
            descripcion = "No se recibió descripción";
        }

        txtTitulo.setText(titulo);
        txtDescripcion.setText(descripcion);

        if (tieneUbicacion) {

            txtCoordenadas.setText(
                    "📍 Ubicación recibida:\nLat: " + latitud + "\nLng: " + longitud
            );

        } else {

            txtCoordenadas.setText(
                    "📍 Aún no se obtenía la ubicación en la pantalla principal"
            );

        }


        // =========================================
        // BOTÓN VOLVER
        // =========================================

        btnVolver.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        // finish() cierra esta Activity y
                        // volvemos a la anterior (MainActivity)
                        finish();

                    }
                });

    }
}
