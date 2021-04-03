package com.java.micarro.ui.tools;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import static com.java.micarro.Constantes.TALLERES_AUTORIZADOS;

public class ToolsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ToolsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue(TALLERES_AUTORIZADOS);
    }

    public LiveData<String> getText() {
        return mText;
    }
}