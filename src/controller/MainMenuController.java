package controller;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import model.Inventory;
import model.Part;
import model.Product;

import static InventorySystem.InventorySystem.*;
import static model.Inventory.*;

/**
 * Controller for main menu.
 * */
public class MainMenuController implements Initializable {

    Stage stage;

    FileWriter fileWriter = new FileWriter("src/files/partSearchLog.txt", true);
    PrintWriter searchPartLog = new PrintWriter(fileWriter);




    /** Holds list of parts returned by query. */
    ObservableList<Part> filteredParts = FXCollections.observableArrayList();
    /** Holds list of products returned by query. */
    ObservableList<Product> filteredProducts = FXCollections.observableArrayList();

    @FXML
    private TableView<Part> partsTableView;

    @FXML
    private TableColumn<Part, Integer> partsIdCol;

    @FXML
    private TableColumn<Part, String> partsNameCol;

    @FXML
    private TableColumn<Part, Integer> partsInventoryLevelCol;

    @FXML
    private TableColumn<Part, Double> partsPriceCol;

    @FXML
    private TextField productSearchTxt;

    @FXML
    private TableView<Product> productsTableView;

    @FXML
    private TableColumn<Product, Integer> productIdCol;

    @FXML
    private TableColumn<Product, String> productNameCol;

    @FXML
    private TableColumn<Product, Integer> productInventoryLevelCol;

    @FXML
    private TableColumn<Product, Double> productPriceCol;

    @FXML
    private TextField partSearchTxt;

    public MainMenuController() throws IOException {
    }

    /**
     * Searches all parts for value in search text field.
     * Displays matched items in place of partsTableView if more than 1 found, else selects the 1 product on partsTableView.
     * */
    private void searchPart() {

        String searchText = partSearchTxt.getText();

        searchPartLog.append(LocalDateTime.now() + " --- " + searchText + "\n");

        try {

            Part part = Inventory.lookupPart(Integer.parseInt(searchText));
            if(part == null){

                filteredParts = lookupPart(searchText);
                if(filteredParts.isEmpty())
                    noMatchAlert();
                else if(filteredParts.size() > 1)
                    partsTableView.setItems(filteredParts);
                else {
                    partsTableView.getSelectionModel().select(filteredParts.get(0));
                    partsTableView.scrollTo(filteredParts.get(0));
                }

            }else{
                int index = getIndex(part, getAllParts());
                partsTableView.getSelectionModel().select(index);
                partsTableView.scrollTo(part);
            }

        } catch (NumberFormatException e) {
            filteredParts = lookupPart(searchText);
            if(filteredParts.isEmpty())
                noMatchAlert();
            else if(filteredParts.size() > 1)
                partsTableView.setItems(filteredParts);
            else {
                partsTableView.getSelectionModel().select(filteredParts.get(0));
                partsTableView.scrollTo(filteredParts.get(0));
            }
        }

    }

    /**
     * Searches all products for value in search text field.
     * Displays matched items in place of productsTableView if more than 1 item found, else selects the 1 part on productsTableView.
     * */
    private void searchProduct(){



        String searchText = productSearchTxt.getText();

        try{

            Product product = Inventory.lookupProduct(Integer.parseInt(searchText));
            if(product == null){

                filteredProducts = lookupProduct(searchText);
                if(filteredProducts.isEmpty())
                    noMatchAlert();
                else if(filteredProducts.size() > 1)
                    productsTableView.setItems(filteredProducts);
                else{
                    productsTableView.getSelectionModel().select(filteredProducts.get(0));
                    productsTableView.scrollTo(filteredProducts.get(0));
                }

            }else {
                int index = getIndex(product, getAllProducts());
                productsTableView.getSelectionModel().select(index);
                productsTableView.scrollTo(product);
            }

        }catch (NumberFormatException e){

            filteredProducts = lookupProduct(searchText);
            if(filteredProducts.isEmpty())
                noMatchAlert();
            else if(filteredProducts.size() > 1)
                productsTableView.setItems(filteredProducts);
            else {
                productsTableView.getSelectionModel().select(filteredProducts.get(0));
                productsTableView.scrollTo(filteredProducts.get(0));
            }

        }

    }

    @FXML
    void onActionSearchProduct(ActionEvent event) {
        searchProduct();
    }

