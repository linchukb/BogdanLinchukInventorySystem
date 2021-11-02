package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.*;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static InventorySystem.InventorySystem.*;
import static model.Inventory.*;

/**
 * The controller for parts.
 * */
public class PartController implements Initializable {

    @FXML
    private RadioButton inHouseRBtn;

    @FXML
    private RadioButton outsourcedRBtn;

    @FXML
    private Label machineId_CompanyNameLbl;

    @FXML
    private TextField partIdTxt;

    @FXML
    private TextField partNameTxt;

    @FXML
    private TextField partInventoryLevelTxt;

    @FXML
    private TextField partPriceTxt;

    @FXML
    private TextField partMaxTxt;

    @FXML
    private TextField machineId_CompanyNameTxt;

    @FXML
    private TextField partMinTxt;

    /**
     * Event handler for Outsourced Radio Button being clicked.
     * <p>
     *     When selected sets label text to "Company Name".
     * </p>
     *
     * @param event contains event information; not used.
     * */
    @FXML
    void outsourcedRBtn(ActionEvent event) throws IOException{
        machineId_CompanyNameLbl.setText("Company Name");
    }

    /**
     * When selected sets label to "Machine ID".
     * @param event the selection.
     * */
    @FXML
    void inHouseRBtn(ActionEvent event) throws IOException{
        machineId_CompanyNameLbl.setText("Machine ID");
    }

    /**
     * The method onActionMainMenu.
     * Loads Main menu. Called after part saved or after cancel button.
     * @param event the call.
     * */
    @FXML
    void onActionMainMenu(ActionEvent event) throws IOException {
        Alert alert = generateAlert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?\nAll changes will be discarded");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK)
            loadStage(event, "/view/MainMenu.fxml");
    }

    /**
     * The method onActionSaveModifiedPart.
     * Commits modified part to list of all parts in Inventory.java.
     * @param event when save button clicked.
     * */
    @FXML
    void onActionSaveModifiedPart(ActionEvent event) throws IOException{

        try{

            InHouse updatedInHousePart = null;
            Outsourced updatedOutsourcedPart = null;

            int id = Integer.parseInt(partIdTxt.getText());
            String name = partNameTxt.getText();
            int inventoryLevel = Integer.parseInt(partInventoryLevelTxt.getText());
            double price = Double.parseDouble(partPriceTxt.getText());
            int max = Integer.parseInt(partMaxTxt.getText());
            int min = Integer.parseInt(partMinTxt.getText());

            // Handles logical exceptions
            if(!name.equals("")){
                // Handles logical exceptions
                if(!((inventoryLevel > max) || (inventoryLevel < min))){
                    if(inHouseRBtn.isSelected()){
                        int machineId = Integer.parseInt(machineId_CompanyNameTxt.getText().toString());
                        updatedInHousePart = new InHouse(id, name, price, inventoryLevel, min, max, machineId);
                    }else{
                        String companyName = machineId_CompanyNameTxt.getText().toString();
                        updatedOutsourcedPart = new Outsourced(id, name, price, inventoryLevel, min, max, companyName);
                    }

                    Optional<ButtonType> result = generateAlert(Alert.AlertType.CONFIRMATION, "Press OK to Save").showAndWait();
                    if(result.isPresent() && result.get() == ButtonType.OK) {
                        if(inHouseRBtn.isSelected())
                            Inventory.updatePart(getIndex(updatedInHousePart, getAllParts()), updatedInHousePart);
                        else
                            Inventory.updatePart(getIndex(updatedOutsourcedPart, getAllParts()), updatedOutsourcedPart);
                        loadStage(event, "/view/MainMenu.fxml");
                    }
                }else generateAlert(Alert.AlertType.WARNING, "Max must be more than min\nInv must be between max and min").show();

            }else generateAlert(Alert.AlertType.WARNING, "Part must have a name").show();

        //Handles runtime exceptions
        }catch(NumberFormatException e){
            generateAlert(Alert.AlertType.ERROR,"Please enter valid value for each field").show();
        }

    }

    /**
     * The method onActionSaveNewPart.
     * Commits new part to list of all parts in Inventory.java.
     * @param event the save button clicked on add part menu.
     * */
    @FXML
    void onActionSaveNewPart(ActionEvent event) throws IOException{

        try{

            InHouse newInHousePart = null;
            Outsourced newOutsourcedPart = null;
            int id = partId++;
            String name = partNameTxt.getText();
            int inventoryLevel = Integer.parseInt(partInventoryLevelTxt.getText());
            double price = Double.parseDouble(partPriceTxt.getText());
            int max = Integer.parseInt(partMaxTxt.getText());
            int min = Integer.parseInt(partMinTxt.getText());

            // Handles logical exceptions
            if(!name.equals("")){
                // Handles logical exceptions
                if(!((inventoryLevel > max) || (inventoryLevel < min))){
                    if(inHouseRBtn.isSelected()){
                        int machineId = Integer.parseInt(machineId_CompanyNameTxt.getText().toString());
                        newInHousePart = new InHouse(id, name, price, inventoryLevel, min, max, machineId);
                    }else{
                        String companyName = machineId_CompanyNameTxt.getText().toString();
                        newOutsourcedPart = new Outsourced(id, name, price, inventoryLevel, min, max, companyName);
                    }

                    Optional<ButtonType> result = generateAlert(Alert.AlertType.CONFIRMATION, "Press OK to Save").showAndWait();
                    if(result.isPresent() && result.get() == ButtonType.OK) {
                        if(inHouseRBtn.isSelected())
                            Inventory.addPart(newInHousePart);
                        else
                            Inventory.addPart(newOutsourcedPart);
                        loadStage(event, "/view/MainMenu.fxml");
                    }
                }else generateAlert(Alert.AlertType.WARNING, "Max must be more than min\nInv must be between max and min").show();

            }else generateAlert(Alert.AlertType.WARNING, "Part must have a name").show();

        //Handles runtime exceptions
        }catch(NumberFormatException e){
            generateAlert(Alert.AlertType.ERROR,"Please enter valid value for each field").show();
        }
    }

    /**
     * The method sendPart.
     * Sends parts across screens.
     * @param part the part to send.
     * */
    public void sendPart(Part part){

        partIdTxt.setText(String.valueOf(part.getId()));
        partNameTxt.setText(part.getName());
        partInventoryLevelTxt.setText(String.valueOf(part.getStock()));
        partPriceTxt.setText(String.valueOf(part.getPrice()));
        partMaxTxt.setText(String.valueOf(part.getMax()));
        partMinTxt.setText(String.valueOf(part.getMin()));
        if(part instanceof InHouse) {
            machineId_CompanyNameTxt.setText(String.valueOf(((InHouse) part).getMachineID()));
            machineId_CompanyNameLbl.setText("Machine ID");
            inHouseRBtn.setSelected(true);
        }else if(part instanceof Outsourced){
            machineId_CompanyNameTxt.setText(((Outsourced) part).getCompanyName());
            machineId_CompanyNameLbl.setText("Company Name");
            outsourcedRBtn.setSelected(true);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb){}

}
