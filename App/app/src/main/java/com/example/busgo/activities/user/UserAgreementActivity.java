package com.example.busgo.activities.user;
import android.view.View;
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
                        " Khi sử dụng ứng dụng BUS GO, bạn xác nhận đã đọc, hiểu và đồng ý tuân thủ các điều khoản, chính sách và quy định được công bố trên ứng dụng. Thỏa thuận này có hiệu lực kể từ thời điểm bạn bắt đầu sử dụng dịch vụ.\n\n" +
                        "2. Nghĩa vụ của người dùng\n" +
                        " Người dùng cam kết cung cấp thông tin chính xác khi đăng ký tài khoản và sử dụng dịch vụ đúng mục đích. Bạn có trách nhiệm bảo mật thông tin đăng nhập và chịu trách nhiệm với mọi hoạt động phát sinh từ tài khoản của mình.\n\n" +
                        "3. Quy định sử dụng dịch vụ\n" +
                        "Người dùng không được thực hiện hành vi gian lận, giả mạo thông tin, can thiệp hệ thống hoặc gây ảnh hưởng đến BUS GO, nhà xe và bên thứ ba. Việc vi phạm có thể dẫn đến tạm ngưng hoặc chấm dứt quyền sử dụng dịch vụ.\n\n" +
                        "4. Quyền và trách nhiệm của BUS GO\n" +
                        "BUS GO có quyền điều chỉnh, cập nhật hoặc từ chối cung cấp dịch vụ khi phát hiện vi phạm. Chúng tôi không chịu trách nhiệm đối với thiệt hại phát sinh do người dùng cung cấp thông tin sai hoặc sử dụng dịch vụ không đúng quy định.\n\n" +
                        "5. Chấm dứt và xử lý vi phạm\n" +
                        "Người dùng có thể ngừng sử dụng ứng dụng bất kỳ lúc nào. BUS GO có quyền khóa hoặc chấm dứt tài khoản nếu có vi phạm. Mọi thắc mắc hoặc khiếu nại có thể liên hệ bộ phận hỗ trợ qua các kênh được cung cấp trên ứng dụng."
        );

        btnBack.setOnClickListener(v -> finish());
    }
}