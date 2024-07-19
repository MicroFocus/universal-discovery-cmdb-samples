/**
 * Copyright 2020 - 2023 Open Text
 * The only warranties for products and services of Open Text and its
 * affiliates and licensors ("Open Text") are as may be set forth in the
 * express warranty statements accompanying such products and services.
 * Nothing herein should be construed as constituting an additional warranty.
 * Open Text shall not be liable for technical or editorial errors or
 * omissions contained herein.
 * The information contained herein is subject to change without notice.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.microfocus.ucmdb.rest.sample.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Copyright 2021-2023 Open Text
 */
public class RestApiConnectionUtils {
    public static final String MEDIA_TYPE_JSON = "application/json";
    public static final String MEDIA_TYPE_OCTET_STREAM = "application/octet-stream";
    public static final String MEDIA_ALL = "*/*";
    public static final String MEDIA_TYPE_MULTIPART_FORM_DATA = "multipart/form-data";

    public static final String TCP_PROTOCOL = "https://";
    public static final String CONTAINER_CONTEXT = "/ucmdb-server";
    public static final String URI_PREFIX = "/rest-api/";
    public static final String RETURNED_A_STATUS_CODE_OF = "Returned a status code of ";
    public static final String RESPONSE_RESULT = "Response Result: ";
    public static final String SPLITER = "==============================================================";
    public static String cookies;

    static class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {
        public static final String METHOD_NAME = "DELETE";

        public String getMethod() { return METHOD_NAME; }

        public HttpDeleteWithBody(final String uri) {
            super();
            setURI(URI.create(uri));
        }
        public HttpDeleteWithBody(final URI uri) {
            super();
            setURI(uri);
        }
        public HttpDeleteWithBody() { super(); }
    }

    public static String buildRootUrl(String serverIP) {
        return buildRootUrl(serverIP, "8443", false);
    }

    public static String buildRootUrl(String serverIP, String port) {
        return buildRootUrl(serverIP, port, false);
    }

    public static String buildRootUrl(String serverIP, String port, boolean isContainerized) {
        return TCP_PROTOCOL + serverIP + ":" + port + (isContainerized ? CONTAINER_CONTEXT : "") + URI_PREFIX;
    }

    public static String loginServer(String url, String userName, char[] plainPwd) throws IOException {
        //HTTPS protocol, server IP and API type as the prefix of REST API URL.
        if(url == null || url.length() == 0 || userName == null || userName.length() == 0 || plainPwd == null || plainPwd.length== 0){
            System.out.println("Please input correct url or userName or password!");
            return null;
        }

        //adminUser has the sample access.
        JSONObject loginJson = new JSONObject();
        loginJson.put("clientContext","1");
        loginJson.put("username",userName);
        loginJson.put("password",new String(plainPwd));

        //Put username and password in HTTP request body and invoke REST API(rest-api/authenticate) with POST method to get token.
        String result = doPost(url + "authenticate", null, loginJson.toString(), "LOG IN TO SERVER.");
        if(result == null || result.length() == 0){
            System.out.println("Failed to connect with the UCMDB server!");
            return result;
        }
        String token = new JSONObject(result).getString("token");

        if(token == null || token.length() == 0){
            System.out.println("Can not log in to the UCMDB server. Check your serverIp, userName or password!");
            System.exit(0);
        }

        if (token != null) {
            System.out.println("Connect to server Successfully!");
        }

        Arrays.fill(plainPwd, ' ');
        return token;
    }

