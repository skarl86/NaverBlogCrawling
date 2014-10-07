import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import parser.Connector;
import parser.naver.NaverBlog;

/**
 * @FileName 	: Application.java
 * @Project 	: NaverCrowling
 * @Date 		: 2014. 10. 6.
 * @Author 		: NCri
 */

/**
 * @Class		: Application
 * @Date 		: 2014. 10. 6.
 * @Author 		: NCri
 */
public class Application {
	public static void main(String[] args){
		String keyword = "맛집";
		Connector conn = Connector.getInstance(Connector.Target.NAVER_BLOG);
		conn.connect(keyword);
		ArrayList<Map<String, String>> results = conn.getResults();
		int count = 0;
		BufferedWriter bw ;
		try {
			bw = new BufferedWriter(new FileWriter(keyword + ".txt"));
			while(( results = conn.nextResult() ) != null){
				for(Map<String, String> result : results){
					bw.write((count++) + "\t" + result.get(NaverBlog.FieldName.BLOG_CONTENT.value));
				}
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
