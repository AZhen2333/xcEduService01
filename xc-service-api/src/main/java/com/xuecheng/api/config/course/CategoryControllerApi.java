package com.xuecheng.api.config.course;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import static com.sun.corba.se.spi.activation.IIOP_CLEAR_TEXT.value;

@Api(value = "课程分类管理", description = "课程分类管理", tags = {"课程分类管理"})
public interface CategoryControllerApi {

    @ApiOperation("查询分类")
    public CategoryNode findList();
}
