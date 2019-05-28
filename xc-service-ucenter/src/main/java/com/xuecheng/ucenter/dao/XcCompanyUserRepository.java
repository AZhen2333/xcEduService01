package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author Czz
 * @Description TODO
 * @Date 2019-05-25 16:26
 * @Version 1.0
 */
public interface XcCompanyUserRepository extends JpaRepository<XcCompanyUser, String> {
    //根据用户id查询所属企业id
    XcCompanyUser findByUserId(String userId);
}
