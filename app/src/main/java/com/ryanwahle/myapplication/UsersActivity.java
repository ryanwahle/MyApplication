package com.ryanwahle.myapplication;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Layout;
import android.text.Selection;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class UsersActivity extends AppCompatActivity {
    private static final String TAG = "UsersActivity";
    GestureDetector singleTapGestureDetector;
    private EditText searchEditText;

    // Converts a view into a BitmapDrawable
    private static BitmapDrawable convertViewToDrawable(View view) {
        int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        view.measure(measureSpec, measureSpec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);

        return new BitmapDrawable(Resources.getSystem(), view.getDrawingCache(true));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        List<Contact> contactsList = Contact.createContactsList(20);
        contactsList.add(0, new Contact("Ryan Wahle"));
        contactsList.add(3, new Contact("James Brown"));

        final ContactsAdapter contactsAdapter = new ContactsAdapter(contactsList);

        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
        rvContacts.setAdapter(contactsAdapter);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.setHasFixedSize(true);

        searchEditText = (EditText) findViewById(R.id.search_editText);

        singleTapGestureDetector = new GestureDetector(getApplicationContext(), new SingleTapGestureListener());

        searchEditText.addTextChangedListener(new SearchEditTextFiltersTextWatcher(contactsAdapter));
        searchEditText.addTextChangedListener(new SearchTagTextWatcher());
        searchEditText.setOnTouchListener(new SearchEditTextOnTouchListener());

        Button createTagsButton = (Button) findViewById(R.id.createTags_button);
        createTagsButton.setOnClickListener(new CreateTagsButtonOnClickListener());
    }

    // Creates a new TextView object, sets the text, and sets the background drawable
    private TextView createContactTextView(String text) {
        TextView textView = new TextView(this);

        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics()));
        textView.setBackgroundResource(R.drawable.bubble);
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_remove_search_tag, 0, 0, 0);

        return textView;
    }

    private static class SearchEditTextFiltersTextWatcher implements TextWatcher {
        private final ContactsAdapter contactsAdapter;

        public SearchEditTextFiltersTextWatcher(ContactsAdapter contactsAdapter) {
            this.contactsAdapter = contactsAdapter;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            contactsAdapter.applyFilter(s);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private class SearchTagTextWatcher implements TextWatcher {
        int numberOfCharactersAdded = 0;
        int numberOfCharactersDeleted = 0;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            numberOfCharactersAdded = after;
            numberOfCharactersDeleted = count;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            int selectionStart = Selection.getSelectionStart(s);
            int selectionEnd = Selection.getSelectionEnd(s);

            if (selectionStart < 0) selectionStart = 0;
            if (selectionEnd < 0) selectionEnd = 0;

            if (numberOfCharactersAdded == 0 && numberOfCharactersDeleted == 1) {
                ImageSpan searchTagImageSpans[] = s.getSpans(selectionStart, selectionEnd, ImageSpan.class);
                if (searchTagImageSpans.length > 0) {
                    Selection.setSelection(s, s.getSpanStart(searchTagImageSpans[0]), s.getSpanEnd(searchTagImageSpans[0]));
                }
            }

        }
    }

    private class CreateTagsButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String currentSearchTextString = searchEditText.getText().toString();

            // Clear the Search EditText
            searchEditText.setText("");

            TextUtils.SimpleStringSplitter searchTagsSplitter = new TextUtils.SimpleStringSplitter(' ');
            searchTagsSplitter.setString(currentSearchTextString);

            while (searchTagsSplitter.hasNext()) {
                String tag = searchTagsSplitter.next();

                if (!tag.isEmpty()) {
                    TextView textView = createContactTextView(tag);

                    android.graphics.drawable.BitmapDrawable bitmapDrawable = convertViewToDrawable(textView);
                    bitmapDrawable.setBounds(0, 0, bitmapDrawable.getIntrinsicWidth(), bitmapDrawable.getIntrinsicHeight());

                    ImageSpan imageSpan = new ImageSpan(bitmapDrawable, ImageSpan.ALIGN_BASELINE);

                    SpannableString spannableString = new SpannableString(tag + " ");
                    spannableString.setSpan(imageSpan, 0, tag.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    searchEditText.append(spannableString);
                }
            }
        }
    }

    private class SearchEditTextOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            return singleTapGestureDetector.onTouchEvent(event);
            //return false;
        }
    }

    private class SingleTapGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();

            x -= searchEditText.getTotalPaddingLeft();
            y -= searchEditText.getTotalPaddingTop();

            x += searchEditText.getScrollX();
            y += searchEditText.getScrollY();

            Layout layout = searchEditText.getLayout();
            int line = layout.getLineForVertical(y);
            int offset = layout.getOffsetForHorizontal(line, x);

            ImageSpan[] imageSpans = searchEditText.getText().getSpans(0, searchEditText.getText().length(), ImageSpan.class);

            for (ImageSpan imageSpan : imageSpans) {
                int spanStart = searchEditText.getText().getSpanStart(imageSpan);
                int spanEnd = searchEditText.getText().getSpanEnd(imageSpan);

                if (spanStart <= offset && spanEnd >= offset) {
                    searchEditText.getText().replace(spanStart, spanEnd + 1, "");
                    return true;
                } else {
                    // There are no image spans selected, so user is touching at end of text so always make sure there is a space there
                    int editTextLength = searchEditText.length();

                    if (!searchEditText.getText().subSequence(editTextLength - 1, editTextLength).toString().contentEquals(" ")) {
                        searchEditText.append(" ");
                        searchEditText.setSelection(searchEditText.length());
                    }
                }
            }

            return false;
        }
    }
}

