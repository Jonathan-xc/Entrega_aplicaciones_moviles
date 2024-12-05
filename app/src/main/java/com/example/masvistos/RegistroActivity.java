package com.example.masvistos;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class RegistroActivity extends AppCompatActivity {

    private Button loginButton, registerButton, loginSubmitButton, registerSubmitButton, showUsersButton;
    private EditText emailLoginInput, passwordLoginInput;
    private EditText usernameRegisterInput, emailRegisterInput, passwordRegisterInput;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicializar DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Vincular componentes del layout
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        showUsersButton = findViewById(R.id.showUsersButton);

        emailLoginInput = findViewById(R.id.emailLoginInput);
        passwordLoginInput = findViewById(R.id.passwordLoginInput);
        usernameRegisterInput = findViewById(R.id.usernameRegisterInput);
        emailRegisterInput = findViewById(R.id.emailRegisterInput);
        passwordRegisterInput = findViewById(R.id.passwordRegisterInput);

        loginSubmitButton = findViewById(R.id.loginSubmitButton);
        registerSubmitButton = findViewById(R.id.registerSubmitButton);

        // Predeterminado para la opción de Login
        setLoginMode();

        // Configurar botones para alternar entre Login y Registro
        loginButton.setOnClickListener(v -> setLoginMode());
        registerButton.setOnClickListener(v -> setRegisterMode());

        // Botón para registrar un usuario
        registerSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameRegisterInput.getText().toString().trim();
                String email = emailRegisterInput.getText().toString().trim();
                String password = passwordRegisterInput.getText().toString().trim();

                if (username.isEmpty() || !isValidEmail(email) || !isValidPassword(password)) {
                    Toast.makeText(RegistroActivity.this, "Por favor, ingrese información válida.", Toast.LENGTH_SHORT).show();
                } else {
                    // Registrar el usuario en la base de datos
                    boolean isInserted = dbHelper.registerUser(username, email, password);
                    if (isInserted) {
                        Toast.makeText(RegistroActivity.this, "Registro exitoso.", Toast.LENGTH_SHORT).show();
                        setLoginMode(); // Cambiar a modo Login tras el registro
                    } else {
                        Toast.makeText(RegistroActivity.this, "Error: El correo ya está registrado.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Botón para iniciar sesión
        loginSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailLoginInput.getText().toString().trim();
                String password = passwordLoginInput.getText().toString().trim();

                if (!isValidEmail(email) || !isValidPassword(password)) {
                    Toast.makeText(RegistroActivity.this, "Credenciales inválidas.", Toast.LENGTH_SHORT).show();
                } else {
                    // Verificar el inicio de sesión en la base de datos
                    boolean isLoggedIn = dbHelper.loginUser(email, password);
                    if (isLoggedIn) {
                        Toast.makeText(RegistroActivity.this, "Inicio de sesión exitoso.", Toast.LENGTH_SHORT).show();
                        // Redirigir a MainActivity tras iniciar sesión
                        Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Finalizar RegistroActivity
                    } else {
                        Toast.makeText(RegistroActivity.this, "Credenciales incorrectas.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Botón para mostrar usuarios
        showUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener usuarios registrados
                StringBuilder userInfo = new StringBuilder();
                Cursor cursor = dbHelper.getAllUsers();
                if (cursor.getCount() == 0) {
                    userInfo.append("No hay usuarios registrados.");
                } else {
                    while (cursor.moveToNext()) {
                        userInfo.append("ID: ").append(cursor.getInt(0))
                                .append("\nNombre: ").append(cursor.getString(1))
                                .append("\nCorreo: ").append(cursor.getString(2))
                                .append("\n\n");
                    }
                }
                cursor.close();

                // Mostrar los usuarios en un cuadro de diálogo
                AlertDialog.Builder builder = new AlertDialog.Builder(RegistroActivity.this);
                builder.setTitle("Usuarios Registrados");
                builder.setMessage(userInfo.toString());
                builder.setPositiveButton("Cerrar", null);
                builder.show();
            }
        });
    }

    // Mostrar campos para Login y ocultar los de Registro
    private void setLoginMode() {
        emailLoginInput.setVisibility(View.VISIBLE);
        passwordLoginInput.setVisibility(View.VISIBLE);
        loginSubmitButton.setVisibility(View.VISIBLE); // Mostrar botón de "Iniciar sesión"

        usernameRegisterInput.setVisibility(View.GONE);
        emailRegisterInput.setVisibility(View.GONE);
        passwordRegisterInput.setVisibility(View.GONE);
        registerSubmitButton.setVisibility(View.GONE); // Ocultar botón de "Registrarse"

        loginButton.setAlpha(1f); // Resaltar el botón Login
        registerButton.setAlpha(0.5f); // Atenuar el botón Registro
    }

    // Mostrar campos para Registro y ocultar los de Login
    private void setRegisterMode() {
        emailLoginInput.setVisibility(View.GONE);
        passwordLoginInput.setVisibility(View.GONE);
        loginSubmitButton.setVisibility(View.GONE); // Ocultar botón de "Iniciar sesión"

        usernameRegisterInput.setVisibility(View.VISIBLE);
        emailRegisterInput.setVisibility(View.VISIBLE);
        passwordRegisterInput.setVisibility(View.VISIBLE);
        registerSubmitButton.setVisibility(View.VISIBLE); // Mostrar botón de "Registrarse"

        loginButton.setAlpha(0.5f); // Atenuar el botón Login
        registerButton.setAlpha(1f); // Resaltar el botón Registro
    }

    // Validar formato de email
    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    // Validar longitud de la contraseña
    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
}
