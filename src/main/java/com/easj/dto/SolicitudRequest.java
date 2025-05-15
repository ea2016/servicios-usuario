package com.easj.dto;

import lombok.Data;
import java.util.List;

@Data
public class SolicitudRequest {
    private String usuarioId;
    private List<SolicitudItemRequest> items;
}
