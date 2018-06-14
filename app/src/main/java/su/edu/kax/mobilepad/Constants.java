
package su.edu.kax.mobilepad;

/**
 * Static class with constants used across whole application
 */
public interface Constants {
    int MESSAGE_STATE_CHANGE = 1;
    int MESSAGE_READ = 2;
    int MESSAGE_WRITE = 3;
    int MESSAGE_TOAST = 5;

    String TOAST = "toast";


    int[] keysValues = new int[]{
            0x1B, 0x11, '\b',
            '\t', 0x10, '\n',
            0x14, 0x12, 0x20,
            0x23, 0x7F, 0x9B
    };


}
