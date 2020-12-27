package com.loaferframework.entity;

import org.springframework.lang.NonNull;

import java.io.Serializable;

/**
 * <p>
 * 基础实体类
 * </p>
 * <p>
 * 创建时间: 2020年12月19号
 * 联系方式: hchkang8710@gmail.com
 * </p>
 *
 * @author kanghouchao
 * @since 2.0.0
 */
public abstract class BaseEntity implements Serializable {

    /**
     * 获取数据主键
     *
     * @return 主键
     */
    @NonNull
    public abstract Serializable getId();

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "id='" + this.getId() + '\'' +
                '}';
    }

    /**
     * 一般情况下判断id是否相等，id为空的情况下判断地址码
     *
     * @param entity 目标对象
     * @return 是否相等
     */
    @Override
    public boolean equals(Object entity) {
        if (this == entity) {
            return true;
        }
        if (!(entity instanceof BaseEntity)) {
            return false;
        }
        return this.getId().equals(((BaseEntity) entity).getId());
    }

    /**
     * 取id的哈希码
     *
     * @return id哈希值
     */
    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

}
