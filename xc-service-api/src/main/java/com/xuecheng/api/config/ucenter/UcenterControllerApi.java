package com.xuecheng.api.config.ucenter;

import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import io.swagger.annotations.Api;

/**
 * @Author Czz
 * @Description TODO
 * @Date 2019-05-25 16:24
 * @Version 1.0
 */
@Api(value = "用户中心", description = "用户中心管理")
public interface UcenterControllerApi {
    public XcUserExt getUserext(String username);
}
