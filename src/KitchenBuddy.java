import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class KitchenBuddy
{
    public static void main(String[] args)
    {

        String url = "jdbc:mariadb://localhost:3306/kitchenbuddy";
        String user = "root";
        String password = "";

        while (true) {
            String[] optionMain = {"View", "Input", "Exit"};
            String select = (String) JOptionPane.showInputDialog(
                    null,
                    "Was willst du tun?",
                    "Main menu",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    optionMain,
                    optionMain[0]
            );

            if ("Exit".equals(select)) return;

            if ("Input".equals(select)) {
                Zutat.popualteZutaten();                        //Populates the array in Zutat with all the current ingredients, to have ease of making the relationships and to reference later
                //Zutat.printZutaten();   // if you want to print them

                String[] optionInput = {"Zutaten", "Rezept"};                //INPUT mode
                String inputView = (String) JOptionPane.showInputDialog(
                        null,
                        "Input where:",
                        "Input Mode",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        optionInput,
                        optionInput[0]
                );
                if ("Zutaten".equals(inputView)) {   //Ingredients


                    JTextField Zutat = new JTextField(50);
                    JTextField Ingredient = new JTextField(50);
                    JTextField Lebensmittelgroup = new JTextField(50);
                    JTextField Season = new JTextField(50);
                    JTextField Einkaufstyp = new JTextField(50);


                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.add(new JLabel("Zutat:"));
                    panel.add(Zutat);
                    panel.add(Box.createVerticalStrut(15)); // spacing
                    panel.add(new JLabel("Ingredient:"));
                    panel.add(Ingredient);
                    panel.add(Box.createVerticalStrut(15)); // spacing
                    panel.add(new JLabel("Lebensmittelgroup"));
                    panel.add(Lebensmittelgroup);
                    panel.add(Box.createVerticalStrut(15)); // spacing
                    panel.add(new JLabel("Season"));
                    panel.add(Season);
                    panel.add(Box.createVerticalStrut(15)); // spacing
                    panel.add(new JLabel("<html>"
                            + "Einkaufstyp:<br>"                        // ease of putting the relationships tables together
                            + "1 - Lebensmittelladen   &emsp  2 - BIOmarkt   &emsp  3- Backieri    &emsp 4 - Asienladen<br>"
                            + "5 - Metzger   &emsp  6 - Käseladen  &emsp   7 - Delikatessen   &emsp  8 - Ganz Spezielleläden<br>"
                            + "9 - arabischer supermarkt  &emsp   10 - Fischhändler<br>"
                            + "Gib einfach die Nummer ein &emsp&emsp   geteilt beim ein ','<br>"
                            + "</html>"));
                    panel.add(Einkaufstyp);
                    panel.add(Box.createVerticalStrut(15)); // spacing


                    int result = JOptionPane.showConfirmDialog(
                            null,
                            panel,
                            "Enter your info",
                            JOptionPane.OK_CANCEL_OPTION
                    );

                    Zutat zu1 = null;
                    if (result == JOptionPane.OK_OPTION) {
                        zu1 = new Zutat(Zutat.getText(), Ingredient.getText(), Lebensmittelgroup.getText(), Season.getText(), Einkaufstyp.getText());
                    }

                    if (zu1.inDB == false) {
                        zu1.inputZutaten();
                    } else System.out.println(zu1.Zutat + "Zutan gibt's schon");            // if the ingredient already exists

                }

                if ("Rezept".equals(inputView)) {               //recipe


                    JTextField Titel = new JTextField(50);
                    JTextField Kueche = new JTextField(50);
                    JTextField Schwerigkeit = new JTextField(50);
                    JTextField Zeit = new JTextField(50);
                    JTextField Zutaten = new JTextField(50);
                    JTextField Mealarten = new JTextField(50);
                    JTextField Methode = new JTextField(50);

                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.add(new JLabel("Titel:"));
                    panel.add(Titel);
                    panel.add(Box.createVerticalStrut(15)); // spacing
                    panel.add(new JLabel("Keuche:"));
                    panel.add(Kueche);
                    panel.add(Box.createVerticalStrut(15)); // spacing
                    panel.add(new JLabel("Schwerigket Zwischen 1 - 5:"));
                    panel.add(Schwerigkeit);
                    panel.add(Box.createVerticalStrut(15)); // spacing
                    panel.add(new JLabel("Zeit Zwischen 1 - 5:"));
                    panel.add(Zeit);
                    panel.add(Box.createVerticalStrut(15)); // spacing
                    panel.add(new JLabel("Zutaten geteilt beim ein '.'"));
                    panel.add(Zutaten);
                    panel.add(Box.createVerticalStrut(15)); // spacing
                    panel.add(new JLabel("<html>"
                            + "Mealart:<br>"
                            + "1 - Vegan   &emsp  2 - Vegetarisch   &emsp  3- Meal Prep    &emsp 4 - Desserts / Süßspeisen &emsp 5 - Comfort Food<br>"
                            + "Gib einfach die Nummer ein &emsp&emsp   geteilt beim ein ','<br>"
                            + "</html>"));
                    panel.add(Mealarten);
                    panel.add(Box.createVerticalStrut(15)); // spacing
                    panel.add(new JLabel("Methode:"));
                    JTextArea methodeArea = new JTextArea(6, 40); // 6 rows, 40 columns
                    methodeArea.setLineWrap(true);
                    methodeArea.setWrapStyleWord(true);
                    JScrollPane methodeScroll = new JScrollPane(methodeArea);
                    panel.add(methodeScroll);


                    int result = JOptionPane.showConfirmDialog(
                            null,
                            panel,
                            "Enter your info",
                            JOptionPane.OK_CANCEL_OPTION
                    );

                    Rezept re1 = null;
                    if (result == JOptionPane.OK_OPTION) {
                        re1 = new Rezept(Titel.getText(), Kueche.getText(), Integer.parseInt(Schwerigkeit.getText()),
                                Integer.parseInt(Zeit.getText()), methodeArea.getText(), Zutaten.getText(), Mealarten.getText());
                    }
                    if (re1.checkBDzutaten() == true) {
                        re1.inputRezept();              // if all the ingredients already exist in the DB
                    } else {
                        int i = 0;
                        while (re1.Zutaten[i] != null) {                // if not, loop in input all the missing ingredients
                            if (re1.Zutaten[i].inDB == false) {


                                JTextField zutat = new JTextField(50);
                                JTextField Ingredient = new JTextField(50);
                                JTextField Lebensmittelgroup = new JTextField(50);
                                JTextField Season = new JTextField(50);
                                JTextField Einkaufstyp = new JTextField(50);


                                panel = new JPanel();
                                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                                panel.add(new JLabel("<html>"
                                        + "Input info: " + re1.Zutaten[i].Zutat + " / " + re1.Zutaten[i].Ingredient
                                        + "Zutat:<br>"
                                        + "</html>"));
                                panel.add(zutat);


                                panel.add(Box.createVerticalStrut(15)); // spacing
                                panel.add(new JLabel("Ingredient:"));
                                panel.add(Ingredient);
                                panel.add(Box.createVerticalStrut(15)); // spacing
                                panel.add(new JLabel("Lebensmittelgroup"));
                                panel.add(Lebensmittelgroup);
                                panel.add(Box.createVerticalStrut(15)); // spacing
                                panel.add(new JLabel("Season"));
                                panel.add(Season);
                                panel.add(Box.createVerticalStrut(15)); // spacing
                                panel.add(new JLabel("<html>"
                                        + "Einkaufstyp:<br>"
                                        + "1 - Lebensmittelladen   &emsp  2 - BIOmarkt   &emsp  3- Backieri    &emsp 4 - Asienladen<br>"
                                        + "5 - Metzger   &emsp  6 - Käseladen  &emsp   7 - Delikatessen   &emsp  8 - Ganz Spezielleläden<br>"
                                        + "9 - arabischer supermarkt  &emsp   10 - Fischhändler<br>"
                                        + "Gib einfach die Nummer ein &emsp&emsp   geteilt beim ein ','<br>"
                                        + "</html>"));
                                panel.add(Einkaufstyp);
                                panel.add(Box.createVerticalStrut(15)); // spacing


                                result = JOptionPane.showConfirmDialog(
                                        null,
                                        panel,
                                        "Enter your info",
                                        JOptionPane.OK_CANCEL_OPTION
                                );

                                if (result == JOptionPane.OK_OPTION) {
                                    re1.Zutaten[i].updateZutatfromRezeptInput(zutat.getText(), Ingredient.getText(), Lebensmittelgroup.getText(), Season.getText(), Einkaufstyp.getText());
                                }
                                re1.Zutaten[i].inputZutaten();
                                Zutat.popualteZutaten();            // reupdating so give correct ID's
                                re1.Zutaten[i].dbcheck();
                            }
                            i++;
                        }
                       re1.inputRezept();
                    }
                }
            }


            if ("View".equals(select)) {                // VIEW MODE
                String[] optionView = {"Alle Rezepte", "Alle Zutaten", "Single Rezept - Cooking mode :)"};
                String selectView = (String) JOptionPane.showInputDialog(
                        null,
                        "Was willst du sehen?",
                        "View Mode",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        optionView,
                        optionView[0]
                );

                if ("Alle Rezepte".equals(selectView)) {
                    String[] columns = {"RezeptID", "Titel", "Kueche", "Schwerigkeit", "Zeit", "Methode"};

                    DefaultTableModel model = new DefaultTableModel(columns, 0);
                    String sql = "SELECT * FROM rezepte";
                    try
                            (Connection conn = DriverManager.getConnection(url, user, password);
                             PreparedStatement stmt = conn.prepareStatement(sql)) {
                        {

                            ResultSet rs = stmt.executeQuery();


                            while (rs.next()) {
                                Object[] row = {
                                        rs.getInt("RezeptID"),
                                        rs.getString("Titel"),
                                        rs.getString("Kueche"),
                                        rs.getInt("Schwerigkeit"),
                                        rs.getInt("Zeit"),
                                        rs.getString("Methode"),
                                };
                                model.addRow(row);
                            }
                            JTable table = new JTable(model) {
                                // Make cells wrap text
                                @Override
                                public TableCellRenderer getCellRenderer(int row, int column) {
                                    return new JTextAreaRenderer();
                                }
                            };

                            table.getColumnModel().getColumn(0).setPreferredWidth(80); // First column
                            table.getColumnModel().getColumn(1).setPreferredWidth(200); // Second column
                            table.getColumnModel().getColumn(2).setPreferredWidth(100); // 3
                            table.getColumnModel().getColumn(3).setPreferredWidth(80); // 4
                            table.getColumnModel().getColumn(4).setPreferredWidth(80); // 5
                            table.getColumnModel().getColumn(5).setPreferredWidth(960); //  6
                            table.setRowHeight(100);
                            JScrollPane scrollPane = new JScrollPane(table);
                            scrollPane.setPreferredSize(new Dimension(1500, 500));

                            JOptionPane.showMessageDialog(null, scrollPane, "What will you pick", JOptionPane.INFORMATION_MESSAGE);


                        }

                    } catch (SQLException e) {
                        System.out.println("Connection failed.");
                        e.printStackTrace();
                    }
                }

                if ("Alle Zutaten".equals(selectView)) {
                    String[] columns = {"ZutatID", "Zutat", "Ingredient", "Lebensmittelgruppe", "Season"};

                    DefaultTableModel model = new DefaultTableModel(columns, 0);
                    String sql = "SELECT * FROM zutaten ORDER BY Zutat";
                    try
                            (Connection conn = DriverManager.getConnection(url, user, password);
                             PreparedStatement stmt = conn.prepareStatement(sql)) {
                        {

                            ResultSet rs = stmt.executeQuery();


                            while (rs.next()) {
                                Object[] row = {
                                        rs.getInt("ZutatID"),
                                        rs.getString("Zutat"),
                                        rs.getString("Ingredient"),
                                        rs.getString("Lebensmittelgruppe"),
                                        rs.getString("Season"),
                                };
                                model.addRow(row);
                            }
                            JTable table = new JTable(model) {
                                // Make cells wrap text
                                @Override
                                public TableCellRenderer getCellRenderer(int row, int column) {
                                    return new JTextAreaRenderer();
                                }
                            };
                            table.setRowHeight(25);
                            JScrollPane scrollPane = new JScrollPane(table);
                            scrollPane.setPreferredSize(new Dimension(800, 800));

                            JOptionPane.showMessageDialog(null, scrollPane, "WOOWW sehr viel zutaten!!", JOptionPane.INFORMATION_MESSAGE);


                        }

                    } catch (SQLException e) {
                        System.out.println("Connection failed.");
                        e.printStackTrace();
                    }
                }

                if ("Single Rezept - Cooking mode :)".equals(selectView)) {         // To view only one selected recipe for your cooking experience
                    String search = JOptionPane.showInputDialog("Gib Titel an");
                    String[] columns = {"Titel", "Zutaten", "Methode"};

                    DefaultTableModel model = new DefaultTableModel(columns, 0);
                    String sql = "SELECT Titel, Zutaten, Methode FROM cookingtime WHERE Titel = ?";
                    try
                            (Connection conn = DriverManager.getConnection(url, user, password);
                             PreparedStatement stmt = conn.prepareStatement(sql)) {
                        {
                            stmt.setString(1, search);
                            ResultSet rs = stmt.executeQuery();


                            while (rs.next()) {
                                Object[] row = {
                                        rs.getString("Titel"),
                                        rs.getString("Zutaten"),
                                        rs.getString("Methode"),
                                };
                                model.addRow(row);
                            }
                            JTable table = new JTable(model) {
                                // Make cells wrap text
                                @Override
                                public TableCellRenderer getCellRenderer(int row, int column) {
                                    return new JTextAreaRenderer();
                                }
                            };
                            table.getColumnModel().getColumn(0).setPreferredWidth(200); // First column
                            table.getColumnModel().getColumn(1).setPreferredWidth(400); // Second column
                            table.getColumnModel().getColumn(2).setPreferredWidth(900); // 3

                            table.setRowHeight(475);
                            JScrollPane scrollPane = new JScrollPane(table);
                            scrollPane.setPreferredSize(new Dimension(1500, 500));

                            JOptionPane.showMessageDialog(null, scrollPane, "Ready.. steady... COOK!!", JOptionPane.INFORMATION_MESSAGE);
                        }

                    } catch (SQLException e) {
                        System.out.println("Connection failed.");
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

