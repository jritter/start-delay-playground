package io.riju;

import java.lang.management.ManagementFactory;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Liveness;

@Liveness
@ApplicationScoped  
public class DelayHealthCheck implements HealthCheck {

    Integer startupDelaySec = ConfigProvider.getConfig().getValue("startup.delay-sec", Integer.class);
    
    @Override
    public HealthCheckResponse call() {
    
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("Startup Delay Health Check");

        if (ManagementFactory.getRuntimeMXBean().getUptime() > startupDelaySec*1000){
            responseBuilder.up();
        }

        return responseBuilder.build();
    }
}