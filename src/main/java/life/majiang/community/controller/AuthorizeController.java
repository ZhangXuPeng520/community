package life.majiang.community.controller;



import life.majiang.community.dto.AccessTokenDTO;
import life.majiang.community.dto.Githubuser;
import life.majiang.community.mapper.UserMapper;
import life.majiang.community.model.User;
import life.majiang.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;


@Controller
public class AuthorizeController {


    @Autowired
    private GithubProvider githubProvider;

    /*注入配置文件中的设置变量*/
    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.client.uri}")
    private String clientUri;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletRequest request){


        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setState(state);
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setRedirect_uri(clientUri);
        accessTokenDTO.setClient_secret(clientSecret);

        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        Githubuser githubuser = githubProvider.getUser(accessToken);
        if (githubuser != null){
            User user = new User();
            user.setToken(UUID.randomUUID().toString());
            user.setName(githubuser.getLogin());
            user.setAccountId(String.valueOf(githubuser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
            
            // 登录成功，写cookie 和 session
            request.getSession().setAttribute("user",githubuser);
            return "redirect:/";
        }else {
            // 登录视频 重新登录
            return "redirect:/";
        }
    }
}
