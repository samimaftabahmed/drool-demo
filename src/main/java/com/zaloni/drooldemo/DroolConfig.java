package com.zaloni.drooldemo;

import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.boot.jta.bitronix.PoolingDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DroolConfig {

    private final KieServices kieServices = KieServices.Factory.get();

    private KieFileSystem getKieFileSystem() {

        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newClassPathResource("offer.drl"));
        return kieFileSystem;
    }

    @Bean
    public KieContainer getKieContainer() {

        System.out.println("Container created");
        getKieRepository();
        KieBuilder kb = kieServices.newKieBuilder(getKieFileSystem());
        kb.buildAll();
        KieModule kieModule = kb.getKieModule();
        KieContainer kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());
        return kieContainer;
    }

    private void getKieRepository() {

        final KieRepository kieRepository = kieServices.getRepository();
        kieRepository.addKieModule(new KieModule() {
            @Override
            public ReleaseId getReleaseId() {
                return kieRepository.getDefaultReleaseId();
            }
        });
    }

    @Bean
    public KieSession getKieSession() {
        System.out.println("session created");
        return getKieContainer().newKieSession();
    }

    public PoolingDataSource getPoolingDS() {
        PoolingDataSource ds = new PoolingDataSource();
        ds.setUniqueName("jdbc/BitronixJTADataSource");
        ds.setClassName("org.h2.jdbcx.JdbcDataSource");
        ds.setMaxPoolSize(3);
        ds.setAllowLocalTransactions(true);
        ds.getDriverProperties().put("user", "root");
        ds.getDriverProperties().put("password", "1234");
        ds.getDriverProperties().put("URL", "jdbc:mysql://localhost:3306/drool_demo");
        ds.init();
        return ds;
    }

}
