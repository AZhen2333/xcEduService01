package com.xuecheng.manage_cms.controller;

import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.PageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

@Controller
public class CmsPagePreviewController extends BaseController {

    @Autowired
    PageService pageService;

    /**
     * 页面预览
     *
     * @param pageId
     */
    public void perview(@PathVariable("pageId") String pageId) {
        response.setHeader("Content‐type", "text/html;charset=utf‐8");
        String pagrHtml = pageService.getPageHtml(pageId);
        if (StringUtils.isNotEmpty(pagrHtml)) {
            try {
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(pagrHtml.getBytes("UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
