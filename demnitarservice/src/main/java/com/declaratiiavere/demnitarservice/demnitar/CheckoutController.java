package com.declaratiiavere.demnitarservice.demnitar;

import java.math.BigDecimal;
import java.util.Arrays;

import com.braintreegateway.*;
import com.braintreegateway.Transaction.Status;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping(value = "/checkouts")
public class CheckoutController {

//    private BraintreeGateway gateway = new BraintreeGateway(Environment.SANDBOX,
//            "3fqyqjhtgt2m24nt",
//            "d3gkr4wnh9yysf5w",
//            "e72f2555de7178cbca9d238d989fb33b");

    private BraintreeGateway gateway = new BraintreeGateway(Environment.SANDBOX,
            "3fqyqjhtgt2m24nt",
            "d3gkr4wnh9yysf5w",
            "e72f2555de7178cbca9d238d989fb33b");

    private Status[] TRANSACTION_SUCCESS_STATUSES = new Status[] {
            Transaction.Status.AUTHORIZED,
            Transaction.Status.AUTHORIZING,
            Transaction.Status.SETTLED,
            Transaction.Status.SETTLEMENT_CONFIRMED,
            Transaction.Status.SETTLEMENT_PENDING,
            Transaction.Status.SETTLING,
            Transaction.Status.SUBMITTED_FOR_SETTLEMENT
    };

//    @RequestMapping(value = "/", method = RequestMethod.GET)
//    public String root(Model model) {
//        return "redirect:checkouts";
//    }

    @RequestMapping(method = RequestMethod.POST)
    public String postForm(@RequestParam("amount") String amount, @RequestParam("payment_method_nonce") String nonce, Model model, final RedirectAttributes redirectAttributes) {
        BigDecimal decimalAmount;
        try {
            decimalAmount = new BigDecimal(amount);
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("errorDetails", "Error: 81503: Amount is an invalid format.");
            return "redirect:checkouts";
        }

        TransactionRequest request = new TransactionRequest()
                .amount(decimalAmount)
                .paymentMethodNonce("fake-valid-visa-nonce")
                .merchantAccountId("razvandani2")
                .type(Transaction.Type.SALE)
                .shippingAddress().firstName("Razvan").lastName("Dani").
                        streetAddress("Retezat 3").countryCodeAlpha2("RO").locality("Cluj-Napoca").region("Cluj").postalCode("1111").
                        done()
                .creditCard()
//                .token("dfksdhfgjkxdhjk345643534534534534zdfgdfghfcghfd")
                .cardholderName("Razan Dani")
                .cvv("222")
                .expirationMonth("10")
                .expirationYear("2020")
                .number("4111111111111111")

//                .submitForSettlement(true)


                .done();

        Result<Transaction> result = gateway.transaction().sale(request);

        if (result.isSuccess()) {
            Transaction transaction = result.getTarget();
            return "success redirect:checkouts/" + transaction.getId();
        } else if (result.getTransaction() != null) {
            Transaction transaction = result.getTransaction();
            return "fail redirect:checkouts/" + transaction.getId();
        } else {
            String errorString = "";
            for (ValidationError error : result.getErrors().getAllDeepValidationErrors()) {
                errorString += "Error: " + error.getCode() + ": " + error.getMessage() + "\n";
            }
            redirectAttributes.addFlashAttribute("errorDetails", errorString);
            return "redirect:checkouts";
        }
    }

    @RequestMapping(value = "/checkouts/{transactionId}")
    public String getTransaction(@PathVariable String transactionId, Model model) {
        Transaction transaction;
        CreditCard creditCard;
        Customer customer;

        try {
            transaction = gateway.transaction().find(transactionId);
            creditCard = transaction.getCreditCard();
            customer = transaction.getCustomer();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            return "redirect:/checkouts";
        }

        model.addAttribute("isSuccess", Arrays.asList(TRANSACTION_SUCCESS_STATUSES).contains(transaction.getStatus()));
        model.addAttribute("transaction", transaction);
        model.addAttribute("creditCard", creditCard);
        model.addAttribute("customer", customer);

        return "checkouts/show";
    }
}