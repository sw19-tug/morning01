package at.tugraz.ist.swe.note;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.min;

import at.tugraz.ist.swe.note.database.DatabaseHelper;
import at.tugraz.ist.swe.note.database.NotFoundException;


public class NoteActivity extends AppCompatActivity {
    public final static String NOTE_KEY = "note";
    public final static String FLAG_KEY = "flag";
    private Menu menu;
    private Note note;
    NoteStorage storage;
    NoteTagStorage tagStorage;
    private int requestCode;
    private int requestCodeProtectedActivity;
    private ArrayList<String> tagsAsStrings = new ArrayList<>();
    private final Map<String, Integer> tagColors = new HashMap<>();
    private MultiAutoCompleteTextView tagField;

    private void updateTagField(boolean onCreate) {
        StringBuilder tagsStringBuilder = new StringBuilder();
        for (int i = 0; i < tagsAsStrings.size(); i++) {
            String tagAsString = tagsAsStrings.get(i);
            if (i == tagsAsStrings.size() - 1 && !onCreate) {
                tagsStringBuilder.append(tagAsString);
            } else {
                int color = NoteTag.DEFAULT_COLOR;
                if (tagColors.containsKey(tagAsString)) {
                    color = tagColors.get(tagAsString);
                }
                tagsStringBuilder.append(NoteTag.formatAsHtml(tagAsString, color));
                tagsStringBuilder.append(' ');
            }

        }
        tagField.setText(Html.fromHtml(tagsStringBuilder.toString()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        storage = new NoteStorage(new DatabaseHelper(getApplicationContext()));
        tagStorage = new NoteTagStorage(new DatabaseHelper(getApplicationContext()));

        note = (Note) getIntent().getSerializableExtra(NOTE_KEY);
        if (note == null) {
            note = new Note();
        }
        requestCode = getIntent().getIntExtra(TrashActivity.REQUEST_CODE_KEY, 0);
        requestCodeProtectedActivity = getIntent().getIntExtra(ProtectedActivity.REQUEST_CODE_KEY, 0);

        TextView tfTitle = findViewById(R.id.tfTitle);
        tfTitle.setText(note.getTitle());
        tfTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                note.setTitle(s.toString());
            }
        });

