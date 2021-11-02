package controller;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import InventorySystem.InventorySystem;

import model.Inventory;
import model.Part;
import model.Product;
import static model.Inventory.*;
import static InventorySystem.InventorySystem.*;

/**
 * The class ProductController.java
 * */
public class ProductController implements Initializable{

    private Product controllerProduct;
    /**
     * The member associatedParts.
     * Temporarily stores parts associated with a product.
     * */
    public static ObservableList<Part> associatedParts = FXCollections.observableArrayList();

    /**
     * The filteredParts member.
     * Holds parts returned by search query in a product screen.
     * */
    ObservableList<Part> filteredParts = FXCollections.observableArrayList();

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
    private TableView<Part> associatedPartsTableView;

    @FXML
    private TableColumn<Product, Integer> associatedPartIdCol;

    @FXML
    private TableColumn<Product, String> associatedPartNameCol;

    @FXML
    private TableColumn<Product, Integer> associatedPartInventoryLevelCol;

    @FXML
    private TableColumn<Product, Double> associatedPartPriceCol;

    @FXML
    private TextField productIdTxt;

    @FXML
    private TextField productNameTxt;

    @FXML
    private TextField productInventoryLevelTxt;

    @FXML
    private TextField productPriceTxt;

    @FXML
    private TextField productMaxTxt;

    @FXML
    private TextField productMinTxt;

    @FXML
    private TextField partSearchTxt;

    /**
     * The method getIndex.
     * Gets index of part to be removed from product.
     * @param selectedPart the part to find index of.
     * @return the index of selectedPart.
     * */
    private int getIndex(Part selectedPart) {
        int index = 0, tempIndex = 0;
        for (Part part : associatedParts){
            int lhs = part.getId(), rhs = selectedPart.getId();

            if(lhs == rhs){
                index = tempIndex;
            }
            tempIndex++;
        }
        return index;
    }

    /**
     * The method onActionAddAssociatedPart.
     * Adds part from list of all parts in Inventory.java to a product.
     * @param event the add button clicked
     * */
    @FXML
    void onActionAddAssociatedPart(ActionEvent event) {

        Part selectedPart = partsTableView.getSelectionModel().getSelectedItem();
        if(selectedPart != null){

            associatedParts = associatedPartsTableView.getItems();
            associatedParts.add(selectedPart);
            associatedPartsTableView.setItems(associatedParts);

        }else {
            generateAlert(Alert.AlertType.ERROR, "Select a part to add.").show();
        }


    }

    @FXML
    void onActionMainMenu(ActionEvent event) throws IOException{
        Optional<ButtonType> result = generateAlert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?\nAll changes will be discarded").showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK)
            loadStage(event, "/view/MainMenu.fxml");
    }

