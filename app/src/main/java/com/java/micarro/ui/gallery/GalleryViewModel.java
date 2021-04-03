package com.java.micarro.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import static com.java.micarro.Constantes.ESPECIFICACIONES;

public class GalleryViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public GalleryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue(ESPECIFICACIONES);
    }

    public LiveData<String> getText() {
        return mText;
    }
}