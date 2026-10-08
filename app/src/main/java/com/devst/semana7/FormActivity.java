package com.devst.semana7;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

/*
 * =============================================
 * EXPLÍCITO 3: FormActivity → ConfirmActivity
 *              (con resultado)
 * =============================================
 *
 * 1. El usuario llena el formulario.
 * 2. Validamos los campos.
 * 3. Abrimos ConfirmActivity enviando los datos (putExtra).
 * 4. ConfirmActivity responde "confirmado" o "corregir".
 * 5. Recibimos esa respuesta con registerForActivityResult().
 */
public class FormActivity extends AppCompatActivity {

    // Claves de los extras que se envían a ConfirmActivity
    public static final String EXTRA_NOMBRE = "extra_nombre";
    public static final String EXTRA_CORREO = "extra_correo";
    public static final String EXTRA_MENSAJE = "extra_mensaje";

    EditText etNombre;
    EditText etCorreoForm;
    EditText etMensaje;
    Button btnEnviar;
    Button btnVolverForm;
    TextView txtResultado;


    // =============================================
    // LAUNCHER: RECIBE LA RESPUESTA DE ConfirmActivity
    // =============================================

    private final ActivityResultLauncher<Intent> confirmLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    resultado -> {

                        // ---- El usuario presionó "CONFIRMAR" ----
                        if (resultado.getResultCode() == RESULT_OK) {

                            String codigo = "";

                            // VALIDACIÓN: el Intent de respuesta podría ser null
                            if (resultado.getData() != null) {

                                codigo = resultado.getData()
                                        .getStringExtra(ConfirmActivity.EXTRA_CODIGO);

                            }

                            txtResultado.setText(
                                    "✅ Registro confirmado\nCódigo: " + codigo
                            );

                            limpiarFormulario();

                        // ---- El usuario presionó "CORREGIR" o volvió atrás ----
                        } else {

                            txtResultado.setText(
                                    "✏️ Registro no confirmado, puedes corregir los datos"
                            );

                        }

                    }
            );


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_form);

        etNombre = findViewById(R.id.etNombre);
        etCorreoForm = findViewById(R.id.etCorreoForm);
        etMensaje = findViewById(R.id.etMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);
        btnVolverForm = findViewById(R.id.btnVolverForm);
        txtResultado = findViewById(R.id.txtResultado);


        btnEnviar.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        enviarFormulario();

                    }
                });


        btnVolverForm.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        finish();

                    }
                });

    }


    // =============================================
    // VALIDAR Y ENVIAR
    // =============================================

    private void enviarFormulario() {

        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreoForm.getText().toString().trim();
        String mensaje = etMensaje.getText().toString().trim();


        // ---------- VALIDACIONES ----------

        if (nombre.isEmpty()) {
            etNombre.setError("El nombre es obligatorio");
            etNombre.requestFocus();
            return;
        }

        if (nombre.length() < 3) {
            etNombre.setError("Mínimo 3 caracteres");
            etNombre.requestFocus();
            return;
        }

        if (correo.isEmpty()) {
            etCorreoForm.setError("El correo es obligatorio");
            etCorreoForm.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreoForm.setError("Correo inválido");
            etCorreoForm.requestFocus();
            return;
        }

        if (mensaje.isEmpty()) {
            etMensaje.setError("Escribe un mensaje");
            etMensaje.requestFocus();
            return;
        }


        // ---------- INTENT EXPLÍCITO CON DATOS ----------

        Intent intent =
                new Intent(
                        FormActivity.this,
                        ConfirmActivity.class
                );

        intent.putExtra(EXTRA_NOMBRE, nombre);
        intent.putExtra(EXTRA_CORREO, correo);
        intent.putExtra(EXTRA_MENSAJE, mensaje);

        // Usamos el launcher (NO startActivity)
        // porque esperamos una respuesta.
        confirmLauncher.launch(intent);

    }


    // =============================================
    // LIMPIAR CAMPOS
    // =============================================

    private void limpiarFormulario() {

        etNombre.setText("");
        etCorreoForm.setText("");
        etMensaje.setText("");

        Toast.makeText(
                FormActivity.this,
                "Formulario enviado correctamente",
                Toast.LENGTH_SHORT
        ).show();

    }
}
