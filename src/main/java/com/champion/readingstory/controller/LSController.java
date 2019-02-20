package com.champion.readingstory.controller;

import com.alibaba.fastjson.JSONObject;
import com.champion.readingstory.constant.LSReturnCode;
import com.champion.readingstory.dao.entity.User;
import com.champion.readingstory.dao.entity.UserStory;
import com.champion.readingstory.exception.BusinessException;
import com.champion.readingstory.service.RedisDao;
import com.champion.readingstory.service.UserService;
import com.champion.readingstory.service.UserStoryService;
import com.champion.readingstory.vo.MyPageVO;
import com.champion.readingstory.vo.UserVoiceVO;
import com.champion.readingstory.vo.request.WXACodeRequest;
import com.champion.readingstory.vo.response.CommonResponse;
import com.champion.readingstory.constant.CommonConstants;
import com.champion.readingstory.constant.LSAPI;
import com.champion.readingstory.util.LiuShengClient;
import com.champion.readingstory.util.WeChatClient;
import com.champion.readingstory.vo.request.BaseRequest;
import com.champion.readingstory.vo.request.PTTSRequest;
import com.champion.readingstory.vo.request.UserBindRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author: JiangPing Li
 * @date: 2018-09-04 10:04
 */
@Api(description = "留声服务接口")
@Slf4j
@Validated
@RestController
public class LSController {

    @Autowired
    private UserService userService;
    @Autowired
    private LiuShengClient client;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private UserStoryService userStoryService;
    @Autowired
    private WeChatClient weChatClient;

