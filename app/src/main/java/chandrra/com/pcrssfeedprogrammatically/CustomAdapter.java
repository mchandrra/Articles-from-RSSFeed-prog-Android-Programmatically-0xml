package chandrra.com.pcrssfeedprogrammatically;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by smallipeddi on 10/7/16.
 *
 *
 * Custom RecyclerView Adapter
 */

public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int HEADER_ARTICLE = 0;
    public static final int GROUP_ARTICLES = 1;
    ArrayList<PCRssFeedItems> feedItems = null;
    Context context;
    MainActivityArticles mainActivityArticles = new MainActivityArticles();

    public CustomAdapter(Context context, ArrayList<PCRssFeedItems> feedItems) {
        this.feedItems = feedItems;
        this.context = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case GROUP_ARTICLES:
                view = cardViewOtherArticles();
                return new GroupViewHolder(view);
            case HEADER_ARTICLE:
                view = cardViewFirstArticle();
                return new FirstHeaderArticleViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final PCRssFeedItems current = feedItems.get(position);

        if (getItemCount() > 0) {
            if (current != null) {
                switch (getItemViewType(position)) {
                    case GROUP_ARTICLES:
                        ((GroupViewHolder)holder).image.setImageBitmap(null);
                        ((GroupViewHolder)holder).title.setText(current.getTitle());
                        //Custom Image loader
//                        new DownloadImageTask(((GroupViewHolder)holder).image)
//                                .execute(current.getImageUrl());
                        //Image Loading with Glide
                        //Image Loading with Glide
                        Glide.with(context).load(current.getImageUrl()).into(((GroupViewHolder) holder).image);
                        //Image Loading with Picasso
                        //Picasso.with(context).load(current.getImageUrl()).into(((GroupViewHolder)holder).image);
                        ((GroupViewHolder)holder).cardView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, ArticleWebView.class);
                                String link = current.getLink();
                                intent.putExtra("link", link);
                                intent.putExtra("title", current.getTitle());
                                context.startActivity(intent);
                            }
                        });
                        break;
                    case HEADER_ARTICLE:
                        ((FirstHeaderArticleViewHolder)holder).image.setImageBitmap(null);

                        ((FirstHeaderArticleViewHolder)holder).title.setText(current.getTitle());
                        //Custom Image Loader
