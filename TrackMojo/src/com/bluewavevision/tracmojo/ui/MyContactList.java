package com.bluewavevision.tracmojo.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import org.json.JSONException;

import com.bluewavevision.tracmojo.R;
import com.bluewavevision.tracmojo.adapters.ContactListAdapter;
import com.bluewavevision.tracmojo.model.Contact;
import com.bluewavevision.tracmojo.model.Follower;
import com.bluewavevision.tracmojo.util.AppSession;
import com.bluewavevision.tracmojo.util.Util;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MyContactList extends BaseActivity{

    ListView lvContactList;
    ArrayList<Contact> listContacts = new ArrayList<Contact>();
    EditText etSearchBox;
    public ContactListAdapter adapter;
    private ArrayList<Follower> listFollowers = new ArrayList<Follower>();
    private Button btnDone;
    private boolean isFromEditFollowers;
    private SharedPreferences preferences;
    private String selectedContacts = "";
    private boolean isForParticipants;

    private ArrayList<String> contactIdList = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_contact_list);

        Intent intent = getIntent();
        if (intent != null) {
            contactIdList = (ArrayList<String>) intent.getSerializableExtra("contactIdList");
            selectedContacts = intent.getStringExtra("selectedContacts");
            isFromEditFollowers = intent.getBooleanExtra("isFromEdit", false);
            isForParticipants = intent.getBooleanExtra("isForParticipants", false);
            if (isFromEditFollowers) {
                listFollowers = (ArrayList<Follower>) intent.getSerializableExtra("listFollowers");
            }
        }

        AppSession session = new AppSession(this);
        preferences = session.getPreferences();

        etSearchBox = (EditText) findViewById(R.id.etSearchBox);
        etSearchBox.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                /*String text = etSearchBox.getText().toString().toLowerCase(Locale.getDefault());
                if(text!=null && !TextUtils.isEmpty(text))
                    adapter.filter(text);*/
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                String text = "" + etSearchBox.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
                //if(text!=null && !TextUtils.isEmpty(text))
            }
        });

        lvContactList = (ListView) findViewById(R.id.listContact);

        new FetchContactsTask().execute();

        btnDone = (Button) findViewById(R.id.my_contact_list_btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.filter("");
                Intent intent = new Intent();
                intent.putExtra("contact_list", listContacts);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private boolean isExists(String email) {
        for (int i = 0; i < listFollowers.size(); i++) {
            Follower follower = listFollowers.get(i);
            if (follower.getEmailId().equalsIgnoreCase(email)) {
                return true;
            }
        }

        return false;
    }

    private boolean isLoggedInUser(String email) {
        if (preferences.getString("useremail", "").equalsIgnoreCase(email))
            return true;
        else
            return false;
    }

    public class FetchContactsTask extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;

        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MyContactList.this);
            progressDialog.setMessage("Please wait");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            getNameEmailDetails();
            //fetchContacts();

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressDialog.isShowing() && progressDialog!=null) {
                progressDialog.dismiss();
            }

            Collections.sort(listContacts);
            adapter = new ContactListAdapter(MyContactList.this, listContacts,isForParticipants);
            lvContactList.setAdapter(adapter);

        }
    }

    public void getNameEmailDetails() {
        Context context = MyContactList.this;
        ContentResolver cr = context.getContentResolver();
        String[] PROJECTION = new String[] { ContactsContract.RawContacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.CommonDataKinds.Email.DATA,
                ContactsContract.CommonDataKinds.Photo.CONTACT_ID };
        String order = "CASE WHEN "
                + ContactsContract.Contacts.DISPLAY_NAME
                + " NOT LIKE '%@%' THEN 1 ELSE 2 END, "
                + ContactsContract.Contacts.DISPLAY_NAME
                + ", "
                + ContactsContract.CommonDataKinds.Email.DATA
                + " COLLATE NOCASE";
        String filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, filter, null, order);
        if (cur.moveToFirst()) {
            do {
                Contact contact = new Contact();
                // names comes in hand sometimes
                String id = cur.getString(0);
                String name = cur.getString(1);
                String emlAddr = cur.getString(3);
                contact.setEmail(emlAddr);
                contact.setName(name);
                contact.setId(id);


                if (!TextUtils.isEmpty(contact.getEmail()) && !isExists(contact.getEmail()) && !isLoggedInUser(contact.getEmail()) && Util.emailValidator(emlAddr)){
                    try {
                        if(isAddAtFirst(contact.getId())){
                            contact.setSelected(true);
                            //listContacts.add(0,contact);
                        }else {
                        }
                        listContacts.add(contact);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } while (cur.moveToNext());
        }

        cur.close();
    }

    private boolean isAddAtFirst(String id) throws JSONException {
        for (int i = 0; i < contactIdList.size(); i++) {
            if(id.equalsIgnoreCase(contactIdList.get(i))){
                return true;
            }
        }
        return  false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideKeyboard();
    }

    protected void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        View focus = getCurrentFocus();

        if (focus != null) {

            inputManager.hideSoftInputFromWindow(focus.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
