
package za.ac.cput.gesondlinks.controllers;
import java.util.regex.Pattern;
/**
 *
 * @author Fezek
 */
public class ValidationUtils {
   

    // Email validation
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.matches(emailRegex, email);
    }

    // Phone number validation (South African format)
    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) return false;
        String phoneRegex = "^\\+?27[0-9]{9}$|^0[0-9]{9}$";
        return Pattern.matches(phoneRegex, phone.replaceAll("\\s", ""));
    }

    // Name validation (letters, spaces, hyphens, apostrophes)
    public static boolean isValidName(String name) {
        if (name == null || name.isEmpty()) return false;
        String nameRegex = "^[A-Za-z\\s\\-']+$";
        return Pattern.matches(nameRegex, name);
    }

    // Date validation - check if date is in the past
    public static boolean isPastDate(java.util.Date date) {
        if (date == null) return false;
        return date.before(new java.util.Date());
    }

    // Check if string is empty or null
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    // Validate required fields
    public static String validateRequiredFields(Object... fieldValues) {
        for (Object value : fieldValues) {
            if (value == null) return "All required fields must be filled";
            if (value instanceof String && isEmpty((String) value)) {
                return "All required fields must be filled";
            }
        }
        return null;
    }

    // Patient validation
    public static String validatePatient(String firstName, String lastName, String email, 
                                         String phoneNumber, String gender, java.sql.Date dateOfBirth) {
        if (isEmpty(firstName)) return "First name is required";
        if (isEmpty(lastName)) return "Last name is required";
        if (!isValidName(firstName)) return "Invalid first name format";
        if (!isValidName(lastName)) return "Invalid last name format";
        if (!isValidEmail(email)) return "Invalid email address";
        if (!isValidPhoneNumber(phoneNumber)) return "Invalid phone number (Use format: 0821234567 or +2712345678)";
        if (gender == null || gender.isEmpty()) return "Please select a gender";
        if (dateOfBirth == null) return "Date of birth is required";
        if (isPastDate(dateOfBirth)) return "Date of birth must be in the past";
        return null;
    }

    // Doctor validation
    public static String validateDoctor(String firstName, String lastName, String email, 
                                        String phoneNumber, String specialization) {
        if (isEmpty(firstName)) return "First name is required";
        if (isEmpty(lastName)) return "Last name is required";
        if (!isValidName(firstName)) return "Invalid first name format";
        if (!isValidName(lastName)) return "Invalid last name format";
        if (!isValidEmail(email)) return "Invalid email address";
        if (!isValidPhoneNumber(phoneNumber)) return "Invalid phone number (Use format: 0821234567)";
        if (isEmpty(specialization)) return "Specialization is required";
        return null;
    }

    // Appointment validation
    public static String validateAppointment(int patientId, int doctorId, java.sql.Date date, java.sql.Time time) {
        if (patientId <= 0) return "Please select a patient";
        if (doctorId <= 0) return "Please select a doctor";
        if (date == null) return "Appointment date is required";
        if (time == null) return "Appointment time is required";
        if (date.before(new java.util.Date())) return "Appointment date must be in the future";
        return null;
    }
}

