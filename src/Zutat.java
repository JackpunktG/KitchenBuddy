import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class Zutat
{
    String Zutat;
    int ZutatID;
    int ID;
    String Ingredient, Lebensmittelgroup, Season;
    int[] Einkaufstyp = new int[10];
    String rezeptTitel;
    boolean inDB = false;
    static int index = 1;
    static int zutatenCount;
    static String[][] alleZutaten = new String[500][2];



    Zutat(String zutat)
    {
        setZutat(zutat);
        System.out.println("Zutaten: " + this.Zutat + " ID: " + this.ID + " RecipeID: " + this.rezeptTitel);
        dbcheck();
        System.out.println("Is it in DB?: " + inDB);
        System.out.println();
    }

    Zutat(String zutat, String ingredient, String lebensmittelgroup, String season, String einkaufstypen)
    {
        this(zutat);
        this.Ingredient = ingredient;
        this.Lebensmittelgroup = lebensmittelgroup;
        this.Season = season;
        setEinkauftstyp(einkaufstypen);
        System.out.println(season + "  this.-->" + this.Season);

        for (int print : Einkaufstyp) System.out.println(print);

    }


        Zutat(String zutat, String RezeptTitel)           //with recipe
    {
        setZutat(zutat);
        this.rezeptTitel = RezeptTitel;
        this.ID = index;
        index++;
        System.out.println("Zutaten: " + this.Zutat + " ID: " + this.ID + " RecipeID: " + this.rezeptTitel);
        dbcheck();
        System.out.println("Is it in DB?: " + inDB);
        System.out.println();
    }

    void updateZutatfromRezeptInput(String zutat, String ingredient, String lebensmittelgroup, String season, String einkaufstypen)
    {
        setZutat(zutat);
        this.Ingredient = ingredient;
        this.Lebensmittelgroup = lebensmittelgroup;
        this.Season = season;
        setEinkauftstyp(einkaufstypen);
        System.out.println(season + "  this.-->" + this.Season);

        for (int print : Einkaufstyp) System.out.println(print);
    }

    void setEinkauftstyp(String einkaufstypen)
    {
        String[] typ = einkaufstypen.split("\\s*,\\s*");

        for (int i = 0; i < typ.length; i++) {
            this.Einkaufstyp[i] = Integer.parseInt(typ[i]);
        }
    }


    void setZutat(String zutat)
    {
        this.Zutat = zutat;
    }


    void dbcheck()
    {

        // Set the zutatenCount starting at 1 for ID

        for (int i = 0, j = 0; i < alleZutaten.length && alleZutaten[i][j] != null; i++) {
            if (this.Zutat.equalsIgnoreCase(alleZutaten[i][j++])) {
                inDB = true;
                ZutatID = i;
                break;
            }
            if (this.Zutat.equalsIgnoreCase(alleZutaten[i][j--])) {
                inDB = true;
                ZutatID = i;
                break;
            }
        }

    }


    static String[] createZutatenArrayFromString(String zutatenString) //Own attempted before i found out about the standard library function.. haha
    {
        char[] zutatenChar = new char[zutatenString.length()]; //Char array of string
        int index = 1;     //how many zutaten

        for (int i = 0; i < zutatenString.length(); i++){
            zutatenChar[i] = zutatenString.charAt(i);
            if (zutatenChar[i] == '.') index++;
        }

        String[] zutaten = new String[index];
        int index2 = 0;
        int offset = 0;
        int count = 0;

        for (int i = 0; i < zutatenChar.length; i++) {          //Split up the zutaten into String[]
            if (zutatenChar[i] == '.') {
                count = i - offset;      //finding the length of the zutat
                zutaten[index2++] = String.valueOf(zutatenChar, offset, count);
                offset = i + 1;         //finding the start of the next zutat
            }
            if (index2 == (index - 1)) {
                zutaten[index2] = String.valueOf(zutatenChar, offset, zutatenChar.length - offset);
                break;
            }
        }

        for (int j = 0; j < zutaten.length; j++) {          //Top and tails the empty spaces
            if (zutaten[j].charAt(0) == ' ') {              // deletes the empty spaces at the start
                for (int i = 0; i < zutaten[j].length(); i++)
                    if (zutaten[j].charAt(i) != ' ') {
                        zutaten[j] = zutaten[j].substring(i);
                        break;
                    }
            }
            if (zutaten[j].charAt(zutaten[j].length() - 1) == ' ') {        // deletes the empty spaces at the end
                for (int i = zutaten[j].length() - 1; i >= 0; i--) {
                    if (zutaten[j].charAt(i) != ' ') {
                        zutaten[j] = zutaten[j].substring(0, zutaten[j].length() - (zutaten[j].length() - i - 1));
                        break;
                    }
                }
            }
        }
        for (String j : zutaten) {
            System.out.println(j);
        }
        return zutaten;
    }



    static void popualteZutaten()
    {
        String url = "jdbc:mariadb://localhost:3306/kitchenbuddy";
        String user = "root";
        String password = "";
        zutatenCount = 0;

        String[] columns = {"Zutat", "Ingredient"};

        DefaultTableModel model = new DefaultTableModel(columns, 0);
        String sql = "SELECT Zutat, Ingredient FROM zutaten";

        try
                (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
            {

                ResultSet rs = stmt.executeQuery();

                int i = 0;
                int j = 0;
                while (rs.next()) {
                    Object[] row = {
                            alleZutaten[i][j++] = rs.getString("Zutat"),
                            alleZutaten[i++][j--] = rs.getString("Ingredient"),
                            zutatenCount++,
                    };
                    model.addRow(row);
                }
            }
        } catch (SQLException e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
        }
    }

    static void printZutaten()
    {
        for (String[] row : alleZutaten) {
            for (String zutat : row)
                if (zutat != null) System.out.println(zutat);
        }
        System.out.println(zutatenCount);

    }



    void inputZutaten()
    {
        String url = "jdbc:mariadb://localhost:3306/kitchenbuddy";
        String user = "root";
        String password = "";



        String sql = "INSERT INTO Zutaten (Zutat, Ingredient, Lebensmittelgruppe, Season)" +
                     "VALUES (?, ?, ?, ?)";

        try
                (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
            {
                pstmt.setString(1, this.Zutat);
                pstmt.setString(2, this.Ingredient);
                pstmt.setString(3, this.Lebensmittelgroup);
                if (this.Season == null || this.Season.isBlank()) {
                    pstmt.setNull(4, java.sql.Types.VARCHAR);
                } else pstmt.setString(4, this.Season);

                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
        }

        int i = 0;
        zutatenCount++;  // make correct ZutatID straight away for Einkaufen relationship insert

        while (Einkaufstyp[i] != 0) {           // Loop to make all the relationships between ingredient and shopping

            sql = "INSERT INTO Einkaufen (EinkaufstypID, ZutatID)" +
                    "VALUES (?, ?) ";

            try
                    (Connection conn = DriverManager.getConnection(url, user, password);
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                {
                    pstmt.setInt(1, Einkaufstyp[i]);
                    pstmt.setInt(2, zutatenCount);


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