    /**
     * The method onActionRemoveAssociatedPart.
     * Removes associated part from a product.
     * @param event the "remove associated part" button clicked.
     * */
    @FXML
    void onActionRemoveAssociatedPart(ActionEvent event) {

        Part selectedPart = associatedPartsTableView.getSelectionModel().getSelectedItem();//new

        associatedParts = associatedPartsTableView.getItems();
        Optional<ButtonType> result = generateAlert(Alert.AlertType.CONFIRMATION, "Are you sure you want to remove this part?").showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            associatedParts.remove(getIndex(selectedPart));
            controllerProduct.deleteAssociatedPart(selectedPart);//new
            associatedPartsTableView.setItems(associatedParts);
        }
    }

    /**
     * The method onActionSaveNewProduct.
     * Commits product to list of all products in Inventory.java.
     * @param event the save button clicked.
     * */
    @FXML
    void onActionSaveNewProduct(ActionEvent event) throws IOException{

        try{

            Product product = null;
            int id = Inventory.productId++;
            String name = productNameTxt.getText();
            int inventoryLevel = Integer.parseInt(productInventoryLevelTxt.getText());
            double price = Double.parseDouble(productPriceTxt.getText());
            int max = Integer.parseInt(productMaxTxt.getText());
            int min = Integer.parseInt(productMinTxt.getText());

            // Handles logical exceptions
            if(!name.equals("")){
                // Handles logical exceptions
                if(!((inventoryLevel > max) || (inventoryLevel < min))){

                    double associatedPartsPriceSum = 0;
                    for(Part part : associatedParts){
                        associatedPartsPriceSum += part.getPrice();
                    }
                    if(associatedPartsPriceSum > price)
                        generateAlert(Alert.AlertType.WARNING, "Product price must greater than or equal to the sum of the price of parts.").show();
                    else{

                        product = new Product(id, name, price, inventoryLevel, min, max);
                        for(Part part : associatedParts) product.addAssociatedPart(part);

                        Optional<ButtonType> result = generateAlert(Alert.AlertType.CONFIRMATION, "Press OK to Save").showAndWait();
                        if(result.isPresent() && result.get() == ButtonType.OK) {
                            Inventory.addProduct(product);
                            loadStage(event, "/view/MainMenu.fxml");
                        }

                    }

                }else generateAlert(Alert.AlertType.WARNING, "Max must be more than min\nInv must be between max and min").show();

            }else generateAlert(Alert.AlertType.WARNING, "Product must have a name").show();
        // Handles runtime exceptions
        }catch (NumberFormatException e){
            generateAlert(Alert.AlertType.ERROR,"Please enter valid value for each field").show();
        }
    }

    /**
     * The method onActionSaveModifiedProduct.
     * Commits modified product to list of all products in Inventory.java.
     * @param event the save button clicked.
     * */
    @FXML
    void onActionSaveModifiedProduct(ActionEvent event) throws IOException{

        try{

            Product updatedProduct = null;
            int id = Integer.parseInt(productIdTxt.getText());
            String name = productNameTxt.getText();
            int inventoryLevel = Integer.parseInt(productInventoryLevelTxt.getText());
            double price = Double.parseDouble(productPriceTxt.getText());
            int max = Integer.parseInt(productMaxTxt.getText());
            int min = Integer.parseInt(productMinTxt.getText());
            associatedParts = controllerProduct.getAllAssociatedParts();
            // Handles logical exceptions
            if(!name.equals("")){
                // Handles logical exceptions
                if(!((inventoryLevel > max) || (inventoryLevel < min))){

                    double associatedPartsPriceSum = 0;
                    for(Part part : controllerProduct.getAllAssociatedParts()){
                        associatedPartsPriceSum += part.getPrice();
                    }
                    if(associatedPartsPriceSum > price)
                        generateAlert(Alert.AlertType.WARNING, "Product price must greater than or equal to the sum of the price of parts.").show();
                    else{

                        updatedProduct = new Product(id, name, price, inventoryLevel, min, max);
                        for(Part part : associatedParts) updatedProduct.addAssociatedPart(part);

                        Optional<ButtonType> result = generateAlert(Alert.AlertType.CONFIRMATION, "Press OK to Save").showAndWait();
                        if(result.isPresent() && result.get() == ButtonType.OK) {
                            Inventory.updateProduct(InventorySystem.getIndex(updatedProduct, getAllProducts()), updatedProduct);
                            loadStage(event, "/view/MainMenu.fxml");
                        }
                    }

                }else generateAlert(Alert.AlertType.WARNING, "Max must be more than min\nInv must be between max and min").show();

            }else generateAlert(Alert.AlertType.WARNING, "Product must have a name").show();
        // Handles runtime exceptions
        }catch (NumberFormatException e){
            generateAlert(Alert.AlertType.ERROR,"Please enter valid value for each field").show();
        }

    }

    /**
     * The method sendProduct.
     * Passes products between screens.
     * @param product the product to pass.
     * */
    public void sendProduct(Product product){
        controllerProduct = product;
        productIdTxt.setText(String.valueOf(controllerProduct.getId()));
        productNameTxt.setText(controllerProduct.getName());
        productInventoryLevelTxt.setText(String.valueOf(controllerProduct.getStock()));
        productPriceTxt.setText(String.valueOf(controllerProduct.getPrice()));
        productMaxTxt.setText(String.valueOf(controllerProduct.getMax()));
        productMinTxt.setText(String.valueOf(controllerProduct.getMin()));
        associatedPartsTableView.setItems(controllerProduct.getAllAssociatedParts());

    }

    /**
     * The method searchPart.
     * Searches list of all parts.
     * */
    private void searchPart(){

        String searchText = partSearchTxt.getText();

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
                int index = InventorySystem.getIndex(part, getAllParts());
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
     * The method onActionSearchPart.
     * Searches list of all parts.
     * @param event the search button clicked.
     * */
    @FXML
    void onActionSearchPart(ActionEvent event){
        searchPart();
    }

    /**
     * The method onKeyPressedSearchPart.
     * Searches list of all parts.
     * @param event the enter key pressed.
     * */
    @FXML
    void onKeyPressedSearchPart(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER) searchPart();
    }

    /**
     * Searches parts as you type. Each time a character is typed or deleted from the text field, searchPart() is executed.
     * */
    @FXML
    void onKeyTypedSearchPart(KeyEvent event){
        searchPart();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb){
        partsTableView.setItems(Inventory.getAllParts());
        partsIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        partsNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        partsInventoryLevelCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partsPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        associatedPartIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        associatedPartNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        associatedPartInventoryLevelCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        associatedPartPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

}
