package util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class HTTPResponse {
	private String statusCode;
	private Map<String, String> headers;
	private Map<String, String> cookies;
	private String body;
	
	public HTTPResponse(String statusCode, Map<String, String> headers,
			Map<String, String> cookies, String body) {
		this.statusCode = statusCode;
		this.headers = headers;
		this.cookies = cookies;
		this.body = body;
	}
	
	public String getStatusCode() {
		return this.statusCode;
	}
	public Map<String, String> getHeaders() {
		return this.headers;
	}
	public Map<String, String> getCookies() {
		return this.cookies;
	}
	public String body() {
		return this.body;
	}

	public void write(DataOutputStream dos) {
		try {
			dos.writeBytes("");
		} catch (IOException e) {
			
		}
	}
}
