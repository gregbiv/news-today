
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.ui.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.gregbiv.news.R;
import com.github.gregbiv.news.core.model.Article;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter;

public class ArticleSourceAdapter extends ArticleAdapter implements StickyHeaderAdapter<ArticleSourceAdapter.HeaderHolder> {
    private Map<Integer, String>  titleMap    = new HashMap<>();
    private Map<Integer, Integer> positionMap = new HashMap<>();

    public ArticleSourceAdapter(@NonNull Fragment fragment, List<Article> articles) {
        super(fragment, articles);

        // TODO sources
        titleMap.put(1, fragment.getString(R.string.category_1));
        titleMap.put(2, fragment.getString(R.string.category_2));
        titleMap.put(3, fragment.getString(R.string.category_3));
        titleMap.put(4, fragment.getString(R.string.category_4));

        sortItems(articles);
    }

    @Override
    public void add(@NonNull List<Article> newItems) {
        newItems = sortItems(newItems);

        if (!newItems.isEmpty()) {
            int currentSize = mItems.size();
            int amountInserted = newItems.size();

            mItems.addAll(newItems);
            notifyItemRangeInserted(currentSize, amountInserted);
        }
    }

    @Override
    public void onBindHeaderViewHolder(HeaderHolder viewHolder, int position) {
        viewHolder.header.setText(titleMap.get(mItems.get(position).getSourceId()));
    }

    @Override
    public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        final View view = mInflater.inflate(R.layout.feed_header, parent, false);

        return new HeaderHolder(view);
    }

    public List<Article> sortItems(@NonNull List<Article> newItems) {
        Collections.sort(newItems, new SourceCategoryComparator());
        positionMap.clear();

        int amount = 0;
        int i = 0;

        // preparing each container
        for (int categoryId : titleMap.keySet()) {
            List<Article> articlesList = new ArrayList<>();

            // do sort
            for (Article item : newItems) {
                if (categoryId == item.getSource().getCategory()) {
                    articlesList.add(item);
                }
            }

            positionMap.put(categoryId, i == 0 ? 0 : amount + articlesList.size());
            i++;
        }

        return newItems;
    }

    @Override
    public long getHeaderId(int position) {
        return positionMap.get(mItems.get(position).getSource().getCategory());
    }

    public class SourceCategoryComparator implements Comparator<Article> {
        @Override
        public int compare(Article lhs, Article rhs) {
            if (null != lhs.getSource() && null != rhs.getSource() && lhs.getSource().getCategory() == rhs.getSource().getCategory()) {
                if (lhs.getSourceId() > rhs.getSourceId()) {
                    return 1;
                }

                if (lhs.getSourceId() < rhs.getSourceId()) {
                    return -1;
                }

                return 0;
            }

            if (null != lhs.getSource() && null != rhs.getSource() && lhs.getSource().getCategory() > rhs.getSource().getCategory()) {
                return 1;
            }

            return -1;
        }
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {
        public TextView header;

        public HeaderHolder(View itemView) {
            super(itemView);
            header = (TextView) itemView;
        }
    }
}
