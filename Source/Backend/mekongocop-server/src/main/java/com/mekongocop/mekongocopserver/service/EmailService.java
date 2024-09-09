package com.mekongocop.mekongocopserver.service;

import com.mekongocop.mekongocopserver.repository.EmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailParseException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final MailjetEmailService emailSender;

    public EmailService(MailjetEmailService mailjetEmailService) {
        this.emailSender = mailjetEmailService;
    }

    public void sendOTPEmail(String to, String otp) {
        // Trim the email to remove leading/trailing spaces
        to = to.trim();

        // Validate email address
        if (to == null || !to.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
            throw new IllegalArgumentException("Invalid email address");
        }

        // Log the email address for debugging
        System.out.println("Sending OTP to email: " + to);

        try {
            emailSender.sendOTPEmail(to, otp);
            logger.info("OTP Sent Successfully");
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
            throw new MailParseException("Could not parse mail", e);
        }
    }

    @Async
    public void sendApprovalEmail(String to, String userName) {
        String subject = "Kết quả xét duyệt";
        String htmlContent = "<html><body>" +
                "<table width='100%' cellpadding='0' cellspacing='0' style='background-color: #f5f5f5;'>" +
                "  <tr>" +
                "    <td align='center' style='padding: 20px 0;'>" +
                "      <img src='https://res.cloudinary.com/duwzchnsp/image/upload/v1725289359/qygz0ghl1ac3ajas4pny.png' alt='Mekong OCOP' style='max-width: 200px;'>" +
                "    </td>" +
                "  </tr>" +
                "  <tr>" +
                "    <td align='center' style='padding: 20px;'>" +
                "      <h1 style='color: #333;'>Xin chào, " + userName + "!</h1>" +
                "      <p style='font-size: 16px;'>Chúng tôi đã xem xét yêu cầu trở thành người bán hàng của bạn.</p>" +
                "      <p>Bạn đã được xét duyệt thành công và hãy đăng nhập lại website và đăng ký cửa hàng của mình.</p>" +
                "      <p>Cảm ơn bạn đã tham gia cùng Mekong OCOP</p>" +
                "      <p>Chúc may mắn,</p>" +
                "      <p>The Mekong OCOP Team</p>" +
                "    </td>" +
                "  </tr>" +
                "</table>" +
                "</body></html>";

        try {
            emailSender.sendEmail(to, subject, htmlContent);
        } catch (Exception e) {
            // Handle email sending errors
            e.printStackTrace();
        }
    }

    @Async
    public void sendRejectionEmail(String to, String userName) {
        String subject = "Kết quả xét duyệt";
        String htmlContent = "<html><body>" +
                "<table width='100%' cellpadding='0' cellspacing='0' style='background-color: #f5f5f5;'>" +
                "  <tr>" +
                "    <td align='center' style='padding: 20px 0;'>" +
                "      <img src='https://res.cloudinary.com/duwzchnsp/image/upload/v1725289359/qygz0ghl1ac3ajas4pny.png' alt='Mekong OCOP' style='max-width: 200px;'>" +
                "    </td>" +
                "  </tr>" +
                "  <tr>" +
                "    <td align='center' style='padding: 20px;'>" +
                "      <h1 style='color: #333;'>Xin chào, " + userName + "!</h1>" +
                "      <p style='font-size: 16px;'>Sau khi xem xét yêu cầu chúng tôi cảm thấy bạn chưa đạt đủ yêu cầu.</p>" +
                "      <p>Xin hãy bổ sung giấy tờ và thông tin cụ thể bằng cách trả lời Email này.</p>" +
                "      <p>Xin cảm ơn,</p>" +
                "      <p>The Mekong OCOP Team</p>" +
                "    </td>" +
                "  </tr>" +
                "</table>" +
                "</body></html>";

        try {
            emailSender.sendEmail(to, subject, htmlContent);
        } catch (Exception e) {
            // Handle email sending errors
            e.printStackTrace();
        }
    }
}