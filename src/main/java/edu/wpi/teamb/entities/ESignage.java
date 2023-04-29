package edu.wpi.teamb.entities;

import edu.wpi.teamb.DBAccess.DAO.Repository;
import edu.wpi.teamb.DBAccess.ORMs.LocationName;
import edu.wpi.teamb.DBAccess.ORMs.Node;
import edu.wpi.teamb.DBAccess.ORMs.Sign;

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

    public ArrayList<Sign> getAllSigns() {
        return Repository.getRepository().getAllSigns();
    }

    public ArrayList<String> getAllLongnames() {return Repository.getRepository().getAllLongNames();}

    public int[] getSignXandY(String signageGroup) {
        ArrayList<Sign> signs = Repository.getRepository().getAllSigns();
        for (Sign s : signs) {
            if (s.getSignageGroup().equals(signageGroup)) {
                String signLocation = s.getLocationName();
                int nodeID = Repository.getRepository().getNodeIDfromLongName(signLocation);
                Node n = Repository.getRepository().getNode(nodeID);
                return new int[] {n.getxCoord(), n.getyCoord()};
            }
        }
        return new int[] {0,0};
    }
}
