package com.ryanwahle.myapplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryanwahle on 9/1/15.
 */
class Contact {
    private static int mContactIDCount = 0;

    private final String mName;
    private final int mContactID;

    public Contact(String name) {
        mContactID = ++mContactIDCount;
        mName = name;
    }

    public static List<Contact> createContactsList(int numContacts) {
        List<Contact> contacts = new ArrayList<>();

        for (int i = 1; i <= numContacts; i++) {
            contacts.add(new Contact("Person " + i));
        }

        return contacts;
    }

    public int getContactID() {
        return mContactID;
    }

    public String getName() {
        return mName;
    }
}
