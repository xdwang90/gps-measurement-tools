/*
 * Copyright (C) 2016 The Android Open Source Project
 *定义显示观测值的Fragment碎片
 */

package com.google.android.apps.location.gps.gnsslogger;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**  The UI fragment that hosts a logging view. */
public class LoggerFragment extends Fragment {

    private TextView mLogView;
    private ScrollView mScrollView;
    private FileLogger mFileLogger;
    private UiLogger mUiLogger;

    private final UIFragmentComponent mUiComponent = new UIFragmentComponent();

    public void setUILogger(UiLogger value) {
        mUiLogger = value;
    }

    public void setFileLogger(FileLogger value) {
        mFileLogger = value;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View newView = inflater.inflate(R.layout.fragment_log, container, false /* attachToRoot */);
        mLogView = (TextView) newView.findViewById(R.id.log_view);
        mScrollView = (ScrollView) newView.findViewById(R.id.log_scroll);

        UiLogger currentUiLogger = mUiLogger;
        if (currentUiLogger != null) {
            currentUiLogger.setUiFragmentComponent(mUiComponent);
        }
        FileLogger currentFileLogger = mFileLogger;
        if (currentFileLogger != null) {
            currentFileLogger.setUiComponent(mUiComponent);
        }

        Button start = (Button) newView.findViewById(R.id.start_log);
        start.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mScrollView.fullScroll(View.FOCUS_UP);
                    }
                });

        Button end = (Button) newView.findViewById(R.id.end_log);
        end.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mScrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });

        Button clear = (Button) newView.findViewById(R.id.clear_log);
        clear.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mLogView.setText("");
                    }
                });

        final Button startLog = (Button) newView.findViewById(R.id.start_logs);
        final Button sendFile = (Button) newView.findViewById(R.id.send_file);

        startLog.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startLog.setEnabled(false);
                        sendFile.setEnabled(true);
                        Toast.makeText(getContext(), "Starting log...", Toast.LENGTH_LONG).show();
                        mFileLogger.startNewLog();
                    }
                });

        sendFile.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startLog.setEnabled(true);
                        sendFile.setEnabled(false);
                        Toast.makeText(getContext(), "Sending file...", Toast.LENGTH_LONG).show();
                        mFileLogger.send();
                    }
                });

        return newView;
    }

    /**
     * A facade for UI and Activity related operations that are required for {@link GnssListener}s.
     */
    public class UIFragmentComponent {

        private static final int MAX_LENGTH = 12000;
        private static final int LOWER_THRESHOLD = (int) (MAX_LENGTH * 0.5);

        public synchronized void logTextFragment(final String tag, final String text, int color) {
            final SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(tag).append(" | ").append(text).append("\n");
            builder.setSpan(
                    new ForegroundColorSpan(color),
                    0 /* start */,
                    builder.length(),
                    SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);

            Activity activity = getActivity();
            if (activity == null) {
                return;
            }
            activity.runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            mLogView.append(builder);
                            Editable editable = mLogView.getEditableText();
                            int length = editable.length();
                            if (length > MAX_LENGTH) {
                                editable.delete(0, length - LOWER_THRESHOLD);
                            }
                        }
                    });
        }

        public void startActivity(Intent intent) {
            getActivity().startActivity(intent);
        }
    }
}

