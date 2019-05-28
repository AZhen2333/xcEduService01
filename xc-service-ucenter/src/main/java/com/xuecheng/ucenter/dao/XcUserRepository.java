package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author Czz
 * @Description TODO
 * @Date 2019-05-25 16:25
 * @Version 1.0
 */
public interface XcUserRepository extends JpaRepository<XcUser, String> {

    /**
     * 根据手机号查询用户信息
     *
     * @param username
     * @return
     */
    XcUser findXcUserByUsername(String username);
}
