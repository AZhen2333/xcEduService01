package com.xuecheng.manage_course.client;

import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author Czz
 * @Description cms系统远程调用接口
 * @Date 2019-04-21 11:32
 * @Version 1.0
 */
@FeignClient(value = XcServiceList.XC_SERVICE_MANAGE_CMS) // 用于创建声明是API接口，该接口是RESTful风格的
public interface CmsPageClient {

    @GetMapping("/cms/page/get/{id}")
    public CmsPage findById(@PathVariable("id") String id);
}
