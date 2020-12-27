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

package com.loaferframework.jackson;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

/**
 * <p>
 * 创建时间: 2020年12月27号
 * 联系方式: hchkang8710@gmail.com
 * </p>
 *
 * @author kanghouchao
 * @since 2.0.0
 */
@Configuration
@AutoConfigureBefore(JacksonAutoConfiguration.class)
public class ChineseObjectMapperConfiguration {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return new ZeroSkyStandardJackson2ObjectMapperBuilderCustomizer();
    }

    static final class ZeroSkyStandardJackson2ObjectMapperBuilderCustomizer
            implements Jackson2ObjectMapperBuilderCustomizer, Ordered {

        @Override
        public void customize(Jackson2ObjectMapperBuilder builder) {
            builder.timeZone(TimeZone.getTimeZone(ZoneId.of("UTC+8")));
            builder.locale(Locale.CHINA);
        }

        @Override
        public int getOrder() {
            return 1;
        }
    }
}
