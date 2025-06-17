package com.example.sever.service;

import com.example.sever.enity.DoHoa;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DoHoaService {
    List<DoHoa> getAllDoHoaforDisplayPage (Pageable pageable);
}
