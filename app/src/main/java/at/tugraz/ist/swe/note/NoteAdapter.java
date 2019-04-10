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

public class NoteAdapter extends ArrayAdapter<Note> {

    public NoteAdapter(@NonNull Context context, @NonNull ArrayList<Note> notesList) {
        super(context,0, notesList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        Note note = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.note_list_item, parent, false);
        }

        TextView titleTextView = (TextView)convertView.findViewById(R.id.titleTextView);
        TextView contentTextView = (TextView)convertView.findViewById(R.id.contentTextView);

        titleTextView.setText(note.getTitle());
        contentTextView.setText(note.getContent());

        return convertView;
    }
}
