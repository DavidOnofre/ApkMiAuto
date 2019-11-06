package com.java.micarro;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    EditText txtUsuario;
    EditText txtContrasena;
    TextInputLayout impUsuario;
    TextInputLayout impContrasena;
    Button btnIniciar;
    Boolean pass = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtUsuario = (EditText)findViewById(R.id.txtUsuario);
        txtContrasena = (EditText)findViewById(R.id.txtContrasena);

        impUsuario = (TextInputLayout)findViewById(R.id.impUsuario);
        impContrasena = (TextInputLayout)findViewById(R.id.impContrasena);

        btnIniciar = (Button)findViewById(R.id.btnIniciar);

        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Pattern p = Pattern.compile("[0-9][0-9][0-9][0-9][0-9]");
                if(p.matcher(txtContrasena.getText().toString()).matches() == false) {
                    impContrasena.setError("Longitud de contrasena 5 dígitos");
                    pass = false;
                }else {
                    pass = true;
                    impContrasena.setError(null);
                }

                if(pass == true){
                   String usuario = txtUsuario.getText().toString();
                   String clave = txtContrasena.getText().toString();

                   if(usuario.equals("admin") && clave.equals("74123")){

                     // Intent i = new Intent(getApplicationContext(), Principal.class);
                     // startActivity(i);

                       Intent i = new Intent(getApplicationContext(), MenuLateralActivity.class);
                       startActivity(i);


                   }else {
                       Toast.makeText(getApplicationContext(),"Usuario o Contraseña Incorrectos",Toast.LENGTH_SHORT).show();
                   }

                }

            }
        });

    }

}