    @FXML
    void onKeyPressedSearchProduct(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER) searchProduct();
    }

    /**
     * Searches products as you type.
     * Each time a character is typed or deleted from the text field, searchProduct() is executed.
     * */
    @FXML
    void onKeyTypedSearchProduct(KeyEvent event) {
        searchProduct();
    }

    @FXML
    void onActionAddPart(ActionEvent event) throws IOException {
        loadStage(event, "/view/AddPart.fxml");
    }

    @FXML
    void onActionAddProduct(ActionEvent event) throws IOException{
        loadStage(event, "/view/AddProduct.fxml");
    }

    /**
     * Deletes parts from list of all parts in Inventory.java.
     * @param event the delete button clicked.
     * */
    @FXML
    void onActionDeletePart(ActionEvent event) {

        Part selectedPart = partsTableView.getSelectionModel().getSelectedItem();

        if(selectedPart != null){

            Optional<ButtonType> result = generateAlert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this part?\nThis cannot be undone.").showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK)
                if (!deletePart(selectedPart)) generateAlert(Alert.AlertType.ERROR, "Unable to delete part").showAndWait();
                else if (!partSearchTxt.getText().equals("")) {
                    filteredParts.remove(getIndex(selectedPart, filteredParts));
                    partsTableView.setItems(filteredParts);
                    searchPart();
                }

        }else {
            generateAlert(Alert.AlertType.ERROR, "Select a part to delete").show();
        }



    }

    /**
     * Deletes product from list of all products in Inventory.java.
     * @param event the delete button clicked.
     * */
    @FXML
    void onActionDeleteProduct(ActionEvent event) {
        Product selectedProduct = productsTableView.getSelectionModel().getSelectedItem();

        if(selectedProduct != null){

            if(selectedProduct.getAllAssociatedParts().size() > 0){
                generateAlert(Alert.AlertType.WARNING, "Cannot delete product with 1 or more associated parts.\nRemove all associated parts then try again.").show();
            }else{
                Optional<ButtonType> result = generateAlert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this part?\nThis cannot be undone.").showAndWait();
                if(result.isPresent() && result.get() == ButtonType.OK)
                    if(!deleteProduct(selectedProduct)) generateAlert(Alert.AlertType.ERROR, "Unable to delete product").showAndWait();
                    else if (!productSearchTxt.getText().equals("")) {
                        filteredProducts.remove(getIndex(selectedProduct, filteredProducts));
                        productsTableView.setItems(filteredProducts);
                        searchProduct();
                    }
            }

        }else {
            generateAlert(Alert.AlertType.ERROR, "Select a product to delete").show();
        }



    }

    /**
     * Exits the program.
     * @param event the exit button clicked.
     * */
    @FXML
    void onActionExit(ActionEvent event) throws IOException{



        Optional<ButtonType> result = generateAlert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?").showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            searchPartLog.append("\n\n");
            searchPartLog.close();
            System.exit(0);
        }
    }

    /**
     * Loads modify part menu.
     * @param event the modify button clicked.
     * */
    @FXML
    void onActionModifyPart(ActionEvent event) throws IOException{

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/ModifyPart.fxml"));
        loader.load();

        PartController PController = loader.getController();

        try {

            Part part = partsTableView.getSelectionModel().getSelectedItem();

            PController.sendPart(part);
            stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();

        }catch (NullPointerException e){
            generateAlert(Alert.AlertType.ERROR, "Please Select a part to modify").show();
        }
    }

    /**
     * Loads modify product menu.
     * @param event the modify button clicked.
     * */
    @FXML
    void onActionModifyProduct(ActionEvent event) throws IOException{

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/ModifyProduct.fxml"));
        loader.load();

        ProductController PController = loader.getController();

        try{

            Product product = productsTableView.getSelectionModel().getSelectedItem();
            PController.sendProduct(product);
            stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();

        }catch (NullPointerException e){
            generateAlert(Alert.AlertType.ERROR, "Please Select a part to modify").show();
        }

    }

    /**
     * Searches for selected part in list of all parts in Inventory.java.
     * @param event the search button clicked.
     * */
    @FXML
    void onActionSearchPart(ActionEvent event) {
        searchPart();
    }

    /**
     * Searches for selected part in list of all parts in Inventory.java.
     * @param event the enter key pressed.
     * */
    @FXML
    void onKeyPressedSearchPart(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER) searchPart();
    }

    /**
     * Live Search method. Searches parts as you type.
     * Each time a character is typed or deleted from the text field, searchPart() is executed.
     * */
    @FXML
    void onKeyTypedSearchPart(KeyEvent event) {
        //searchPart();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb){

        searchPartLog.append(LocalDateTime.now().toLocalDate() + "\n");

        // Parts
        partsTableView.setItems(Inventory.getAllParts());
        partsIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        partsNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        partsInventoryLevelCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partsPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Products
        productsTableView.setItems(Inventory.getAllProducts());
        productIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        productInventoryLevelCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        productPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

    }

}
