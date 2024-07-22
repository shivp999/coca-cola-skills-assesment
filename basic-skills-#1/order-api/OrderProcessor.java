import java.util.*;
import java.util.Map;
import java.util.UUID;

// To run:

// STEPS TO RUN PROGRAM
//1. javac OrderProcessor.java
//2. java OrderProcessor

//*
// In the following code I am assuming the following:
//
// 1. Not using external dependencies
//  -> If I had a pom file I could use dependencies for parsing the data
//  -> Prefereably use spring boot
// 2. No Restcontroller is needed for this question otherswise I would create an endpoint to hit with data
// 3. Parsing the test data string manually other would use GSON library
// 4. Using a SET to store the hash of processed orders since we dont want duplicates
//  -> In real time there could be a quick query done to check if order hash already exists in database to save memory
//     instead of using a data structure in app with MAX_SIZE of 1000000 daily
//
// SCALING OPTIONS:
// 1. One way to properly scale in real time would be to get an unique id from client for each order in the payload.
//    Thus, we can always check the unique id first and if it is present then no need to even parse the data
// 2. Can use a performance storage solutions like Redis to fast lookups of hashes/unique keys
// 3. Implement retry logic
// */





public class OrderProcessor {

    private static Set<String> processedOrders = new HashSet<>();

    public static void main(String[] args) {
        // Example order payload from sample document
        String orderJson = "{\n" +
                "  \"outletId\": \"001234567\",\n" +
                "  \"item\": [\n" +
                "    {\n" +
                "      \"UPC_Code\": \"0 49000 01278 1\",\n" +
                "      \"qty\": \"50\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"UPC_Code\": \"049000064971\",\n" +
                "      \"qty\": \"30\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"totalOrderValue\": \"80.00\",\n" +
                "  \"totalDiscount\": \"0.0\",\n" +
                "  \"totalTax\": \"4.00\",\n" +
                "  \"movFee\": \"15.00\",\n" +
                "  \"deliveryFee\": \"10.00\",\n" +
                "  \"netInvoice\": \"109.00\"\n" +
                "}";


        Order newOrder = parseOrder(orderJson);

        // Generate UUID which can be logged to trace any issues to specific order
        // Not logging here but for future
        UUID uuid = UUID.randomUUID();

        if (isDuplicateOrder(newOrder)) {
            System.out.println("Duplicate order detected. Ignoring order.");
        } else {
            processOrder(newOrder);
        }
        // Processing the same order again to check if duplicate it caught
        newOrder = parseOrder(orderJson);

        if (isDuplicateOrder(newOrder)) {
            System.out.println("Duplicate order detected. Ignoring order."
            processOrder(newOrder);
        }
    }


    private static Order parseOrder(String orderJson) {
        Order order = new Order();
        // Remove the curly braces and split into key-value pairs
        String[] keyValuePairs = orderJson.substring(1, orderJson.length() - 1).split(",");
        for (String pair : keyValuePairs) {
            String[] parts = pair.trim().split(":");
            String key = parts[0].replaceAll("[{\"}]", "").trim();
            String value = parts[1].replaceAll("[{\"}]", "").trim();
            switch (key) {
                case "outletId":
                    order.outletId = value;
                    break;
                case "totalOrderValue":
                    order.totalOrderValue = value;
                    break;
                case "totalDiscount":
                    order.totalDiscount = value;
                    break;
                case "totalTax":
                    order.totalTax = value;
                    break;
                case "movFee":
                    order.movFee = value;
                    break;
                case "deliveryFee":
                    order.deliveryFee = value;
                    break;
                case "netInvoice":
                    order.netInvoice = value;
                    break;
                case "item":
                    // Parse items array - this is simplified
                    order.item = parseItems(value);
                    break;
            }
        }
        return order;
    }


    private static Item[] parseItems(String itemsString) {
        String[] itemsArray = itemsString.substring(1, itemsString.length() - 1).split(",");
        Item[] items = new Item[itemsArray.length];
        for (int i = 0; i < itemsArray.length; i++) {
            String itemString = itemsArray[i].trim();
            String[] parts = itemString.split(",");
            // Parse UPC_Code and qty
            String upc_code = null;
            String qty = null;
            for (String part : parts) {
                String[] keyValue = part.replaceAll("[{\"}]", "").trim().split(":");
                if (keyValue[0].equals("UPC_Code")) {
                    upc_code = keyValue[1].trim();
                } else if (keyValue[0].equals("qty")) {
                    qty = keyValue[1].trim();
                }
            }
            items[i] = new Item(UPC_Code, qty);
        }
        return items;
    }

    // Check for duplicate orders based on hash
    private static boolean isDuplicateOrder(Order order) {
        String orderHash = calculateOrderHash(order);
        // If duplicate order
        if (processedOrders.contains(orderHash)) {
            return true;
        }
        return false;
    }

    // Simulate order processing
    private static void processOrder(Order order) {
        String orderHash = calculateOrderHash(order);
        processedOrders.add(orderHash);
        System.out.println("Order processed successfully.");
    }

    // Using simple hashing with outletId and itemUPC_Code to check for duplicate orders
    private static String calculateOrderHash(Order order) {
        // Combine outletId and item UPCs and quantities for a simple hash
        StringBuilder item_hash = new StringBuilder();
        for(Item i : order.item){
            item_hash.append(i.UPC_Code).append(i.qty);
        }
        return order.outletId + item_hash.toString();
    }
}

// Model classes based on sample document

class Order {
    String outletId;
    String totalOrderValue;
    String totalDiscount;
    String totalTax;
    String movFee;
    String deliveryFee;
    String netInvoice;
    Item[] item;
}

class Item {
    String UPC_Code;
    String qty;
    String desc;
    String basePrice;
    String itemPrice;
    Adjustment[] adjustments;

    public Item(String UPC_Code, String qty) {
        this.UPC_Code = UPC_Code;
        this.qty = qty;
    }
}


class Adjustment {
    String adjustment;
    String amount;
    String desc;
}