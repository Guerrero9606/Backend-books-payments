package com.unir.payments.controller.model;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseDto {
	private Long id;
	private String bookIsbn;
	private LocalDateTime purchaseDate;
	private Integer quantity;
	private String buyer;
	private String status;
}
