package com.xuecheng.auth.service;

import com.alibaba.fastjson.JSON;
import com.netflix.discovery.converters.Auto;
import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.exception.ExceptionCast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author Czz
 * @Description TODO
 * @Date 2019-05-25 11:11
 * @Version 1.0
 */
@Service
public class AuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    @Value("${auth.tokenValiditySeconds}")
    int tokenValiditySeconds;

    @Autowired
    LoadBalancerClient loadBalancerClient;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 用户认证申请令牌
     *
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @return
     */
    public AuthToken login(String username, String password, String clientId, String clientSecret) {
        // 申请令牌
        AuthToken authToken = applyToken(username, password, clientId, clientSecret);
        if (authToken == null) {
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_APPLYTOKEN_FAIL);
        }
        String access_token = authToken.getAccess_token();
        String content = JSON.toJSONString(authToken);
        // 存储到redis
        boolean saveRedis = saveRedis(access_token, content, tokenValiditySeconds);
        if (!saveRedis) {
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_TOKEN_SAVEFAIL);
        }
        return authToken;
    }

    /**
     * 申请令牌
     *
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @return
     */
    private AuthToken applyToken(String username, String password, String clientId, String clientSecret) {
        // 从eureka中获取认证服务的地址（因为spring security在认证服务中）
        // 从eureka中获取认证服务的一个实例地址
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        // 此地址是http://ip:prot
        URI uri = serviceInstance.getUri();
        String authUrl = uri + "/auth/oauth/token";

        // 1.设置请求的header
        LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        String httpBasic = getHttpBasic(clientId, clientSecret);
        header.add("Authorization", httpBasic);

        // 设置请求的body
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("username", username);
        body.add("password", password);
        body.add("grant_type", "password");

        HttpEntity<MultiValueMap<String, String>> multiValueMapHttpEntity = new HttpEntity<>(body, header);
        // 设置远程请求返回400或401时不报错
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401) {
                    super.handleError(response);
                }
            }
        });
        // 发起请求
        ResponseEntity<Map> exchange = restTemplate.exchange(authUrl, HttpMethod.POST, multiValueMapHttpEntity, Map.class);
        // 获取申请令牌的信息
        Map bodyMap = exchange.getBody();
        if (bodyMap == null
                || bodyMap.get("access_token") == null
                || bodyMap.get("refresh_token") == null
                // jti是jwt令牌的唯一标识作为用户身份令牌
                || bodyMap.get("jti") == null) {
            return null;
        }
        AuthToken authToken = new AuthToken();
        // jwt令牌
        authToken.setAccess_token((String) bodyMap.get("access_token"));
        // 刷新令牌
        authToken.setAccess_token((String) bodyMap.get("refresh_token"));
        // jti令牌
        authToken.setAccess_token((String) bodyMap.get("jti"));
        return authToken;
    }

    /**
     * 存储到redis
     *
     * @param access_token twitoken
     * @param content      令牌内容
     * @param tt1          过期实践
     * @return
     */
    private boolean saveRedis(String access_token, String content, long tt1) {
        String key = "user_token" + access_token;
        stringRedisTemplate.boundValueOps(key).set(content, tt1, TimeUnit.SECONDS);
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return expire > 0;
    }

    /**
     * 获取httpbasic的串
     *
     * @param clientId
     * @param clientSecret
     * @return
     */
    private String getHttpBasic(String clientId, String clientSecret) {
        // 将客户端的id和客户端密码拼接，按"客户端id:客户端密码"
        String str = clientId + ":" + clientSecret;
        // 进行base64编码
        byte[] encode = Base64Utils.encode(str.getBytes());
        return "Basic " + new String(encode);

    }

}
