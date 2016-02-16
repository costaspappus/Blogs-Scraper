package dal;

/**
 * 
 * @author Konstantinos Pappas
 *
 */
public class BloggerPost {

	private String url;
	
	private String title;
	
	private String content;
	
	private long published;
	
	private String authorUrl;
	
	private Double locationLatitude;
	
	private Double locationLongitude;
	
	private String locationName;
	
	private String locationSpan;
	
	public BloggerPost(String url){
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public long getPublished() {
		return published;
	}

	public void setPublished(long published) {
		this.published = published;
	}

	public String getAuthorUrl() {
		return authorUrl;
	}

	public void setAuthorUrl(String authorUrl) {
		this.authorUrl = authorUrl;
	}

	public Double getLocationLatitude() {
		return locationLatitude;
	}

	public void setLocationLatitude(Double locationLatitude) {
		this.locationLatitude = locationLatitude;
	}

	public Double getLocationLongitude() {
		return locationLongitude;
	}

	public void setLocationLongitude(Double locationLongitude) {
		this.locationLongitude = locationLongitude;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getLocationSpan() {
		return locationSpan;
	}

	public void setLocationSpan(String locationSpan) {
		this.locationSpan = locationSpan;
	}

	public String toString(){
		return "Post: { " +
	             "{Url: " + url + "}, " +
	             "{Title: " + title + "}, " +
	             "{Content: " + content + "} " +
	             "{Published: " + published + "} " +
	             "{Author Url: " + authorUrl + "} " +
	             "{Location Latitude: " + locationLatitude + "} " +
	             "{Location Longitude: " + locationLongitude + "} " +
	             "{Location Name: " + locationName + "} " +
	             "{Location Span: " + locationSpan + "} " +
				" } ";
	}
}
