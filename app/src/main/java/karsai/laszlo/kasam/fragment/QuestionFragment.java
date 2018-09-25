package karsai.laszlo.kasam.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import karsai.laszlo.kasam.CustomViewPager;
import karsai.laszlo.kasam.KasamApplication;
import karsai.laszlo.kasam.R;
import karsai.laszlo.kasam.model.Option;
import karsai.laszlo.kasam.utils.ApplicationUtils;

public class QuestionFragment extends Fragment {

    private int mWidth;
    private int mHeight;
    private float mChooserStartCoordinateX = -1;
    private float mChooserStartCoordinateY = -1;
    private int mChooserItemRadius;
    private int mOptionItemRadius;
    private int mChooserItemDiameter;
    private int mOptionItemDiameter;
    private int mOptionItemInnerPadding;
    private int mClosestIndex;
    private ImageView mChooserImageView;
    private View.OnTouchListener mOnTouchListener;
    private Activity mActivity;
    private String mName;
    private String mQuestion;
    private String mAnswerNoteSeven;
    private String mAnswerNoteOne;
    private int mPosition;
    private float[] mChooserItemStartingCoordinates;
    private Context mContext;

    private FrameLayout mFrameLayout;
    private TextView mInfoTextView;
    private CustomViewPager mViewPager;
    private TextView mQuestionTextView;
    private View mFirstView;
    private View mSeventhView;
    private TextView mAnswerNoteOneTextView;
    private TextView mAnswerNoteSevenTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_question, container, false);
        mFrameLayout = rootView.findViewById(R.id.fl_choosing);
        mInfoTextView = rootView.findViewById(R.id.tv_info);
        mQuestionTextView = rootView.findViewById(R.id.tv_question);
        mFirstView = rootView.findViewById(R.id.v_first);
        mSeventhView = rootView.findViewById(R.id.v_seventh);
        mAnswerNoteOneTextView = rootView.findViewById(R.id.tv_answer_one_note);
        mAnswerNoteSevenTextView = rootView.findViewById(R.id.tv_answer_seven_note);

        mActivity = getActivity();
        mContext = getContext();
        if (mContext == null && isActivityAlive()) {
            mContext = ((KasamApplication) mActivity.getApplication()).getContext();
        }

        Bundle bundle = getArguments();
        if (bundle != null) {
            mName = bundle.getString(ApplicationUtils.NAME);
            mQuestion = bundle.getString(ApplicationUtils.QUESTION);
            mAnswerNoteOne = bundle.getString(ApplicationUtils.ANSWER_NOTE_ONE);
            mAnswerNoteSeven = bundle.getString(ApplicationUtils.ANSWER_NOTE_SEVEN);
            mPosition = bundle.getInt(ApplicationUtils.POSITION, -1);
            if (mName != null
                    && mQuestion != null
                    && mAnswerNoteOne != null
                    && mAnswerNoteSeven != null
                    && mPosition != -1) {
                mQuestionTextView.setText(mQuestion);
                mAnswerNoteOneTextView.setText(mAnswerNoteOne);
                mAnswerNoteSevenTextView.setText(mAnswerNoteSeven);
            } else {
                ApplicationUtils.exit(mContext);
            }
        } else {
            ApplicationUtils.exit(mContext);
        }

        initChooserLayout();

        mOnTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        if (isActivityAlive()) {
                            setViewPagerSwipingMode(false);
                        }
                        mChooserStartCoordinateX = view.getX() - motionEvent.getRawX();
                        mChooserStartCoordinateY = view.getY() - motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float x = motionEvent.getRawX() + mChooserStartCoordinateX;
                        float y = motionEvent.getRawY() + mChooserStartCoordinateY;
                        if (x < 0 || x > mWidth - mChooserItemDiameter
                                || y < 0 || y > mHeight - mChooserItemDiameter) {
                            return true;
                        }
                        mClosestIndex = getClosestOptionIndex(x, y);
                        if (mClosestIndex != -1) {
                            mInfoTextView.setText(String.valueOf(mClosestIndex));
                        }
                        view.animate()
                                .x(x)
                                .y(y)
                                .setDuration(0)
                                .start();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isActivityAlive()) {
                            setViewPagerSwipingMode(true);
                        }
                        if (mClosestIndex != -1) {
                            putChooserItemOntoOptionIndex();
                            registerChosenValue();
                            loadNextPage();
                        }
                        break;
                    default:
                        return false;
                }
                return true;
            }
        };

        return rootView;
    }

    private void initChooserLayout() {
        mFrameLayout.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        mWidth = mFrameLayout.getMeasuredWidth();
                        mHeight = mFrameLayout.getMeasuredHeight();
                        mChooserItemRadius = (int) Math.hypot(mWidth, mHeight) / 15;
                        mOptionItemRadius = (int) Math.hypot(mWidth, mHeight) / 25;
                        mOptionItemInnerPadding = (int) Math.hypot(mWidth, mHeight) / 10;
                        mChooserItemDiameter = 2 * mChooserItemRadius;
                        mOptionItemDiameter = 2 * mOptionItemRadius;
                        mFirstView.setLayoutParams(getNoteBackgroundParams());
                        mSeventhView.setLayoutParams(getNoteBackgroundParams());
                        addOptionItems();
                        applyStoredValueAction();
                        addChooserItem(mChooserItemStartingCoordinates[0], mChooserItemStartingCoordinates[1]);
                        mFrameLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                        return true;
                    }
                });
    }

    private void applyStoredValueAction() {
        mChooserItemStartingCoordinates = new float[2];
        mChooserItemStartingCoordinates[0] = mWidth / 2 - mChooserItemRadius;
        mChooserItemStartingCoordinates[1] = mHeight / 2 - mChooserItemRadius;
        if (isActivityAlive()) {
            int storedValue = ApplicationUtils.getValueFromPrefs(
                    mActivity.getApplicationContext(),
                    mName,
                    mPosition
            );
            if (storedValue != -1) {
                double angle = getAngle(storedValue);
                mChooserItemStartingCoordinates[0] = (float) (mWidth/ 2 - mChooserItemRadius
                        + Math.cos(angle) * (mChooserItemRadius + mOptionItemInnerPadding + mOptionItemRadius));
                mChooserItemStartingCoordinates[1] = (float) (mHeight / 2 - mChooserItemRadius
                        + Math.sin(angle) * (mChooserItemRadius + mOptionItemInnerPadding + mOptionItemRadius));
                mInfoTextView.setText(String.valueOf(storedValue));
            }
        }
    }

    private double getAngle(int index) {
        return Math.PI / 2F + (index * ((2 * Math.PI) / 8));
    }

    private void registerChosenValue() {
        if (isActivityAlive()) {
            ApplicationUtils.saveValueToPrefs(
                    mActivity.getApplicationContext(),
                    mName,
                    mPosition,
                    mClosestIndex
            );
            ApplicationUtils.updateProgressStatus(
                    (TextView) mActivity.findViewById(R.id.tv_process_status),
                    mActivity.findViewById(R.id.v_container),
                    mActivity.findViewById(R.id.v_content),
                    (Button) mActivity.findViewById(R.id.btn_show_result),
                    mActivity.getApplicationContext(),
                    mName
            );
            final TextView missingSequenceTextView = mActivity.findViewById(R.id.tv_missing);
            if (mPosition == ApplicationUtils.COUNT - 1
                    || missingSequenceTextView.getVisibility() == View.VISIBLE) {
                ApplicationUtils.showNotAnsweredStatementsIfAny(
                        mActivity,
                        missingSequenceTextView,
                        mName
                );
            }
            if (mPosition == ApplicationUtils.COUNT - 1) {
                ApplicationUtils.saveUserWentThroughValueToPrefs(
                        mActivity.getApplicationContext(),
                        mName
                );
            }
        }
    }

    private void loadNextPage() {
        if (mViewPager == null) {
            mViewPager = mActivity.findViewById(R.id.viewpager_questions);
        }
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
    }

    private void addOptionItems() {
        for (int i = 1; i < 8; i++) {
            Option option = new Option(i);
            FrameLayout frameLayout = (FrameLayout) LayoutInflater.from(mContext)
                    .inflate(R.layout.option, (ViewGroup) null);
            ImageView imageView = frameLayout.findViewById(R.id.iv_option);
            FrameLayout.LayoutParams imageViewParams = new FrameLayout.LayoutParams(mOptionItemDiameter, mOptionItemDiameter);
            imageViewParams.gravity = Gravity.CENTER;
            imageView.setLayoutParams(imageViewParams);
            final int currIndex = i;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClosestIndex = currIndex;
                    mInfoTextView.setText(String.valueOf(mClosestIndex));
                    putChooserItemOntoOptionIndex();
                    registerChosenValue();
                    loadNextPage();
                }
            });
            TextView textView = frameLayout.findViewById(R.id.tv_option);
            textView.setText(option.getText());
            double angle = getAngle(i);
            mFrameLayout.addView(
                    frameLayout,
                    getItemParams(
                            mChooserItemDiameter,
                            mWidth/ 2 - mChooserItemRadius + Math.cos(angle) * (mChooserItemRadius + mOptionItemInnerPadding + mOptionItemRadius),
                            mHeight / 2 - mChooserItemRadius + Math.sin(angle) * (mChooserItemRadius + mOptionItemInnerPadding + mOptionItemRadius)
                    )
            );
        }
    }

    private FrameLayout.LayoutParams getNoteBackgroundParams() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                mOptionItemDiameter,
                mOptionItemDiameter
        );
        layoutParams.gravity = Gravity.CENTER;
        return layoutParams;
    }

    private int getClosestOptionIndex(float x, float y) {
        int index = -1;
        double distance = mWidth > mHeight ? mWidth : mHeight;
        for (int i = 1; i < 8; i++) {
            double angle = getAngle(i);
            double optionCoordinateX = mWidth/ 2 - mChooserItemRadius + Math.cos(angle) * (
                    mChooserItemRadius + mOptionItemInnerPadding + mOptionItemRadius);
            double optionCoordinateY = mHeight / 2 - mChooserItemRadius + Math.sin(angle) * (
                    mChooserItemRadius + mOptionItemInnerPadding + mOptionItemRadius);
            double currDistance = Math.hypot(x - optionCoordinateX, y - optionCoordinateY);
            if (currDistance < distance) {
                distance = currDistance;
                index = i;
            }
        }
        return index;
    }

    private void putChooserItemOntoOptionIndex() {
        mFrameLayout.removeView(mChooserImageView);
        double angle = getAngle(mClosestIndex);
        double optionCoordinateX = mWidth/ 2 - mChooserItemRadius + Math.cos(angle) * (
                mChooserItemRadius + mOptionItemInnerPadding + mOptionItemRadius);
        double optionCoordinateY = mHeight / 2 - mChooserItemRadius + Math.sin(angle) * (
                mChooserItemRadius + mOptionItemInnerPadding + mOptionItemRadius);
        addChooserItem((float) optionCoordinateX, (float) optionCoordinateY);
    }

    private void addChooserItem(float x, float y) {
        mChooserImageView = new ImageView(mContext);
        mChooserImageView.setBackground(mContext.getResources().getDrawable(R.drawable.chooser_item_background));
        mChooserImageView.setElevation(
                mContext.getResources().getDimensionPixelSize(R.dimen.chooser_elevation)
                        / mContext.getResources().getDisplayMetrics().scaledDensity
        );
        mFrameLayout.addView(
                mChooserImageView,
                getItemParams(
                        mChooserItemDiameter,
                        x,
                        y
                )
        );
        mChooserImageView.setOnTouchListener(mOnTouchListener);
    }

    private FrameLayout.LayoutParams getItemParams(int diameter, double x, double y) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                diameter,
                diameter
        );
        layoutParams.setMargins(
                (int)x,
                (int)y,
                0,
                0
        );
        return layoutParams;
    }

    private boolean isActivityAlive() {
        return mActivity != null;
    }

    private void setViewPagerSwipingMode(boolean isAllowed) {
        if (mViewPager == null) {
            mViewPager = mActivity.findViewById(R.id.viewpager_questions);
        }
        mViewPager.setIsSwipingAllowed(isAllowed);
    }
}
