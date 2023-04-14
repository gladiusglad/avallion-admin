package net.avallion.admin;

import lombok.Data;

import java.util.List;

@Data
public class DeathsResult {

    private final List<DeathStats> deaths;
    private final int pageQty;
}
