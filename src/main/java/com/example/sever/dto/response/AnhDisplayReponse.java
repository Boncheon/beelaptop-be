package com.example.sever.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnhDisplayReponse {

     private UUID id;
     private String idAnh;
     private String imgURL;


}
