package com.devst.semana7;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

/*
 * =============================================
 * EXPLÍCITO 2: MainActivity → ConfigActivity
 * =============================================
 *
 * Simula una pantalla de ajustes interna.
 * Usa una Toolbar con el botón "Atrás" (flecha ←).
 *
 * Los ajustes se guardan en SharedPreferences
 * para que no se pierdan al cerrar la app.
 */
public class ConfigActivity extends AppCompatActivity {

    // Nombre del archivo de preferencias y sus claves
    public static final String PREFS = "ajustes_app";
    public static final String KEY_NOTIFICACIONES = "notificaciones";
    public static final String KEY_MODO_OSCURO = "modo_oscuro";

    Toolbar toolbar;
    SwitchCompat swNotificaciones;
    SwitchCompat swModoOscuro;
    SharedPreferences preferencias;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_config);


        // =========================================
        // TOOLBAR CON BOTÓN ATRÁS
        // =========================================

        toolbar = findViewById(R.id.toolbar);

        // Convertimos la Toolbar en la "barra superior" de la Activity
        setSupportActionBar(toolbar);

        // VALIDACIÓN: getSupportActionBar() podría ser null
        if (getSupportActionBar() != null) {

            getSupportActionBar().setTitle("⚙️ Ajustes");

            // Muestra la flecha ← en la Toolbar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }


        // =========================================
        // CARGAR AJUSTES GUARDADOS
        // =========================================

        preferencias = getSharedPreferences(PREFS, MODE_PRIVATE);

        swNotificaciones = findViewById(R.id.swNotificaciones);
        swModoOscuro = findViewById(R.id.swModoOscuro);

        swNotificaciones.setChecked(
                preferencias.getBoolean(KEY_NOTIFICACIONES, true)
        );

        swModoOscuro.setChecked(
                preferencias.getBoolean(KEY_MODO_OSCURO, false)
        );


        // =========================================
        // GUARDAR CAMBIOS
        // =========================================

        swNotificaciones.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton boton, boolean activado) {

                        preferencias.edit()
                                .putBoolean(KEY_NOTIFICACIONES, activado)
                                .apply();

                        Toast.makeText(
                                ConfigActivity.this,
                                activado ? "Notificaciones activadas" : "Notificaciones desactivadas",
                                Toast.LENGTH_SHORT
                        ).show();

                    }
                });

        swModoOscuro.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton boton, boolean activado) {

                        preferencias.edit()
                                .putBoolean(KEY_MODO_OSCURO, activado)
                                .apply();

                        // Cambia el tema de TODA la app (claro / oscuro)
                        AppCompatDelegate.setDefaultNightMode(
                                activado
                                        ? AppCompatDelegate.MODE_NIGHT_YES
                                        : AppCompatDelegate.MODE_NIGHT_NO
                        );

                    }
                });

    }


    // =============================================
    // FLECHA ← DE LA TOOLBAR
    // =============================================

    /*
     * Se ejecuta al tocar la flecha "Atrás" de la Toolbar.
     * finish() cierra esta pantalla y volvemos a MainActivity.
     */
    @Override
    public boolean onSupportNavigateUp() {

        finish();

        return true;
    }
}
