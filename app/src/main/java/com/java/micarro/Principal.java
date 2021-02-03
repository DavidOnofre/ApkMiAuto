package com.java.micarro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Principal extends AppCompatActivity {

    FloatingActionButton fab1;
    FloatingActionButton fab2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        fab1 = (FloatingActionButton) findViewById(R.id.fabBtn1);
        fab2 = (FloatingActionButton) findViewById(R.id.fabBtn2);

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), IngresarAuto.class);
                startActivity(i);
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), IngresarPersona.class);
                startActivity(i);
            }
        });

    }
}