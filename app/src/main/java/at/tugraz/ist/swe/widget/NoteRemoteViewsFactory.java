package at.tugraz.ist.swe.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.Arrays;

import at.tugraz.ist.swe.note.Note;
import at.tugraz.ist.swe.note.NoteStorage;
import at.tugraz.ist.swe.note.R;
import at.tugraz.ist.swe.note.database.DatabaseHelper;

public class NoteRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private int appWidgetId;
    private NoteStorage noteStorage;
    private ArrayList<Note> notes = null;

    public NoteRemoteViewsFactory(Context context, Intent intent) {
        this.mContext = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        noteStorage = new NoteStorage(new DatabaseHelper(context));
    }

    private void updateWidgetListView()
    {
        Note[] notes = noteStorage.getAll();
        ArrayList<Note> convertedToList = new ArrayList<>(Arrays.asList(notes));
        this.notes = convertedToList;
    }

    @Override
    public void onCreate() {
        updateWidgetListView();
    }

    @Override
    public void onDataSetChanged() {
        updateWidgetListView();
    }

    @Override
    public void onDestroy() {
        notes.clear();
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row = new RemoteViews(mContext.getPackageName(), R.layout.note_list_item);

        Note note = notes.get(position);

        row.setTextViewText(R.id.titleTextView, note.getTitle());
        row.setTextViewText(R.id.contentTextView, note.getContent());

        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
