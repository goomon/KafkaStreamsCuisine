package com.goomon.kafkastreams.util.datagen;

import com.github.javafaker.Faker;
import com.github.javafaker.Finance;
import com.github.javafaker.Name;
import com.goomon.kafkastreams.model.Purchase;
import com.goomon.kafkastreams.util.datagen.model.Customer;
import com.goomon.kafkastreams.util.datagen.model.Store;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class DataGenerator {

    public static final int NUMBER_UNIQUE_CUSTOMERS = 100;
    public static final int NUMBER_UNIQUE_STORES = 15;
    public static final int DEFAULT_NUM_PURCHASES = 100;
    public static final int NUM_ITERATIONS = 10;

    private static Faker dateFaker = new Faker();
    private static Supplier<Date> timestampGenerator = () -> dateFaker.date().past(15, TimeUnit.MINUTES, new Date());

    private DataGenerator() {
    }

    public static List<Purchase> generatePurchases(int number, int numberCustomers) {
        List<Purchase> purchases = new ArrayList<>();

        Faker faker = new Faker();
        List<Customer> customers = generateCustomers(numberCustomers);
        List<Store> stores = generateStores();

        Random random = new Random();
        for (int i = 0; i < number; i++) {
            String itemPurchased = faker.commerce().productName();
            int quantity = faker.number().numberBetween(1, 5);
            double price = Double.parseDouble(faker.commerce().price(4.00, 295.00));
            Date purchaseDate = timestampGenerator.get();

            Customer customer = customers.get(random.nextInt(numberCustomers));
            Store store = stores.get(random.nextInt(NUMBER_UNIQUE_STORES));

            Purchase purchase = Purchase.builder()
                    .creditCardNumber(customer.getCreditCardNumber())
                    .customerId(customer.getCustomerId())
                    .department(store.getDepartment())
                    .employeeId(store.getEmployeeId())
                    .firstName(customer.getFirstName())
                    .lastName(customer.getLastName())
                    .itemPurchased(itemPurchased)
                    .quantity(quantity)
                    .price(price)
                    .purchaseDate(purchaseDate)
                    .zipCode(store.getZipCode())
                    .storeId(store.getStoreId())
                    .build();
            purchases.add(purchase);
        }
        return purchases;
    }

    public static List<Customer> generateCustomers(int numberCustomers) {
        List<Customer> customers = new ArrayList<>(numberCustomers);
        Faker faker = new Faker();
        List<String> creditCards = generateCreditCardNumbers(numberCustomers);
        for (int i = 0; i < numberCustomers; i++) {
            Name name = faker.name();
            String creditCard = creditCards.get(i);
            String customerId = faker.idNumber().valid();
            customers.add(new Customer(name.firstName(), name.lastName(), customerId, creditCard));
        }
        return customers;
    }

    private static List<String> generateCreditCardNumbers(int numberCards) {
        int counter = 0;
        Pattern visaMasterCardAmex = Pattern.compile("(\\d{4}-){3}\\d{4}");
        List<String> creditCardNumbers = new ArrayList<>(numberCards);
        Finance finance = new Faker().finance();
        while (counter < numberCards) {
            String cardNumber = finance.creditCard();
            if (visaMasterCardAmex.matcher(cardNumber).matches()) {
                creditCardNumbers.add(cardNumber);
                counter++;
            }
        }
        return creditCardNumbers;
    }

    private static List<Store> generateStores() {
        List<Store> stores = new ArrayList<>(NUMBER_UNIQUE_STORES);
        Faker faker = new Faker();
        for (int i = 0; i < NUMBER_UNIQUE_STORES; i++) {
            String department = (i % 5 == 0) ? "Electronics" : faker.commerce().department();
            String employeeId = Long.toString(faker.number().randomNumber(5, false));
            String zipCode = faker.options().option("47197-9482", "97666", "113469", "334457");
            String storeId = Long.toString(faker.number().randomNumber(6, true));
            stores.add(new Store(employeeId, zipCode, storeId, department));
        }
        return stores;
    }
}
