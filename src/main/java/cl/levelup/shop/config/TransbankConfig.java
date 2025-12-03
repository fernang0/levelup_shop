package cl.levelup.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TransbankConfig {
    
    @Value("${transbank.environment}")
    private String environment;
    
    @Value("${transbank.commerce-code}")
    private String commerceCode;
    
    @Value("${transbank.api-key}")
    private String apiKey;
    
    public String getEnvironment() {
        return environment;
    }
    
    public String getCommerceCode() {
        return commerceCode;
    }
    
    public String getApiKey() {
        return apiKey;
    }
}
