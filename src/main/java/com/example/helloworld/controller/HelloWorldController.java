package com.example.helloworld.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import dto.Attemptsrequest;
import dto.GetDateUtil;
import dto.PaymentRequest;
import dto.TransactionPayload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class HelloWorldController {

    private final List<TransactionPayload> transactionPayloads = new ArrayList<>();

    private int intervalTimeInSeconds = 5;

    @GetMapping("/CheckTooManyAttempts")
    public String CheckTooManyAttempts(@RequestParam String requestId,@RequestParam String paymentType)
    {
        try {
            return TooManyAttempt(requestId,paymentType);

        } catch (Exception e) {
            System.out.println(e.toString());

        }
        return "OK";
    }

    public String TooManyAttempt(String requestId,String paymentType)
    {

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            Date currentDateTime = GetDateUtil.getCurrentDateTime();
                // Remove all request expired
                transactionPayloads.removeIf(req -> req.getExpiredAt().before(currentDateTime));
                String key = requestId;
                //Filter by request id and type are already exist and replace with your request type
                List<TransactionPayload> existRequests = transactionPayloads.stream().filter(req -> req.getKey().equalsIgnoreCase(key) && req.getPaymentType().equalsIgnoreCase( paymentType) && req.getExpiredAt().after(currentDateTime)).collect(Collectors.toList());
                //log.info(" after filtering size : " + transactionPayloads.size());
                if (existRequests.size() > 0) {
                    System.out.println(currentDateTime + " Too Many Attempts");
                    return currentDateTime + " Too Many Attempts";
                } else {
                    Date expiredAt = GetDateUtil.addSeconds(currentDateTime, intervalTimeInSeconds);
                    transactionPayloads.add(new TransactionPayload(key,paymentType, expiredAt));
                    System.out.println(expiredAt + " OK");
                    return expiredAt + " OK";
                }

    }



    private static String serializeToJson(TransactionPayload transactionPayload) {
        // Serialize the list to JSON (You can use any JSON serialization library of your choice)
        // Here, we'll use Gson for serialization to JSON.
//        Gson gson = new GsonBuilder()
//                .setLenient()
//                .create();
        Gson gson = new Gson();
        return gson.toJson(transactionPayload);
    }


}
