/*
 * Copyright 2020 the original kanghouchao.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.loaferframework.boot.env;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 创建时间: 2020年12月26号
 * 联系方式: hchkang8710@gmail.com
 * </p>
 *
 * @author kanghouchao
 * @since 2.0.0
 */
public class DefaultBootEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    /**
     * 业务自定义文件配置
     */
    public static final int DEFAULT_ORDER = ConfigFileApplicationListener.DEFAULT_ORDER + 1;

    private int order = DEFAULT_ORDER;

    private final PropertySourceLoader loader = new YamlPropertySourceLoader();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Resource path = new ClassPathResource("default-boot-conf.yml");
        try {
            List<PropertySource<?>> propertySource = this.loader.load("LoaferBootDefaultConf", path);
            propertySource.forEach(environment.getPropertySources()::addLast);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
