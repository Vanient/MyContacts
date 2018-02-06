package com.example.vanient.mycontacts;

/**
 * Created by Vanient on 2018/2/3.
 */


  /*  public boolean isChecked =false;*/

public class Group {
    public String groupName,phNo,phDisplayName,phType,email;
    public String groupid;
    public int groupNumber;


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupID() {
        return groupid;
    }

    public void getGroupID(String GroupID) {
        this.groupid = GroupID;
    }

    public int getGroupNumber(){return groupNumber;}

    public void setGroupNumber(int groupNumber){this.groupNumber =groupNumber;}


}
