package com.unir.payments.service;

import java.util.List;

import com.unir.payments.data.model.Purchase;
import com.unir.payments.controller.model.PurchaseDto;
import com.unir.payments.controller.model.CreatePurchaseRequest;

public interface PurchasesService {

	List<Purchase> getPurchases(String bookIsbn, String buyer, String status);

	Purchase getPurchase(String purchaseId);

	Boolean removePurchase(String purchaseId);

	Purchase createPurchase(CreatePurchaseRequest request);

	Purchase updatePurchase(String purchaseId, String updateRequest);

	Purchase updatePurchase(String purchaseId, PurchaseDto updateRequest);
}
