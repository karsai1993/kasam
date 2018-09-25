package karsai.laszlo.kasam.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import karsai.laszlo.kasam.CustomViewPager;
import karsai.laszlo.kasam.R;
import karsai.laszlo.kasam.adapter.QuestionsPagerAdapter;
import karsai.laszlo.kasam.model.Answer;
import karsai.laszlo.kasam.utils.ApplicationUtils;

public class QuestionsActivity extends AppCompatActivity {

    @BindView(R.id.tab_questions)
    TabLayout mTabLayout;
    @BindView(R.id.viewpager_questions)
    CustomViewPager mViewPager;
    @BindView(R.id.tv_process_status)
    TextView mProcessStatusTextView;
    @BindView(R.id.v_container)
    View mContainerView;
    @BindView(R.id.v_content)
    View mContentView;
    @BindView(R.id.btn_show_result)
    Button mShowResultBtn;
    @BindView(R.id.pb_questions)
    ProgressBar mProgressBar;
    @BindView(R.id.tv_missing)
    TextView mMissingSequencesTextView;

    private QuestionsPagerAdapter mQuestionsPagerAdapter;
    private List<Answer> mAnswerList;
    private int[] mResultScoreArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        ButterKnife.bind(this);

        Intent receivedData = getIntent();
        if (receivedData != null) {
            final String name = receivedData.getStringExtra(Intent.EXTRA_TEXT);
            if (name == null) {
                ApplicationUtils.exit(this);
            } else {
                ActionBar actionBar = getSupportActionBar();
                if (actionBar == null) {
                    ApplicationUtils.exit(this);
                } else {
                    actionBar.setTitle(
                            new StringBuilder()
                                    .append(name)
                                    .append(getResources().getString(R.string.statements_title))
                                    .toString()
                    );
                    ApplicationUtils.updateProgressStatus(
                            mProcessStatusTextView,
                            mContainerView,
                            mContentView,
                            mShowResultBtn,
                            this,
                            name);
                    boolean hasUserWentThrough = ApplicationUtils
                            .getUserWentThroughValueFromPrefs(this, name);
                    if (hasUserWentThrough) {
                        ApplicationUtils.showNotAnsweredStatementsIfAny(
                                this,
                                mMissingSequencesTextView,
                                name
                        );
                    }
                    mQuestionsPagerAdapter = new QuestionsPagerAdapter(
                            getSupportFragmentManager(),
                            this,
                            name
                    );
                    mViewPager.setAdapter(mQuestionsPagerAdapter);
                    mViewPager.setIsSwipingAllowed(true);
                    mTabLayout.setupWithViewPager(mViewPager);

                    final Snackbar snackbar = Snackbar.make(
                            findViewById(android.R.id.content),
                            getResources().getString(R.string.instruction),
                            Snackbar.LENGTH_INDEFINITE
                    );
                    snackbar.setAction(
                            getResources().getString(R.string.dismiss),
                            new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();

                    mShowResultBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            analyzeResults(QuestionsActivity.this, name);
                            Intent showResultIntent
                                    = new Intent(QuestionsActivity.this, ResultActivity.class);
                            showResultIntent.putExtra(
                                    ApplicationUtils.INTENT_RESULT_SCORES, mResultScoreArray
                            );
                            showResultIntent.putParcelableArrayListExtra(
                                    ApplicationUtils.INTENT_ANSWER_LIST,
                                    (ArrayList<? extends Parcelable>) mAnswerList
                            );
                            showResultIntent.putExtra(ApplicationUtils.INTENT_NAME, name);
                            startActivity(showResultIntent);
                        }
                    });
                }
            }
        } else {
            ApplicationUtils.exit(this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_show_about) {
            startActivity(new Intent(QuestionsActivity.this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.questions_menu, menu);
        return true;
    }

    private void analyzeResults(Context context, String name) {
        mResultScoreArray = new int[4];
        mAnswerList = new ArrayList<>();
        List<Integer> sequenceListM = Arrays.asList(4, 7, 8, 11, 14, 16, 22, 28);
        List<Integer> sequenceListB = Arrays.asList(1, 5, 10, 12, 15, 17, 19, 21, 24, 26);
        List<Integer> sequenceListH = Arrays.asList(2, 6, 9, 13, 18, 20, 23, 25, 27, 29);
        for (int i = 0; i < ApplicationUtils.COUNT; i++) {
            int storedValue = ApplicationUtils.getValueFromPrefs(context, name, i);
            if (storedValue == -1) continue;
            int sequence = i + 1;
            if (sequence == 3) {
                mResultScoreArray[3] += storedValue;
                saveAnswer(storedValue, -1);
            } else {
                int valueToAdd = getValueToAdd(sequence, storedValue);
                if (sequenceListM.contains(sequence)) {
                    mResultScoreArray[0] += valueToAdd;
                } else if (sequenceListB.contains(sequence)) {
                    mResultScoreArray[1] += valueToAdd;
                } else if (sequenceListH.contains(sequence)) {
                    mResultScoreArray[2] += valueToAdd;
                }
                saveAnswer(storedValue, valueToAdd);
            }
        }
        mResultScoreArray[3] += mResultScoreArray[0] + mResultScoreArray[1] + mResultScoreArray[2];
    }

    private void saveAnswer(int storedValue, int valueAdded) {
        mAnswerList.add(new Answer(storedValue, valueAdded == -1 ? storedValue : valueAdded));
    }

    private int getValueToAdd(int sequence, int storedValue) {
        switch (sequence) {
            case 1:
            case 4:
            case 5:
            case 6:
            case 7:
            case 11:
            case 13:
            case 14:
            case 16:
            case 20:
            case 23:
            case 25:
            case 27:
                return Math.abs(storedValue - 8);
            default:
                return storedValue;
        }
    }
}
