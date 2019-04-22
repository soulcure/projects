package com.ivmall.android.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivmall.android.app.R;
import com.ivmall.android.app.uitls.ScreenUtils;
import com.ivmall.android.app.views.NumberPicker;

import java.util.Random;

/**
 * @author chenqy
 *         <p>
 *         设置对话框
 */
public class AnswerQuestionDialog extends Dialog {

    private static final String TAG = AnswerQuestionDialog.class.getSimpleName();

    private Context mContext;
    private TextView tvMother;
    private TextView tvSon;
    // private NumberPicker tensPicker;
    private NumberPicker singlePicker;
    private ImageView finger;

    private int number = 0;// 作为除法结果 //[4,9) //作为除法结果

    private boolean mCanDismiss = true;
    private RightAnswerListener mListener;

    public AnswerQuestionDialog(Context context) {
        super(context, R.style.full_dialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.answer_question_dialog);
        initView();
    }

    private void initView() {
        tvMother = (TextView) findViewById(R.id.tv_mother);
        tvSon = (TextView) findViewById(R.id.tv_son);
        finger = (ImageView) findViewById(R.id.finger);
        if (ScreenUtils.isTv(getContext())) {
            finger.setVisibility(View.GONE);
        }

        // tensPicker = (NumberPicker) findViewById(R.id.tens_picker);

        singlePicker = (NumberPicker) findViewById(R.id.single_picker);
        singlePicker.setMinValue(1);
        singlePicker.setMaxValue(9);
        singlePicker.setFocusable(true);
        singlePicker.setFocusableInTouchMode(true);
        singlePicker.setOnScrollListener(mScrollListener);
        singlePicker.setValue(1);

        genQuestion();
    }

    /**
     * 生成随机题目
     */
    private void genQuestion() {

        int son = new Random().nextInt(5) + 3; // [3,9) //作为除法分子
        number = new Random().nextInt(5) + 4;  //[4,8)  //作为除法结果

        int mother = son * number; // 作为除法分母

        tvMother.setText(String.valueOf(mother));
        tvSon.setText(String.valueOf(son));

    }

    private NumberPicker.OnScrollListener mScrollListener = new NumberPicker.OnScrollListener() {

        @Override
        public void onScrollStateChange(NumberPicker picker, int scrollState) {
            if (scrollState == SCROLL_STATE_IDLE) {
                int value = picker.getValue();
                if (number == value) {
                    // right
                    if (mListener != null) {
                        mListener.doing();
                    }
                    try {
                        dismiss();
                    } catch (Exception e) {

                    }
                }
            }
        }
    };

    public void setRightAnswerListener(RightAnswerListener mListener) {
        this.mListener = mListener;
    }

    public interface RightAnswerListener {
        void doing();
    }


    public void setCanDismiss(boolean b) {
        mCanDismiss = b;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            try {
                if (mCanDismiss) {
                    dismiss();
                }
            } catch (Exception e) {

            }
        }
        return true;
    }

    @Override
    public void show() {
        // TODO Auto-generated method stub
        try {
            super.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}