package com.xuecheng.filesystem.controller;

import com.xuecheng.api.config.filesystem.FileSystemControllerApi;
import com.xuecheng.filesystem.service.FileSystemService;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/filesystem")
public class FileSystemController implements FileSystemControllerApi {

    @Autowired
    FileSystemService fileSystemService;

    /**
     * 上传图片
     *
     * @param multipartFile
     * @param filetage
     * @param businesskey
     * @param metadata
     * @return
     */
    @Override
    @PostMapping("/upload")
    public UploadFileResult upload(@RequestParam("file") MultipartFile multipartFile,
                                   @RequestParam(value = "filetag", required = true) String filetage,
                                   @RequestParam(value = "businesskey", required = true) String businesskey,
                                   @RequestParam(value = "metadata", required = true) String metadata) {
        return fileSystemService.upload(multipartFile, filetage, businesskey, metadata);
    }
}
