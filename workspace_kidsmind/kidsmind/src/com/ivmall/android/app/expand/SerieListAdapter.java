package com.ivmall.android.app.expand;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivmall.android.app.R;

import java.util.ArrayList;
import java.util.List;


public class SerieListAdapter extends ExpandableAdapter {

    protected List<SerieListItem> checkItems;

    public SerieListAdapter(Context context) {
        super(context);
        checkItems = new ArrayList<>();
    }


    public List<SerieListItem> getCheckItems() {
        return checkItems;
    }

    public class HeaderViewHolder extends ExpandableAdapter.HeaderViewHolder {
        TextView name;

        public HeaderViewHolder(View view) {
            super(view, (ImageView) view.findViewById(R.id.item_arrow));

            name = (TextView) view.findViewById(R.id.item_header_name);
        }

        public void bind(int position) {
            super.bind(position);

            name.setText(visibleItems.get(position).getText());
        }
    }

    public class BodyViewHolder extends ExpandableAdapter.ViewHolder {
        TextView name;
        CheckBox checkbox;

        public BodyViewHolder(View view) {
            super(view);

            name = (TextView) view.findViewById(R.id.item_name);
            checkbox = (CheckBox) view.findViewById(R.id.checkbox);

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int position = (int) buttonView.getTag();
                    SerieListItem item = visibleItems.get(position);
                    if (isChecked) {
                        item.setChecked(true);
                        checkItems.add(item);
                    } else {
                        item.setChecked(false);
                        checkItems.remove(item);
                    }
                }
            });

        }

        public void bind(int position, boolean isCheck) {
            name.setText(visibleItems.get(position).getText());
            checkbox.setTag(position);
            if (isCheck) {
                checkbox.setChecked(true);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case SerieListItem.TYPE_HEADER:
                return new HeaderViewHolder(inflate(R.layout.item_header, parent));
            case SerieListItem.TYPE_BODY:
            default:
                return new BodyViewHolder(inflate(R.layout.item_body, parent));
        }
    }

    @Override
    public void onBindViewHolder(ExpandableAdapter.ViewHolder holder, int position) {
        SerieListItem item = visibleItems.get(position);

        switch (getItemViewType(position)) {
            case SerieListItem.TYPE_HEADER:
                ((HeaderViewHolder) holder).bind(position);
                break;
            case SerieListItem.TYPE_BODY:
            default:
                if (item.isChecked()) {
                    ((BodyViewHolder) holder).bind(position, true);
                } else {
                    ((BodyViewHolder) holder).bind(position, false);
                }
                break;
        }
    }


}
