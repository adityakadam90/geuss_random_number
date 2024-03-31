import java.util.Random;
import java.sql.*;
import java.util.Scanner;

public class game {
    public static void main(String []args) {
        String url = "jdbc:mysql://localhost:3306/random";
        String password = "@#aditya2006";
        String username = "root";

        Scanner sc = new Scanner(System.in);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Connection cn = DriverManager.getConnection(url,username,password);
            System.out.println();
            System.out.println("-------------- Welcome to guessing number game -------------");
            System.out.println();
            while(true) {
                System.out.println();
                System.out.print("enter chioce\n1.create account in game.\n2.play game.\n3.check your score\n4.exit\n : ");
                System.out.println();
                int choice = sc.nextInt();
                switch (choice) {
                    case 1:createAccount(cn,sc);
                        break;
                    case 2:playGame(cn,sc);
                        break;
                    case 3:chScore(cn,sc);
                    break;
                    case 4:return;
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public static void chScore(Connection cn,Scanner sc) {
        System.out.println();
        System.out.println("enter your id : ");
        int id = sc.nextInt();

        System.out.println();

        try {
            Statement st = cn.createStatement();
            String query = String.format("select won_game,lost_game,name from r where id = %d",id);
            ResultSet rs = st.executeQuery(query);
            if(rs.next()) {
                //System.out.println();
                int w = rs.getInt("won_game");
                int l = rs.getInt("lost_game");
                System.out.println("Hiiiiiiiiiiiii "+rs.getString("name"));
                System.out.println("your total playing's  is = "+(w+l) +"\nyour won games is = "+w+"\nyour lost game is = "+l);
            }else {
                System.out.println("this id cannot having account in game.");
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }
    public static void createAccount(Connection cn,Scanner sc) {
        String query = "insert into r(name) values(?)";
        try {
            PreparedStatement ps = cn.prepareStatement(query);
            System.out.println();
            System.out.print("enter your name : ");
            String name = sc.next();
            ps.setString(1,name);
            int afr = ps.executeUpdate();
            if(afr > 0){
                System.out.println("account created");
                try {
                    String q = String.format("select id from r where name = '%s';",name);
                    Statement st = cn.createStatement();
                    ResultSet rs = st.executeQuery(q);
                    if(rs.next()) {
                        System.out.println();
                        System.out.println("your game is id = "+rs.getString("id"));
                        System.out.println();
                    }
                }catch (SQLException e) {
                    System.out.println("error at create statement");
                }
            }else {
                System.out.println("account not created");
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static boolean idExists(Connection cn,int id) {
        try {
            String query = "select id from r where id = ?;";
            PreparedStatement ps = cn.prepareStatement(query);
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            //int tid = rs.getInt("id");
            if(rs.next()) {
                return true;
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void playGame(Connection cn,Scanner sc) {
        System.out.println();
        System.out.println("enter your id : ");
        int id = sc.nextInt();
        if(idExists(cn,id)){
            System.out.println("okk then start game");
            System.out.println();
            Random r = new Random();
            System.out.print("enter your range (1 to 100) this way.\n enter highest.");
            System.out.print("difference in low = 1 & high minimum greater than 50 : ");
            //int low = sc.nextInt();
            int high = sc.nextInt();
            int rnum = r.nextInt(high) + 1;
            System.out.println();
            System.out.println("number had created.");
            System.out.println();
            System.out.println("you taking only 10 attemts");
            System.out.println();
            int i = 1;
            while (i <= 10) {
                System.out.print("ok! please guess the number : ");
                int guessNumber = sc.nextInt();
                if (guessNumber == rnum) {
                    System.out.println("Congrats!!  you guess correct number.");
                    System.out.println("you taking " + i + " attemts.");
                    System.out.println();
                    try {
                        String query = "update r set won_game = ? where id = ?;";
                        PreparedStatement ps = cn.prepareStatement(query);
                        int prewons = getting_wongame_count(cn,id);
                        ps.setInt(1,prewons+1);
                        ps.setInt(2,id);
                        ps.executeUpdate();
                    }catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                if (guessNumber > rnum + 10) {
                    System.out.println("this number is very high as compare to generated number!");
                } else if (guessNumber < rnum - 10) {
                    System.out.println("this number is very low as compare to generated number!");
                } else if (guessNumber >= rnum + 1 || guessNumber >= rnum + 2 || guessNumber >= rnum + 3 || guessNumber >= rnum + 4 || guessNumber >= rnum + 5) {
                    System.out.println("so close.but this number small high as compare to generated number!");
                } else if (guessNumber <= rnum - 1 || guessNumber <= rnum - 2 || guessNumber <= rnum - 3 || guessNumber <= rnum - 4 || guessNumber <= rnum - 590) {
                    System.out.println("so close.but this number small less as compare to generated number!");
                }
                if (i == 10) {
                    System.out.println("opps!!!!! you failed.");
                    try {
                        String query = "update r set lost_game = ? where id = ?;";
                        PreparedStatement ps = cn.prepareStatement(query);
                        int preLost = getting_lostgame_count(cn,id);
                        ps.setInt(1,preLost+1);
                        ps.setInt(2,id);
                        ps.executeUpdate();
                    }catch (SQLException e) {
                        e.printStackTrace();
                    }
                    System.out.println("genearated number is = " + rnum);
                }
                i++;
            }
        }
        else {
            System.out.println("id can't exist");
        }
    }
    public static int getting_wongame_count(Connection cn,int id) {
        String query = String.format("select won_game from r where id = %d",id);
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(query);
            if(rs.next()) {
                int wonC = rs.getInt("won_game");
                return wonC;
            }
        }catch(SQLException e) {
            System.out.println("error at statement");
        }
        return 0;
    }
    public static int getting_lostgame_count(Connection cn,int id) {
        String query = String.format("select lost_game from r where id = %d",id);
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(query);
            if(rs.next()) {
                int lostC = rs.getInt("lost_game");
                return lostC;
            }
        }catch(SQLException e) {
            System.out.println("error at statement");
        }
        return 0;
    }
}
