
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.ui.adapter;

//~--- non-JDK imports --------------------------------------------------------

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;

import android.text.Html;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.bumptech.glide.Glide;

import com.github.gregbiv.news.R;
import com.github.gregbiv.news.core.model.Article;
import com.github.gregbiv.news.util.StringUtils;
import com.github.gregbiv.news.util.UIUtils;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends EndlessAdapter<Article, NewsAdapter.NewsHolder> {
    public interface OnNewsClickListener {
        void onContentClicked(@NonNull final Article news, View view, int position);

        OnNewsClickListener DUMMY = new OnNewsClickListener() {
            @Override public void onContentClicked(@NonNull Article news, View view, int position) {}
        };
    }
    
    @NonNull private final Fragment mFragment;
    @NonNull private OnNewsClickListener mListener = OnNewsClickListener.DUMMY;
    
    public void setListener(@NonNull OnNewsClickListener listener) {
        this.mListener = listener;
    }

    public NewsAdapter(@NonNull Fragment fragment, List<Article> news) {
        super(fragment.getActivity(), news == null ? new ArrayList<>() : news);
        mFragment = fragment;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return (!isLoadMore(position)) ? mItems.get(position).getId() : -1;
    }

    @Override
    protected NewsAdapter.NewsHolder onCreateItemHolder(ViewGroup parent, int viewType) {
        return new NewsHolder(mInflater.inflate(R.layout.item_news, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {
            ((NewsHolder) holder).bind(mItems.get(position));
        }
    }

    final class NewsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.news_item_container) View mContentContainer;
        @BindView(R.id.news_item_title) TextView mTitleView;
        @BindView(R.id.news_item_image) ImageView mImageView;
        @BindView(R.id.news_item_description) TextView mDescriptionView;
        @BindView(R.id.news_item_feed) TextView mItemFeed;

        public NewsHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(@NonNull final Article news) {
            mContentContainer.setOnClickListener(view -> mListener.onContentClicked(news, view, getAdapterPosition()));

            mImageView.setVisibility(View.GONE);

            if (news.getIllustration() != null) {
                Glide.with(mFragment)
                        .load(news.getIllustration().getLarge())
                        .crossFade()
                        .placeholder(R.color.news_illustration_placeholder)
                        .into(mImageView);
                mImageView.setVisibility(View.VISIBLE);
            }

            mTitleView.setText(news.getTitle());
            mDescriptionView.setText(Html.fromHtml(news.getDescriptionPlain()).toString());
            mItemFeed.setText(StringUtils.join(" | ", new String[] {
                    news.getCategory() != null ? news.getCategory().getTitle() : "",
                    news.getFeed()     != null ? news.getFeed().getTitle()     : "",
                    UIUtils.getDisplayDate(news.getDate())
            }));
        }
    }
}
