package at.tugraz.ist.swe.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class NoteRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new NoteRemoteViewsFactory(this.getApplicationContext(), intent);
    }


}
