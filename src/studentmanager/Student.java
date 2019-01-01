package studentmanager;

import java.util.ArrayList;
import java.util.HashMap;

class Student {

    private String name;
    private ArrayList<String> chars;
    private ArrayList<Student> friends;
    private ArrayList<Student> workWith;
    private ArrayList<Student> dislike;
    private boolean needFront;

    public Student(String name, ArrayList<String> chars, ArrayList<Student> friends,
                   ArrayList<Student> workWith, ArrayList<Student> dislike, boolean needFront){
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

    public ArrayList<Student> getDislike() {
        return dislike;
    }

    public ArrayList<Student> getFriends() {
        return friends;
    }

    public boolean isNeedFront() {
        return needFront;
    }

    public ArrayList<Student> getWorkWith() {
        return workWith;
    }

    public void setChars(ArrayList<String> chars) {
        this.chars = chars;
    }

    public void changeChar(String ch, boolean change){
        if(change && !chars.contains(ch)){
            chars.add(ch);
        }
        else if(!change && chars.contains(ch)){
            chars.remove(ch);
        }
    }

    public ArrayList<String> getChars() {
        return chars;
    }

    public void setDislike(ArrayList<Student> dislike) {
        this.dislike = dislike;
    }

    public void setFriends(ArrayList<Student> friends) {
        this.friends = friends;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNeedFront(boolean needFront) {
        this.needFront = needFront;
    }

    public void setWorkWith(ArrayList<Student> workWith) {
        this.workWith = workWith;
    }

    public boolean isChar(String ch){
        return chars.contains(ch);
    }

    public void addFriend(Student s){
        friends.add(s);
    }

    public void addDislikes(Student s){
        dislike.add(s);
    }

    public void addWorkWith(Student s){
        workWith.add(s);
    }
}

