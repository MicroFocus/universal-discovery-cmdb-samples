package com.microfocus.ucmdb.rest.sample.discoveryintegration;

import com.microfocus.ucmdb.rest.sample.utils.PayloadUtils;
import com.microfocus.ucmdb.rest.sample.utils.RestApiConnectionUtils;

import java.io.Console;
import java.io.File;
import java.util.Scanner;

public class ImportDiscoveryConfiguration {

    private String rootURL;

    public ImportDiscoveryConfiguration(String rootURL) {
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
        ImportDiscoveryConfiguration task = new ImportDiscoveryConfiguration(rootURL);
        task.execute(token);

    }

    private void execute(String token) throws Exception {
        //Import IP Ranges without encrypted
        File file= PayloadUtils.loadFile("discovery_resources_2023-08-09_05-12-55.json");
        RestApiConnectionUtils.uploadFile(rootURL + "discoveryintegration/import?format=JSON&allowConflict=true", token, "Import IP ranges without encrypted", file);
        //Import IP Ranges with encrypted
        file= PayloadUtils.loadFile("discovery_resources_2023-08-09_05-13-26.json");
        RestApiConnectionUtils.uploadFile(rootURL + "discoveryintegration/import?format=JSON&allowConflict=true&password=Admin_1234", token, "Import IP ranges with encrypted", file);
        //Import credentials without encrypted
        file= PayloadUtils.loadFile("discovery_resources_2023-08-09_05-13-51.json");
        RestApiConnectionUtils.uploadFile(rootURL + "discoveryintegration/import?format=JSON&allowConflict=true", token,"Import credentials without encrypted", file);
        //Import credentials with encrypted
        file= PayloadUtils.loadFile("discovery_resources_2023-08-09_05-44-20.json");
        RestApiConnectionUtils.uploadFile(rootURL + "discoveryintegration/import?format=JSON&allowConflict=true&password=Admin_1234", token, "Import credentials with encrypted", file);
        //Import IP ranges and credentials without encrypted
        file= PayloadUtils.loadFile("discovery_resources_2023-08-09_05-14-33.json");
        RestApiConnectionUtils.uploadFile(rootURL + "discoveryintegration/import?format=JSON&allowConflict=true", token,  "Import IP ranges and credentials without encrypted", file);
        //Import IP ranges and credentials with encrypted
        file= PayloadUtils.loadFile("discovery_resources_2023-08-09_05-14-48.json");
        RestApiConnectionUtils.uploadFile(rootURL + "discoveryintegration/import?format=JSON&allowConflict=true&password=Admin_1234", token, "Import IP ranges and credential with encrypted", file);

    }
}
