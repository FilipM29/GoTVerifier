import java.awt.Desktop;
import java.net.*;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import java.io.*;

public class GoTVerifier {	
	
	private static String tvShow = new String("Game of Thrones");
	private static double rating = (double) 9.4;
	private static Boolean CheckForYourself = new Boolean(false); //Default false, if you wish to open rating page on imdb at the end of testscase set true
	private static String imdbURL = new String("https://www.imdb.com");
	
	public static void main(String[] args) throws Exception {
		GoTVerifier GOTV = new GoTVerifier();
		
		System.out.println("This test will review " + tvShow + " rating on imdb.");
		
		GOTV.openImbd();		
		double newRating = GOTV.getRating(GoTVerifier.getImdbURL());//GoTVerifier.getImdbURL());
		GOTV.getResult(newRating);
		if(CheckForYourself) {
			GOTV.openURL(GoTVerifier.getImdbURL());
		}
		
	}
	
	public void openImbd() throws Exception {
		System.out.println("Opening imdb.com.");
		System.out.println(GoTVerifier.getImdbURL());
		Document doc = this.URLConnect(GoTVerifier.getImdbURL());
		System.out.println("Succesfully opened imdb.com.");
		this.searchForGOT(doc);
	}	
	
	public void searchForGOT(Document doc) throws IOException {
		System.out.println("Searching for " + tvShow + ".");
		String newURL = new String(this.URLSearch(doc));
		this.pickFirstOption(this.URLConnect(newURL));
	}
	
	public void pickFirstOption(Document doc) throws IOException {
		System.out.println("Picking first option from search results for " + tvShow + ".");
		this.URLPick(doc);
	}
	
	public double getRating(String url) throws IOException {//opens GOT page on imdb, parses url to only collect scripts and only append script of type "application/ld+json"	
		System.out.println("Retrieving rating value.");
		Document doc =Jsoup.connect(url).timeout(20000).get();
		Elements scriptElements = doc.getElementsByTag("script");
		StringBuffer response = new StringBuffer();

		for (Element element :scriptElements ){
			String type = element.attr("type");
			if(type.contentEquals("application/ld+json")) { // type of this element is json in url source code
				for (DataNode node : element.dataNodes()) {
		        	response.append(node.getWholeData());
		        }
			}		                  
		}
		
        //System.out.println(response.toString());
        JSONObject myResponse = new JSONObject(response.toString());
        //System.out.println(myResponse);
        
        JSONObject aggRating = new JSONObject(myResponse.getJSONObject("aggregateRating").toString()); //collect data from json file
        System.out.println("Rating value: " + aggRating.getDouble("ratingValue")); 
        System.out.println("Succesfully retrieved rating value.");
		return aggRating.getDouble("ratingValue");		
	}
	
	public void getResult(double newRating) {
		
		if (newRating == rating) {
			System.out.println("YES " + tvShow + " rating on imdb is still " + rating + ".");
		} else {
			System.out.println("NO " + tvShow + " rating on imdb changed from " + rating +", new rating is: " + newRating + ".");
		}
	}
	
	public void openURL(String url) {
		try {
			  Desktop desktop = java.awt.Desktop.getDesktop();
			  URI oURL = new URI(url);
			  desktop.browse(oURL);
			  System.out.println("Opening " + tvShow + "imdb page for personal check.");
			} catch (Exception e) {
			  e.printStackTrace();
			}
	}
	
	public Document URLConnect(String url) throws IOException {	//connect to imdb.com	
		Document doc = Jsoup.connect(url).get();
		System.out.println(doc.title());
		return doc;
	}
	
	public String URLSearch(Document doc) {
		String newURL = new String(GoTVerifier.getImdbURL());
		String[] splited = tvShow.split("\\s+");
		newURL = newURL + "/find?ref_=nv_sr_fn&q=";
		for(int i = 0; i < splited.length; i++) {
			if (i != splited.length - 1) {
				newURL = newURL + splited[i] + "+";
			} else {
				newURL = newURL + splited[i];
			}
		}
		newURL = newURL + "&s=all";
		System.out.println(newURL);
		return newURL;
	}
	
	public String URLPick(Document doc)  throws IOException{
		Elements links = doc.select("a[href]");
		for (Element element: links) {
			String link = element.text();
			if(link.contentEquals(tvShow)) {
				System.out.println("\nlink: " + element.attr("href"));
				System.out.println("text: " + element.text());
				GoTVerifier.setImdbURL(element.attr("href"));
				break;
			}
		}			
		return GoTVerifier.getImdbURL();
	}

	public static String getImdbURL() {
		return imdbURL;
	}

	public static void setImdbURL(String imdbURL) {
		GoTVerifier.imdbURL = GoTVerifier.imdbURL + imdbURL;
	}

};
