package dal;

import java.util.List;

/**
 * This class represents a Blogger blog
 * @author Konstantinos Pappas
 *
 */
public class BloggerBlog {
	
	private String url;

	private String description;
	
	private String name;
	
	private long published;
	
	private long updated;
	
	private String localeCountry;
	
	private String localeLanguage;
	
	private String localeVariant;
	
	private List<String> postUrls;
	
	public BloggerBlog(String url){
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<String> getPostUrls() {
		return postUrls;
	}

	public void setPostUrls(List<String> postUrls) {
		this.postUrls = postUrls;
	}
	
	public long getPublished() {
		return published;
	}

	public void setPublished(long published) {
		this.published = published;
	}

	public long getUpdated() {
		return updated;
	}

	public void setUpdated(long updated) {
		this.updated = updated;
	}

	public String getLocaleCountry() {
		return localeCountry;
	}

	public void setLocaleCountry(String localeCountry) {
		this.localeCountry = localeCountry;
	}

	public String getLocaleLanguage() {
		return localeLanguage;
	}

	public void setLocaleLanguage(String localeLanguage) {
		this.localeLanguage = localeLanguage;
	}

	public String getLocaleVariant() {
		return localeVariant;
	}

	public void setLocaleVariant(String localeVariant) {
		this.localeVariant = localeVariant;
	}

	/**
	 * The blog string representation
	 */
	@Override
	public String toString(){
		return "Blog: { " +
	             "{Url: " + url + "}, " +
	             "{Name: " + name + "}, " +
	             "{Description: " + description + "}, " +
	             "{Published: " + published + "}, " +
	             "{Updated: " + updated + "}, " +
	             "{Locale Country: " + localeCountry + "}, " +
	             "{Locale Language: " + localeLanguage + "}, " +
	             "{Locale Variant: " + localeVariant + "}, " +
	             "{Posts: " + postUrls + "} " +
				" } ";
	}
	
}
