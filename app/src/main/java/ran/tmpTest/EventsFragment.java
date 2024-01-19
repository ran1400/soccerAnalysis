package ran.tmpTest;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ran.tmpTest.sharedData.AppData;
import ran.tmpTest.utils.Event;
import ran.tmpTest.utils.ExelHandel;
import ran.tmpTest.utils.lists.SwipeToDeleteList;

import java.util.ArrayList;
import java.util.List;


public class EventsFragment extends Fragment
{
    public static int gameChosen;
    private List<Event> listToShow;
    private Button saveFileBtn;
    private Spinner chooseGameDropDownList;
    private RecyclerView eventsList;
    private SwipeToDeleteList swipeToDeleteList;

    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_events, container, false);
        saveFileBtn = view.findViewById(R.id.saveFileBtn);
        AppData.eventsFragment = this;
        if (AppData.gamesStringList.isEmpty())
        {
            AppData.mainActivity.showSnackBar("הוסף משחקים בהגדרות",700);
            saveFileBtn.setVisibility(View.INVISIBLE);
            return view;
        }
        eventsList = view.findViewById(R.id.eventsList);
        chooseGameDropDownList = view.findViewById(R.id.choseGameDropDownList);
        saveFileBtn.setOnClickListener(this::saveFileBtn);
        swipeToDeleteList = new SwipeToDeleteList();
        eventsList.setAdapter(swipeToDeleteList);
        eventsList.setLayoutManager(new LinearLayoutManager(view.getContext()));
        createEventsList();
        createChoseGameDropDownList();
        return view;
    }

    public void onResume()
    {
        super.onResume();
        if(gameChosen != -1)
            chooseGameDropDownList.setSelection(gameChosen);
    }

    private void createEventsList()
    {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT)
        {
            private Paint paint = new Paint();
            private TextView swipeToDeleteText;

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target)
            {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
            {
                // Remove the swiped item from the list
                int position = viewHolder.getAdapterPosition();
                swipeToDeleteList.listData.remove(position);
                listToShow.remove(position);
                swipeToDeleteList.notifyItemRemoved(position);
                if(listToShow.isEmpty())
                    saveFileBtn.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onChildDraw(Canvas canvas,RecyclerView recyclerView,RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive)
            {
                super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                if (actionState != ItemTouchHelper.ACTION_STATE_SWIPE)
                    return;
                View itemView = viewHolder.itemView;
                paint.setColor(Color.RED);
                canvas.drawRect(itemView.getRight() + dX, itemView.getTop(), itemView.getRight(), itemView.getBottom(), paint);
                if (swipeToDeleteText == null)
                {
                    swipeToDeleteText = new TextView(view.getContext());
                    swipeToDeleteText.setText("החלק למחיקה");
                    swipeToDeleteText.setTextColor(Color.WHITE);
                    swipeToDeleteText.setBackgroundColor(Color.RED);
                    swipeToDeleteText.setPadding(16, 16, 16, 16);
                    swipeToDeleteText.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                    swipeToDeleteText.layout(0, 0, swipeToDeleteText.getMeasuredWidth(), swipeToDeleteText.getMeasuredHeight());
                }
                float textX = itemView.getRight() - swipeToDeleteText.getWidth() - 16; // Adjust padding;
                float textY = itemView.getTop() + ((itemView.getBottom() - itemView.getTop() - swipeToDeleteText.getHeight()) / 2);
                canvas.save();
                canvas.translate(textX, textY);
                swipeToDeleteText.draw(canvas);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(eventsList);
    }

    public void saveFileBtn(View view)
    {
        ExelHandel exelHandel = new ExelHandel();
        boolean success =  exelHandel.makeEventsFile();
        if (success)
            AppData.mainActivity.showSnackBar("הקובץ נשמר בתיקיית הורדות",1000);
        else
            AppData.mainActivity.showSnackBar("שמירת הקובץ נכשלה",1000);
    }

    public void createChoseGameDropDownList()
    {

        ArrayAdapter<String>adapter = new ArrayAdapter(getActivity(),
                                                       android.R.layout.simple_spinner_item,AppData.gamesStringList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseGameDropDownList.setAdapter(adapter);
        chooseGameDropDownList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l)
            {
                gameChosen = position;
                listToShow = AppData.games.get(position).events;
                showGameEvents();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
                gameChosen = -1;
                AppData.listToShow = null;
                AppData.mainActivity.showSnackBar("הוסף משחקים בהגדרות",700);
                saveFileBtn.setVisibility(View.INVISIBLE);
                swipeToDeleteList.listData = null;
                swipeToDeleteList.notifyDataSetChanged();
            }
        });
    }

    private void showGameEvents()
    {
        if(listToShow.isEmpty())
        {
            saveFileBtn.setVisibility(View.INVISIBLE);
            swipeToDeleteList.listData = null;
        }
        else
        {
            swipeToDeleteList.listData = new ArrayList<>();
            saveFileBtn.setVisibility(View.VISIBLE);
            for (Event event : listToShow)
                swipeToDeleteList.listData.add(event.toString());
        }
        swipeToDeleteList.notifyDataSetChanged();
    }
}