package com.example.sever.controller;

import com.example.sever.dto.Pos.GHN.GhnFeeRequest;
import com.example.sever.dto.Pos.GHN.GhnFeeResponse;
import com.example.sever.service.GhnClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ghn")
@CrossOrigin(origins = "*")
public class GhnPublicController {

    private final GhnClientService ghn;

    public GhnPublicController(GhnClientService ghn) {
        this.ghn = ghn;
    }

    @GetMapping("/province")
    public ResponseEntity<Object> provinces() {
        return ResponseEntity.ok(ghn.getProvincesData());
    }

    @GetMapping("/district")
    public ResponseEntity<Object> districts(@RequestParam("province_id") int provinceId) {
        return ResponseEntity.ok(ghn.getDistrictsData(provinceId));
    }

    @GetMapping("/ward")
    public ResponseEntity<Object> wards(@RequestParam("district_id") int districtId) {
        return ResponseEntity.ok(ghn.getWardsData(districtId));
    }

    @PostMapping("/fee")
    public ResponseEntity<GhnFeeResponse> fee(@RequestBody GhnFeeRequest req) {
        int total = ghn.calcFee(req);
        return ResponseEntity.ok(new GhnFeeResponse(total));
    }
}
