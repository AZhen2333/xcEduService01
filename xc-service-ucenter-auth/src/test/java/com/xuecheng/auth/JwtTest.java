package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Czz
 * @Description TODO
 * @Date 2019-05-22 16:44
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class JwtTest {

    /**
     * 生成jwt令牌
     */
    @Test
    public void testCreateJwt() {
        // 证书文件
        String key_location = "xc.keystore";
        // 密钥库密码
        String keystore_password = "xuechengkeystore";
        // 访问证书路径
        ClassPathResource resource = new ClassPathResource(key_location);
        // 密钥工厂
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource, keystore_password.toCharArray());
        // 密钥的密码，此密码和别名要匹配
        String keypassword = "xuecheng";
        // 密钥别名
        String alias = "xckey";
        // 密钥对（密钥和公钥）
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias, keypassword.toCharArray());
        // 私钥
        RSAPrivateKey aPrivate = (RSAPrivateKey) keyPair.getPrivate();
        // 定义payload信息
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("id", "123");
        tokenMap.put("name", "mry");
        tokenMap.put("roles", "r01,r02");
        tokenMap.put("ext", "1");
        // 生成jwt令牌
        // 取出jwt令牌
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(tokenMap), new RsaSigner(aPrivate));
        String token = jwt.getEncoded();
        System.out.println("token=" + token);

    }

    /**
     * 校验jwt令牌
     */
    @Test
    public void testVerify() {
        // jwt令牌
        String token = "";
        // 公钥
        String publickey = "";
        // 校验jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));
        //获取jwt原始内容
        String claims = jwt.getClaims();
        // jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);

    }

    public void test(){
        String password = "111111";
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        for (int i = 0; i <10; i++) {
            String hashPass = passwordEncoder.encode(password);
            System.out.println(hashPass);
        }
    }
}
