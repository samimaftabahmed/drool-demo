package com.zaloni.drooldemo;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.Persistence;

@Configuration
public class DroolConfig {

    public static long KIE_SESSION_ID;
    private final KieServices kieServices = KieServices.Factory.get();

    @Bean(name = "kieSession")
    public KieSession getKieSession() {

        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newClassPathResource("offer.drl"));
        getKieRepository();
        KieBuilder kb = kieServices.newKieBuilder(kieFileSystem);
        kb.buildAll();

        KieModule kieModule = kb.getKieModule();
        KieContainer kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());

        return kieContainer.newKieSession();
    }

    @Bean(name = "persistentSession")
    public KieSession getPersistentKieSession() {

        Environment env = kieServices.newEnvironment();
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, Persistence.createEntityManagerFactory("emf-name"));
        env.set(EnvironmentName.TRANSACTION_MANAGER, TransactionManagerServices.getTransactionManager());

        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newClassPathResource("offer.drl"));
        getKieRepository();
        KieBuilder kb = kieServices.newKieBuilder(kieFileSystem);
        kb.buildAll();

        KieModule kieModule = kb.getKieModule();
        KieContainer kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());
        KieBase kieBase = kieContainer.getKieBase("rules");
        KieSession kieSession = kieServices.getStoreServices().newKieSession(kieBase, null, env);
        KIE_SESSION_ID = kieSession.getIdentifier();
        return kieSession;
    }

    @Bean
    public PoolingDataSource getPoolingDS() {
        PoolingDataSource ds = new PoolingDataSource();
        ds.setUniqueName("jdbc/BitronixJTADataSource");
        ds.setClassName("org.h2.jdbcx.JdbcDataSource");
        ds.setMaxPoolSize(3);
        ds.setAllowLocalTransactions(true);
        ds.getDriverProperties().put("user", "sa");
        ds.getDriverProperties().put("password", "");
        ds.getDriverProperties().put("URL", "jdbc:h2:mem:mydb");
        ds.init();
        return ds;
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

}
