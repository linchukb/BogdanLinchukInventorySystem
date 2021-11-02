package InventorySystem;

import java.io.IOException;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;

import model.*;
import static model.Inventory.*;



// JAVADOC files located at /InventorySystem/javadoc
/**
 * An Inventory System program - FUTURE ENHANCEMENT.
 * This program simulates one a small company may use to store inventory records.
 * FUTURE ENHANCEMENT: make the parts and products dependent so that a part may not be deleted if it is associated with a product.
 * FUTURE ENHANCEMENT: implement a quantity of parts per product.
 * */
public class InventorySystem extends Application {

    /**
     * Gets an index of a part.
     * Provides caller with index of selected part from observableList of all parts
     * @param selectedPart the part to get index of.
     * @param list the list to query.
     * @return the index of selectedPart in list
     * */
    public static int getIndex(Part selectedPart, ObservableList<Part> list) {
        int index = 0, tempIndex = 0;
        for(Part part : list){
            int lhs = part.getId(), rhs = selectedPart.getId();
            if(lhs == rhs){
                index = tempIndex;
                break;
            }
            tempIndex++;
        }
        return index;
    }

    /**
     * Gets an index of a product.
     * Provides caller with index of selected product from observableList of all products.
     * @param selectedProduct the product to get index of.
     * @param list the list to query
     * @return the index of selectedProduct in list
     * */
    public static int getIndex(Product selectedProduct, ObservableList<Product> list){
        int index = 0, tempIndex = 0;
        for(Product product : list){
            int lhs = product.getId(), rhs = selectedProduct.getId();
            if(lhs == rhs)
                index = tempIndex;
            tempIndex++;
        }
        return index;
    }

    /**
     * Generates an alert - RUNTIME ERROR.
     * Fixed runtime issue where dialog pane font appeared unintelligible due to system font error. Addressed by formatting with external css file.
     * @param type the type of alert requested.
     * @param contentText the text to be displayed on the alert.
     * @return the appropriate alert with requested parameters.
     * */
    public static Alert generateAlert(Alert.AlertType type, String contentText){
        Alert alert = new Alert(type);
        alert.setContentText(contentText);

        DialogPane dialog = alert.getDialogPane();
        dialog.getStylesheets().add(InventorySystem.class.getResource("/styles/style.css").toExternalForm());
        dialog.getStyleClass().add("dialog");

        return alert;
    }

    /**
     * Generates a specific alert. This method displays an alert if no matches are found in a search.
     * It is called whenever a search is preformed.
     * */
    public static void noMatchAlert() {
        generateAlert(Alert.AlertType.INFORMATION, "No matches found").show();
    }

    /**
     * Loads each stage when called from each controller.
     * @param fxmlFileLocation the path of stage (fxml file) to be loaded.
     * */
    public static void loadStage(ActionEvent event, String fxmlFileLocation) throws IOException {
        Stage stage;
        Parent scene;

        stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(InventorySystem.class.getResource(fxmlFileLocation));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    @Override
    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

// JAVADOC files located at /InventorySystem/javadoc
/***
 * Called when the program first starts.
 */
    public static void main(String[] args) {
        InHouse     part1 = new InHouse(partId++,"Brake Rotor", 199.99,100,1,1000,1),
                    part2 = new InHouse(partId++,"Brake Pads", 49.99,100,1,1000,2),
                    part3 = new InHouse(partId++,"Oil Filter", 39.99,100,1,1000,3),
                    part4 = new InHouse(partId++,"Spark Plug", 29.99,100,1,1000,4);
        Outsourced  part5 = new Outsourced(partId++,"Engine Block", 699.99,50,1,1000,"Ford Motor Co."),
                    part6 = new Outsourced(partId++,"Gasket Set", 99.99,50,1,1000,"Yokohama"),
                    part7 = new Outsourced(partId++,"Engine Tubing", 399.99,50,1,1000,"Ford Motor Co."),
                    part8 = new Outsourced(partId++,"Bolt 9mm x 3mm", 1.99,500,1,10000,"Rearden metals"),
                    part9 = new Outsourced(partId++,"Wires 20 gauge", 279.99,375,7,75000,"Boyle Wires Ltd.");
        Inventory.addPart(part1);
        Inventory.addPart(part2);
        Inventory.addPart(part3);
        Inventory.addPart(part4);
        Inventory.addPart(part5);
        Inventory.addPart(part6);
        Inventory.addPart(part7);
        Inventory.addPart(part8);
        Inventory.addPart(part9);


        Product product1 = new Product(Inventory.productId++,"Brake Assembly", 249.98,50,1,1000),
                product2 = new Product(Inventory.productId++,"Engine", 249.98,50,1,1000),
                product3 = new Product(Inventory.productId++,"Wiring Bundle #2", 749.98,99,1,9000);
                product1.addAssociatedPart(part1);
                product1.addAssociatedPart(part2);
                product1.addAssociatedPart(part8);

        Inventory.addProduct(product1);
        Inventory.addProduct(product2);
        Inventory.addProduct(product3);




        launch(args);
    }
}
