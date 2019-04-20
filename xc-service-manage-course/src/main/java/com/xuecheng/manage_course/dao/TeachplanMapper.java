package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;

public interface TeachplanMapper {
    public TeachplanNode selectList(String courseId);
}
