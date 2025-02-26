package com.unir.payments.data;

import com.unir.payments.data.utils.Consts;
import com.unir.payments.data.utils.SearchCriteria;
import com.unir.payments.data.utils.SearchOperation;
import com.unir.payments.data.utils.SearchStatement;
import com.unir.payments.data.model.Purchase;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PurchaseRepository {

    private final PurchaseJpaRepository repository;

    public List<Purchase> getPurchases() {
        return repository.findAll();
    }

    public Purchase getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Purchase save(Purchase purchase) {
        return repository.save(purchase);
    }

    public void delete(Purchase purchase) {
        repository.delete(purchase);
    }

    public List<Purchase> search(String bookIsbn, String buyer, String status) {
        SearchCriteria<Purchase> spec = new SearchCriteria<>();

        if (StringUtils.isNotBlank(bookIsbn)) {
            spec.add(new SearchStatement(Consts.BOOK_ISBN, bookIsbn, SearchOperation.MATCH));
        }

        if (StringUtils.isNotBlank(buyer)) {
            spec.add(new SearchStatement(Consts.BUYER, buyer, SearchOperation.EQUAL));
        }

        if (StringUtils.isNotBlank(status)) {
            spec.add(new SearchStatement(Consts.STATUS, status, SearchOperation.EQUAL));
        }

        return repository.findAll(spec);
    }
}
