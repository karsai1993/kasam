package karsai.laszlo.kasam.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import karsai.laszlo.kasam.R;
import karsai.laszlo.kasam.fragment.QuestionFragment;
import karsai.laszlo.kasam.utils.ApplicationUtils;

public class QuestionsPagerAdapter extends FragmentStatePagerAdapter {

    private String mName;
    private String[] mQuestionsArray;
    private String[] mAnswerNoteToQuestionSevenArray;
    private String[] mAnswerNoteToQuestionOneArray;

    public QuestionsPagerAdapter(FragmentManager fm, Context context, String name) {
        super(fm);
        this.mQuestionsArray = context.getResources().getStringArray(R.array.questions);
        this.mAnswerNoteToQuestionOneArray = context.getResources().getStringArray(R.array.answers_1);
        this.mAnswerNoteToQuestionSevenArray = context.getResources().getStringArray(R.array.answers_7);
        this.mName = name;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new QuestionFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ApplicationUtils.QUESTION, mQuestionsArray[position]);
        bundle.putString(ApplicationUtils.ANSWER_NOTE_ONE, mAnswerNoteToQuestionOneArray[position]);
        bundle.putString(ApplicationUtils.ANSWER_NOTE_SEVEN, mAnswerNoteToQuestionSevenArray[position]);
        bundle.putString(ApplicationUtils.NAME, mName);
        bundle.putInt(ApplicationUtils.POSITION, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return String.valueOf(position + 1);
    }

    @Override
    public int getCount() {
        return ApplicationUtils.COUNT;
    }
}
