/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hakimmokadimatsimelapartone; 

  

public class Login { 

  

    private String name; 

    private String surname; 

    private String userName; 

    private String password; 

    private String cellPhone; 

  

    // Checks that the username contains an underscore and is no more than 5 characters. 

    public boolean checkUserName(String userName) { 

        return userName != null 

                && userName.contains("_") 

                && userName.length() <= 5; 

    } 

  

    // Checks that the password is at least 8 characters and contains 

    // a capital letter, a number, and a special character. 

    public boolean checkPasswordComplexity(String password) { 

        if (password == null || password.length() < 8) { 

            return false; 

        } 

  

        boolean hasCapitalLetter = false; 

        boolean hasNumber = false; 

        boolean hasSpecialCharacter = false; 

  

        for (int i = 0; i < password.length(); i++) { 

            char ch = password.charAt(i); 

  

            if (Character.isUpperCase(ch)) { 

                hasCapitalLetter = true; 

            } else if (Character.isDigit(ch)) { 

                hasNumber = true; 

            } else if (!Character.isLetterOrDigit(ch)) { 

                hasSpecialCharacter = true; 

            } 

        } 

  

        return hasCapitalLetter && hasNumber && hasSpecialCharacter; 

    } 

  

    // Regex reference: Oracle Java SE 17 Pattern documentation. 

    // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/regex/Pattern.html 

    // South African international format used here: +27 followed by 9 digits. 

    public boolean checkCellPhoneNumber(String cellPhoneNo) { 

        if (cellPhoneNo == null) { 

            return false; 

        } 

  

        String regex = "^\\+27\\d{9}$"; 

        return cellPhoneNo.matches(regex); 

    } 

  

    // Validates and stores the registration details. 

    public String registerUser(String name, String surname, String userName, 

                               String password, String cellPhoneNo) { 

  

        if (!checkUserName(userName)) { 

            return "Username is not correctly formatted; please ensure that your username " 

                    + "contains an underscore and is no more than five characters in length."; 

        } 

  

        if (!checkPasswordComplexity(password)) { 

            return "Password is not correctly formatted; please ensure that the password " 

                    + "contains at least eight characters, a capital letter, a number, " 

                    + "and a special character."; 

        } 

  

        if (!checkCellPhoneNumber(cellPhoneNo)) { 

            return "Cell phone number is incorrectly formatted or does not contain an " 

                    + "international code; please correct the number and try again."; 

        } 

  

        this.name = name; 

        this.surname = surname; 

        this.userName = userName; 

        this.password = password; 

        this.cellPhone = cellPhoneNo; 

  

        return "The user has been registered successfully."; 

    } 

  

    // Verifies that login details match the details stored during registration. 

    public boolean loginUser(String userName, String password) { 

        return this.userName != null 

                && this.password != null 

                && this.userName.equals(userName) 

                && this.password.equals(password); 

    } 

  

    // Returns the required successful or failed login message. 

    public String returnLoginStatus(boolean loginSuccessful) { 

        if (loginSuccessful) { 

            return "Welcome " + this.name + " " + this.surname 

                    + ", it is great to see you again."; 

        } 

  

        return "Username or password incorrect, please try again."; 

    } 

} 
