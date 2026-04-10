package com.example.busgo.activities.user;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.busgo.R;

public class UserAgreementActivity extends AppCompatActivity {

    private ImageView btnBack, imgUpdateIcon;
    private TextView tvHeaderTitle, tvUpdatedAt, tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_agreement);

        btnBack = findViewById(R.id.btnBack);
        imgUpdateIcon = findViewById(R.id.imgUpdateIcon);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvUpdatedAt = findViewById(R.id.tvUpdatedAt);
        tvContent = findViewById(R.id.tvContent);

        imgUpdateIcon.setImageResource(R.drawable.ic_agree_dull);
        tvHeaderTitle.setText("Thỏa thuận người dùng");
        tvUpdatedAt.setText("Cập nhật lần cuối: 01/03/2026");
        tvContent.setText(
                "1. Chấp nhận thỏa thuận\n" +
                        "Khi sử dụng ứng dụng, bạn đồng ý với nội dung thỏa thuận này.\n\n" +
                        "2. Nghĩa vụ của người dùng\n" +
                        "Người dùng cần cung cấp thông tin chính xác.\n\n" +
                        "3. Quy định sử dụng dịch vụ\n" +
                        "Không được gian lận hay can thiệp hệ thống.\n\n" +
                        "4. Quyền và trách nhiệm của BUS GO\n" +
                        "BUS GO có quyền điều chỉnh dịch vụ khi cần.\n\n" +
                        "5. Chấm dứt và xử lý vi phạm\n" +
                        "Tài khoản có thể bị khóa nếu vi phạm."
        );

        btnBack.setOnClickListener(v -> finish());
    }
}