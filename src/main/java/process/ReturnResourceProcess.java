package process;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import util.HTTPRequest;
import util.HTTPResponse;

public class ReturnResourceProcess extends Process {
	private static final String SOURCE_DIR = "./webapp";
	private String filePath;
	
	@Override
	public void process(HTTPRequest req, HTTPResponse res) {
		String url = req.getPath();
		filePath = SOURCE_DIR + url;
		byte[] body = null;
		try {
			body = Files.readAllBytes(new File(filePath).toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		res.set200();
		res.setBody(body);
		res.setHeader("Content-Length", Integer.toString(body.length));
		
		if(url.endsWith(".html")) {
			res.setHeader("Content-Type", "text/html;charset=utf-8");
		}
		else if(url.endsWith(".css")) {
			res.setHeader("Content-Type", "text/css;charset=utf-8");
		}
		
		res.write();
	}
}
