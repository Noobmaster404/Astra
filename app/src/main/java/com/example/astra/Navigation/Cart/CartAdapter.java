package com.example.astra.Navigation.Cart;

import android.content.Context;
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

import com.bumptech.glide.Glide;
import com.example.astra.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;
    private Context context;

    public CartAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        // Загружаем изображение
        Glide.with(context)
                .load(item.getImageUrl())
                .into(holder.imageView);

        holder.textViewName.setText(item.getName());
        holder.textViewPrice.setText(String.format("%.2f $", item.getPrice() * item.getQuantity()));
        holder.textViewQuantity.setText("Количество: " + item.getQuantity());

        // Удаление товара
        holder.buttonRemove.setOnClickListener(v -> {
            removeFromCart(cartItems.get(position).getProductId());
        });
    }

    private void removeFromCart(String productId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .collection("cart")
                .document(productId)
                .delete()
                .addOnSuccessListener(e -> {
                    Toast.makeText(context, "Товар удален", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewName, textViewPrice, textViewQuantity;
        ImageButton buttonRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewCartItem);
            textViewName = itemView.findViewById(R.id.textViewCartName);
            textViewPrice = itemView.findViewById(R.id.textViewCartPrice);
            textViewQuantity = itemView.findViewById(R.id.textViewCartQuantity);
            buttonRemove = itemView.findViewById(R.id.buttonRemove);
        }
    }
}
