package com.example.service_example_edson;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class GSRBuffer {
    Queue<Double> _queue;
    /// the maximum frames in real time. Case: if 4hz there are 4*60 in 1 minute
    int _maxBuffer = 60*30;
    int _currentStressLevel = 1;

    GSRBuffer() {
        _queue = new LinkedList<>();
    }

    void push(Double value) {
        if (_queue.size() >= _maxBuffer) {
            _queue.remove();//remove first
        }
        _queue.add(value);
    }

    /// generate data from to plot the signal
    List<GSRData> getData() {
        List<GSRData> data = new ArrayList<GSRData>();//new List<GSRData>();
        int cont = 0;
        String time = "0";
        for (Double e : _queue) {
            time = Integer.toString(cont);
            data.add(new GSRData( time,  e));
            cont++;
        }
        return data;
    }

    //TODO: process stress levels
    List<GSRData>  getProcessData(){
        StressLevelsProcessing stressLevelsProcessing = new StressLevelsProcessing(getSignalBuffer());
        List<Double> r = stressLevelsProcessing.getResult();
        List<GSRData> data = new ArrayList<>();
        for(int i=0; i<r.size(); i++){
            String time = Integer.toString(i);
            data.add(new GSRData(time, r.get(i)));
        }
        if(r.size()>0){
            _currentStressLevel = (int) Math.round(r.get(r.size() - 1));//el ultimo nivel
        }

        return data;
    }

    int  CurrentStressLevel (){
      return this._currentStressLevel;
    }



    List<Double> getSignalBuffer() {
        return  (List) _queue;

    }

    int length(){
        return _queue.size();
    }
    int get_timeInSeconds () {

        return _queue.size();

    }

    Double get_timeInMinutes(){
        return (_queue.size())/60.0;
    }



}
