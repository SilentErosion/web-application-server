package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import model.User;
import util.HttpRequestUtils.Pair;

public class HTTPRequest {
	private String queryString = null;;
	private String path = null;
	private String method = null;;
	private String body = null;
	private Map<String, String> headers = Maps.newHashMap();

	public HTTPRequest(InputStream in) throws IOException, IllegalArgumentException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = reader.readLine();
		if(line == null) {
			throw new IllegalArgumentException("Invalid HTTP request");
		}
		String tokens[] = line.split(" ");
		this.method = tokens[0];
		String requestedUrl = tokens[1];
		System.out.println(requestedUrl);
		String tokens2[] = requestedUrl.split("\\?");
		this.path = tokens2[0];
		if(tokens2.length == 2) 
			this.queryString = tokens2[1];
		
		line = reader.readLine();
		while (line != null && !"".equals(line)) {
			Pair pair = HttpRequestUtils.parseHeader(line);
			headers.putIfAbsent(pair.getKey(), pair.getValue());
			line = reader.readLine();
		}
		
		String contentLengthString = headers.get("Content-Length"); 
		int contentLength = contentLengthString == null ?
				0 : Integer.parseInt(contentLengthString);
		body = IOUtils.readData(reader, contentLength);
	}
	
	// Constructor for testing
	/*public HTTPRequest(String in) throws IOException, IllegalArgumentException {
		BufferedReader reader = new BufferedReader(new StringReader(in));
		String line = reader.readLine();
		if(line == null) {
			throw new IllegalArgumentException("Invalid HTTP request");
		}
		method = line.split(" ")[0];
		requestedUrl = line.split(" ")[1];
		line = reader.readLine();
		while (line != null && !"".equals(line)) {
			Pair pair = HttpRequestUtils.parseHeader(line);
			headers.putIfAbsent(pair.getKey(), pair.getValue());
			line = reader.readLine();
		}
		
		String contentLengthString = headers.get("Content-Length"); 
		int contentLength = contentLengthString == null ?
				0 : Integer.parseInt(contentLengthString);
		body = IOUtils.readData(reader, contentLength);
	}*/

	// Getters
	public Map<String, String> getCookies() {
		return HttpRequestUtils.parseCookies(headers.get("Cookie"));
	}

	public String getHeader(String key) {
		return this.headers.get(key);
	}

	public String getPath() {
		return this.path;
	}

	public String getMethod() {
		return method;
	}

	public String getBody() {
		return body;
	}
	
	public String getQueryString( ) {
		return this.queryString;
	}
	
	// For debug
	void printHeaders() {
		final Logger logger = LoggerFactory.getLogger(HTTPRequest.class);
		
		Set<String> keys = headers.keySet();
		Iterator<String> iterator = keys.iterator();
	    // while loop
	    while (iterator.hasNext()) {
	    	String key = iterator.next();
	    	logger.debug(key + ": " + headers.get(key));
	    }
	}
}
