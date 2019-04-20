package com.xuecheng.api.config.filesystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

@Api(value = "图片管理", description = "图片管理", tags = {"图片管理"})
public interface FileSystemControllerApi {

    @ApiOperation("上传文件")
    public UploadFileResult upload(MultipartFile multipartFile, String filetage, String businesskey, String metadata);
}
