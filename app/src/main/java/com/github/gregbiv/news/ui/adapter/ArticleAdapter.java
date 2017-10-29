
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;

import com.github.gregbiv.news.R;
import com.github.gregbiv.news.core.model.Article;
import com.github.gregbiv.news.util.StringUtils;
import com.github.gregbiv.news.util.UIUtils;

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

public class ArticleAdapter extends EndlessAdapter<Article, ArticleAdapter.ArticleHolder> {
    @NonNull
    private OnArticleClickListener mListener = OnArticleClickListener.DUMMY;
    @NonNull
    private final Fragment mFragment;

    public ArticleAdapter(@NonNull Fragment fragment, List<Article> article) {
        super(fragment.getActivity(),
                (article == null)
                        ? new ArrayList<>()
                        : article);
        mFragment = fragment;
        setHasStableIds(true);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {
            ((ArticleHolder) holder).bind(mItems.get(position));
        }
    }

    @Override
    protected ArticleAdapter.ArticleHolder onCreateItemHolder(ViewGroup parent, int viewType) {
        return new ArticleHolder(mInflater.inflate(R.layout.item_article, parent, false));
    }

    @Override
    public long getItemId(int position) {
        return (!isLoadMore(position))
                ? mItems.get(position).getId()
                : -1;
    }

    public void setListener(@NonNull OnArticleClickListener listener) {
        this.mListener = listener;
    }

    public interface OnArticleClickListener {
        OnArticleClickListener DUMMY = new OnArticleClickListener() {
            @Override
            public void onContentClicked(@NonNull Article article, View view, int position) {
            }
        };

        void onContentClicked(@NonNull final Article article, View view, int position);
    }


    final class ArticleHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.article_item_container)
        View mContentContainer;
        @BindView(R.id.article_item_title)
        TextView mTitleView;
        @BindView(R.id.article_item_image)
        ImageView mImageView;
        @BindView(R.id.article_item_description)
        TextView mDescriptionView;
        @BindView(R.id.article_item_feed)
        TextView mItemFeed;

        public ArticleHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(@NonNull final Article article) {
            mContentContainer.setOnClickListener(view -> mListener.onContentClicked(article,
                    view,
                    getAdapterPosition()));
            mImageView.setVisibility(View.GONE);

            if (article.getUrlToImage() != null) {
                Glide.with(mFragment)
                        .load(article.getUrlToImage())
                        .crossFade()
                        .placeholder(R.color.article_illustration_placeholder)
                        .into(mImageView);
                mImageView.setVisibility(View.VISIBLE);
            }

            mTitleView.setText(article.getTitle());
            mDescriptionView.setText(Html.fromHtml(article.getDescription()).toString());
            mItemFeed.setText(StringUtils.join(" | ", new String[]{
                    article.getCategory() != null ? article.getCategory().getTitle() : "",
                    article.getSource()   != null ? article.getSource().getTitle() : "",
                    UIUtils.getDisplayDate(article.getPublishedAt())
            }));
        }
    }
}
