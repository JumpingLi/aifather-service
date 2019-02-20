package com.champion.readingstory.controller;

import com.alibaba.fastjson.JSONObject;
import com.champion.readingstory.dao.entity.Story;
import com.champion.readingstory.dao.entity.User;
import com.champion.readingstory.dao.entity.UserStory;
import com.champion.readingstory.service.StoryService;
import com.champion.readingstory.service.UserService;
import com.champion.readingstory.service.UserStoryService;
import com.champion.readingstory.util.DateTimeUtil;
import com.champion.readingstory.util.SmsClient;
import com.champion.readingstory.util.WeChatClient;
import com.champion.readingstory.vo.StoryVO;
import com.champion.readingstory.vo.UserStoryVO;
import com.champion.readingstory.vo.UserVO;
import com.champion.readingstory.vo.response.CommonResponse;
import com.champion.readingstory.vo.response.SmsResponse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.champion.readingstory.constant.CommonConstants;
import com.champion.readingstory.vo.request.BaseRequest;
import com.champion.readingstory.vo.request.StoryRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Pattern;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author: JiangPing Li
 * @date: 2018-08-17 15:20
 */
@Api(description = "AI讲故事小程序接口")
@Slf4j
@Validated
@RestController
@RequestMapping(path = {"/story"})
public class RootController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private SmsClient smsClient;
    @Autowired
    private StoryService storyService;
    @Autowired
    private UserStoryService userStoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private WeChatClient weChatClient;


    /**
     * @return
     */
    @RequestMapping(path = {"/test"}, method = RequestMethod.GET)
    public CommonResponse<?> test(HttpServletRequest request) {
        return CommonResponse.buildSuccess(getId(request));
    }


    private String getId(HttpServletRequest request){
        String ip = request.getHeader("X-Forwarded-For");

        if(!org.springframework.util.StringUtils.isEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if(index != -1){
                return ip.substring(0,index);
            }else{
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if(!org.springframework.util.StringUtils.isEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
            return ip;
        }
        return request.getRemoteAddr();
    }

    @GetMapping(path = {"/get/openId/{code}"})
    public CommonResponse<?> getOpenIdByCode(@PathVariable String code){
        JSONObject jsonObject = weChatClient.getOpenId(code);
        log.info("=== openId: " + jsonObject.toJSONString());
        String errCode = jsonObject.getString("errcode");
        String errMsg = jsonObject.getString("errmsg");
        if (StringUtils.isNotBlank(errCode) || StringUtils.isNotBlank(errMsg)){
            return CommonResponse.buildFailure(errCode + "-" + errMsg);
        }
        String openId = jsonObject.getString("openid");
        return CommonResponse.buildSuccess(openId);
    }
    @ApiOperation(value="检查用户绑定", notes="检查用户绑定")
    @GetMapping(path = {"/check/bind"})
    public CommonResponse<?> registerUser(HttpServletRequest request){
        String openId = (String) request.getAttribute("sessionId");
        User user = userService.selectOne(User.builder().openId(openId).build());
        if (user == null){
            userService.insertSelective(User.builder().openId(openId).build());
        }else{
            if (user.getIsBind() && StringUtils.isNotBlank(user.getPhoneNo())){
                return CommonResponse.buildSuccess(UserVO.builder()
                        .openId(openId).phoneNo(user.getPhoneNo()).isBind(true).build());
            }
        }
        return CommonResponse.buildSuccess(UserVO.builder().openId(openId).isBind(false).build());
    }

    /**
     * 下发验证码
     * @param request
     * @param mobile
     * @return
     */
    @ApiOperation(value="获取验证码", notes = "获取验证码")
    @GetMapping(path = {"/sms/code/{mobile}"})
    public CommonResponse<?> getSmsCode(HttpServletRequest request,
                                        @Pattern(regexp = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$",message = "手机号码格式不正确")
                                        @PathVariable String mobile){
        boolean hasKey = Optional.ofNullable(redisTemplate.hasKey(String.format(CommonConstants.READING_STORY, mobile))).orElse(Boolean.FALSE);
        if (hasKey) {
            return CommonResponse.buildSuccess(null,"验证码已下发,请注意查收");
        }
        String code = RandomStringUtils.randomNumeric(6);
        SmsResponse response = smsClient.sendSms(code, mobile);
        if (CommonConstants.SMS_SUCCESS_CODE.equals(response.getReturnCode())){
            redisTemplate.opsForValue().set(String.format(CommonConstants.READING_STORY, mobile), code, 5, TimeUnit.MINUTES);
            return CommonResponse.buildSuccess(null,"短信验证码下发成功!");
        }else{
            return CommonResponse.buildFailure(response.getReturnCode(),response.getReturnDesc());
        }
    }

    /**
     * 故事列表
     * @param request
     * @return
     */
    @ApiOperation(value="获取故事列表", notes = "获取故事列表")
    @PostMapping(path = {"/list"})
    public CommonResponse<?> getStoryList(HttpServletRequest req,@RequestBody StoryRequest request) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize(),"update_at desc");
        Story storyQuery = Story.builder().build();
        BeanUtils.copyProperties(request,storyQuery);

        Example example = Example.builder(Story.class).build();
        Example.Criteria criteria = example.createCriteria();
        if (!Objects.isNull(request.getAgeSection())) {
            criteria.andEqualTo("ageSection",request.getAgeSection());
        }
        if (!Objects.isNull(request.getCategory())){
            criteria.andEqualTo("category",request.getCategory());
        }
        List<Story> list = storyService.
//                selectByPage(storyQuery);
                selectByExample(example);
        PageInfo page = PageInfo.of(list);
        List<StoryVO> voList = Lists.newLinkedList();
        voList.addAll(list.stream().sorted(Comparator.comparing(Story::getUpdateAt).reversed()).map(story -> StoryVO.builder()
                .id(story.getId())
                .ageSection(story.getAgeSection().name())
                .category(story.getCategory().name())
                .title(story.getTitle())
                .subtitle(story.getSubtitle())
                .imageUrl(story.getImageUrl())
                .createAt(DateTimeUtil.formatLocalDate(story.getCreateAt()))
                .updateAt(DateTimeUtil.formatLocalDate(story.getUpdateAt()))
                .build()).collect(Collectors.toList()));

        page.setList(voList);
        return CommonResponse.buildSuccess(page);
    }
    @ApiOperation(value="获取故事详情")
    @PostMapping(path = {"/detail"})
    public CommonResponse<?> getStoryDetail(HttpServletRequest req,@RequestBody StoryRequest request) {
        Story storyQuery = Story.builder().build();
        BeanUtils.copyProperties(request,storyQuery);
        storyQuery.setId(null);
        List<Story> list = storyService.selectByPage(storyQuery);
        Set<Integer> ids = list.stream().map(Story::getId).collect(Collectors.toSet());
        Story queryStory = list.stream().filter(story -> story.getId().equals(request.getId()))
                .findFirst().orElse(null);
        if (queryStory == null){
            return CommonResponse.buildFailure("数据不存在");
        }
        StoryVO storyVO = StoryVO.builder().build();
        BeanUtils.copyProperties(queryStory,storyVO);
        storyVO.setIds(ids);
        return CommonResponse.buildSuccess(storyVO);
    }

    /**
     * 已读故事列表
     * @param request
     * @return
     */
    @ApiOperation(value="已读故事列表")
    @PostMapping(path = {"/userStory/list"})
    public CommonResponse<?> getUserStoryList(HttpServletRequest req,@RequestBody BaseRequest request){
        String openId = (String) req.getAttribute("sessionId");
        User user = userService.selectOne(User.builder().openId(openId).build());
        if (user == null){
            return CommonResponse.buildFailure("用户不存在");
        }
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        UserStory storyQuery = UserStory.builder().userId(user.getId()).build();
        List<UserStory> list = userStoryService.selectByPage(storyQuery);
        PageInfo page = PageInfo.of(list);
        Map<Integer,Timestamp> updateAtMap = list.stream()
                .collect(Collectors.toMap(UserStory::getStoryId,UserStory::getUpdateAt));
        List<Integer> storyIds = list.stream()
                .sorted(Comparator.comparing(UserStory::getCreateAt))
                .map(UserStory::getStoryId).collect(Collectors.toList());
        List<UserStoryVO> voList = Lists.newLinkedList();
        if (!CollectionUtils.isEmpty(storyIds)){
            Example example = Example.builder(Story.class).build();
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("id", storyIds);
            List<Story> storyList = storyService.selectByExample(example);
            voList.addAll(storyList.stream().map(story -> UserStoryVO.builder()
                    .id(story.getId())
                    .title(story.getTitle())
                    .imageUrl(story.getImageUrl())
                    .updateAt(DateTimeUtil.formatLocalDate(updateAtMap.get(story.getId())))
                    .build()).collect(Collectors.toList()));
        }
        page.setList(voList);
        return CommonResponse.buildSuccess(page);
    }
}
