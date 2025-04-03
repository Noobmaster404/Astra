package com.example.astra.Navigation.Cart;

public class CartItem {

        private String productId;  // Для связи с Product
        private String name;
        private double price;
        private String imageUrl;  // Новое поле!
        private int quantity;

        public CartItem() {}

        public CartItem(String productId, String name, double price, String imageUrl, int quantity) {
            this.productId = productId;
            this.name = name;
            this.price = price;
            this.imageUrl = imageUrl;
            this.quantity = quantity;
        }

        // Геттеры и сеттеры...
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

