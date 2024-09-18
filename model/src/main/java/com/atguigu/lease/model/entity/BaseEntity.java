package com.atguigu.lease.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseEntity implements Serializable {

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT) //fill属性：在插入时mp自动配置插入的内容
                                                                //插入内容需手动配置：即此时需重写mp中的insertFill方法
    @JsonIgnore
    private Date createTime;

    @Schema(description = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.UPDATE) //fill属性：在更新时mp自动创建更新的内容（更新内容需手动配置）
    @JsonIgnore
    private Date updateTime;

    @Schema(description = "逻辑删除")
    @TableField("is_deleted")
    @JsonIgnore //标注该注解将不再反馈该数据给前端
    @TableLogic //配置该注解mybatis-plus会对逻辑删除的内容进行过滤筛选
    //逻辑删除功能只对Mybatis-Plus自动注入的sql起效，也就是说，对于手动在`Mapper.xml`文件配置的sql不会生效，需要单独考虑。
    private Byte isDeleted;

}