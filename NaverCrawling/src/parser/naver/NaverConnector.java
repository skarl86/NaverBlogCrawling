/**
 * @FileName 	: NaverConnector.java
 * @Project 	: NaverCrowling
 * @Date 		: 2014. 10. 6.
 * @Author 		: NCri
 */
package parser.naver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import parser.Connector;
import parser.naver.NaverTarget;

/**
 * @Class		: NaverConnector
 * @Date 		: 2014. 10. 6.
 * @Author 		: NCri
 */
public class NaverConnector extends Connector{
	private NaverTarget _target;
	private final String _API_KEY = "a0504c8ef72934d16be6d2a29e5b69aa";
	private final String _BASE_URL = "http://openapi.naver.com/search?target=%s";
	private String _xmlData;
	
	public NaverConnector(NaverTarget target){
		_target = target;
	}
	
	public void setStartAndDisplay(int start, int display){
		NaverBlog aTarget = (NaverBlog)_target;
		aTarget.setStart(start);
		aTarget.setDisplay(display);
	}
	
	/* (non-Javadoc)
	 * @see parser.Connector#connect(java.lang.Object)
	 */
	@Override
	public Object connect(String keyword) {
		// TODO Auto-generated method stub
		NaverBlog aTarget = (NaverBlog)_target;
		aTarget.setKeyword(keyword);
		String completeUrl = String.format(_BASE_URL, aTarget.getTargetName());
		URL url;
		Document doc;
		Elements elements = null;
		
		try {
			// Initialize API URL.
			int start = aTarget.getStart();
			int display = aTarget.getDisplay();
			
			completeUrl += _getParam(keyword, display, start);
			 
			url = new URL(completeUrl);
			doc = Jsoup.parse(url, 10000);

			// Check range of contents
			elements = doc.getElementsByTag("total");
						
			if(Integer.parseInt(elements.get(0).text()) < start){
				return null;
			}
			
			_xmlData = doc.toString();

			return _xmlData;
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			System.out.println("MalformedURLException is occured");
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			System.out.println("IOException is occured");
		}
		// When exception is occured null is returned.
		return null;
	}

	/**
	 * 네이버 API를 리퀘스트할 주소를 만드는 메소드
	 * @param keyword
	 * @return
	 */
	private String _getParam(String keyword, int display, int start){
		return String.format("&key=%s&query=%s&display=%d&start=%d",
				_API_KEY, keyword.replace(" ", "+"), display, start);
	}
	public ArrayList<Map<String, String>> nextResult(){
		NaverBlog aTarget = (NaverBlog)_target;
		int start = aTarget.getStart();
		int display = aTarget.getDisplay();

		connect(_target.getKeyword());
		
		start += 100;
		if(start == 901)
			display = 99; // 901~999
		else if(start == 1001){
            start = 1000;
            display = 100; // 1000~1099
		}
		
		aTarget.setStart(start);
		aTarget.setDisplay(display);
		
		return getResults();
	}
	/* (non-Javadoc)
	 * @see parser.Connector#getData(java.util.Map)
	 */
	@Override
	public ArrayList<Map<String, String>> getResults() {
		int success = 0, fail = 0;
		if(_xmlData == null || _xmlData.length() < 1)
			return null;
		
		ArrayList<Map<String, String>> resultList = new ArrayList<Map<String,String>>();
		Map<String, String> resultMap;
		Map<String, String> blogContent;
		String title, writer, link;
		Document doc;
		Elements elements;
		
		doc = Jsoup.parse(_xmlData);
		elements = doc.getElementsByTag("item");
		
		for(Element e : elements){
			title = e.getElementsByTag("title").text().replaceAll("(<b>|</b>)", "");
			writer = e.getElementsByTag("bloggername").text();
			
			try{
				link = _getLink(e.toString()); // 블로그의 주소를 
			}catch (StringIndexOutOfBoundsException exception){
				fail++;
				continue;
			}
			
			blogContent = _getBlogContent(link); // 블로그에 직접 접속하여 필요한 정보를 파싱
			if(blogContent == null){
				fail++;
				continue;
			}
			success++;
			
			resultMap = new HashMap<String, String>();
			resultMap.putAll(blogContent);
			resultMap.put(NaverBlog.FieldName.TITLE.value, title);
			resultMap.put(NaverBlog.FieldName.BLOGGER_NAME.value, writer);
			resultList.add(resultMap);
			blogContent = null;
			resultMap = null;
		}
		
		System.out.printf("Success : %d, Fail : %d\n", success, fail);
		if(resultList.size() <= 0)
			resultList = null;
		return resultList;	
	}
	
