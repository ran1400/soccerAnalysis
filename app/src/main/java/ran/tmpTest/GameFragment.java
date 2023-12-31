package ran.tmpTest;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import android.app.Fragment;

import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;

import ran.tmpTest.alertDialogs.AddEventAlertDialog;
import ran.tmpTest.sharedData.AppData;
import ran.tmpTest.utils.Event;


public class GameFragment extends Fragment
{
    public static int gameChosen;
    private View view;
    private TextView clock,msgToUser;
    private ImageButton playBtn,stopBtn;
    private RadioGroup gamePart,team;
    private NumberPicker playerDigit1,playerDigit2;

    private Button specialEvent;
    private ConstraintLayout scrollViewLayout;

    private ClockThread clockThread = new ClockThread();
    private Handler clockHandler = new Handler();
    private Spinner chooseGame;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_game, container, false);
        AppData.gameFragment = this;
        chooseGame = view.findViewById(R.id.dropDownList);
        scrollViewLayout = view.findViewById(R.id.scrollViewLayout);
        createDropDownList();
        setScrollSize();
        msgToUser = view.findViewById(R.id.msgToUser);
        msgToUser.setVisibility(View.INVISIBLE);
        playerDigit1 = view.findViewById(R.id.playerDigit1);
        playerDigit2 = view.findViewById(R.id.playerDigit2);
        setPlayerNumPickers();
        clock = view.findViewById(R.id.clock);
        playBtn = view.findViewById(R.id.playBtn);
        playBtn.setOnClickListener((View)->playBtn());
        stopBtn = view.findViewById(R.id.stopBtn);
        stopBtn.setOnClickListener((View)-> stopBtn());
        gamePart = view.findViewById(R.id.gamePart);
        team = view.findViewById(R.id.selectTeam);
        specialEvent = view.findViewById(R.id.specialEvent);
        specialEvent.setOnClickListener((View)->specialEvent());
        createButtons(view,AppData.events);
        if (AppData.clockRun)
        {
            playBtn.setVisibility(View.INVISIBLE);
            clock.setText(makeClockText());
            startClock();
        }
        gamePart.setOnCheckedChangeListener((group, checkedId) -> setGamePartValue(checkedId));
        team.setOnCheckedChangeListener((group, checkedId) -> SetTeamValue(checkedId));
        clock.setTextColor(Color.BLACK);
        playerDigit1.setValue(AppData.playerChosenDigit1);
        playerDigit2.setValue(AppData.playerChosenDigit2);
        setGamePart();
        setTeamChosen();
        if (AppData.games.isEmpty() == false && AppData.events.isEmpty() == false)
            msgToUser.setVisibility(View.INVISIBLE);
        return view;
    }

    public void onDestroy()
    {
        super.onDestroy();
        clockHandler.removeCallbacks(clockThread);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        AppData.playerChosenDigit1 = playerDigit1.getValue();
        AppData.playerChosenDigit2 = playerDigit2.getValue();
    }

    public void onResume()
    {
        super.onResume();
        if(GameFragment.gameChosen != -1)
        {
            chooseGame.setSelection(GameFragment.gameChosen);
        }
    }

    private AdapterView.OnItemSelectedListener onSelectGameDropDownList()
    {
        return new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                GameFragment.gameChosen = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
                GameFragment.gameChosen = -1;
            }
        };
    }
    public void specialEvent()
    {
        if(AppData.games.isEmpty())
        {
            Toast.makeText(getActivity(), "הוסף משחק בהגדרות", Toast.LENGTH_LONG).show();
            return;
        }
        AppData.playerChosenDigit1 = playerDigit1.getValue();
        AppData.playerChosenDigit2 = playerDigit2.getValue();
        AddEventAlertDialog addEventAlertDialog = new AddEventAlertDialog();
        addEventAlertDialog.show(AppData.mainActivity.getSupportFragmentManager(),"");
    }


    public void showMsgToUser(String text)
    {
        msgToUser.setText(text);
        msgToUser.setVisibility(View.VISIBLE);
    }

    public void SetTeamValue(int checkedId)
    {
        switch(checkedId)
        {
            case R.id.noTeam :
                AppData.teamChosen = Event.Team.NON;
                break;
            case R.id.home :
                AppData.teamChosen = Event.Team.HOME_TEAM;
                break;
            case R.id.away :
                AppData.teamChosen = Event.Team.AWAY_TEAM;
        }
    }

    public void setGamePart()
    {
        switch(AppData.gamePartChosen)
        {
            case HALF_1:
                gamePart.check(R.id.h1);
                break;
            case HALF_2:
                gamePart.check(R.id.h2);
                break;
            case EXTRA_TIME_1:
                 gamePart.check(R.id.et1);
                 break;
            case EXTRA_TIME_2:
                 gamePart.check(R.id.et2);
        }
    }

    public void setTeamChosen()
    {
        switch (AppData.teamChosen)
        {
            case NON :
                team.check(R.id.noTeam);
                break;
            case HOME_TEAM:
                team.check(R.id.home);
                break;
            case AWAY_TEAM:
                team.check(R.id.away);
        }
    }

    public void setGamePartValue(int checkedId)
    {
        switch(checkedId)
        {
            case R.id.h1:
                AppData.gamePartChosen = Event.GamePart.HALF_1;
                break;
            case R.id.h2:
                AppData.gamePartChosen = Event.GamePart.HALF_2;
                break;
            case R.id.et1 :
                AppData.gamePartChosen = Event.GamePart.EXTRA_TIME_1;
                break;
            case R.id.et2 :
                AppData.gamePartChosen = Event.GamePart.EXTRA_TIME_2;
                break;
        }
    }

    public int getPlayerNumber()
    {
        return playerDigit1.getValue() * 10 + playerDigit2.getValue();
    }

    public void setPlayerNumPickers()
    {
        playerDigit1.setMinValue(0);
        playerDigit2.setMinValue(0);
        playerDigit1.setMaxValue(9);
        playerDigit2.setMaxValue(9);
    }

    public void createDropDownList()
    {
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item,AppData.gamesStringList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseGame.setAdapter(adapter);
        chooseGame.setOnItemSelectedListener(onSelectGameDropDownList());
    }


    public static int dpToPx(int dp, Context context)
    {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public void setScrollSize()
    {
        int height = getResources().getConfiguration().screenHeightDp;
        ViewGroup.LayoutParams params = scrollViewLayout.getLayoutParams();
        //Changes the height and width to the specified *pixels*
        params.height = dpToPx((int) (height * 0.35),getActivity()); //the size of the scrollBar
        scrollViewLayout.setLayoutParams(params);
    }

    public void createButtons(View view , List<String> list)
    {
        if (AppData.gamesStringList.size() == 0)
        {
            showMsgToUser("הוסף משחק בהגדרות");
            return;
        }
        else if (list.isEmpty())
        {
            showMsgToUser("הוסף אירועים בהגדרות");
            return;
        }

        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.buttonList);
        linearLayout.removeAllViewsInLayout();
        final Button[] button = new Button[list.size()];
        for (int i = 0; i < button.length ; i++)
        {
            int buttonStyle = R.style.buttonStyle;
            button[i]  = new Button(new ContextThemeWrapper(getActivity(), buttonStyle), null, buttonStyle);
            button[i].setHeight(170);
            button[i].setOnClickListener(new eventListener(i));
            button[i].setText(list.get(i));
            linearLayout.addView(button[i]);
        }
    }

    private class eventListener implements View.OnClickListener //events buttons
    {
        int eventNum;
        public eventListener(int eventNum)
        {
            this.eventNum = eventNum ;
        }
        @Override
        public void onClick(View v) //buttonListClick
        {
            Event event = makeEvent(eventNum);
            AppData.games.get(GameFragment.gameChosen).events.add(event);
            AppData.mainActivity.showSnackBar("האירוע נרשם",300);
        }
    }


    public Event makeEvent(int eventNum)
    {
        int playerNum = getPlayerNumber();
        Event.GamePart gamePart = AppData.gamePartChosen;
        Event.Team team = AppData.teamChosen;
        String eventName = AppData.events.get(eventNum);
        if (AppData.clockRun)
            return new Event(gamePart,team,AppData.min,AppData.sec,playerNum,eventName);
        else
            return new Event(gamePart,team,0,0,playerNum,eventName);
    }

    public void playBtn()
    {
        if (GameFragment.gameChosen == -1)
        {
            Toast.makeText(getActivity(), "הוסף משחק בהגדרות", Toast.LENGTH_LONG).show();
            return;
        }
        startClock();
        playBtn.setVisibility(View.INVISIBLE);
    }

    public void stopBtn()
    {
        stopClock();
        clock.setText("00:00");
        AppData.min = 0;
        AppData.sec = -1;
        playBtn.setVisibility(View.VISIBLE);
    }


    private class ClockThread implements java.lang.Runnable
    {
        public void run()
        {
            clockHandler.postDelayed(clockThread, 1000);
            AppData.sec++;
            if (AppData.sec == 60)
            {
                AppData.min++;
                AppData.sec = 0;
            }
            clock.setText(makeClockText());
            if (AppData.min == 1000)
                stopBtn();
        }
    }

    public static String makeClockText()
    {
        String minText,secText;
        if (AppData.min  > 9)
            minText = String.valueOf(AppData.min);
        else
            minText = "0" + AppData.min;
        if (AppData.sec > 9)
            secText = String.valueOf(AppData.sec);
        else
            secText = "0" + AppData.sec;
        return minText + ":" + secText;
    }

    public void stopClock()
    {
        AppData.clockRun = false;
        clockHandler.removeCallbacks(clockThread);
    }

    public void startClock()
    {
        AppData.clockRun = true;
        //handler.removeCallbacks(runnable);
        clockThread.run();
    }
}