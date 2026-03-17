package com.smartstock.service;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.smartstock.dto.PaymentVerifyRequest;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HexFormat;

@Service
@Slf4j
public class PaymentService {

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Value("${razorpay.currency}")
    private String currency;

    @Value("${razorpay.company.name}")
    private String companyName;

    /**
     * Create a Razorpay order intent.
     * Amount is in paise (1 INR = 100 paise).
     */
    public JSONObject createRazorpayOrder(BigDecimal amount, String receipt) {
        try {
            RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount.multiply(BigDecimal.valueOf(100))
                    .setScale(0, RoundingMode.HALF_UP).intValue()); // Convert to paise
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", receipt);
            orderRequest.put("payment_capture", 1);

            com.razorpay.Order razorpayOrder = client.orders.create(orderRequest);
            JSONObject result = new JSONObject();
            result.put("razorpay_order_id", razorpayOrder.get("id").toString());
            result.put("amount", amount);
            result.put("currency", currency);
            result.put("key_id", razorpayKeyId);
            result.put("company_name", companyName);

            log.info("Razorpay order created: {}", razorpayOrder.get("id").toString());
            return result;

        } catch (RazorpayException e) {
            log.error("Razorpay order creation failed: {}", e.getMessage());
            throw new RuntimeException("Payment gateway error: " + e.getMessage());
        }
    }

    /**
     * Verify Razorpay payment signature.
     * Prevents payment tampering by validating HMAC-SHA256.
     * Formula: HMAC-SHA256(razorpay_order_id + "|" + razorpay_payment_id, secret)
     */
    public boolean verifyPaymentSignature(PaymentVerifyRequest req) {
        try {
            String data = req.getRazorpayOrderId() + "|" + req.getRazorpayPaymentId();
            String expectedSignature = computeHmacSha256(data, razorpayKeySecret);
            boolean valid = expectedSignature.equals(req.getRazorpaySignature());
            if (!valid) {
                log.warn("Payment signature mismatch! order={} payment={}",
                        req.getRazorpayOrderId(), req.getRazorpayPaymentId());
            }
            return valid;
        } catch (Exception e) {
            log.error("Signature verification error: {}", e.getMessage());
            return false;
        }
    }

    private String computeHmacSha256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        mac.init(keySpec);
        byte[] hash = mac.doFinal(data.getBytes());
        return HexFormat.of().formatHex(hash);
    }
}
