/*
 * MicroFocus.com Inc.
 * Copyright(c) 2020 All Rights Reserved.
 */
package com.microfocus.ucmdb.rest.sample.quickstart;

import com.microfocus.ucmdb.rest.sample.utils.RestApiConnectionUtils;

import java.io.Console;
import java.io.IOException;
import java.util.Scanner;

/**
 * @author wusui
 * @version $Id: LoginUCMDB.java, 2020-07-07 3:58 PM wusui Exp $
 */
public class LogInToUCMDB {

    public static void main(String[] args) {
        String hostname;
        String port;
        String username;
        String password;

        if (args.length < 4) {
            Scanner sc = new Scanner(System.in);
            System.out.print("Please enter hostname/IP of UCMDB Server: ");
            hostname = sc.hasNextLine() ? sc.nextLine() : "";
            System.out.print("Please enter port of UCMDB Server: ");
            port = sc.hasNext() ? sc.next() : "";
            System.out.print("Please enter username for UCMDB: ");
            username = sc.hasNext() ? sc.next() : "";
            Console console = System.console();
            password = new String(console.readPassword("Please enter password for UCMDB: "));
        } else {
            hostname = args[0];
            port = args[1];
            username = args[2];
            password = args[3];
        }





        String rootURL = RestApiConnectionUtils.buildRootUrl(hostname, port,  false);

        // authenticate
        String token = null;
        try {
            token = RestApiConnectionUtils.loginServer(rootURL, username, password);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(token == null || token.length() == 0){
            System.out.println("Can not log in to the UCMDB server. Check your serverIp, userName or password!");
            System.exit(0);
        }
        System.out.println("Login successful! You have got the token to access UCMDB: " + token);

    }

}
