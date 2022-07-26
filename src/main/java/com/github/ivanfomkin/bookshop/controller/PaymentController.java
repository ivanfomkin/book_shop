package com.github.ivanfomkin.bookshop.controller;

import com.github.ivanfomkin.bookshop.dto.CommonResultDto;
import com.github.ivanfomkin.bookshop.dto.payment.PaymentRequestDto;
import com.github.ivanfomkin.bookshop.dto.payment.RobokassaPaymentResultDto;
import com.github.ivanfomkin.bookshop.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.security.NoSuchAlgorithmException;

@Slf4j
@Controller
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payment")
    public RedirectView handlePaymentRequest(PaymentRequestDto paymentRequest) throws NoSuchAlgorithmException {
        return new RedirectView(paymentService.getPaymentUrl(paymentRequest));
    }

    @GetMapping("/payment/success")
    public String paymentResult(RedirectAttributes redirectAttributes, @RequestParam(name = "OutSum") Double outSum, @RequestParam(name = "InvId") Long invId, @RequestParam(name = "SignatureValue") String signatureValue) throws NoSuchAlgorithmException {
        redirectAttributes.addFlashAttribute("paymentStatus", paymentService.checkPaymentResult(outSum, invId, signatureValue));
        return "redirect:/profile/#topup";
    }

    @GetMapping("/payment/failed")
    public String paymentResult(RedirectAttributes redirectAttributes, @RequestParam(name = "InvId") Long invId) {
        redirectAttributes.addFlashAttribute("paymentStatus", false);
        paymentService.makePaymentFailed(invId);
        log.warn("Payment for invId {} is canceled", invId);
        return "redirect:/profile/#topup";
    }

    @ResponseBody
    @PostMapping("/payment/result")
    public RobokassaPaymentResultDto handlePaymentResult(@RequestParam(name = "OutSum") String outSum, @RequestParam(name = "InvId") Long invId, @RequestParam(name = "SignatureValue") String signatureValue, @RequestParam(name = "fee", required = false, defaultValue = "0.00") Double fee, @RequestParam(name = "EMail") String email) throws NoSuchAlgorithmException {
        return paymentService.approvePayment(outSum, invId, signatureValue, fee, email);
    }

    @ResponseBody
    @PostMapping("/order")
    public CommonResultDto orderBooks() {
        return paymentService.orderBooks();
    }
}
