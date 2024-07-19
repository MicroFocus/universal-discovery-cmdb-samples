package com.microfocus.ucmdb.rest.sample.packagemanager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfocus.ucmdb.rest.sample.utils.RestApiConnectionUtils;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

import java.io.Console;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class DeployPackage {

    private String rootURL;

    private String packABSPath;

    public DeployPackage(String rootURL, String packABSPath) {
        this.rootURL = rootURL;
        this.packABSPath = packABSPath;
    }

    public static void main(String args[]) throws Exception {
        String hostname;
        String port;
        String username;
        String password;
        String packagePath;

        if (args.length < 5) {
            Scanner sc = new Scanner(System.in);
            System.out.print("Please enter hostname/IP of UCMDB Server: ");
            hostname = sc.hasNextLine() ? sc.nextLine() : "";
            System.out.print("Please enter port of UCMDB Server: ");
            port = sc.hasNext() ? sc.next() : "";
            System.out.print("Please enter username for UCMDB: ");
            username = sc.hasNext() ? sc.next() : "";
            System.out.print("Please enter password for UCMDB: ");
            password = sc.hasNext() ? sc.next() : "";
            System.out.print("Please enter the absolute path of the package you want to deploy: ");
            packagePath = sc.hasNext() ? sc.next() : "";
        } else {
            hostname = args[0];
            port = args[1];
            username = args[2];
            password = args[3];
            packagePath = args[4];
        }

        String rootURL = RestApiConnectionUtils.buildRootUrl(hostname, port,false);

        // authenticate
        String token = RestApiConnectionUtils.loginServer(rootURL, username, password);

        DeployPackage deployPackage = new DeployPackage(rootURL, packagePath);
        deployPackage.execute(token);

    }

    private void execute(String token) throws Exception {
        System.out.println("Started uploading package, path: " + this.packABSPath);
        String url = rootURL + "packagemanager/packages/resources";

        HttpPost httpPost = RestApiConnectionUtils.getPostRequest(url, token, null,
                RestApiConnectionUtils.MEDIA_TYPE_JSON, null);
        RequestConfig defaultRequestConfig = RequestConfig.custom().setConnectTimeout(50000).
                setConnectionRequestTimeout(50000).setSocketTimeout(150000).build();
        httpPost.setConfig(defaultRequestConfig);

        CloseableHttpResponse httpResponse = null;
        Path path = Paths.get(this.packABSPath);
        String fileName = path.getFileName().toString();
        String resources;
        StringBuilder cookies = new StringBuilder();
        try {
            httpResponse = RestApiConnectionUtils.sendMultiPartRequest(httpPost, this.packABSPath, "file", fileName);
            resources = EntityUtils.toString(httpResponse.getEntity());
            int returnCode = httpResponse.getStatusLine().getStatusCode();
            if (returnCode != 200) {
                System.out.println("Failed to upload package to UCMDB Server");
                System.exit(-1);
            }
            Header[] headers = httpResponse.getHeaders("Set-Cookie");
            // Append all Set-Cookie header values to a single string
            for (Header header : headers) {
                cookies.append(header.getValue()).append("; ");
            }

        } finally {
            RestApiConnectionUtils.close(httpResponse);
        }

        if (cookies.length() > 0) {
            RestApiConnectionUtils.cookies = cookies.toString();
        }
        RestApiConnectionUtils.doPost(rootURL + "packagemanager/packages/" + fileName, token, resources, "DEPLOY THE UPLOADED PACKAGE");

        Thread.sleep(5000);

        boolean isNotFinished = true;
        while (isNotFinished) {
            String response = RestApiConnectionUtils.doGet(rootURL + "packagemanager/packages/" + fileName + "/progress", token, "GET PACKAGE DEPLOYMENT PROGRESS");
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readValue(response, JsonNode.class);
            //INIT, IN-PROGRESS, FINISHED, FAILED
            if (node.get("status").asText().equals("FINISHED")) {
                isNotFinished = false;
                RestApiConnectionUtils.cookies = null;
                System.out.println("Deploy package finished");
            } else if (node.get("status").asText().equals("FAILED")) {
                isNotFinished = false;
                System.out.println("Deploy package failed");
            }
            Thread.sleep(3000);
        }

    }

}
