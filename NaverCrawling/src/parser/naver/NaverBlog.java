/**
 * @FileName 	: NaverBlog.java
 * @Project 	: NaverCrowling
 * @Date 		: 2014. 10. 6.
 * @Author 		: NCri
 */
package parser.naver;

/**
 * @Class		: NaverBlog
 * @Date 		: 2014. 10. 6.
 * @Author 		: NCri
 */
public class NaverBlog implements NaverTarget{
	public static enum FieldName {
		TITLE("title"), BLOG_CONTENT("blogContent"), BLOGGER_NAME("bloggerName"),
		BLOG_LINK("bloggerLink"), BLOG_DATE("date"), BLOG_IMAGES("blogImage"),
		PLACE_NAME("place_name"), PLACE_IMAGE("link"), BLOG_TODAY_COUNT("today_count"),
		BLOG_SYMPATHY_COUNT("sympathy_count");
		public String value;
		FieldName(String value){
			this.value = value;
		}
	}
	
	private String _keyword;
	private String _targetName = "blog";
	
	private int _start = 1;
	private int _display = 100;
		
	/**
	 * @return the _start
	 */
	public int getStart() {
		return _start;
	}

	/**
	 * @return the _display
	 */
	public int getDisplay() {
		return _display;
	}

	/**
	 * @param _start the _start to set
	 */
	public void setStart(int start) {
		_start = start;
	}

	/**
	 * @param _display the _display to set
	 */
	public void setDisplay(int display) {
		_display = display;
	}

	/* (non-Javadoc)
	 * @see parser.naver.NaverTarget#setKeyword(java.lang.String)
	 */
	@Override
	public void setKeyword(String keyword) {
		// TODO Auto-generated method stub
		_keyword = keyword;
		
	}

	/* (non-Javadoc)
	 * @see parser.naver.NaverTarget#getKeyword()
	 */
	@Override
	public String getKeyword(){
		return _keyword;
	}
	
	/* (non-Javadoc)
	 * @see parser.naver.NaverTarget#setTargetName(java.lang.String)
	 */
	@Override
	public void setTargetName(String targetName) {
		// TODO Auto-generated method stub
		_targetName = targetName;
		
	}

	/* (non-Javadoc)
	 * @see parser.naver.NaverTarget#getTargetName()
	 */
	@Override
	public String getTargetName() {
		// TODO Auto-generated method stub
		return _targetName;
	}

}
