package com.example.busgo.until;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Helper tích hợp thanh toán MoMo (môi trường TEST).
 *
 * Flow: App gọi API /create → nhận payUrl/deeplink → mở MoMo → nhận kết quả qua redirect.
 *
 * LƯU Ý: Trong production, việc tạo signature và gọi API phải thực hiện ở backend
 * để bảo mật secretKey. Đây là cách triển khai dành cho demo/test.
 */
public class MoMoPaymentHelper {

    private static final String TAG = "MoMoPayment";

    // ========== CẤU HÌNH MÔI TRƯỜNG TEST ==========
    // Nguồn: https://github.com/momo-wallet/payment (repo chính thức MoMo)
    // Đăng ký tại https://business.momo.vn để lấy credentials production
    public static final String PARTNER_CODE = "MOMO";
    public static final String ACCESS_KEY = "F8BBA842ECF85";
    public static final String SECRET_KEY = "K951B6PE1waDMi640xX08PD3vg6EkVlz";
    public static final String API_ENDPOINT = "https://test-payment.momo.vn/v2/gateway/api/create";
    public static final String REDIRECT_URL = "busgo://momo_return";
    public static final String IPN_URL = "https://callback.url/momo_notify";
    // "payWithATM" = thanh toán thẻ ATM qua web (không cần app MoMo)
    // "captureWallet" = thanh toán ví MoMo (cần app MoMo)
    public static final String REQUEST_TYPE = "payWithATM";

    /**
     * Kết quả trả về từ MoMo API
     */
    public static class PaymentResponse {
        public int resultCode = -1;
        public String message = "";
        public String payUrl = "";
        public String deeplink = "";
        public String orderId = "";

        public boolean isSuccess() {
            return resultCode == 0;
        }
    }

    /**
     * Tạo yêu cầu thanh toán MoMo.
     * PHẢI GỌI TRÊN BACKGROUND THREAD (có network I/O).
     *
     * @param orderId   Mã đơn hàng duy nhất
     * @param amount    Số tiền (VNĐ), tối thiểu 1.000, tối đa 50.000.000
     * @param orderInfo Mô tả đơn hàng
     * @return PaymentResponse chứa payUrl và deeplink
     */
    public static PaymentResponse createPayment(String orderId, long amount, String orderInfo) {
        PaymentResponse result = new PaymentResponse();
        result.orderId = orderId;

        try {
            String requestId = orderId;
            String extraData = "";

            // Tạo chuỗi raw signature (các field sắp xếp theo alphabet)
            String rawSignature = "accessKey=" + ACCESS_KEY
                    + "&amount=" + amount
                    + "&extraData=" + extraData
                    + "&ipnUrl=" + IPN_URL
                    + "&orderId=" + orderId
                    + "&orderInfo=" + orderInfo
                    + "&partnerCode=" + PARTNER_CODE
                    + "&redirectUrl=" + REDIRECT_URL
                    + "&requestId=" + requestId
                    + "&requestType=" + REQUEST_TYPE;

            String signature = hmacSHA256(SECRET_KEY, rawSignature);

            // Build JSON request body
            JSONObject body = new JSONObject();
            body.put("partnerCode", PARTNER_CODE);
            body.put("requestId", requestId);
            body.put("amount", amount);
            body.put("orderId", orderId);
            body.put("orderInfo", orderInfo);
            body.put("redirectUrl", REDIRECT_URL);
            body.put("ipnUrl", IPN_URL);
            body.put("requestType", REQUEST_TYPE);
            body.put("extraData", extraData);
            body.put("lang", "vi");
            body.put("signature", signature);

            Log.d(TAG, "Request: " + body.toString());

            // Gọi API MoMo
            JSONObject response = httpPost(API_ENDPOINT, body.toString());
            Log.d(TAG, "Response: " + response.toString());

            // Parse kết quả
            result.resultCode = response.optInt("resultCode", -1);
            result.message = response.optString("message", "Lỗi không xác định");
            result.payUrl = response.optString("payUrl", "");
            result.deeplink = response.optString("deeplink", "");

        } catch (Exception e) {
            Log.e(TAG, "Lỗi tạo thanh toán MoMo", e);
            result.resultCode = -1;
            result.message = "Lỗi kết nối: " + e.getMessage();
        }

        return result;
    }

    /**
     * Tạo chữ ký HMAC-SHA256
     */
    private static String hmacSHA256(String key, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(
                key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * HTTP POST request trả về JSON
     */
    private static JSONObject httpPost(String urlStr, String jsonBody) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(30000);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }

        int responseCode = conn.getResponseCode();
        InputStream is = (responseCode >= 200 && responseCode < 300)
                ? conn.getInputStream()
                : conn.getErrorStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        conn.disconnect();

        return new JSONObject(response.toString());
    }
}
