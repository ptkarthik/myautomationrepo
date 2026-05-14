package org.billing.dbclasses;

import io.cucumber.java.sl.In;

public class FxSwapRateDBData {
    private String id;
    private Integer lowerBound;
    private Integer upperBound;
    private String tenor;
    private double revenueShare;
    private boolean maxUpperBound;

    public FxSwapRateDBData(String id, Integer lowerBound, Integer upperBound, String tenor,
                            double revenueShare, boolean maxUpperBound) {
        this.id = id;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.tenor = tenor;
        this.revenueShare = revenueShare;
        this.maxUpperBound = maxUpperBound;
    }

    public String getId() {
        return id;
    }

    public Integer getLowerBound() {
        return lowerBound;
    }

    public Integer getUpperBound() {
        return upperBound;
    }

    public String getTenor() {
        return tenor;
    }

    public double getRevenueShare() {
        return revenueShare;
    }

    public boolean isMaxUpperBound() {
        return maxUpperBound;
    }
}