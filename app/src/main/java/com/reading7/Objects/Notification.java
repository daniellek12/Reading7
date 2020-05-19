package com.reading7.Objects;

import com.google.firebase.Timestamp;
import com.reading7.Utils;

import java.util.ArrayList;
import java.util.Comparator;

public class Notification {

    private String type;
    private String from;
    private String book_title;
    private Timestamp time;
    private String question_content;
    private ArrayList<String> possible_answers;
    private String right_answer;
    private Utils.ChallengeState challengeState;

    public Notification() {

    }

    public Notification(String type, String from, String book_title, Timestamp time) {
        this.type = type;
        this.from = from;
        this.book_title = book_title;
        this.time = time;
        this.challengeState = Utils.ChallengeState.NOT_ANSWERED;
    }


    public Utils.ChallengeState getChallengeState() {
        return challengeState;
    }

    public void setChallengeState(Utils.ChallengeState challengeState) {
        this.challengeState = challengeState;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getBook_title() {
        return book_title;
    }

    public void setBook_title(String book_title) {
        this.book_title = book_title;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public static class SortByDate implements Comparator<Notification> {
        public int compare(Notification a, Notification b) {
            return -a.time.compareTo(b.time);
        }
    }

    @Override
    public boolean equals(Object object) {
        boolean same = false;

        if (object != null && object instanceof Notification) {

            same = (this.book_title.equals(((Notification) object).book_title)) && (this.from.equals(((Notification) object).from));

            if ((type.equals("הסכים שתעקוב אחריו") && (((Notification) object).getType()).equals("הסכים שתעקוב אחריו")))
                same = (this.from.equals(((Notification) object).from));
            if (type.equals("שלח לך אתגר") && (((Notification) object).getType()).equals("שלח לך אתגר"))
                same = (this.time.equals(((Notification) object).time));// user can send as much challenges as he wants for now

        }

        return same;
    }

    public String getQuestion_content() {
        return question_content;
    }

    public ArrayList<String> getPossible_answers() {
        return possible_answers;
    }

    public String getRight_answer() {
        return right_answer;
    }
}