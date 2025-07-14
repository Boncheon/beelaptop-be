package com.example.sever.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PinDisplayReponse {

     private UUID id;

     private String idPin;

     private String dungLuong;
}
