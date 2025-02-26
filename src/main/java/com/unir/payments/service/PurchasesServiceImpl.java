package com.unir.payments.service;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.unir.payments.data.PurchaseRepository;
import com.unir.payments.controller.model.PurchaseDto;
import com.unir.payments.controller.model.CreatePurchaseRequest;
import com.unir.payments.data.model.Purchase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class PurchasesServiceImpl implements PurchasesService {

	@Autowired
	private PurchaseRepository repository;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public List<Purchase> getPurchases(String bookIsbn, String buyer, String status) {
		// Si se proporciona algún criterio de búsqueda, se utiliza el método search
		if (StringUtils.hasLength(bookIsbn) || StringUtils.hasLength(buyer) || StringUtils.hasLength(status)) {
			return repository.search(bookIsbn, buyer, status);
		}
		List<Purchase> purchases = repository.getPurchases();
		return purchases.isEmpty() ? null : purchases;
	}

	@Override
	public Purchase getPurchase(String purchaseId) {
		return repository.getById(Long.valueOf(purchaseId));
	}

	@Override
	public Boolean removePurchase(String purchaseId) {
		Purchase purchase = repository.getById(Long.valueOf(purchaseId));
		if (purchase != null) {
			repository.delete(purchase);
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	@Override
	public Purchase createPurchase(CreatePurchaseRequest request) {
		if (request != null
				&& StringUtils.hasLength(request.getBookIsbn().trim())
				&& request.getQuantity() != null
				&& StringUtils.hasLength(request.getBuyer().trim())
				&& StringUtils.hasLength(request.getStatus().trim())) {

			Purchase purchase = Purchase.builder()
					.bookIsbn(request.getBookIsbn())
					.purchaseDate(request.getPurchaseDate() != null ? request.getPurchaseDate() : LocalDateTime.now())
					.quantity(request.getQuantity())
					.buyer(request.getBuyer())
					.status(request.getStatus())
					.build();

			return repository.save(purchase);
		} else {
			return null;
		}
	}

	@Override
	public Purchase updatePurchase(String purchaseId, String request) {
		Purchase purchase = repository.getById(Long.valueOf(purchaseId));
		if (purchase != null) {
			try {
				JsonMergePatch jsonMergePatch = JsonMergePatch.fromJson(objectMapper.readTree(request));
				JsonNode target = jsonMergePatch.apply(objectMapper.readTree(objectMapper.writeValueAsString(purchase)));
				Purchase patched = objectMapper.treeToValue(target, Purchase.class);
				repository.save(patched);
				return patched;
			} catch (JsonProcessingException | JsonPatchException e) {
				log.error("Error updating purchase {}", purchaseId, e);
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public Purchase updatePurchase(String purchaseId, PurchaseDto updateRequest) {
		Purchase purchase = repository.getById(Long.valueOf(purchaseId));
		if (purchase != null) {
			// Se asume que la entidad Purchase tiene un método update(PurchaseDto purchaseDto)
			purchase.update(updateRequest);
			repository.save(purchase);
			return purchase;
		} else {
			return null;
		}
	}
}
