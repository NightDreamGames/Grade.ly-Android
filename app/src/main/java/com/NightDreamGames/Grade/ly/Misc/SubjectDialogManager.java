package com.NightDreamGames.Grade.ly.Misc;

import android.app.Dialog;
import android.content.Context;
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

import com.NightDreamGames.Grade.ly.Calculator.Manager;
import com.NightDreamGames.Grade.ly.Calculator.Subject;
import com.NightDreamGames.Grade.ly.R;

import java.util.Objects;

public class SubjectDialogManager extends DialogFragment {

    private final int type;
    SubjectDialogListener listener;
    private double coefficient = -1;
    private String name = "";
    private String defaultName;

    public SubjectDialogManager() {
        this.type = 0;
    }

    public SubjectDialogManager(String name, double coefficient) {
        this.type = 1;
        this.coefficient = coefficient;
        this.name = name;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the TestDialogListener so we can send events to the host
            listener = (SubjectDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(this.toString() + " must implement TestDialogListener");
        }
    }

    private void positiveClick(EditText editText0, EditText editText2) {
        if (editText0.getText().toString().isEmpty())
            name = defaultName;
        else
            name = editText0.getText().toString();

        if (editText2.getText().toString().isEmpty())
            coefficient = 1;
        else
            coefficient = Double.parseDouble(editText2.getText().toString());

        listener.onDialogPositiveClick(SubjectDialogManager.this, name, coefficient, type);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        View dialogView = getLayoutInflater().inflate(R.layout.subject_dialog, null);
        EditText editText0 = dialogView.findViewById(R.id.nameEditTextSubject);
        EditText editText2 = dialogView.findViewById(R.id.coefficientEditText);

        if (!name.isEmpty())
            editText0.setText(name);
        if (coefficient != -1) {
            String a = Manager.format(coefficient);
            if (a.startsWith("0"))
                a = a.substring(1);
            editText2.setText(a);
        }

        editText0.selectAll();
        editText2.selectAll();

        boolean a = false;
        int i = 1;

        do {
            defaultName = getString(R.string.subject) + " " + (Manager.periodTemplate.size() + i);

            for (Subject s : Manager.periodTemplate) {
                if (s.name.equals(defaultName)) {
                    a = true;
                    i++;
                    break;
                } else
                    a = false;
            }
        }
        while (a);

        editText0.setHint(defaultName);
        editText0.requestFocus();

        editText2.setOnEditorActionListener((v, actionId, event) -> {
            if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                positiveClick(editText0, editText2);
                Objects.requireNonNull(getDialog()).dismiss();
                return true;
            }
            return false;
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        if (type == 0)
            builder.setTitle(getString(R.string.add_subject));
        else
            builder.setTitle(getString(R.string.edit_subject));

        builder.setView(dialogView)
                .setPositiveButton(getString(android.R.string.ok), (dialog, id) -> positiveClick(editText0, editText2))
                .setNegativeButton(getString(android.R.string.cancel), (dialog, id) -> listener.onDialogNegativeClick(SubjectDialogManager.this));

        AlertDialog u = builder.create();
        u.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return u;
    }

    public interface SubjectDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, String name, double coefficient, int type);

        void onDialogNegativeClick(DialogFragment dialog);
    }
}