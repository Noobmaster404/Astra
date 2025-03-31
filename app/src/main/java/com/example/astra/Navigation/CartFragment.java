package com.example.astra.Navigation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.astra.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private List<CartItem> cartItems = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerViewCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CartAdapter(getContext(), cartItems);
        recyclerView.setAdapter(adapter);

        loadCartItems();
    }

    private void loadCartItems() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .collection("cart")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    cartItems.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        CartItem item = document.toObject(CartItem.class);
                        if (item != null) {
                            cartItems.add(item);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });

    }
    private void removeFromCart(int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || position < 0 || position >= cartItems.size()) return;

        // Получаем productId из элемента корзины
        String productId = cartItems.get(position).getProductId();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .collection("cart")
                .document(productId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Удаляем элемент из списка после успешного удаления из Firestore
                    cartItems.remove(position);
                    adapter.notifyItemRemoved(position);
                });
    }
}