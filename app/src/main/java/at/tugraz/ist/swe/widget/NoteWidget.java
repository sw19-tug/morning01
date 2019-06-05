package at.tugraz.ist.swe.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.widget.RemoteViews;

import java.util.ArrayList;

import at.tugraz.ist.swe.note.MainActivity;
import at.tugraz.ist.swe.note.Note;
import at.tugraz.ist.swe.note.R;

/**
 * Implementation of App Widget functionality.
 */
public class NoteWidget extends AppWidgetProvider {

    public static final String ACTION_NOTES_PULLED = "at.tugraz.ist.swe.widget.NOTES_PULLED";

    public static void sendRefreshBroadcast(Context context) {
        Intent intent = new Intent(context,NoteWidget.class);
        intent.setAction(NoteWidget.ACTION_NOTES_PULLED);
        context.sendBroadcast(intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; ++i) {
            Intent intent = new Intent(context, NoteRemoteViewsService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.note_widget);
            rv.setRemoteAdapter(R.id.widgetNotesList, intent);
            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_NOTES_PULLED)) {
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, NoteWidget.class);
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.widgetNotesList);
        }
        super.onReceive(context, intent);
    }
}

