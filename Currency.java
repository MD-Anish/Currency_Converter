import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;
import org.json.JSONObject;

public class CurrencyConverter {
    private static final String API_KEY = "YOUR_API_KEY"; // Replace with your API key
    private static final String BASE_URL = "https://api.exchangerate-api.com/v4/latest/";
    
    private static HashMap<String, String> currencyCodes = new HashMap<>();

    public static void main(String[] args) {
        initializeCurrencyCodes();
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Welcome to Currency Converter");
        System.out.println("Supported currencies:");
        displaySupportedCurrencies();
        
        while (true) {
            System.out.println("\nOptions:");
            System.out.println("1. Convert currency");
            System.out.println("2. Update exchange rates");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            
            switch (choice) {
                case 1:
                    convertCurrency(scanner);
                    break;
                case 2:
                    System.out.println("Exchange rates are automatically fetched in real-time.");
                    break;
                case 3:
                    System.out.println("Exiting currency converter...");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void initializeCurrencyCodes() {
        // Add more currencies as needed
        currencyCodes.put("USD", "US Dollar");
        currencyCodes.put("EUR", "Euro");
        currencyCodes.put("GBP", "British Pound");
        currencyCodes.put("JPY", "Japanese Yen");
        currencyCodes.put("INR", "Indian Rupee");
        currencyCodes.put("AUD", "Australian Dollar");
        currencyCodes.put("CAD", "Canadian Dollar");
        currencyCodes.put("CNY", "Chinese Yuan");
    }

    private static void displaySupportedCurrencies() {
        for (String code : currencyCodes.keySet()) {
            System.out.println(code + " - " + currencyCodes.get(code));
        }
    }

    private static void convertCurrency(Scanner scanner) {
        System.out.print("Enter base currency code (e.g., USD): ");
        String fromCurrency = scanner.next().toUpperCase();
        
        if (!currencyCodes.containsKey(fromCurrency)) {
            System.out.println("Invalid currency code!");
            return;
        }
        
        System.out.print("Enter target currency code (e.g., EUR): ");
        String toCurrency = scanner.next().toUpperCase();
        
        if (!currencyCodes.containsKey(toCurrency)) {
            System.out.println("Invalid currency code!");
            return;
        }
        
        System.out.print("Enter amount to convert: ");
        double amount = scanner.nextDouble();
        
        try {
            double exchangeRate = getExchangeRate(fromCurrency, toCurrency);
            double convertedAmount = amount * exchangeRate;
            
            System.out.printf("\n%.2f %s = %.2f %s\n", 
                amount, fromCurrency, convertedAmount, toCurrency);
            System.out.printf("Exchange rate: 1 %s = %.4f %s\n", 
                fromCurrency, exchangeRate, toCurrency);
        } catch (IOException e) {
            System.out.println("Error fetching exchange rates: " + e.getMessage());
        }
    }

    private static double getExchangeRate(String fromCurrency, String toCurrency) throws IOException {
        if (fromCurrency.equals(toCurrency)) {
            return 1.0;
        }
        
        String urlStr = BASE_URL + fromCurrency + "?apikey=" + API_KEY;
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject rates = jsonResponse.getJSONObject("rates");
            return rates.getDouble(toCurrency);
        } else {
            throw new IOException("API request failed with code: " + responseCode);
        }
    }
}
