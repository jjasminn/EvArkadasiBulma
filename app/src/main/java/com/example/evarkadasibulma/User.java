package com.example.evarkadasibulma;

import android.widget.EditText;
import android.widget.Spinner;

import java.io.Serializable;

public class User implements Serializable {
    private String email,UID,department,studentClass,distance,duration,status,contact,photoUrl,name;
    private boolean userUpdated;



    public User(){
    }

    public User(String email,String UID){
        this.email=email;
        this.UID=UID;
        this.userUpdated=false;

    }
    public void UpdateUser(String name,String department,String studentClass,String distance,String duration,String status,String contact,String photoUrl){
        this.userUpdated=true;
        this.name=name;
        this.contact=contact;
        this.department=department;
        this.distance=distance;
        this.studentClass=studentClass;
        this.duration=duration;
        this.status=status;
        this.photoUrl=photoUrl;
    }
    public String getUid() {
        return UID;
    }

    public String getEmail() {
        return email;
    }
    public String getContact() {
        return contact;
    }
    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public String getDistance() {
        return distance;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public String getDuration() {
        return duration;
    }

    public String getStatus() {
        return status;
    }
    public void setUID(String UID){
        this.UID = UID;
    }
    public boolean isUserUpdated() {
        return userUpdated;
    }


}
