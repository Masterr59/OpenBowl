/*
 * Copyright (C) 2020 Open Bowl <http://www.openbowlscoring.org/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openbowl.common;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Open Bowl <http://www.openbowlscoring.org/>
 */
public class WebFunctions {

    public final static String GET_METHOD = "GET";
    public final static String POST_METHOD = "POST";
    public static final int DefaultPort = 47687;
    private static final int DefaultTimeout = 2000;

    //https://stackoverflow.com/questions/11640025/how-to-obtain-the-query-string-in-a-get-with-java-httpserver-httpexchange

    /**
     * 
     * Splits a http(s) query string into key-value pairs
     * Duplicate keys will be hammered
     *
     * @param query The query string from a HTTP(s) request
     * @return Key Value pair in a map
     */
    public static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        if (query != null) {
            for (String param : query.split("&")) {
                String[] entry = param.split("=");
                if (entry.length > 1) {
                    result.put(entry[0], entry[1]);
                } else {
                    result.put(entry[0], "");
                }
            }
        }
        return result;
    }

    /**
     * 
     * Creates a HttpServer on the default port for this application
     *
     * @return
     * @throws IOException
     */
    public static HttpServer createDefaultServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(DefaultPort), 0);
        //default executor
        server.setExecutor(null);
        return server;
    }
    
    /**
     * 
     * Creates a custom HttpServer on a non-standard port
     *
     * @param offset The default port number offset value
     * @return An instance of the HttpServer on defaultPort + offset
     * @throws IOException
     */
    public static HttpServer createCustomServer(int offset) throws IOException{
        HttpServer server = HttpServer.create(new InetSocketAddress(DefaultPort + offset), 0);
        //default executor
        server.setExecutor(null);
        return server;
    }

    /**
     *
     * Preforms a HTTP get request on the applications default port 
     * and returns the resulting data as a string
     * 
     * @param address The IP address or FQDN
     * @param parms The URL's path and query
     * @param authToken The authorization token to be set in the header
     * @return The String value of the response
     * @throws MalformedURLException
     * @throws IOException
     * @throws InterruptedException
     */
    public static String doHttpGetRequest(String address, String parms, String authToken) throws MalformedURLException, IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();
        
        String uri = "http://" + address + ":" + Integer.toString(DefaultPort) + "/" + parms;
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .setHeader("User-Agent", "Java 11 HttpClient Bot")
                .setHeader(AuthorizedUser.AUTHKEYWORD, authToken)
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        return response.body();
    }
    
    /**
     *
     * Preforms a HTTP post request on the applications default port 
     * and returns the resulting data as a string
     * 
     * @param address The IP address or FQDN
     * @param parms The URL's path and query
     * @param postData The String in JSON format to be posted to the server
     * @param authToken The authorization token to be set in the header
     * @return The String value of the response
     * @throws MalformedURLException
     * @throws IOException
     * @throws InterruptedException
     */
    public static String doHttpPostRequest(String address, String parms, String postData, String authToken) throws MalformedURLException, IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();
        
        String uri = "http://" + address + ":" + Integer.toString(DefaultPort) + "/" + parms;
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(postData))
                .uri(URI.create(uri))
                .setHeader("User-Agent", "Java 11 HttpClient Bot")
                .header("Content-Type", "application/json")
                .setHeader(AuthorizedUser.AUTHKEYWORD, authToken)
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        return response.body();
    
    }
}
