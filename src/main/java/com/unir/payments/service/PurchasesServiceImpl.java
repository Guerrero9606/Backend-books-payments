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
import com.unir.payments.controller.model.BookResponseDTO; // Modelo para mapear la respuesta del microservicio de libros
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class PurchasesServiceImpl implements PurchasesService {

	@Autowired
	private PurchaseRepository repository;

	@Autowired
	private ObjectMapper objectMapper;

	// Para llamar al microservicio de libros vía Gateway
	@Autowired
	private RestTemplate restTemplate;

	// Inyectamos la URL del Gateway (definida en las propiedades o variables de entorno)
	@Value("${gateway.url:http://localhost:8762}")
	private String gatewayUrl;

	@Override
	public List<Purchase> getPurchases(String bookId, String buyer, String status) {
		// Si se proporciona algún criterio de búsqueda, se utiliza el método search
		if (StringUtils.hasLength(bookId) || StringUtils.hasLength(buyer) || StringUtils.hasLength(status)) {
			return repository.search(bookId, buyer, status);
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
				&& StringUtils.hasLength(request.getBookId().trim())
				&& request.getQuantity() != null
				&& StringUtils.hasLength(request.getBuyer().trim())
				&& StringUtils.hasLength(request.getStatus().trim())) {

			String bookId = request.getBookId();
			// Construir la URL del Gateway para consultar el libro (se usa el ID o ISBN, según cómo esté configurado)
			String bookUrl = gatewayUrl + "/ms-books-catalogue/books/" + bookId;
			String getPayload = "{\"targetMethod\": \"GET\"}";

			log.info("Consultando disponibilidad del libro en: {}", bookUrl);
			ResponseEntity<BookResponseDTO> bookResponse = restTemplate.postForEntity(bookUrl, getPayload, BookResponseDTO.class);

			boolean available = false;
			if (bookResponse.getStatusCode() == HttpStatus.OK && bookResponse.getBody() != null) {
				BookResponseDTO book = bookResponse.getBody();
				available = Boolean.TRUE.equals(book.getVisible());
				log.info("Libro consultado: ID={}, visible={}", book.getBookId(), available);
			} else {
				log.warn("No se encontró el libro con ID: {}", bookId);
			}

			// Construir el objeto Purchase
			Purchase purchase = Purchase.builder()
					.bookId(bookId)
					.purchaseDate(request.getPurchaseDate() != null ? request.getPurchaseDate() : LocalDateTime.now())
					.quantity(request.getQuantity())
					.buyer(request.getBuyer())
					.build();

			if (available) {
				purchase.setStatus("CONFIRMED");
				Purchase savedPurchase = repository.save(purchase);

				// Actualizar el libro: enviar solicitud para cambiar 'visible' a false.
				String updatePayload = "{\"targetMethod\": \"PATCH\", \"queryParams\": {}, \"body\": {\"visible\": false}}";
				log.info("Actualizando visibilidad del libro a false en: {}", bookUrl);
				try {
					restTemplate.postForEntity(bookUrl, updatePayload, Void.class);
				} catch (Exception e) {
					log.error("Error al actualizar visibilidad del libro: {}", e.getMessage(), e);
					// Aquí puedes decidir si revertir la compra o notificar el error.
				}
				return savedPurchase;
			} else {
				// Libro no disponible: registra la compra con status CANCELLED
				purchase.setStatus("CANCELLED");
				Purchase savedPurchase = repository.save(purchase);
				log.info("Compra registrada con status CANCELLED, libro no disponible.");
				return savedPurchase;
			}
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
			purchase.update(updateRequest);
			repository.save(purchase);
			return purchase;
		} else {
			return null;
		}
	}
}
