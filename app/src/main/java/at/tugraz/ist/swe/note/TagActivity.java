package at.tugraz.ist.swe.note;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.colorpicker.ColorPickerDialog;
import com.android.colorpicker.ColorPickerSwatch;

public class TagActivity extends AppCompatActivity {
    public final static String TAG_KEY = "tag";
    public final static String FLAG_KEY = "tag_flag";
    private Menu menu;
    private NoteTag tag;
    boolean editFlag = false;
    NoteTagStorage storage;
    ImageView colorView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        tag = (NoteTag) getIntent().getSerializableExtra(TAG_KEY);
        if(tag == null){
            tag = new NoteTag();
        }

        setTitle(R.string.tags_activity_title);

        TextView tagNameTextView = findViewById(R.id.tagNameEditText);
        tagNameTextView.setText(tag.getName());
        tagNameTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tag.setName(s.toString());
            }
        });

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                boolean keepOriginal = true;
                StringBuilder sb = new StringBuilder(end - start);
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (isCharAllowed(c))
                        sb.append(c);
                    else
                        keepOriginal = false;
                }
                if (keepOriginal)
                    return null;
                else {
                    if (source instanceof Spanned) {
                        SpannableString sp = new SpannableString(sb);
                        TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, sp, 0);
                        return sp;
                    } else {
                        return sb;
                    }
                }
            }

            private boolean isCharAllowed(char c) {
                return !Character.isWhitespace(c);
            }
        };
        tagNameTextView.setFilters(new InputFilter[] { filter });

        colorView = findViewById(R.id.colorTagSelector);
        colorView.setBackgroundColor(tag.getColor());
        colorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showColourPicker(view);
            }
        });
        createToolbar();
    }

    private  void  createToolbar(){
        setSupportActionBar((Toolbar) findViewById(R.id.add_new_tag_toolbar));
        if(getSupportActionBar() != null) {
            if(tag.getName().equals(""))
                getSupportActionBar().setTitle(R.string.text_new_tag);
            else
                getSupportActionBar().setTitle(R.string.text_edit_tag);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_new_tag, menu);
        this.menu = menu;

        MenuItem removeButton = this.menu.findItem(R.id.action_tag_remove);
        removeButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                TagActivity.this.deleteNoteTag();
                return true;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        OptionFlag flag = saveNoteTag();
        startIntentMain(flag);
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private OptionFlag saveNoteTag(){
        TextView tagNameEditText = (TextView)findViewById(R.id.tagNameEditText);
        String name = tagNameEditText.getText().toString();

        if (tag.getId() == NoteTag.ILLEGAL_ID) {
            return OptionFlag.SAVE;
        } else {
            tag.setName(name);
            return OptionFlag.EDIT;
        }
    }

    private void startIntentMain(OptionFlag flag)
    {
        Intent tagIntent = new Intent();
        tagIntent.putExtra(TAG_KEY, tag);
        tagIntent.putExtra(FLAG_KEY, flag);
        setResult(RESULT_OK, tagIntent);
        finish();
    }

    private void deleteNoteTag(){
        AlertDialog.Builder confirmDeleteDialog = new AlertDialog.Builder(
                TagActivity.this);

        confirmDeleteDialog.setTitle("Confirm Delete");
        confirmDeleteDialog.setMessage("Are you sure you want delete this tag?");
        confirmDeleteDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startIntentMain(OptionFlag.REMOVE);
                    }
                });

        confirmDeleteDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        confirmDeleteDialog.show();
    }

    public void showColourPicker(View view) {
        final ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
        colorPickerDialog.initialize(R.string.color_picker_default_title,
                new int[]{
                        getResources().getColor(R.color.Yellow),
                        getResources().getColor(R.color.DarkOrange),
                        getResources().getColor(R.color.Coral),
                        getResources().getColor(R.color.HotPink),
                        getResources().getColor(R.color.Tomato),
                        getResources().getColor(R.color.PowderBlue),
                        getResources().getColor(R.color.GreenYellow),
                        getResources().getColor(R.color.LightBlue),
                        getResources().getColor(R.color.DarkGray),
                        getResources().getColor(R.color.Brown),
                        getResources().getColor(R.color.Sienna),
                        getResources().getColor(R.color.YellowGreen),
                        getResources().getColor(R.color.DarkOrchid),
                        getResources().getColor(R.color.PaleGreen),
                        getResources().getColor(R.color.DarkViolet),
                        getResources().getColor(R.color.MediumPurple),
                        getResources().getColor(R.color.LightGreen),
                        getResources().getColor(R.color.SaddleBrown),
                        getResources().getColor(R.color.DarkMagenta),
                        getResources().getColor(R.color.MediumSlateBlue),
                        getResources().getColor(R.color.LightSlateGray),
                        getResources().getColor(R.color.SlateGray),
                        getResources().getColor(R.color.OliveDrab),
                        getResources().getColor(R.color.Aqua),
                        getResources().getColor(R.color.Cyan),
                        getResources().getColor(R.color.SpringGreen),
                        getResources().getColor(R.color.Lime),
                        getResources().getColor(R.color.MediumSpringGreen),
                        getResources().getColor(R.color.DarkTurquoise),
                        getResources().getColor(R.color.DeepSkyBlue),
                        getResources().getColor(R.color.DarkCyan),
                        getResources().getColor(R.color.colorPrimaryDark),
                }, getResources().getColor(R.color.DarkCyan), 4, 2);

        colorPickerDialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                tag.setColor(color);
                colorView.setBackgroundColor(color);
            }
        });

        android.app.FragmentManager fm = this.getFragmentManager();
        colorPickerDialog.show(fm, getString(R.string.color_picker));
    }
}
