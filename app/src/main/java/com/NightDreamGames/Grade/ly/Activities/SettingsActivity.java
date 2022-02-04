package com.NightDreamGames.Grade.ly.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.NightDreamGames.Grade.ly.Calculator.Manager;
import com.NightDreamGames.Grade.ly.Calculator.Period;
import com.NightDreamGames.Grade.ly.R;
import com.NightDreamGames.Grade.ly.databinding.SettingsActivityBinding;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.settings);

        com.NightDreamGames.Grade.ly.databinding.SettingsActivityBinding binding = SettingsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        binding.fab.setVisibility(View.GONE);
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
        private boolean resultValue;

        @Override
        @SuppressWarnings("ConstantConditions")
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            //TODO add ripple effect
            setPreferencesFromResource(R.xml.preferences, rootKey);

            androidx.preference.Preference es = findPreference("edit_subjects");
            androidx.preference.Preference period = findPreference("period");
            androidx.preference.Preference tMark = findPreference("total_marks");
            androidx.preference.EditTextPreference cMark = findPreference("custom_mark");
            androidx.preference.Preference reset = findPreference("reset");
            androidx.preference.Preference dark = findPreference("dark_theme");
            androidx.preference.Preference language = findPreference("language");
            androidx.preference.Preference version = findPreference("version");
            androidx.preference.Preference github = findPreference("github");
            androidx.preference.Preference contact = findPreference("contact");

            es.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(getContext(), CreatorActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
                return true;
            });

            period.setOnPreferenceChangeListener((preference, newValue) -> {
                int k = 0;
                switch ((String) newValue) {
                    case "period_trimester":
                        k = 3;
                        break;
                    case "period_semester":
                        k = 2;
                        break;
                    case "period_year":
                        k = 1;
                        break;
                }

                while (Manager.getCurrentYear().periods.size() > k)
                    Manager.getCurrentYear().periods.remove(Manager.getCurrentYear().periods.size() - 1);

                while (Manager.getCurrentYear().periods.size() < k)
                    Manager.getCurrentYear().periods.add(new Period());

                Manager.currentPeriod = 0;
                Manager.calculate();

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

            reset.setOnPreferenceClickListener(preference -> confirmChange());

            dark.setOnPreferenceChangeListener((preference, newVal) -> {
                final String value = (String) newVal;

                switch (value) {
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
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                        }
                        break;
                }
                return true;
            });

            language.setOnPreferenceChangeListener((preference, newValue) -> {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
                return true;
            });


            try {
                version.setSummary(getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            github.setOnPreferenceClickListener(preference -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/NightDreamGames/Grade.ly"));
                startActivity(browserIntent);
                return true;
            });

            contact.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, "contact.nightdreamgames@gmail.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Grade.ly feedback");
                //if (intent.resolveActivity(MainActivity.sApplication.getPackageManager()) != null) {
                startActivity(intent);
                //}
                return true;
            });

            SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(requireContext());
            preference.registerOnSharedPreferenceChangeListener(listener);
        }

        private boolean confirmChange() {
            final Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    throw new RuntimeException();
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setTitle(R.string.confirm)
                    .setMessage(R.string.confirm_delete)
                    .setCancelable(true)
                    .setPositiveButton(getString(android.R.string.ok), (dialog, id) -> {
                        resultValue = true;
                        handler.sendMessage(handler.obtainMessage());
                        Manager.clear();
                        Manager.sortAll();
                    })
                    .setNegativeButton(getString(android.R.string.cancel), (dialog, id) -> {
                        resultValue = false;
                        handler.sendMessage(handler.obtainMessage());
                    })
                    .setOnCancelListener(dialog -> {
                        resultValue = false;
                        handler.sendMessage(handler.obtainMessage());
                    })
                    .show();

            try {
                Looper.loop();
            } catch (RuntimeException ignored) {
            }

            return resultValue;
        }
    }
}