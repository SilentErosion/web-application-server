package process;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HTTPRequest;
import util.HTTPResponse;
import util.HttpRequestUtils;

public class LoginProcess extends Process {
	private static final Logger logger = LoggerFactory.getLogger(LoginProcess.class);
	
	@Override
	public void process(HTTPRequest req, HTTPResponse res) {	
		Map<String, String> params = HttpRequestUtils.parseQueryString(req.getBody());
		String id = params.get("userId");
		String pwd = params.get("password");
		User dbUser = DataBase.findUserById(id);
		if(dbUser == null || !dbUser.getPassword().equals(pwd)) {
			res.set302("/user/login_failed.html");
			res.write();
			return;
		}
		res.set303("/index.html");
		res.setCookie("logined", "true"); // Domain 설정 안 해주면 index에서 cookie 안 보임
		res.writeDebug();
		res.write();
	}
}
