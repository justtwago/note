package com.twago.TaskBook.NoteMain;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.twago.TaskBook.Module.Constants;
import com.twago.TaskBook.NoteList.ListFragment;
import com.twago.TaskBook.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class NoteMainActivity extends AppCompatActivity {
    private static final String TAG = NoteMainActivity.class.getSimpleName();
    private ListFragment noteListFragment;
    private MainContract.UserActionListener mainUserActionListener;
    private Drawer drawer;

    @BindView(R.id.button_create_note)
    FloatingActionButton createNoteButton;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_main);
        setTitle(R.string.tasks);
        Realm.init(this);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        noteListFragment = ListFragment.newInstance();
        getFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_layout, noteListFragment)
                .commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        initSetUp();
        buildMenuButton();
        buildDrawer();
        mainUserActionListener.setupSubscriberActiveTasks();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notes_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_pick_date_action:
                choseDate();
                break;
            default:
                toggleDrawer();
        }
        return true;
    }

    @OnClick(R.id.button_create_note)
    public void onCreateNote(){
        noteListFragment.getPresenter().openNewEditor(Constants.NEW_NOTE_ID);
    }

    private void buildDrawer() {
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        buildActiveTasksDrawerItem(),
                        buildArchiveDrawerItem()
                )
                .withSliderBackgroundColorRes(R.color.dark_blue)
                .withDrawerWidthDp(60)
                .build();
    }

    private PrimaryDrawerItem buildActiveTasksDrawerItem() {
        return new PrimaryDrawerItem()
                .withIcon(R.drawable.ic_date_range_white_36dp)
                .withName(R.string.tasks)
                .withSelectedColorRes(R.color.granate)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        mainUserActionListener.setupSubscriberActiveTasks();
                        setTitle(R.string.tasks);
                        createNoteButton.setVisibility(View.VISIBLE);
                        return false;
                    }
                });
    }

    private PrimaryDrawerItem buildArchiveDrawerItem() {
        return new PrimaryDrawerItem()
                .withIcon(R.drawable.ic_archive_white_36dp)
                .withName(R.string.archive)
                .withSelectedColorRes(R.color.granate)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        mainUserActionListener.setupSubscriberArchive();
                        setTitle(R.string.archive);
                        createNoteButton.setVisibility(View.INVISIBLE);
                        return false;
                    }
                });
    }

    public void toggleDrawer() {
        if (drawer.isDrawerOpen())
            drawer.closeDrawer();
        else
            drawer.openDrawer();
    }

    private void buildMenuButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
    }

    private void initSetUp() {
        mainUserActionListener = new MainPresenter(this, noteListFragment.getPresenter(), Realm.getDefaultInstance());
    }

    private void choseDate() {
        mainUserActionListener.setInfoBarDate();
    }
}