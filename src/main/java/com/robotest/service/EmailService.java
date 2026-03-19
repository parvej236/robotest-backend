package com.robotest.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    // ── Verification email ────────────────────────────────────
    @Async
    public void sendVerificationEmail(String to, String name, String token) {
        String link = frontendUrl + "/verify-email?token=" + token;
        send(to,
            "Robotest — Verify Your Email",
            buildEmail(name,
                "Verify Your Email Address",
                "Thank you for registering! Click the button below to verify your "
                + "email and activate your account.",
                link,
                "Verify Email",
                "This link expires in 24 hours. If you did not sign up, please ignore this email.")
        );
    }

    // ── Welcome email ─────────────────────────────────────────
    @Async
    public void sendWelcomeEmail(String to, String name) {
        send(to,
            "Welcome to Robotest!",
            buildEmail(name,
                "Your Account Is Active",
                "Your email has been verified successfully. Your account is now fully active!",
                frontendUrl + "/login",
                "Login Now",
                "Good luck in the contests!")
        );
    }

    // ── Forgot password email ─────────────────────────────────
    @Async
    public void sendPasswordResetEmail(String to, String name, String token) {
        String link = frontendUrl + "/reset-password?token=" + token;
        send(to,
            "Robotest — Reset Your Password",
            buildEmail(name,
                "Password Reset Request",
                "We received a request to reset your password. Click the button below to set a new one.",
                link,
                "Reset Password",
                "This link expires in 1 hour. If you did not request a password reset, "
                + "you can safely ignore this email — your password will not change.")
        );
    }

    // ── Password changed notification ─────────────────────────
    @Async
    public void sendPasswordChangedEmail(String to, String name) {
        send(to,
            "Robotest — Password Changed",
            buildEmail(name,
                "Password Changed Successfully",
                "Your password has been changed. If you did not make this change, "
                + "please reset your password immediately.",
                frontendUrl + "/forgot-password",
                "Reset Password",
                "For your security, all active sessions have been signed out.")
        );
    }

    // ── Private helpers ───────────────────────────────────────
    private void send(String to, String subject, String html) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(msg, true, "UTF-8");
            h.setFrom(fromEmail);
            h.setTo(to);
            h.setSubject(subject);
            h.setText(html, true);
            mailSender.send(msg);
            log.info("Email sent → [{}] subject: {}", to, subject);
        } catch (Exception e) {
            log.error("Email failed → [{}]: {}", to, e.getMessage());
        }
    }

    private String buildEmail(String name, String title, String body,
                               String ctaUrl, String ctaLabel, String note) {
        return "<!DOCTYPE html><html lang=\"en\">"
            + "<head><meta charset=\"UTF-8\"/><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"/></head>"
            + "<body style=\"margin:0;padding:0;background:#050508;font-family:Arial,sans-serif;\">"
            + "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">"
            + "<tr><td align=\"center\" style=\"padding:40px 16px;\">"
            + "<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" "
            + "style=\"background:#0f0f1a;border:1px solid #ff003355;border-radius:8px;overflow:hidden;\">"
            // header
            + "<tr><td style=\"background:#0a0a0f;padding:20px 32px;border-bottom:2px solid #ff0033;\">"
            + "<span style=\"font-family:monospace;font-size:20px;font-weight:900;"
            + "color:#ff0033;letter-spacing:5px;\">ROBOTEST</span>"
            + "</td></tr>"
            // body
            + "<tr><td style=\"padding:40px 32px;\">"
            + "<p style=\"color:#cccccc;font-size:15px;margin:0 0 8px;\">Hello, <strong style=\"color:#fff;\">" + name + "</strong></p>"
            + "<h2 style=\"color:#ffffff;font-size:20px;margin:0 0 20px;\">" + title + "</h2>"
            + "<p style=\"color:#aaaaaa;font-size:14px;line-height:1.7;margin:0 0 28px;\">" + body + "</p>"
            + "<a href=\"" + ctaUrl + "\" "
            + "style=\"display:inline-block;background:#ff0033;color:#fff;text-decoration:none;"
            + "padding:13px 32px;border-radius:4px;font-weight:700;font-size:13px;"
            + "letter-spacing:1px;text-transform:uppercase;\">" + ctaLabel + "</a>"
            + "<p style=\"color:#666;font-size:12px;margin:28px 0 0;line-height:1.6;\">" + note + "</p>"
            + "</td></tr>"
            // footer
            + "<tr><td style=\"background:#07070f;padding:16px 32px;border-top:1px solid #ffffff11;\">"
            + "<p style=\"color:#444;font-size:11px;margin:0;\">"
            + "&copy; 2024 Robotest Platform. All rights reserved.</p>"
            + "</td></tr>"
            + "</table></td></tr></table>"
            + "</body></html>";
    }

    @Async
    public void sendRegistrationSuccessEmail(String to, String name, String contestName) {
        send(to,
                "Robotest — Contest Registration Confirmed",
                buildEmail(name,
                        "Registration Confirmed!",
                        "You have successfully registered for: <strong>" + contestName + "</strong>",
                        frontendUrl + "/contests",
                        "View Contest",
                        "Good luck in the competition!")
        );
    }
}
