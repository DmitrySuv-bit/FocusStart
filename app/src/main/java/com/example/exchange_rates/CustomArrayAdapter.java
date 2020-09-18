package com.example.exchange_rates;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CustomArrayAdapter extends ArrayAdapter<ItemExchangeRates> {
    private LayoutInflater inflater;
    private List<ItemExchangeRates> listItem;

    public CustomArrayAdapter(@NonNull Context context, int resource, List<ItemExchangeRates> listItem, LayoutInflater inflater) {
        super(context, resource, listItem);
        this.inflater = inflater;
        this.listItem = listItem;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        ItemExchangeRates listItemMain = listItem.get(position);
        if(convertView == null){
            convertView = inflater.inflate(R.layout.list_item, null, true);
            viewHolder = new ViewHolder();


            viewHolder.charCode = convertView.findViewById(R.id.textViewCharCode);

            viewHolder.name = convertView.findViewById(R.id.textViewName);
            viewHolder.value = convertView.findViewById(R.id.textViewValue);
            viewHolder.previous = convertView.findViewById(R.id.textViewPrevious);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.charCode.setText(listItemMain.getCharCode());
        viewHolder.name.setText(listItemMain.getName());
        viewHolder.value.setText(listItemMain.getValue());
        viewHolder.previous.setText(listItemMain.getPrevious());


        return convertView;
    }
    private static class ViewHolder{
        TextView charCode;
        TextView name;
        TextView value;
        TextView previous;
    }
}
