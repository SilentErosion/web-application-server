package process;

import java.util.Collection;

import db.DataBase;
import model.User;
import util.HTTPRequest;
import util.HTTPResponse;

public class ListProcess extends Process {
	@Override
	public void process(HTTPRequest req, HTTPResponse res) {
		if(req.getCookies().get("logined").equals("true")) {
			Collection<User> users = DataBase.findAll();
			StringBuilder sb = new StringBuilder();
			for(User user : users) {
				sb.append(user.toString());
				sb.append("\r\n");
			}
			res.set200();
			res.setHeader("Content-Length", Integer.toString(sb.length()));
			res.setBody(sb.toString().getBytes());
			res.write();
		}
		else {
			res.set302("/login.html");
			res.write();
		}
	}
}
