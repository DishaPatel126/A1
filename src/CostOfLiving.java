import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CostOfLiving {
    //Map to store products with product name as key
    private List<Products.Product> productList;
    private Map<Integer, Map<String, Map<String, Float>>> carts;
    private int cartID;


    //constructor to initialise instance of the object
    public CostOfLiving() {
        productList = new ArrayList<>();
        carts = new HashMap<>();
        cartID = 1;
    }

    public List<Products.Product> getProductList() {
        return productList;
    }

    //Method to load the product list from file
    public int loadProductHistory(BufferedReader productStream) {
        int count = 0;
        try {
            String line;

            //Reading each line from the file and splitting into parts
            while ((line = productStream.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length == 4) {
                    //Extract the product date
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                    Date date;
                    try {
                        date = dateFormat.parse(parts[0]);
                    } catch (ParseException e) {
                        System.out.println("Invalid date format: " + parts[0]);
                        continue;
                    }
                    String name = parts[1].toLowerCase();
                    String size = parts[2].toLowerCase();
                    float price = Float.parseFloat(parts[3].substring(1)); //remove the dollar sign

                    //Create new Product object
                    Products.Product product = new Products.Product(date, name, size, price);

                    //Add product to the product list
                    productList.add(product);//use product name as key
                    count++;
                } else {
                    //Print error message if the line is not in correct format
                    System.out.println("Invalid product data " + line);
                }
            }

//            // Print the product data
//            System.out.println("Product List:");
//            for (Products.Product p : productList) {
//                System.out.println("  Name: " + p.getName());
//                System.out.println("  Date: " + p.getDate());
//                System.out.println("  Size: " + p.getSize());
//                System.out.println("  Price: $" + p.getPrice());
//                System.out.println();
//            }

//            findProductByName("Apple Juice");

        } catch (IOException e) {
            System.out.println("Error reading product list: " + e.getMessage());
        }
        return count;
    }

    //Method to load the shopping cart list from file
    public int loadShoppingCart(BufferedReader cartStream) {
        try {
            String line;
            // Mapping the product name to size and the total desired quantity (e.g. "2 l" or "2.5 kg")
            Map<String, Map<String, Float>> cart = new HashMap<>();

            // Reading each line from the file and splitting into parts
            while ((line = cartStream.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length != 3) {
                    System.out.println("Invalid line format: " + line);
                    return -1;
                }

                String productName = parts[0].trim().toLowerCase();  // Product name
                String size = parts[1].trim().toLowerCase();         // Size (e.g. "1 l")
                String totalQuantityStr = parts[2].trim().toLowerCase(); // Desired total quantity (e.g. "2 l", "2.5 kg")

                try {
                    // Convert total quantity to a float in the same unit as the size (e.g., "2 l" -> 2.0)
                    float totalQuantity = convertToBaseUnit(totalQuantityStr);

                    // Check if the product name already exists in the cart
                    if (!cart.containsKey(productName)) {
                        cart.put(productName, new HashMap<>());
                    }

                    // Store the desired total quantity for this product size
                    cart.get(productName).put(size, totalQuantity);

                } catch (NumberFormatException e) {
                    System.out.println("Invalid total quantity: " + totalQuantityStr);
                    return -1;
                }
            }

            if (cart.isEmpty()) {
                System.out.println("Shopping cart is empty");
                return -1;
            }

            // Add the cart to the map
            carts.put(cartID, cart);
            return cartID++;

        } catch (IOException e) {
            System.out.println("Error in reading shopping cart: " + e.getMessage());
            return -1;
        }
    }


    float shoppingCartCost(int cartNumber, int year, int month) {
        float totalCost = 0.0f;
        Map<String, Map<String, Float>> cart = carts.get(cartNumber); // Using Float for total quantity (like 2 l or 500 g)

        if (cart != null) {
            for (Map.Entry<String, Map<String, Float>> entry : cart.entrySet()) {
                String productName = entry.getKey();
                Map<String, Float> sizeQuantityMap = entry.getValue();

                for (Map.Entry<String, Float> sizeQuantityEntry : sizeQuantityMap.entrySet()) {
                    String requestedSize = sizeQuantityEntry.getKey(); // e.g. "1 l"
                    float requestedQuantity = sizeQuantityEntry.getValue();  // e.g. 2.0 (representing 2 l)

                    // Find matching products in the product list
                    List<Products.Product> matchingProducts = findMatchingProducts(productName, year, month);

                    if (!matchingProducts.isEmpty()) {
                        // Group products by size and sort by price ascending within each size
                        Map<Float, List<Products.Product>> sizeToProducts = new HashMap<>();
                        for (Products.Product product : matchingProducts) {
                            float productSizeInBaseUnit = convertToBaseUnit(product.getSize());
                            if (productSizeInBaseUnit > 0) {
                                sizeToProducts.computeIfAbsent(productSizeInBaseUnit, k -> new ArrayList<>()).add(product);
                            }
                        }

                        // Convert the size-to-products map to a sorted list by price per unit size
                        List<Map.Entry<Float, List<Products.Product>>> sortedSizes = new ArrayList<>(sizeToProducts.entrySet());
                        sortedSizes.sort((entry1, entry2) -> {
                            // Compare prices per unit size
                            Products.Product cheapest1 = Collections.min(entry1.getValue(), Comparator.comparing(Products.Product::getPrice));
                            Products.Product cheapest2 = Collections.min(entry2.getValue(), Comparator.comparing(Products.Product::getPrice));
                            float pricePerUnit1 = cheapest1.getPrice() / entry1.getKey();
                            float pricePerUnit2 = cheapest2.getPrice() / entry2.getKey();
                            return Float.compare(pricePerUnit1, pricePerUnit2);
                        });

                        // Fulfill the required quantity using the available product sizes
                        float remainingQuantity = requestedQuantity;
                        for (Map.Entry<Float, List<Products.Product>> sizeEntry : sortedSizes) {
                            Float size = sizeEntry.getKey();
                            List<Products.Product> productsOfSize = sizeEntry.getValue();
                            if (productsOfSize != null && !productsOfSize.isEmpty()) {
                                // Use the cheapest available product of this size
                                Products.Product cheapestProduct = Collections.min(productsOfSize, Comparator.comparing(Products.Product::getPrice));

                                // Calculate how many units are needed
                                int unitsNeeded = (int) Math.ceil(remainingQuantity / size);
                                totalCost += unitsNeeded * cheapestProduct.getPrice();
                                remainingQuantity -= unitsNeeded * size;

                                // If we have fulfilled the requested quantity, stop
                                if (remainingQuantity <= 0) {
                                    break;
                                }
                            }
                        }

                        if (remainingQuantity > 0) {
                            System.out.println("Unable to fulfill the total quantity for " + productName + " (" + requestedQuantity + " " + requestedSize + ")");
                        }
                    } else {
                        System.out.println("No matching products found for " + productName + " in the given year and month.");
                    }
                }
            }
        }
        return totalCost;
    }

    public Map<String, Float> inflation(int startYear, int startMonth, int endYear, int endMonth) {
        Map<String, Float> inflationMap = new HashMap<>();

        // Loop through all products in the start month
        for (Products.Product startProduct : productList) {
            // Get the date of the current product
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startProduct.getDate());
            int productYear = calendar.get(Calendar.YEAR);
            int productMonth = calendar.get(Calendar.MONTH) + 1; // 0-based month, so +1

            // Only consider products from the start year and month
            if (productYear != startYear || productMonth != startMonth) {
                continue;
            }

            // Try to find a matching product in the end period
            Products.Product matchingEndProduct = null;
            for (Products.Product tempProduct : productList) {
                // Get the date of the temp product
                calendar.setTime(tempProduct.getDate());
                int tempYear = calendar.get(Calendar.YEAR);
                int tempMonth = calendar.get(Calendar.MONTH) + 1;

                // Match by both name and size, and ensure it is in the end date range
                if (tempProduct.getName().equals(startProduct.getName()) &&
                        tempProduct.getSize().equals(startProduct.getSize()) &&
                        tempYear == endYear && tempMonth == endMonth) {

                    matchingEndProduct = tempProduct;  // Found matching product
                    break;  // Exit loop if exact match found (same name and size)
                }
            }

            // If no exact size match is found, check for other sizes of the same product
            if (matchingEndProduct == null) {
                for (Products.Product tempProduct : productList) {
                    // Match only by name (ignore size) and ensure it's within the end date range
                    calendar.setTime(tempProduct.getDate());
                    int tempYear = calendar.get(Calendar.YEAR);
                    int tempMonth = calendar.get(Calendar.MONTH) + 1;

                    if (tempProduct.getName().equals(startProduct.getName()) &&
                            tempYear == endYear && tempMonth == endMonth) {

                        matchingEndProduct = tempProduct;  // Found a match with a different size
                        break;  // Exit loop if we find a different size of the same product
                    }
                }
            }

            if (matchingEndProduct == null) {
                continue;
            }

            // Convert sizes to base units (to handle shrinkflation if sizes differ)
            float startSizeInBaseUnits = convertToBaseUnit(startProduct.getSize());
            float endSizeInBaseUnits = convertToBaseUnit(matchingEndProduct.getSize());

            // Calculate inflation or shrinkflation
            float inflationRate = 0;
            if (startSizeInBaseUnits == endSizeInBaseUnits) {
                // Regular cost inflation (same size)
                inflationRate = (matchingEndProduct.getPrice() - startProduct.getPrice()) / startProduct.getPrice() * 100;
            } else {
                // Shrinkflation: sizes changed
                float startUnitCost = startProduct.getPrice() / startSizeInBaseUnits;
                float endUnitCost = matchingEndProduct.getPrice() / endSizeInBaseUnits;
                inflationRate = (endUnitCost - startUnitCost) / startUnitCost * 100;
            }

            // Add to the inflation map
            if(inflationRate > 0) {
                String productKey = matchingEndProduct.getName() + " " + matchingEndProduct.getSize();
                inflationMap.put(productKey, inflationRate);
            }
        }
        return inflationMap.isEmpty() ? null : inflationMap;
    }


    public List<String> priceInversion(int year, int month, int tolerance) {
        return null;
    }

    public List<Products.Product> findMatchingProducts(String productName, int year, int month) {
        List<Products.Product> matchingProducts = new ArrayList<>();

        // Loop until we find matching products or run out of months to check
        while (year >= 0) {  // Continue until we reach an invalid year (year < 0)
            for (Products.Product product : productList) {
                // Check if the product name matches
                if (product.getName().toLowerCase().equals(productName.toLowerCase())) {
                    // Check if the product's date matches the given year and month
                    Date productDate = product.getDate();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(productDate);
                    int productYear = calendar.get(Calendar.YEAR);
                    int productMonth = calendar.get(Calendar.MONTH) + 1; // +1 because MONTH is 0-based

                    if (productYear == year && productMonth == month) {
                        matchingProducts.add(product); // Add the product to the matching list
                    }
                }
            }

            // If we found matching products, return them
            if (!matchingProducts.isEmpty()) {
                return matchingProducts;
            }

            // Otherwise, move to the previous month
            month--;
            if (month == 0) {
                // If we reach month 0, move to the previous year and set the month to December (12)
                year--;
                month = 12;
            }
        }

        // Return an empty list if no matches are found
        return matchingProducts;
    }


    private float convertToBaseUnit(String size) {
        size = size.toLowerCase().trim(); // Convert to lowercase and remove extra spaces

        try {
            if (size.endsWith("ml")) {
                // Handle milliliters (convert to liters)
                return Float.parseFloat(size.replace("ml", "").trim()) / 1000;
            } else if (size.endsWith("l")) {
                // Handle liters
                return Float.parseFloat(size.replace("l", "").trim());
            } else if (size.endsWith("g") && !size.endsWith("kg")) {
                // Handle grams (convert to kilograms)
                return Float.parseFloat(size.replace("g", "").trim()) / 1000;
            } else if (size.endsWith("kg")) {
                // Handle kilograms
                return Float.parseFloat(size.replace("kg", "").trim());
            } else {
                throw new IllegalArgumentException("Unknown or missing size unit: " + size);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid size format: [" + size + "]");
            return -1; // Handle the error as necessary
        }
    }
}