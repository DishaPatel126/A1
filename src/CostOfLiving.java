import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CostOfLiving {
    //Map to store products with product name as key
    private List<Products.Product> productList;
    private Map<Integer, Map<String, Float>> carts;
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
        Set<String> validUnits = new HashSet<>();
        validUnits.add("l"); // Add valid units here
        validUnits.add("ml");
        validUnits.add("kg");
        validUnits.add("g");

        if (productStream == null) {
            System.out.println("Product stream is null");
            return -1; // Return a negative value to indicate failure when stream is null
        }
        try {
            String line;

            //Reading each line from the file and splitting into parts
            while ((line = productStream.readLine()) != null) {

                // Skip empty or blank lines
                if (line.trim().isEmpty()) {
                    continue;
                }

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

                    String name = parts[1].trim();
                    if (name.isEmpty()) {
                        System.out.println("Invalid product name (empty or spaces): " + parts[1]);
                        return -1;  // Return error for empty or spaces-only name
                    }

                    String size = parts[2].trim();
                    // **Check for missing space between quantity and unit**
                    if (size.isEmpty() || !size.matches("\\d+(\\.\\d+)?\\s+[a-zA-Z]+")) {
                        System.out.println("Invalid size format (missing space or invalid size): " + size);
                        return -1;  // Return error for missing space or invalid format
                    }

                    // Check for valid unit
                    String unit = size.split(" ")[1]; // Assuming size is in format "quantity unit"
                    if (!validUnits.contains(unit)) {
                        System.out.println("Invalid unit size: " + unit);
                        return -1; // Return error for invalid unit
                    }

                    float price ;
                    try {
                        price = Float.parseFloat(parts[3].trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid price format: " + parts[3]);
                        return -1; // Return error if price is invalid
                    }

                    if (price < 0) {
                        System.out.println("Invalid cost (negative): " + price);
                        return -1;  // Return error for negative price
                    }

                    // Extract quantity from size
                    float quantity;
                    try {
                        // Extract numeric value from size (e.g., "1 l" -> 1)
                        quantity = Float.parseFloat(size.split(" ")[0]);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid quantity in size: " + size);
                        return -1; // Return error if quantity is not a valid number
                    }

                    // Check for negative or zero quantity
                    if (quantity <= 0) {
                        System.out.println("Invalid quantity (negative or zero): " + quantity);
                        return -1; // Return error for invalid quantity
                    }

                    //Create new Product object
                    Products.Product product = new Products.Product(date, name, size, price);

                    //Add product to the product list
                    productList.add(product);//use product name as key
                    count++;
                } else {
                    //Print error message if the line is not in correct format
                    System.out.println("Invalid product data " + line);
                    return -1;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return count;
    }

    //Method to load the shopping cart list from file
    public int loadShoppingCart(BufferedReader cartStream) {
        try {
            String line;
            // Mapping the product name to size and the total desired quantity (e.g. "2 l" or "2.5 kg")
            Map<String, Float> cart = new HashMap<>();

            // Reading each line from the file and splitting into parts
            while ((line = cartStream.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length != 2) {
                    System.out.println("Invalid line format: " + line);
                    return -1;
                }

                String productName = parts[0].trim().toLowerCase();  // Product name
                String totalQuantityStr = parts[1].trim().toLowerCase(); // Desired total quantity (e.g. "2 l", "2.5 kg")

                try {
                    // Convert total quantity to a float in the same unit as the size (e.g., "2 l" -> 2.0)
                    float totalQuantity = convertToBaseUnit(totalQuantityStr);

                    // Store the desired total quantity for this product size
                    cart.put(productName, totalQuantity);

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
            e.printStackTrace();
            return -1;
        }
    }


    float shoppingCartCost(int cartNumber, int year, int month) {
        float totalCost = 0.0f;
        Map<String, Float> cart = carts.get(cartNumber); // Cart map with product name and required quantity

        if (cart != null) {
            for (Map.Entry<String, Float> entry : cart.entrySet()) {
                String productName = entry.getKey();
                float quantity = entry.getValue();

                // Find the latest matching products for the given product name
                List<Products.Product> matchingProducts = findMatchingProducts(productName, year, month);

                if (!matchingProducts.isEmpty()) {
                    // Filter out products with a price of 0 on or before the given date
                    matchingProducts = filterOutZeroCostProducts(matchingProducts, year, month);

                    // Group products by size
                    Map<Float, List<Products.Product>> sizeToProducts = new HashMap<>();
                    for (Products.Product product : matchingProducts) {
                        float productSizeInBaseUnit = convertToBaseUnit(product.getSize());
                        if (productSizeInBaseUnit > 0) {
                            sizeToProducts.computeIfAbsent(productSizeInBaseUnit, k -> new ArrayList<>()).add(product);
                        }
                    }

                    // Convert size-to-products map to a sorted list by price per unit size
                    List<Map.Entry<Float, List<Products.Product>>> sortedSizes = new ArrayList<>(sizeToProducts.entrySet());
                    sortedSizes.sort((entry1, entry2) -> {
                        Products.Product cheapest1 = Collections.min(entry1.getValue(), Comparator.comparing(Products.Product::getPrice));
                        Products.Product cheapest2 = Collections.min(entry2.getValue(), Comparator.comparing(Products.Product::getPrice));
                        float pricePerUnit1 = cheapest1.getPrice() / entry1.getKey();
                        float pricePerUnit2 = cheapest2.getPrice() / entry2.getKey();
                        return Float.compare(pricePerUnit1, pricePerUnit2);
                    });

                    // Fulfill the required quantity using the available product sizes
                    float remainingQuantity = quantity;
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
                        System.out.println("Unable to fulfill the total quantity for " + productName + " " + quantity + ")");
                    }
                } else {
                    System.out.println("No matching products found for " + productName + " in the given year and month.");
                }
            }
        }
        return totalCost;
    }



    public Map<String, Float> inflation(int startYear, int startMonth, int endYear, int endMonth) {
        Map<String, Float> inflationMap = new HashMap<>();

        // Create date boundaries
        Calendar startBoundary = Calendar.getInstance();
        startBoundary.set(startYear, startMonth - 1, 1); // 1st of start month
        Calendar endBoundary = Calendar.getInstance();
        endBoundary.set(endYear, endMonth - 1, 1); // 1st of end month

        // Map to store the starting prices for each product and size
        Map<String, Float> startPrices = new HashMap<>();

        // Collect starting prices for products introduced on or before the start of the period
        for (Products.Product product : productList) {
            Calendar productDate = Calendar.getInstance();
            productDate.setTime(product.getDate());

            // Only consider products introduced on or before the start boundary
            if (productDate.compareTo(startBoundary) <= 0) {
                String key = product.getName() + " " + product.getSize();
                if (!startPrices.containsKey(key)) { // Store only the first instance found
                    startPrices.put(key, product.getPrice());
//                    System.out.println("Start Price Captured: " + key + " Price: " + product.getPrice());
                }
            }
        }

        // Loop through products to find matching end prices and calculate inflation
        for (Products.Product endProduct : productList) {
            Calendar productDate = Calendar.getInstance();
            productDate.setTime(endProduct.getDate());

            // Only consider products introduced on or before the end boundary
            if (productDate.compareTo(endBoundary) <= 0) {
                String productKey = endProduct.getName() + " " + endProduct.getSize();

                // Check if there's a start price for the current end product
                Float startPrice = startPrices.get(productKey);
                if (startPrice != null) {
                    float startSizeInBaseUnits = convertToBaseUnit(getSizeFromProductNameAndSize(productKey)); // Use proper size string
                    float endSizeInBaseUnits = convertToBaseUnit(endProduct.getSize());

                    // Calculate inflation or shrinkflation
                    float inflationRate = 0;
                    boolean inflationCalculated = false;

                    if (startSizeInBaseUnits == endSizeInBaseUnits) {
                        // Regular cost inflation (same size)
                        inflationRate = (endProduct.getPrice() - startPrice) / startPrice * 100;
                        inflationCalculated = inflationRate > 0;
                    } else {
                        // Shrinkflation: sizes changed
                        float startUnitCost = startPrice / startSizeInBaseUnits;
                        float endUnitCost = endProduct.getPrice() / endSizeInBaseUnits;
                        inflationRate = (endUnitCost - startUnitCost) / startUnitCost * 100;
                        inflationCalculated = inflationRate > 0;
                    }

                    // Add to the inflation map if inflation rate is positive
                    if (inflationCalculated) {
                        inflationMap.put(productKey, inflationRate);
                    } else {
//                        System.out.println("No inflation for: " + productKey);
                    }
                } else {
//                    System.out.println("No start price found for: " + productKey);
                }
            } else {
//                System.out.println("End product " + endProduct.getName() + " " + endProduct.getSize() + " is not within the end boundary.");
            }
        }

        if (inflationMap.isEmpty()) {
//            System.out.println("No inflation data available.");
        }

        Map<String, Float> shrinkflationMap = calculateShrinkflation(startYear, startMonth,endYear, endMonth);
        Map<String, Float> combinedMap = new HashMap<>();
        if (inflationMap != null) {
            combinedMap.putAll(inflationMap);
        }
        if (shrinkflationMap != null) {
            for (Map.Entry<String, Float> entry : shrinkflationMap.entrySet()) {
                // If the product already exists in the map (from inflation), choose how to handle it
                combinedMap.merge(entry.getKey(), entry.getValue(), (oldValue, newValue) -> {
                    // This lambda defines how to handle duplicates (e.g., sum or pick the max rate)
                    return Math.max(oldValue, newValue); // You can decide to pick max, sum, etc.
                });
            }
        }

        return combinedMap.isEmpty() ? null : combinedMap;
    }

    public Map<String, Float> calculateShrinkflation(int startYear, int startMonth, int endYear, int endMonth) {
        Map<String, Float> shrinkflationMap = new HashMap<>();

        // Create date boundaries
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(startYear, startMonth - 1, 1); // 1st of start month
        Date startBoundary = startCalendar.getTime(); // Convert Calendar to Date

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(endYear, endMonth - 1, 1); // 1st of end month
        Date endBoundary = endCalendar.getTime(); // Convert Calendar to Date

        // Maps to store the starting sizes and prices
        Map<String, Products.Product> initialProductMap = new HashMap<>();
        Map<String, Products.Product> newProductMap = new HashMap<>();

        // Collect initial products
        for (Products.Product product : productList) {
            Calendar productCalendar = Calendar.getInstance();
            productCalendar.setTime(product.getDate());

            if (product.getDate().compareTo(startBoundary) <= 0) {
                String key = product.getName() + " " + product.getSize();
                if (!initialProductMap.containsKey(key)) {
                    initialProductMap.put(key, product);
                }
            }
        }

        // Collect new products
        for (Products.Product product : productList) {
            Calendar productCalendar = Calendar.getInstance();
            productCalendar.setTime(product.getDate());

            if (product.getDate().compareTo(endBoundary) <= 0) {
                String key = product.getName() + " " + product.getSize();
                if (!newProductMap.containsKey(key)) {
                    newProductMap.put(key, product);
                }
            }
        }

        // Check for shrinkflation
        for (Map.Entry<String, Products.Product> initialEntry : initialProductMap.entrySet()) {
            String initialKey = initialEntry.getKey();
            Products.Product initialProduct = initialEntry.getValue();
            String[] initialParts = initialKey.split(" ", 2);
            String baseName = initialParts[0];
            float initialSizeInBaseUnits = convertToBaseUnit(initialParts[1]);

            boolean shrinkflationFound = false;
            for (Map.Entry<String, Products.Product> newEntry : newProductMap.entrySet()) {
                String newKey = newEntry.getKey();
                Products.Product newProduct = newEntry.getValue();
                String[] newParts = newKey.split(" ", 2);
                String newBaseName = newParts[0];

                // Check if the new product is the same but with a smaller size and introduced within the period
                if (baseName.equals(newBaseName) && newProduct.getDate().compareTo(startBoundary) >= 0) {
                    float newSizeInBaseUnits = convertToBaseUnit(newParts[1]);

                    if (newSizeInBaseUnits < initialSizeInBaseUnits) {
                        // Calculate per-unit costs
                        float initialUnitCost = initialProduct.getPrice() / initialSizeInBaseUnits;
                        float newUnitCost = newProduct.getPrice() / newSizeInBaseUnits;

                        // Calculate shrinkflation rate
                        float shrinkflationRate = (newUnitCost - initialUnitCost) / initialUnitCost * 100;

                        // Check if the rate is positive (indicating shrinkflation)
                        if (shrinkflationRate > 0) {
                            shrinkflationMap.put(newKey, shrinkflationRate);
                        }
                        shrinkflationFound = true;
                        break;
                    }
                }
            }

            if (!shrinkflationFound) {
//                System.out.println("No shrinkflation found for: " + initialKey);
            }
        }

        if (shrinkflationMap.isEmpty()) {
//            System.out.println("No shrinkflation data available.");
        }

        return shrinkflationMap.isEmpty() ? null : shrinkflationMap;
    }

        public List<String> priceInversion(int year, int month, int tolerance) {
        // List to store results
        List<String> results = new ArrayList<>();

        // Filter products by the specified year and month
        List<Products.Product> filteredProducts = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        for (Products.Product product : productList) {
            calendar.setTime(product.getDate());
            int productYear = calendar.get(Calendar.YEAR);
            int productMonth = calendar.get(Calendar.MONTH) + 1;

            if (productYear == year && productMonth == month) {
                filteredProducts.add(product);
            }
        }

        // Group products by name
        Map<String, List<Products.Product>> productGroups = new HashMap<>();
        for (Products.Product product : filteredProducts) {
            String productName = product.getName().toLowerCase();
            productGroups.computeIfAbsent(productName, k -> new ArrayList<>()).add(product);
        }

        // Iterate over each product group
        for (Map.Entry<String, List<Products.Product>> entry : productGroups.entrySet()) {
            String productName = entry.getKey();
            List<Products.Product> products = entry.getValue();

            // Sort products by size (converted to base unit) ascending
            products.sort(Comparator.comparing(p -> convertToBaseUnit(p.getSize())));

            for (int i = 0; i < products.size(); i++) {
                for (int j = i + 1; j < products.size(); j++) {
                    Products.Product larger = products.get(j);
                    Products.Product smaller = products.get(i);

                    float largerUnitCost = larger.getPrice() / convertToBaseUnit(larger.getSize());
                    float smallerUnitCost = smaller.getPrice() / convertToBaseUnit(smaller.getSize());

                    float percentageDifference = ((largerUnitCost -smallerUnitCost) / largerUnitCost) * 100;

                    if (percentageDifference > tolerance) {
                        String result = productName + "\t" + larger.getSize() + "\t" + smaller.getSize();
                        results.add(result);
                    }
                }
            }
        }

        // Return the results, or null if no results
        return results.isEmpty() ? null : results;
    }

    public List<Products.Product> findMatchingProducts(String productName, int year, int month) {
        // Map to keep track of the latest product for each size
        Map<Float, Products.Product> latestProductsBySize = new HashMap<>();

        while (year >= 0) {
            for (Products.Product product : productList) {
                // Check if the product name matches
                if (product.getName().equalsIgnoreCase(productName)) {
                    // Check if the product's date matches the given year and month
                    Date productDate = product.getDate();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(productDate);
                    int productYear = calendar.get(Calendar.YEAR);
                    int productMonth = calendar.get(Calendar.MONTH) + 1; // +1 because MONTH is 0-based

                    if (productYear == year && productMonth == month) {
                        float productSizeInBaseUnit = convertToBaseUnit(product.getSize());
                        // Only consider products with positive sizes
                        if (productSizeInBaseUnit > 0) {
                            // Check if we already have a product for this size
                            Products.Product existingProduct = latestProductsBySize.get(productSizeInBaseUnit);
                            if (existingProduct == null || productDate.after(existingProduct.getDate())) {
                                // Update to the latest product for this size
                                latestProductsBySize.put(productSizeInBaseUnit, product);
                            }
                        }
                    }
                }
            }

            // Move to the previous month
            month--;
            if (month == 0) {
                // If we reach month 0, move to the previous year and set the month to December (12)
                year--;
                month = 12;
            }
        }

        // Convert map values to a list
        List<Products.Product> latestProducts = new ArrayList<>(latestProductsBySize.values());

        return latestProducts;
    }

    // Helper method to filter out products with a cost of 0 on or before the given date
    private List<Products.Product> filterOutZeroCostProducts(List<Products.Product> products, int year, int month) {
        List<Products.Product> filteredProducts = new ArrayList<>();
        for (Products.Product product : products) {
            Date productDate = product.getDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(productDate);
            int productYear = calendar.get(Calendar.YEAR);
            int productMonth = calendar.get(Calendar.MONTH) + 1; // MONTH is 0-based

            if (product.getPrice() > 0 || (productYear > year || (productYear == year && productMonth > month))) {
                filteredProducts.add(product);
            }
        }
        return filteredProducts;
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

    // Helper function to extract size from a product key
    private String getSizeFromProductNameAndSize(String key) {
        // Split the key by space and get the size part
        String[] parts = key.split(" ");
        if (parts.length >= 2) {
            return String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
        }
        return "";
    }

}