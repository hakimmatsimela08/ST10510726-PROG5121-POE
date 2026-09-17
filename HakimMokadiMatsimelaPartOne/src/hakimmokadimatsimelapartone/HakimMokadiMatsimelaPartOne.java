/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hakimmokadimatsimelapartone;
import java.util.Scanner;
/**
 *
 * @author user1
 */
public class HakimMokadiMatsimelaPartOne{
      //A simple console app that takes input and returns output, letting users register and then log in.
    public static void main(String[] args) {
       
        //Declarations
        String name, surname, username, password, cellPhone;
 
        // Create a scanner object that will handle user input
        Scanner scanner = new Scanner(System.in);
        Login user = new Login();
        
        //prompt the user to enter their name ,surname ,username ,password and SA cellphone number
        System.out.println("===User Registration===");
 
        System.out.println("Enter your first name ");
        name = scanner.nextLine();
 
        System.out.println("Enter your surname");
        surname = scanner.nextLine();
 
           do { 

            System.out.print("Please enter your username: "); 

            username = scanner.nextLine(); 

  

            if (user.checkUserName(username)) { 

                System.out.println("Username successfully captured."); 

            } else { 

                System.out.println("Username is not correctly formatted; please ensure that " 

                        + "your username contains an underscore and is no more than five " 

                        + "characters in length."); 

            } 

        } while (!user.checkUserName(username)); 

  

        do { 

            System.out.print("Please enter your password: "); 

            password = scanner.nextLine(); 

  

            if (user.checkPasswordComplexity(password)) { 

                System.out.println("Password successfully captured."); 

            } else { 

                System.out.println("Password is not correctly formatted; please ensure that " 

                        + "the password contains at least eight characters, a capital letter, " 

                        + "a number, and a special character."); 

            } 

        } while (!user.checkPasswordComplexity(password)); 

  

        do { 

            System.out.print("Please enter your South African cell phone number (e.g. +27838968976): "); 

            cellPhone = scanner.nextLine(); 

  

            if (user.checkCellPhoneNumber(cellPhone)) { 

                System.out.println("Cell phone number successfully added."); 

            } else { 

                System.out.println("Cell phone number is incorrectly formatted or does not contain " 

                        + "an international code; please correct the number and try again."); 

            } 

        } while (!user.checkCellPhoneNumber(cellPhone)); 

  

        String registrationResult = user.registerUser(name, surname, username, password, cellPhone); 

        System.out.println(registrationResult); 

  

        System.out.println(); 

        System.out.println("=== User Login ==="); 

  

        boolean loginSuccessful; 

  

        do { 

            System.out.print("Please enter your username: "); 

            String loginUsername = scanner.nextLine(); 

  

            System.out.print("Please enter your password: "); 

            String loginPassword = scanner.nextLine(); 

  

            loginSuccessful = user.loginUser(loginUsername, loginPassword); 

            System.out.println(user.returnLoginStatus(loginSuccessful)); 

  

        } while (!loginSuccessful); 

  

        scanner.close(); 

    } 

} 