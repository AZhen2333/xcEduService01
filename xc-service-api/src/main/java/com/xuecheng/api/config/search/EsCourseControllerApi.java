package com.xuecheng.api.config.search;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;

/**
 * @Author Czz
 * @Description TODO
 * @Date 2019-05-02 15:50
 * @Version 1.0
 */
@Api(value = "课程搜索",description = "课程搜索",tags = {"课程搜索"})
public interface EsCourseControllerApi {
    @ApiOperation("课程搜索")
    public QueryResponseResult list(int page, int size,
                                               CourseSearchParam courseSearchParam) throws IOException;
}
