package com.taku.safe.survey;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.taku.safe.R;

import java.io.IOException;
import java.io.InputStream;

public class SurveyMainActivity extends AppCompatActivity {

    private static final int SURVEY_REQUEST = 1337;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_main);
        mContext = this;

        //Nothing fancy here. Plain old simple buttons....

        Button button_survey_example_1 = (Button) findViewById(R.id.button_survey_example_1);
        Button button_survey_example_2 = (Button) findViewById(R.id.button_survey_example_2);
        Button button_survey_example_3 = (Button) findViewById(R.id.button_survey_example_3);

        button_survey_example_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i_survey = new Intent(mContext, SurveyActivity.class);
                //you have to pass as an extra the json string.
                i_survey.putExtra("json_survey", loadSurveyJson("example_survey_1.json"));
                startActivityForResult(i_survey, SURVEY_REQUEST);
            }
        });

        button_survey_example_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i_survey = new Intent(mContext, SurveyActivity.class);
                i_survey.putExtra("json_survey", loadSurveyJson("example_survey_2.json"));
                startActivityForResult(i_survey, SURVEY_REQUEST);
            }
        });

        button_survey_example_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i_survey = new Intent(mContext, SurveyActivity.class);
                i_survey.putExtra("json_survey", loadSurveyJson("example_survey_3.json"));
                startActivityForResult(i_survey, SURVEY_REQUEST);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SURVEY_REQUEST) {
            if (resultCode == RESULT_OK) {

                String answers_json = data.getExtras().getString("answers");
                Log.d("****", "****************** WE HAVE ANSWERS ******************");
                Log.v("ANSWERS JSON", answers_json);
                Log.d("****", "*****************************************************");

                //do whatever you want with them...
            }
        }
    }


    //json stored in the assets folder. but you can get it from wherever you like.
    private String loadSurveyJson(String filename) {
        try {
            InputStream is = getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


}
