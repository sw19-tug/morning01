package at.tugraz.ist.swe.note;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class TagAdapter extends ArrayAdapter<NoteTag> {


    public TagAdapter(@NonNull Context context, @NonNull ArrayList<NoteTag> noteTagList) {

        super(context,0, noteTagList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.tag_list_item, parent, false);
        }

        TextView titleTextView = convertView.findViewById(R.id.titleTagView);
        ImageView colorImageView = convertView.findViewById(R.id.colorTag);

        NoteTag tag = getItem(position);
        if(tag == null) {
            return  convertView;
        }
        titleTextView.setText(tag.getName());
        colorImageView.setBackgroundColor(tag.getColor());


        return convertView;
    }
}
