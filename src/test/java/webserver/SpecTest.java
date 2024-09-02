package webserver;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import util.HttpRequestUtils.Pair;
import util.IOUtils;
import util.IOUtilsTest;

public class SpecTest {
	private static final Logger logger = LoggerFactory.getLogger(IOUtilsTest.class);

	@Test
	// Header에서 Content-Length를 읽는다
	public void getContentLength() throws IOException {
		int length;
		Pair pair;
		String header = "HOST: localhost:8080\r\nContent-Length: 59";
		StringReader sr = new StringReader(header);
		BufferedReader br = new BufferedReader(sr);

		do {
			pair = HttpRequestUtils.parseHeader(br.readLine());
		} while (!pair.getKey().equals("Content-Length"));
		length = Integer.parseInt(pair.getValue());
		assertEquals(length, 59);
	}

	@Test
	// HTML Body에서 문자열을 불러온다
	public void getQueryPost() throws IOException {
		String request = "POST /user/create HTTP/1.1\r\n\r\nuserId=javajigi&password=password&name=JaeSung";
		String body = "userId=javajigi&password=password&name=JaeSung";
		StringReader sr = new StringReader(request);
		BufferedReader br = new BufferedReader(sr);
		int contentLength = 59;

		while (!"".equals(br.readLine())) {
			// parse headers
		}
		assertEquals(body, IOUtils.readData(br, contentLength).trim());
	}

	@Test
	public void parseQuery() throws IOException {
		String url = "userId=javajigi&password=password&name=JaeSung";
		Map<String, String> queries = HttpRequestUtils.parseQueryString(url);

		assertThat(queries.get("userId"), is("javajigi"));
		assertThat(queries.get("password"), is("password"));
		assertThat(queries.get("name"), is("JaeSung"));
	}

	@Test
	public void write302() {
		String loc = "/index.html";
		StringBuilder sb = new StringBuilder();
		sb.append("HTTP/1.1 302 Found \r\n");
		sb.append("Location: " + loc);
		sb.append("\r\n");
		logger.debug(sb.toString());
	}

	@Test
	public void addUserToDB() {
		User usr = new User("id", "pwd", "name", "");
		User usr2 = new User("id2", "pwd2", "name2", "2");
		DataBase.addUser(usr);
		DataBase.addUser(usr2);
		Collection<User> res = DataBase.findAll();
		Iterator<User> iterator = res.iterator();
		 
        // while loop
        while (iterator.hasNext()) {
        	User thisUser = iterator.next();
        	logger.debug("id: " + thisUser.getUserId() + ", name: " + thisUser.getName());
        }
	}
	
	@Test
	public void loginSuc() {
		User usr = new User("id", "pwd", "name", "");
		DataBase.addUser(usr);
		String id = "id", pwd = "pwd";
		User dbUser = DataBase.findUserById(id);
		assertTrue(dbUser != null && dbUser.getName().equals(id) && dbUser.getPassword().equals(pwd));
	}

	@Test
	public void loginFail() {
		User usr = new User("id", "pwd", "name", "");
		DataBase.addUser(usr);
		String id = "id", pwd = "wrongPwd";
		User dbUser = DataBase.findUserById(id);
		assertFalse(dbUser != null && dbUser.getName().equals(id) && dbUser.getPassword().equals(pwd));
	}

	@Test
	public void checkCookies() {
		String cookieString = "Cookie: logined=true";
		StringBuilder sb = new StringBuilder();
		Map<String, String> cookies = HttpRequestUtils.parseCookies(HttpRequestUtils.parseHeader(cookieString).getValue());
		for(String key : cookies.keySet()) {
			sb.append(key + ": " + cookies.get(key) + "\r\n");
		}
		logger.debug(sb.toString());
	}
	
	private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	private void responseBody(DataOutputStream dos, byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.flush();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
}
