package com.unir.payments.controller.model;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePurchaseRequest {
	// ISBN del libro a comprar (para validación con ms-books-catalogue)
	private String bookIsbn;

	// Cantidad de libros a adquirir
	private Integer quantity;

	// Información del comprador (por ejemplo, email o nombre)
	private String buyer;

	// Estado inicial de la compra (puede definirse aquí o asignarse en el servicio)
	private String status;

	// Opcional: en caso de que el cliente envíe la fecha de compra; si no, se asignará automáticamente
	private LocalDateTime purchaseDate;
}
