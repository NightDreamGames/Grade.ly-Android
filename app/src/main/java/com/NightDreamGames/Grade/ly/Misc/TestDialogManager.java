package com.NightDreamGames.Grade.ly.Misc;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.NightDreamGames.Grade.ly.Calculator.Calculator;
import com.NightDreamGames.Grade.ly.Calculator.Manager;
import com.NightDreamGames.Grade.ly.Calculator.Subject;
import com.NightDreamGames.Grade.ly.Calculator.Test;
import com.NightDreamGames.Grade.ly.R;

import java.util.Objects;

public class TestDialogManager extends DialogFragment {

    private final int type;
    private final Subject subject;
    TestDialogListener listener;
    private double grade1 = -1;
    private double grade2 = -1;
    private String name = "";
    private String defaultName;

    public TestDialogManager(Subject subject) {
        this.type = 0;
        this.subject = subject;
    }

    public TestDialogManager(double grade1, double grade2, String name, Subject subject) {
        this.type = 1;
        this.grade1 = grade1;
        this.grade2 = grade2;
        this.name = name;
        this.subject = subject;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the TestDialogListener so we can send events to the host
            listener = (TestDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(this + " must implement TestDialogListener");
        }
    }

    private void positiveClick(EditText editText0, EditText editText1, EditText editText2) {
        if (editText0.getText().toString().isEmpty())
            name = defaultName;
        else
            name = editText0.getText().toString();

        if (editText1.getText().toString().isEmpty())
            grade1 = 1;
        else
            grade1 = Double.parseDouble(editText1.getText().toString());

        if (editText2.getText().toString().isEmpty())
            grade2 = Manager.totalGrades;
        else
            grade2 = Double.parseDouble(editText2.getText().toString());

        listener.onDialogPositiveClick(TestDialogManager.this, grade1, grade2, name, type);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        View dialogView = getLayoutInflater().inflate(R.layout.test_dialog, null);
        EditText editText0 = dialogView.findViewById(R.id.nameEditText);
        EditText editText1 = dialogView.findViewById(R.id.grade1EditText);
        EditText editText2 = dialogView.findViewById(R.id.grade2EditText);

        if (!name.isEmpty())
            editText0.setText(name);
        if (grade1 != -1)
            editText1.setText(Calculator.format(grade1));
        if (grade2 != -1)
            editText2.setText(Calculator.format(grade2));

        editText0.selectAll();
        editText1.selectAll();
        editText2.selectAll();

        boolean a = false;
        int i = 1;

        do {
            defaultName = getString(R.string.test) + " " + (subject.tests.size() + i);

            for (Test t : subject.tests) {
                if (t.name.equals(defaultName)) {
                    a = true;
                    i++;
                    break;
                } else
                    a = false;
            }
        }
        while (a);

        editText0.setHint(defaultName);
        editText2.setHint(String.valueOf(Manager.totalGrades));

        editText2.setOnEditorActionListener((v, actionId, event) -> {
            if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    || actionId == EditorInfo.IME_ACTION_DONE) {
                positiveClick(editText0, editText1, editText2);
                Objects.requireNonNull(getDialog()).dismiss();
                return true;
            }
            return false;
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        if (type == 0)
            builder.setTitle(getString(R.string.add_test));
        else
            builder.setTitle(getString(R.string.edit_test));

        builder.setView(dialogView)
                .setPositiveButton(getString(android.R.string.ok), (dialog, id) -> positiveClick(editText0, editText1, editText2))
                .setNegativeButton(getString(android.R.string.cancel), (dialog, id) -> listener.onDialogNegativeClick(TestDialogManager.this));

        AlertDialog u = builder.create();
        u.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            u.getWindow().setBackgroundDrawableResource(R.color.blue_grey_950);
        }

        return u;
    }

    public interface TestDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, double grade1, double grade2, String name, int type);

        @SuppressWarnings("EmptyMethod")
        void onDialogNegativeClick(DialogFragment dialog);
    }
}

