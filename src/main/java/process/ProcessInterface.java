package process;

import util.HTTPRequest;
import util.HTTPResponse;

public interface ProcessInterface {
	void process(HTTPRequest req, HTTPResponse res);
}
