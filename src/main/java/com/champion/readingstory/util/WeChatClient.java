package com.champion.readingstory.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.champion.readingstory.vo.response.CommonResponse;
import com.champion.readingstory.constant.CommonConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author: JiangPing Li
 * @date: 2018-09-16 10:37
 */
@Component
public class WeChatClient {

    @Value("${wechat.mini-program.app-id}")
    private String appId;
    @Value("${wechat.mini-program.secret}")
    private String secret;

    private String access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={0}&secret={1}";
    private String openId_url = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";
    private String mimi_code_url_b = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token={0}";
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 小程序获取openId
     *
     * @param code
     * @return
     */
    public JSONObject getOpenId(String code) {
        String body = restTemplate.getForEntity(String.format(openId_url, appId, secret, code), String.class).getBody();
        return JSONObject.parseObject(body);
    }

    /**
     * 获取 接口access_token
     *
     * @return
     */
    public String getAccessToken() {
        String key = String.format(CommonConstants.ACCESS_TOKEN, "access_token");
        boolean hasKey = Optional.ofNullable(redisTemplate.hasKey(key)).orElse(Boolean.FALSE);
        String access_token = null;
        if (hasKey) {
            access_token = (String) redisTemplate.opsForValue().get(key);
        } else {
            String body = restTemplate.getForEntity(MessageFormat.format(access_token_url, appId, secret), String.class).getBody();
            JSONObject jsonObject = JSON.parseObject(body);
            if (jsonObject != null) {
                access_token = jsonObject.getString("access_token");
                int expires = jsonObject.getIntValue("expires_in");
                redisTemplate.opsForValue().setIfAbsent(key, access_token);
                redisTemplate.expire(key,expires,TimeUnit.SECONDS);
            }
        }
        return access_token;
    }

    public CommonResponse<?> getWXACodeUnLimit(String scene, String page) throws IOException {
        String accessToken = getAccessToken();
        if (StringUtils.isBlank(accessToken)) {
            return CommonResponse.buildFailure("获取access_token失败");
        }
        JSONObject params = new JSONObject();
        params.put("scene", scene);
        params.put("page", page);
        params.put("width",400);
        params.put("auto_color",false);
        params.put("is_hyaline",false);
        JSONObject lineColor = new JSONObject();
        lineColor.put("r",85);
        lineColor.put("g",107);
        lineColor.put("b",47);
//        params.put("line_color",lineColor);
        InputStream inputStream = null;
        OutputStream outputStream = null;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(params.toString(), headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(
                MessageFormat.format(mimi_code_url_b,accessToken),
                HttpMethod.POST,
                entity,
                byte[].class);
        byte[] result = response.getBody();
        System.out.println("===== 字节组长度:" + result.length);
        if (result == null | result.length < 1024){
            return CommonResponse.buildFailure("生成小程序接口返回为空!");
        }

//        StringBuilder builder = new StringBuilder();
        try {
            inputStream = new ByteArrayInputStream(result);
            String fileName = UUID.randomUUID().toString().replace("-","") + ".jpg";
            File file = new File(CommonConstants.MINI_PROGRAM_ACODE_PATH + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            outputStream = new FileOutputStream(file);
            // 读取到的字节组长度
            int len;
            byte[] buf = new byte[1024];
            while ((len = inputStream.read(buf)) != -1) {
                // 从buf中的0位置写入len长度
//                builder.append(new String(buf,0,len));
                outputStream.write(buf, 0, len);
            }
            outputStream.flush();
//            System.out.println("===== 返回json:" + builder.toString());
            return CommonResponse.buildSuccess(CommonConstants.DOMAIN + file.getName());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }
}
