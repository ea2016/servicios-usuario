package com.easj.dto;

import lombok.Data;
import java.util.List;

@Data
public class SolicitudRequest {
    private Long usuarioId;
    private List<SolicitudItemRequest> items;
}
