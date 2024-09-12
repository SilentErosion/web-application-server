package process;

import util.HTTPRequest;
import util.HTTPResponse;
import util.HttpRequestUtils;

import java.util.Map;

import db.DataBase;
import model.User;

public class SignupProcess extends Process {
	@Override
	public void process(HTTPRequest req, HTTPResponse res) {
		String queryString = req.getQueryString();
		addUser(queryString);
		res.set302("/index.html");
		res.write();
	}
	
	private void addUser(String queryString) {
		Map<String, String> params = HttpRequestUtils.parseQueryString(queryString);
		String id = params.get("userId");
		String pwd = params.get("password");
		String name = params.get("name");
		String email = params.get("email");
		User user = new User(id, pwd, name, email);
		DataBase.addUser(user);
	}
}
