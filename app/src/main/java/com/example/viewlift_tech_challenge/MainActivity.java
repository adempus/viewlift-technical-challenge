package com.example.viewlift_tech_challenge;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * MainActivity which initiates download and parsing of XML data
 * for display in a ListView of video selections.
 */
public class MainActivity extends AppCompatActivity {
    private String feedUrl;
    private List<Video> videoList;
    private ListView videoListView;
    private VideoListAdapter videoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        feedUrl = "http://sample-firetv-web-app.s3-website-us-west-2.amazonaws.com/feed_firetv.xml";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoList = new ArrayList<>();
        try {
            new XmlFeedDownloadTask().execute(feedUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Initializes and sets up a ListView with ArrayAdapter for display of extracted content **/
    private void initVideoPlaylist() {
        videoListView = findViewById(R.id.videoListView);
        videoListAdapter = new VideoListAdapter(
                this, R.layout.video_listview_layout, videoList
        );
        videoListView.setAdapter(videoListAdapter);
        
        // Event listener for video selections. Launches a fullscreen activity for video playback.
        videoListView.setOnItemClickListener((parent, view, position, id) -> {
            Video vid = (Video) videoListView.getItemAtPosition(position);
            final String vidUrl = vid.videoUrl;
            launchVideoPlaybackActivity(vidUrl);
        });
    }

    /** Starts the activity for video playback.
     * @param videoUrl  String url to remote video resource.
     */
    private void launchVideoPlaybackActivity(String videoUrl) {
        Intent videoPlaybackIntent = new Intent(this, VideoPlaybackActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("video_url", videoUrl);
        videoPlaybackIntent.putExtras(bundle);
        startActivity(videoPlaybackIntent);
    }

    /**
     * Downloads the XML document containing video items.
     * @param url   link to the XML resource to download
     * @returns     a Document object model for the downloaded XML resource.
     */
    private Document getXmlDocument(String url) throws Exception {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        return docBuilder.parse(new URL(url).openStream());
    }

    /**
     * Parses the downloaded XML document.
     * @param xmlDocument   Document object representing downloaded XML resource.
     */
    private void parseXmlDocument(Document xmlDocument) {
        Element root = xmlDocument.getDocumentElement();
        NodeList items = root.getElementsByTagName("media:content");
        for (int i = 0 ; i < items.getLength() ; i++) {
            String title = parseVideoTitle(items.item(i));
            String thumbnailUrl = parseVideoThumbnailUrl(items.item(i));
            String videoUrl = parseVideoUrl(items.item(i));
            String duration = formatDuration(parseVideoDuration(items.item(i)));
            videoList.add(new Video(title, thumbnailUrl, videoUrl, duration));
        }
    }

    /* to verify correct initialization of Video objects from extracted XML data. */
    private void printResults() {
        for (Video v : videoList) {
            System.out.println(v.toString());
        }
    }

    /**
     * Extracts a video's title from XML resource
     * @param node  a Node from the Document object containing datum for title.
     * @return      String of extracted video title.
     */
    public String parseVideoTitle(Node node) {
        return node.getFirstChild().getNextSibling().getTextContent();
    }

    /**
     * Extracts a video's mp4 URL
     * @param node  a Node from the Document object containing datum for video URL.
     * @return      String of extracted video url.
     */
    public String parseVideoUrl(Node node) {
        return node.getAttributes().getNamedItem("url").getNodeValue();
    }

    /**
     * Extracts a video's thumbnail image URL.
     * @param node  a Node from the Document object containing datum for thumbnail URL.
     * @return      String of extracted video thumbnail URL.
     */
    public String parseVideoThumbnailUrl(Node node) {
        Node n = ((Element) node).getElementsByTagName("media:thumbnail").item(0);
        return n.getAttributes().getNamedItem("url").getNodeValue();
    }

    /**
     * Extracts a video's float duration from XML resource.
     * @param node  a Node from the Document object containing datum for duration.
     * @return      String of extracted video duration.
     */
    public String parseVideoDuration(Node node) {
        return node.getAttributes().getNamedItem("duration").getNodeValue();
    }

    /**
     * Converts float value of a video's duration to "MM:SS" format.
     * @param duration  String of a video's duration represented as a float value.
     * @return           String of video's duration time as "MM:SS".
     */
    public String formatDuration(String duration) {
        Float durFloat = new Float(duration);
        int min = Math.round(durFloat / 60);
        int secs = Math.round(durFloat % 60);
        return secs <= 9 ? min+":0"+secs : min+":"+secs;
    }

    /** An AsyncTask for downloading the XML document from provided URL. */
    private class XmlFeedDownloadTask extends AsyncTask<String, Void, Document> {
        private ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(MainActivity.this);
            loading.setMessage("Loading Playlist");
            loading.show();
        }

        @Override
        protected Document doInBackground(String... urls) {
            try {
                return getXmlDocument(urls[0]);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Document result) {
            parseXmlDocument(result);
//            printResults();
            initVideoPlaylist();
            loading.dismiss();
        }
    }
}
