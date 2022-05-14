package com.cbd.teammate;

import com.cbd.database.entities.Venue;

import java.util.*;
import java.lang.*;

public class HashMapSort {

    // function to sort hashmap by values
    public HashMapSort() {

    }

    public static HashMap<Venue, Double> sort(HashMap<Venue, Double> hashmap)
    {
        List<Map.Entry<Venue, Double> > list = new LinkedList<Map.Entry<Venue, Double> >(hashmap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Venue, Double> >() {
            public int compare(Map.Entry<Venue, Double> o1,
                               Map.Entry<Venue, Double> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        HashMap<Venue, Double> result = new LinkedHashMap<>();

        for (Map.Entry<Venue, Double> mp : list) {
            result.put(mp.getKey(), mp.getValue());
        }
        return result;
    }

}