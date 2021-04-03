package com.java.micarro.ui.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import static com.java.micarro.Constantes.DETALLE_CONSUMOS;

public class SlideshowViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SlideshowViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue(DETALLE_CONSUMOS);
    }

    public LiveData<String> getText() {
        return mText;
    }
}