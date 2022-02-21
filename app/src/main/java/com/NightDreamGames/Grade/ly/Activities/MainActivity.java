package com.NightDreamGames.Grade.ly.Activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.NightDreamGames.Grade.ly.Calculator.Calculator;
import com.NightDreamGames.Grade.ly.Calculator.Manager;
import com.NightDreamGames.Grade.ly.Calculator.Term;
import com.NightDreamGames.Grade.ly.Misc.Compatibility;
import com.NightDreamGames.Grade.ly.Misc.CustomRecyclerViewAdapter;
import com.NightDreamGames.Grade.ly.Misc.Preferences;
import com.NightDreamGames.Grade.ly.Misc.Serialization;
import com.NightDreamGames.Grade.ly.R;
import com.NightDreamGames.Grade.ly.databinding.MainSubjectActivityBinding;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements CustomRecyclerViewAdapter.ItemClickListener {

    protected static final String EXTRA_MESSAGE = "com.NightDreamGames.Grade.ly.SUBJECT";
    public static Application sApplication;
    public static String sDefSystemLanguage;
    private MainSubjectActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sApplication = getApplication();

        Compatibility.periodPreferences();

        switch (Preferences.getPreference("dark_theme", "auto")) {
            case "on":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "off":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            default:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                break;
        }

        Manager.init();

        Serialization.Deserialize();

        if (Boolean.parseBoolean(Preferences.getPreference("isFirstRun", "true"))) {
            startActivity(new Intent(MainActivity.this, SetupActivity.class));
        }

        Compatibility.init();

        Manager.calculate();
    }

    @Override
    protected void onResume() {
        super.onResume();

        adaptView();
        updateView();
    }

    protected void adaptView() {
        setTitle();

        binding = MainSubjectActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.fab.setVisibility(View.GONE);
        binding.bonus.setVisibility(View.GONE);

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
    }

    protected void updateView() {
        Manager.sortAll();

        Term p = Manager.getCurrentTerm();

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        CustomRecyclerViewAdapter adapter = new CustomRecyclerViewAdapter(this, p.getSubjects(), p.getGrades(), 0);

        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        binding.textView3.setText((p.result == -1) ? "-" : Calculator.format(p.result));
    }

    protected void setTitle() {
        switch (Manager.currentTerm) {
            case 0:
                if (Preferences.getPreference("term", "term_trimester").equals("term_semester"))
                    setTitle(R.string.semester_1);
                else if (Preferences.getPreference("term", "term_trimester").equals("term_trimester"))
                    setTitle(R.string.trimester_1);
                else if (Preferences.getPreference("term", "term_trimester").equals("term_year"))
                    setTitle(R.string.year);
                break;
            case 1:
                if (Preferences.getPreference("term", "term_trimester").equals("term_semester"))
                    setTitle(R.string.semester_2);
                else if (Preferences.getPreference("term", "term_trimester").equals("term_trimester"))
                    setTitle(R.string.trimester_2);
                break;
            case 2:
                setTitle(R.string.trimester_3);
                break;
            case -1:
                setTitle(R.string.year);
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, SubjectActivity.class);

        intent.putExtra(EXTRA_MESSAGE, position);
        startActivity(intent);
        overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (Preferences.getPreference("term", "term_trimester")) {
            case "term_trimester":
                menu.findItem(R.id.trimester_1).setVisible(true);
                menu.findItem(R.id.trimester_2).setVisible(true);
                menu.findItem(R.id.trimester_3).setVisible(true);

                menu.findItem(R.id.semester_1).setVisible(false);
                menu.findItem(R.id.semester_2).setVisible(false);
                break;
            case "term_semester":
                menu.findItem(R.id.semester_1).setVisible(true);
                menu.findItem(R.id.semester_2).setVisible(true);

                menu.findItem(R.id.trimester_1).setVisible(false);
                menu.findItem(R.id.trimester_2).setVisible(false);
                menu.findItem(R.id.trimester_3).setVisible(false);
                break;
            case "term_year":
                menu.findItem(R.id.term_selector).setVisible(false);
                break;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sort_az) {
            Preferences.setPreference("sort_mode", "0");
            Manager.sortAll();
            updateView();
            return true;
        } else if (id == R.id.sort_grade) {
            Preferences.setPreference("sort_mode", "1");
            Manager.sortAll();
            updateView();
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            //overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            return true;
        } else if (id == R.id.semester_1 || id == R.id.trimester_1) {
            Manager.currentTerm = 0;
            Preferences.setPreference("current_term", String.valueOf(Manager.currentTerm));
            adaptView();
            updateView();
            return true;
        } else if (id == R.id.semester_2 || id == R.id.trimester_2) {
            Manager.currentTerm = 1;
            Preferences.setPreference("current_term", String.valueOf(Manager.currentTerm));
            adaptView();
            updateView();
            return true;
        } else if (id == R.id.trimester_3) {
            Manager.currentTerm = 2;
            Preferences.setPreference("current_term", String.valueOf(Manager.currentTerm));
            adaptView();
            updateView();
            return true;
        } else if (id == R.id.year) {
            if (Preferences.getPreference("term", "term_trimester").equals("term_year"))
                Manager.currentTerm = 0;
            else Manager.currentTerm = -1;
            Preferences.setPreference("current_term", String.valueOf(Manager.currentTerm));
            adaptView();
            updateView();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(updateBaseContextLocale(base));
    }

    @SuppressLint("ObsoleteSdkInt")
    private Context updateBaseContextLocale(Context context) {
        sDefSystemLanguage = Locale.getDefault().getLanguage();

        String language = String.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString("language", "default"));
        if (language.equals("default"))
            language = sDefSystemLanguage;

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResourcesLocale(context, locale);
        }

        return updateResourcesLocaleLegacy(context, locale);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        sDefSystemLanguage = newConfig.getLocales().get(0).getLanguage();
    }

    @TargetApi(Build.VERSION_CODES.N)
    private Context updateResourcesLocale(Context context, Locale locale) {
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private Context updateResourcesLocaleLegacy(Context context, Locale locale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }
}