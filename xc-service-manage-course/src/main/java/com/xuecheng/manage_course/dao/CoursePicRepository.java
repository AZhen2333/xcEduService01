package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CoursePic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoursePicRepository extends JpaRepository<CoursePic, String> {

    /**
     * 删除图片并返回影响条数
     *
     * @param courseId
     * @return
     */
    long deleteByCourseid(String courseId);
}
