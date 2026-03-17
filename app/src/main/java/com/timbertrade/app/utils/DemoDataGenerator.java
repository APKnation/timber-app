package com.timbertrade.app.utils;

import com.timbertrade.app.models.Auction;
import com.timbertrade.app.models.Payment;
import com.timbertrade.app.models.Product;
import com.timbertrade.app.models.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DemoDataGenerator {
    
    public static List<User> generateDemoUsers() {
        List<User> users = new ArrayList<>();
        
        // Demo buyers
        users.add(new User("user1", "John Mwangi", "john@example.com", "+255712345678", User.UserRole.BUYER));
        users.add(new User("user2", "Grace Kimani", "grace@example.com", "+255713456789", User.UserRole.BUYER));
        users.add(new User("user3", "David Otieno", "david@example.com", "+255714567890", User.UserRole.BUYER));
        
        // Demo sellers
        users.add(new User("seller1", "John\'s Timber", "johns.timber@example.com", "+255715678901", User.UserRole.SELLER));
        users.add(new User("seller2", "Quality Wood Ltd", "quality.wood@example.com", "+255716789012", User.UserRole.SELLER));
        users.add(new User("seller3", "Tanzania Timber Co", "tz.timber@example.com", "+255717890123", User.UserRole.SELLER));
        
        // Demo admin
        users.add(new User("admin1", "Admin User", "admin@timbertrade.app", "+255718901234", User.UserRole.ADMIN));
        
        return users;
    }
    
    public static List<Product> generateDemoProducts() {
        List<Product> products = new ArrayList<>();
        
        // Mahogany products
        products.add(new Product(
                "prod1", "seller1", "John\'s Timber",
                "Premium Mahogany Timber - Grade A Quality",
                "High quality mahogany timber perfect for furniture making. Sustainably sourced from certified forests.",
                Product.TimberCategory.MAHOGANY, 850000, "per cubic meter", 50, "m³",
                "Dar es Salaam"
        ));
        
        products.add(new Product(
                "prod2", "seller2", "Quality Wood Ltd",
                "Mahogany Planks - Premium Quality",
                "Premium mahogany planks with excellent grain patterns. Ideal for high-end furniture.",
                Product.TimberCategory.MAHOGANY, 920000, "per cubic meter", 30, "m³",
                "Arusha"
        ));
        
        // Teak products
        products.add(new Product(
                "prod3", "seller1", "John\'s Timber",
                "Burmese Teak - Marine Grade",
                "Premium Burmese teak suitable for marine applications and outdoor furniture.",
                Product.TimberCategory.TEAK, 1200000, "per cubic meter", 25, "m³",
                "Dar es Salaam"
        ));
        
        products.add(new Product(
                "prod4", "seller3", "Tanzania Timber Co",
                "African Teak - Sustainable",
                "Sustainably harvested African teak with beautiful golden color.",
                Product.TimberCategory.TEAK, 980000, "per cubic meter", 40, "m³",
                "Mwanza"
        ));
        
        // Pine products
        products.add(new Product(
                "prod5", "seller2", "Quality Wood Ltd",
                "Radiata Pine - Construction Grade",
                "High-quality radiata pine perfect for construction and framing.",
                Product.TimberCategory.PINE, 450000, "per cubic meter", 100, "m³",
                "Dodoma"
        ));
        
        products.add(new Product(
                "prod6", "seller3", "Tanzania Timber Co",
                "Pine Timber - Treated",
                "Chemically treated pine timber resistant to termites and decay.",
                Product.TimberCategory.PINE, 520000, "per cubic meter", 75, "m³",
                "Mbeya"
        ));
        
        // Oak products
        products.add(new Product(
                "prod7", "seller1", "John\'s Timber",
                "European Oak - Premium Grade",
                "Imported European oak with exceptional strength and durability.",
                Product.TimberCategory.OAK, 1500000, "per cubic meter", 20, "m³",
                "Dar es Salaam"
        ));
        
        // Cedar products
        products.add(new Product(
                "prod8", "seller2", "Quality Wood Ltd",
                "Western Red Cedar - Aromatic",
                "Aromatic western red cedar perfect for outdoor applications.",
                Product.TimberCategory.CEDAR, 680000, "per cubic meter", 35, "m³",
                "Tanga"
        ));
        
        // Set demo data for products
        for (Product product : products) {
            product.setVerified(true);
            product.setRating(4.0 + Math.random()); // Random rating between 4.0 and 5.0
            product.setReviewCount((int) (10 + Math.random() * 40)); // Random reviews between 10-50
        }
        
        return products;
    }
    
    public static List<Auction> generateDemoAuctions() {
        List<Auction> auctions = new ArrayList<>();
        List<Product> products = generateDemoProducts();
        
        Date now = new Date();
        long oneHour = 60 * 60 * 1000;
        long oneDay = 24 * oneHour;
        
        // Live auctions
        auctions.add(new Auction(
                "auc1", "prod1", products.get(0), "seller1", "John\'s Timber",
                800000, now, new Date(now.getTime() + 2 * oneHour)
        ));
        auctions.get(0).setCurrentBid(850000);
        auctions.get(0).setCurrentBidderId("user1");
        auctions.get(0).setCurrentBidderName("John Mwangi");
        auctions.get(0).setStatus(Auction.AuctionStatus.ACTIVE);
        auctions.get(0).setBidCount(12);
        
        auctions.add(new Auction(
                "auc2", "prod3", products.get(2), "seller1", "John\'s Timber",
                1100000, now, new Date(now.getTime() + 4 * oneHour)
        ));
        auctions.get(1).setCurrentBid(1250000);
        auctions.get(1).setCurrentBidderId("user2");
        auctions.get(1).setCurrentBidderName("Grace Kimani");
        auctions.get(1).setStatus(Auction.AuctionStatus.ACTIVE);
        auctions.get(1).setBidCount(8);
        
        // Upcoming auctions
        auctions.add(new Auction(
                "auc3", "prod5", products.get(4), "seller2", "Quality Wood Ltd",
                400000, new Date(now.getTime() + oneDay), new Date(now.getTime() + oneDay + 6 * oneHour)
        ));
        auctions.get(2).setStatus(Auction.AuctionStatus.UPCOMING);
        
        // Ended auctions
        auctions.add(new Auction(
                "auc4", "prod2", products.get(1), "seller2", "Quality Wood Ltd",
                900000, new Date(now.getTime() - 2 * oneDay), new Date(now.getTime() - oneDay)
        ));
        auctions.get(3).setCurrentBid(1050000);
        auctions.get(3).setCurrentBidderId("user3");
        auctions.get(3).setCurrentBidderName("David Otieno");
        auctions.get(3).setStatus(Auction.AuctionStatus.ENDED);
        auctions.get(3).setBidCount(15);
        
        return auctions;
    }
    
    public static List<Payment> generateDemoPayments() {
        List<Payment> payments = new ArrayList<>();
        Date now = new Date();
        long oneHour = 60 * 60 * 1000;
        
        payments.add(new Payment(
                "pay1", "user1", "John Mwangi", "seller1", "John\'s Timber",
                850000, Payment.PaymentMethod.MPESA, Payment.PaymentType.PRODUCT_PURCHASE,
                "Payment for Premium Mahogany Timber (TXN-2024-001)"
        ));
        payments.get(0).setStatus(Payment.PaymentStatus.COMPLETED);
        payments.get(0).setProcessedAt(new Date(now.getTime() - 2 * oneHour));
        payments.get(0).setCreatedAt(new Date(now.getTime() - 3 * oneHour));
        
        payments.add(new Payment(
                "pay2", "user2", "Grace Kimani", "seller1", "John\'s Timber",
                1250000, Payment.PaymentMethod.AIRTEL_MONEY, Payment.PaymentType.AUCTION_PAYMENT,
                "Payment for won auction - Burmese Teak (TXN-2024-002)"
        ));
        payments.get(1).setStatus(Payment.PaymentStatus.COMPLETED);
        payments.get(1).setProcessedAt(new Date(now.getTime() - 5 * oneHour));
        payments.get(1).setCreatedAt(new Date(now.getTime() - 6 * oneHour));
        
        payments.add(new Payment(
                "pay3", "user3", "David Otieno", "seller2", "Quality Wood Ltd",
                450000, Payment.PaymentMethod.TIGO_PESA, Payment.PaymentType.PRODUCT_PURCHASE,
                "Payment for Radiata Pine Timber (TXN-2024-003)"
        ));
        payments.get(2).setStatus(Payment.PaymentStatus.PENDING);
        payments.get(2).setCreatedAt(new Date(now.getTime() - oneHour));
        
        return payments;
    }
    
    public static String[] getDemoImages() {
        return new String[]{
                "https://images.unsplash.com/photo-1518837695005-2083093ee35b?ixlib=rb-4.0.3",
                "https://images.unsplash.com/photo-1580984969071-a8da5656c2fb?ixlib=rb-4.0.3",
                "https://images.unsplash.com/photo-1549488344-1f9b8d2bd1f3?ixlib=rb-4.0.3",
                "https://images.unsplash.com/photo-1516534765038-b5e2b5c3d6a6?ixlib=rb-4.0.3"
        };
    }
}
