package com.vansuita.stockexplorer.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioGroup;

import com.vansuita.stockexplorer.R;

/**
 * Created by jrvansuita on 16/10/17.
 */

public class InputDialog {

    Context context;
    String hint;
    String title;
    String positiveLabel;
    String def;
    IOnProductSearch listener;

    View view;
    EditText edInput;

    NumberPicker npInput;
    RadioGroup rgRadio;

    int inputType = InputType.TYPE_CLASS_TEXT;

    InputDialog(Context context, IOnProductSearch listener) {
        this.context = context;
        this.listener = listener;
    }

    public static InputDialog build(Context context, IOnProductSearch listener) {
        return new InputDialog(context, listener);

    }

    public InputDialog setHint(String hint) {
        this.hint = hint;
        return this;
    }

    public InputDialog setHint(int hint) {
        this.hint = context.getString(hint);
        return this;
    }

    public InputDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public InputDialog setTitle(int title) {
        this.title = context.getString(title);
        return this;
    }

    public InputDialog setPositiveLabel(int title) {
        this.positiveLabel = context.getString(title);
        return this;
    }

    public InputDialog setDefault(String def) {
        this.def = def;
        return this;
    }

    public InputDialog setDefault(int def) {
        this.def = context.getString(def);
        return this;
    }

    public InputDialog setInputType(int e) {
        this.inputType = e;
        return this;
    }

    private View getView() {

        view = LayoutInflater.from(context).inflate(R.layout.input_dialog, null, false);

        switch (inputType) {

            case InputType.TYPE_CLASS_NUMBER:
                view.findViewById(R.id.text_hint).setVisibility(View.GONE);

                npInput = (NumberPicker) view.findViewById(R.id.number_input);
                rgRadio = (RadioGroup) view.findViewById(R.id.radio);
                npInput.setMaxValue(1000);
                npInput.setMinValue(1);
                int val = Math.abs(Integer.parseInt(def));
                npInput.setValue(val == 0 ? 1 : val);
                break;

            case InputType.TYPE_CLASS_TEXT:
                view.findViewById(R.id.number_holder).setVisibility(View.GONE);

                edInput = (EditText) view.findViewById(R.id.text_input);
                edInput.setText(def);
                edInput.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
                edInput.selectAll();
                edInput.requestFocus();

                TextInputLayout l = (TextInputLayout) view.findViewById(R.id.text_hint);
                l.setHint(hint);

                break;
            case InputType.TYPE_NUMBER_FLAG_DECIMAL:
                view.findViewById(R.id.number_holder).setVisibility(View.GONE);

                edInput = (EditText) view.findViewById(R.id.text_input);
                edInput.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                edInput.setText(def);
                edInput.selectAll();
                edInput.requestFocus();
                break;
        }

        showKeyboard();

        return view;
    }


    public void show() {

        if (positiveLabel == null) {
            positiveLabel = context.getString(android.R.string.ok);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(getView())
                .setPositiveButton(positiveLabel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        hideKeyboard();
                        dialog.dismiss();
                        if (listener != null) {
                            if (edInput != null && !edInput.getText().toString().isEmpty()) {
                                listener.onSearch(edInput.getText().toString());
                            } else if (npInput != null && npInput.getValue() != 0) {
                                npInput.clearFocus();
                                int value = npInput.getValue();

                                if (rgRadio.getCheckedRadioButtonId() == R.id.dec) {
                                    value = -value;
                                }

                                listener.onSearch(String.valueOf(value));
                            }
                        }
                    }
                });


        builder .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                hideKeyboard();
                dialog.dismiss();
            }
        });

        builder.show();

    }


    public interface IOnProductSearch {
        void onSearch(String value);
    }


    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void hideKeyboard() {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInputFromWindow(view.getWindowToken(), 0, 0);
        }
    }
}
