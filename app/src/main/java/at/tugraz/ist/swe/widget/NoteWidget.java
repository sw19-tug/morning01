package at.tugraz.ist.swe.widget;

import android.app.PendingIntent;
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
import at.tugraz.ist.swe.note.NoteActivity;
import at.tugraz.ist.swe.note.R;
import at.tugraz.ist.swe.note.TrashActivity;

import static android.support.v4.content.ContextCompat.startActivity;


/**
 * Implementation of App Widget functionality.
 */
public class NoteWidget extends AppWidgetProvider {

    public static final String ACTION_NOTES_PULLED = "at.tugraz.ist.swe.widget.NOTES_PULLED";
    public static final String EXTRA_ITEM = "at.tugraz.ist.swe.widget.EXTRA_ITEM";

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

            // This section makes it possible for items to have individualized behavior.
            // It does this by setting up a pending intent template. Individuals items of a collection
            // cannot set up their own pending intents. Instead, the collection as a whole sets
            // up a pending intent template, and the individual items set a fillInIntent
            // to create unique behavior on an item-by-item basis.
            // Set the action for the intent.
            // When the user touches a particular view.

            Intent startActivityIntent = new Intent(context, NoteActivity.class);
            startActivityIntent.setAction(EXTRA_ITEM);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, startActivityIntent,
                    0);
            rv.setPendingIntentTemplate(R.id.widgetNotesList, pendingIntent);


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

