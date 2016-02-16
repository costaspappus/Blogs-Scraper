package scraper.scr;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import dal.BloggerProfile;
import java.sql.DriverManager;

/**
 * 
 * Given a file with blogger profiles urls this class will scrape the profile contents and save them to MySQL connection
 * Important: Google blocks the IP if you try to scrape more than about 90 blogs per half an hour. So, after 90 blogs we wait for 30 minutes.
 * @author Konstantinos Pappas
 *
 */
public class ScrapeProfiles {

	public static String filePath = "yourFilePath";
	
	public static String mySqlDatabaseName = "yourmySqlDatabaseName";
	public static String profilesTableName = "yourprofilesTableName";
	public static String profilesBlogsTableName = "yourprofilesBlogsTableName";
	public static String profilesBlogsFollowedTableName = "yourprofilesBlogsFollowedTableName";
	public static String ipAddress = "youripAddress";
	
	public static String[] interestingCategories = {"Gender", "Industry", "Occupation", "Location", "Introduction",
	                                                "Interests", "Favorite Movies", "Favorite Music", "Favorite Books"};
	
	public static PreparedStatement insertProfile = null;
	public static PreparedStatement insertProfileBlog = null;
	public static PreparedStatement insertProfileBlogFollowed = null;
	
	//initialize static variables
	static{
		try {
		      //load the MySQL driver
		      Class.forName("com.mysql.jdbc.Driver");
		      //setup the connection with MySQL, myblogs database
		      Connection connection = DriverManager.getConnection("jdbc:mysql://"+ipAddress+":3306/"+mySqlDatabaseName+"?user=root&password=");
		      insertProfile = connection.prepareStatement("INSERT INTO "+profilesTableName+" (url, gender, industry, occupation, city, state, country, introduction, interests, movies, music, books, name, image_url, email, web_page_url, instant_messaging_service, instant_messaging_username) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		      insertProfileBlog = connection.prepareStatement("INSERT INTO "+profilesBlogsTableName+" (profile_url, blog_url) VALUES (?, ?)");
		      insertProfileBlogFollowed = connection.prepareStatement("INSERT INTO "+profilesBlogsFollowedTableName+" (profile_url, blog_url) VALUES (?, ?)");
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){

		File file = new File(filePath);
		
		try{
			//read the lines from the profile urls file
			@SuppressWarnings("unchecked")
			List<String> lines = FileUtils.readLines(file, "UTF-8");
			
			int blogsScrapped = 0;
			
			//for each profile URL
			for (String profileURL : lines) {

				BloggerProfile profile = scrapeProfile(profileURL);
				blogsScrapped++;
				if(profile.getBlogUrls().size() != 0){
					
					//save the analyzed info in the MySQL database
					saveProfile(profile);
				}
				//if number of blogs scrapped is multiple of 90 wait for 30 minutes
				if(blogsScrapped % 90 == 0){
					Thread.sleep(1800000);
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		 
	}
	
	/**
	 * Given a profile url, this method scrapes this profile and returns a BloggerProfile object
	 * @param profileURL
	 * @return
	 */
	public static BloggerProfile scrapeProfile(String profileURL){
		
		BloggerProfile profile = new BloggerProfile(profileURL);
		
		try {
			//get request to the given url and parse the result to an HTML document
			Document doc = Jsoup.connect(profileURL).timeout(50000).get();
			
			populateInterestingCategories(profile, doc);
			
			populateBlogs(profile, doc);
			
			populateName(profile, doc);
			
			populateImageUrl(profile, doc);
			
			populateEmail(profile, doc);
			
			populateWebPageUrl(profile, doc);
			
			populateInstantMessagingInfo(profile, doc);
			
			populateBlogsFollowed(profile, doc);
			
		} catch(Exception e){
			e.printStackTrace();
		}
		return profile;
		
	}
	
	/**
	 * This method saves incoming profiles to the open connection to MySQL
	 */
	public static void saveProfile(BloggerProfile profile){
		//set profile parameters
		try{
			insertProfile.setString(1, profile.getUrl());
			if(profile.getGender() == null){
				insertProfile.setString(2, null);
			} else {
				//1 means male, 0 means female in the database
				insertProfile.setString(2, profile.getGender().equalsIgnoreCase("Male")?"1":"0");
			}
			insertProfile.setString(3, profile.getIndustry());
			insertProfile.setString(4, profile.getOccupation());
			insertProfile.setString(5, profile.getCity());
			insertProfile.setString(6, profile.getState());
			insertProfile.setString(7, profile.getCountry());
			insertProfile.setString(8, profile.getIntroduction());
			insertProfile.setString(9, profile.getInterests());
			insertProfile.setString(10, profile.getMovies());
			insertProfile.setString(11, profile.getMusic());
			insertProfile.setString(12, profile.getBooks());
			insertProfile.setString(13, profile.getName());
			insertProfile.setString(14, profile.getImageUrl());
			insertProfile.setString(15, profile.getEmail());
			insertProfile.setString(16, profile.getWebPageUrl());
			insertProfile.setString(17, profile.getInstantMessagingService());
			insertProfile.setString(18, profile.getInstantMessagingUsername());
			//set the profile in the database
			insertProfile.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
		
		//for each blog url on this profile
		for(String blogURL : profile.getBlogUrls()){
			//set profile_blog parameters
			try{
				insertProfileBlog.setString(1, profile.getUrl());
				insertProfileBlog.setString(2, blogURL);
				//save the association in the database
				insertProfileBlog.executeUpdate();
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
		}
		
		//for each blog followed on this profile
		for(String blogFollowedUrl : profile.getBlogsFollowedUrls()){
			//set profile_blog parameters
			try{
				insertProfileBlogFollowed.setString(1, profile.getUrl());
				insertProfileBlogFollowed.setString(2, blogFollowedUrl);
				//save the association in the database
				insertProfileBlogFollowed.executeUpdate();
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
		}
		
	}
	
	/**
	 * Given a blogger profile HTML document this method parses it to discover the fields included in the interestingCategories matrix
	 * @param profile
	 * @param doc
	 */
	public static void populateInterestingCategories(BloggerProfile profile, Document doc){
		//get the table element of the HTML page
		Element table = doc.select("table").first();
		//for each <tr> element
		for (Element element : table.select("tr")) {
			//get the element that has the attribute class="item-key" and extract the value in it
			String category = element.getElementsByAttributeValue("class", "item-key").text();
			
			if(category.equalsIgnoreCase(interestingCategories[0])){ //Gender
				String gender = element.getElementsByTag("td").text();
				if(gender != null){
					//for male gender we store true
					profile.setGender(gender);
				}
			} else if(category.equalsIgnoreCase(interestingCategories[1])){ //Industry
				profile.setIndustry(element.getElementsByTag("td").text());
			} else if(category.equalsIgnoreCase(interestingCategories[2])){ //Occupation
				profile.setOccupation(element.getElementsByTag("td").text());
			} else if(category.equalsIgnoreCase(interestingCategories[3])){ //Location
				for(Element el : element.getElementsByTag("span")){
					if(el.hasClass("locality")){
						profile.setCity(el.text().replaceAll(",", "")); // City
					} else if(el.hasClass("region")){
						profile.setState(el.text().replaceAll(",", "")); // State
					} else if(el.hasClass("country-name")){
						profile.setCountry(el.text().replaceAll(",", "")); // Country
					}
				}
			} else if(category.equalsIgnoreCase(interestingCategories[4])){ //Introduction
				profile.setIntroduction(element.getElementsByTag("td").text());
			} else if(category.equalsIgnoreCase(interestingCategories[5])){ //Interests
				profile.setInterests(element.getElementsByTag("td").text());
			} else if(category.equalsIgnoreCase(interestingCategories[6])){ //Favorite Movies
				profile.setMovies(element.getElementsByTag("td").text());
			} else if(category.equalsIgnoreCase(interestingCategories[7])){ //Favorite Music
				profile.setMusic(element.getElementsByTag("td").text());
			} else if(category.equalsIgnoreCase(interestingCategories[8])){ //Favorite Books
				profile.setBooks(element.getElementsByTag("td").text());
			}
		}
	}
	
	/**
	 * Given a blogger profile HTML document this method extracts the blogs urls for this profile
	 * @param blogs
	 * @param doc
	 */
	public static void populateBlogs(BloggerProfile profile, Document doc){
		
		List<String> blogs = new ArrayList<String>();
		//for each blog in this profile
		for (Element element : doc.getElementsByAttributeValue("rel", "me")) {
			String blogUrl = element.attr("href");
			blogs.add(blogUrl);
		}
		
		//set the profile blogs
		profile.setBlogUrls(blogs);
	}

	/**
	 * Given a blogger profile HTML document this method extracts the name for this profile
	 * @param blogs
	 * @param doc
	 */
	public static void populateName(BloggerProfile profile, Document doc){
		Element title = doc.select("title").first();
		String name = title.text().split("Blogger: User Profile:")[1].trim();
		profile.setName(name);
	}
	
	/**
	 * Given a blogger profile HTML document this method extracts the image url for this profile
	 * @param blogs
	 * @param doc
	 */
	public static void populateImageUrl(BloggerProfile profile, Document doc){
		Element element = doc.getElementsByAttributeValue("class", "photo").first();
		profile.setImageUrl(element!=null?element.attr("src"):null);
	}
	
	/**
	 * Given a blogger profile HTML document this method extracts the email for this profile
	 * @param blogs
	 * @param doc
	 */
	public static void populateEmail(BloggerProfile profile, Document doc){
		for(Element element : doc.getElementsByAttributeValue("type", "text/javascript")){
			String scriptValue = element.data();
			if(scriptValue.contains("printEmail(\"")){
				profile.setEmail(scriptValue.split("printEmail\\(\"blog")[1].split(".biz\",")[0]);
			}
		}
	}
	
	/**
	 * Given a blogger profile HTML document this method extracts the web page for this profile
	 * @param blogs
	 * @param doc
	 */
	public static void populateWebPageUrl(BloggerProfile profile, Document doc){
		for(Element element : doc.getElementsByAttributeValue("rel", "me nofollow")){
			profile.setWebPageUrl(element.attr("href"));
		}
	}
	
	/**
	 * Given a blogger profile HTML document this method extracts the instant messaging service and username for this profile
	 * @param blogs
	 * @param doc
	 */
	public static void populateInstantMessagingInfo(BloggerProfile profile, Document doc){
		for(Element element : doc.getElementsByAttributeValue("class", "sidebar-item")){
			String content = element.toString();
			if(content.contains("<div class=\"\">")){
				profile.setInstantMessagingUsername(content.split("<div class=\"\">")[1].split("</div>")[0].trim());
				String service = content.split("</div>")[1].split("</li>")[0].trim();
				//remove the parentheses
				profile.setInstantMessagingService(service.substring(1, service.length()-1));
			}
		}
	}
	
	/**
	 * Given a blogger profile HTML document this method extracts the blogs followed for this profile
	 * @param blogs
	 * @param doc
	 */
	public static void populateBlogsFollowed(BloggerProfile profile, Document doc){
		Set<String> blogsFollowed = new TreeSet<String>();
		//get the class="sidebar-item elements
		for(Element element : doc.getElementsByAttributeValue("class", "sidebar-item")){
			if(element.getAllElements().size() == 2 && element.getAllElements().get(1).getElementsByTag("a").size() > 0)
			{
				//if it contains rel="me nofollow" it is the web page: skip
				if(element.getElementsByAttributeValue("rel", "me nofollow").size() > 0){
					continue;
				}
				//else we found a blog followed
				blogsFollowed.add(element.getElementsByTag("a").first().attr("href"));
			}
		}
		profile.setBlogsFollowedUrls(blogsFollowed);
	}
	
}
