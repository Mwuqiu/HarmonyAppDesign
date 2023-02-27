package com.example.designview;

public class SampleItem {
    private String name;
    private String content;

    public SampleItem(String name,String content) {
        this.name = name;
        this.content = content;
    }
    public String getName() {
        return name;
    }
    public String getContent(){
        return content;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setContent(String content){
        this.content = content;
    }
}
