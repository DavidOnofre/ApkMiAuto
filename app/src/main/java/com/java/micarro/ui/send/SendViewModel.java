package com.java.micarro.ui.send;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import static com.java.micarro.Constantes.RECORDATORIOS;

public class SendViewModel extends ViewModel {


    private MutableLiveData<String> mText;

    public SendViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue(RECORDATORIOS);
    }

    public LiveData<String> getText() {
        return mText;
    }
}