package edu.wpi.teamb.entities;

import edu.wpi.teamb.DBAccess.DAO.Repository;

import java.util.ArrayList;
import java.util.HashSet;

public class ESignage {

    public ESignage () {

    }

    public HashSet<String> getSignageGroups() {
        //replace with repository function
        return Repository.getRepository().getSignageGroups();
    }

    public ArrayList<String> getLocationNames(String signageGroup) {
        return Repository.getRepository().getLocationNames(signageGroup);
    }
}
