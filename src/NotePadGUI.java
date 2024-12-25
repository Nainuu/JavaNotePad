import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class NotePadGUI extends JFrame {
//    To show file explorer dialogue box when saving a new file or save as
    private JFileChooser fileChooser;
    private JTextArea textArea;
//    Keeps track of the currently opened or saved file. This helps implement the Save functionality.
    private File currentFile;
//    Swing built in library to manage undo and redo functionality
//    as every text edit (add, remove, etc.) is recorded as an UndoableEditEvent.
    private UndoManager undoManager;
//    constructor
    public NotePadGUI() {
        super("Notepad");
        setSize( 400 , 500 );
//        center location
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

//      FILE CHOOSER SETUP (initialization)
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("src/assets")); //This will open file relative to "assets" folder
//        apply filter that we can only apply .txt file in the save dialogue box
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files" , "txt"));

//        (initialization)
        undoManager = new UndoManager();
        addGuiComponents();
    }

    private void addGuiComponents() {
        addToolbar();

//       To ADD Textarea to the type text into
        textArea = new JTextArea();

        textArea.setLineWrap(true); // Wraps lines that are too long
        textArea.setWrapStyleWord(true); // Wraps at word boundaries

        textArea.getDocument().addUndoableEditListener(new UndoableEditListener() {
            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
//                add each edit that we do in the text area (either adding or removing text)
                undoManager.addEdit(e.getEdit());
            }
        });
        add(textArea, BorderLayout.CENTER);
    }

    private void addToolbar() {
        JToolBar toolBar = new JToolBar();

//        make the toolbar not float able
        toolBar.setFloatable(false);

        //Menu Bar
        JMenuBar menuBar = new JMenuBar();
        toolBar.add(menuBar);

        // add menus
        menuBar.add(addFileMenu());
        menuBar.add(addEditMenu());
        menuBar.add(addViewMenu());

        add(toolBar, BorderLayout.NORTH);
    }
//    FileMenu GUI setup
    private JMenu addFileMenu() {
        JMenu fileMenu = new JMenu("File");

//       "NEW" functionality - resets everything
        JMenuItem newMenuItems = new JMenuItem("New");
//        Functionality
        newMenuItems.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                reset Header title of the file
                setTitle("Notepad By Husnain");
//                reset text area
                textArea.setText("");

//                reset current file variable to allow to save the file using "SAVE"
            }
        });
        fileMenu.add(newMenuItems);

//      "OPEN" functionality - open a text file
        JMenuItem openMenuItem = new JMenuItem("Open");
//        Functionality
        openMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                open file explorer
                int result = fileChooser.showOpenDialog(NotePadGUI.this);
                if(result != JFileChooser.APPROVE_OPTION) return;
                try{
                    //                reset Notepad
                    newMenuItems.doClick();

//                get the selected file
                    File selectedFile = fileChooser.getSelectedFile();

//                    update current file
                    currentFile = selectedFile;

//                update title header
                    setTitle(selectedFile.getName());

//                read the file
                    FileReader fileReader = new FileReader(selectedFile);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);

//                store the text using a StringBuilder
                    StringBuilder fileText = new StringBuilder();
                    String readText;

                    while ((readText = bufferedReader.readLine()) !=null){
                            fileText.append(readText + "\n");

//                            Update text area GUI
                        textArea.setText(fileText.toString());
                    }
                }catch(Exception e1){
                    e1.printStackTrace();
                }



            }
        });
        fileMenu.add(openMenuItem);


//        "SAVE AS" functionality - create a new text file and save user text
        JMenuItem saveAsMenuItem = new JMenuItem("Save As");
        //        Functionality
        saveAsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                open save dialogue
                int result = fileChooser.showSaveDialog(NotePadGUI.this);

