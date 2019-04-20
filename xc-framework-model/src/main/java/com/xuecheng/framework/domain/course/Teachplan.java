package com.xuecheng.framework.domain.course;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by admin on 2018/2/7.
 */
@Data
@ToString
@Entity
@Table(name = "teachplan")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class Teachplan implements Serializable {
    private static final long serialVersionUID = -916357110051689485L;
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String id;
    /**
     * 父级名称
     */
    private String pname;
    /**
     * 父id
     */
    private String parentid;
    /**
     * 课程类型：1视频、2文档
     */
    private String grade;
    /**
     * 章节及课程时介绍
     */
    private String ptype;
    /**
     * 章节及课时介绍
     */
    private String description;
    /**
     * 课程id
     */
    private String courseid;
    /**
     * 状态：未发布、已发布
     */
    private String status;
    /**
     * 排序字段
     */
    private Integer orderby;
    /**
     * 时长，单位分钟
     */
    private Double timelength;
    /**
     * 是否试学
     */
    private String trylearn;

}
