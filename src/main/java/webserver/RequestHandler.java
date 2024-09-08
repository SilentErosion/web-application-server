package webserver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HTTPRequest;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
	private static final String SOURCE_DIR = "./webapp";
	private Socket connection;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
				connection.getPort());

		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
			// TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
			HTTPRequest req = new HTTPRequest(in);
			String url = req.getUrl();
			DataOutputStream dos = new DataOutputStream(out);
			
			// 일단은 if-else로 라우팅
			if(url.equals("/user/login")) {
				if(req.getCookies().get("logined") != null && req.getCookies().get("logined").equals("true")) {
					response302(dos, "/index.html");
					return;
				}
				
				Map<String, String> params = HttpRequestUtils.parseQueryString(req.getBody());
				String id = params.get("userId");
				String pwd = params.get("password");
				
				User dbUser = DataBase.findUserById(id);
				if(dbUser == null || !dbUser.getPassword().equals(pwd)) {
					dos.writeBytes("HTTP/1.1 302 Found \r\n");
					dos.writeBytes("Location: ");
					dos.writeBytes("login_failed.html");
					dos.writeBytes("\r\n");
					dos.writeBytes("Set-Cookie: logined=false");
					dos.flush();
					return;
				}
				dos.writeBytes("HTTP/1.1 200 OK \r\n");
				dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
				dos.writeBytes("Set-Cookie: logined=true");
				dos.writeBytes("\r\n");
				dos.flush();
			}
			else if(url.startsWith("/user/create")) {
				String reqUrl = null;
				if(req.getMethod().equals("POST")) {
					reqUrl = req.getBody();
				}
				
				int idx = url.indexOf("?");
				reqUrl = url.substring(idx+1); 
				addUser(reqUrl);
				
				response302(dos, "/index.html");
			}
			
			else if(url.equals("/user/list")) {
				if(req.getCookies().get("logined").equals("true")) {
					Collection<User> users = DataBase.findAll();
					StringBuilder sb = new StringBuilder();
					for(User user : users) {
						sb.append(user.toString());
						sb.append("\r\n");
					}
					response200Header(dos, sb.length());
					responseBody(dos, sb.toString().getBytes());
				}
				else {
					response302(dos, "/login.html");
				}
			}
			else if(url.endsWith(".css")) {
				String filepath = SOURCE_DIR + url;
				byte[] body = Files.readAllBytes(new File(filepath).toPath());
				dos.writeBytes("HTTP/1.1 200 OK \r\n");
				dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
				dos.writeBytes("Content-Length: " + body.length + "\r\n");
				dos.writeBytes("\r\n");
				responseBody(dos, body);
			}
			else if(url.endsWith(".html")) {
				String filepath = SOURCE_DIR + url;
				byte[] body = Files.readAllBytes(new File(filepath).toPath());
				response200Header(dos, body.length);
				responseBody(dos, body);
			}
			
			// Show HelloWorld
			/*DataOutputStream dos = new DataOutputStream(out);
			byte[] body = "Hello World".getBytes();
			response200Header(dos, body.length);
			responseBody(dos, body);*/

		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void responseBody(DataOutputStream dos, byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void response302(DataOutputStream dos, String loc) {
		try {
			dos.writeBytes("HTTP/1.1 302 Found \r\n");
			dos.writeBytes("Location: ");
			dos.writeBytes(loc);
			dos.writeBytes("\r\n");
			dos.flush();
			
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	
	private void addUser(String reqUrl) {
		Map<String, String> params = HttpRequestUtils.parseQueryString(reqUrl);
		String id = params.get("userId");
		String pwd = params.get("password");
		String name = params.get("name");
		String email = params.get("email");
		User user = new User(id, pwd, name, email);
		DataBase.addUser(user);
	}
}
