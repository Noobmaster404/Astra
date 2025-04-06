package com.example.astra.Navigation;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.astra.Navigation.Product;
import com.example.astra.Navigation.ProductAdapter;
import com.example.astra.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;
    private List<Product> filteredList;
    private SearchView searchView;
    private FloatingActionButton fabFilter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        searchView = view.findViewById(R.id.searchView);
        recyclerView = view.findViewById(R.id.recyclerViewProducts);
        fabFilter = view.findViewById(R.id.fabFilter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productList = new ArrayList<>();
        filteredList = new ArrayList<>();

        adapter = new ProductAdapter(requireContext(), filteredList);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);

        setupSearchView();
        setupFilterButton();
        loadProducts();
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterProducts(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText);
                return false;
            }
        });
    }

    private void setupFilterButton() {
        fabFilter.setOnClickListener(v -> showFilterDialog());
    }

    private void loadProducts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    productList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Product product = document.toObject(Product.class);
                        product.setId(document.getId());
                        productList.add(product);
                    }
                    filteredList.addAll(productList);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("FirebaseError", e.getMessage()));
    }

    private void filterProducts(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(productList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Product product : productList) {
                if (product.getName().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(product);
                }
            }
        }
        adapter.updateList(filteredList);
    }

    private void filterByPrice(double minPrice, double maxPrice) {
        filteredList.clear();
        for (Product product : productList) {
            if (product.getPrice() >= minPrice && product.getPrice() <= maxPrice) {
                filteredList.add(product);
            }
        }
        adapter.updateList(filteredList);
    }

    private void resetFilters() {
        filteredList.clear();
        filteredList.addAll(productList);
        adapter.updateList(filteredList);
        searchView.setQuery("", false);
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Фильтр товаров");

        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_filter, null);
        EditText etMinPrice = view.findViewById(R.id.etMinPrice);
        EditText etMaxPrice = view.findViewById(R.id.etMaxPrice);

        builder.setView(view);
        builder.setPositiveButton("Применить", (dialog, which) -> {
            try {
                double minPrice = etMinPrice.getText().toString().isEmpty() ? 0 :
                        Double.parseDouble(etMinPrice.getText().toString());
                double maxPrice = etMaxPrice.getText().toString().isEmpty() ? Double.MAX_VALUE :
                        Double.parseDouble(etMaxPrice.getText().toString());

                if (minPrice > maxPrice) {
                    Toast.makeText(requireContext(), "Минимальная цена не может быть больше максимальной", Toast.LENGTH_SHORT).show();
                    return;
                }

                filterByPrice(minPrice, maxPrice);
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Введите корректные значения", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Сбросить", (dialog, which) -> resetFilters());
        builder.setNeutralButton("Отмена", null);
        builder.show();
    }
}