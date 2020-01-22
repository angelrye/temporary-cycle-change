package com.ryan.temporarycyclechange;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @author rsapl00
 */
@Configuration
@EnableEncryptableProperties
public class Ps01JasyptConfig {

    private final static Log logger = LogFactory.getLog(Ps01JasyptConfig.class);

    @Bean(name = "encryptorBean")
    public StringEncryptor stringEncryptor() {

        if (logger.isDebugEnabled()) {
            logger.debug("Creating encryption bean for Jasypt usage.");
        }

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword("secondarypassword");
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);

        logger.info("Encrypted Password: " + encryptor.encrypt("realpassword"));

        return encryptor;
    }

}