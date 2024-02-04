package com.example.cs2340proj1.ui.courses;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cs2340proj1.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Locale;

public class CourseEditorFragment extends BottomSheetDialogFragment {

    Button addButton, startButton, endButton, deleteButton;
    EditText courseEdit, professorEdit, locationEdit;
    int hour, minute;
    CourseInfo newCourse;
    CourseViewModel viewModel;


    int currPosition = -1;

    public CourseEditorFragment() {
    }

    public static Fragment newInstance(ArrayList<CourseInfo> currCourseList, int currIndex)
    {
        CourseEditorFragment myFragment = new CourseEditorFragment();
        Bundle args = new Bundle();
        args.putSerializable("currList", currCourseList);
        args.putInt("currPosition", currIndex);
        myFragment.setArguments(args);
        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.card_editor, container, false);

        // simply instantiates all of the buttons and EditText
        findLayout(view);

        if (getArguments() != null) {
            setLayout();
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String courseName = courseEdit.getText().toString();
                String professor = professorEdit.getText().toString();
                String startTime = startButton.getText().toString();
                String endTime = endButton.getText().toString();
                String location = locationEdit.getText().toString();

                String[] dates = getDates(view);

                newCourse = new CourseInfo(courseName, professor, startTime, endTime, dates, location);

                viewModel = new ViewModelProvider(requireActivity()).get(CourseViewModel.class);

                if (currPosition > -1) {
                    viewModel.editCourseInfo(newCourse, currPosition);
                } else {
                    viewModel.addCourseInfo(newCourse);
                }

                dismiss();
            }

        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete Confirmation")
                        .setMessage("Are you sure you want to delete this?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                viewModel = new ViewModelProvider(requireActivity()).get(CourseViewModel.class);
                                if (currPosition > -1) {
                                    viewModel.deleteCourseInfo(currPosition);
                                }
                                dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });



        // buttom two onClick Listeners will open the Time Dialog for start and end time buttons
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerPopup(startButton);
            }
        });

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerPopup(endButton);
            }
        });

        return view;
    }

    public boolean[] confirmationDialog() {

        final boolean[] returnedBoolean = new boolean[1];
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        returnedBoolean[0] = true;
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        returnedBoolean[0] = false;
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

        return returnedBoolean;
    }


    private void setLayout() {
        currPosition = getArguments().getInt("currPosition");

        ArrayList<CourseInfo> courseList = (ArrayList<CourseInfo>) getArguments().getSerializable("currList");
        CourseInfo currCourse = courseList.get(currPosition);

        professorEdit.setText(currCourse.getProfessor());
        courseEdit.setText(currCourse.getCourseName());
        locationEdit.setText(currCourse.getLocation());
        startButton.setText(currCourse.getStartTime());
        endButton.setText(currCourse.getEndTime());

        deleteButton.setText("Delete");
    }

    private void findLayout(View view) {
        addButton = view.findViewById(R.id.save_card_edit);
        deleteButton = view.findViewById(R.id.cancel_card_edit);
        startButton = view.findViewById(R.id.start_time_button);
        endButton = view.findViewById(R.id.end_time_button);

        courseEdit = view.findViewById(R.id.courseInputEdit);
        professorEdit = view.findViewById(R.id.professorInputEdit);
        locationEdit = view.findViewById(R.id.locationInputEdit);
    }


    private String[] getDates(View view) {
        String[] dates = new String[5];

        Switch monSwitch = view.findViewById(R.id.mon_switch);
        Switch tuesSwitch = view.findViewById(R.id.tue_switch);
        Switch wedSwitch = view.findViewById(R.id.wed_switch);
        Switch thurSwitch = view.findViewById(R.id.thur_switch);
        Switch friSwitch = view.findViewById(R.id.fri_switch);


        // ternary operators where 1 = true, 0 = false
        // depending on if the switch for that date is on or off, each element in the string
        // is set to 0 or 1
        dates[0] = (monSwitch.isChecked()) ? " Mon " : "";
        dates[1] = (tuesSwitch.isChecked()) ? " Tues " : "";
        dates[2] = (wedSwitch.isChecked()) ? " Wed " : "";
        dates[3] = (thurSwitch.isChecked()) ? " Thurs " : "";
        dates[4] = (friSwitch.isChecked()) ? " Fri " : "";

        return dates;
    }




    // the method that creates a TimePickerDialog
    public void TimePickerPopup(Button currButton) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;

                currButton.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));

            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), onTimeSetListener, hour, minute, true);
        timePickerDialog.show();
    }

}
