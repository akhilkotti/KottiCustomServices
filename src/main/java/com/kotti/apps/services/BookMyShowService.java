package com.kotti.apps.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.kotti.apps.constants.ServiceConstants;
import com.kotti.apps.utils.GoogleMail;

public class BookMyShowService {

	private String cookies;
	private final String USER_AGENT = "Mozilla/5.0";

	private List<String> availTheatList = new ArrayList<String>();

	private String movieNameBMS;

	private boolean newItemAdded = false;

	public boolean isNewItemAdded() {
		return newItemAdded;
	}

	public void setNewItemAdded(boolean newItemAdded) {
		this.newItemAdded = newItemAdded;
	}

	public String getMovieNameBMS() {
		return movieNameBMS;
	}

	public void setMovieNameBMS(String movieNameBMS) {
		this.movieNameBMS = movieNameBMS;
	}

	public List<String> getAvailTheatList() {
		return availTheatList;
	}

	public void setAvailTheatList(List<String> availTheatList) {
		this.availTheatList = availTheatList;
	}

	private String conversationId;

	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	private String logoutUrl;

	public String getLogoutUrl() {
		return logoutUrl;
	}

	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}

	List<String> mySearchTheatreList = ServiceConstants.getMyTheatreList;
	List<String> availableSearchInBMSList = new ArrayList<String>();

	public void findBookMyShow(String movieUrl, String movieId) {
		// make sure cookies is turn on
		CookieHandler.setDefault(new CookieManager());

		String url = movieUrl + "/" + movieId;
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(url);

		// add header
		get.setHeader("Host", "in.bookmyshow.com");
		get.setHeader("User-Agent", USER_AGENT);
		get.setHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		get.setHeader("Accept-Language", "en-US,en;q=0.5");
		get.setHeader("Accept-Encoding", "gzip, deflate");
		get.setHeader("Cookie", getCookies());
		get.setHeader("Connection", "keep-alive");

		try {

			HttpResponse response = client.execute(get);
			System.out.println("Response Code : "
					+ response.getStatusLine().getStatusCode());

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			// set cookies
			setCookies(response.getFirstHeader("Set-Cookie") == null ? ""
					: response.getFirstHeader("Set-Cookie").toString());

			Document doc = Jsoup.parse(result.toString());

			// System.out.println(doc);

			String movieName = "";

			Elements titles = doc.select("title");
			for (Element title : titles) {
				movieName = title.text();
				break;
			}

			List<String> bookMyShowDisplayedTheatreList = new ArrayList<String>();

			Elements links = doc.select("a[href]");
			setNewItemAdded(false);
			for (Element link : links) {
				String hrefLink = link.toString();
				if (hrefLink.contains("buytickets")
						&& !bookMyShowDisplayedTheatreList
								.contains(link.text())) {
					bookMyShowDisplayedTheatreList.add(link.text());

					if (mySearchTheatreList.contains(link.text())) {

						if (!availableSearchInBMSList.contains(link.text()
								+ "\n\t")) {
							availableSearchInBMSList.add(link.text() + "\n\t");
							System.out.println(availableSearchInBMSList.size());
							System.out.println("Added new item in List:"
									+ link.text());
							setNewItemAdded(true);
						}
					}

				}
			}

			List<String> updatedSearchTheatreList = new ArrayList<String>();
			for (int i = 0; i < mySearchTheatreList.size(); i++) {
				updatedSearchTheatreList.add(mySearchTheatreList.get(i)
						+ "\n\t\t");
			}

			// System.out
			// .println("\nBook My Show site showing the below theatres...");
			// System.out.println(bookMyShowDisplayedTheatreList);
			//
			// System.out.println("\nLooking for below theatres...");
			// System.out.println(mySearchTheatreList);
			//
			// System.out.println("\nAvailable theatres...");
			// System.out.println(updatedSearchInBMSList);

			if (availableSearchInBMSList.size() > 0) {

				setAvailTheatList(availableSearchInBMSList);

				// String[] mailArray = (String[])
				// ServiceConstants.getMailingList.toArray();
				String mailString = ServiceConstants.getMailingList.toString()
						.replace("[", "").replace("]", "");
				// System.out.println(mailString);

				String availableTheatres = availableSearchInBMSList.toString()
						.replace("[", "").replace("]", "");
				// System.out.println(availableTheatres);

				String searchTheatres = updatedSearchTheatreList.toString()
						.replace("[", "").replace("]", "");
				// System.out.println(searchTheatres);

				String mName = movieName.substring(0,
						movieName.indexOf("Show Timings"));
				// System.out.println("mName=" + mName);
				setMovieNameBMS(mName);

				String bindedMessage = ServiceConstants.getBookMyShowEmailTemplate
						.replace("{moviename}", mName)
						.replace("{bookmyshowURL}", url)
						.replace("{theatres}", availableTheatres)
						.replace("{searchtheatres}", searchTheatres);
				// String title =
				// "Book My Show Tickets opened for Movie "+movieName;

				if (isNewItemAdded()) {
					System.out.println("Sending Mail.....");
					// Send mail to the mailing list
					GoogleMail.Send("kotti.services", "Rishi2000", mailString,
							mName, bindedMessage);
					System.out.println("Mail Sent Successfully.");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean Way2SMSLogin(String userName, String password) {
		// make sure cookies is turn on
		CookieHandler.setDefault(new CookieManager());

		String url = "http://site25.way2sms.com/Login1.action";
		boolean isLoginSuccess = false;
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);

		// add header

		// add header
		post.setHeader("Host", "site25.way2sms.com");
		post.setHeader("User-Agent", USER_AGENT);
		post.setHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		post.setHeader("Accept-Language", "en-US,en;q=0.5");
		post.setHeader("Accept-Encoding", "gzip, deflate");
		post.setHeader("Cookie", getCookies());
		post.setHeader("Connection", "keep-alive");
		post.setHeader("Referer", "http://site25.way2sms.com/entry?ec=0080");

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("username", userName));
		urlParameters.add(new BasicNameValuePair("password", password));

		try {
			post.setEntity(new UrlEncodedFormEntity(urlParameters));

			HttpResponse response = client.execute(post);
			System.out.println("Response Code : "
					+ response.getStatusLine().getStatusCode());

			String locationURI = "";
			String sessidstr = "jsessionid";
			Header[] hdrs = response.getAllHeaders();
			for (int i = 0; i < hdrs.length; i++) {
				// System.out.println("Header Name :" + hdrs[i].getName()
				// + "<<<>>>Header Value :" + hdrs[i].getValue());
				if (hdrs[i].getName().equalsIgnoreCase("Location")) {
					locationURI = hdrs[i].getValue();
					setConversationId(locationURI.substring(
							locationURI.indexOf(sessidstr)
									+ (sessidstr.length() + 1),
							locationURI.indexOf("?")));
				}
			}

			if (locationURI != null) {
				HttpGet get = new HttpGet(locationURI);

				get.setHeader("Host", "site25.way2sms.com");
				get.setHeader("User-Agent", USER_AGENT);
				get.setHeader("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
				get.setHeader("Accept-Language", "en-US,en;q=0.5");
				get.setHeader("Accept-Encoding", "gzip, deflate");
				get.setHeader("Cookie", getCookies());
				get.setHeader("Connection", "keep-alive");
				get.setHeader("Referer",
						"http://site25.way2sms.com/entry?ec=0080");

				response = client.execute(get);
				System.out.println("Response Code : "
						+ response.getStatusLine().getStatusCode());

				hdrs = response.getAllHeaders();
				// for (int i = 0; i < hdrs.length; i++) {
				// System.out.println("Header Name :" + hdrs[i].getName()
				// + "<<<>>>Header Value :" + hdrs[i].getValue());
				// }
			}

			isLoginSuccess = true;
		} catch (Exception e) {

			e.printStackTrace();
		}

		return isLoginSuccess;
	}

	public boolean Way2SMSLogoutNoSMS() {
		// make sure cookies is turn on
		CookieHandler.setDefault(new CookieManager());

		String url = "http://site25.way2sms.com/entry?ec=0080&id=1wwd";
		boolean isLogoutSuccess = false;
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(url);

		get.setHeader("Host", "site25.way2sms.com");
		get.setHeader("User-Agent", USER_AGENT);
		get.setHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		get.setHeader("Accept-Language", "en-US,en;q=0.5");
		get.setHeader("Accept-Encoding", "gzip, deflate");
		get.setHeader("Cookie", getCookies());
		get.setHeader("Connection", "keep-alive");
		get.setHeader("Referer", "http://site25.way2sms.com/ebrdg.action?id="
				+ getConversationId());

		try {

			HttpResponse response = client.execute(get);
			System.out.println("Response Code : "
					+ response.getStatusLine().getStatusCode());

			Header[] hdrs = response.getAllHeaders();
			// for (int i = 0; i < hdrs.length; i++) {
			// System.out.println("Header Name :" + hdrs[i].getName()
			// + "<<<>>>Header Value :" + hdrs[i].getValue());
			// }

			isLogoutSuccess = true;
		} catch (Exception e) {

			e.printStackTrace();
		}

		return isLogoutSuccess;
	}

	public boolean Way2SMSLogoutAfterSMS() {
		// make sure cookies is turn on
		CookieHandler.setDefault(new CookieManager());

		String url = "http://site25.way2sms.com/getContacts";
		boolean isLogoutSuccess = false;
		HttpClient client1 = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);

		// add header

		// add header
		post.setHeader("Host", "site25.way2sms.com");
		post.setHeader("User-Agent", USER_AGENT);
		post.setHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		post.setHeader("Accept-Language", "en-US,en;q=0.5");
		post.setHeader("Accept-Encoding", "gzip, deflate");
		post.setHeader("X-Requested-With", "XMLHttpRequest");
		post.setHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=UTF-8");
		post.setHeader("Cookie", getCookies());
		post.setHeader("Referer",
				"http://site25.way2sms.com/main.action?section=s&Token="
						+ getConversationId() + "&vfType=register_verify");

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("Token", getConversationId()));

		try {
			post.setEntity(new UrlEncodedFormEntity(urlParameters));

			HttpResponse response = client1.execute(post);
			System.out.println("Response Code : "
					+ response.getStatusLine().getStatusCode());

			Header[] hdrs = response.getAllHeaders();
			// for (int i = 0; i < hdrs.length; i++) {
			// System.out.println("Header Name :" + hdrs[i].getName()
			// + "<<<>>>Header Value :" + hdrs[i].getValue());
			// }

			HttpGet get = new HttpGet(
					"http://site25.way2sms.com/entry?ec=0080&id=0.3341648676515936");

			get.setHeader("Host", "site25.way2sms.com");
			get.setHeader("User-Agent", USER_AGENT);
			get.setHeader("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			get.setHeader("Accept-Language", "en-US,en;q=0.5");
			get.setHeader("Accept-Encoding", "gzip, deflate");
			get.setHeader("Cookie", getCookies());
			get.setHeader("Connection", "keep-alive");
			get.setHeader("Referer",
					"http://site25.way2sms.com/main.action?section=s&Token="
							+ getConversationId() + "&vfType=register_verify");

			response = client1.execute(get);
			System.out.println("Response Code : "
					+ response.getStatusLine().getStatusCode());

			hdrs = response.getAllHeaders();
			// for (int i = 0; i < hdrs.length; i++) {
			// System.out.println("Header Name :" + hdrs[i].getName()
			// + "<<<>>>Header Value :" + hdrs[i].getValue());
			// }

			isLogoutSuccess = true;
		} catch (Exception e) {

			e.printStackTrace();
		}

		return isLogoutSuccess;
	}

	public void sendSMS(String toMobileNo, String message, String msgLength) {
		// make sure cookies is turn on
		CookieHandler.setDefault(new CookieManager());

		String url = "http://site25.way2sms.com/smstoss.action";
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);

		// add header
		post.setHeader("Host", "site25.way2sms.com");
		post.setHeader("User-Agent", USER_AGENT);
		post.setHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		post.setHeader("Accept-Language", "en-US,en;q=0.5");
		post.setHeader("Accept-Encoding", "gzip, deflate");
		post.setHeader("Cookie", getCookies());
		post.setHeader("Connection", "keep-alive");
		post.setHeader("Referer", "http://site25.way2sms.com/sendSMS?Token="
				+ getConversationId());

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("ssaction", "ss"));
		urlParameters.add(new BasicNameValuePair("Token", getConversationId()));
		urlParameters.add(new BasicNameValuePair("mobile", toMobileNo));
		urlParameters.add(new BasicNameValuePair("msgLen", msgLength));
		urlParameters.add(new BasicNameValuePair("message", message));
		try {
			post.setEntity(new UrlEncodedFormEntity(urlParameters));

			HttpResponse response = client.execute(post);
			System.out.println("Response Code : "
					+ response.getStatusLine().getStatusCode());

			String locationURI = "";

			Header[] hdrs = response.getAllHeaders();
			for (int i = 0; i < hdrs.length; i++) {
				// System.out.println("Header Name :" + hdrs[i].getName()
				// + "<<<>>>Header Value :" + hdrs[i].getValue());
				if (hdrs[i].getName().equalsIgnoreCase("Location")) {
					locationURI = hdrs[i].getValue();
				}
			}

			if (locationURI != null) {
				HttpGet get = new HttpGet(locationURI);

				get.setHeader("Host", "site25.way2sms.com");
				get.setHeader("User-Agent", USER_AGENT);
				get.setHeader("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
				get.setHeader("Accept-Language", "en-US,en;q=0.5");
				get.setHeader("Accept-Encoding", "gzip, deflate");
				get.setHeader("Cookie", getCookies());
				get.setHeader("Connection", "keep-alive");
				get.setHeader("Referer",
						"http://site25.way2sms.com/sendSMS?Token="
								+ getConversationId());

				response = client.execute(get);
				System.out.println("Response Code : "
						+ response.getStatusLine().getStatusCode());

				hdrs = response.getAllHeaders();
				// for (int i = 0; i < hdrs.length; i++) {
				// System.out.println("Header Name :" + hdrs[i].getName()
				// + "<<<>>>Header Value :" + hdrs[i].getValue());
				// }
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public String getCookies() {
		return cookies;
	}

	public void setCookies(String cookies) {
		this.cookies = cookies;
	}
}
