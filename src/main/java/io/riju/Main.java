package io.riju;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

import org.eclipse.microprofile.config.ConfigProvider;

import org.jboss.logging.Logger;


@QuarkusMain
public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class);

    public static void main(String... args) {
        Quarkus.run(MyApp.class, args);
    }

    public static class MyApp implements QuarkusApplication {

        Integer startupDelaySec = ConfigProvider.getConfig().getValue("startup.delay-sec", Integer.class);

        @Override
        public int run(String... args) throws Exception {

            LOG.info("Environment Variable STARTUP_DELAY_SEC is set to " + startupDelaySec);
            for (int i = 0; i < startupDelaySec; i++){
                LOG.info("Startup in " + (startupDelaySec - i) + " seconds");
                Thread.sleep(1000);
            }
            LOG.info("Startup done!");
            Quarkus.waitForExit();
            return 0;
        }
    }
}