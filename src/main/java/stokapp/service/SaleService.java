package stokapp.service;

// Bu servis tekli veya çoklu ürün satışını fiş mantığında indirimle birlikte yönetir.
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stokapp.dto.EndOfDayResponse;
import stokapp.dto.EndOfDaySaleItemResponse;
import stokapp.dto.EndOfDaySummaryResponse;
import stokapp.dto.ExpenseResponse;
import stokapp.dto.SaleItemRequest;
import stokapp.dto.SaleRequest;
import stokapp.entity.Expense;
import stokapp.entity.PaymentType;
import stokapp.entity.Product;
import stokapp.entity.Sale;
import stokapp.entity.SaleItem;
import stokapp.repository.ExpenseRepository;
import stokapp.repository.ProductRepository;
import stokapp.repository.SaleItemRepository;
import stokapp.repository.SaleRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;
    private final ExpenseRepository expenseRepository;

    @Transactional
    public Sale createSale(SaleRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Satış için en az bir ürün eklenmelidir");
        }

        if (request.getPaymentType() == null) {
            throw new RuntimeException("Ödeme tipi boş olamaz");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Sale sale = new Sale();
        sale.setPaymentType(request.getPaymentType());
        sale.setSaleTime(LocalDateTime.now());
        sale.setCustomerName(emptyToNull(request.getCustomerName()));
        sale.setCustomerSurname(emptyToNull(request.getCustomerSurname()));
        sale.setCustomerTc(emptyToNull(request.getCustomerTc()));
        sale.setSellerUsername(username);

        List<SaleItem> saleItems = new ArrayList<>();
        double originalTotalPrice = 0.0;

        for (SaleItemRequest itemRequest : request.getItems()) {
            if (itemRequest.getProductId() == null) {
                throw new RuntimeException("Ürün id boş olamaz");
            }

            if (itemRequest.getQuantity() == null || itemRequest.getQuantity() <= 0) {
                throw new RuntimeException("Satış adedi 0'dan büyük olmalı");
            }

            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Ürün bulunamadı: " + itemRequest.getProductId()));

            if (product.getStock() < itemRequest.getQuantity()) {
                throw new RuntimeException(product.getIlacIsmi() + " için yetersiz stok");
            }

            double unitPrice = product.getSatisFiyati();
            double lineTotal = unitPrice * itemRequest.getQuantity();

            product.setStock(product.getStock() - itemRequest.getQuantity());
            productRepository.save(product);

            SaleItem saleItem = new SaleItem();
            saleItem.setSale(sale);
            saleItem.setProduct(product);
            saleItem.setQuantity(itemRequest.getQuantity());
            saleItem.setUnitPrice(unitPrice);
            saleItem.setLineTotal(lineTotal);

            saleItems.add(saleItem);
            originalTotalPrice += lineTotal;
        }

        double discountAmount = request.getDiscountAmount() == null ? 0.0 : request.getDiscountAmount();

        if (discountAmount < 0) {
            throw new RuntimeException("İndirim tutarı negatif olamaz");
        }

        if (discountAmount > originalTotalPrice) {
            throw new RuntimeException("İndirim tutarı toplam tutardan büyük olamaz");
        }

        double totalPrice = originalTotalPrice - discountAmount;

        sale.setOriginalTotalPrice(originalTotalPrice);
        sale.setDiscountAmount(discountAmount);
        sale.setTotalPrice(totalPrice);
        sale.setItems(saleItems);

        Sale savedSale = saleRepository.save(sale);
        saleItemRepository.saveAll(saleItems);

        return savedSale;
    }

    public EndOfDayResponse getEndOfDayReport(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Sale> sales = saleRepository.findBySaleTimeBetween(start, end);
        List<Expense> expenses = expenseRepository.findByExpenseTimeBetweenOrderByExpenseTimeDesc(start, end);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        List<EndOfDaySaleItemResponse> saleItems = sales.stream()
                .flatMap(sale -> sale.getItems().stream().map(item ->
                        new EndOfDaySaleItemResponse(
                                sale.getId(),
                                item.getId(),
                                item.getProduct().getId(),
                                item.getProduct().getIlacIsmi(),
                                item.getQuantity(),
                                item.getUnitPrice(),
                                item.getLineTotal(),
                                sale.getOriginalTotalPrice(),
                                sale.getDiscountAmount(),
                                sale.getTotalPrice(),
                                sale.getPaymentType().name(),
                                sale.getSaleTime().format(dateFormatter),
                                sale.getSaleTime().format(timeFormatter),
                                sale.getCustomerName(),
                                sale.getCustomerSurname(),
                                sale.getCustomerTc(),
                                sale.getSellerUsername()
                        )
                ))
                .toList();

        List<ExpenseResponse> expenseResponses = expenses.stream()
                .map(expense -> new ExpenseResponse(
                        expense.getId(),
                        expense.getAmount(),
                        expense.getDescription(),
                        expense.getExpenseTime().format(dateFormatter),
                        expense.getExpenseTime().format(timeFormatter),
                        expense.getCreatedBy()
                ))
                .toList();

        int totalSalesCount = sales.size();

        int totalQuantity = sales.stream()
                .flatMap(sale -> sale.getItems().stream())
                .mapToInt(SaleItem::getQuantity)
                .sum();

        double grossSales = sales.stream()
                .mapToDouble(Sale::getOriginalTotalPrice)
                .sum();

        double totalDiscount = sales.stream()
                .mapToDouble(Sale::getDiscountAmount)
                .sum();

        double netSales = grossSales - totalDiscount;

        double totalNakit = sales.stream()
                .filter(sale -> sale.getPaymentType() == PaymentType.NAKIT)
                .mapToDouble(Sale::getTotalPrice)
                .sum();

        double totalPos = sales.stream()
                .filter(sale -> sale.getPaymentType() == PaymentType.POS)
                .mapToDouble(Sale::getTotalPrice)
                .sum();

        double totalExpenses = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        double cashInHand = netSales - totalExpenses;

        EndOfDaySummaryResponse summary = new EndOfDaySummaryResponse(
                date.format(dateFormatter),
                totalSalesCount,
                totalQuantity,
                grossSales,
                netSales,
                totalDiscount,
                totalNakit,
                totalPos,
                totalExpenses,
                cashInHand
        );

        return new EndOfDayResponse(summary, saleItems, expenseResponses);
    }

    private String emptyToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}