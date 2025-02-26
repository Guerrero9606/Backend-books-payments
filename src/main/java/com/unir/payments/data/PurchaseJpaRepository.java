package com.unir.payments.data;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.unir.payments.data.model.Purchase;

public interface PurchaseJpaRepository extends JpaRepository<Purchase, Long>, JpaSpecificationExecutor<Purchase> {

	List<Purchase> findByBookIsbn(String bookIsbn);

	List<Purchase> findByBuyer(String buyer);

	List<Purchase> findByStatus(String status);

	List<Purchase> findByBookIsbnAndBuyer(String bookIsbn, String buyer);
}
