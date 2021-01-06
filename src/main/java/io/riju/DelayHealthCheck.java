package io.riju;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Liveness;

import javax.enterprise.context.ApplicationScoped;

@Liveness
@ApplicationScoped  
public class DelayHealthCheck implements HealthCheck {

    long startupTimestamp = System.currentTimeMillis();
    Integer startupDelaySec = ConfigProvider.getConfig().getValue("startup.delay-sec", Integer.class);
    
    @Override
    public HealthCheckResponse call() {
    
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("Startup Delay Health Check");

        if (System.currentTimeMillis() > startupTimestamp + startupDelaySec*1000){
            responseBuilder.up();
        }

        return responseBuilder.build();
    }
}