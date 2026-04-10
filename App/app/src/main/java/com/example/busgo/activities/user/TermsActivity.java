package com.example.busgo.activities.user;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.busgo.R;

public class TermsActivity extends AppCompatActivity {

    private ImageView btnBack, imgUpdateIcon;
    private TextView tvHeaderTitle, tvUpdatedAt, tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        btnBack = findViewById(R.id.btnBack);
        imgUpdateIcon = findViewById(R.id.imgUpdateIcon);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvUpdatedAt = findViewById(R.id.tvUpdatedAt);
        tvContent = findViewById(R.id.tvContent);

        imgUpdateIcon.setImageResource(R.drawable.ic_text_dull);
        tvHeaderTitle.setText("Điều khoản và điều kiện");
        tvUpdatedAt.setText("Cập nhật lần cuối: 01/03/2026");
        tvContent.setText(
                "1. Giới thiệu\n" +
                        "BUS GO là nền tảng đặt vé xe khách trực tuyến, cho phép người dùng tìm chuyến, chọn chỗ và thanh toán.\n\n" +
                        "2. Phạm vi dịch vụ\n" +
                        "BUS GO là đơn vị trung gian kết nối nhà xe và hành khách.\n\n" +
                        "3. Tài khoản người dùng\n" +
                        "Người dùng cần cung cấp thông tin chính xác và bảo mật tài khoản.\n\n" +
                        "4. Đặt vé và thanh toán\n" +
                        "Việc đặt vé chỉ hoàn tất khi thanh toán thành công.\n\n" +
                        "5. Hủy vé và hoàn tiền\n" +
                        "Áp dụng theo quy định của từng nhà xe.\n\n" +
                        "6. Giới hạn trách nhiệm\n" +
                        "BUS GO không chịu trách nhiệm với các lỗi ngoài tầm kiểm soát.\n\n" +
                        "7. Thay đổi điều khoản\n" +
                        "BUS GO có quyền cập nhật điều khoản khi cần."
        );

        btnBack.setOnClickListener(v -> finish());
    }
}