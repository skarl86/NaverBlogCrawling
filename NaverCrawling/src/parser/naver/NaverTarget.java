/**
 * @FileName 	: NaverTaget.java
 * @Project 	: NaverCrowling
 * @Date 		: 2014. 10. 6.
 * @Author 		: NCri
 */
package parser.naver;

/**
 * @Class		: NaverTaget
 * @Date 		: 2014. 10. 6.
 * @Author 		: NCri
 */
abstract public interface NaverTarget {
	abstract public void setTargetName(String targetName);
	abstract public String getTargetName();
	abstract public void setKeyword(String keyword);
	abstract public String getKeyword();
}
