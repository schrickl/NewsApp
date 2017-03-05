package com.example.android.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/*
* {@link NewsItem} is an {@link ArrayAdapter} that can provide the layout for each list
* based on a data source, which is a list of {@link NewsItem} objects.
* */
public class NewsItemAdapter extends ArrayAdapter<NewsItem> {

    private List<NewsItem> mNewsList = new ArrayList<>();

    public NewsItemAdapter(Context context, ArrayList<NewsItem> news) {
        super(context, 0, news);
        mNewsList = news;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolder;

        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);

            // Set up the ViewHolderItem
            viewHolder = new ViewHolderItem();
            // Find the TextView in the news_list_item layout with the ID iv_thumbnail
            viewHolder.iv_thumbnail = (ImageView) listItemView.findViewById(R.id.iv_thumbnail);
            // Find the TextView in the news_list_item layout with the ID tv_title
            viewHolder.tv_title = (TextView) listItemView.findViewById(R.id.tv_title);
            // Find the TextView in the news_list_item layout with the ID tv_author
            viewHolder.tv_author = (TextView) listItemView.findViewById(R.id.tv_author);
            // Find the TextView in the news_list_item layout with the ID tv_trail
            viewHolder.tv_trail = (TextView) listItemView.findViewById(R.id.tv_trail);
            // Find the TextView in the news_list_item layout with the ID tv_date
            viewHolder.tv_date = (TextView) listItemView.findViewById(R.id.tv_date);
            // Find the TextView in the news_list_item layout with the ID tv_section
            viewHolder.tv_section = (TextView) listItemView.findViewById(R.id.tv_section);

            // store the holder with the view.
            listItemView.setTag(viewHolder);
        } else {
            // Use the ViewHolderItem instead of calling findViewById every time
            viewHolder = (ViewHolderItem) listItemView.getTag();
        }

        // Get the {@link NewsItem} object located at this position in the list
        NewsItem currentNewsItem = getItem(position);

        // Assign values if the object is not null
        if(currentNewsItem != null) {
            viewHolder.iv_thumbnail.setImageBitmap(currentNewsItem.getThumbnail());
            viewHolder.tv_title.setText(currentNewsItem.getTitle());
            if (currentNewsItem.getAuthor().equals("")) {
                viewHolder.tv_author.setText(R.string.no_author);
            } else {
                viewHolder.tv_author.setText(currentNewsItem.getAuthor());
            }
            viewHolder.tv_trail.setText(currentNewsItem.getTrailText());
            viewHolder.tv_date.setText(formatDate(currentNewsItem.getDate()));
            viewHolder.tv_section.setText(currentNewsItem.getSection());
        }

        return listItemView;
    }

    /**
     * Helper method to convert the date into a more human readable format
     * @param date the date to convert
     * @return the formatted date
     */
    private String formatDate(String date) {

        SimpleDateFormat dateInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat dateOutput = new SimpleDateFormat("LLL dd, yyyy - h:mm a");
        String dateReturn = null;
        try {
            dateReturn = dateOutput.format(dateInput.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateReturn;
    }

    // ViewHolder class for layout items
    static class ViewHolderItem {
        ImageView iv_thumbnail;
        TextView tv_title;
        TextView tv_section;
        TextView tv_author;
        TextView tv_date;
        TextView tv_trail;
    }
}