    /**
     * 绑定用户
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "录入用户手机号", notes = "去留声平台绑定")
    @PostMapping(path = {"/ls" + LSAPI.USER_BIND})
    public CommonResponse<?> userBind(HttpServletRequest request,
                                      @RequestBody @Valid UserBindRequest req) {
        boolean hasKey = Optional.ofNullable(redisTemplate.hasKey(String.
                format(CommonConstants.READING_STORY, req.getPhoneNo()))).orElse(Boolean.FALSE);
        if (!hasKey) {
            return CommonResponse.buildFailure("请先下发验证码");
        }
        String redisCode = (String) redisTemplate.opsForValue().get(String.format(CommonConstants.READING_STORY, req.getPhoneNo()));
        if (!req.getCode().equals(redisCode)) {
            return CommonResponse.buildFailure("验证码不正确");
        }

        String openId = (String) request.getAttribute("sessionId");

        JSONObject params = new JSONObject();
        params.put("third_user_id", openId);
        params.put("phone_no", req.getPhoneNo());
        HttpHeaders headers = new HttpHeaders();
        JSONObject jsonObject = client.commonCall(LSAPI.USER_BIND, params.toString(), headers);
        if (Objects.nonNull(jsonObject)) {
            String code = (String) jsonObject.getJSONObject("base").get("ret_code");
            String msg = (String) jsonObject.getJSONObject("base").get("ret_desc");
            if (LSReturnCode.SUCCESS.getCode().equals(code)) {
                Example example = Example.builder(User.class).build();
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("openId", openId);
                userService.updateByExampleSelective(User.builder().isBind(true).phoneNo(req.getPhoneNo()).build(), example);
                return CommonResponse.buildSuccess(null, "绑定成功");
            } else if(LSReturnCode.USER_BOUND.getCode().equals(code) || LSReturnCode.PHONE_NO_BOUND.getCode().equals(code)){
                Example example = Example.builder(User.class).build();
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("openId", openId);
                userService.updateByExampleSelective(User.builder().isBind(true).build(), example);
                return CommonResponse.buildSuccess(null, "账户已经绑定");
            }else {
                return CommonResponse.buildFailure(code, msg);
            }
        } else {
            return CommonResponse.buildFailure("留声接口返回为空!");
        }
    }

    /**
     * 获取授权token
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "获取授权token")
    @PostMapping(path = {"/ls" + LSAPI.USER_AUTHOR})
    public CommonResponse<?> userAuth(HttpServletRequest request) {
        String openId = (String) request.getAttribute("sessionId");
        Example example = Example.builder(User.class).build();
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("openId", openId);
        criteria.andIsNotNull("phoneNo");
        User user = Optional.ofNullable(userService.selectOneByExample(example))
                .orElseThrow(() -> new BusinessException("用户信息异常!"));
        if (!user.getIsBind()) {
            return CommonResponse.buildFailure("请先绑定用户!");
        }
        String token = getToken(openId);
        if (StringUtils.isBlank(token)) {
            return CommonResponse.buildFailure("授权失败!");
        } else {
            return CommonResponse.buildSuccess(token);
        }
    }

    /**
     * 语音合成
     *
     * @param request
     * @param pttsRequest
     * @return
     */
    @ApiOperation(value = "调用留声平台合成语音")
    @PostMapping(path = {"/ls" + LSAPI.PTTS})
    public CommonResponse<?> ptts(HttpServletRequest request,
                                  @RequestBody @Valid PTTSRequest pttsRequest) {
        String openId = (String) request.getAttribute("sessionId");

        Example example = Example.builder(User.class).build();
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("openId", openId);
        criteria.andIsNotNull("phoneNo");
        criteria.andEqualTo("isBind", Boolean.TRUE);
        User user = Optional.ofNullable(userService.selectOneByExample(example))
                .orElseThrow(() -> new BusinessException("请先绑定用户!"));
        String token = getToken(openId);
        if (StringUtils.isBlank(token)) {
            return CommonResponse.buildFailure("授权失败!");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Token", token);
        JSONObject jsonObject = client.pttsCall(LSAPI.PTTS, pttsRequest.getContent(), headers,pttsRequest.getVcn());
        if (Objects.nonNull(jsonObject)) {
            String code = (String) jsonObject.getJSONObject("base").get("ret_code");
            String msg = (String) jsonObject.getJSONObject("base").get("ret_desc");
            if (LSReturnCode.SUCCESS.getCode().equals(code)) {
                String audioUrl = (String) jsonObject.get("audio_url");
                return CommonResponse.buildSuccess(audioUrl);
            } else {
                return CommonResponse.buildFailure(code, msg);
            }
        } else {
            return CommonResponse.buildFailure("留声接口返回为空!");
        }
    }

    /**
     * 音库列表
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "调用留声平台获得音库列表")
    @PostMapping(path = {"/ls" + LSAPI.USER_VOICE_LIST})
    public CommonResponse<?> getUserVoiceList(HttpServletRequest request,
                                              @RequestBody BaseRequest baseRequest) {
        String openId = (String) request.getAttribute("sessionId");
        Example example = Example.builder(User.class).build();
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("openId", openId);
        criteria.andIsNotNull("phoneNo");
        criteria.andEqualTo("isBind", Boolean.TRUE);
        User user = Optional.ofNullable(userService.selectOneByExample(example))
                .orElseThrow(() -> new BusinessException("请先绑定用户!"));
        String token = getToken(openId);
        if (StringUtils.isBlank(token)) {
            return CommonResponse.buildFailure("授权失败!");
        }
        JSONObject params = new JSONObject();
        params.put("third_user_id", openId);
        params.put("offset", 0);
        params.put("count", 20);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Token", token);
        JSONObject jsonObject = client.commonCall(LSAPI.USER_VOICE_LIST, params.toString(), headers);
        if (Objects.nonNull(jsonObject)) {
            String code = (String) jsonObject.getJSONObject("base").get("ret_code");
            String msg = (String) jsonObject.getJSONObject("base").get("ret_desc");
            if (LSReturnCode.SUCCESS.getCode().equals(code)) {
                String userVoices = JSONObject.toJSONString(jsonObject.getJSONArray("user_voices"));
                List<UserVoiceVO> list = JSONObject.parseArray(userVoices, UserVoiceVO.class);
                return CommonResponse.buildSuccess(list);
            } else {
                return CommonResponse.buildFailure(code, msg);
            }
        } else {
            return CommonResponse.buildFailure("留声接口返回为空!");
        }
    }

    /**
     * 删除音库
     *
     * @param request
     * @param voiceId
     * @return
     */
    @ApiOperation(value = "删除音库")
    @GetMapping(path = {"/ls" + LSAPI.USER_VOICE_DELETE})
    public CommonResponse<?> deleteUserVoice(HttpServletRequest request,
                                             @NotBlank(message = "voiceId不能为空")
                                             @RequestParam(value = "voiceId") String voiceId) {
        String openId = (String) request.getAttribute("sessionId");
        Example example = Example.builder(User.class).build();
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("openId", openId);
        criteria.andIsNotNull("phoneNo");
        criteria.andEqualTo("isBind", Boolean.TRUE);
        User user = Optional.ofNullable(userService.selectOneByExample(example))
                .orElseThrow(() -> new BusinessException("请先绑定用户!"));
        String token = getToken(openId);
        if (StringUtils.isBlank(token)) {
            return CommonResponse.buildFailure("授权失败!");
        }
        JSONObject params = new JSONObject();
        params.put("third_user_id", openId);
        params.put("voice_id", voiceId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Token", token);
        JSONObject jsonObject = client.commonCall(LSAPI.USER_VOICE_DELETE, params.toString(), headers);
        if (Objects.nonNull(jsonObject)) {
            String code = (String) jsonObject.getJSONObject("base").get("ret_code");
            if (LSReturnCode.SUCCESS.getCode().equals(code)) {
                return CommonResponse.buildSuccess(null, "删除音库成功!");
            } else {
                return CommonResponse.buildFailure(code, (String) jsonObject.getJSONObject("base").get("ret_desc"));
            }
        } else {
            return CommonResponse.buildFailure("留声接口返回为空!");
        }
    }

    @GetMapping(path = {"/ls/get/myPage"})
    public CommonResponse<?> myPage(HttpServletRequest request) {
        String openId = (String) request.getAttribute("sessionId");
        Integer voiceCount = 0;
        Integer userStoryCount = 0;
        Boolean isBind = false;
        String token = getToken(openId);
        if (StringUtils.isNotBlank(token)) {
            JSONObject params = new JSONObject();
            params.put("third_user_id", openId);
            params.put("offset", 0);
            params.put("count", 20);
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Token", token);
            JSONObject jsonObject = client.commonCall(LSAPI.USER_VOICE_LIST, params.toString(), headers);
            if (Objects.nonNull(jsonObject)) {
                String code = (String) jsonObject.getJSONObject("base").get("ret_code");
                if (LSReturnCode.SUCCESS.getCode().equals(code)) {
                    String userVoices = JSONObject.toJSONString(jsonObject.getJSONArray("user_voices"));
                    voiceCount = JSONObject.parseArray(userVoices, UserVoiceVO.class).size();
                }
            }
        }

        User user = userService.selectOne(User.builder().openId(openId).build());
        if (user == null) {
            userService.insertSelective(User.builder().openId(openId).build());
        } else {
            if (user.getIsBind() && StringUtils.isNotBlank(user.getPhoneNo())) {
                isBind = true;
            }
            userStoryCount = userStoryService.selectByPage(UserStory.builder().userId(user.getId()).build()).size();
        }
        return CommonResponse.buildSuccess(MyPageVO.builder().voiceCount(voiceCount).isBind(isBind).userStoryCount(userStoryCount).build());
    }

    private String getToken(String openId) {
        String token = null;
        boolean hasToken = Optional.ofNullable(redisTemplate.hasKey(String.format(CommonConstants.READING_STORY, openId))).orElse(Boolean.FALSE);
        if (!hasToken) {
            JSONObject params = new JSONObject();
            params.put("third_user_id", openId);
            HttpHeaders headers = new HttpHeaders();
            JSONObject jsonObject = client.commonCall(LSAPI.USER_AUTHOR, params.toString(), headers);
            if (Objects.nonNull(jsonObject)) {
                String code = (String) jsonObject.getJSONObject("base").get("ret_code");
                if (LSReturnCode.SUCCESS.getCode().equals(code)) {
                    token = (String) jsonObject.get("token");
                    Integer expireTime = (Integer) jsonObject.get("expire_time");
                    redisTemplate.opsForValue().set(String.format(CommonConstants.READING_STORY, openId), token, expireTime - 30, TimeUnit.SECONDS);
                } else {
                    log.info("===== code:{},msg:{}", code, jsonObject.getJSONObject("base").get("ret_desc"));
                }
            }
        } else {
            token = (String) redisTemplate.opsForValue().get(String.format(CommonConstants.READING_STORY, openId));
        }
        log.debug("===== token:{}", token);
        return token;
    }

    @ApiOperation(value = "获取小程序码")
    @PostMapping(path = {"/ls/getWXACode"})
    public CommonResponse<?> getWXACode(HttpServletRequest request, @RequestBody @Valid WXACodeRequest req){
        String openId = (String) request.getAttribute("sessionId");
        String sceneKey = UUID.randomUUID().toString().replace("-","");
        String key = String.format(CommonConstants.MINI_PROGRAM_SCENE,sceneKey);
        redisTemplate.opsForValue().set(key,req.getScene(),72,TimeUnit.HOURS);
        try {
            return weChatClient.getWXACodeUnLimit(sceneKey,req.getPage());
        } catch (IOException e) {
            redisTemplate.delete(key);
            log.error("===== 获取小程序码失败,openId={},scene={},page={}",openId,sceneKey,req.getPage(),e);
            return CommonResponse.buildFailure("获取小程序码失败!");
        }
    }

    @ApiOperation(value = "获取缓存的场景参数")
    @GetMapping(path = {"/getScene"})
    public CommonResponse<?> getSceneByKey(@NotBlank(message = "场景key不能为空")
                                               @RequestParam(value = "sceneKey") String sceneKey){
        String key = String.format(CommonConstants.MINI_PROGRAM_SCENE,sceneKey);
        boolean hasKey = redisTemplate.hasKey(key);
        if(hasKey){
            String scene = (String) redisTemplate.opsForValue().get(key);
            return CommonResponse.buildSuccess(scene);
        }
        return CommonResponse.buildFailure("获取缓存场景参数失败!");
    }
}