//                        new DownloadImageTask(((FirstHeaderArticleViewHolder)holder).image)
//                                .execute(current.getImageUrl());
                        //Image Loading with Glide
                        Glide.with(context).load(current.getImageUrl()).into(((FirstHeaderArticleViewHolder)holder).image);
                        //Picasso.with(context).load(current.getImageUrl()).into(((FirstHeaderArticleViewHolder)holder).image);
                        String desciptionPubDate = current.getPubDate().substring(0, 17) + " - " + current.getDescription().replaceAll("\\<.*?>","");
                        ((FirstHeaderArticleViewHolder)holder).descriptionPubDate.setText(desciptionPubDate);
                        ((FirstHeaderArticleViewHolder)holder).cardView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, ArticleWebView.class);
                                intent.putExtra("link", current.getLink());
                                intent.putExtra("title", current.getTitle());
                                context.startActivity(intent);
                            }
                        });
                        break;

                }
            }

        }

    }

    /**
     *  Differentiating First article and other articles by getting Item view type
     * @param position
     * @return
     */

    @Override
    public int getItemViewType(int position) {
        //return super.getItemViewType(position);
        if (position == 0) {
            return HEADER_ARTICLE;
        } else {
            return GROUP_ARTICLES;
        }
    }

    @Override
    public int getItemCount() {
        if (feedItems != null) {
            return feedItems.size();
        } else {
            return 0;
        }

    }

    /**
     * Custom View Holder for Second Article
     */

    public class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;
        CardView cardView;
        public GroupViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.imageP);
            image.setImageResource(R.drawable.loading);
            title = (TextView) itemView.findViewById(R.id.titleP);
            cardView = (CardView) itemView.findViewById(R.id.cardP);

        }
    }

    /**
     * Custom View Holder for First Article
     */
    public class FirstHeaderArticleViewHolder extends RecyclerView.ViewHolder {
        TextView title, descriptionPubDate;
        ImageView image;
        CardView cardView;

        public FirstHeaderArticleViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.titlePfirst);
            descriptionPubDate = (TextView) itemView.findViewById(R.id.descriptionPfirst);
            image = (ImageView) itemView.findViewById(R.id.imagePfirst);
            image.setImageResource(R.drawable.loading);
            cardView = (CardView) itemView.findViewById(R.id.cardPfirst);

        }
    }

    /**
     * Downloading Images in Async Task
     */

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bmImage.setImageResource(R.drawable.loading);
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


    /**
     *
     * @return cardView for first article
     */

    public View cardViewFirstArticle() {

        LinearLayout rootLinearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams rootParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rootLinearLayout.setOrientation(LinearLayout.VERTICAL);

        CardView cardViewFirst = new CardView(context);
        cardViewFirst.setId(R.id.cardPfirst);
        cardViewFirst.setCardBackgroundColor(ContextCompat.getColor(context, R.color.cardview_shadow_end_color));
        cardViewFirst.setPreventCornerOverlap(false);
        cardViewFirst.setUseCompatPadding(true);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams childParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rootLinearLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        ImageView imageFirstArticle = new ImageView(context);
        imageFirstArticle.setImageResource(R.drawable.loading);
        imageFirstArticle.setId(R.id.imagePfirst);
        imageFirstArticle.setScaleType(ImageView.ScaleType.FIT_XY);

        imageFirstArticle.setLayoutParams(imageParams);
        if (new MainActivityArticles().isTablet(context)) {
            imageFirstArticle.getLayoutParams().height = dpTopxs(400);
        } else {
            imageFirstArticle.getLayoutParams().height = dpTopxs(200);
        }
        //imageFirstArticle.getLayoutParams().height = dpTopxs(300);//800;

        TextView titleFirstArticle = new TextView(context);
        titleFirstArticle.setId(R.id.titlePfirst);
        titleFirstArticle.setEllipsize(TextUtils.TruncateAt.END);
        titleFirstArticle.setLines(1);
        titleFirstArticle.setTextColor(Color.BLACK);
        titleFirstArticle.setPadding(20,30, 20, 0);
        titleFirstArticle.setTypeface(Typeface.DEFAULT_BOLD);
        titleFirstArticle.setLayoutParams(childParams);

        TextView descriptionPubDate = new TextView(context);
        descriptionPubDate.setId(R.id.descriptionPfirst);
        descriptionPubDate.setEllipsize(TextUtils.TruncateAt.END);
        descriptionPubDate.setLines(2);
        descriptionPubDate.setTextColor(Color.BLACK);
        descriptionPubDate.setPadding(20,30, 20, 30);
        descriptionPubDate.setTypeface(Typeface.DEFAULT);

        descriptionPubDate.setLayoutParams(rootParams);

        TextView previousArticles = new TextView(context);
        previousArticles.setText("Previous Articles");
        previousArticles.setPadding(20,30, 20, 0);
        previousArticles.setTextColor(Color.BLACK);
        previousArticles.setTextAppearance(context, android.R.style.TextAppearance_Material_Medium);
        previousArticles.setTypeface(Typeface.DEFAULT_BOLD);
        previousArticles.setLayoutParams(rootParams);


        linearLayout.addView(imageFirstArticle);
        linearLayout.addView(titleFirstArticle);
        linearLayout.addView(descriptionPubDate);
        cardViewFirst.addView(linearLayout);
        rootLinearLayout.addView(cardViewFirst);
        rootLinearLayout.addView(previousArticles);

        return rootLinearLayout;
    }

    /**
     *
     * @return cardView for other Articles
     */

    public View cardViewOtherArticles() {
        CardView cardView = new CardView(context);
        cardView.setId(R.id.cardP);
        cardView.setPreventCornerOverlap(false);
        cardView.setUseCompatPadding(true);

        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        ImageView imageView = new ImageView(context);
        imageView.setBackgroundColor(Color.GRAY);
        imageView.setId(R.id.imageP);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params2);
        imageView.setImageResource(R.drawable.loading);

        imageView.getLayoutParams().height = dpTopxs(120);//400;

        ProgressBar progressBar = new ProgressBar(context);


        TextView textView = new TextView(context);
        //tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
        textView.setTextColor(Color.BLACK);//setTextColor(Color.RED);
        textView.setId(R.id.titleP);
        textView.setLines(2);
        textView.setPadding(20,0,20,0);
        textView.setTextAppearance(context, android.R.style.TextAppearance_Material_Small);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setLayoutParams(params);

        linearLayout.addView(imageView);
        linearLayout.addView(textView);
        cardView.addView(linearLayout);

        return cardView;
    }
    public int dpTopxs(int dps) {
        //final float scale = getContext().getResources().getDisplayMetrics().density;
        final float scale = context.getResources().getDisplayMetrics().density;
        int pixels = (int) (dps * scale + 0.5f);
        return pixels;
    }

}

