package com.example.socceranalysis.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Game
{
    public  String gameName;
    public List<Event> events;

    public Game(String gameName)
    {
        this.gameName = gameName;
        events = new ArrayList<>();
    }
}
