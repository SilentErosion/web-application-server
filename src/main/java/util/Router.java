package util;

import java.util.HashMap;
import java.util.Map;

import process.ListProcess;
import process.LoginProcess;
import process.Process;
import process.ReturnResourceProcess;
import process.SignupProcess;

public class Router {
	private static Map<String, Process> router = new HashMap<String, Process>();
	
	private static final LoginProcess loginProcess = new LoginProcess();
	private static final ReturnResourceProcess resourceProcess = new ReturnResourceProcess();
	private static final SignupProcess signUpProcess = new SignupProcess();
	private static final ListProcess listProcess = new ListProcess();
	
	static {
		router.put("/user/login", loginProcess);
		router.put("/user/create", signUpProcess);
		router.put("/user/list", listProcess);
	}
	
	public static Process getProcessUnit(String url) {
		Process processUnit = router.get(url);
		
		if(processUnit == null) return resourceProcess;
		return processUnit;
	}
}
