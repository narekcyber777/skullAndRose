package com.example.skullandroses.helper;

import android.view.View;

public class AnimationHelper {

    public void basicSet(View view) {
        view.setTranslationY(-1500);
        view.animate()
                .translationYBy(1500)
                .setDuration(300);

    }

    public void basicSetGroup(View view) {

        view.animate().
                rotationYBy(360).
                setDuration(1000);
    }


}
