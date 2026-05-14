package org.billing.utils.billing;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MathUtil {

    public double getPercentageChange(double previousValue, double currentValue) {

        return ((currentValue - previousValue) / previousValue) * 100;
    }
}
