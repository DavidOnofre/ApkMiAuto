package com.java.micarro.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.java.micarro.R;

public class HomeFragment extends Fragment {

    public static final String SHARED_LOGIN_DATA = "shared_login_data";
    public static final String DATO_01 = "dato01";
    private String uid = "";

    private HomeViewModel homeViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        uid = obtenerUid();

        return root;
    }

    /**
     * Método usado para recuperar el uid del usuario logeado.
     * @return uid.
     */
    private String obtenerUid() {
        String salida = "";
        SharedPreferences prefs = this.getActivity().getSharedPreferences(SHARED_LOGIN_DATA, Context.MODE_PRIVATE);
        salida = prefs.getString(DATO_01, "");
        return  salida;
    }

}