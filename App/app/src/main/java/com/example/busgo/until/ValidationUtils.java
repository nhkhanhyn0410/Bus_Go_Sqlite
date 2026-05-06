package com.example.busgo.until;

import java.util.regex.Pattern;

public class ValidationUtils {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(0[3|5|7|8|9])+([0-9]{8})$");


    public static boolean isValidEmail(String email) {
        return email != null
                && !email.trim().isEmpty()
                && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null
                && !phone.trim().isEmpty()
                && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static String getEmailError(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email không được để trống";
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return "Email không hợp lệ";
        }
        return null;
    }

    public static String getPhoneError(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return "Số điện thoại không được để trống";
        }
        if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            return "Số điện thoại không hợp lệ";
        }
        return null;
    }

    public static String getPasswordError(String password) {
        if (password == null || password.isEmpty()) {
            return "Mật khẩu không được để trống";
        }
        if (password.length() < Constants.MIN_PASSWORD_LENGTH) {
            return "Mật khẩu phải có ít nhất " + Constants.MIN_PASSWORD_LENGTH + " ký tự";
        }
        return null;
    }

    public static String getConfirmPasswordError(String password, String confirmPassword) {
        if (confirmPassword == null || confirmPassword.isEmpty()) {
            return "Vui lòng xác nhận mật khẩu";
        }
        if (!confirmPassword.equals(password)) {
            return "Mật khẩu không khớp";
        }
        return null;
    }

    public static String getFullnameError(String fullname) {
        if (fullname == null || fullname.trim().isEmpty()) {
            return "Vui lòng nhập họ và tên";
        }
        if (fullname.trim().length() < Constants.MIN_FULLNAME_LENGTH) {
            return "Họ và tên phải có ít nhất " + Constants.MIN_FULLNAME_LENGTH + " ký tự";
        }
        return null;
    }

    public static String getBirthdayError(String birthday) {
        if (birthday == null || birthday.trim().isEmpty()) {
            return "Vui lòng nhập ngày sinh";
        }
        return null;
    }

    public static String getGenderError(String gender) {
        if (gender == null || gender.isEmpty()) {
            return "Vui lòng chọn giới tính";
        }
        return null;
    }

    public static String getPhoneOrEmailError(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "Vui lòng nhập số điện thoại hoặc email";
        }
        String trimmed = input.trim();
        if (isPhoneFormat(trimmed)) {
            if (!PHONE_PATTERN.matcher(trimmed).matches()) {
                return "Số điện thoại không hợp lệ";
            }
            return null;
        }
        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            return "Email không hợp lệ";
        }
        return null;
    }

    private static boolean isPhoneFormat(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        String trimmed = input.trim();
        return trimmed.matches("^[0-9]+$") || trimmed.startsWith("0");
    }
}
