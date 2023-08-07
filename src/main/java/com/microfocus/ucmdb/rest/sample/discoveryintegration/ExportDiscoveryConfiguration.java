package com.microfocus.ucmdb.rest.sample.discoveryintegration;

import com.microfocus.ucmdb.rest.sample.utils.PayloadUtils;
import com.microfocus.ucmdb.rest.sample.utils.RestApiConnectionUtils;

import java.io.Console;
import java.util.Scanner;

public class ExportDiscoveryConfiguration {
    private String rootURL;

    public ExportDiscoveryConfiguration(String rootURL) {
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
        ExportDiscoveryConfiguration task = new ExportDiscoveryConfiguration(rootURL);
        task.execute(token);

    }

    private void execute(String token) throws Exception {
        String content = PayloadUtils.loadContent(this.getClass().getSimpleName(), 1);
        //Export IP Ranges with encrypted
        RestApiConnectionUtils.exportFile(rootURL + "discoveryintegration/export?categories=IP_RANGE&format=JSON&isEncrypted=true&password=Admin_1234", token, content, "Export IP ranges with encrypted");
        //Export IP Ranges without encrypted
        RestApiConnectionUtils.exportFile(rootURL + "discoveryintegration/export?categories=IP_RANGE&format=JSON&isEncrypted=false", token, content, "Export IP ranges without encrypted");
        //Export credentials without encrypted
        RestApiConnectionUtils.exportFile(rootURL + "discoveryintegration/export?categories=CREDENTIAL&format=JSON&isEncrypted=false", token, content, "Export credentials without encrypted");
        //Export credentials with encrypted
        RestApiConnectionUtils.exportFile(rootURL + "discoveryintegration/export?categories=CREDENTIAL&format=JSON&isEncrypted=true&password=Admin_1234", token, content, "Export credentials without encrypted");
        //Export IP ranges and credentials without encrypted
        RestApiConnectionUtils.exportFile(rootURL + "discoveryintegration/export?categories=IP_RANGE&categories=CREDENTIAL&format=JSON&isEncrypted=false", token, content, "Export IP ranges and credentials without encrypted");
        //Export all discovery resources with encrypted
        RestApiConnectionUtils.exportFile(rootURL + "discoveryintegration/export?categories=IP_RANGE&categories=CREDENTIAL&format=JSON&isEncrypted=true&password=Admin_1234", token, content, "Export all discovery resources with encrypted");

    }

}
