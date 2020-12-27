package com.loaferframework.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * 创建时间: 2020年11月05号
 * 联系方式: houchao.kang@hydosky.com
 * </p>
 *
 * @author kanghouchao
 * @since 2.0.0
 */
@Configuration
public class GeneralBootAutoConfiguration {

    private final static Logger logger = LoggerFactory.getLogger(GeneralBootAutoConfiguration.class);

    @Bean
    public CommandLineRunner bootedShow() {
        return (args) -> logger.info("===========================启动完成=============================");
    }
}
