package util;

import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;

public class EmailUtil {
    public static boolean sendOtpEmail(String toEmail, String otp) {
        final String fromEmail = ConfigUtil.getProperty("email.user");
        final String password = ConfigUtil.getProperty("email.password");

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Mã OTP Khôi Phục Mật Khẩu - TEA POS");

            String content = "<h3>Xin chào,</h3>"
                    + "<p>Mã OTP để khôi phục mật khẩu của bạn là: <b style='color:red; font-size:20px'>" + otp + "</b></p>"
                    + "<p>Mã này có hiệu lực trong 5 phút. Vui lòng không chia sẻ cho bất kỳ ai.</p>"
                    + "<br><p>Trân trọng,<br>Hệ thống Quản lý TEA POS</p>";

            message.setContent(content, "text/html; charset=utf-8");
            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
}