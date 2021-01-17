package com.zhokhov.dumper.api.config;

import io.micronaut.context.annotation.Factory;
import io.micronaut.runtime.http.scope.RequestScope;
import org.dataloader.DataLoaderRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Factory
public class DataLoaderRegistryFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DataLoaderRegistryFactory.class);

    @SuppressWarnings("unused")
    @RequestScope
    public DataLoaderRegistry dataLoaderRegistry() {
        DataLoaderRegistry dataLoaderRegistry = new DataLoaderRegistry();

        LOG.trace("Created new data loader registry");
        return dataLoaderRegistry;
    }

}
