package com.example.busgo.until;

import android.os.Handler;
import android.os.Looper;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import com.example.busgo.BuildConfig;

public class EmailOtpSender {
    private static final String SENDER_EMAIL = BuildConfig.SENDER_EMAIL;
    private static final String APP_PASSWORD = BuildConfig.APP_PASSWORD;

    private static final String SENDER_NAME = "BusGo";

    public interface OnEmailSentListener {
        void onSuccess();
        void onFailure(String error);
    }

    public static void sendOtp(String toEmail, String otpCode, OnEmailSentListener listener) {
        new Thread(() -> {
            try {
                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD);
                    }
                });
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(SENDER_EMAIL, SENDER_NAME));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject("Mã xác minh BusGo: " + otpCode);
                String htmlContent = buildEmailContent(otpCode);
                message.setContent(htmlContent, "text/html; charset=utf-8");
                Transport.send(message);
                notifyOnMainThread(() -> listener.onSuccess());

            } catch (Exception e) {
                e.printStackTrace();
                String errorMsg = e.getMessage() != null ? e.getMessage() : "Lỗi không xác định";
                notifyOnMainThread(() -> listener.onFailure(errorMsg));
            }
        }).start();
    }

    private static String buildEmailContent(String otpCode) {
        return "<!DOCTYPE html>"
                + "<html>"
                + "<body style='font-family: Arial, sans-serif; padding: 20px; background-color: #f5f5f5;'>"
                + "<div style='max-width: 400px; margin: 0 auto; background: white; border-radius: 12px; padding: 30px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);'>"
                + "  <h2 style='color: #FF6B35; text-align: center; margin-bottom: 10px;'>BusGo</h2>"
                + "  <p style='text-align: center; color: #666; font-size: 14px;'>Mã xác minh tài khoản của bạn</p>"
                + "  <div style='background: #FFF3EE; border-radius: 8px; padding: 20px; text-align: center; margin: 20px 0;'>"
                + "    <span style='font-size: 32px; font-weight: bold; letter-spacing: 8px; color: #FF6B35;'>" + otpCode + "</span>"
                + "  </div>"
                + "  <p style='text-align: center; color: #999; font-size: 12px;'>Mã có hiệu lực trong 5 phút.<br>Không chia sẻ mã này với bất kỳ ai.</p>"
                + "</div>"
                + "</body>"
                + "</html>";
    }

    private static void notifyOnMainThread(Runnable action) {
        new Handler(Looper.getMainLooper()).post(action);
    }
}

