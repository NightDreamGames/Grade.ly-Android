package com.NightDreamGames.Grade.ly.Activities;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.NightDreamGames.Grade.ly.Calculator.Manager;
import com.NightDreamGames.Grade.ly.Calculator.Subject;
import com.NightDreamGames.Grade.ly.Misc.CustomRecyclerViewAdapter;
import com.NightDreamGames.Grade.ly.Misc.TestDialogManager;
import com.NightDreamGames.Grade.ly.R;
import com.NightDreamGames.Grade.ly.databinding.MainSubjectActivityBinding;

import java.util.Objects;

public class SubjectActivity extends AppCompatActivity implements CustomRecyclerViewAdapter.ItemClickListener, TestDialogManager.TestDialogListener, PopupMenu.OnMenuItemClickListener {

    private MainSubjectActivityBinding binding;
    private Subject subject;
    private int testPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        int position = intent.getIntExtra(MainActivity.EXTRA_MESSAGE, 0);
        subject = Manager.getCurrentPeriod().subjects.get(position);

        adaptView();
        updateView();
    }

    protected void adaptView() {
        binding = MainSubjectActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(subject.name);

        binding.fab.setOnClickListener(v -> showNoticeDialog(0));

        if (Manager.currentPeriod != -1) {
            binding.average.setVisibility(View.GONE);
            binding.bonus.setVisibility(View.VISIBLE);

            binding.appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
                binding.constraintLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.textView3.getLayoutParams();
                double range = appBarLayout.getTotalScrollRange() / 2.75;
                int margin = (verticalOffset <= -range) ? getResources().getDimensionPixelSize(R.dimen.collapsed) : getResources().getDimensionPixelSize(R.dimen.expanded);
                params.setMarginEnd(margin);

                binding.textView3.setLayoutParams(params);
                binding.textView3.requestLayout();
            });

            binding.button.setOnClickListener(v -> {
                subject.changeBonus(-1);
                updateView();
            });
            binding.button2.setOnClickListener(v -> {
                subject.changeBonus(1);
                updateView();
            });
        } else {
            binding.fab.setVisibility(View.GONE);
            binding.fab.setVisibility(View.GONE);
            binding.bonus.setVisibility(View.GONE);
        }
    }

    protected void updateView() {
        Manager.sortAll();

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CustomRecyclerViewAdapter adapter = new CustomRecyclerViewAdapter(this, subject.getNames(), subject.getMarks(), 1);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        binding.textView3.setText((subject.result == -1) ? "-" : Manager.format(subject.result));
        binding.bonusText.setText(String.valueOf(subject.bonus));
    }

    @Override
    public void onItemClick(View view, int testPosition) {
        this.testPosition = testPosition;
        showPopup(view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.subject_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.sort_az) {
            Manager.writePreference("sort_mode2", "0");
            Manager.sortAll();
            updateView();
            return true;
        } else if (id == R.id.sort_mark) {
            Manager.writePreference("sort_mode2", "1");
            Manager.sortAll();
            updateView();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showNoticeDialog(int type) {
        if (type == 0)
            new TestDialogManager(subject).show(getSupportFragmentManager(), "TestDialogManager");
        else
            new TestDialogManager(subject.tests.get(testPosition).mark1, subject.tests.get(testPosition).mark2, subject.tests.get(testPosition).name, subject).show(getSupportFragmentManager(), "TestDialogManager");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, double mark1, double mark2, String name, int type) {
        if (type == 0) {
            subject.addTest(mark1, mark2, name);
        } else {
            subject.editTest(testPosition, mark1, mark2, name);
        }
        Manager.sortAll();
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
            subject.removeTest(testPosition);
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