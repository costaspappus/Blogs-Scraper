package dal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This class represents the info for a Blogger profile
 * @author Konstantinos Pappas
 *
 */
public class BloggerProfile implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String url;
	
	//true stands for male, false stands for female
	private String gender;
	
	private String industry;
	
	private String occupation;
	
	private String city;
	
	private String state;
	
	private String country;
	
	private String introduction;
	
	private String interests;
	
	private String movies;
	
	private String music;
	
	private String books;
	
	private List<String> blogUrls;
	
	private String name;
	
	private String imageUrl;
	
	private String email;
	
	private String webPageUrl;
	
	private String instantMessagingService;
	
	private String instantMessagingUsername;
	
	private Set<String> blogsFollowedUrls;

	/**
	 * constructor that sets the url
	 * @param url
	 */
	public BloggerProfile(String url){
		blogUrls = new ArrayList<String>();
		setUrl(url);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getInterests() {
		return interests;
	}

	public void setInterests(String interests) {
		this.interests = interests;
	}

	public String getMovies() {
		return movies;
	}

	public void setMovies(String movies) {
		this.movies = movies;
	}

	public String getMusic() {
		return music;
	}

	public void setMusic(String music) {
		this.music = music;
	}

	public String getBooks() {
		return books;
	}

	public void setBooks(String books) {
		this.books = books;
	}

	public List<String> getBlogUrls() {
		return blogUrls;
	}

	public void setBlogUrls(List<String> blogUrls) {
		this.blogUrls = blogUrls;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWebPageUrl() {
		return webPageUrl;
	}

	public void setWebPageUrl(String webPageUrl) {
		this.webPageUrl = webPageUrl;
	}

	public String getInstantMessagingService() {
		return instantMessagingService;
	}

	public void setInstantMessagingService(String instantMessagingService) {
		this.instantMessagingService = instantMessagingService;
	}

	public String getInstantMessagingUsername() {
		return instantMessagingUsername;
	}

	public void setInstantMessagingUsername(String instantMessagingUsername) {
		this.instantMessagingUsername = instantMessagingUsername;
	}

	public Set<String> getBlogsFollowedUrls() {
		return blogsFollowedUrls;
	}

	public void setBlogsFollowedUrls(Set<String> blogsFollowedUrls) {
		this.blogsFollowedUrls = blogsFollowedUrls;
	}

	/**
	 * The profile string representation
	 */
	@Override
	public String toString(){
		return "Profile: { " +
	             "{Url: " + url + "}, " +
	             "{Gender: " + gender + "}, " +
	             "{Industry: " + industry + "}, " +
	             "{Occupation: " + occupation + "}, " +
	             "{City: " + city + "}, " +
	             "{State: " + state + "}, " +
	             "{Country: " + country + "}, " +
	             "{Introduction: " + introduction + "}, " +
	             "{Interests: " + interests + "}, " +
	             "{Movies: " + movies + "}, " +
	             "{Music: " + music + "}, " +
	             "{Books: " + books + "}, " +
	             "{Blogs: " + blogUrls + "} " + 
	             "{Name: " + name + "} " + 
	             "{ImageUrl: " + imageUrl + "} " + 
	             "{Email: " + email + "} " + 
	             "{WebPageUrl: " + webPageUrl + "} " + 
	             "{InstantMessagingService: " + instantMessagingService + "} " + 
	             "{InstantMessagingUsername: " + instantMessagingUsername + "} " + 
	             "{BlogsFollowed: " + blogsFollowedUrls + "} " + 
				" } ";
	}
	
}
