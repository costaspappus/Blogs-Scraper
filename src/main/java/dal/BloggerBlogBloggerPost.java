package dal;

/**
 * 
 * @author Konstantinos Pappas
 *
 */
public class BloggerBlogBloggerPost {
	
	private String blogUrl;
	
	private String postUrl;
	
	public BloggerBlogBloggerPost(String blogUrl, String postUrl){
		this.blogUrl = blogUrl;
		this.postUrl = postUrl;
	}

	public String getBlogUrl() {
		return blogUrl;
	}

	public void setBlogUrl(String blogUrl) {
		this.blogUrl = blogUrl;
	}

	public String getPostUrl() {
		return postUrl;
	}

	public void setPostUrl(String postUrl) {
		this.postUrl = postUrl;
	}

	@Override
	public String toString(){
		return "BlogPost: {" +
	            "{blogUrl: " + blogUrl + "}, " +
	            "{postUrl: " + postUrl + "} " +
	            "}";
	}

}
