package util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;
import model.User;

public class HTTPRequestTest {
	private String getIndex = "GET /index.html HTTP/1.1\r\nHost: localhost:8080\r\nConnection: keep-alive\r\nAccept: */*";
	private String getCreate = "GET /user/create?userId=javajigi&password=password&name=JaeSung HTTP/1.1";
	private String postCreate = "POST /index.html HTTP/1.1\r\n"
			+ "Host: localhost:8080\r\n"
			+ "Connection: keep-alive\r\n"
			+ "Content-Length: 59\r\n"
			+ "Content-Type: application/x-www-form-urlencoded\r\n"
			+ "Accept: */*\r\n"
			+ "\r\n"
			+ "userId=javajigi&password=password&name=JaeSung";
	
	@Test
	public void printHeaders() throws IllegalArgumentException, IOException {
		HTTPRequest req = new HTTPRequest(postCreate);
		/*req.printHeaders();
		System.out.println(req.getBody());*/
	}
	


}
