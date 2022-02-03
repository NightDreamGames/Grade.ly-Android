package com.NightDreamGames.Grade.ly.Activities;

import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.NightDreamGames.Grade.ly.Calculator.Manager;
import com.NightDreamGames.Grade.ly.Calculator.Period;
import com.NightDreamGames.Grade.ly.Calculator.Subject;
import com.NightDreamGames.Grade.ly.Misc.CustomRecyclerViewAdapter;
import com.NightDreamGames.Grade.ly.Misc.SubjectDialogManager;
import com.NightDreamGames.Grade.ly.R;
import com.NightDreamGames.Grade.ly.databinding.EditSubjectActivityBinding;

import java.util.ArrayList;
import java.util.Objects;

public class CreatorActivity extends AppCompatActivity implements CustomRecyclerViewAdapter.ItemClickListener, SubjectDialogManager.SubjectDialogListener, PopupMenu.OnMenuItemClickListener {

    private EditSubjectActivityBinding binding;
    private int subjectPosition;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = EditSubjectActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(R.string.edit_subjects);

        updateView();
    }

    public void updateView() {
        Manager.sortAll();

        ArrayList<String> a = new ArrayList<>();
        for (int i = 0; i < Manager.periodTemplate.size(); i++)
            a.add(Manager.periodTemplate.get(i).name);

        ArrayList<String> b = new ArrayList<>();
        for (int i = 0; i < Manager.periodTemplate.size(); i++) {
            String x = Manager.format(Manager.periodTemplate.get(i).coefficient);
            if (x.startsWith("0"))
                x = x.substring(1);
            b.add(x);
        }

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CustomRecyclerViewAdapter adapter = new CustomRecyclerViewAdapter(this, a, b, 1);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int subjectPosition) {
        this.subjectPosition = subjectPosition;
        showPopup(view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.creator_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.add) {
            showNoticeDialog(0);
        } else if (id == R.id.sort_az) Manager.writePreference("sort_mode", "0");
        else if (id == R.id.sort_mark) Manager.writePreference("sort_mode", "1");

        updateView();
        return true;
    }

    public void showNoticeDialog(int type) {
        if (type == 0)
            new SubjectDialogManager().show(getSupportFragmentManager(), "SubjectDialogManager");
        else
            new SubjectDialogManager(Manager.periodTemplate.get(subjectPosition).name, Manager.periodTemplate.get(subjectPosition).coefficient).show(getSupportFragmentManager(), "SubjectDialogManager");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String name, double coefficient, int type) {
        if (type == 0) {
            Manager.periodTemplate.add(new Subject(name, coefficient));

            for (Period p : Manager.getCurrentYear().periods) {
                p.subjects.add(new Subject(name, coefficient));
            }

        } else {
            Manager.periodTemplate.get(subjectPosition).name = name;
            Manager.periodTemplate.get(subjectPosition).coefficient = coefficient;

            for (Period p : Manager.getCurrentYear().periods)
                for (int i = 0; i < p.subjects.size(); i++) {
                    p.subjects.get(i).name = Manager.periodTemplate.get(i).name;
                    p.subjects.get(i).coefficient = Manager.periodTemplate.get(i).coefficient;
                }
        }

        Manager.calculate();
        updateView();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.test_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.setGravity(Gravity.END);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.edit) {
            editTest();
            updateView();
            return true;
        } else if (itemId == R.id.delete) {
            Manager.periodTemplate.remove(subjectPosition);

            for (Period p : Manager.getCurrentYear().periods)
                p.subjects.remove(subjectPosition);

            updateView();
            return true;
        }
        return false;
    }

    public void editTest() {
        showNoticeDialog(1);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right);
    }
}