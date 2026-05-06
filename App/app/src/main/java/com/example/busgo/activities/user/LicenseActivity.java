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
                        "Ứng dụng BUS GO và toàn bộ nội dung liên quan như logo, hình ảnh, giao diện, thiết kế và mã nguồn thuộc quyền sở hữu của BUS GO và được bảo hộ theo quy định pháp luật về sở hữu trí tuệ.\n\n" +
                        "2. Phạm vi cấp phép\n" +
                        "Chúng tôi cấp cho người dùng quyền sử dụng ứng dụng với mục đích cá nhân, không độc quyền và không thể chuyển nhượng. Việc sử dụng chỉ giới hạn trong phạm vi chức năng mà ứng dụng cung cấp.\n\n" +
                        "3. Hạn chế sử dụng\n" +
                        "Người dùng không được sao chép, chỉnh sửa, phân phối, khai thác thương mại, can thiệp hệ thống hoặc thực hiện hành vi phân tích ngược (reverse engineer) ứng dụng khi chưa có sự cho phép bằng văn bản từ BUS GO.\n\n" +
                        "4. Vi phạm và xử lý\n" +
                        "Mọi hành vi vi phạm quyền sở hữu trí tuệ hoặc sử dụng ứng dụng sai mục đích có thể dẫn đến việc chấm dứt quyền sử dụng và xử lý theo quy định pháp luật.\n\n" +
                        "5. Thay đổi nội dung\n" +
                        "BUS GO có quyền cập nhật, chỉnh sửa hoặc nâng cấp ứng dụng nhằm cải thiện chất lượng dịch vụ mà không cần thông báo trước."
        );

        btnBack.setOnClickListener(v -> finish());
    }
}