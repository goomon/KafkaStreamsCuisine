package com.goomon.kafkastreams.model;

import lombok.*;

@ToString
@Getter @Setter
@AllArgsConstructor
public class RewardAccumulator {

    private String customerId;
    private double purchaseTotal;
    private int totalRewardPoints;
    private int currentRewardPoints;

    public RewardAccumulator() {
    }

    public static RewardAccumulator build(Purchase purchase) {
        RewardAccumulator rewardAccumulator = new RewardAccumulator();
        rewardAccumulator.setCustomerId(purchase.getCustomerId());
        rewardAccumulator.setPurchaseTotal(purchase.getPrice() * purchase.getQuantity());
        return rewardAccumulator;
    }

    public static Builder builder(Purchase purchase){
        return new Builder(purchase);
    }

    public static final class Builder {
        private String customerId;
        private double purchaseTotal;
        private int rewardPoints;

        private Builder(Purchase purchase){
            this.customerId = purchase.getLastName() + "," + purchase.getFirstName();
            this.purchaseTotal = purchase.getPrice() * (double) purchase.getQuantity();
            this.rewardPoints = (int) purchaseTotal;
        }

        public RewardAccumulator build(){
            return new RewardAccumulator(customerId, purchaseTotal, rewardPoints, rewardPoints);
        }
    }
}
