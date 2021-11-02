package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import static InventorySystem.InventorySystem.getIndex;

/**
 * The class Inventory.java
 * */
public class Inventory {

    public static int partId = 1, productId = 1;

    private static ObservableList<Part> allParts = FXCollections.observableArrayList();

    private static ObservableList<Product> allProducts = FXCollections.observableArrayList();

    /**
     * @param newPart the part to add
     * */
    public static void addPart(Part newPart){allParts.add(newPart);}

    /**
     * @param newProduct the product to add
     * */
    public static void addProduct(Product newProduct){allProducts.add(newProduct);}

    /**
     * @param partId the id of the part find
     * @return the Part
     * */
    public static Part lookupPart(int partId){
        Part part = null;

        for(Part currentPart : allParts){
            if(partId == currentPart.getId()){
                part = currentPart;
                break;
            }
        }
        return part;
    }


    /**
     * @param productId the id of the product to find
     * @return the product
     * */
    public static Product lookupProduct(int productId){
        Product product = null;

        for (Product currentProduct : allProducts){
            if(productId == currentProduct.getId()){
                product = currentProduct;
                break;
            }
        }

        return product;
    }

    /**
     * @param partName the name of the part to find
     * @return list of parts matching parts
     * */
    public static ObservableList<Part> lookupPart(String partName){

        ObservableList<Part> filteredParts = FXCollections.observableArrayList();

        for(Part part : getAllParts()){
            if(part.getName().toLowerCase().contains(partName.toLowerCase()))
                filteredParts.add(part);
        }
        return filteredParts;
    }

    /**
     * @param productName the name of the product to find
     * @return the list of matching products
     * */
    public static ObservableList<Product> lookupProduct(String productName){

        ObservableList<Product> filteredProducts = FXCollections.observableArrayList();

        for(Product product : getAllProducts()){
            if(product.getName().toLowerCase().contains(productName.toLowerCase()) || String.valueOf(product.getId()).contains(productName))
                filteredProducts.add(product);
        }

        return filteredProducts;
    }

    /**
     * @param index the index of the part to update
     * @param selectedPart the part to place in that location
     * */
    public static void updatePart(int index, Part selectedPart){ allParts.set(index, selectedPart); }

    /**
     * @param index the index of the product to update
     * @param newProduct the product to place in that location
     * */
    public static void updateProduct(int index, Product newProduct){
        allProducts.set(index, newProduct);
    }

    /**
     * @param selectedPart the part to delete
     * @return whether the part was deleted
     * */
    public static boolean deletePart(Part selectedPart){

        try {

            allParts.remove(getIndex(selectedPart, allParts));
            return true;

        }catch (Exception e){
            return false;
        }
    }

    /**
     * @param selectedProduct the product to delete
     * @return whether the product was deleted
     * */
    public static boolean deleteProduct(Product selectedProduct){
        try{

            allProducts.remove(getIndex(selectedProduct, allProducts));
            return true;

        }catch (Exception e){
            return false;
        }
    }

    /**
     * @return all parts in inventory
     * */
    public static ObservableList<Part> getAllParts(){return allParts;}

    /**
     * @return all products in inventory
     * */
    public static ObservableList<Product> getAllProducts(){return allProducts;}


}
