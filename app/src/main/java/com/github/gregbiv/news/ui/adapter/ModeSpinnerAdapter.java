
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.ui.adapter;

import java.util.ArrayList;

import com.github.gregbiv.news.R;
import com.github.gregbiv.news.core.model.SpinnerItem;

import android.app.Activity;

import android.support.annotation.NonNull;

import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.TextView;

public class ModeSpinnerAdapter extends BaseAdapter {
    private ArrayList<SpinnerItem> mItems;
    Activity                       mContext;

    public ModeSpinnerAdapter(Activity context, ArrayList<SpinnerItem> items) {
        mItems   = (items == null)
                   ? new ArrayList<>()
                   : items;
        mContext = context;
    }

    public void addHeader(String title) {
        mItems.add(new SpinnerItem(true, "", title, false));
    }

    public void addItem(String tag, String title, boolean indented) {
        mItems.add(new SpinnerItem(false, tag, title, indented));
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    public void clear() {
        mItems.clear();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        if ((view == null) ||!view.getTag().toString().equals("DROPDOWN")) {
            view = mContext.getLayoutInflater().inflate(R.layout.item_toolbar_spinner_dropdown, parent, false);
            view.setTag("DROPDOWN");
        }

        TextView headerTextView = (TextView) view.findViewById(R.id.header_text);
        View     dividerView    = view.findViewById(R.id.divider_view);
        TextView normalTextView = (TextView) view.findViewById(android.R.id.text1);

        if (isHeader(position)) {
            headerTextView.setText(getTitle(position));
            headerTextView.setVisibility(View.VISIBLE);
            normalTextView.setVisibility(View.GONE);
            dividerView.setVisibility(View.VISIBLE);
        } else {
            headerTextView.setVisibility(View.GONE);
            normalTextView.setVisibility(View.VISIBLE);
            dividerView.setVisibility(View.GONE);
            setUpNormalDropdownView(position, normalTextView);
        }

        return view;
    }

    @Override
    public boolean isEnabled(int position) {
        return !isHeader(position);
    }

    private boolean isHeader(int position) {
        return (position >= 0) && (position < mItems.size()) && mItems.get(position).isHeader();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @NonNull
    public ArrayList<SpinnerItem> getItems() {
        return mItems;
    }

    public String getMode(int position) {
        return ((position >= 0) && (position < mItems.size()))
               ? mItems.get(position).getMode()
               : "";
    }

    private String getTitle(int position) {
        return ((position >= 0) && (position < mItems.size()))
               ? mItems.get(position).getTitle()
               : "";
    }

    private void setUpNormalDropdownView(int position, TextView textView) {
        textView.setText(getTitle(position));
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if ((view == null) ||!view.getTag().toString().equals("NON_DROPDOWN")) {
            view = mContext.getLayoutInflater().inflate(R.layout.item_toolbar_spinner, parent, false);
            view.setTag("NON_DROPDOWN");
        }

        TextView textView = (TextView) view.findViewById(android.R.id.text1);

        textView.setText(getTitle(position));

        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }
}
