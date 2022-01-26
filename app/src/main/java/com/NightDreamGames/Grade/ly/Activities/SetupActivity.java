package com.NightDreamGames.Grade.ly.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.NightDreamGames.Grade.ly.Calculator.Manager;
import com.NightDreamGames.Grade.ly.Misc.ExcelParser;
import com.NightDreamGames.Grade.ly.R;
import com.NightDreamGames.Grade.ly.databinding.SettingsActivityBinding;

import java.io.IOException;

public class SetupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.setup);

        com.NightDreamGames.Grade.ly.databinding.SettingsActivityBinding binding = SettingsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        setSupportActionBar(binding.toolbar);
        binding.fab.setOnClickListener(view ->
        {
            String variant = Manager.getPreference("variant", "basic");
            ExcelParser.fillSubjects(Manager.getPreference("class", "7CI"), variant.equals("latin"), variant.equals("chinese"));
            Manager.writePreference("isFirstRun", "false");
            startActivity(new Intent(SetupActivity.this, MainActivity.class));
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            super.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        SharedPreferences.OnSharedPreferenceChangeListener listener = (prefs, key) -> {
            Manager.readPreferences();
            Manager.calculate();
        };

        @Override
        @SuppressWarnings("ConstantConditions")
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            //TODO add ripple effect
            setPreferencesFromResource(R.xml.setup_preferences, rootKey);

            androidx.preference.Preference system = findPreference("school_system");
            androidx.preference.PreferenceCategory lux = findPreference("lux_system");
            androidx.preference.PreferenceCategory other = findPreference("other_system");
            androidx.preference.ListPreference classPreference = findPreference("class");
            androidx.preference.ListPreference variant = findPreference("variant");
            androidx.preference.Preference es = findPreference("edit_subjects");
            androidx.preference.Preference tMark = findPreference("total_marks");
            androidx.preference.EditTextPreference cMark = findPreference("custom_mark");

            lux.setVisible(false);
            other.setVisible(false);

            system.setOnPreferenceChangeListener((preference, newValue) -> {
                if (newValue.equals("lux")) {
                    lux.setVisible(true);
                    other.setVisible(false);
                } else {
                    lux.setVisible(false);
                    other.setVisible(true);
                }

                return true;
            });

            try {
                classPreference.setEntries(ExcelParser.Init());
                classPreference.setEntryValues(ExcelParser.Init());
            } catch (IOException e) {
                e.printStackTrace();
            }
            classPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                variant.setVisible(!newValue.equals("7C"));
                return true;
            });

            es.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(getContext(), CreatorActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
                return true;
            });

            tMark.setOnPreferenceChangeListener((preference, newValue) -> {
                cMark.setVisible(newValue.equals("-1"));
                return true;
            });
            cMark.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.selectAll();
                int maxLength = 6;
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
            });
            cMark.setVisible(Manager.getPreference("total_marks", "60").equals("-1"));

            variant.setVisible(false);

            SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(requireContext());
            preference.registerOnSharedPreferenceChangeListener(listener);
        }
    }
}