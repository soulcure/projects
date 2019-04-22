package com.taku.safe.survey.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by colin on 2018/1/4.
 */

public class SurveyPojo implements Parcelable {

    private SurveyPropertiesBean survey_properties;

    private List<QuestionsBean> questions;

    public SurveyPropertiesBean getSurvey_properties() {
        return survey_properties;
    }

    public void setSurvey_properties(SurveyPropertiesBean survey_properties) {
        this.survey_properties = survey_properties;
    }

    public List<QuestionsBean> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionsBean> questions) {
        this.questions = questions;
    }

    public static class SurveyPropertiesBean implements Parcelable {

        private String intro_message;
        private String end_message;
        private boolean skip_intro;

        public String getIntro_message() {
            return intro_message;
        }

        public void setIntro_message(String intro_message) {
            this.intro_message = intro_message;
        }

        public String getEnd_message() {
            return end_message;
        }

        public void setEnd_message(String end_message) {
            this.end_message = end_message;
        }

        public boolean isSkip_intro() {
            return skip_intro;
        }

        public void setSkip_intro(boolean skip_intro) {
            this.skip_intro = skip_intro;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.intro_message);
            dest.writeString(this.end_message);
            dest.writeByte(this.skip_intro ? (byte) 1 : (byte) 0);
        }

        public SurveyPropertiesBean() {
        }

        protected SurveyPropertiesBean(Parcel in) {
            this.intro_message = in.readString();
            this.end_message = in.readString();
            this.skip_intro = in.readByte() != 0;
        }

        public static final Creator<SurveyPropertiesBean> CREATOR = new Creator<SurveyPropertiesBean>() {
            @Override
            public SurveyPropertiesBean createFromParcel(Parcel source) {
                return new SurveyPropertiesBean(source);
            }

            @Override
            public SurveyPropertiesBean[] newArray(int size) {
                return new SurveyPropertiesBean[size];
            }
        };
    }

    public static class QuestionsBean implements Parcelable {
        /**
         * question_type : Checkboxes
         * question_title : What were you hoping the XYZ mobile app would do?
         * description : (Select all that apply)
         * required : false
         * random_choices : false
         * choices : ["thing #1","thing #2","thing #3","thing #4"]
         * number_of_lines : 4
         */

        private String question_type;
        private String question_title;
        private String description;
        private boolean required;
        private boolean random_choices;
        private int number_of_lines;
        private List<String> choices;

        public String getQuestion_type() {
            return question_type;
        }

        public void setQuestion_type(String question_type) {
            this.question_type = question_type;
        }

        public String getQuestion_title() {
            return question_title;
        }

        public void setQuestion_title(String question_title) {
            this.question_title = question_title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        public boolean isRandom_choices() {
            return random_choices;
        }

        public void setRandom_choices(boolean random_choices) {
            this.random_choices = random_choices;
        }

        public int getNumber_of_lines() {
            return number_of_lines;
        }

        public void setNumber_of_lines(int number_of_lines) {
            this.number_of_lines = number_of_lines;
        }

        public List<String> getChoices() {
            return choices;
        }

        public void setChoices(List<String> choices) {
            this.choices = choices;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.question_type);
            dest.writeString(this.question_title);
            dest.writeString(this.description);
            dest.writeByte(this.required ? (byte) 1 : (byte) 0);
            dest.writeByte(this.random_choices ? (byte) 1 : (byte) 0);
            dest.writeInt(this.number_of_lines);
            dest.writeStringList(this.choices);
        }

        public QuestionsBean() {
        }

        protected QuestionsBean(Parcel in) {
            this.question_type = in.readString();
            this.question_title = in.readString();
            this.description = in.readString();
            this.required = in.readByte() != 0;
            this.random_choices = in.readByte() != 0;
            this.number_of_lines = in.readInt();
            this.choices = in.createStringArrayList();
        }

        public static final Creator<QuestionsBean> CREATOR = new Creator<QuestionsBean>() {
            @Override
            public QuestionsBean createFromParcel(Parcel source) {
                return new QuestionsBean(source);
            }

            @Override
            public QuestionsBean[] newArray(int size) {
                return new QuestionsBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.survey_properties, flags);
        dest.writeList(this.questions);
    }

    public SurveyPojo() {
    }

    protected SurveyPojo(Parcel in) {
        this.survey_properties = in.readParcelable(SurveyPropertiesBean.class.getClassLoader());
        this.questions = new ArrayList<>();
        in.readList(this.questions, QuestionsBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<SurveyPojo> CREATOR = new Parcelable.Creator<SurveyPojo>() {
        @Override
        public SurveyPojo createFromParcel(Parcel source) {
            return new SurveyPojo(source);
        }

        @Override
        public SurveyPojo[] newArray(int size) {
            return new SurveyPojo[size];
        }
    };
}
