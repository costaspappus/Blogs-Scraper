package scraper.scr;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.blogger.Blogger;
import com.google.api.services.blogger.BloggerRequestInitializer;
import com.google.api.services.blogger.model.Blog;
import com.google.api.services.blogger.model.Post;
import com.google.api.services.blogger.model.PostList;
import dal.BloggerBlog;
import dal.BloggerPost;

/**
 * Given profile information and blog urls in MySQL this class will scrape the actual blog contents
 * using the Blogger API
 * @author Konstantinos Pappas
 *
 */
public class ScrapeBlogs {

	//your key -- 10K requests per day
	public static final String bloggerApiKey = "yourbloggerApiKey";
	
	//the application name
	public static final String applicationName = "yourapplicationName";
	
	//the google api for Blogger service
	public static Blogger bloggerService = null;
	
	//database information
	public static String mySqlDatabaseName = "yourmySqlDatabaseName";
	public static String profilesBlogsTableName = "profiles_blogs";
	public static String blogUrlColumnName = "url";
	public static String blogsTableName = "blogs";
	public static String postsTableName = "posts";
	public static String blogsPostsTableName = "blogs_posts";
	public static String scrapedBlogUrlColumnName = "url";
	
	public static PreparedStatement selectBlogUrls = null;
	public static PreparedStatement selectScrapedBlogUrls = null;
	public static PreparedStatement insertBlog = null;
	public static PreparedStatement insertPost = null;
	public static PreparedStatement insertBlogPost = null;
	
	public static int maxPostsPerBlog = 20;
	
	public static int toScrape = 0;
	public static int soFar = 0;
	
