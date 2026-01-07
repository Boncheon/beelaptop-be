package com.example.sever.controller.customer;

import com.example.sever.dto.response.CustomerLaptopChiTietResponse;
import com.example.sever.dto.response.CustomerLaptopProjection;
import com.example.sever.dto.response.ListLaptopCustomerProjection;
import com.example.sever.dto.response.PhieuGiamGiaCustomerProjection;
import com.example.sever.dto.response.Search.BrandSearchResponse;
import com.example.sever.dto.response.Search.LaptopSearchBrandProjection;
import com.example.sever.dto.response.Search.LaptopSearchResponse;
import com.example.sever.service.LapTopCTService;
import com.example.sever.service.LaptopService;
import com.example.sever.service.PhieuGiamGiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/laptops")
@CrossOrigin(origins = "*")
public class CustomerLaptopController {
    @Autowired
    private LaptopService laptopService;
    @Autowired
    private LapTopCTService lapTopCTService;
    @Autowired
    private PhieuGiamGiaService phieuGiamGiaService;

    @GetMapping("/home")
    public List<CustomerLaptopProjection> getLaptopForHome() {
        return laptopService.getCustomerLaptop();
    }

    @GetMapping("/latest")
    public List<CustomerLaptopProjection> getLatestLaptops() {
        return laptopService.getLatestLaptops();
    }

    @GetMapping("/{laptopId}/details")
    public ResponseEntity<List<CustomerLaptopChiTietResponse>> getLaptopDetails(@PathVariable UUID laptopId) {
        List<CustomerLaptopChiTietResponse> details = lapTopCTService.getLapTopCustomer(laptopId);
        if (details.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(details);
    }

    @GetMapping("/check-quantity/{id}")
    public String checkQuantity(@PathVariable UUID id, @RequestParam int quantity) {
        return lapTopCTService.checkQuantityProduct(id, quantity);
    }

    @GetMapping("/list-product-customer")
    public List<ListLaptopCustomerProjection> getAllVersionLaptop() {
        return lapTopCTService.listProductLaptop();
    }

    @GetMapping("/get-voucher")
    public List<PhieuGiamGiaCustomerProjection> getVoucherForBill(@Param("tongTien") BigDecimal tongTien) {
        return phieuGiamGiaService.getAllVoucherForCustomer(tongTien);
    }
    @GetMapping("/search")
    public List<LaptopSearchResponse> search(@RequestParam String keyword) {
        return lapTopCTService.searchLaptopCustomer(keyword);
    }
    @GetMapping("/search-brand")
    public List<LaptopSearchBrandProjection> searchBrand(@RequestParam UUID idBrand) {
        return laptopService.getSearchBrand(idBrand);
    }
    @GetMapping("/get-all-brand")
    public List<BrandSearchResponse> listBrand() {
        return laptopService.getAllBrand();
    }

    //Update code huy 05.01
    @GetMapping("/check-inventory/{id}")
    public Integer checkInvenTory(@PathVariable UUID id) {
        return laptopService.getAllStatusIs1Laptop(id);
    }
}
