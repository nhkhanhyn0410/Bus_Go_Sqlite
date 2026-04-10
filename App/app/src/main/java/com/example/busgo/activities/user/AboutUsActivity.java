package com.example.busgo.activities.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.busgo.R;

public class AboutUsActivity extends AppCompatActivity {

    private ImageView btnBack;
    private LinearLayout itemTerms, itemPrivacy, itemWebsite, itemLicense, itemAgreement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        initViews();
        initActions();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);

        itemTerms = findViewById(R.id.itemTerms);
        itemPrivacy = findViewById(R.id.itemPrivacy);
        itemWebsite = findViewById(R.id.itemWebsite);
        itemLicense = findViewById(R.id.itemLicense);
        itemAgreement = findViewById(R.id.itemAgreement);
    }

    private void initActions() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        itemTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AboutUsActivity.this, TermsActivity.class);
                startActivity(intent);
            }
        });

        itemPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AboutUsActivity.this, PrivacyPolicyActivity.class);
                startActivity(intent);
            }
        });

        itemWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://your-website.com")
                );
                startActivity(intent);
            }
        });

        itemLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AboutUsActivity.this, LicenseActivity.class);
                startActivity(intent);
            }
        });

        itemAgreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AboutUsActivity.this, UserAgreementActivity.class);
                startActivity(intent);
            }
        });
    }
}