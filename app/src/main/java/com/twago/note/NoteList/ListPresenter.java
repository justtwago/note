package com.twago.note.NoteList;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;

import com.twago.note.Constants;
import com.twago.note.Note;
import com.twago.note.NoteEditor.EditorFragment;
import com.twago.note.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.functions.Action1;

public class ListPresenter implements ListContract.UserActionListener {
    private ListContract.View noteListFragmentView;
    private Activity activity;
    private Realm realm;
    private long currentNoteDate = Calendar.getInstance().getTimeInMillis();

    ListPresenter(Activity activity, final ListContract.View noteListFragmentView) {
        this.noteListFragmentView = noteListFragmentView;
        this.realm = Realm.getDefaultInstance();
        this.activity = activity;
    }

    private void updateRecyclerView(RealmResults<Note> notes) {
        ListAdapter recyclerViewAdapter = noteListFragmentView.getRecyclerViewAdapter();
        recyclerViewAdapter.setData(notes);
        recyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void initialization() {
        inflateView();
        setObserver();
    }

    private void inflateView() {
        inflateRecyclerView();
        inflateInfoBar();
    }

    private void inflateRecyclerView() {
        noteListFragmentView.setAdapterOnRecyclerView(new ListAdapter(this));
    }

    private void inflateInfoBar() {
        setCurrentDate();
    }

    private void setObserver() {
        realm.where(Note.class)
                .findAllSorted(Note.DATE)
                .asObservable()
                .subscribe(new Action1<RealmResults<Note>>() {
                    @Override
                    public void call(RealmResults<Note> notes) {
                        updateRecyclerView(notes);
                    }
                });
    }

    private void setCurrentDate() {
        noteListFragmentView.setDateInInfoBar(getFormatedDay(), getFormatedMonth());
    }

    @Override
    public void openNewEditor(int id) {
        FragmentTransaction fragmentTransaction = activity.getFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        DialogFragment newFragment = EditorFragment.newInstance(id);
        newFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
        newFragment.show(fragmentTransaction, "");
    }

    @Override
    public int getTaskIcon(Note note) {
        switch (note.getTask()) {
            case Constants.MAIN_TASK:
                return R.drawable.ic_star_indigo_500_24dp;
            case Constants.PART_TASK:
                return R.drawable.ic_star_half_indigo_500_24dp;
            case Constants.SKILLS_TASK:
                return R.drawable.ic_lightbulb_outline_indigo_500_24dp;
            case Constants.UNIMPORTANT_TASK:
                return R.drawable.ic_help_outline_indigo_500_24dp;
        }
        return 0;
    }

    @Override
    public void deleteNote(final int id) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Note.class).equalTo(Note.ID, id).findAll().deleteAllFromRealm();
            }
        });
    }

    @Override
    public String getFormatedDate(Note note) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE dd MMM yyyy", Locale.getDefault());
        return simpleDateFormat.format(new Date(note.getDate()));
    }

    private String getFormatedDay() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd", Locale.getDefault());
        return simpleDateFormat.format(new Date(currentNoteDate));
    }

    private String getFormatedMonth() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
        return simpleDateFormat.format(new Date(currentNoteDate));
    }
}
