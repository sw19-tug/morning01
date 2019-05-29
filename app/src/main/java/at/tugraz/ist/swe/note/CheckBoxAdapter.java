package at.tugraz.ist.swe.note;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CheckBoxAdapter extends ArrayAdapter<Note> {

    public CheckBoxAdapter(@NonNull Context context, @NonNull ArrayList<Note> notesList) {
        super(context,0, notesList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.note_list_item_checkbox, parent, false);
        }

        TextView titleTextView = convertView.findViewById(R.id.titleTextView);
        TextView contentTextView = convertView.findViewById(R.id.contentTextView);
        //CheckBox checkBox =  convertView.findViewById(R.id.checkBox);

        Note note = getItem(position);
        if(note == null) {
            return  convertView;
        }
        titleTextView.setText(note.getTitle());
        contentTextView.setText(note.getContent());
        //checkBox.setVisibility(View.VISIBLE);

        return convertView;
    }

}
