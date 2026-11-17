package com.example.app.ui;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.app.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ConfirmDeleteDialogFragment extends DialogFragment {

    public static final String TAG = "ConfirmDeleteDialogFragment";

    public static final String REQUEST_KEY_DELETE = "request_key_delete";

    public static final String ARG_REQUEST_KEY = "arg_request_key";
    public static final String ARG_TITLE = "arg_title";
    public static final String ARG_MESSAGE = "arg_message";
    public static final String ARG_ITEM_TYPE = "arg_item_type";
    public static final String ARG_ITEM_ID = "arg_item_id";

    public static final String RESULT_KEY_CONFIRMED = "result_key_confirmed";
    public static final String RESULT_KEY_ITEM_TYPE = "result_key_item_type";
    public static final String RESULT_KEY_ITEM_ID = "result_key_item_id";

    public static final String TYPE_DO_AN = "DO_AN";
    public static final String TYPE_NHIEM_VU = "NHIEM_VU";
    public static final String TYPE_CUOC_HEN = "CUOC_HEN";

    public static ConfirmDeleteDialogFragment newInstance(
            String requestKey,
            String title,
            String message,
            String itemType,
            long itemId
    ) {
        ConfirmDeleteDialogFragment fragment = new ConfirmDeleteDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REQUEST_KEY, requestKey != null ? requestKey : REQUEST_KEY_DELETE);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_ITEM_TYPE, itemType);
        args.putLong(ARG_ITEM_ID, itemId);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        String title = args != null ? args.getString(ARG_TITLE, "") : "";
        String message = args != null ? args.getString(ARG_MESSAGE, "") : "";
        String requestKey = args != null ? args.getString(ARG_REQUEST_KEY, REQUEST_KEY_DELETE) : REQUEST_KEY_DELETE;
        String itemType = args != null ? args.getString(ARG_ITEM_TYPE, "") : "";
        long itemId = args != null ? args.getLong(ARG_ITEM_ID, -1L) : -1L;

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    Bundle result = new Bundle();
                    result.putBoolean(RESULT_KEY_CONFIRMED, true);
                    result.putString(RESULT_KEY_ITEM_TYPE, itemType);
                    result.putLong(RESULT_KEY_ITEM_ID, itemId);
                    getParentFragmentManager().setFragmentResult(requestKey, result);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    Bundle result = new Bundle();
                    result.putBoolean(RESULT_KEY_CONFIRMED, false);
                    result.putString(RESULT_KEY_ITEM_TYPE, itemType);
                    result.putLong(RESULT_KEY_ITEM_ID, itemId);
                    getParentFragmentManager().setFragmentResult(requestKey, result);
                })
                .create();
    }
}
