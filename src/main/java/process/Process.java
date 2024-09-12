package process;

import util.HTTPRequest;
import util.HTTPResponse;

public abstract class Process implements ProcessInterface {
	abstract public void process(HTTPRequest req, HTTPResponse res);
}
