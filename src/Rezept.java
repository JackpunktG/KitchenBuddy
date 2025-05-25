import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class Rezept
{
    String Titel;
    String Kueche;
    int Schwerigkeit;
    int Zeit;
    String Methode;
    Zutat[] Zutaten = new Zutat[20];

    int[] Mealarten = new int[10];
    int rezeptID;


    Rezept(String titel, String kueche, int schwerigkeit, int zeit, String methode, String zutanten, String mealarten) {
        setRezept(titel, kueche, schwerigkeit, zeit, methode);
        setZutaten(zutanten);
        setMealarten(mealarten);
    }

    void setRezept(String titel, String kueche, int schwerigkeit, int zeit, String methode) {
        this.Titel = titel;
        this.Kueche = kueche;
        this.Schwerigkeit = schwerigkeit;
        this.Zeit = zeit;
        this.Methode = methode;
    }

    void setZutaten(String zutaten)         //Split up the ingredients
    {
        String[] zutat = zutaten.split("\\s*,\\s*");

        for (int i = 0; i < zutat.length; i++) {
            this.Zutaten[i] = new Zutat(zutat[i], this.Titel);      //zutaten objs save in arrays in Obj Rezept
        }
    }

    boolean checkBDzutaten()            // checking if all ingredients are in, and if only 1 is not then will return false
    {
        for (int i = 0; i < this.Zutaten.length && this.Zutaten[i] != null ; i++) {
            if (this.Zutaten[i].inDB == false) return false;
        }
        return true;
    }


    void setMealarten(String mealarten)
    {
        String[] typ = mealarten.split("\\s*,\\s*");

        for (int i = 0; i < typ.length; i++) {
            this.Mealarten[i] = Integer.parseInt(typ[i]);
        }
    }
    void print()
    {
        System.out.println(this.Titel);
        System.out.println(this.Kueche);
        System.out.println(this.Schwerigkeit);
        System.out.println(this.Zeit);
        System.out.println(this.Methode);
        System.out.println();

    }

    void inputRezept()
    {
        String url = "jdbc:mariadb://localhost:3306/kitchenbuddy";
        String user = "root";
        String password = "";



        String sql = "INSERT INTO Rezepte (Titel, Kueche, Schwerigkeit, Zeit, Methode)" +
                "VALUES (?, ?, ?, ?, ?)";

        try
                (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
            {
                pstmt.setString(1, this.Titel);
                pstmt.setString(2, this.Kueche);
                pstmt.setInt(3, this.Schwerigkeit);
                pstmt.setInt(4, this.Zeit);
                pstmt.setString(5, this.Methode);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
        }


        String[] columns = {"RezeptID"};

        DefaultTableModel model = new DefaultTableModel(columns, 0);
        sql = "SELECT RezeptID FROM rezepte WHERE Titel = ?";

        try
                (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
            {
                stmt.setString(1, this.Titel);

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Object[] row = {
                            rezeptID = Integer.parseInt(rs.getString("RezeptID")),
                    };
                    model.addRow(row);
                }
            }
        } catch (SQLException e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
        }

        int i = 0;
        while (Mealarten[i] != 0) {     //Loop to create the relationship table between the type of meal and recipe

            sql = "INSERT INTO rezeptarten (RezeptID, MealartID)" +
                    "VALUES (?, ?)     ";

            try
                    (Connection conn = DriverManager.getConnection(url, user, password);
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                {
                    pstmt.setInt(1, this.rezeptID);
                    pstmt.setInt(2, this.Mealarten[i]);

                    pstmt.executeUpdate();
                }
            } catch (SQLException e) {
                System.out.println("Connection failed.");
                e.printStackTrace();
            }
            i++;
        }


        i = 0;
        while (Zutaten[i] != null) {  //relationships wit recipe and ingredients

            sql = "INSERT INTO Rezept_Zutaten (RezeptID, ZutatID, Menge)" +
                    "VALUES (?, ?, ?)     ";

            try
                    (Connection conn = DriverManager.getConnection(url, user, password);
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                {
                    pstmt.setInt(1, this.rezeptID);
                    pstmt.setInt(2, this.Zutaten[i].ZutatID);
                    pstmt.setString(3, JOptionPane.showInputDialog("Menge für " + this.Zutaten[i].Zutat + " / " + this.Zutaten[i].Ingredient));

                    pstmt.executeUpdate();
                }
            } catch (SQLException e) {
                System.out.println("Connection failed.");
                e.printStackTrace();
            }
            i++;
        }

    }
}


