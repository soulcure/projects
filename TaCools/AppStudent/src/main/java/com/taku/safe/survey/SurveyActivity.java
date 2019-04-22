package com.taku.safe.survey;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.taku.safe.R;
import com.taku.safe.survey.fragment.FragmentCheckboxes;
import com.taku.safe.survey.fragment.FragmentEnd;
import com.taku.safe.survey.fragment.FragmentMultiline;
import com.taku.safe.survey.fragment.FragmentNumber;
import com.taku.safe.survey.fragment.FragmentRadioboxes;
import com.taku.safe.survey.fragment.FragmentStart;
import com.taku.safe.survey.fragment.FragmentTextSimple;
import com.taku.safe.survey.model.SurveyPojo;

import java.util.ArrayList;

public class SurveyActivity extends AppCompatActivity {

    private SurveyPojo mSurveyPojo;
    private ViewPager mPager;
    private String style_string = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);


        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            mSurveyPojo = new Gson().fromJson(bundle.getString("json_survey"), SurveyPojo.class);
            if (bundle.containsKey("style")) {
                style_string = bundle.getString("style");
            }
        }


        Log.i("json Object = ", String.valueOf(mSurveyPojo.getQuestions()));

        final ArrayList<Fragment> arraylist_fragments = new ArrayList<>();

        //- START -
        if (!mSurveyPojo.getSurvey_properties().isSkip_intro()) {
            FragmentStart frag_start = new FragmentStart();
            Bundle sBundle = new Bundle();
            sBundle.putParcelable("survery_properties", mSurveyPojo.getSurvey_properties());
            sBundle.putString("style", style_string);
            frag_start.setArguments(sBundle);
            arraylist_fragments.add(frag_start);
        }

        //- FILL -
        for (SurveyPojo.QuestionsBean mQuestion : mSurveyPojo.getQuestions()) {

            if (mQuestion.getQuestion_type().equals("String")) {
                FragmentTextSimple frag = new FragmentTextSimple();
                Bundle xBundle = new Bundle();
                xBundle.putParcelable("data", mQuestion);
                xBundle.putString("style", style_string);
                frag.setArguments(xBundle);
                arraylist_fragments.add(frag);
            }

            if (mQuestion.getQuestion_type().equals("Checkboxes")) {
                FragmentCheckboxes frag = new FragmentCheckboxes();
                Bundle xBundle = new Bundle();
                xBundle.putParcelable("data", mQuestion);
                xBundle.putString("style", style_string);
                frag.setArguments(xBundle);
                arraylist_fragments.add(frag);
            }

            if (mQuestion.getQuestion_type().equals("Radioboxes")) {
                FragmentRadioboxes frag = new FragmentRadioboxes();
                Bundle xBundle = new Bundle();
                xBundle.putParcelable("data", mQuestion);
                xBundle.putString("style", style_string);
                frag.setArguments(xBundle);
                arraylist_fragments.add(frag);
            }

            if (mQuestion.getQuestion_type().equals("Number")) {
                FragmentNumber frag = new FragmentNumber();
                Bundle xBundle = new Bundle();
                xBundle.putParcelable("data", mQuestion);
                xBundle.putString("style", style_string);
                frag.setArguments(xBundle);
                arraylist_fragments.add(frag);
            }

            if (mQuestion.getQuestion_type().equals("StringMultiline")) {
                FragmentMultiline frag = new FragmentMultiline();
                Bundle xBundle = new Bundle();
                xBundle.putParcelable("data", mQuestion);
                xBundle.putString("style", style_string);
                frag.setArguments(xBundle);
                arraylist_fragments.add(frag);
            }

        }

        //- END -
        FragmentEnd frag_end = new FragmentEnd();
        Bundle eBundle = new Bundle();
        eBundle.putParcelable("survery_properties", mSurveyPojo.getSurvey_properties());
        eBundle.putString("style", style_string);
        frag_end.setArguments(eBundle);
        arraylist_fragments.add(frag_end);


        mPager = (ViewPager) findViewById(R.id.pager);
        AdapterFragmentQ mPagerAdapter = new AdapterFragmentQ(getSupportFragmentManager(), arraylist_fragments);
        mPager.setAdapter(mPagerAdapter);


    }

    public void go_to_next() {
        mPager.setCurrentItem(mPager.getCurrentItem() + 1);
    }


    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    public void event_survey_completed(Answers instance) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("answers", instance.get_json_object());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }


    private class AdapterFragmentQ extends FragmentPagerAdapter {
        private final ArrayList<Fragment> fragments;

        public AdapterFragmentQ(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }


        @Override
        public int getCount() {
            return this.fragments.size();
        }
    }
}