	private Map<String, String> _getBlogContent(String naverAPIUrl){
		Document doc;
		String src, logNo;
		Map<String, String> result = new HashMap<String, String>();
		int start, end;
		
		try {
			// Jsoup의 커넥터를 이용하면 네이버API에서 주는 주소를 인식 못해 오류발생..
			URL url = new URL(naverAPIUrl);
			doc = Jsoup.parse(url, 5000);
			
			// mainFrame에 포함되어 있는 주소를 추출
			src = doc.getElementById("mainFrame").toString().replace("&amp;", "&");
			start = src.indexOf("src=") + 5;
			end = src.indexOf("&beginTime");
			src = src.substring(start, end);
			src = "http://blog.naver.com" + src;
			start = src.indexOf("logNo=") + 6;
			logNo = src.substring(start); // Parse post number
			
			// URL의 커넥터를 사용하면 Jsoup에서 본문내용을 파싱하지 못함...
			doc = Jsoup.connect(src).get();
			result.put(NaverBlog.FieldName.BLOG_DATE.value, doc.getElementsByClass("_postAddDate").text().trim().replaceAll("/", "-"));
			Element el = doc.getElementById("post-view" + logNo);
			String content = el.text().trim(); //+ "\n" + _getAddressInBlog(el);
			result.put(NaverBlog.FieldName.BLOG_CONTENT.value, content);
//			result.put(NaverBlog.FieldName.BLOG_CONTENT.value, doc.getElementById("post-view" + logNo).text().trim());
			result.put(NaverBlog.FieldName.BLOG_IMAGES.value, _getImageLinkOfBlog(logNo, doc));
			result.put(NaverBlog.FieldName.BLOG_LINK.value, src);
			
			if(result.size() <= 0)
				result = null;
			return result;
		} catch (SocketTimeoutException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
//			System.out.println("It is not naver blog.");
		} catch (IndexOutOfBoundsException e) {
//			System.out.println("Date is not exists");
		}
		return null;
	}
	
	/**
	 * 네이버 API를 통해 얻어온 XML에서 블로그 주소를 파싱 (JSoup이 파싱하지 못함..)
	 * @param item 네이버 API에서 넘겨주는 XML에서 <item>태그에 포함된 내용
	 * @return 블로그 주소
	 * @throws StringIndexOutOfBoundsException
	 */
	private String _getLink(String item) throws StringIndexOutOfBoundsException {
		int start, end;
		start = item.indexOf("<link />");
		end = item.indexOf("<description>");
		item = item.substring(start+8, end);
		return item;
	}
	
	/**
	 * 블로그에 포함된 이미지 중 유의미한 이미지를 파싱한다
	 * @param logNo 네이버 블로그에서 블로그 번호
	 * @param doc 이미지가 포함되어있는 블로그의 Document
	 * @return 이미지 주소 리스트
	 */
	private String _getImageLinkOfBlog(String logNo, Document doc){
		String result = "";
		// 본문에서 img태그를 모두 파싱한 후 "_photoImage" class 속성을 포함한 이미지의 URL을 파싱
		Elements imageLinks = doc.getElementById("post-view" + logNo).getElementsByTag("img");
		for(Element image : imageLinks)
			if(image.attr("class").equals("_photoImage"))
				result += image.attr("id") + "\t";
		
		return result;
	}
	
	/**
	 * 블로그에 포함된 네이버 지도에서 주소를 파싱한다
	 * @param element 네이버 지도가 포함되어있는 Element
	 * @return 성공 : 파싱된 주소, 실패 : 공백
	 */
	private String _getAddressInBlog(Element element) {
		try {
			Elements elements = element.getElementsByTag("iframe");
			String mapUrl = "";
			
			// 지도가 포함된 <iframe> 태그를 찾음
			for(int i = 0; i < elements.size(); i++) {
				if(elements.get(i).attr("title").equals("포스트에 첨부된 지도"))
					mapUrl =  elements.get(i).attr("src");
			}
			if(mapUrl.equals(""))	//지도가 없는 블로그일 때 공백 반환
				return "";
			
			// 네이버 지도로 접속하여 지도에 포함된 주소를 파싱
			URL url = new URL(mapUrl);
			Document doc = Jsoup.parse(url, 5000);
			String javascript = doc.toString();
			int start = javascript.indexOf("address")+12;
			int end = javascript.indexOf(",", start)-2;
			return javascript.substring(start, end);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
