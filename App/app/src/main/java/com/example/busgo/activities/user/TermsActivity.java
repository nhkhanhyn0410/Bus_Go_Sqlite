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
                        "BUS GO là nền tảng đặt vé xe khách trực tuyến, cho phép người dùng tìm chuyến, chọn ghế và thanh toán. Khi sử dụng dịch vụ, bạn đồng ý với các điều khoản này.\n\n" +
                        "2. Phạm vi dịch vụ\n" +
                        "BUS GO là đơn vị trung gian kết nối hành khách với nhà xe, không trực tiếp vận hành phương tiện. Thông tin về lịch trình, giá vé và chính sách do nhà xe cung cấp, có thể thay đổi theo thời điểm.\n\n" +
                        "3. Tài khoản người dùng\n" +
                        "Người dùng cần cung cấp thông tin chính xác, tự bảo mật tài khoản và chịu trách nhiệm với mọi hoạt động phát sinh. BUS GO có quyền khóa tài khoản nếu phát hiện vi phạm.\n\n" +
                        "4. Đặt vé và thanh toán\n" +
                        "Vé được xác nhận khi thanh toán thành công. Người dùng cần kiểm tra kỹ thông tin chuyến đi trước khi đặt. Giá vé có thể gồm một số khoản phí theo quy định.\n\n" +
                        "5. Hủy vé và hoàn tiền\n" +
                        "Chính sách hủy vé và hoàn tiền áp dụng theo quy định của từng nhà xe. Thời gian hoàn tiền phụ thuộc vào phương thức thanh toán và đơn vị liên quan.\n\n" +
                        "6. Giới hạn trách nhiệm\n" +
                        "BUS GO không chịu trách nhiệm với việc thay đổi lịch trình, hủy chuyến hoặc sự cố từ phía nhà xe, nhưng sẽ hỗ trợ người dùng trong phạm vi dịch vụ.\n\n" +
                        "7. Thay đổi điều khoản\n" +
                        " BUS GO có quyền cập nhật các điều khoản để phù hợp với quy định pháp luật và hoạt động thực tế."
        );

        btnBack.setOnClickListener(v -> finish());
    }
}