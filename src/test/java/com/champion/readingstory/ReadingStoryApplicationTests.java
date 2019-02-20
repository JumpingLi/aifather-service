package com.champion.readingstory;

import com.champion.readingstory.service.RedisDao;
import com.champion.readingstory.service.UserService;
import com.champion.readingstory.util.WeChatClient;
import com.champion.readingstory.vo.response.CommonResponse;
import com.champion.readingstory.filter.SecurityFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.io.IOException;
import java.util.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReadingStoryApplicationTests {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private WeChatClient weChatClient;
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("UTF-8"), "/*")
                .addFilter(new SecurityFilter(), "/*")
                .alwaysExpect(status().isOk())
                .alwaysExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .build();
    }

    @Test
    public void testGet() throws Exception {
        String url = "/story/list";
        String responseString = mockMvc.perform(get(url)
//                        .requestAttr("sessionId", "abc")
                        .header("openId", "odxQd0RTLne-hxk2VcEMaFQYUMHA")
        )
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
    }

    @Test
    public void testPost() throws Exception {
        String url = "/story/list";
        String json = "{}";
        String responseString = mockMvc.perform(post(url,json)
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(json.getBytes("UTF-8"))
                        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
//                        .requestAttr("sessionId", "abc")
                        .header("openId", "odxQd0RTLne-hxk2VcEMaFQYUMHA")
        )
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
    }

    @Test
    public void testService() throws IOException {

//        User user = User.builder().id(1).isBind(false).openId("123").phoneNo("321")
//                .createAt(Timestamp.valueOf(LocalDateTime.now()))
//                .updateAt(Timestamp.valueOf(LocalDateTime.now())).build();
//        redisDao.set("reading-story-object",user);
//        List<User> users = Collections.singletonList(user);
//        redisDao.addList("reading-story-list",users);
//        List<User> userList = redisDao.getList("reading-story-list");
//        userList.forEach(user1 -> System.out.println(JSON.toJSONString(user1)));

//        redisTemplate.opsForValue().set("jumping-string","hello world");
//        User object = (User) redisTemplate.opsForValue().get("reading-story-object");
//        System.out.println(JSON.toJSONString(object));
//        String access_token = weChatClient.getAccessToken();
        String access_token = "{\"access_token\":\"13_mw-xMLtaR9jk7jjLB5MTNgYqTiqu-dgMOQXp0mpTxSsexkKCGB6JWvA-wVP_93JHBcneLEeBKZwSkgfKrx8oLGfkkK8EmYO9-aW0HrsoB-rSfnmSZGVnQ2pYZwgXGOcAEANWE\",\"expires_in\":7200}";
//        System.out.println("===== : " + access_token);
//        JSONObject jsonObject = new JSONObject(access_token);
//        System.out.println(jsonObject.getString("access_token"));
//        JsonObject jsonObject = (JsonObject) new JsonParser().parse(access_token);
//        System.out.println(jsonObject.get("access_token"));
        CommonResponse response = weChatClient.getWXACodeUnLimit(UUID.randomUUID().toString().replace("-",""),"packageStory/pages/tellstory/tellstory");
        System.out.println(response.getData());
    }
}
