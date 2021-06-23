package com.java.micarro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LogoActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonIngresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        inicializarVariables();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_Logo_Ingresar:
                ingresar();
                break;
        }
    }

    /**
     * Método usado para ingresar al menu principal de la apk
     */
    private void ingresar() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

    /**
     * Método usado para inicializar variables
     */
    private void inicializarVariables() {
        buttonIngresar = findViewById(R.id.button_Logo_Ingresar);
        buttonIngresar.setOnClickListener(this);
    }
}