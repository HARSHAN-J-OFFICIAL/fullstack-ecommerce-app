package com.ecommerce.service;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.entity.User;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@ecommerce.com}")
    private String fromEmail;

    @Value("${admin.notification.email:admin@ecommerce.com}")
    private String adminEmail;

    // a) WELCOME EMAIL ON REGISTRATION
    @Async
    public void sendWelcomeEmail(User user) {
        if (user == null || user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return;
        }

        String subject = "Welcome to E-Commerce Store!";
        String name = (user.getUsername() != null && !user.getUsername().trim().isEmpty())
                ? user.getUsername()
                : "Valued Customer";

        String htmlContent = "<html><body style='font-family: Arial, sans-serif; color: #333; line-height: 1.6;'>"
                + "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e2e8f0; rounded: 16px;'>"
                + "<h2 style='color: #4F46E5; margin-bottom: 10px;'>Welcome to E-Commerce Store, " + name + "! 🎉</h2>"
                + "<p>Thank you for creating an account with us. We are thrilled to have you on board!</p>"
                + "<p>Explore our wide selection of products, save your favorite items to your wishlist, and enjoy a seamless shopping experience.</p>"
                + "<br/><p style='color: #64748b;'>Best regards,<br/><strong>The E-Commerce Team</strong></p>"
                + "</div></body></html>";

        sendHtmlEmail(user.getEmail(), subject, htmlContent);
    }

    // b) ORDER CONFIRMATION EMAIL
    @Async
    public void sendOrderConfirmationEmail(Order order) {
        if (order == null || order.getUser() == null || order.getUser().getEmail() == null) {
            return;
        }

        String recipientEmail = order.getUser().getEmail();
        String subject = "Order Confirmation - Order #" + order.getId();

        StringBuilder itemsTable = new StringBuilder();
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                String productName = item.getProduct() != null ? item.getProduct().getName() : "Product Item";
                itemsTable.append("<tr>")
                        .append("<td style='padding: 10px; border-bottom: 1px solid #edf2f7;'>").append(productName).append("</td>")
                        .append("<td style='padding: 10px; border-bottom: 1px solid #edf2f7; text-align: center;'>").append(item.getQuantity()).append("</td>")
                        .append("<td style='padding: 10px; border-bottom: 1px solid #edf2f7; text-align: right;'>₹").append(item.getPrice()).append("</td>")
                        .append("</tr>");
            }
        }

        String addressStr = order.getShippingAddress() != null
                ? order.getShippingAddress().getStreet() + ", " + order.getShippingAddress().getCity() + ", " + order.getShippingAddress().getState() + " - " + order.getShippingAddress().getPincode()
                : "Standard Delivery Address";

        String htmlContent = "<html><body style='font-family: Arial, sans-serif; color: #333; line-height: 1.6;'>"
                + "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e2e8f0; border-radius: 12px;'>"
                + "<h2 style='color: #4F46E5;'>Order Confirmed! 🛍️</h2>"
                + "<p>Thank you for your purchase. Here is your order summary:</p>"
                + "<ul style='list-style: none; padding: 0;'>"
                + "<li><strong>Order ID:</strong> #" + order.getId() + "</li>"
                + "<li><strong>Order Date:</strong> " + order.getOrderDate() + "</li>"
                + "<li><strong>Payment Status:</strong> " + (order.getPaymentStatus() != null ? order.getPaymentStatus() : "PENDING") + "</li>"
                + "<li><strong>Shipping Address:</strong> " + addressStr + "</li>"
                + "</ul>"
                + "<h3>Ordered Products:</h3>"
                + "<table style='width: 100%; border-collapse: collapse; margin-top: 10px;'>"
                + "<tr style='background: #f8fafc; color: #64748b; font-size: 13px;'><th style='padding: 10px; text-align: left;'>Product</th><th style='padding: 10px; text-align: center;'>Qty</th><th style='padding: 10px; text-align: right;'>Price</th></tr>"
                + itemsTable.toString()
                + "</table>"
                + "<h3 style='color: #0f172a; text-align: right; margin-top: 15px;'>Total Paid/Payable: ₹" + order.getTotalAmount() + "</h3>"
                + "<br/><p style='color: #64748b; font-size: 13px;'>We will notify you when your items are packed and shipped!</p>"
                + "</div></body></html>";

        sendHtmlEmail(recipientEmail, subject, htmlContent);
    }

    // c) PAYMENT SUCCESS EMAIL
    @Async
    public void sendPaymentSuccessEmail(Order order, String paymentId) {
        if (order == null || order.getUser() == null || order.getUser().getEmail() == null) {
            return;
        }

        String recipientEmail = order.getUser().getEmail();
        String subject = "Payment Successful - Order #" + order.getId();

        String htmlContent = "<html><body style='font-family: Arial, sans-serif; color: #333; line-height: 1.6;'>"
                + "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e2e8f0; border-radius: 12px;'>"
                + "<h2 style='color: #10B981;'>Payment Verified Successfully 💳</h2>"
                + "<p>We have successfully verified your payment for <strong>Order #" + order.getId() + "</strong>.</p>"
                + "<ul style='list-style: none; padding: 0;'>"
                + "<li><strong>Payment ID:</strong> " + (paymentId != null ? paymentId : "N/A") + "</li>"
                + "<li><strong>Amount Paid:</strong> ₹" + order.getTotalAmount() + "</li>"
                + "<li><strong>Payment Method:</strong> " + (order.getPaymentMethod() != null ? order.getPaymentMethod() : "ONLINE") + "</li>"
                + "</ul>"
                + "<p style='color: #64748b;'>Thank you for your business!</p>"
                + "</div></body></html>";

        sendHtmlEmail(recipientEmail, subject, htmlContent);
    }

    // d) ORDER STATUS UPDATE EMAIL
    @Async
    public void sendOrderStatusUpdateEmail(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        if (order == null || order.getUser() == null || order.getUser().getEmail() == null) {
            return;
        }

        String recipientEmail = order.getUser().getEmail();
        String subject = "Order #" + order.getId() + " Status Update: " + newStatus;

        String statusColor = "#4F46E5";
        if (newStatus == OrderStatus.DELIVERED || newStatus == OrderStatus.CONFIRMED) statusColor = "#10B981";
        if (newStatus == OrderStatus.CANCELLED) statusColor = "#EF4444";

        String htmlContent = "<html><body style='font-family: Arial, sans-serif; color: #333; line-height: 1.6;'>"
                + "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e2e8f0; border-radius: 12px;'>"
                + "<h2 style='color: " + statusColor + ";'>Order Status Updated 📦</h2>"
                + "<p>The status of your <strong>Order #" + order.getId() + "</strong> has changed.</p>"
                + "<p style='font-size: 18px; font-weight: bold;'>New Status: <span style='color: " + statusColor + ";'>" + newStatus + "</span></p>"
                + "<p style='color: #64748b; font-size: 13px;'>Previous Status: " + (oldStatus != null ? oldStatus : "PENDING") + "</p>"
                + "<br/><p>Log in to your account dashboard to track your order details in real-time.</p>"
                + "</div></body></html>";

        sendHtmlEmail(recipientEmail, subject, htmlContent);
    }

    // e) ADMIN NEW ORDER NOTIFICATION EMAIL
    @Async
    public void sendAdminOrderNotificationEmail(Order order) {
        if (order == null) return;

        String subject = "🚨 New Order Notification - Order #" + order.getId();
        String customerName = order.getUser() != null ? (order.getUser().getUsername() != null ? order.getUser().getUsername() : order.getUser().getEmail()) : "Customer";
        String customerEmail = order.getUser() != null ? order.getUser().getEmail() : "N/A";

        StringBuilder itemsTable = new StringBuilder();
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                String productName = item.getProduct() != null ? item.getProduct().getName() : "Product Item";
                itemsTable.append("<tr>")
                        .append("<td style='padding: 10px; border-bottom: 1px solid #edf2f7;'>").append(productName).append("</td>")
                        .append("<td style='padding: 10px; border-bottom: 1px solid #edf2f7; text-align: center;'>").append(item.getQuantity()).append("</td>")
                        .append("<td style='padding: 10px; border-bottom: 1px solid #edf2f7; text-align: right;'>₹").append(item.getPrice()).append("</td>")
                        .append("</tr>");
            }
        }

        String htmlContent = "<html><body style='font-family: Arial, sans-serif; color: #333; line-height: 1.6;'>"
                + "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e2e8f0; border-radius: 12px;'>"
                + "<h2 style='color: #4F46E5;'>New Order Received 🛒</h2>"
                + "<p>A new order has been placed on the store.</p>"
                + "<ul style='list-style: none; padding: 0;'>"
                + "<li><strong>Order ID:</strong> #" + order.getId() + "</li>"
                + "<li><strong>Customer Name:</strong> " + customerName + "</li>"
                + "<li><strong>Customer Email:</strong> " + customerEmail + "</li>"
                + "<li><strong>Total Amount:</strong> ₹" + order.getTotalAmount() + "</li>"
                + "</ul>"
                + "<h3>Order Items:</h3>"
                + "<table style='width: 100%; border-collapse: collapse; margin-top: 10px;'>"
                + "<tr style='background: #f8fafc; color: #64748b; font-size: 13px;'><th style='padding: 10px; text-align: left;'>Product</th><th style='padding: 10px; text-align: center;'>Qty</th><th style='padding: 10px; text-align: right;'>Price</th></tr>"
                + itemsTable.toString()
                + "</table>"
                + "</div></body></html>";

        sendHtmlEmail(adminEmail, subject, htmlContent);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Email successfully sent to {} with subject '{}'", to, subject);
        } catch (Exception e) {
            log.error("Failed to send email to {} with subject '{}'. Reason: {}", to, subject, e.getMessage());
            // Gracefully catch exception so calling business transactions are never aborted
        }
    }
}
