package studentmanager;

import java.util.ArrayList;
import java.util.HashMap;

class Student {

    private String name;
    private ArrayList<Characteristic> chars;
    private ArrayList<String> friends;
    private ArrayList<String> workWith;
    private ArrayList<String> dislike;
    public enum Characteristic{
        SMART, NEEDSHELP, TALKATIVE, SHY, EMOTIONAL, UNFOCUSED, FOCUSED
    }
    private boolean needFront;

    public Student(String name, ArrayList<Characteristic> chars, ArrayList<String> friends,
                   ArrayList<String> workWith, ArrayList<String> dislike, boolean needFront){
        this.name = name;
        this.chars = chars;
        this.dislike = dislike;
        this.friends = friends;
        this.workWith = workWith;
        this.needFront = needFront;
    }

    public Student(String name){
        this.name = name;
        chars = new ArrayList<>();
        dislike= new ArrayList<>();
        friends = new ArrayList<>();
        workWith = new ArrayList<>();
        needFront = false;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getDislike() {
        return dislike;
    }

    public ArrayList<String> getFriends() {
        return friends;
    }

    public boolean isNeedFront() {
        return needFront;
    }

    public ArrayList<String> getWorkWith() {
        return workWith;
    }

    public void setChars(ArrayList<Characteristic> chars) {
        this.chars = chars;
    }

    public void changeChar(Characteristic ch, boolean change){
        if(change && !chars.contains(ch)){
            chars.add(ch);
        }
        else if(!change && chars.contains(ch)){
            chars.remove(ch);
        }
    }

    public ArrayList<Characteristic> getChars() {
        return chars;
    }

    public void setDislike(ArrayList<String> dislike) {
        this.dislike = dislike;
    }

    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNeedFront(boolean needFront) {
        this.needFront = needFront;
    }

    public void setWorkWith(ArrayList<String> workWith) {
        this.workWith = workWith;
    }

    public boolean isChar(String ch){
        return chars.contains(ch);
    }

    public void addFriend(String s){
        friends.add(s);
    }

    public void removeFriend(String s){
        friends.remove(s);
    }

    public void addDislikes(String s){
        dislike.add(s);
    }

    public void removeDislikes(String s) {
        dislike.remove(s);
    }

    public void addWorkWith(String s){
        workWith.add(s);
    }

    public void removeWorkWith(String s) {
        workWith.remove(s);
    }

}

