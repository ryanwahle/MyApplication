package com.ryanwahle.myapplication;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryanwahle on 9/1/15.
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private final List<Contact> mContacts;
    private List<Contact> mFilteredContacts;

    private int mSelectedContactID;

    public ContactsAdapter(List<Contact> contacts) {
        mContacts = contacts;
        mFilteredContacts = new ArrayList<>(mContacts);
    }

    public int getSelectedContactID() {
        return mSelectedContactID;
    }

    public void applyFilter(CharSequence filterString) {
        mFilteredContacts.clear();

        if (filterString.length() == 0) {
            mFilteredContacts = new ArrayList<>(mContacts);
            notifyDataSetChanged();
            return;
        }

        String filter = filterString.toString().toLowerCase().trim();

        for (Contact contact : mContacts) {
            String contactNameLowerCase = contact.getName().toLowerCase();
            if (contactNameLowerCase.contains(filter)) {
                mFilteredContacts.add(contact);
            }
        }

        notifyDataSetChanged();
    }

    public void removeFilter() {
        applyFilter("");
    }

    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View contactView = layoutInflater.inflate(R.layout.item_contact, parent, false);

        final ViewHolder viewHolder = new ViewHolder(contactView);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact contact = mFilteredContacts.get(viewHolder.getAdapterPosition());

                mSelectedContactID = contact.getContactID();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                }, 300);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ContactsAdapter.ViewHolder viewHolder, int position) {
        Contact contact = mFilteredContacts.get(position);

        CardView cardView = viewHolder.cardView;

        if (position % 2 == 0) {
            cardView.setBackgroundColor(Color.LTGRAY);
        } else {
            cardView.setBackgroundColor(Color.WHITE);
        }

        TextView textView = viewHolder.nameTextView;
        textView.setText(contact.getName());

        RadioButton radioButton = viewHolder.selectedButton;

        if (contact.getContactID() == mSelectedContactID) {
            radioButton.setChecked(true);
        } else {
            radioButton.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return mFilteredContacts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView nameTextView;
        public final RadioButton selectedButton;
        public final CardView cardView;
        public final LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardView);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
            nameTextView = (TextView) itemView.findViewById(R.id.contact_name);
            selectedButton = (RadioButton) itemView.findViewById(R.id.selected_button);
        }
    }
}
