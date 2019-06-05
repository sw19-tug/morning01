package at.tugraz.ist.swe.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
        Intent intent = new Intent();
        intent.setAction(NoteWidget.ACTION_NOTES_PULLED);
        context.sendBroadcast(intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        // update each of the app widgets with the remote adapter
        for (int i = 0; i < appWidgetIds.length; ++i) {

            // Set up the intent that starts the StackViewService, which will
            // provide the views for this collection.
            Intent intent = new Intent(context, NoteRemoteViewsService.class);
            // Add the app widget ID to the intent extras.
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            // Instantiate the RemoteViews object for the app widget layout.
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.note_widget);
            // Set up the RemoteViews object to use a RemoteViews adapter.
            // This adapter connects
            // to a RemoteViewsService  through the specified intent.
            // This is how you populate the data.
            rv.setRemoteAdapter(R.id.widgetNotesList, intent);

            // The empty view is displayed when the collection has no items.
            // It should be in the same layout used to instantiate the RemoteViews
            // object above.
            //rv.setEmptyView(R.id.stack_view, R.id.empty_view);

            //
            // Do additional processing specific to this app widget...
            //

            //appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetNotesList);
            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
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

