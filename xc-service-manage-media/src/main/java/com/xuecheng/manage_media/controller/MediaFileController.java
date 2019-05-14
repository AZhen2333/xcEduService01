package com.xuecheng.manage_media.controller;

import com.xuecheng.api.config.media.MediaFileControllerApi;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.manage_media.service.MediaFileService;
import com.xuecheng.manage_media.service.MediaUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/media/file")
public class MediaFileController implements MediaFileControllerApi {

    @Autowired
    MediaFileService mediaFileService;

    @Autowired
    MediaUploadService mediaUploadService;

    @Override
    public QueryResponseResult findList(int page, int size, QueryMediaFileRequest queryMediaFileRequest) {
        //媒资文件查询
        return mediaFileService.findList(page, size, queryMediaFileRequest);
    }
}
