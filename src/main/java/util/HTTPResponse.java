package util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class HTTPResponse {
	private DataOutputStream dos = null;
	private String firstLine = null;
	// Content-Type, Content-Length, and so on should be
	// Set by the calling processUnit object.
	private Map<String, String> headers = new HashMap<String, String>();
	private Map<String, String> cookies = new HashMap<String, String>();
	private byte[] body; // Need a length limit?

	public HTTPResponse(OutputStream out) {
		this.dos = new DataOutputStream(out);
	}

	/*public String getStatusCode() {
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
	}*/

	public void set302(String loc) {
//		this.statusCode = "302";
		this.firstLine = "HTTP/1.1 302 Found\r\n";
		this.headers.put("Location", loc);
	}

	public void set200() {
//		this.statusCode = "200";
		this.firstLine = "HTTP/1.1 200 OK\r\n";
	}
	
	public void set303(String loc) {
		this.firstLine = "HTTP/1.1 303 See Other\r\n";
		this.headers.put("Location", loc);
	}

	public void setHeader(String key, String value) {
		this.headers.put(key, value);
	}
	
	public void setCookie(String name, String value) {
		this.cookies.put(name, value);
	}
	
	public void setBody(byte[] body) {
		this.body = body;
	}

	public void write() {
		try {
			this.dos.writeBytes(this.firstLine);
			if (!cookies.isEmpty()) {
				writeCookies();
			}
			writeHeaders();
			writeBody();
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeDebug() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.firstLine);
		if(!cookies.isEmpty()) {
			sb.append("Set-Cookie: ");
			for (String key : this.cookies.keySet()) {
				sb.append(key + "=" + cookies.get(key) + "; ");
			}
			sb.append("\r\n");
		}
		for (String key : this.headers.keySet()) {
			sb.append(key + ": " + headers.get(key) + "\r\n");
		}
		sb.append(body);
		System.out.println(sb.toString());
	}
	
	private void writeHeaders() {
		for (String key : this.headers.keySet()) {
			try {
				this.dos.writeBytes(key + ": " + headers.get(key) + "\r\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void writeCookies() {
		try {
			this.dos.writeBytes("Set-Cookie: ");
			for (String key : this.cookies.keySet()) {
				this.dos.writeBytes(key + "=" + cookies.get(key) + ";");
			}

			this.dos.writeBytes("\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeBody() {
		if(this.body == null)
			return;
		
		try {
			this.dos.writeBytes("\r\n");
			this.dos.write(this.body);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
