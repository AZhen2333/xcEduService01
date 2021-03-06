package com.xuecheng.api.config.media;

import com.sun.org.apache.xml.internal.security.algorithms.implementations.IntegrityHmac;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.PrinterGraphics;

@Api(value = "媒资管理接口", description = "媒资管理接口，提供文件上传，文件处理等接口")
public interface MediaUploadControllerApi {

    @ApiOperation("文件上传注册")
    public ResponseResult register(String fileMd5, String fileName, String fileSize, String mimetype, String fileExt);

    @ApiOperation("检查的分块")
    public CheckChunkResult checkchunk(String fileMd5, String chunk, Integer chunkSize);

    @ApiOperation("'上传分块")
    public ResponseResult uploadchunk(MultipartFile file, String chunk, String fileMd5);

    @ApiOperation("合并文件")
    public ResponseResult mergechunks(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt);


}
