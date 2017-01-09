package com.vevo.versions;

//import com.vevo.genesis.module.DependsOn;
import com.vevo.genesis.module.GenesisModule;
import com.vevo.versions.resources.VersionsResource;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
@DependsOn({
    MyBatisModule.class,
    LiquibaseModule.class,
    TokensModule.class,
    DataDogModule.class,
    NFSCompliance.class
    VevoApiModule.class,
    SentryModule.class,
    ScarfModule.class
})
*/
class Module extends GenesisModule {

    @Override
    public void configure() {
        //resources
        bind(VersionsResource.class);

        //set this so we don't constantly get warnings from the jersey client / replay code
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");

        //turn off chatty console loggers (looking at you, aws)
        Handler[] handlers = Logger.getLogger("").getHandlers();
        for (Handler handler : handlers) {
            handler.setLevel(Level.SEVERE);
        }
    }
}
