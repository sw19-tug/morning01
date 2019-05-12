package at.tugraz.ist.swe.note;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class TagListActivity extends AppCompatActivity {
    private ListView tagListView;
    private TagAdapter tagAdaper;
    private ArrayList<NoteTag> tags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags_list);
        Toolbar toolbar = findViewById(R.id.tagsActivityToolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.createTagButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        tagListView = findViewById(R.id.tagListView);
        tags = new ArrayList<NoteTag>();
        tagAdaper = new TagAdapter(this, tags);
        tagListView.setAdapter(tagAdaper);


    }
    public void setTagList(NoteTag[] tagsList) {
        tags.clear();
        for(NoteTag tag : tagsList){
            tags.add(tag);
        }
    }

}

