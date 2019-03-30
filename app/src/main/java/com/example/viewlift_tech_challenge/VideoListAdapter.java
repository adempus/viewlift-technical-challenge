package com.example.viewlift_tech_challenge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import java.util.List;

public class VideoListAdapter extends ArrayAdapter<Video> {
    private Context context;
    private int resource;
    private List<Video> videos;

    public VideoListAdapter(Context context, int resource, List<Video> videos) {
        super(context, resource, videos);
        this.context = context;
        this.resource = resource;
        this.videos = videos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        initImageLoader();
        Video video = videos.get(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(resource, parent, false);

            // gather view resources for content display
            ImageView thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail_view);
            TextView title = (TextView) convertView.findViewById(R.id.video_title);
            TextView duration = (TextView) convertView.findViewById(R.id.duration);
            // set content for title and duration
            title.setText(video.title);
            duration.setText(video.duration);

            ImageLoader imageLoader = ImageLoader.getInstance();
            // set default image to display if error is encountered displaying images from URLs.
            int defaultImage = context.getResources().getIdentifier(
                    "@drawable/thumbnail_pending",null, context.getPackageName()
            );
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(defaultImage)
                    .showImageOnFail(defaultImage)
                    .showImageOnLoading(defaultImage).build();
            //download and display image from url
            imageLoader.displayImage(video.thumbnailUrl, thumbnail , options);
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return videos.size();
    }

    /** Initializes ImageLoader object to display video thumbnails from URLs to ImageViews. **/
    private void initImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
    }
}
