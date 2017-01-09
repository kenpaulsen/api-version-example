package com.vevo.versions;

import com.vevo.genesis.Genesis;
import com.vevo.genesis.bootstrap.cli.CommandLineParser;
import com.vevo.genesis.bootstrap.cli.ParsedCommandLine;

public class Service {

    public static void main(String[] args) {
        ParsedCommandLine cl = CommandLineParser.parse(args);
        Genesis genesis = new Genesis.Builder()
            .parseConfig(cl, Config.class)
            .bindModule(Module.class)
            .build();

        genesis.getEndpoint().start();
    }
}
