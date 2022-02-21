package com.NightDreamGames.Grade.ly.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.NightDreamGames.Grade.ly.Calculator.Manager;
import com.NightDreamGames.Grade.ly.Misc.Compatibility;
import com.NightDreamGames.Grade.ly.Misc.ExcelParser;
import com.NightDreamGames.Grade.ly.Misc.Preferences;
import com.NightDreamGames.Grade.ly.R;
import com.NightDreamGames.Grade.ly.databinding.SettingsActivityBinding;

import java.io.IOException;

public class SetupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.NightDreamGames.Grade.ly.databinding.SettingsActivityBinding binding = SettingsActivityBinding.inflate(getLayoutInflater());

        setTitle(R.string.setup);

        setContentView(binding.getRoot());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment(binding))
                .commit();

        setSupportActionBar(binding.toolbar);

        binding.fab.setVisibility(View.INVISIBLE);
        binding.fab.setOnClickListener(view ->
        {
            if (Preferences.getPreference("school_system", "").equals("lux")) {
                Preferences.setPreference("term", "term_semester");
                Preferences.setPreference("total_grades", "60");
                Preferences.setPreference("rounding_mode", "rounding_up");
                Preferences.setPreference("round_to", "1");

                String variant = Preferences.getPreference("variant", "basic");
                ExcelParser.fillSubjects(Preferences.getPreference("class", "7CI"), variant.equals("latin"), variant.equals("chinese"));
            }
            Preferences.setPreference("isFirstRun", "false");

            finish();
        });

        Preferences.deletePreference("school_system");
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
        private final SettingsActivityBinding binding;

        public SettingsFragment(SettingsActivityBinding binding) {
            this.binding = binding;
        }

        final SharedPreferences.OnSharedPreferenceChangeListener listener = (prefs, key) -> {
            Manager.interpretPreferences();
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
            androidx.preference.Preference term = findPreference("term");
            androidx.preference.Preference tGrade = findPreference("total_grades");
            androidx.preference.EditTextPreference cGrade = findPreference("custom_grade");

            lux.setVisible(false);
            other.setVisible(false);

            system.setOnPreferenceChangeListener((preference, newValue) -> {
                if (newValue.equals("lux")) {
                    lux.setVisible(true);
                    other.setVisible(false);
                    if (Preferences.getPreference("class", "Not set").equals("Not set"))
                        binding.fab.setVisibility(View.INVISIBLE);
                    else
                        binding.fab.setVisibility(View.VISIBLE);

                } else {
                    lux.setVisible(false);
                    other.setVisible(true);
                    binding.fab.setVisibility(View.VISIBLE);
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
                if (newValue.toString().startsWith("1C")) {
                    variant.setEntries(R.array.variant_entries_short);
                    variant.setEntryValues(R.array.variant_values_short);
                } else {
                    variant.setEntries(R.array.variant_entries);
                    variant.setEntryValues(R.array.variant_values);
                }
                binding.fab.setVisibility(View.VISIBLE);
                return true;
            });

            es.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(getContext(), CreatorActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
                return true;
            });

            term.setOnPreferenceChangeListener((preference, newValue) -> {
                Compatibility.termCount((String) newValue);
                return true;
            });

            tGrade.setOnPreferenceChangeListener((preference, newValue) -> {
                cGrade.setVisible(newValue.equals("-1"));
                return true;
            });
            cGrade.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.selectAll();
                int maxLength = 6;
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
            });
            cGrade.setVisible(Preferences.getPreference("total_grades", "60").equals("-1"));

            variant.setVisible(false);

            SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(requireContext());
            preference.registerOnSharedPreferenceChangeListener(listener);
        }
    }
}