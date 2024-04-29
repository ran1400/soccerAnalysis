package ran.tmpTest.alertDialogs;

import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.os.Bundle;
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

import ran.tmpTest.GameFragment;
import ran.tmpTest.MainActivity;
import ran.tmpTest.R;


import ran.tmpTest.sharedData.AppData;
import ran.tmpTest.utils.Event;
import ran.tmpTest.utils.Game;


public class AddEventAlertDialog extends AppCompatDialogFragment
{

    private NumberPicker playerDigit1NumberPicker, playerDigit2NumberPicker;
    private ConstraintLayout eventsScrollView;
    private RadioGroup eventsRadioGroup;
    private RadioButton specialEventRadioButton;
    private EditText specialEventEditText;
    private TextView clockTextView,gamePartTextView;
    private Event.Team teamChosen;
    private final int PERSONAL_EVENT = -1;
    private int getEventChosenHelper;
    int min,sec;
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
        specialEventRadioButton = view.findViewById(R.id.specialEvent);
        specialEventEditText = view.findViewById(R.id.specialEventText);
        playerDigit1NumberPicker = view.findViewById(R.id.playerDigit1);
        playerDigit2NumberPicker = view.findViewById(R.id.playerDigit2);
        eventsRadioGroup = view.findViewById(R.id.eventsRadioGroup);
        eventsScrollView = view.findViewById(R.id.scrollViewLayout);
        RadioGroup choseTeamRadioGroup = view.findViewById(R.id.selectTeam);
        eventsRadioGroup.setOnCheckedChangeListener(this::eventsRadioGroupOnCheckedChanged);
        specialEventRadioButton.setOnClickListener(this::showKeyboard);
        specialEventEditText.setOnClickListener(this::specialEventTextOnCLicked);
        choseTeamRadioGroup.setOnCheckedChangeListener(this::chooseTeamRadioBtnOnCheckedChanged);
        cancelBtn.setOnClickListener(this::cancelBtn);
        saveBtn.setOnClickListener(this::saveBtn);
        min = AppData.min;
        sec = AppData.sec;
        setPlayerNumPickers0To9();
        GameFragment.setLayoutSize(eventsScrollView,50);
        getEventChosenHelper = addEvents(); //addEvents return the button Id of the first event in list
        setClockAndPickers();
        builder.setView(view);
        return builder.create();
    }



    public void  specialEventTextOnCLicked(View view)
    {
        eventsRadioGroup.check(specialEventRadioButton.getId());
        showKeyboard();
    }

    public void eventsRadioGroupOnCheckedChanged(RadioGroup group, int checkedId)
    {
        if ( checkedId != specialEventRadioButton.getId() )
        {
            specialEventEditText.setText("");
            specialEventEditText.setFocusable(false);
            hideKeyboard();
        }
        else
            showKeyboard();
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
        if (eventChosen == PERSONAL_EVENT && specialEventEditText.getText().toString().isEmpty())
        {
            Toast.makeText(getActivity(), "הכנס אירוע",Toast.LENGTH_SHORT).show();
            return;
        }
        Event event = makeEvent(eventChosen);
        Game crntGame = AppData.games.get(GameFragment.gameChosen);
        crntGame.events.add(event);
        AppData.gameFragment.showEventAddedSnackBar(crntGame);
        dismiss();
    }

    private Event makeEvent(int eventChosen)
    {
        int playerNum = getPlayerNumber();
        Event.GamePart gamePart = AppData.gamePartChosen;
        Event.Team team = teamChosen;
        String eventName;
        if ( eventChosen == PERSONAL_EVENT)
            eventName = specialEventEditText.getText().toString();
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
            return PERSONAL_EVENT;
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
        return playerDigit1NumberPicker.getValue() * 10 + playerDigit2NumberPicker.getValue();
    }

    private void setClockAndPickers()
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
        RadioButton gamePartRadioBtnToCheck ;
        if (teamChosen == Event.Team.NON)
            gamePartRadioBtnToCheck = view.findViewById(R.id.noTeam);
        else if (teamChosen == Event.Team.HOME_TEAM)
            gamePartRadioBtnToCheck = view.findViewById(R.id.home);
        else // (teamChosen == Event.Team.AWAY_TEAM)
            gamePartRadioBtnToCheck = view.findViewById(R.id.away);
        gamePartRadioBtnToCheck.setChecked(true);
        playerDigit1NumberPicker.setValue(AppData.playerChosenDigit1);
        playerDigit2NumberPicker.setValue(AppData.playerChosenDigit2);
    }
    private void showKeyboard(View view)
    {
        showKeyboard();
    }

    public void showKeyboard()
    {
            specialEventEditText.setFocusableInTouchMode(true);
            specialEventEditText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    private void hideKeyboard()
    {
        specialEventEditText.setEnabled(false);
        specialEventEditText.setEnabled(true);
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
        return button.getId() - AppData.events.size() + 1;
    }

    public void setPlayerNumPickers0To9()
    {
        playerDigit1NumberPicker.setMinValue(0);
        playerDigit2NumberPicker.setMinValue(0);
        playerDigit1NumberPicker.setMaxValue(9);
        playerDigit2NumberPicker.setMaxValue(9);
    }


}