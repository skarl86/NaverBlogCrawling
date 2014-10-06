package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import parser.naver.NaverBlog;
import parser.naver.NaverConnector;

abstract public class Connector {
	public static enum Target{
		NAVER_BLOG
	}
//	public final static int NAVER_LOCAL = 0;
//	public final static int NAVER_BLOG = 1;
//	public final static int GOOGLE_PLACE = 2;
//	public final static int TWITTER = 3;
//	public final static int NAVER_IMAGE = 4;
	
	protected String _baseURL;
	protected int type;
	public static Connector getInstance(Target targetType){
		Map<Target, Connector> instanceMap = new HashMap<Target, Connector>();
		NaverBlog blog = new NaverBlog();
		// init instance
		instanceMap.put(Target.NAVER_BLOG, new NaverConnector(blog));
		
		return instanceMap.get(targetType);
	}
	public ArrayList<Map<String, String>> nextResult(){
		// none action.
		return null;
	}
	abstract public Object connect(String keyword);
	abstract public ArrayList<Map<String, String>> getResults();
}
