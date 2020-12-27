package com.loaferframework.mybatisplus.basic.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.loaferframework.entity.BaseEntity;
import com.loaferframework.mybatisplus.basic.mapper.BaseMapper;

/**
 * <p>
 * 创建时间: 2020年12月22号
 * 联系方式: houchao.kang@hydosky.com
 * </p>
 *
 * @author kanghouchao
 * @since 2.0.0
 */
public class BaseServiceImpl<M extends BaseMapper<T>, T extends BaseEntity> extends ServiceImpl<M, T> implements BaseService<T> {

}
