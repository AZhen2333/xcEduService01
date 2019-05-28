package com.xuecheng.auth;

import com.xuecheng.framework.client.XcServiceList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * @Author Czz
 * @Description TODO
 * @Date 2019-05-22 17:32
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestClient {

    @Autowired
    LoadBalancerClient loadBalancerClient;

    @Autowired
    RestTemplate restTemplate;


    @Test
    public void testJWTClient() {
        // 从eureka中获取认证服务的地址（因为spring security在认证服务中）
        // 从eureka中获取认证服务的一个实例地址
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        // 此地址是http://ip:prot
        URI uri = serviceInstance.getUri();
        String authUrl = uri + "/auth/oauth/token";

        // 1.设置请求的header
        LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        String httpBasic = getHttpBasic("XcWebApp", "XcWebApp");
        header.add("Authorization", httpBasic);

        // 设置请求的body
        LinkedMultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("username", "");
        bodyMap.add("password", "");
        bodyMap.add("grant_type", "password");

        HttpEntity<MultiValueMap<String, String>> multiValueMapHttpEntity = new HttpEntity<>(bodyMap, header);
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
        Map body = exchange.getBody();
    }

    /**
     * @param clientId
     * @param clientSecret
     * @return
     */
    private String getHttpBasic(String clientId, String clientSecret) {
        String str = clientId + ":" + clientSecret;
        // 将字符串进行base64编码
        byte[] encode = Base64Utils.encode(str.getBytes());
        return "Basic " + new String(encode);
    }

    public void testClient() {
        // 采用客户端负载均衡，从eureka获取认证服务的ip和端口
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        URI uri = serviceInstance.getUri();

        String authUrl = uri + "/auth/oauth/token";
        // 请求的内容分两部分
        // 1.heard信息，包括了http basic认证信息
        LinkedMultiValueMap<String, String> heards = new LinkedMultiValueMap<>();
        String httpbasic = httpbasic("XcWebApp", "XcWebApp");
        heards.add("Authorization", httpbasic);

    }

    /**
     * 获取httpbasic的串
     *
     * @param clientId
     * @param clientSecret
     * @return
     */
    private String httpbasic(String clientId, String clientSecret) {
        // 将客户端的id和客户端密码拼接，按"客户端id:客户端密码"
        String str = clientId + ":" + clientSecret;
        // 进行base64编码
        byte[] encode = Base64Utils.encode(str.getBytes());
        return "Basic " + new String(encode);

    }


}
