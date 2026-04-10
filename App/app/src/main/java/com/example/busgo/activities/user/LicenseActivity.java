package com.example.busgo.activities.user;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.busgo.R;

public class LicenseActivity extends AppCompatActivity {

    private ImageView btnBack, imgUpdateIcon;
    private TextView tvHeaderTitle, tvUpdatedAt, tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);

        btnBack = findViewById(R.id.btnBack);
        imgUpdateIcon = findViewById(R.id.imgUpdateIcon);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvUpdatedAt = findViewById(R.id.tvUpdatedAt);
        tvContent = findViewById(R.id.tvContent);

        imgUpdateIcon.setImageResource(R.drawable.ic_certificate_dull);
        tvHeaderTitle.setText("Giấy phép");
        tvUpdatedAt.setText("Cập nhật lần cuối: 01/03/2026");
        tvContent.setText(
                "1. Quyền sở hữu\n" +
                        "Ứng dụng BUS GO và nội dung liên quan thuộc quyền sở hữu hợp pháp.\n\n" +
                        "2. Phạm vi cấp phép\n" +
                        "Người dùng được cấp quyền sử dụng với mục đích cá nhân.\n\n" +
                        "3. Hạn chế sử dụng\n" +
                        "Không được sao chép hoặc khai thác thương mại trái phép.\n\n" +
                        "4. Vi phạm và xử lý\n" +
                        "Mọi vi phạm sẽ bị xử lý theo quy định pháp luật.\n\n" +
                        "5. Thay đổi nội dung\n" +
                        "BUS GO có quyền cập nhật nội dung khi cần."
        );

        btnBack.setOnClickListener(v -> finish());
    }
}