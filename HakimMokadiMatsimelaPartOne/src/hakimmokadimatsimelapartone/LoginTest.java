package hakimmokadimatsimelapartone; 

  

import org.junit.jupiter.api.BeforeEach; 

import org.junit.jupiter.api.Test; 

import static org.junit.jupiter.api.Assertions.*; 

  

public class LoginTest { 

  

    private Login login; 

  

    @BeforeEach 

    public void setUp() { 

        login = new Login(); 

    } 

  

    @Test 

    public void testUsernameCorrectlyFormatted() { 

        assertTrue(login.checkUserName("kyl_1")); 

    } 

  

    @Test 

    public void testUsernameIncorrectlyFormatted() { 

        assertFalse(login.checkUserName("kyle!!!!!!!")); 

    } 

  

    @Test 

    public void testPasswordMeetsComplexityRequirements() { 

        assertTrue(login.checkPasswordComplexity("Ch&&sec@ke99!")); 

    } 

  

    @Test 

    public void testPasswordDoesNotMeetComplexityRequirements() { 

        assertFalse(login.checkPasswordComplexity("password")); 

    } 

  

    @Test 

    public void testCellPhoneNumberCorrectlyFormatted() { 

        assertTrue(login.checkCellPhoneNumber("+27838968976")); 

    } 

  

    @Test 

    public void testCellPhoneNumberIncorrectlyFormatted() { 

        assertFalse(login.checkCellPhoneNumber("08966553")); 

    } 

  

    @Test 

    public void testRegistrationSuccess() { 

        String result = login.registerUser( 

                "Hakim", "Matsimela", "kyl_1", 

                "Ch&&sec@ke99!", "+27838968976"); 

  

        assertEquals("The user has been registered successfully.", result); 

    } 

  

    @Test 

    public void testRegistrationWithInvalidUsername() { 

        String result = login.registerUser( 

                "Hakim", "Matsimela", "kyle!!!!!!!", 

                "Ch&&sec@ke99!", "+27838968976"); 

  

        assertEquals( 

                "Username is not correctly formatted; please ensure that your username " 

                + "contains an underscore and is no more than five characters in length.", 

                result); 

    } 

  

    @Test 

    public void testRegistrationWithInvalidPassword() { 

        String result = login.registerUser( 

                "Hakim", "Matsimela", "kyl_1", 

                "password", "+27838968976"); 

  

        assertEquals( 

                "Password is not correctly formatted; please ensure that the password " 

                + "contains at least eight characters, a capital letter, a number, " 

                + "and a special character.", 

                result); 

    } 

  

    @Test 

    public void testLoginSuccessful() { 

        login.registerUser( 

                "Hakim", "Matsimela", "kyl_1", 

                "Ch&&sec@ke99!", "+27838968976"); 

  

        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!")); 

    } 

  

    @Test 

    public void testLoginFailed() { 

        login.registerUser( 

                "Hakim", "Matsimela", "kyl_1", 

                "Ch&&sec@ke99!", "+27838968976"); 

  

        assertFalse(login.loginUser("kyl_1", "wrongPassword")); 

    } 

  

    @Test 

    public void testSuccessfulLoginMessage() { 

        login.registerUser( 

                "Hakim", "Matsimela", "kyl_1", 

                "Ch&&sec@ke99!", "+27838968976"); 

  

        assertEquals( 

                "Welcome Hakim Matsimela, it is great to see you again.", 

                login.returnLoginStatus(true)); 

    } 

  

    @Test 

    public void testFailedLoginMessage() { 

        assertEquals( 

                "Username or password incorrect, please try again.", 

                login.returnLoginStatus(false)); 

    } 

} 