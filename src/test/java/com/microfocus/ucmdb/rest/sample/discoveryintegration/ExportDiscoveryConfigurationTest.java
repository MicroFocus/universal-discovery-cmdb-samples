package com.microfocus.ucmdb.rest.sample.discoveryintegration;

import org.junit.Test;

public class ExportDiscoveryConfigurationTest {
    @Test
    public void testMain() throws Exception {
        ExportDiscoveryConfiguration.main(new String[]{"${UCMDB_IP_ADDRESS}", "${UCMDB_PORT}", "${UCMDB_USERNAME}", "${UCMDB_PASSWORD}"});
    }
}
