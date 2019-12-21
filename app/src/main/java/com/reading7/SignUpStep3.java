package com.reading7;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SignUpStep3 extends Fragment {

    ArrayList<String> favourite_genres = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sign_up_step3_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final String[] genres = {
                 "הרפתקאות",
                 "פנטזיה",
                 "היסטוריה",
                 "אהבה",
                 "מתח",
                 "אימה",
                 "מדע",
                 "דרמה",
                 "קומדיה"}; //TODO GENRES

        for (final String genre: genres) {

            final Button button = getView().findViewWithTag(genre);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Drawable drawable = (button.getCompoundDrawables())[1];

                    if (favourite_genres.contains(genre)) {
                        favourite_genres.remove(genre);
                        drawable.setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
                        button.setTextColor(getResources().getColor(R.color.darkGrey));
                        button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.grey)));
                    } else {
                        favourite_genres.add(genre);
                        drawable.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
                        button.setTextColor(getResources().getColor(R.color.white));
                        button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                    }

                    button.setCompoundDrawables(null,drawable,null,null);
                }
            });

        }
    }

    public ArrayList<String> getFavourite_genres(){
        return favourite_genres;
    }

}
