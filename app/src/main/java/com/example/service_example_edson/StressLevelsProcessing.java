package com.example.service_example_edson;


import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StressLevelsProcessing {
    List<Double> signal;
    public StressLevelsProcessing( List<Double> data){
        this.signal=data;
    };
    // 1) Preprocesing (noise reduction)
    List<Double> medianFilter(List<Double> signal) {
        int lenFilter = 25; //TODO: default 100;
        int lenSignal = signal.size();
        List<Double> suavizado = Collections.emptyList();
        int mitad = lenFilter / 2; //divicion entera
        for (int i = 0; i < lenSignal; i++) {
            int initIndex = i - mitad;
            Double s = 0.0;
            int counter = 0;
            for (int f = 0; f < lenFilter; f++) {
                int currentIndex = initIndex + f;
                //check valid current index
                if (0 < currentIndex && currentIndex < lenSignal) {
                    s += signal.get(currentIndex); // *filter[f] o (*1)
                    counter += 1;
                }
            }
            suavizado.add(s / counter);
        }
        return suavizado;
    }
    //2 Agregation
    List<Double> aggregation(List<Double> signal) {
        int lenFilter = 60; //TODO: default 240(4hz)  1 minute sampling
        List<Double> aggregated = Collections.emptyList();
        int lenSignal = signal.size();
        int mitad = lenFilter / 2;
        for (int i = 0; i < lenSignal; i++) {
            int initIndex = i - mitad;
            Double maxValue = 0.0;
            for (int f = 0; f < lenFilter; f++) {
                int currentIndex = initIndex + f;
                //check valid current index
                if (0 < currentIndex && currentIndex < lenSignal) {
                    maxValue = Math.max(maxValue, signal.get(currentIndex));
                }
            }
            aggregated.add(maxValue);
        }
        return aggregated;
    }

    // 3) Discretization
    Double mean(List<Double> signal) {
        Double s = 0.0;
        for (Double e : signal) {
            s = s + e;
        }
        return s / (signal.size());
    }

    Double standardDeviation(List<Double> signal) {
        Double variance = 0.0;
        Double m = mean(signal);
        for (int i = 0; i < signal.size(); ++i) {
            variance += Math.pow(signal.get(i) - m, 2.0);
        }
        Double stdDeviation = Math.sqrt(variance / signal.size());
        return stdDeviation;
    }

    List<Double> znorm(List<Double> signal, Double znormTheshold ) {// znormTheshold = 0.01
        List<Double> r = Collections.emptyList();
        Double std = standardDeviation(signal);
        Log.i("STD: $std", String.valueOf(std));
        if (std < znormTheshold) {
            return signal;
        } else {
            Double m = mean(signal);
            Log.i("Media: $m", String.valueOf(m));

            signal.forEach((e) ->{
                    r.add((e - m) / std);
      });
            return r;
        }
    }

    List<Double> paa(List<Double> series, int paaSegments) {
        int seriesLen = series.size();
        //check for the trivial case
        if (seriesLen == paaSegments) {
            return series;
        } else {
            return series;
            //todo: not using for this project
        }
    }

    List<Double> aggregationPAA(List<Double> signal) {
        List<Double> datZnorm = znorm(signal,0.01);
        List<Double> r = paa(datZnorm, signal.size());
        return r;
    }

    // 3.1 -----------------final dicretization

    // Convert a numerical index to a char
    String idx2letter(int idx) {
        if (0 <= idx && idx < 5) {
            return  String.valueOf(97 + idx);
        } else {
            String text = "Warning: idx2letter idx out of range";
            Log.i("idx2letter",text);//"\x1B[33m$text\x1B[0m"
            return String.valueOf(97);
        }
    }

    // num-to-string conversion.
    List<String> ts_to_string(List<Double> series, List<Double> cuts) {
        int a_size = cuts.size();
        List<String> sax = Collections.emptyList();
        for (int i = 0; i < series.size(); i++) {
            Double num = series.get(i);
            //if the number below 0, start from the bottom, or else from the top
            int j;
            if (num >= 0) {
                j = a_size - 1;
                while ((j > 0) && (cuts.get(j) >= num)) {
                    j = j - 1;
                }
                sax.add(idx2letter(j));
            } else {
                j = 1;
                while ((j < a_size) && (cuts.get(j) <= num)) {
                    j = j + 1;
                }
                sax.add(idx2letter(j - 1));
            }
        }
        return sax;
    }

    List<Double> cuts_for_asize(Integer a_size) {
        Double inf = 999999.0;
        List<Double> options= new ArrayList<Double>();
        if (a_size==3){
            options.add(-inf);
            options.add(-0.4307273);
            options.add(0.4307273);
        }else if(a_size==5){
            options.add(-inf);
            options.add(-0.841621233572914);
            options.add(-0.2533471031358);
            options.add(0.2533471031358);
            options.add(0.841621233572914);

        }

        return options;
    }

    List<Integer> discretizar(List<Double> signal) {

        List<String> abc =
                ts_to_string(signal, cuts_for_asize(5)); // abc : (cadena de String)
        List<Integer> r = new ArrayList<>();
        Integer val =0;
        for (Integer i = 0; i < abc.size(); i++) {
            switch (abc.get(i)){
                case "a":  val= 1;
                    break;
                case "b":  val = 2;
                    break;
                case "c":  val = 3;
                    break;
                case "d":  val = 4;
                    break;
                case "e":  val = 5;
                    break;
            }
            r.add(val);
        }
        return r;
    }

    List<Double> getResult() {
        List<Double> signalSuavizado = medianFilter(signal);
        List<Double> aggregated = aggregation(signalSuavizado);
        List<Double> ppaV = aggregationPAA(aggregated);
        List<Integer> eda_discretizado = discretizar(ppaV);
        List<Double> r = new ArrayList<>();
        eda_discretizado.forEach((e) -> {
                r.add(e * 1.0);
    });

        return r;
    }

}
