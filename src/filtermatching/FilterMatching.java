/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filtermatching;

//import static java.lang.Math.max;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Scanner;


/**
 *
 * @author KEERTHANA
 */
public class FilterMatching {

    /**
     * @param args the command line arguments
     */
    
   
    public static void main(String[] args) {
       int lat,longi,minBudget,maxBudget,minBedroom,maxBedroom,minBathroom,maxBathroom;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the latitude:");
        lat = sc.nextInt();
        System.out.println("Enter the longitude:");
        longi = sc.nextInt();
        System.out.println("Enter the minimum budget:");
        minBudget = sc.nextInt();
        System.out.println("Enter the maximum budget:");
        maxBudget = sc.nextInt();
        System.out.println("Enter the minimum bedroom:");
        minBedroom = sc.nextInt();
        System.out.println("Enter the maximum bedroom:");
        maxBedroom = sc.nextInt();
        System.out.println("Enter the minimum bathroom:");
        minBathroom = sc.nextInt();
        System.out.println("Enter the maximum bathroom:");
        maxBathroom = sc.nextInt();


        
        int qMaxBudget = (int) (maxBudget + (0.25*maxBudget));
        int qMinBudget = (int) Math.max(minBudget - (0.25*minBudget),0);
        int qMinBedroom = Math.max(minBedroom - 2 ,0);
        int qMaxBedroom = maxBedroom + 2;
        int qMinBathroom = Math.max(minBathroom - 2 ,0);
        int qMaxBathroom = maxBathroom + 2;
        
        try{
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/seller","test","test"); 
            Statement st = con.createStatement();
            String sql = "Select id, (3959 * acos ( cos ( radians("+lat +")) * cos( radians( latitude) ) * cos( radians(longitude ) - radians("+ longi +" ))  + sin ( radians("+lat+") ) * sin( radians( latitude ) ) )) as distanceBetween,price,bedroom,bathroom from seller where (3959 * acos ( cos ( radians( "+lat +")) * cos( radians( latitude) ) * cos( radians(longitude ) - radians("+ longi +" ))  + sin ( radians("+lat+") ) * sin( radians( latitude ) ) ) <= 10) AND (price >= "+qMinBudget+" AND price <= "+qMaxBudget+") AND (bedroom >= "+qMinBedroom+" AND bedroom <= "+qMaxBedroom+" ) AND (bathroom >="+ qMinBathroom+" and bathroom <="+ qMaxBathroom+ " ) ";
//            String sql = "SELECT * FROM seller";
            System.out.println(sql);
            ResultSet rs = st.executeQuery(sql);
            
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnNumber = rsmd.getColumnCount();
            
            while(rs.next()){
                float percentage = 0;
                /*String distancesql = "select id,(3959 * acos ( cos ( radians(lat) "+lat +") * cos( radians( latitude) ) * cos( radians(longitude ) - radians(longi)"+ longi +" )  + sin ( radians(lat)"+lat+" ) * sin( radians( latitude ) )) AS Distance FROM seller HAVING Distance <10 ORDER BY Distance LIMIT 0,10 ";
                ResultSet distanceBetween = st.executeQuery(distancesql);*/
                
                //Distance percentage Calculation
                int distanceBetween = rs.getInt(1);
                if(distanceBetween <= 2){
                    percentage+=30;
                }else{
                    percentage+=(10-distanceBetween)*3.75;
                }
                //System.out.println("Distance - "+percentage);
                
                //Budget percentage Calculation
                if(maxBudget==0 || minBudget==0){
                    maxBudget = Math.max(minBudget,maxBudget);
                    minBudget = (int) Math.max(0,minBudget - (0.10 * minBudget));
                    maxBudget = (int) (maxBudget + (0.10 * maxBudget));
                    
                }
                int price = rs.getInt(3);
                //System.out.println("Price - "+price+" Min - "+minBudget+" Max - "+maxBudget);
                    if(price>=minBudget && price<=maxBudget){
                        percentage += 30;
                    }else {
                        if(price<minBudget){
                        percentage += ((price - qMinBudget)/minBudget) * 100 * 1.2;
                    }else{
                        percentage += ((qMaxBudget - price)/maxBudget) * 100 * 1.2;
                    }
                   }
                //System.out.println("Budget - "+percentage);
                
                //Bedroom Percentage calculation
                if(maxBedroom!=0||minBedroom!=0){
                    maxBedroom= Math.max(minBedroom,maxBedroom);
                    minBedroom= (int) Math.max(0,(maxBedroom - (2)));
                    maxBedroom= (int) (maxBedroom+ (2));
                    
                }
                int bedroom = rs.getInt(4);
//                System.out.println("No of bedroom - "+bedroom+" Min - "+minBedroom+" Max - "+maxBedroom);
                    if(bedroom>=minBedroom&&bedroom<=maxBedroom){
                        percentage+=20;
                    }else{ 
                        if(bedroom<minBedroom){
                        percentage += ((bedroom - qMinBedroom)/(minBedroom - qMinBedroom))*20;
                    }else{
                        percentage += ((qMaxBedroom - bedroom)/(qMaxBedroom - maxBedroom))*20;
                    }
                    }
                //System.out.println("Bedroom - "+percentage);
                
                //Bathroom percentage Calcualtion
                if(maxBathroom!=0||minBathroom!=0){
                    maxBathroom= Math.max(minBathroom,maxBathroom);
                    minBathroom= (int) Math.max(0,(maxBathroom - (2)));
                    maxBathroom= (int) (maxBathroom+ (2));
                }
                int bathroom = rs.getInt(5);
                    if(bathroom>=minBathroom && bathroom<=maxBathroom){
                        percentage+=20;
                    }else{
                        if(bathroom<minBathroom){
                        percentage += ((bathroom - qMinBathroom)/(minBathroom - qMinBathroom))*20;
                    }else{
                        percentage += ((qMaxBathroom - bathroom)/(qMaxBathroom - maxBathroom))*20;
                    }
                   }
                
                System.out.println("Total Property Percentage - "+percentage);
                
                if(percentage>=40){
                    for(int i=1;i<=columnNumber;i++){
                        if(i>1)
                            System.out.println(", ");
                        
                        String ColumnValue = rs.getString(i);
                        System.out.print(ColumnValue+" "+rsmd.getColumnName(i));
                   
                    }
     
                    System.out.println(" ");
                }
                
                
            }
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
    }
    
}

