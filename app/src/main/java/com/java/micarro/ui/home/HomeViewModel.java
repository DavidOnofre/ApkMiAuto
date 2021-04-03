package com.java.micarro.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import static com.java.micarro.Constantes.AUTO_PRINCIPAL;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue(AUTO_PRINCIPAL);
    }

    public LiveData<String> getText() {
        return mText;
    }
}