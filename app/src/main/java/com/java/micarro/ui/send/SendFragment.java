package com.java.micarro.ui.send;

import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.java.micarro.R;

public class SendFragment extends Fragment {

    private Button button;
    private SendViewModel sendViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sendViewModel =
                ViewModelProviders.of(this).get(SendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_send, container, false);
        final TextView textView = root.findViewById(R.id.text_send);
        sendViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        asignarAlarma(root);

        return root;
    }

    private void asignarAlarma(View root) {
        button = root.findViewById(R.id.button_recordatorio_asignar_alarma);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                establecerAlarma("Recuerde realizar su mantenimiento", 6, 0);
            }
        });

    }

    private void establecerAlarma(String mensaje, int hora, int minutos) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, mensaje)
                .putExtra(AlarmClock.EXTRA_HOUR, hora)
                .putExtra(AlarmClock.EXTRA_MINUTES, minutos);

        if ((intent.resolveActivity(getActivity().getPackageManager())) != null) {
            startActivity(intent);

        }
    }

}