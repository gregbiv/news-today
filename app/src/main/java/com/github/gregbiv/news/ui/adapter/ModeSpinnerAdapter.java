
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.ui.adapter;

//~--- non-JDK imports --------------------------------------------------------

import android.app.Activity;

import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.gregbiv.news.R;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;

public class ModeSpinnerAdapter extends BaseAdapter {
    private ArrayList<ModeSpinnerItem> mItems = new ArrayList<ModeSpinnerItem>();
    Activity                           mContext;

    public ModeSpinnerAdapter(Activity context) {
        mContext = context;
    }

    public void clear() {
        mItems.clear();
    }

    public void addItem(String tag, String title, boolean indented) {
        mItems.add(new ModeSpinnerItem(false, tag, title, indented));
    }

    public void addHeader(String title) {
        mItems.add(new ModeSpinnerItem(true, "", title, false));
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private boolean isHeader(int position) {
        return (position >= 0) && (position < mItems.size()) && mItems.get(position).isHeader;
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
    public View getView(int position, View view, ViewGroup parent) {
        if ((view == null) ||!view.getTag().toString().equals("NON_DROPDOWN")) {
            view = mContext.getLayoutInflater().inflate(R.layout.item_toolbar_spinner, parent, false);
            view.setTag("NON_DROPDOWN");
        }

        TextView textView = (TextView) view.findViewById(android.R.id.text1);

        textView.setText(getTitle(position));

        return view;
    }

    private String getTitle(int position) {
        return ((position >= 0) && (position < mItems.size()))
               ? mItems.get(position).title
               : "";
    }

    public String getMode(int position) {
        return ((position >= 0) && (position < mItems.size()))
               ? mItems.get(position).mode
               : "";
    }

    private void setUpNormalDropdownView(int position, TextView textView) {
        textView.setText(getTitle(position));
    }

    @Override
    public boolean isEnabled(int position) {
        return !isHeader(position);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    private class ModeSpinnerItem {
        boolean isHeader;
        String  mode, title;
        boolean indented;

        ModeSpinnerItem(boolean isHeader, String mode, String title, boolean indented) {
            this.isHeader = isHeader;
            this.mode     = mode;
            this.title    = title;
            this.indented = indented;
        }
    }
}
