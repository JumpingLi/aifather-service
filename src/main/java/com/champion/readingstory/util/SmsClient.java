package com.champion.readingstory.util;

import com.alibaba.fastjson.JSONObject;
import com.champion.readingstory.vo.response.SmsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * @author jpli3
 */
@Component
public class SmsClient {
    private final String apiKey = "YXH5";
    private final String apiSecret = "467663568760706";
    private final String baseUrl = "http://fdpapi.adsring.cn/sms/order/1";
    @Value("${sms.sign.id:54}")
    private Integer smsSignId;
    @Value("${sms.template.id:116916819812179}")
    private Long smsTemplateId;
    @Autowired
    private RestTemplate restTemplate;

    public SmsResponse sendSms(String code, String telNo) {
        String body = "{\"code\":" + "\"" + code + "\"" + "}";
        //获取请求参数
        String content = getOrderParam(telNo, smsSignId, smsTemplateId, body);
        //请求时间戳
        String timestamp = String.valueOf(System.currentTimeMillis());
        //请求唯一流水号
        String uuId = UUID.randomUUID().toString();
        String sign = apiSecret + uuId + timestamp;
        String digest = Md5Util.md5Encode(sign);
        // spring restTemplate
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.add("X-IFLYFDP-USER", apiKey);
        headers.add("X-IFLYFDP-TS", timestamp);
        headers.add("X-IFLYFDP-NONCE", uuId);
        headers.add("X-IFLYFDP-TOKEN", digest.toUpperCase());
        HttpEntity<String> entity = new HttpEntity<>(content, headers);
        String strBody = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, String.class).getBody();
        return JSONObject.parseObject(strBody, SmsResponse.class);
    }

    private String getOrderParam(String telNo, Integer smsSignId, Long smsTemplateId, String smsParam) {
        Map<String, String> paramMap = new TreeMap<>();
        paramMap.put("telNo", telNo);
        paramMap.put("smsSignId", String.valueOf(smsSignId));
        paramMap.put("smsTemplateId", String.valueOf(smsTemplateId));
        paramMap.put("smsParam", smsParam);
        paramMap.put("smsType", String.valueOf(1));
        paramMap.put("customerOrderId", "");
        return getParamStr(paramMap);
    }

    private String getParamStr(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        for (String key : map.keySet()) {
            if (map.get(key) != null && !"".equals(map.get(key))) {
                sb.append(key).append("=").append(map.get(key)).append("&");
            }
        }
        return sb.toString();
    }
}
