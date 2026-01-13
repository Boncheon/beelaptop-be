package com.example.sever.service;

import com.example.sever.config.GhnProperties;

import com.example.sever.dto.Pos.GHN.GhnFeeRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class GhnClientService {

    private final GhnProperties props;
    private final RestTemplate restTemplate;

    public GhnClientService(GhnProperties props, RestTemplate restTemplate) {
        this.props = props;
        this.restTemplate = restTemplate;
    }

    private HttpHeaders headers(boolean includeShopId) {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.set("Token", props.getToken());
        if (includeShopId) h.set("ShopId", String.valueOf(props.getShopId()));
        return h;
    }

    @SuppressWarnings("unchecked")
    public int calcFee(GhnFeeRequest req) {
        if (req.getToDistrictId() == null || req.getToWardCode() == null || req.getToWardCode().isBlank()) {
            throw new IllegalArgumentException("Thiếu toDistrictId/toWardCode");
        }
        if (req.getWeight() == null || req.getWeight() <= 0) {
            throw new IllegalArgumentException("Thiếu weight (gram)");
        }
        if (req.getLength() == null || req.getLength() <= 0
                || req.getWidth() == null || req.getWidth() <= 0
                || req.getHeight() == null || req.getHeight() <= 0) {
            throw new IllegalArgumentException("Thiếu kích thước (length/width/height) (cm)");
        }
        if (req.getInsuranceValue() == null || req.getInsuranceValue() < 0) {
            req.setInsuranceValue(0);
        }

        String url = props.getBaseUrl() + "/v2/shipping-order/fee";

        Map<String, Object> body = new HashMap<>();
        body.put("service_type_id", props.getServiceTypeId());
        body.put("from_district_id", props.getFromDistrictId());
        body.put("from_ward_code", props.getFromWardCode());
        body.put("to_district_id", req.getToDistrictId());
        body.put("to_ward_code", req.getToWardCode());

        body.put("weight", req.getWeight());
        body.put("length", req.getLength());
        body.put("width", req.getWidth());
        body.put("height", req.getHeight());
        body.put("insurance_value", req.getInsuranceValue());
        body.put("coupon", null);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers(true));
        log.info("[GHN fee] request body={}", body);

        ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        log.info("[GHN fee] status={} respBody={}", resp.getStatusCode(), resp.getBody());


        Map<String, Object> respBody = resp.getBody();
        if (respBody == null) return 0;

        Object dataObj = respBody.get("data");
        if (!(dataObj instanceof Map)) return 0;

        Map<String, Object> data = (Map<String, Object>) dataObj;

        Object totalObj = data.get("total");
        return (totalObj instanceof Number) ? ((Number) totalObj).intValue() : 0;

    }

    @SuppressWarnings("unchecked")
    private Object unwrapData(Object body) {
        if (!(body instanceof Map)) return body;
        Object data = ((Map<String, Object>) body).get("data");
        return data != null ? data : body;
    }

    public Object getProvincesData() { return unwrapData(getMaster("/master-data/province")); }
    public Object getDistrictsData(int provinceId) { return unwrapData(getMaster("/master-data/district?province_id=" + provinceId)); }
    public Object getWardsData(int districtId) { return unwrapData(getMaster("/master-data/ward?district_id=" + districtId)); }

    // dropdown master-data (giống CartPage)


    private Object getMaster(String path) {
        String url = props.getBaseUrl() + path;
        HttpEntity<Void> entity = new HttpEntity<>(headers(false));
        return restTemplate.exchange(url, HttpMethod.GET, entity, Object.class).getBody();
    }
}
