package com.example.socceranalysis.alertDialogs;

import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socceranalysis.GameFragment;
import com.example.socceranalysis.R;


import com.example.socceranalysis.sharedData.AppData;
import com.example.socceranalysis.utils.Event;

public class AddEventAlertDialog extends AppCompatDialogFragment
{

    private NumberPicker playerDigit1,playerDigit2;
    private ConstraintLayout scrollViewLayout;
    private RadioGroup eventsRadioGroup;
    private RadioButton specialEvent;
    private EditText specialEventText;
    private TextView clockTextView,gamePartTextView;
    private Event.Team teamChosen;
    private final int personalEvent = -1;
    private int getEventChosenHelper;
    private boolean keyboardUp = true;
    private View view;

    private Button saveBtn,cancelBtn;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.alert_dialog_add_event, null);
        clockTextView = view.findViewById(R.id.clockText);
        saveBtn = view.findViewById(R.id.saveButton);
        cancelBtn = view.findViewById(R.id.cancelButton);
        gamePartTextView = view.findViewById(R.id.gamePartText);
        specialEvent = view.findViewById(R.id.specialEvent);
        specialEventText = view.findViewById(R.id.specialEventText);
        playerDigit1 = view.findViewById(R.id.playerDigit1);
        playerDigit2 = view.findViewById(R.id.playerDigit2);
        eventsRadioGroup = view.findViewById(R.id.eventsRadioGroup);
        cancelBtn.setOnClickListener(this::cancelBtn);
        saveBtn.setOnClickListener(this::saveBtn);
        setPlayerNumPickers();
        scrollViewLayout = view.findViewById(R.id.scrollViewLayout);
        setScrollSize();
        getEventChosenHelper = addEvents();
        getDetails();
        eventsRadioGroup.setOnCheckedChangeListener(this::eventsRadioGroupOnCheckedChanged);
        specialEvent.setOnClickListener(this::showKeyboard);
        specialEventText.setOnClickListener(this::specialEventTextOnCLicked);
        RadioGroup choseTeamRadioGroup = view.findViewById(R.id.selectTeam);
        choseTeamRadioGroup.setOnCheckedChangeListener(this::chooseTeamRadioBtnOnCheckedChanged);
        builder.setView(view);
        return builder.create();
    }



    public void  specialEventTextOnCLicked(View view)
    {
        eventsRadioGroup.check(specialEvent.getId());
        showKeyboard();
    }

    public void eventsRadioGroupOnCheckedChanged(RadioGroup group, int checkedId)
    {
        if ( checkedId != specialEvent.getId() )
        {
            specialEventText.setText("");
            specialEventText.setFocusable(false);
            hideKeyboard();
        }
    }

    public void chooseTeamRadioBtnOnCheckedChanged(RadioGroup group, int checkedId)
    {
        choseTeam(checkedId);
    }

    public void cancelBtn(View view)
    {
        dismiss();
    }


    public void saveBtn(View view)
    {
        int eventChosen = getEventChosen();
        if (eventChosen == personalEvent && specialEventText.getText().toString().isEmpty())
        {
            Toast.makeText(getActivity(), "הכנס אירוע",Toast.LENGTH_SHORT).show();
            return;
        }
        Event event = makeEvent(eventChosen);
        AppData.games.get(GameFragment.gameChosen).events.add(event);
        AppData.mainActivity.showSnackBar("האירוע נרשם",500);
        dismiss();
    }

    private Event makeEvent(int eventChosen)
    {
        int playerNum = getPlayerNumber();
        Event.GamePart gamePart = AppData.gamePartChosen;
        int min = AppData.min;
        int sec = AppData.sec;
        Event.Team team = teamChosen;
        String eventName;
        if ( eventChosen == personalEvent )
            eventName = specialEventText.getText().toString();
        else
            eventName = AppData.events.get(eventChosen);
        if (AppData.clockRun)
            return new Event(gamePart,team,min,sec,playerNum,eventName);
        else
            return new Event(gamePart,team,0,0,playerNum,eventName);
    }

    private int getEventChosen()
    {
        int checkedEvent = eventsRadioGroup.getCheckedRadioButtonId();
        if ( checkedEvent == R.id.specialEvent)
            return personalEvent;
        Log.d("getEventChosen" , " = " + (checkedEvent - getEventChosenHelper));
        return checkedEvent - getEventChosenHelper;
    }


    public void choseTeam(int checkedId)
    {
        switch(checkedId)
        {
            case R.id.noTeam :
                teamChosen = Event.Team.NON;
                break;
            case R.id.home :
                teamChosen = Event.Team.HOME_TEAM;
                break;
            case R.id.away :
                teamChosen = Event.Team.AWAY_TEAM;
        }
    }

    public int getPlayerNumber()
    {
        return playerDigit1.getValue() * 10 + playerDigit2.getValue();
    }

    private void getDetails()
    {
        if(AppData.clockRun)
        {
            String clockText = AppData.gameFragment.makeClockText();
            clockTextView.setText(clockText);
        }
        else
            clockTextView.setText("00:00");
        switch( AppData.gamePartChosen )
        {
            case HALF_1:
                gamePartTextView.setText("מחצית 1");
                break;
            case HALF_2:
                gamePartTextView.setText("מחצית 2");
                break;
            case EXTRA_TIME_1:
                gamePartTextView.setText("הארכה 1");
                break;
            case EXTRA_TIME_2:
                gamePartTextView.setText("הארכה 2");
        }
        teamChosen = AppData.teamChosen;
        RadioButton gamePartRadioBtn ;
        if (teamChosen == Event.Team.NON)
            gamePartRadioBtn = view.findViewById(R.id.noTeam);
        else if (teamChosen == Event.Team.HOME_TEAM)
            gamePartRadioBtn = view.findViewById(R.id.home);
        else // (teamChosen == Event.Team.AWAY_TEAM)
            gamePartRadioBtn = view.findViewById(R.id.away);
        gamePartRadioBtn.setChecked(true);
        playerDigit1.setValue(AppData.playerChosenDigit1);
        playerDigit2.setValue(AppData.playerChosenDigit2);
    }
    private void showKeyboard(View view)
    {
        showKeyboard();
    }

    public void showKeyboard()
    {
        if (keyboardUp == false)
        {
            specialEventText.setFocusableInTouchMode(true);
            specialEventText.requestFocus();
            keyboardUp = true;
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }
    }

    private void hideKeyboard()
    {
        specialEventText.setEnabled(false);
        specialEventText.setEnabled(true);
        keyboardUp = false;
    }

    private int addEvents()
    {
        if (AppData.events.size() == 0 )
            return -1;
        RadioButton button = null;
        for (int i = 0; i < AppData.events.size() ; i++)
        {
            button = new RadioButton(getActivity());
            button.setText(AppData.events.get(i));
            eventsRadioGroup.addView(button);
        }
        Log.d("radioGroupCheck2" ," - start in : " + (button.getId() - AppData.events.size() + 1));
        Log.d("radioGroupCheck2" ," - end in : " + button.getId());
        return button.getId() - AppData.events.size() + 1;
    }

    public void setScrollSize()
    {
        int height = getResources().getConfiguration().screenHeightDp;
        Log.d("size323",String.valueOf(dpToPx(height)));
        ViewGroup.LayoutParams params = scrollViewLayout.getLayoutParams();
        //Changes the height and width to the specified *pixels*

        params.height = dpToPx((int) (height * 0.5)); //the size of the scrollBar
        Log.d("size323",String.valueOf(params.height));
        scrollViewLayout.setLayoutParams(params);
    }

    public int dpToPx(int dp)
    {
        float density = this.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public void setPlayerNumPickers()
    {
        playerDigit1.setMinValue(0);
        playerDigit2.setMinValue(0);
        playerDigit1.setMaxValue(9);
        playerDigit2.setMaxValue(9);
    }


}