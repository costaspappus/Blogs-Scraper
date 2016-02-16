package scraper.scr;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * This class read blogger profile urls and writes them in a text file
 * @author Konstantinos Pappas
 *
 */
public class ScrapeSearchPageByLocation {

	//the 50 US states
	public static final String[] statesCodes = {
		"AL", "AK", "AR", "AZ", "CA", "CO", "CT", "DE", "FL", "GA",
		"HI", "IA", "ID", "IL", "IN", "KS", "KY", "LA", "MA", "MD",
		"ME", "MI", "MN", "MO", "MS", "MT", "NC", "ND", "NE", "NH",
		"NJ", "NM", "NV", "NY", "OH", "OK", "OR", "PA", "RI", "SC",
		"SD", "TN", "TX", "UT", "VA", "VT", "WA", "WI", "WV", "WY",
		"Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware", "Florida", "Georgia",
		"Hawaii", "Iowa", "Idaho", "Illinois", "Indiana", "Kansas", "Kentucky", "Louisiana", "Massachusetts", "Maryland",
		"Maine", "Michigan", "Minnesota", "Missouri", "Mississippi", "Montana", "North Carolina", "North Dakota", "Nebraska", "New Hampshire",
		"New Jersey", "New Mexico", "Nevada", "New York", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina",
		"South Dakota", "Tennessee", "Texas", "Utah", "Virginia", "Vermont", "Washington", "Wisconsin", "West Virginia", "Wyoming"
	};
	
	//number of profiles to get per location
	//public static final int numOfProfilesPerState = 1000;
	
	//output file
	public static String filePath = "yourFilePath";
	public static String fineName = "yourFileName";
	
	/**
	 * search blogs according to their declared location
	 */
	public static void main(String[] args){
		//scrape search page
		String searchPageURL = "http://www.blogger.com/profile-find.g?t=l&loc1=";//&loc0=US
		//for each state
		for(String stateCode : statesCodes){
			ArrayList<String> bloggerProfilesURLs = new ArrayList<String>();
			addProfilesForStateCode(searchPageURL+stateCode, bloggerProfilesURLs, 0);
			//write the profile urls on file
			PrintWriter writer = null;
			try{
				//open file
				writer = new PrintWriter(new FileWriter(new File(filePath+stateCode+bloggerProfilesURLs.size()+".txt")));
				for(String profileURL : bloggerProfilesURLs){
					writer.println(profileURL);
					writer.flush();
				}
			} catch(Exception e){
				e.printStackTrace();
			} finally {
				writer.close();
			}
		}
		
	}
	
	/**
	 * Given a state code add numOfProfilesPerState blogger profiles on this state to the given list
	 * @param stateCode
	 * @return
	 */
	public static void addProfilesForStateCode(String searchByStateURL, ArrayList<String> bloggerProfiesURLs, int profilesAdded){
		
		try {
			//connect to the given URL, and get the HTTP response
			Connection.Response response = Jsoup.connect(searchByStateURL).ignoreHttpErrors(true).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, likea Gecko) Chrome/19.0.1042.0 Safari/535.21").timeout(20000).execute();
			//get the status code of the response
			int statusCode = response.statusCode();
			//placeholder for the profiles
			Set<String> newProfiles = new HashSet<String>();
			//if response code is OK
			if (statusCode == 200) {
				Document doc = response.parse();
				Iterator<Element> iterator = doc.getElementsByAttribute("href").iterator();
				//for each element in the response
				while (iterator.hasNext()) {
					String profileURL = iterator.next().attr("href");
					//if blogger profile found
					if (profileURL.startsWith("http://www.blogger.com/profile/")) {
						//add it in the list
						newProfiles.add(profileURL);
					}
				}
				//add all new distinct blog profiles to the main list
				bloggerProfiesURLs.addAll(newProfiles);
				profilesAdded += newProfiles.size();
				System.out.println(profilesAdded);
				System.out.flush();
				//check if enough blogger profiles have been added
				//if(profilesAdded < numOfProfilesPerState){
					//if it isn't add more profiles found in the next page
					//get next page's url
					String nextPageURL = doc.getElementsByAttributeValue("id", "next-btn").iterator().next().attr("href");
					addProfilesForStateCode(nextPageURL, bloggerProfiesURLs, profilesAdded);
				//}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
