package com.example.busgo.until;

import java.util.regex.Pattern;

public class ValidationUtils {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(0[3|5|7|8|9])+([0-9]{8})$");

    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        return password.length() >= Constants.MIN_PASSWORD_LENGTH;
    }

    public static boolean isValidFullname(String fullname) {
        if (fullname == null || fullname.trim().isEmpty()) {
            return false;
        }

        return fullname.trim().length() >= Constants.MIN_FULLNAME_LENGTH;
    }


    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true; // Email là optional
        }

        return EMAIL_PATTERN.matcher(email).matches();
    }


    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }

        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }


    public static boolean isPasswordMatch(String password, String confirmPassword) {
        if (password == null || confirmPassword == null) {
            return false;
        }

        return password.equals(confirmPassword);
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


    public static String getFullnameError(String fullname) {
        if (fullname == null || fullname.trim().isEmpty()) {
            return "Họ tên không được để trống";
        }

        if (fullname.trim().length() < Constants.MIN_FULLNAME_LENGTH) {
            return "Họ tên phải có ít nhất " + Constants.MIN_FULLNAME_LENGTH + " ký tự";
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


    public static String getEmailError(String email) {

        if (email == null || email.trim().isEmpty()) {
            return "Email không được để trống";
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return "Email không hợp lệ";
        }

        return null;
    }


    public static boolean isPhoneFormat(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        String trimmed = input.trim();
        return trimmed.matches("^[0-9]+$") || trimmed.startsWith("0");
    }

    public static boolean isValidPhoneOrEmail(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }

        String trimmed = input.trim();

        if (isPhoneFormat(trimmed)) {
            return PHONE_PATTERN.matcher(trimmed).matches();
        }

        return EMAIL_PATTERN.matcher(trimmed).matches();
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
}
