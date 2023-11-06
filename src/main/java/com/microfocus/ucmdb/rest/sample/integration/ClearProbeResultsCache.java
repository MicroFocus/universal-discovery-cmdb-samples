package com.microfocus.ucmdb.rest.sample.integration;

import com.microfocus.ucmdb.rest.sample.utils.PayloadUtils;
import com.microfocus.ucmdb.rest.sample.utils.RestApiConnectionUtils;

import java.io.Console;
import java.util.Scanner;

public class ClearProbeResultsCache {

    private String rootURL;

    public ClearProbeResultsCache(String rootURL) {
        this.rootURL = rootURL;
    }

    public static void main(String[] args) throws Exception {
        String hostname;
        String port;
        String username;
        char[] password;

        if (args.length < 4) {
            Scanner sc = new Scanner(System.in);
            System.out.print("Please enter hostname/IP of UCMDB Server: ");
            hostname = sc.hasNextLine() ? sc.nextLine() : "";
            System.out.print("Please enter port of UCMDB Server: ");
            port = sc.hasNext() ? sc.next() : "";
            System.out.print("Please enter username for UCMDB: ");
            username = sc.hasNext() ? sc.next() : "";
            Console console = System.console();
            password = console.readPassword("Please enter password for UCMDB: ");
        } else {
            hostname = args[0];
            port = args[1];
            username = args[2];
            password = args[3].toCharArray();
        }

        String rootURL = RestApiConnectionUtils.buildRootUrl(hostname, port, false);

        // authenticate
        String token = RestApiConnectionUtils.loginServer(rootURL, username, password);

        // start the task
        ClearProbeResultsCache task = new ClearProbeResultsCache(rootURL);
        task.execute(token);

    }

    private void execute(String token) throws Exception {
        int count = 1;
        String content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        //Clear Probe Results Cache
        RestApiConnectionUtils.doPatch(rootURL+"integration/jobs?operation=clearcache",token,content,"Clear Probe Results Cache");

    }
}
