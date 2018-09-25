package karsai.laszlo.kasam.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import karsai.laszlo.kasam.R;
import karsai.laszlo.kasam.utils.ApplicationUtils;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.tv_version)
    TextView mVersionTextView;
    @BindView(R.id.tv_icons)
    TextView mIconsTextView;
    @BindView(R.id.tv_docs1)
    TextView mDocsOneTextView;
    @BindView(R.id.tv_docs2)
    TextView mDocsTwoTextView;
    @BindView(R.id.tv_info)
    TextView mInfoTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        try {
            String version = getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(
                            getApplicationContext().getPackageName(),
                            0
                    ).versionName;
            mVersionTextView.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            mVersionTextView.setVisibility(View.GONE);
        } finally {
            ApplicationUtils.addHyperlinkedText(
                    mIconsTextView,
                    getResources().getString(R.string.about_icons)
            );
            ApplicationUtils.addHyperlinkedText(
                    mDocsOneTextView,
                    getResources().getString(R.string.about_documents1)
            );
            ApplicationUtils.addHyperlinkedText(
                    mDocsTwoTextView,
                    getResources().getString(R.string.about_documents2)
            );
            ApplicationUtils.addHyperlinkedText(
                    mInfoTextView,
                    getResources().getString(R.string.about_information)
            );
        }
    }
}
