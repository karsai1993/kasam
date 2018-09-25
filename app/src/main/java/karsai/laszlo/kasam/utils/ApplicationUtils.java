package karsai.laszlo.kasam.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import karsai.laszlo.kasam.R;

public class ApplicationUtils {

    public static final String NAME = "name";
    public static final String QUESTION = "question";
    public static final String ANSWER_NOTE_SEVEN = "answer_note_seven";
    public static final String ANSWER_NOTE_ONE = "answer_note_one";
    private static final String USER_WENT_THROUGH = "user_went_through";
    public static final String POSITION = "position";
    public final static int COUNT = 29;
    public final static String INTENT_NAME = "intent_name";
    public final static String INTENT_RESULT_SCORES = "intent_result_score";
    public final static String INTENT_ANSWER_LIST = "intent_answer_list";

    public static void exit(Context context) {
        Toast.makeText(
                context,
                context.getResources().getString(R.string.problem),
                Toast.LENGTH_LONG
        ).show();
        ((Activity) context).finish();
    }

    public static void addHyperlinkedText(TextView textView, String text) {
        Spanned spanned;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            spanned = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        } else {
            spanned = Html.fromHtml(text);
        }
        textView.setText(spanned);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static void saveNameToPrefs(Context context, String name) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(createNamePrefKey(context), name);
        editor.apply();
    }

    public static String getNameFromPrefs(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(createNamePrefKey(context), null);
    }

    private static String createNamePrefKey(Context context) {
        return new StringBuilder()
                .append(context.getResources().getString(R.string.app_name))
                .append("_")
                .append(NAME)
                .toString();
    }

    public static void saveUserWentThroughValueToPrefs(Context context, String name) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(createUserWentThroughPrefKey(context, name), true);
        editor.apply();
    }

    public static boolean getUserWentThroughValueFromPrefs(Context context, String name) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(createUserWentThroughPrefKey(context, name), false);
    }

    private static String createUserWentThroughPrefKey(Context context, String name) {
        return new StringBuilder()
                .append(context.getResources().getString(R.string.app_name))
                .append("_")
                .append(USER_WENT_THROUGH)
                .append("_")
                .append(name)
                .toString();
    }

    public static void updateProgressStatus(
            TextView textView,
            final View containerView,
            final View view,
            Button btnShowResult,
            Context context,
            String name) {
        int setDataCounter = 0;
        for (int i = 0; i < COUNT; i++) {
            int storedValue = getValueFromPrefs(context, name, i);
            if (storedValue != -1) {
                setDataCounter ++;
            }
        }
        final double ratio = (double) setDataCounter / COUNT;
        if (ratio == 1) {
            btnShowResult.setVisibility(View.VISIBLE);
        } else {
            btnShowResult.setVisibility(View.GONE);
        }
        double percentageValueRounded = round(ratio * 100);
        containerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = containerView.getWidth();
                int height = containerView.getHeight();
                double widthPercentage = (double) width * ratio;
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                        (int) widthPercentage,
                        height
                );
                view.setLayoutParams(layoutParams);
            }
        });
        textView.setText(
                new StringBuilder()
                        .append(percentageValueRounded)
                        .append("% (")
                        .append(setDataCounter)
                        .append(context.getResources().getString(R.string.of))
                        .append(COUNT)
                        .append(")")
        );
    }

    private static double round(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static void showNotAnsweredStatementsIfAny(
            Activity activity,
            TextView missingSequenceTextView,
            String name) {
        List<Integer> missedStatementSequenceList = getMissingStatementSequences(
                activity.getApplicationContext(),
                name
        );
        if (!missedStatementSequenceList.isEmpty()) {
            //goToTheFirstUnansweredQuestion(activity, missedStatementSequenceList.get(0));
            StringBuilder textToApplyBuilder = new StringBuilder();
            textToApplyBuilder.append(
                    activity.getResources().getString(R.string.answers_missing)
            );
            int size = missedStatementSequenceList.size();
            for (int j = 0; j < size; j ++) {
                if (j != 0) {
                    textToApplyBuilder.append(", ");
                }
                textToApplyBuilder.append(missedStatementSequenceList.get(j));
                if (j == size - 1) {
                    textToApplyBuilder.append(".");
                }
            }
            missingSequenceTextView.setText(textToApplyBuilder.toString());
            missingSequenceTextView.setVisibility(View.VISIBLE);
        } else {
            missingSequenceTextView.setVisibility(View.GONE);
        }
    }

    /*private static void goToTheFirstUnansweredQuestion(Activity activity, int index) {
        int realIndex = index - 1;
        ViewPager viewPager = activity.findViewById(R.id.viewpager_questions);
        if (realIndex != viewPager.getCurrentItem()) {
            viewPager.setCurrentItem(realIndex);
            activity.recreate();
        }
    }*/

    public static void saveValueToPrefs(Context context, String name, int position, int value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt(createPrefKey(context, name, position), value);
        editor.apply();
    }

    public static int getValueFromPrefs(Context context, String name, int position) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(createPrefKey(context, name, position), -1);
    }

    private static String createPrefKey(Context context, String name, int position) {
        return new StringBuilder()
                .append(context.getResources().getString(R.string.app_name))
                .append("_")
                .append(name)
                .append("_")
                .append(position)
                .toString();
    }

    private static List<Integer> getMissingStatementSequences(Context context, String name) {
        List<Integer> missingList = new ArrayList<>();
        for (int i = 0; i < COUNT; i++) {
            int storedValue = getValueFromPrefs(context, name, i);
            if (storedValue == -1) {
                missingList.add(i + 1);
            }
        }
        return missingList;
    }
}