//                Continue to execute code if the user has excuted save button
                if(result != JFileChooser.APPROVE_OPTION) return;
                try {
                    //                choose selected file
                    File selectedFile = fileChooser.getSelectedFile();
                    // if file name doesn't have the ".txt" extension then append it
                    // Get the name of the selected file (without the full path).
                    String fileName = selectedFile.getName();

                    // Check if the file name does not already end with ".txt" (case insensitive).
                    // We do this by taking the last 4 characters of the file name and comparing them to ".txt".
                    if (!fileName.substring(fileName.length() - 4).equalsIgnoreCase(".txt")) {

                        // If the file name doesn't end with ".txt", create a new File object
                        // with the same path as the original, but add ".txt" to the end of the file name.
                        selectedFile = new File(selectedFile.getAbsoluteFile() + ".txt");
                    }

                    //create new file
                    selectedFile.createNewFile();

//                    Now we will write the user text into the created file
                    FileWriter fileWriter = new FileWriter(selectedFile);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(textArea.getText());
                    bufferedWriter.close();
                    fileWriter.close();

//                    update the title header of GUI for the save text file
                    setTitle(fileName);

//                  Update current file
                    currentFile = selectedFile;

//                    show display dialogue
                    JOptionPane.showMessageDialog(NotePadGUI.this, "Saved File!");

                }catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
        });

        fileMenu.add(saveAsMenuItem);


//        "SAVE" functionality - save the file in the existing file location
        JMenuItem saveMenuItem = new JMenuItem("Save");
//        Functionality
        saveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                if the current file is null then we have to perform save as functionality
                if(currentFile == null) saveAsMenuItem.doClick();

//               if the user chooses to cancel the saving the file this means that current file will still be null
//                then we want to prevent executing the rest of the code
                if(currentFile == null) return;

                try {
//                    write to current file
                    FileWriter fileWriter = new FileWriter(currentFile);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(textArea.getText());
                    bufferedWriter.close();
                    fileWriter.close();

                }catch(Exception e1){
                    e1.printStackTrace();
                }
            }
        });
        fileMenu.add(saveMenuItem);

//        "exit" functionality - exit the file
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NotePadGUI.this.dispose();
            }
        });
        fileMenu.add(exitMenuItem);

        return fileMenu;
    }
    private JMenu addEditMenu() {
        JMenu editMenu = new JMenu("Edit");

//        "UNDO"
        JMenuItem undoMenuItem = new JMenuItem("Undo");
//        Functionality
        undoMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                means that if there are any edits that we can undo, then we undo them
                if(undoManager.canUndo()){
                    undoManager.undo();
                }
            }
        });
        editMenu.add(undoMenuItem);

//        "REDO"
        JMenuItem redoMenuItem = new JMenuItem("Redo");
//        functionality
        redoMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                Redo whenever this is pressed
                if(undoManager.canRedo()){
                    undoManager.redo();
                }
            }
        });
        editMenu.add(redoMenuItem);


        return editMenu;
    }
    private JMenu addViewMenu() {
        JMenu viewMenu = new JMenu("View");

        // Create a "Zoom" submenu
        JMenu zoomMenu = new JMenu("Zoom");

        // "Zoom In" functionality
        JMenuItem zoomInMenuItem = new JMenuItem("Zoom In");
        zoomInMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                gets style,name,size
                Font currentFont = textArea.getFont();
                textArea.setFont(new Font(
                        currentFont.getName(),
                        currentFont.getStyle(),
                        currentFont.getSize() + 1
                ));
            }
        });
        zoomMenu.add(zoomInMenuItem);

        // "Zoom Out" functionality
        JMenuItem zoomOutMenuItem = new JMenuItem("Zoom Out");
        zoomOutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Font currentFont = textArea.getFont();
                textArea.setFont(new Font(
                        currentFont.getName(),
                        currentFont.getStyle(),
                        currentFont.getSize() - 1
                ));
            }
        });
        zoomMenu.add(zoomOutMenuItem);

        // "Restore Default Zoom" functionality
        JMenuItem zoomRestoreMenuItem = new JMenuItem("Restore Default Zoom");
        zoomRestoreMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Font currentFont = textArea.getFont();
                textArea.setFont(new Font(
                        currentFont.getName(),
                        currentFont.getStyle(),
                        12 // Restore to default font size
                ));
            }
        });
        zoomMenu.add(zoomRestoreMenuItem);

        // Add the "Zoom" submenu to the "View" menu
        viewMenu.add(zoomMenu);

        // "About" menu item
        JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Display a dialog box with application details
                String aboutMessage = "<html><h2>NotePad Application</h2>"
                        + "<p>Version: 1.0.1</p>"
                        + "<p>Developed By: <em>Husnain and Team<em/></p>"
                        + "<p>Special thanks to Sir Fawad for Teaching!</p></html>";
                JOptionPane.showMessageDialog(
                        null,
                        aboutMessage,
                        "About NotePad",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
        viewMenu.add(aboutMenuItem);

        return viewMenu;
    }
//    Closing Braces
}


