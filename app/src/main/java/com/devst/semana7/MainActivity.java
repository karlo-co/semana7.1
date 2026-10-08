package com.devst.semana7;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    // Botones del proyecto base
    Button btnLinterna;
    Button btnUbicacion;
    Button btnMapa;

    // Botones de intents implícitos
    Button btnWeb;
    Button btnLlamar;
    Button btnCorreo;
    Button btnGaleria;

    // Botones de intents explícitos
    Button btnDetalle;
    Button btnConfig;
    Button btnFormulario;

    // Campos de texto
    EditText etUrl;
    EditText etTelefono;
    EditText etCorreo;

    // TextView para mostrar ubicación
    TextView txtUbicacion;

    // ImageView para mostrar la imagen elegida en la galería
    ImageView imgGaleria;

    // Variables de la linterna
    CameraManager cameraManager;
    String idCamara;
    boolean linternaEncendida = false;

    // Variables de ubicación
    LocationManager locationManager;
    double latitud = 0;
    double longitud = 0;
    boolean ubicacionObtenida = false;

    // Códigos para permisos
    final int PERMISO_UBICACION = 100;
    final int PERMISO_CAMARA = 200;


    // =============================================
    // LAUNCHER PARA LA GALERÍA (IMPLÍCITO 5)
    // =============================================

    /*
     * registerForActivityResult reemplaza al antiguo
     * startActivityForResult + onActivityResult.
     *
     * Se debe declarar como atributo (antes de onCreate).
     * Cuando la galería se cierra, Android ejecuta este
     * callback con el resultado.
     */
    private final ActivityResultLauncher<Intent> galeriaLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    resultado -> {

                        // VALIDACIÓN: el usuario pudo cancelar (volver atrás)
                        if (resultado.getResultCode() != RESULT_OK
                                || resultado.getData() == null) {

                            Toast.makeText(
                                    MainActivity.this,
                                    "No se seleccionó ninguna imagen",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        // La galería nos devuelve la URI de la imagen
                        Uri uriImagen = resultado.getData().getData();

                        // VALIDACIÓN: la URI podría venir vacía
                        if (uriImagen == null) {

                            Toast.makeText(
                                    MainActivity.this,
                                    "No se pudo leer la imagen",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        // Mostramos la imagen en el ImageView
                        imgGaleria.setImageURI(uriImagen);
                        imgGaleria.setVisibility(View.VISIBLE);

                    }
            );


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Aplicamos el modo oscuro guardado en ConfigActivity
        // (si el usuario nunca lo cambió, se usa el del sistema)
        aplicarModoGuardado();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        // =========================================
        // CONECTAR COMPONENTES DEL XML
        // =========================================

        btnLinterna = findViewById(R.id.btnLinterna);
        btnUbicacion = findViewById(R.id.btnUbicacion);
        btnMapa = findViewById(R.id.btnMapa);

        btnWeb = findViewById(R.id.btnWeb);
        btnLlamar = findViewById(R.id.btnLlamar);
        btnCorreo = findViewById(R.id.btnCorreo);
        btnGaleria = findViewById(R.id.btnGaleria);

        btnDetalle = findViewById(R.id.btnDetalle);
        btnConfig = findViewById(R.id.btnConfig);
        btnFormulario = findViewById(R.id.btnFormulario);

        etUrl = findViewById(R.id.etUrl);
        etTelefono = findViewById(R.id.etTelefono);
        etCorreo = findViewById(R.id.etCorreo);

        txtUbicacion = findViewById(R.id.txtUbicacion);
        imgGaleria = findViewById(R.id.imgGaleria);


        // =========================================
        // PREPARAR LA LINTERNA
        // =========================================

        cameraManager =
                (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {

            String[] camaras =
                    cameraManager.getCameraIdList();

            if (camaras.length > 0) {

                idCamara = camaras[0];

            }

        } catch (CameraAccessException e) {

            Toast.makeText(
                    MainActivity.this,
                    "Error al acceder a la cámara",
                    Toast.LENGTH_SHORT
            ).show();

        }


        // =========================================
        // BOTÓN LINTERNA
        // =========================================

        btnLinterna.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        // Revisamos permiso de cámara
                        if (ActivityCompat.checkSelfPermission(
                                MainActivity.this,
                                Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {

                            // Pedimos permiso
                            ActivityCompat.requestPermissions(
                                    MainActivity.this,
                                    new String[]{
                                            Manifest.permission.CAMERA
                                    },
                                    PERMISO_CAMARA
                            );

                            return;
                        }

                        cambiarLinterna();

                    }
                });


        // =========================================
        // OBTENER GEOLOCALIZACIÓN
        // =========================================

        btnUbicacion.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        obtenerUbicacion();

                    }
                });


        // =========================================
        // IMPLÍCITO 1: ABRIR UBICACIÓN EN MAPA
        // =========================================

        btnMapa.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        abrirMapa();

                    }
                });


        // =========================================
        // IMPLÍCITO 2: ABRIR PÁGINA WEB
        // =========================================

        btnWeb.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        abrirPaginaWeb();

                    }
                });


        // =========================================
        // IMPLÍCITO 3: ABRIR MARCADOR TELEFÓNICO
        // =========================================

        btnLlamar.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        abrirMarcador();

                    }
                });


        // =========================================
        // IMPLÍCITO 4: ENVIAR CORREO
        // =========================================

        btnCorreo.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        enviarCorreo();

                    }
                });


        // =========================================
        // IMPLÍCITO 5: ELEGIR IMAGEN DE GALERÍA
        // =========================================

        btnGaleria.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        abrirGaleria();

                    }
                });


        // =========================================
        // EXPLÍCITO 1: MAIN → DETALLE (CON EXTRAS)
        // =========================================

        btnDetalle.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        abrirDetalle();

                    }
                });


        // =========================================
        // EXPLÍCITO 2: MAIN → AJUSTES
        // =========================================

        btnConfig.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        /*
                         * INTENT EXPLÍCITO
                         *
                         * Indicamos exactamente la clase
                         * que queremos abrir.
                         */
                        Intent intent =
                                new Intent(
                                        MainActivity.this,
                                        ConfigActivity.class
                                );

                        startActivity(intent);

                    }
                });


        // =========================================
        // EXPLÍCITO 3: MAIN → FORMULARIO
        // (luego Form → Confirm devuelve un resultado)
        // =========================================

        btnFormulario.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        Intent intent =
                                new Intent(
                                        MainActivity.this,
                                        FormActivity.class
                                );

                        startActivity(intent);

                    }
                });

    }


    // =============================================
    // APLICAR MODO OSCURO GUARDADO
    // =============================================

    private void aplicarModoGuardado() {

        SharedPreferences preferencias =
                getSharedPreferences(ConfigActivity.PREFS, MODE_PRIVATE);

        if (preferencias.contains(ConfigActivity.KEY_MODO_OSCURO)) {

            boolean oscuro =
                    preferencias.getBoolean(ConfigActivity.KEY_MODO_OSCURO, false);

            AppCompatDelegate.setDefaultNightMode(
                    oscuro
                            ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO
            );

        }

    }


    // =============================================
    // MÉTODO AUXILIAR: LANZAR INTENT IMPLÍCITO
    // =============================================

    /*
     * Todos los intents implícitos pasan por aquí.
     *
     * VALIDACIÓN: antes de abrir, revisamos si existe
     * alguna app instalada que pueda responder.
     * Si no existe y lanzamos el intent igual,
     * la app se cerraría (ActivityNotFoundException).
     */
    private void lanzarIntentImplicito(Intent intent, String mensajeSiNoHayApp) {

        if (intent.resolveActivity(getPackageManager()) != null) {

            try {

                startActivity(intent);

            } catch (ActivityNotFoundException e) {

                // Segunda red de seguridad
                Toast.makeText(
                        MainActivity.this,
                        mensajeSiNoHayApp,
                        Toast.LENGTH_SHORT
                ).show();

            }

        } else {

            Toast.makeText(
                    MainActivity.this,
                    mensajeSiNoHayApp,
                    Toast.LENGTH_SHORT
            ).show();

        }

    }


    // =============================================
    // ENCENDER / APAGAR LINTERNA
    // =============================================

    private void cambiarLinterna() {

        if (idCamara == null) {

            Toast.makeText(
                    MainActivity.this,
                    "No se encontró una cámara con flash",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        try {

            // Cambiamos el estado
            linternaEncendida = !linternaEncendida;

            // Encendemos o apagamos
            cameraManager.setTorchMode(
                    idCamara,
                    linternaEncendida
            );


            // Cambiar texto del botón

            if (linternaEncendida) {

                btnLinterna.setText(
                        "APAGAR LINTERNA"
                );

            } else {

                btnLinterna.setText(
                        "ENCENDER LINTERNA"
                );

            }

        } catch (CameraAccessException e) {

            Toast.makeText(
                    MainActivity.this,
                    "No se pudo controlar la linterna",
                    Toast.LENGTH_SHORT
            ).show();

        }

    }


    // =============================================
    // OBTENER UBICACIÓN
    // =============================================

    private void obtenerUbicacion() {

        locationManager =
                (LocationManager)
                        getSystemService(
                                Context.LOCATION_SERVICE
                        );


        // Revisamos permiso
        if (ActivityCompat.checkSelfPermission(
                MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            // Pedimos permiso
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISO_UBICACION
            );

            return;
        }


        // Revisamos si el GPS está encendido
        if (!locationManager.isProviderEnabled(
                LocationManager.GPS_PROVIDER)) {

            Toast.makeText(
                    MainActivity.this,
                    "Debes activar el GPS",
                    Toast.LENGTH_LONG
            ).show();


            /*
             * INTENT IMPLÍCITO (extra, no se cuenta en los 5)
             *
             * Android abre la pantalla
             * de configuración correspondiente.
             */

            Intent intent =
                    new Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS
                    );

            startActivity(intent);

            return;
        }


        txtUbicacion.setText(
                "Buscando ubicación..."
        );


        // Pedimos una ubicación
        locationManager.requestLocationUpdates(

                LocationManager.GPS_PROVIDER,

                1000,

                1,

                new LocationListener() {

                    @Override
                    public void onLocationChanged(
                            @NonNull Location location) {

                        // Guardamos los datos
                        latitud =
                                location.getLatitude();

                        longitud =
                                location.getLongitude();

                        ubicacionObtenida = true;


                        // Mostramos la ubicación
                        txtUbicacion.setText(

                                "Latitud: "
                                        + latitud
                                        +
                                        "\nLongitud: "
                                        + longitud

                        );


                        Toast.makeText(
                                MainActivity.this,
                                "Ubicación obtenida",
                                Toast.LENGTH_SHORT
                        ).show();


                        // Dejamos de pedir actualizaciones
                        locationManager.removeUpdates(this);

                    }
                }
        );

    }


    // =============================================
    // IMPLÍCITO 1: ABRIR MAPA
    // =============================================

    private void abrirMapa() {

        // VALIDACIÓN: necesitamos tener ubicación
        if (!ubicacionObtenida) {

            Toast.makeText(
                    MainActivity.this,
                    "Primero obtén tu ubicación",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // Creamos la dirección para el mapa
        // Formato: geo:lat,lng?q=lat,lng(Etiqueta)
        String direccion =

                "geo:"
                        + latitud
                        + ","
                        + longitud
                        + "?q="
                        + latitud
                        + ","
                        + longitud
                        + "(Mi ubicación)";


        Uri uri =
                Uri.parse(direccion);


        /*
         * INTENT IMPLÍCITO
         *
         * No decimos qué aplicación abrir.
         *
         * Android busca una aplicación
         * capaz de mostrar mapas.
         */

        Intent intent =
                new Intent(
                        Intent.ACTION_VIEW,
                        uri
                );

        lanzarIntentImplicito(
                intent,
                "No existe una aplicación de mapas instalada"
        );

    }


    // =============================================
    // IMPLÍCITO 2: ABRIR PÁGINA WEB
    // =============================================

    private void abrirPaginaWeb() {

        String url = etUrl.getText().toString().trim();

        // VALIDACIÓN 1: campo vacío
        if (url.isEmpty()) {

            etUrl.setError("Ingresa una dirección web");
            return;
        }

        // Si el usuario escribió "www.inacap.cl" le agregamos https://
        if (!url.startsWith("http://") && !url.startsWith("https://")) {

            url = "https://" + url;
        }

        // VALIDACIÓN 2: formato de URL válido
        if (!Patterns.WEB_URL.matcher(url).matches()) {

            etUrl.setError("La dirección no es válida");
            return;
        }

        /*
         * INTENT IMPLÍCITO
         *
         * ACTION_VIEW + https://  →  Android abre un navegador.
         */
        Intent intent =
                new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(url)
                );

        lanzarIntentImplicito(
                intent,
                "No hay un navegador instalado"
        );

    }


    // =============================================
    // IMPLÍCITO 3: ABRIR MARCADOR TELEFÓNICO
    // =============================================

    private void abrirMarcador() {

        String telefono = etTelefono.getText().toString().trim();

        // VALIDACIÓN 1: campo vacío
        if (telefono.isEmpty()) {

            etTelefono.setError("Ingresa un número de teléfono");
            return;
        }

        // VALIDACIÓN 2: solo números (con + opcional), entre 8 y 12 dígitos
        if (!telefono.matches("^\\+?[0-9]{8,12}$")) {

            etTelefono.setError("Número inválido (ej: +56912345678)");
            return;
        }

        /*
         * INTENT IMPLÍCITO
         *
         * ACTION_DIAL solo MUESTRA el número en el marcador.
         * El usuario decide si llama o no.
         *
         * Por eso NO necesita el permiso CALL_PHONE
         * (ese permiso es para ACTION_CALL, que llama directo).
         */
        Intent intent =
                new Intent(
                        Intent.ACTION_DIAL,
                        Uri.parse("tel:" + telefono)
                );

        lanzarIntentImplicito(
                intent,
                "No hay una aplicación de teléfono disponible"
        );

    }


    // =============================================
    // IMPLÍCITO 4: ENVIAR CORREO
    // =============================================

    private void enviarCorreo() {

        String correo = etCorreo.getText().toString().trim();

        // VALIDACIÓN 1: campo vacío
        if (correo.isEmpty()) {

            etCorreo.setError("Ingresa un correo");
            return;
        }

        // VALIDACIÓN 2: formato de correo
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {

            etCorreo.setError("Correo inválido");
            return;
        }

        /*
         * INTENT IMPLÍCITO
         *
         * ACTION_SENDTO + "mailto:" hace que SOLO
         * aparezcan apps de correo (no WhatsApp, etc).
         *
         * Con putExtra prellenamos destinatario,
         * asunto y cuerpo.
         */
        Intent intent =
                new Intent(Intent.ACTION_SENDTO);

        intent.setData(Uri.parse("mailto:"));

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{correo});

        intent.putExtra(Intent.EXTRA_SUBJECT, "Consulta desde App Android");

        intent.putExtra(
                Intent.EXTRA_TEXT,
                "Hola,\n\nEste correo fue generado desde el Prototipo 2.\n\nSaludos."
        );

        lanzarIntentImplicito(
                intent,
                "No hay una aplicación de correo instalada"
        );

    }


    // =============================================
    // IMPLÍCITO 5: ELEGIR IMAGEN DE GALERÍA
    // =============================================

    private void abrirGaleria() {

        /*
         * INTENT IMPLÍCITO
         *
         * ACTION_GET_CONTENT + tipo "image/*"
         * Android muestra las apps que entregan imágenes
         * (Galería, Google Fotos, Archivos...).
         *
         * Usamos el launcher (no startActivity) porque
         * necesitamos que nos DEVUELVA la imagen elegida.
         */
        Intent intent =
                new Intent(Intent.ACTION_GET_CONTENT);

        intent.setType("image/*");

        // VALIDACIÓN: ¿hay alguna app que entregue imágenes?
        if (intent.resolveActivity(getPackageManager()) != null) {

            galeriaLauncher.launch(intent);

        } else {

            Toast.makeText(
                    MainActivity.this,
                    "No hay una aplicación de galería disponible",
                    Toast.LENGTH_SHORT
            ).show();

        }

    }


    // =============================================
    // EXPLÍCITO 1: ABRIR DETALLE CON EXTRAS
    // =============================================

    private void abrirDetalle() {

        /*
         * INTENT EXPLÍCITO
         *
         * Con putExtra "metemos" datos dentro del Intent.
         * Cada dato tiene una clave (KEY) y un valor.
         *
         * Las claves están como constantes en DetalleActivity
         * para no escribirlas mal en las dos pantallas.
         */
        Intent intent =
                new Intent(
                        MainActivity.this,
                        DetalleActivity.class
                );

        intent.putExtra(DetalleActivity.EXTRA_TITULO, "Prototipo 2 - Intents");

        intent.putExtra(
                DetalleActivity.EXTRA_DESCRIPCION,
                "Pantalla abierta con un Intent explícito que transporta datos con putExtra."
        );

        // También enviamos números (double y boolean)
        intent.putExtra(DetalleActivity.EXTRA_TIENE_UBICACION, ubicacionObtenida);
        intent.putExtra(DetalleActivity.EXTRA_LATITUD, latitud);
        intent.putExtra(DetalleActivity.EXTRA_LONGITUD, longitud);

        startActivity(intent);

    }


    // =============================================
    // RESPUESTA DE LOS PERMISOS
    // =============================================

    @Override
    public void onRequestPermissionsResult(

            int requestCode,

            @NonNull String[] permissions,

            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );


        // =========================================
        // PERMISO DE UBICACIÓN
        // =========================================

        if (requestCode == PERMISO_UBICACION) {

            if (grantResults.length > 0
                    &&
                    grantResults[0]
                            == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(
                        MainActivity.this,
                        "Permiso de ubicación aceptado",
                        Toast.LENGTH_SHORT
                ).show();

                obtenerUbicacion();

            } else {

                Toast.makeText(
                        MainActivity.this,
                        "Permiso de ubicación rechazado",
                        Toast.LENGTH_SHORT
                ).show();

            }

        }


        // =========================================
        // PERMISO DE CÁMARA
        // =========================================

        if (requestCode == PERMISO_CAMARA) {

            if (grantResults.length > 0
                    &&
                    grantResults[0]
                            == PackageManager.PERMISSION_GRANTED) {

                cambiarLinterna();

            } else {

                Toast.makeText(
                        MainActivity.this,
                        "Permiso de cámara rechazado",
                        Toast.LENGTH_SHORT
                ).show();

            }

        }

    }

}
