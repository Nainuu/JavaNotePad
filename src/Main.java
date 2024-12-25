import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // made this invoke later to make updates to our GUI more thread safe
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");
                    new NotePadGUI().setVisible(true);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        }
}