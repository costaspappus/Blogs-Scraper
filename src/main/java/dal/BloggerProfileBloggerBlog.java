package dal;

/**
 * 
 * @author Konstantinos Pappas
 *
 */
public class BloggerProfileBloggerBlog {
	
	private String profileUrl;
	
	private String blogUrl;
	
	public BloggerProfileBloggerBlog(String profileUrl, String blogUrl){
		this.profileUrl = profileUrl;
		this.blogUrl = blogUrl;
	}
	
	public String getProfileUrl() {
		return profileUrl;
	}

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}

	public String getBlogUrl() {
		return blogUrl;
	}

	public void setBlogUrl(String blogUrl) {
		this.blogUrl = blogUrl;
	}

	@Override
	public String toString(){
		return "ProfileBlog: {" +
	            "{profileUrl: " + profileUrl + "}, " +
	            "{blogUrl: " + blogUrl + "} " +
	            "}";
	}
	
}
