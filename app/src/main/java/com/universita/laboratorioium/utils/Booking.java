package com.universita.laboratorioium.utils;

import java.io.Serializable;

public class Booking implements Serializable {
    int id;
    Teacher teacher;
    Course course;
    User user;
    int day;
    String hour;
    String status;

    public Booking(int id, Teacher teacher, Course course, User user, int day, String hour, String status) {
        this.id = id;
        this.teacher = teacher;
        this.course = course;
        this.user = user;
        this.day = day;
        this.hour = hour;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public Course getCourse(){
        return course;
    }

    public User getUser() {
        return user;
    }

    public int getDay() {
        return day;
    }

    public String getHour() {
        return hour;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", teacher=" + teacher +
                ", course=" + course +
                ", user=" + user +
                ", day=" + day +
                ", hour='" + hour + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