        TextView tfContent = findViewById(R.id.tfContent);
        tfContent.setText(note.getContent());
        tfContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                note.setContent(s.toString());
            }
        });

        for (NoteTag noteTag : storage.getAssociatedTags(note)) {
            tagsAsStrings.add(noteTag.getName());
        }
        tagField = findViewById(R.id.tag_edit_field);
        tagField.setThreshold(1);
        NoteTag[] noteTags = tagStorage.getAllTags();
        for (NoteTag noteTag : noteTags) {
            tagColors.put(noteTag.getName(), noteTag.getColor());
        }
        tagField.setAdapter(new TagAdapter(this, noteTags));
        tagField.setTokenizer(new CharacterTokenizer(' '));
        updateTagField(true);

        tagField.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {
                int selectionStart = tagField.getSelectionStart();
                String input = String.valueOf(sequence);
                ArrayList<String> parts = new ArrayList<>(Arrays.asList(input.split("\\s", -1)));
                for (int i = tagsAsStrings.size() - 2; i >= 0; i--) { // Skip the last entered tag.
                    if (parts.get(i).length() < tagsAsStrings.get(i).length()) {
                        parts.remove(i);
                        selectionStart = Integer.MAX_VALUE;
                    }
                }
                boolean changeInTagFields = tagsAsStrings.size() != parts.size();
                tagsAsStrings = parts;
                if (changeInTagFields) {
                    updateTagField(false);
                    tagField.setSelection(min(selectionStart, tagField.getText().length()));
                }
            }
        });
        if (requestCode == TrashActivity.NOTE_RESTORE_CODE) {
            tfTitle.setEnabled(false);
            tfContent.setEnabled(false);
            tagField.setEnabled(false);
        }
        createToolbar();
    }

    private void createToolbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.add_new_note_toolbar));
        if (getSupportActionBar() != null) {
            if (note.getTitle().equals(""))
                getSupportActionBar().setTitle(R.string.text_new_note);
            else
                getSupportActionBar().setTitle(note.getTitle());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void enableUnpinningButton(boolean enabled) {
        menu.findItem(R.id.action_pinning).setVisible(!enabled);
        menu.findItem(R.id.action_unpinning).setVisible(enabled);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_new_note, menu);
        this.menu = menu;
        MenuItem disabledProtectButton = this.menu.findItem(R.id.action_protect_disabled);
        MenuItem setProtectedNoteButton = this.menu.findItem(R.id.action_protect);
        if (note.getTitle() == "" && note.getContent() == "") {
            setProtectedNoteButton.setVisible(false);
            disabledProtectButton.setVisible(true);
        } else {
            disabledProtectButton.setVisible(false);
            setProtectedNoteButton.setVisible(true);
        }
        enableUnpinningButton(false);

        MenuItem removeButton = this.menu.findItem(R.id.action_remove);
        removeButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                NoteActivity.this.deleteNote();
                return true;
            }
        });

        MenuItem pinningButton = this.menu.findItem(R.id.action_pinning);
        pinningButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                NoteActivity.this.pinningNote();
                return true;
            }
        });

        MenuItem unpinningButton = this.menu.findItem(R.id.action_unpinning);
        unpinningButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                NoteActivity.this.unpinningNote();
                return true;
            }
        });

        if (note.getPinned() > 0) {
            enableUnpinningButton(true);
        } else {
            enableUnpinningButton(false);
        }

        MenuItem shareButton = this.menu.findItem(R.id.action_share);
        shareButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                NoteActivity.this.callShare();
                return true;
            }
        });


        setProtectedNoteButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                setProtectedNote();
                return true;
            }
        });

        MenuItem unprotectedNoteButton = this.menu.findItem(R.id.action_unprotect);
        unprotectedNoteButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                unprotectNote();
                return true;
            }
        });

        MenuItem restoreButton = this.menu.findItem(R.id.action_restore);
        restoreButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                restoreNote();
                return true;
            }
        });

        if (requestCode == TrashActivity.NOTE_RESTORE_CODE) {
            shareButton.setVisible(false);
            pinningButton.setVisible(false);
            unpinningButton.setVisible(false);
            unprotectedNoteButton.setVisible(false);
            setProtectedNoteButton.setVisible(false);
        } else if (requestCodeProtectedActivity == ProtectedActivity.NOTE_PROTECT_CODE) {
            shareButton.setVisible(false);
            pinningButton.setVisible(false);
            unpinningButton.setVisible(false);
            restoreButton.setVisible(false);
            removeButton.setVisible(false);
            unprotectedNoteButton.setVisible(true);
            setProtectedNoteButton.setVisible(false);
        } else {
            restoreButton.setVisible(false);
            unprotectedNoteButton.setVisible(false);
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        OptionFlag flag = saveNote();
        startIntentMain(flag);
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private OptionFlag saveNote() {
        TextView tfTitle = findViewById(R.id.tfTitle);
        TextView tfContent = findViewById(R.id.tfContent);
        String title = tfTitle.getText().toString();
        String content = tfContent.getText().toString();

        note.setContent(content);
        note.setTitle(title);
        storage.dissociateAll(note);
        for (String tagAsString : tagsAsStrings) {
            if (tagAsString.length() == 0) {
                continue;
            }
            NoteTag noteTag = tagStorage.findByName(tagAsString);
            if (noteTag == null) {
                noteTag = new NoteTag(tagAsString, NoteTag.DEFAULT_COLOR);
                tagStorage.insert(noteTag);
            }
            note.addTag(noteTag);
        }
        if (note.getId() == Note.ILLEGAL_ID) {
            return OptionFlag.SAVE;
        } else {
            return OptionFlag.EDIT;
        }
    }

    private void deleteNote() {
        String title = "Confirm Delete";
        String message;
        AlertDialog.Builder confirmDeleteDialog = new AlertDialog.Builder(
                NoteActivity.this);
        if (requestCode == TrashActivity.NOTE_RESTORE_CODE) {
            message = "Are you sure you want permanently delete this note?";
        } else {
            message = "Are you sure you want send this note to trash?";
        }
        confirmDeleteDialog.setTitle(title);
        confirmDeleteDialog.setMessage(message);
        confirmDeleteDialog.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (requestCode == TrashActivity.NOTE_RESTORE_CODE) {
                            startIntentMain(OptionFlag.REMOVE);
                        } else {
                            startIntentMain(OptionFlag.SOFT_REMOVE);
                        }

                    }
                });

        confirmDeleteDialog.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        confirmDeleteDialog.show();
    }

    private void setProtectedNote() {
        String title = "Confirm Protection";
        String message = "Are you sure you want to move the note to the protected area?";
        AlertDialog.Builder confirmDeleteDialog = new AlertDialog.Builder(NoteActivity.this);
        confirmDeleteDialog.setTitle(title);
        confirmDeleteDialog.setMessage(message);
        confirmDeleteDialog.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        note.setProtected(true);
                        startIntentMain(OptionFlag.PROTECT);
                    }
                });

        confirmDeleteDialog.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        confirmDeleteDialog.show();
    }

    private void unprotectNote() {
        AlertDialog.Builder confirmUnprotectDialog = new AlertDialog.Builder(
                NoteActivity.this);

        confirmUnprotectDialog.setTitle("Confirm Unprotection");
        confirmUnprotectDialog.setMessage("Are you sure you want move this note in unprotected area?");
        confirmUnprotectDialog.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startIntentMain(OptionFlag.UNPROTECT);
                    }
                });

        confirmUnprotectDialog.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        confirmUnprotectDialog.show();
    }

    private void restoreNote() {
        AlertDialog.Builder confirmDeleteDialog = new AlertDialog.Builder(
                NoteActivity.this);

        confirmDeleteDialog.setTitle("Confirm Restore");
        confirmDeleteDialog.setMessage("Are you sure you want restore this note?");
        confirmDeleteDialog.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startIntentMain(OptionFlag.RESTORE);

                    }
                });

        confirmDeleteDialog.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        confirmDeleteDialog.show();
    }

    private void pinningNote() {
        note.setPinned(getCurrentPinningNumber());
        enableUnpinningButton(true);
    }

    private void unpinningNote() {
        note.setPinned(0);
        enableUnpinningButton(false);
    }

    private void callShare() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, note.getTitle());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, note.getContent());
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));
    }

    private void startIntentMain(OptionFlag flag) {
        Intent noteIntent = new Intent();
        noteIntent.putExtra(NOTE_KEY, note);
        noteIntent.putExtra(FLAG_KEY, flag);
        boolean widgetCall = getIntent().getBooleanExtra("widget", false);
        if (widgetCall) {
            try {
                storage.update(note);
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            return;
        }
        setResult(RESULT_OK, noteIntent);
        finish();
    }

    public Note getNote() {
        return note;
    }

    int getCurrentPinningNumber() {
        return storage.getNewPinningNumber();
    }
}