package com.java.micarro.ui.slideshow;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.java.micarro.R;

public class SlideshowFragment extends Fragment implements View.OnClickListener {

    private SlideshowViewModel slideshowViewModel;

    private ProgressBar progressBar;
    private Button button;
    private TextView textViewAceite;
    private Handler handler;
    private Boolean activo;
    private int contador;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel = ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);

        slideshowViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        inicializarVariables(root);
        button.setOnClickListener(this);

        return root;
    }

    private void inicializarVariables(View root) {
        handler = new Handler();
        progressBar = (ProgressBar) root.findViewById(R.id.progressBar_consumibles_aceite);
        button = (Button) root.findViewById(R.id.button_consumibles_aceite);
        textViewAceite = (TextView) root.findViewById(R.id.textView_consumibles_aceite);
        activo = false;
        contador = 0;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_consumibles_aceite) {

            if (!activo) {
                Thread hilo = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (contador <= 100) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    textViewAceite.setText("Aceite: " + contador + " %");
                                    progressBar.setProgress(contador);
                                }
                            });
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            contador++;
                            activo = true;
                        }
                    }
                });
                hilo.start();
            }
        }
    }
}





















