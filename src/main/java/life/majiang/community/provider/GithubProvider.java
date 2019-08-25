package life.majiang.community.provider;

import com.alibaba.fastjson.JSON;
import life.majiang.community.dto.AccessTokenDTO;
import life.majiang.community.dto.Githubuser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

/* 初始化到上下文*/
@Component
public class GithubProvider {
    // 如果参数过多  就封装成对象 放在dto包中

    public String getAccessToken(AccessTokenDTO accessTokenDTO){
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            System.out.println(string); // access_token=c480fc5d00f19d4bb3080298fe3e419b1e13aa1a&scope=user&token_type=bearer
            // 拆分  access_token
            String[] split = string.split("&");
            String tokenStr = split[0];
            String token = tokenStr.split("=")[1];

            return token;
        } catch (Exception e){
            e.printStackTrace();

        }
        return null;


       // https://api.github.com/user?access_token=24adbca9639bca019a95b51df188ad7226ae279e

    }

    public Githubuser getUser(String accessToken){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token=" + accessToken)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            Githubuser githubuser = JSON.parseObject(string, Githubuser.class);
            return githubuser;
        } catch (IOException e){
        }
        return null;


    }
}