	//initialize static context
	static{
		//initialize the blogger service
		bloggerService = new com.google.api.services.blogger.Blogger.Builder(new NetHttpTransport(), new JacksonFactory(), null)
			.setApplicationName(applicationName).setGoogleClientRequestInitializer(new BloggerRequestInitializer(bloggerApiKey)).build();
		//initialize the MySQL connection
		try {
		      //load the MySQL driver
		      Class.forName("com.mysql.jdbc.Driver");
		      //setup the connection with MySQL, myblogs database
		      Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/"+mySqlDatabaseName+"?user=root&password=");
		      selectBlogUrls = connection.prepareStatement("SELECT "+blogUrlColumnName+" FROM "+mySqlDatabaseName+"."+profilesBlogsTableName+" ");
		      selectScrapedBlogUrls = connection.prepareStatement("SELECT "+scrapedBlogUrlColumnName+" FROM "+mySqlDatabaseName+"."+blogsTableName+";");
		      insertBlog = connection.prepareStatement("INSERT INTO "+blogsTableName+" (url, name, description, published, updated, locale_country, locale_language, locale_variant) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
		      insertPost = connection.prepareStatement("INSERT INTO "+postsTableName+" (url, title, content, published, author_url, location_latitude, location_longitude, location_name, location_span) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
		      insertBlogPost = connection.prepareStatement("INSERT INTO "+blogsPostsTableName+" (blog_url, post_url) VALUES (?, ?)");
		} catch(Exception e){
			System.out.println("Failed to connect to MySQL on static initializer: " + e.getMessage());
			System.out.println("Exiting..");
			System.out.flush();
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public static void main(String[] args) throws Exception{
		List<String> blogUrls = new ArrayList<String>();
		try{
			//get all blogUrls from the database
			ResultSet result = selectBlogUrls.executeQuery();
			//and insert them in our Java list
			while(result.next()){
				//get the first column of the result set
				blogUrls.add(result.getString(1));
			}
		} catch (Exception e){
			e.printStackTrace();
			System.exit(0);
		}
		
		//remove any blog already scraped
		List<String> blogsScraped = new ArrayList<String>();
		try{
			//get all scraped blogUrls from the database
			ResultSet result = selectScrapedBlogUrls.executeQuery();
			//and insert them in our Java list
			while(result.next()){
				//get the first column of the result set
				blogsScraped.add(result.getString(1));
			}
		} catch (Exception e){
			e.printStackTrace();
			System.exit(0);
		}
		blogUrls.removeAll(blogsScraped);
		toScrape = blogUrls.size();
		
		//for each blog url
		for(String blogUrl : blogUrls){
			soFar++;
			BloggerBlog myBlog = new BloggerBlog(blogUrl);
			try{
				//get the blog from the Blogger service
				Blog blog = bloggerService.blogs().getByUrl(blogUrl).setFields("id,name,url,description,published,updated,locale,posts").execute();
				//and create a BloggerBlog in our model
				myBlog.setName(blog.getName());
				myBlog.setDescription(blog.getDescription());
				myBlog.setPublished(blog.getPublished()!=null?blog.getPublished().getValue():null);
				myBlog.setUpdated(blog.getUpdated()!=null?blog.getUpdated().getValue():null);
				Blog.Locale locale = blog.getLocale();
				if(locale != null){
					String country = locale.getCountry(), language = locale.getLanguage(), variant = locale.getVariant();
					if(country != null){
						myBlog.setLocaleCountry(country.length()==0?null:country);
					}
					if(language != null){
						myBlog.setLocaleLanguage(language.length()==0?null:language);
					}
					if(variant != null){
						myBlog.setLocaleVariant(variant.length()==0?null:variant);
					}
				}
				//if the blog has posts save it in the database
				if(blog != null && blog.getPosts().getTotalItems() > 0){
					//if blog is successfully save then save its posts
					if(saveBlog(myBlog)){
						//retrieve and save all posts of that blog
						getAndSavePostsForBlog(blog);
					}
				}
			} catch(com.google.api.client.googleapis.json.GoogleJsonResponseException e){
				//identify the daily limit exceeded exception and wait a 24 hours
				System.out.println("Google exception on blog level: " + e.getMessage());
				System.out.flush();
				e.printStackTrace();
				if(e.toString() != null && e.toString().contains("dailyLimitExceeded")){
					waitADay();
				}
			} catch(Exception e){
				System.out.println("Exception on blog level: " + e.getMessage());
				System.out.flush();
				e.printStackTrace();
			}
		}		
	}
	
	/**
	 * Given a blog this method retrieves and saves its posts
	 * @param blog
	 * @return
	 */
	public static void getAndSavePostsForBlog(Blog blog){
		//for reference, see: https://developers.google.com/blogger/docs/3.0/reference/posts/list
		int postsForThisBlog = 0;
		try{
			//restrict the result content to just the data we need
			Blogger.Posts.List postsListAction = bloggerService.posts().list(blog.getId()).setFields("items(content,title,url,published,author,location),nextPageToken");
			//this step sends the request to the server
			PostList posts = postsListAction.execute();
			//now we can navigate the response
			while (posts.getItems() != null && !posts.getItems().isEmpty()) {
		        for (Post post : posts.getItems()) {
	                BloggerPost myPost = new BloggerPost(post.getUrl());
	                myPost.setTitle(post.getTitle());
	                myPost.setContent(post.getContent());
	                myPost.setPublished(post.getPublished()!=null?post.getPublished().getValue():null);
	                myPost.setAuthorUrl(post.getAuthor()!=null?post.getAuthor().getUrl():null);
	                Post.Location location = post.getLocation();
	                if(location != null){
		                myPost.setLocationLatitude(location.getLat());
		                myPost.setLocationLongitude(location.getLng());
		                myPost.setLocationName(location.getName());
		                myPost.setLocationSpan(location.getSpan());
	                }
	                savePost(myPost, blog.getUrl());
	                postsForThisBlog++;
	                //stop saving posts for this blog if limit is reached
	                if(postsForThisBlog > maxPostsPerBlog){
	                	return;
	                }
		        }
		        // Pagination logic
		        String pageToken = posts.getNextPageToken();
		        if (pageToken == null) {
	                break;
		        }
		        postsListAction.setPageToken(pageToken);
				//restrict the result content to just the data we need
				postsListAction.setFields("items(content,title,url,published,author,location),nextPageToken");
				//this step sends the request to the server
		        posts = postsListAction.execute();
			}
		} catch(com.google.api.client.googleapis.json.GoogleJsonResponseException e){
			System.out.println("Google exception on post level: " + e.getMessage());
			System.out.flush();
			e.printStackTrace();
			if(e.toString() != null && e.toString().contains("dailyLimitExceeded")){
				waitADay();
			}
		} catch(Exception e){
			System.out.println("Exception on post level: " + e.getMessage());
			System.out.flush();
			e.printStackTrace();
		}
	}
	
	/**
	 * Save the given BloggerBlog in the database
	 */
	public static boolean saveBlog(BloggerBlog blog){
		try{
			insertBlog.setString(1, blog.getUrl());
			insertBlog.setString(2, blog.getName());
			insertBlog.setString(3, blog.getDescription());
			insertBlog.setString(4, blog.getPublished()+"");
			insertBlog.setString(5, blog.getUpdated()+"");
			insertBlog.setString(6, blog.getLocaleCountry());
			insertBlog.setString(7, blog.getLocaleLanguage());
			insertBlog.setString(8, blog.getLocaleVariant());
			insertBlog.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Save the given BloggerPost in the database and associate it with the given blog url
	 */
	public static void savePost(BloggerPost post, String blogUrl){
		//save the post
		try{
			insertPost.setString(1, post.getUrl());
			insertPost.setString(2, post.getTitle());
			insertPost.setString(3, post.getContent());
			insertPost.setString(4, post.getPublished()+"");
			insertPost.setString(5, post.getAuthorUrl());
			insertPost.setString(6, post.getLocationLatitude()!=null?post.getLocationLatitude()+"":null);
			insertPost.setString(7, post.getLocationLongitude()!=null?post.getLocationLongitude()+"":null);
			insertPost.setString(8, post.getLocationName());
			insertPost.setString(9, post.getLocationSpan());
			insertPost.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
		//save the association with the blog
		try{
			insertBlogPost.setString(1, blogUrl);
			insertBlogPost.setString(2, post.getUrl());
			insertBlogPost.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * When this function is called the program prints the date and time and freezes for a day
	 * It then resets the requests counter and gets a fresh connection with the database
	 */
	public static void waitADay(){
		System.out.println("Will wait for 10 hours now: " + (new Date()).toString());
		System.out.println("All to scrape: " + toScrape + ". So far: " + soFar + ".");
		System.out.flush();
		try {
			//Note: sometimes even though a whole day has not passed, the Blogger API will still send results therefore, here we only wait 10 hours.
			//sleep a day
			//Thread.sleep(86400000);
			Thread.sleep(36000000);
			//refresh the MySQL connection because it may have been closed
			try {
			      //load the MySQL driver
			      Class.forName("com.mysql.jdbc.Driver");
			      //setup the connection with MySQL, myblogs database
			      Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/"+mySqlDatabaseName+"?user=root&password=");
			      //selectBlogUrls = connection.prepareStatement("SELECT "+blogUrlColumnName+" FROM "+mySqlDatabaseName+"."+profilesBlogsTableName+";");
			      //selectScrapedBlogUrls = connection.prepareStatement("SELECT "+scrapedBlogUrlColumnName+" FROM "+mySqlDatabaseName+"."+blogsTableName+";");
			      insertBlog = connection.prepareStatement("INSERT INTO "+blogsTableName+" (url, name, description, published, updated, locale_country, locale_language, locale_variant) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
			      insertPost = connection.prepareStatement("INSERT INTO "+postsTableName+" (url, title, content, published, author_url, location_latitude, location_longitude, location_name, location_span) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
			      insertBlogPost = connection.prepareStatement("INSERT INTO "+blogsPostsTableName+" (blog_url, post_url) VALUES (?, ?)");
			} catch(Exception e){
				System.out.println("Failed to reconnect to MySQL on waitADay: " + e.getMessage());
				System.out.println("Exiting..");
				System.out.flush();
				e.printStackTrace();
				System.exit(0);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
