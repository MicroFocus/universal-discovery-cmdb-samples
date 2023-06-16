package com.microfocus.ucmdb.rest.sample.probesetup;

import org.junit.Test;

public class NetworkScopeConfigurationForCredentialTest{
    @Test
    public void testMain() throws Exception{
        NetworkScopeConfigurationForCredential.main(new String[]{"${UCMDB_IP_ADDRESS}", "${UCMDB_PORT}", "${UCMDB_USERNAME}", "${UCMDB_PASSWORD}"});
    }
}