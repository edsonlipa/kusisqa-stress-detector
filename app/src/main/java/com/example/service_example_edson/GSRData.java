package com.example.service_example_edson;

public class GSRData {
    String time;
    Double value;
    GSRData(String time,Double value){
        this.time=time;
        this.value=value;
    };
    GSRData(){
        this.time="0";
        this.value=0.0;
    };
}
