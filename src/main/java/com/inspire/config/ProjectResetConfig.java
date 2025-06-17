package com.inspire.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;

@Configuration
public class ProjectResetConfig {
    
    @Bean
    public PlatformTransactionManager projectResetTransactionManager(
            EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
    
    @Bean
    public ProjectResetProperties projectResetProperties() {
        ProjectResetProperties properties = new ProjectResetProperties();
        properties.setCompletedStatus("COMPLETED");
        properties.setErrorFlag('E');
        properties.setPendingFlag('P');
        properties.setForcedCallbackMessage("Forced om callback");
        return properties;
    }
    
    public static class ProjectResetProperties {
        private String completedStatus;
        private Character errorFlag;
        private Character pendingFlag;
        private String forcedCallbackMessage;
        
        public String getCompletedStatus() { return completedStatus; }
        public void setCompletedStatus(String completedStatus) { this.completedStatus = completedStatus; }
        
        public Character getErrorFlag() { return errorFlag; }
        public void setErrorFlag(Character errorFlag) { this.errorFlag = errorFlag; }
        
        public Character getPendingFlag() { return pendingFlag; }
        public void setPendingFlag(Character pendingFlag) { this.pendingFlag = pendingFlag; }
        
        public String getForcedCallbackMessage() { return forcedCallbackMessage; }
        public void setForcedCallbackMessage(String forcedCallbackMessage) { this.forcedCallbackMessage = forcedCallbackMessage; }
    }
}