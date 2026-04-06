package stokapp.controller;

// Bu controller satış oluşturma ve gün sonu raporunu yönetir.
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import stokapp.dto.EndOfDayResponse;
import stokapp.dto.SaleRequest;
import stokapp.entity.Sale;
import stokapp.service.SaleService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@CrossOrigin("*")
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    public Sale createSale(@RequestBody SaleRequest request) {
        return saleService.createSale(request);
    }

    @GetMapping("/end-of-day")
    public EndOfDayResponse getEndOfDayReport(@RequestParam String date) {
        return saleService.getEndOfDayReport(LocalDate.parse(date));
    }
}