package com.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAnalyticsDTO {

    private long totalUsers;

    private long newUsersCount;

    private long totalAdmins;
}
