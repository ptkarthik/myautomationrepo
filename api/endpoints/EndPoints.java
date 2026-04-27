package org.billing.api.endpoints;

public class EndPoints {
    public static final String GET_ALL_BROKER = "api/brokers/all";
    public static final String GET_BROKER = "api/brokers?name={name}";
    public static final String POST_BROKER = "api/brokers";
    public static final String BROKER_AUTH = "api/authenticate";
    public static final String DELETE_BROKER = "api/brokers/{name}";
    public static final String POST_FXSWAPRATE = "api/fxSwapRates";
    public static final String PUT_FXSWAPRATE = "api/fxSwapRates";
    public static final String DELETE_FXSWAPRATE = "api/fxSwapRates";
    public static final String GET_ALL_TRADERS = "api/traders/all";
    public static final String GET_TRADERS = "api/traders";
    public static final String UPLOAD_TRADERS = "api/traders/upload";
    public static final String POST_TRADER = "api/traders";
    public static final String PUT_TRADER = "api/traders/{id}";
    public static final String DELETE_TRADER = "api/traders/{id}";
    public static final String POST_CURRENCYRATE = "api/currency-rates";
    public static final String PUT_CURRENCYRATE = "api/currency-rates";
    public static final String DELETE_CURRENCYRATE = "api/currency-rates";
    public static final String GET_ALL_CURRENCYRATE = "api/currency-rates";
}