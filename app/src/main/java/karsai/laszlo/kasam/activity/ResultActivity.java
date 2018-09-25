package karsai.laszlo.kasam.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import karsai.laszlo.kasam.OnContentViewTypeChangeListener;
import karsai.laszlo.kasam.R;
import karsai.laszlo.kasam.adapter.InformationAdapter;
import karsai.laszlo.kasam.model.Answer;
import karsai.laszlo.kasam.model.Information;
import karsai.laszlo.kasam.utils.ApplicationUtils;

public class ResultActivity extends AppCompatActivity implements OnContentViewTypeChangeListener {

    @BindView(R.id.tv_meaningful_value)
    TextView mMeaningfulValueTextView;
    @BindView(R.id.tv_comprehension_value)
    TextView mComprehensionValueTextView;
    @BindView(R.id.tv_manageability_value)
    TextView mManageabilityValueTextView;
    @BindView(R.id.tv_total_value)
    TextView mTotalScoreTextView;
    @BindView(R.id.rv_result)
    RecyclerView mResultRecyclerView;

    private String mName;
    private List<Answer> mAnswerList;
    private int mMeaningfulValue;
    private int mComprehensionValue;
    private int mManageabilityValue;
    private int mTotalScore;
    private List<Information> mInformationList;
    private InformationAdapter mInformationAdapter;

    private final static String SAVE_LIST = "saved_list";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);

        Intent receivedData = getIntent();
        if (receivedData != null) {
            int[] data = receivedData.getIntArrayExtra(ApplicationUtils.INTENT_RESULT_SCORES);
            mName = receivedData.getStringExtra(ApplicationUtils.INTENT_NAME);
            mAnswerList = receivedData.getParcelableArrayListExtra(ApplicationUtils.INTENT_ANSWER_LIST);
            if (data != null && mName != null && mAnswerList != null) {
                setTitle(mName + getResources().getString(R.string.results_title));
                mMeaningfulValue = data[0];
                mComprehensionValue = data[1];
                mManageabilityValue = data[2];
                mTotalScore = data[3];
                initResultData();
                initRecyclerView(savedInstanceState);
            } else {
                ApplicationUtils.exit(this);
            }
        } else {
            ApplicationUtils.exit(this);
        }
    }

    private void initRecyclerView(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mInformationList = getInformationList();
        } else {
            mInformationList = savedInstanceState.getParcelableArrayList(SAVE_LIST);
        }
        mResultRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mResultRecyclerView.setHasFixedSize(true);
        mInformationAdapter = new InformationAdapter(
                this,
                mInformationList
        );
        mResultRecyclerView.setAdapter(mInformationAdapter);
    }

    private List<Information> getInformationList() {
        List<Information> informationList = new ArrayList<>();
        if (mTotalScore >= 120 && mTotalScore <= 160) {
            informationList.add(
                    new Information(
                            getResources().getString(R.string.kasam_g_120_l_160_header),
                            getResources().getString(R.string.kasam_g_120_l_160_value)
                    )
            );
        } else if (mTotalScore > 160 && mTotalScore <= 190) {
            informationList.add(
                    new Information(
                            getResources().getString(R.string.kasam_g_160_l_190_header),
                            getResources().getString(R.string.kasam_g_160_l_190_value)
                    )
            );
        } else if (mTotalScore >= 70 && mTotalScore < 120) {
            informationList.add(
                    new Information(
                            getResources().getString(R.string.kasam_g_70_l_120_header),
                            getResources().getString(R.string.kasam_g_70_l_120_value)
                    )
            );
            if (mTotalScore <= 100) {
                informationList.add(
                        new Information(
                                getResources().getString(R.string.kasam_l_120_l_100_header),
                                getResources().getString(R.string.kasam_l_120_l_100_value)
                        )
                );
            }
            if (mMeaningfulValue <= 30) {
                informationList.add(
                        new Information(
                                getResources().getString(R.string.kasam_l_120_low_m_header),
                                getResources().getString(R.string.kasam_l_120_low_m_value)
                        )
                );
            }
            if (mComprehensionValue <= 35) {
                informationList.add(
                        new Information(
                                getResources().getString(R.string.kasam_l_120_low_b_header),
                                getResources().getString(R.string.kasam_l_120_low_b_value)
                        )
                );
            }
            if (mManageabilityValue <= 35) {
                informationList.add(
                        new Information(
                                getResources().getString(R.string.kasam_l_120_low_h_header),
                                getResources().getString(R.string.kasam_l_120_low_h_value)
                        )
                );
            }
        } else if (mTotalScore > 190 || mTotalScore < 70) {
            informationList.add(
                    new Information(
                            getResources().getString(R.string.kasam_g_190_l_70_header),
                            getResources().getString(R.string.kasam_g_190_l_70_value)
                    )
            );
        }
        return informationList;
    }

    private void initResultData() {
        mMeaningfulValueTextView.setText(String.valueOf(mMeaningfulValue));
        mComprehensionValueTextView.setText(String.valueOf(mComprehensionValue));
        mManageabilityValueTextView.setText(String.valueOf(mManageabilityValue));
        mTotalScoreTextView.setText(String.valueOf(mTotalScore));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(SAVE_LIST, (ArrayList<Information>) mInformationList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewTypeChangeNeeded(int position) {
        Information information = mInformationList.get(position);
        information.setExpanded(!information.isExpanded());
        mInformationAdapter.notifyDataSetChanged();
        mResultRecyclerView.smoothScrollToPosition(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.results_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
            share();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void share() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(getResources().getString(R.string.share_welcome), mName));
        sb.append(
                String.format(
                        getResources().getString(R.string.share_main_scores),
                        String.valueOf(mMeaningfulValue),
                        String.valueOf(mComprehensionValue),
                        String.valueOf(mManageabilityValue),
                        String.valueOf(mTotalScore))
        );
        sb.append(getResources().getString(R.string.share_results));
        for (int i = 0; i < mInformationList.size(); i++) {
            sb.append("(");
            sb.append(i+1);
            sb.append(") ");
            Information information = mInformationList.get(i);
            sb.append(information.getHeader());
            sb.append("\n\n");
            sb.append(information.getContent());
            sb.append("\n");
            if (i != mInformationList.size() - 1) {
                sb.append("\n");
            }
        }
        sb.append(getResources().getString(R.string.share_details));
        for (int i = 0; i < mAnswerList.size(); i++) {
            sb.append(i+1);
            sb.append(".");
            sb.append(mAnswerList.get(i).toString());
            sb.append("\n");
        }
        sb.append(getResources().getString(R.string.share_note));
        sb.append(getResources().getString(R.string.share_from));
        Intent sendReportIntent = new Intent();
        sendReportIntent.setAction(Intent.ACTION_SEND);
        sendReportIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        sendReportIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_subject));
        sendReportIntent.setType("text/plain");
        if (sendReportIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(sendReportIntent, getResources().getString(R.string.share_title)));
        }
    }
}
