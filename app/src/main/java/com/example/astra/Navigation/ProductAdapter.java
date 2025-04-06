package com.example.astra.Navigation;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
//
import com.bumptech.glide.Glide;
import com.example.astra.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.textViewName.setText(product.getName());
        holder.textViewPrice.setText(String.format("%.2f $", product.getPrice()));
        Glide.with(context).load(product.getImageUrl()).into(holder.imageView);

        holder.buttonAddToCart.setOnClickListener(v -> addToCart(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateList(List<Product> newList) {
        productList = newList;
        notifyDataSetChanged();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewPrice;
        ImageView imageView;
        ImageButton buttonAddToCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            imageView = itemView.findViewById(R.id.imageViewProduct);
            buttonAddToCart = itemView.findViewById(R.id.buttonAddToCart);
        }
    }

    private void addToCart(Product product) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(context, "Войдите в аккаунт!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .document(user.getUid())
                .collection("cart")
                .document(product.getId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        int currentQuantity = documentSnapshot.getLong("quantity").intValue();
                        updateCartItem(user.getUid(), product.getId(), currentQuantity + 1);
                    } else {
                        createNewCartItem(user.getUid(), product);
                    }
                });
    }

    private void createNewCartItem(String userId, Product product) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("productId", product.getId());
        cartItem.put("name", product.getName());
        cartItem.put("price", product.getPrice());
        cartItem.put("imageUrl", product.getImageUrl());
        cartItem.put("quantity", 1);

        db.collection("users")
                .document(userId)
                .collection("cart")
                .document(product.getId())
                .set(cartItem)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(context, "Товар добавлен в корзину!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateCartItem(String userId, String productId, int newQuantity) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(userId)
                .collection("cart")
                .document(productId)
                .update("quantity", newQuantity)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(context, "Количество обновлено!", Toast.LENGTH_SHORT).show());
    }
}


