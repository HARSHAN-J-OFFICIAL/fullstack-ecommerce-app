package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueTrendsDTO {

    private List<RevenueTrendItemDTO> dailyTrends;

    private List<RevenueTrendItemDTO> monthlyTrends;

    private List<RevenueTrendItemDTO> yearlyTrends;
}
