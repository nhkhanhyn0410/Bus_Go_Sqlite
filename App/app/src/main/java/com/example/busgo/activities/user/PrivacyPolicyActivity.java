package com.example.busgo.activities.user;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.busgo.R;

public class PrivacyPolicyActivity extends AppCompatActivity {

    private ImageView btnBack, imgUpdateIcon;
    private TextView tvHeaderTitle, tvUpdatedAt, tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        btnBack = findViewById(R.id.btnBack);
        imgUpdateIcon = findViewById(R.id.imgUpdateIcon);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvUpdatedAt = findViewById(R.id.tvUpdatedAt);
        tvContent = findViewById(R.id.tvContent);

        imgUpdateIcon.setImageResource(R.drawable.ic_key_dull);
        tvHeaderTitle.setText("Chính sách bảo mật");
        tvUpdatedAt.setText("Cập nhật lần cuối: 01/03/2026");
        tvContent.setText(
                "1. Giới thiệu\n" +
                        "BUS GO tôn trọng quyền riêng tư của người dùng.\n\n" +
                        "2. Phạm vi thu thập\n" +
                        "Có thể thu thập họ tên, số điện thoại, email và lịch sử giao dịch.\n\n" +
                        "3. Mục đích sử dụng\n" +
                        "Dùng để đặt vé, xác nhận giao dịch và hỗ trợ khách hàng.\n\n" +
                        "4. Bảo mật dữ liệu\n" +
                        "Chúng tôi áp dụng biện pháp kỹ thuật để bảo vệ dữ liệu.\n\n" +
                        "5. Chia sẻ thông tin\n" +
                        "Chỉ chia sẻ khi cần thiết với đối tác liên quan.\n\n" +
                        "6. Quyền của người dùng\n" +
                        "Người dùng có quyền kiểm tra và chỉnh sửa dữ liệu.\n\n" +
                        "7. Cập nhật chính sách\n" +
                        "Chính sách có thể được điều chỉnh khi cần."
        );

        btnBack.setOnClickListener(v -> finish());
    }
}