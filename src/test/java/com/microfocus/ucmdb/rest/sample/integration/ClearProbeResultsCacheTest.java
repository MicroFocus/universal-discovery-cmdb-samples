package com.microfocus.ucmdb.rest.sample.integration;

import junit.framework.TestCase;
import org.junit.Test;

public class ClearProbeResultsCacheTest extends TestCase {

    @Test
    public void testMain() throws Exception {
        ClearProbeResultsCache.main(new String[]{"localhost", "8443", "admin", "Admin_1234"});
    }

}