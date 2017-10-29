
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.ui.adapter;

//~--- non-JDK imports --------------------------------------------------------

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter;

import com.github.gregbiv.news.R;
import com.github.gregbiv.news.core.model.Article;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsFeedAdapter extends NewsAdapter implements StickyHeaderAdapter<NewsFeedAdapter.HeaderHolder> {
    private Map<Integer, String>  titleMap    = new HashMap<>();
    private Map<Integer, Integer> positionMap = new HashMap<>();

    public NewsFeedAdapter(@NonNull Fragment fragment, List<Article> news) {
        super(fragment, news);
        // TODO sources

        sortItems(news);
    }

    @Override
    public void add(@NonNull List<Article> newItems) {
        newItems = sortItems(newItems);

        if (!newItems.isEmpty()) {
            int currentSize    = mItems.size();
            int amountInserted = newItems.size();

            mItems.addAll(newItems);
            notifyItemRangeInserted(currentSize, amountInserted);
        }
    }

    public List<Article> sortItems(@NonNull List<Article> newItems) {
        Collections.sort(newItems, new FeedCategoryComparator());
        positionMap.clear();
        int ammount = 0;
        int i       = 0;

        // preparing each container
        for (int categoryId : titleMap.keySet()) {
            List<Article> newsList = new ArrayList<>();

            // do sort
            for (Article item : newItems) {
                if (categoryId == item.getFeed().getCategory()) {
                    newsList.add(item);
                }
            }

            positionMap.put(categoryId, i == 0 ? 0 : ammount + newsList.size());
            i++;
        }

        return newItems;
    }

    @Override
    public long getHeaderId(int position) {
        return positionMap.get(mItems.get(position).getFeed().getCategory());
    }

    @Override
    public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        final View view = mInflater.inflate(R.layout.feed_header, parent, false);
        return new HeaderHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderHolder viewHolder, int position) {
        viewHolder.header.setText(titleMap.get(mItems.get(position).getFeed().getCategory()));
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {
        public TextView header;

        public HeaderHolder(View itemView) {
            super(itemView);

            header = (TextView) itemView;
        }
    }

    public class FeedCategoryComparator implements Comparator<Article> {
        @Override
        public int compare(Article lhs, Article rhs) {
            if (null != lhs.getFeed() && null != rhs.getFeed() && lhs.getFeed().getCategory() == rhs.getFeed().getCategory()) {
                if (lhs.getFeedId() > rhs.getFeedId()) {
                    return 1;
                }

                if (lhs.getFeedId() < rhs.getFeedId()) {
                    return -1;
                }

                return 0;
            }

            if (null != lhs.getFeed() && null != rhs.getFeed() && lhs.getFeed().getCategory() > rhs.getFeed().getCategory()) {
                return 1;
            }

            return -1;
        }
    }
}
