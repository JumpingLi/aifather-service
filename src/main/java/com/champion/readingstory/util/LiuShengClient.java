package com.champion.readingstory.util;

import com.alibaba.fastjson.JSONObject;
import com.champion.readingstory.vo.UserVoiceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author jpli3
 */
@Component
public class LiuShengClient {

    @Value("${ls.app.id:q8BY9wYBtB}")
    private String appId;
    @Value("${ls.app.key:247fa60f977b1b5dbac7802f95c19b0a}")
    private String appKeyServer;
    @Value("${ls.baseurl:http://ossptest.voicecloud.cn/h5tob}")
    private String baseUrl;
    @Value("${ls.ptts.spd}")
    private Integer spd;
    @Value("${ls.ptts.vol}")
    private Integer vol;
    @Value("${ls.ptts.pit}")
    private Integer pit;

    @Autowired
    private RestTemplate restTemplate;

    public JSONObject commonCall(String url, String requestBody, HttpHeaders headers) {
        long timestamp = System.currentTimeMillis();
        headers.add("Content-Type", "application/json;charset=utf-8");
        headers.add("X-Cur-Time", String.valueOf(timestamp));
        headers.add("X-App-Info", MessageFormat.format("appid={0};portal=server", appId));
        String signContent = appKeyServer + timestamp + Md5Util.md5Encode(requestBody);
        String sign = Md5Util.md5Encode(signContent).toLowerCase();
        headers.add("X-Sign", sign);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        return restTemplate.exchange(baseUrl + url, HttpMethod.POST, entity, JSONObject.class).getBody();
    }

    public JSONObject pttsCall(String url, String requestBody, HttpHeaders headers,String vcn) {
        long timestamp = System.currentTimeMillis();
        headers.add("Content-Type", "application/json;charset=utf-8");
        headers.add("X-Cur-Time", String.valueOf(timestamp));
        headers.add("X-App-Info", MessageFormat.format("appid={0};portal=server", appId));
        headers.add("X-Ptts-Param", "vcn=" + vcn + ";aue=lame;spd=" + spd);
        String signContent = appKeyServer + timestamp + Md5Util.md5Encode(requestBody);
        String sign = Md5Util.md5Encode(signContent).toLowerCase();
        headers.add("X-Sign", sign);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        String body = restTemplate.postForEntity(baseUrl + url,entity,String.class).getBody();
        JSONObject jsonObject = JSONObject.parseObject(body);
        return jsonObject;
    }

    public static void main(String[] args) {
        String smsParam = "{\"base\":{\"ret_code\":\"000000\", \"ret_desc\":\"success\"},\"user_voices\":[{\"voice_id\":\"1\",\"voice_id\":\"1\",\"vcn\":\"1\",\"app_id\":\"1\",\"third_user_id\":\"1\",\"phone_number\":\"1\",\"voice_name\":\"1\",\"train_ret\":\"1\",\"listen_url\":\"1\",\"updated_time\":\"1\",\"created_time\":\"1\",\"batch_id\":\"1\"},{\"voice_id\":\"2\",\"voice_id\":\"2\",\"vcn\":\"2\",\"app_id\":\"2\",\"third_user_id\":\"2\",\"phone_number\":\"2\",\"voice_name\":\"2\",\"train_ret\":\"2\",\"listen_url\":\"2\",\"updated_time\":\"2\",\"created_time\":\"2\",\"batch_id\":\"2\"}]}";
        JSONObject postData = JSONObject.parseObject(smsParam);
        String userVoices = JSONObject.toJSONString(postData.getJSONArray("user_voices"));
        List<UserVoiceVO> list = JSONObject.parseArray(userVoices, UserVoiceVO.class);
        System.out.println(postData.getJSONObject("base").get("ret_code"));
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).toString());
        }
    }
}
