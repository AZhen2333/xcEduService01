package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.dao.XcCompanyUserRepository;
import com.xuecheng.ucenter.dao.XcUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author Czz
 * @Description TODO
 * @Date 2019-05-25 16:27
 * @Version 1.0
 */
public class UserService {

    @Autowired
    private XcUserRepository xcUserRepository;

    @Autowired
    private XcCompanyUserRepository xcCompanyUserRepository;

    /**
     * 根据用户账号查询用户信息
     *
     * @param username
     * @return
     */
    public XcUser findXcUserByUsername(String username) {
        return xcUserRepository.findXcUserByUsername(username);
    }

    /**
     * 根据账号查询用户的信息，返回用户扩展信息
     *
     * @param username
     * @return
     */
    public XcUserExt getUserExt(String username) {
        XcUser xcUser = this.findXcUserByUsername(username);
        if (xcUser == null) {
            return null;
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser, xcUserExt);
        // 用户id
        String userId = xcUserExt.getId();
        // 查询用户所属公司
        XcCompanyUser xcCompanyUser = xcCompanyUserRepository.findByUserId(userId);
        if (xcCompanyUser != null) {
            String companyId = xcCompanyUser.getCompanyId();
            xcUserExt.setCompanyId(companyId);
        }
        return xcUserExt;
    }
}
