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
	private String requestedUrl = null;;
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
		this.requestedUrl = tokens[1];
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
	public HTTPRequest(String in) throws IOException, IllegalArgumentException {
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
	}

	// Getters
	public Map<String, String> getCookies() {
		return HttpRequestUtils.parseCookies(headers.get("Cookie"));
	}

	public String getHeader(String key) {
		return this.headers.get(key);
	}

	public String getUrl() {
		return requestedUrl;
	}

	public String getMethod() {
		return method;
	}

	public String getBody() {
		return body;
	}
	
	// For debug
	/*void printHeaders() {
		final Logger logger = LoggerFactory.getLogger(IOUtilsTest.class);
		
		Set<String> keys = headers.keySet();
		Iterator<String> iterator = keys.iterator();
	    // while loop
	    while (iterator.hasNext()) {
	    	String key = iterator.next();
	    	logger.debug(key + ": " + headers.get(key));
	    }
	}*/
}
