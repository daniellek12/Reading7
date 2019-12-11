package com.reading7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.reading7.Adapters.QuestionnaireAdapter;

import java.util.ArrayList;

public class QuestionnaireActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questionnaire_activity);

        Button submit_button = findViewById(R.id.submit);
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionnaireActivity.this, LoginActivity.class);
                intent.putExtra("NEW_USER", true);
                startActivity(intent);
                finish();
            }
        });

        final String[] select_qualification = {
                "בחר ז'אנרים",
                "פרוזה מקור",
                "פרוזה תרגום",
                "מתח ופעולה",
                "רומן רומנטי",
                "ילדים",
                "פעוטות",
                "ילדי גן",
                "ראשית קריאה",
                "נוער צעיר",
                "נוער בוגר",
                "מד\"ב ופנטזיה"}; //TODO GENRES
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        ArrayList<MultiSpinner> list = new ArrayList<>();

        for (int i = 0; i < select_qualification.length; i++) {
            MultiSpinner multi_spinner = new MultiSpinner();
            multi_spinner.setTitle(select_qualification[i]);
            multi_spinner.setSelected(false);
            list.add(multi_spinner);
        }
        QuestionnaireAdapter myAdapter = new QuestionnaireAdapter(QuestionnaireActivity.this, 0,
                list);
        spinner.setAdapter(myAdapter);



//        ((Spinner)findViewById(R.id.spinner)).getSelectedItem();
    }


}
