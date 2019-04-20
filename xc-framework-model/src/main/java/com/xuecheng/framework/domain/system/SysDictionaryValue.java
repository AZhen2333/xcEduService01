package com.xuecheng.framework.domain.system;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Created by admin on 2018/2/6.
 */
@Data
@ToString
public class SysDictionaryValue {

    /**
     * 项目id
     */
    @Field("sd_id")
    private String sdId;

    /**
     * 项目名称
     */
    @Field("sd_name")
    private String sdName;

    /**
     * 项目状态（1：可用，0不可用）
     */
    @Field("sd_status")
    private String sdStatus;

}
