package com.goomon.kafkastreams.model;

import lombok.*;

@ToString
@Getter @Setter
@AllArgsConstructor
public class RewardAccumulator {

    private int customerId;
    private double purchaseTotal;

    public RewardAccumulator() {
    }

    public static RewardAccumulator build(Purchase purchase) {
        RewardAccumulator rewardAccumulator = new RewardAccumulator();
        rewardAccumulator.setCustomerId(purchase.getCustomerId());
        rewardAccumulator.setPurchaseTotal(purchase.getPrice() * purchase.getQuantity());
        return rewardAccumulator;
    }
}
