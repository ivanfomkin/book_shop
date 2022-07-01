package com.github.ivanfomkin.bookshop.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ivanfomkin.bookshop.dto.call.CallResultDto;
import com.github.ivanfomkin.bookshop.dto.call.CallStatus;
import com.github.ivanfomkin.bookshop.exception.CallServiceException;
import com.github.ivanfomkin.bookshop.service.CallService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.StringJoiner;

@Slf4j
@Service
public class SmsRuCallServiceImpl implements CallService {
    private static final String ROOT_SMS_RU_CALL_ENDPOINT = "https://sms.ru/code/call";
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    private final RestTemplate restTemplate;

    public SmsRuCallServiceImpl(ObjectMapper objectMapper, @Value("${bookshop.call.sms.ru.token}") String apiId, RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        baseUrl = ROOT_SMS_RU_CALL_ENDPOINT + "?api_id=" + apiId;
        this.restTemplate = restTemplate;
    }

    @Override
    public String call(String phoneNumber, String ip) {
        var requestUrl = new StringJoiner("&")
                .add(baseUrl)
                .add("phone=" + phoneNumber)
                .add("ip=" + ip)
                .toString();
        try {
            var callResult = convertResponseToDto(restTemplate.getForObject(requestUrl, String.class));
            if (callResult.getStatus() != CallStatus.OK) {
                throw new CallServiceException(MessageFormatter.format("Error call to phone {}, message: {}", phoneNumber, callResult.getStatusText()).getMessage());
            }
            return callResult.getCode();
        } catch (JsonProcessingException e) {
            throw new CallServiceException(MessageFormatter.format("Error parse call response for phone {}", phoneNumber).getMessage());
        }

    }

    private CallResultDto convertResponseToDto(String response) throws JsonProcessingException {
        return objectMapper.readValue(response, CallResultDto.class);
    }
}
