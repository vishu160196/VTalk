package com.example.vishal.vtalk;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.R.attr.description;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private static Database mDbHelper;
    private static ListView mContactList;
    private static ListView mChatsList;

    private static Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDbHelper = new Database(getApplicationContext());
        mContactList = (ListView)findViewById(R.id.contacts_list);
        mChatsList=(ListView)findViewById(R.id.chats_list);
        mContext=getApplicationContext();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addContact();
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        // link ViewPager with TabLayout
        tabLayout.setupWithViewPager(mViewPager);

    }

    private void addContact() {
        AddNewContact newContact = AddNewContact.newContact("Add new contact");
        newContact.show(getFragmentManager(), "launchAddContactTypeDialog");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);

        if (searchItem != null) {
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    //some operation
                    return true;
                }
            });
            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //some operation
                }
            });
            EditText searchPlate = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchPlate.setHint("Search");
            View searchPlateView = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
            searchPlateView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            // use this method for search process
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // use this method when query submitted
                    SearchClient searchClient = LoginActivity.retrofit.create(SearchClient.class);

                    Call<SearchResponse> call = searchClient.searchUser(query);
                    call.enqueue(new Callback<SearchResponse>() {
                        @Override
                        public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                            Intent displayContacts = new Intent(getApplicationContext(), DisplayContacts.class);
                            displayContacts.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            displayContacts.putExtra("contacts", (Serializable)response.body().getResponseList());
                            startActivity(displayContacts);
                        }

                        @Override
                        public void onFailure(Call<SearchResponse> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
                        }
                    });

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // use this method for auto complete search process
                    return false;
                }
            });
            SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // TODO : Logout
            LoginActivity.token=null;
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //Toast.makeText(getApplicationContext(), getString(R.string.login_to_continue), Toast.LENGTH_LONG).show();
            startActivity(login);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void doPositiveClick(final String name, final String username) {


        CheckExists checkExistsClient = LoginActivity.retrofit.create(CheckExists.class);

        Call<ExistsResponse> call = checkExistsClient.userExists(username);
        call.enqueue(new Callback<ExistsResponse>() {
            @Override
            public void onResponse(Call<ExistsResponse> call, Response<ExistsResponse> response) {
                if(response.body().getExists()){

                    Integer id=response.body().getId();
                    // username exists add contact to local database
                    SQLiteDatabase db = mDbHelper.getWritableDatabase();
                    Cursor cursor = db.rawQuery("insert into contact(contact_id, name, username) values(" + id + ", " + name + ", " + username +
                            ");", null);

                    cursor.close();
                }
                else{
                    // username does not exist display error
                    Toast.makeText(getApplicationContext(), getString(R.string.user_not_exists), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ExistsResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ContactsFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public ContactsFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ContactsFragment newInstance(int sectionNumber) {
            ContactsFragment fragment = new ContactsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_contacts, container, false);
            return rootView;
        }

        @Override public void onActivityCreated(Bundle savedInstanceState){

            super.onActivityCreated(savedInstanceState);

            SQLiteDatabase db = mDbHelper.getReadableDatabase();

            Cursor cursor = db.rawQuery("select * from contact order by name asc;", null);

            List<Contact> contactList=new ArrayList<>();
            while(cursor.moveToNext()) {
                final String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(Contacts.FeedEntry.name));

                final String username = cursor.getString(
                        cursor.getColumnIndexOrThrow(Contacts.FeedEntry.username));

                contactList.add(new Contact(name, username));
            }



            // get data from the table by the ListAdapter
            CustomAdapter customAdapter = new CustomAdapter(getContext(), R.layout.contact_list_row, contactList);

            mContactList.setAdapter(customAdapter);

            cursor.close();

        }


    }

    public static class ChatsFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public ChatsFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ChatsFragment newInstance(int sectionNumber) {
            ChatsFragment fragment = new ChatsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_chats, container, false);

            return rootView;
        }

        @Override public void onActivityCreated(Bundle savedInstanceState){

            super.onActivityCreated(savedInstanceState);

            SQLiteDatabase db = mDbHelper.getReadableDatabase();

            Set<Integer> messengerIdList=new HashSet<>();
            Cursor cursor = db.rawQuery("select sender_id from message_info group by sender_id;", null);


            while(cursor.moveToNext()) {
                Integer senderId=cursor.getInt(cursor.getColumnIndexOrThrow(Messages.FeedEntry.sender_id));

                messengerIdList.add(senderId);
            }
            cursor.close();

            cursor = db.rawQuery("select receiver_id from message_info group by receiver_id;", null);

            while(cursor.moveToNext()) {
                Integer receiverId=cursor.getInt(cursor.getColumnIndexOrThrow(Messages.FeedEntry.receiver_id));

                messengerIdList.add(receiverId);
            }
            cursor.close();

            List<Contact> chatList=new ArrayList<>();
            for(Integer i : messengerIdList){

                cursor = db.rawQuery("select name, username from contact where id = " + i + ";", null);
                cursor.moveToNext();
                String name=cursor.getString(cursor.getColumnIndexOrThrow(Contacts.FeedEntry.name));
                String username=cursor.getString(cursor.getColumnIndexOrThrow(Contacts.FeedEntry.username));
                chatList.add(new Contact(name, username));
                cursor.close();
            }

            // get data from the table by the ListAdapter
            CustomAdapter customAdapter = new CustomAdapter(getContext(), R.layout.contact_list_row, chatList);


            mChatsList.setAdapter(customAdapter);
            mChatsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String username=((TextView)((RelativeLayout)((TableRow)((TableLayout)view).getChildAt(0)).getChildAt(0)).getChildAt(1)).getText().toString();
                    Intent openChatWindow=new Intent(mContext, ChatWindow.class);
                    openChatWindow.putExtra("username", username);
                    startActivity(openChatWindow);
                }
            });
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch(position){
                case 0:
                    return ContactsFragment.newInstance(position + 1);

                case 1:
                    return ChatsFragment.newInstance(position + 1);

            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "CONTACTS";
                case 1:
                    return "CHATS";

            }
            return null;
        }
    }
}
