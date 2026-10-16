   package parceldelivery.exception;

   /** Thrown when a user-supplied value fails validation. */
   public class InvalidInputException extends ParcelException {

       private static final long serialVersionUID = 1L;

       public InvalidInputException(String message) {
           super(message);
       }
   }