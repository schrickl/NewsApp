package com.example.android.newsapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving news articles data from the Guardian API.
 */
public final class QueryUtils {

    // Tag for the log messages
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Guardian dataset and return a list of {@link NewsItem} objects.
     */
    public static List<NewsItem> fetchNewsItemData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link NewsItem}s
        List<NewsItem> newsItems = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link NewsItem}s
        return newsItems;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news item JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link NewsItem} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<NewsItem> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news items to
        List<NewsItem> newsItemList = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            // For a given newsItem, extract the JSONObject associated with the
            // key called "response", which represents a list of responses (or newsItems)
            JSONObject newsItemObject = baseJsonResponse.getJSONObject("response");

            // For a given newsItemObject, extract the JSONArray associated with the
            // key called "results".
            JSONArray newsItemArray = newsItemObject.getJSONArray("results");

            // For each newsItem in the newsItemArray, create a {@link NewsItem} object
            for (int i = 0; i < newsItemArray.length(); i++) {

                // Get a single newsItem at position i within the list of newsItems
                JSONObject currentNewsItem = newsItemArray.getJSONObject(i);

                // For a given newsItem, extract the JSONObject associated with the
                // key called "fields", which represents a list of all properties
                // for that newsItem.
                JSONObject fields = currentNewsItem.getJSONObject("fields");

                // Extract the value for the key called "webTitle"
                String title = currentNewsItem.getString("webTitle");

                // Extract the value for the key called "sectionName"
                String section = currentNewsItem.getString("sectionName");

                // Extract the value for the key called "byline"
                String author = fields.getString("byline");

                // Extract the value for the key called "webPublicationDate"
                String date = currentNewsItem.getString("webPublicationDate");

                // Extract the value for the key called "trailText"
                String trailText = fields.getString("trailText");

                // Extract the value for the key called "shortUrl"
                String url = fields.getString("shortUrl");

                // Extract the value for the key called "thumbnail"
                Bitmap thumbnail = getBitmap(fields.getString("thumbnail"));

                // Create a new {@link NewsItem} object with the title, section, author,
                // date, trailText, url, and thumbnail from the JSON response.
                NewsItem newsItem = new NewsItem(title, section, author, date, trailText, url, thumbnail);

                // Add the new {@link NewsItem} to the list of news items.
                newsItemList.add(newsItem);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the news item JSON results", e);
        }

        // Return the list of news items
        return newsItemList;
    }

    /**
     * Helper method for converting a thumbnail into a bitmap
     * @param thumbnail the thumbnail retrieved from newsJSON
     * @return a bitmap of the thumbnail or null if the thumbnail is null
     */
    private static Bitmap getBitmap(String thumbnail) {

        if (thumbnail != null) {
            try {
                return BitmapFactory.decodeStream((InputStream) new URL(thumbnail).getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
