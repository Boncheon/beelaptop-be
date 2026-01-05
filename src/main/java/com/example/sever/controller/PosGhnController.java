package com.example.sever.controller;


import com.example.sever.dto.Pos.GHN.GhnFeeRequest;
import com.example.sever.dto.Pos.GHN.GhnFeeResponse;
import com.example.sever.service.GhnClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pos/ghn")
@CrossOrigin(origins = "*")
public class PosGhnController {

    private final GhnClientService ghn;

    public PosGhnController(GhnClientService ghn) {
        this.ghn = ghn;
    }

    @PostMapping("/fee")
    public ResponseEntity<GhnFeeResponse> fee(@RequestBody GhnFeeRequest req) {
        int total = ghn.calcFee(req);
        return ResponseEntity.ok(new GhnFeeResponse(total));
    }

    @GetMapping("/province")
    public ResponseEntity<Object> provinces() {
        return ResponseEntity.ok(ghn.getProvinces());
    }

    @GetMapping("/district")
    public ResponseEntity<Object> districts(@RequestParam("province_id") int provinceId) {
        return ResponseEntity.ok(ghn.getDistricts(provinceId));
    }

    @GetMapping("/ward")
    public ResponseEntity<Object> wards(@RequestParam("district_id") int districtId) {
        return ResponseEntity.ok(ghn.getWards(districtId));
    }
}