package at.tugraz.ist.swe.note;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.ArrayList;

import at.tugraz.ist.swe.note.database.DatabaseHelper;
import at.tugraz.ist.swe.note.database.NotFoundException;

public class TagListActivity extends AppCompatActivity {
    private ListView tagListView;
    private TagAdapter tagAdaper;
    private NoteTagStorage tagStorage;
    ArrayList<NoteTag> tags;
    private static final int TAG_REQUEST_CODE = 2;
    public static final String TAG_KEY = "tag_key";
    int currentSelectedTag = 0;
    private boolean editMode = false;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags_list);
        Toolbar toolbar = findViewById(R.id.tagsActivityToolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.tags_activity_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FloatingActionButton fab = findViewById(R.id.createTagButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), TagActivity.class);
                startActivityForResult(intent, TAG_REQUEST_CODE);
            }
        });

        tagListView = findViewById(R.id.tagListView);
        tags = new ArrayList<NoteTag>();
        tagAdaper = new TagAdapter(this, tags);
        tagListView.setAdapter(tagAdaper);
        tagStorage = new NoteTagStorage(new DatabaseHelper(this));
        setTagList(tagStorage.getAllTags());
        tagListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(editMode) {
                    Intent intent = new Intent(view.getContext(), TagActivity.class);
                    intent.putExtra(TagActivity.TAG_KEY, tags.get(position));
                    startActivityForResult(intent, TAG_REQUEST_CODE);
                    currentSelectedTag = position;
                }else{
                    Intent noteIntent = new Intent();
                    noteIntent.putExtra(TAG_KEY, tags.get(position));
                    setResult(RESULT_OK, noteIntent);
                    finish();
                }
            }
        });
    }
    public void setTagList(NoteTag[] tagsList) {
        tags.clear();
        for(NoteTag tag : tagsList){
            tags.add(tag);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_tags_list_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tagEditToggleSwitch:
                if(editMode){
                    editMode = false;
                    item.setIcon(R.drawable.ic_sort_black_24dp);
                }else{
                    editMode = true;
                    item.setIcon(R.drawable.ic_mode_edit_black_24dp);
                }
            break;
            default:
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAG_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                NoteTag tag = (NoteTag) data.getSerializableExtra(TagActivity.TAG_KEY);
                if (tag.getName().isEmpty())
                {
                    return;
                }
                OptionFlag flag = (OptionFlag) data.getSerializableExtra(TagActivity.FLAG_KEY);

                switch (flag)
                {
                    case SAVE:
                        tags.add(tag);
                        tagStorage.insert(tag);
                        tagAdaper.notifyDataSetChanged();
                        break;
                    case EDIT:
                        try {
                            tagStorage.update(tag);
                            tags.set(currentSelectedTag, tag);
                            tagAdaper.notifyDataSetChanged();
                        } catch (NotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                    case REMOVE:
                        try {
                            tags.remove(tag);
                            tagStorage.delete(tag.getId());
                            tagAdaper.notifyDataSetChanged();
                        } catch (NotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                }


                if (resultCode == RESULT_CANCELED) {
                }
            }
        }
    }

}

