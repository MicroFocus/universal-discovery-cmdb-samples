/*
 * MicroFocus.com Inc.
 * Copyright(c) 2020 All Rights Reserved.
 */
package com.microfocus.ucmdb.rest.sample.quickstart;

import com.microfocus.ucmdb.rest.sample.utils.RestApiConnectionUtils;

import java.io.IOException;

/**
 * @author wusui
 * @version $Id: LoginUCMDB.java, 2020-07-07 3:58 PM wusui Exp $
 */
public class LogInToUCMDB {

    public static void main(String[] args) {
        if(args.length < 4){
            System.out.println("Parameters: hostname port username password");
            System.exit(0);
        }

        String hostname = args[0];
        String port = args[1];
        String username = args[2];
        String password = args[3];

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
