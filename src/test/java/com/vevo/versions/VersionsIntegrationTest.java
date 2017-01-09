package com.vevo.versions;

import com.vevo.genesis.Genesis;
import com.vevo.genesis.IntegrationTest;
import com.vevo.genesis.bootstrap.cli.CommandLineParser;
import com.vevo.genesis.bootstrap.cli.ParsedCommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Preconditions.checkState;


/**
 * This is the base integration test that your tests will call to verify that the container has been started.
 */
public class VersionsIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(VersionsIntegrationTest.class);

    private static final AtomicReference<Genesis> genesis = new AtomicReference<>();


    protected static Genesis verifyStarted() {
        if (genesis.get() != null) {
            return genesis.get();
        }

        System.getenv().entrySet().forEach(set -> log.info(set.getKey() + "=" + set.getValue().length()));

        String config = System.getProperty("config");

        checkState(config != null, "Oops, you forgot to add a -Dconfig=<path> param!");

        ParsedCommandLine cl = CommandLineParser.parse(config);

        //this block replicates whats in your NedService but can be centralized in a module (which is preferred)
        Genesis genesis = new Genesis.Builder()
            .parseConfig(cl, Config.class)
            .bindModule(Module.class)
            .build();

        IntegrationTest.verifyStarted(genesis);
        VersionsIntegrationTest.genesis.set(genesis);

        return genesis;
    }

}
