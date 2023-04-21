package edu.wpi.teamb.DBAccess.ORMs;

import edu.wpi.teamb.DBAccess.DButils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MealRequest {
  private int id;
  private String orderFrom;
  private String food;
  private String drink;
  private String snack;
  private String mealModification;

  public MealRequest() {
    this.id = 0;
    this.orderFrom = "";
    this.food = "";
    this.drink = "";
    this.snack = "";
    this.mealModification = "";
  }

  public MealRequest(int id, String orderFrom, String food, String drink, String snack, String mealModification) {
    this.id = id;
    this.orderFrom = orderFrom;
    this.food = food;
    this.drink = drink;
    this.snack = snack;
    this.mealModification = mealModification;
  }

  public MealRequest(ResultSet rs) throws java.sql.SQLException {
    this(
      rs.getInt("id"),
      rs.getString("orderFrom"),
      rs.getString("food"),
      rs.getString("drink"),
      rs.getString("snack"),
      rs.getString("mealModification")
    );
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getOrderFrom() {
    return orderFrom;
  }

  public void setOrderFrom(String orderFrom) {
    this.orderFrom = orderFrom;
  }

  public String getFood() {
    return food;
  }

  public void setFood(String food) {
    this.food = food;
  }

  public String getDrink() {
    return drink;
  }

  public void setDrink(String drink) {
    this.drink = drink;
  }

  public String getSnack() {
    return snack;
  }

  public void setSnack(String snack) {
    this.snack = snack;
  }

  public String getMealModification() {
    return mealModification;
  }

  public void setMealModification(String mealModification) {
    this.mealModification = mealModification;
  }

  public static MealRequest getMealRequest(int id) {
    ResultSet rs = DButils.getRowCond("MealRequests", "*", "id = '" + id + "'");
    try {
      if (rs.isBeforeFirst()) {
        rs.next();
        return new MealRequest(rs);
      } else
        throw new SQLException("No rows found");
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }


}
