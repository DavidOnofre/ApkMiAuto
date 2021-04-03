package com.java.micarro.ui.share;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import static com.java.micarro.Constantes.GASTOS;

public class ShareViewModel extends ViewModel {


    private MutableLiveData<String> mText;

    public ShareViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue(GASTOS);
    }

    public LiveData<String> getText() {
        return mText;
    }
}