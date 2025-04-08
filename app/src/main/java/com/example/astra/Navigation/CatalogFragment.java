package com.example.astra.Navigation;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.astra.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CatalogFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;
    private List<Product> filteredList;
    private SearchView searchView;
    private FloatingActionButton fabFilter;

    // Константы для сортировки
    private static final int SORT_DEFAULT = 0;
    private static final int SORT_NAME_ASC = 1;
    private static final int SORT_NAME_DESC = 2;
    private static final int SORT_PRICE_ASC = 3;
    private static final int SORT_PRICE_DESC = 4;
    private int currentSortType = SORT_DEFAULT;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalog, container, false);

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
                applyFilters();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                applyFilters();
                return true;
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
                    applyFilters();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseError", e.getMessage());
                    Toast.makeText(requireContext(), "Ошибка загрузки товаров", Toast.LENGTH_SHORT).show();
                });
    }

    private void applyFilters() {
        filteredList.clear();
        filteredList.addAll(productList);

        // Применяем поиск по названию
        String query = searchView.getQuery().toString().toLowerCase();
        if (!query.isEmpty()) {
            filteredList.removeIf(product -> !product.getName().toLowerCase().contains(query));
        }

        // Применяем сортировку
        applySorting();

        adapter.updateList(filteredList);
    }

    private void applySorting() {
        switch (currentSortType) {
            case SORT_NAME_ASC:
                Collections.sort(filteredList, (p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
                break;
            case SORT_NAME_DESC:
                Collections.sort(filteredList, (p1, p2) -> p2.getName().compareToIgnoreCase(p1.getName()));
                break;
            case SORT_PRICE_ASC:
                Collections.sort(filteredList, (p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));
                break;
            case SORT_PRICE_DESC:
                Collections.sort(filteredList, (p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
                break;
            case SORT_DEFAULT:
            default:
                // Без сортировки
                break;
        }
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Сортировка товаров");

        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_filter, null);
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        radioGroup.check(getRadioButtonIdForSortType());

        builder.setView(view);
        builder.setPositiveButton("Применить", (dialog, which) -> {
            int checkedId = radioGroup.getCheckedRadioButtonId();
            if (checkedId == R.id.rbNameAsc) {
                currentSortType = SORT_NAME_ASC;
            } else if (checkedId == R.id.rbNameDesc) {
                currentSortType = SORT_NAME_DESC;
            } else if (checkedId == R.id.rbPriceAsc) {
                currentSortType = SORT_PRICE_ASC;
            } else if (checkedId == R.id.rbPriceDesc) {
                currentSortType = SORT_PRICE_DESC;
            } else {
                currentSortType = SORT_DEFAULT;
            }
            applyFilters();
        });

        builder.setNegativeButton("Сбросить", (dialog, which) -> {
            currentSortType = SORT_DEFAULT;
            searchView.setQuery("", false);
            applyFilters();
        });

        builder.setNeutralButton("Отмена", null);
        builder.show();
    }

    private int getRadioButtonIdForSortType() {
        switch (currentSortType) {
            case SORT_NAME_ASC: return R.id.rbNameAsc;
            case SORT_NAME_DESC: return R.id.rbNameDesc;
            case SORT_PRICE_ASC: return R.id.rbPriceAsc;
            case SORT_PRICE_DESC: return R.id.rbPriceDesc;
            default: return R.id.rbDefault;
        }
    }
}