    public static String ensureZoneBasedDiscoveryIsEnabled(String rootURL, String token) {

        String response = null;
        try {
            response = RestApiConnectionUtils.doGet(rootURL + "infrasetting?name=appilog.collectors.enableZoneBasedDiscovery", token, "ENSURE ZONE BASED DISCOVERY IS ENABLED.");
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(response);
            if(!"true".equals(node.get("value").asText())){
                System.out.println("New Discovery backend is not enabled.");
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

        return response;
    }

    public static String doPost(String url, String token, String content, String description) throws IOException {
        System.out.println();
        System.out.println(SPLITER);
        System.out.println(description);
        HttpPost httpPost = getPostRequest(url, token, MEDIA_TYPE_JSON, MEDIA_TYPE_JSON, content);
        String result = getResponseString(httpPost);
        System.out.println(SPLITER);
        System.out.println();
        return result;
    }

    public static String downloadFile(String url, String token, String content, String description,String fileType) throws IOException {
        System.out.println();
        System.out.println(SPLITER);
        System.out.println(description);
        HttpPost httpPost = getPostRequest(url, token, MEDIA_TYPE_JSON, MEDIA_TYPE_OCTET_STREAM, content);
        String result = storeFile(httpPost,"Export_Data_"+System.currentTimeMillis()+"."+fileType);
        System.out.println(SPLITER);
        System.out.println();
        return result;
    }

    public static void exportFile(String url, String token, String content, String description) throws UnsupportedEncodingException {
        System.out.println();
        System.out.println(SPLITER);
        System.out.println(description);
        HttpPost httpPost = getPostRequest(url, token, MEDIA_TYPE_JSON, MEDIA_TYPE_JSON, content);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
        storeFile(httpPost, "discovery_resources_" + df.format(new Date()) + ".json");
        System.out.println(SPLITER);
        System.out.println();
    }

    public static String uploadFile(String url, String token,  String description, File file) throws IOException {
        System.out.println();
        System.out.println(SPLITER);
        System.out.println(description);
        System.out.println("file path is "+file.getAbsolutePath());
        HttpPost httpPost = getPostRequest(url, token, MEDIA_TYPE_MULTIPART_FORM_DATA, MEDIA_TYPE_JSON, null);
        String boundary = "--------------4585696313564699";
        httpPost.setHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setCharset(Charset.forName("UTF-8")).setBoundary(boundary).addBinaryBody("file", file);
        HttpEntity entity = builder.build();
        httpPost.setEntity(entity);
        String result = getResponseString(httpPost);
        System.out.println(SPLITER);
        System.out.println();
        return result;
    }
    public static String doGet(String url, String token, String description) {
        System.out.println();
        System.out.println(SPLITER);
        System.out.println(description);
        HttpGet httpGet = getGetRequest(url, token, MEDIA_TYPE_JSON, MEDIA_TYPE_JSON);
        String result = getResponseString(httpGet);
        System.out.println(SPLITER);
        System.out.println();
        return result;
    }


    public static String doPatch(String url, String token, String content, String description) throws IOException{
        System.out.println();
        System.out.println(SPLITER);
        System.out.println(description);
        HttpPatch patchRequest = getPatchRquest(url, token, MEDIA_TYPE_JSON, MEDIA_TYPE_JSON, content);
        String result = getResponseString(patchRequest);
        System.out.println(SPLITER);
        System.out.println();
        return result;
    }

    public static String doDelete(String url, String token, String content, String description) throws IOException {
        System.out.println();
        System.out.println(SPLITER);
        System.out.println(description);
        HttpDeleteWithBody httpDeleteWithBody = getDeleteRequest(url, token, MEDIA_TYPE_JSON, MEDIA_TYPE_JSON, content);
        String result = getResponseString(httpDeleteWithBody);
        System.out.println(SPLITER);
        System.out.println();
        return result;
    }

    public static CloseableHttpResponse sendMultiPartRequest(HttpPost httpPost, String fileFullPath,
                                                             String paramatersName, String fileName) {
        File file = new File(fileFullPath);
        InputStream fileInputStream = null;
        try{
            fileInputStream = new FileInputStream(file);
            HttpEntity requestEntity = MultipartEntityBuilder.create()
                    .addBinaryBody(paramatersName, fileInputStream, ContentType.DEFAULT_BINARY, fileName)
                    .build();
            httpPost.setEntity(requestEntity);
            return sendRequest(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }
        return null;
    }

    public static void close(CloseableHttpResponse httpResponse) {
        if (httpResponse != null) {
            try {
                httpResponse.close();
            } catch (IOException e) {
                //ignore
            }
        }
    }

    public static String printStatusCode(int status){
        String result = "";
        switch (status){
            case 200:
                result = "200 (Successful)";break;
            case 400:
                result = "400 (Bad Request) Syntax/data provided is not valid for the request";break;
            case 401:
                result = "401 (Unauthorized) User not authorized or invalid session token";break;
            case 403:
                result = "403 (Forbidden) Operation is not allowed (for any user)";break;
            case 404:
                result = "404 (Not Found) The URI points to a non-existent resource/collection";break;
            case 405:
                result = "405 (Method Not Allowed) The HTTP method is not allowed for the resource";break;
            case 406:
                result = "406 (Not Acceptable) Media-type specified in the Accept header is not supported";break;
            case 412:
                result = "412 (Precondition Failed) Start and count values cannot be satisfied in a query";break;
            case 415:
                result = "415 (Unsupported Media Type) Media-type specified in the Content-Type header is not supported";break;
            case 500:
                result = "500 (Internal Server Error) An unexpected/server-side error has occurred";break;
            case 501:
                result = "501 (Not Implemented) The HTTP method is not currently implemented for the given resource/collection URI";break;
            case 503:
                result = "503 (Service Unavailable) The server is currently unavailable";break;

        }
        return result;
    }

    public static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    public static HttpGet getGetRequest(String url, String token, String requestContentType, String responseType) {
        HttpGet httpGet = new HttpGet(url);
        setRequestHeader(token, requestContentType, responseType, httpGet);
        System.out.println("Request is " + httpGet.getMethod() + ": " + httpGet.getURI());
        return httpGet;
    }

    public static HttpPatch getPatchRquest(String url, String token, String requestContentType, String responseType,
                                           String content) throws UnsupportedEncodingException {
        HttpPatch httpPatch = new HttpPatch(url);
        setRequestHeader(token, requestContentType, responseType, httpPatch);
        setRequestContent(content, httpPatch);
        System.out.println("Request is " + httpPatch.getMethod() + ": " + httpPatch.getURI());
        System.out.println("Request body is " + content);
        return httpPatch;
    }

    public static HttpPost getPostRequest(String url, String token, String requestContentType, String responseType,
                                          String content) throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(url);
        setRequestHeader(token, requestContentType, responseType, httpPost);
        setRequestContent(content, httpPost);
        System.out.println("Request is " + httpPost.getMethod() + ": " + httpPost.getURI());
        System.out.println("Request body is " + content);
        return httpPost;
    }

    public static HttpDeleteWithBody getDeleteRequest(String url, String token, String requestContentType,
                                                      String responseType, String content) throws UnsupportedEncodingException {
        HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(url);
        setRequestHeader(token, requestContentType, responseType, httpDelete);
        if(content != null){
            setRequestContent(content, httpDelete);
        }
        System.out.println("Request is " + httpDelete.getMethod() + ": " + httpDelete.getURI());
        System.out.println("Request body is " + content);
        return httpDelete;
    }

    public static HttpDelete getDeleteRequest(String url, String token, String requestContentType, String responseType){
        HttpDelete httpDelete = new HttpDelete(url);
        setRequestHeader(token, requestContentType, responseType, httpDelete);
        System.out.println("Request is " + httpDelete.getMethod() + ": " + httpDelete.getURI());
        return httpDelete;
    }

    public static CloseableHttpResponse sendRequest(HttpRequestBase httpRequest) throws IOException {
        SSLConnectionSocketFactory sslConnectionSocketFactory = getSslConnectionSocketFactory();
        CloseableHttpClient c = HttpClients
                .custom()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .setSSLHostnameVerifier(new RestApiConnectionUtils.TrustAnyHostnameVerifier())
                .build();
        return c.execute(httpRequest);
    }

    public static SSLConnectionSocketFactory getSslConnectionSocketFactory() {
        SSLContext sslcontext = null;
        try {
            // Just sample code
            // Please use TrustSelfSignedStrategy instead of TrustAllStrategy in production code
            sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustAllStrategy()).build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            e.printStackTrace();
        }
        return new SSLConnectionSocketFactory(sslcontext,
                (s, sslSession) -> true);
    }

    public static void saveFile(HttpEntity entity, String targetFilePath) {
        InputStream is = null;
        FileOutputStream out = null;
        try {
            is = entity.getContent();
            out = new FileOutputStream(targetFilePath);
            byte[] buffer = new byte[4096];
            int readLength = 0;
            while ((readLength = is.read(buffer)) > 0) {
                byte[] bytes = new byte[readLength];
                System.arraycopy(buffer, 0, bytes, 0, readLength);
                out.write(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {

                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void setRequestHeader(String token, String requestContentType, String responseType, HttpRequestBase request) {
        if (token != null) {
            request.setHeader("Authorization", "Bearer " + token);
        }
        if (requestContentType != null) {
            request.setHeader("Content-Type", requestContentType + ";charset=utf-8");
        }
        if (responseType != null) {
            request.setHeader("Accept", responseType);
        }
        if (cookies != null && cookies.length() > 0) {
            request.setHeader("Cookie", cookies);
        }
    }

    private static void setRequestContent(String content, HttpEntityEnclosingRequestBase request) throws UnsupportedEncodingException {
        if (content != null && content.length() > 0) {
            StringEntity entity = new StringEntity(content);
            request.setEntity(entity);
        }
    }

    private static String getResponseString(HttpRequestBase request) {
        CloseableHttpResponse httpResponse = null;
        String result = null;
        try {
            httpResponse = sendRequest(request);
            result = EntityUtils.toString(httpResponse.getEntity());
            System.out.println("Returned a status code of " + printStatusCode(httpResponse.getStatusLine().getStatusCode()));
            System.out.println("Response body is " + result);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {
            close(httpResponse);
        }
        return result;
    }
    private static String storeFile(HttpRequestBase request,String fileName) {
        CloseableHttpResponse httpResponse = null;
        File targetFile = null;
        try {
            httpResponse = sendRequest(request);
            if (httpResponse.getHeaders("Content-Disposition") != null && httpResponse.getHeaders("Content-Disposition")[0] != null) {
                String fileNameFromResponse = httpResponse.getHeaders("Content-Disposition")[0].toString();
                if (fileNameFromResponse.split("filename=").length == 2) {
                    fileName = fileNameFromResponse.split("filename=")[1];
                }
            }
            targetFile = new File(fileName);
            InputStream resultStream=httpResponse.getEntity().getContent();
            try (FileOutputStream outputStream = new FileOutputStream(targetFile, false)) {
                int read;
                byte[] bytes = new byte[1024];
                while ((read = resultStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
            }
            System.out.println("Returned a status code of " + printStatusCode(httpResponse.getStatusLine().getStatusCode()));
            System.out.println("File stored at " + targetFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {
            close(httpResponse);
        }
        return targetFile.getAbsolutePath();
    }
}
