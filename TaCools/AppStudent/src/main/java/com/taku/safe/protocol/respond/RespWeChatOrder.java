package com.taku.safe.protocol.respond;

/**
 * Created by fylder on 2017/8/4.
 */

public class RespWeChatOrder extends RespBaseBean {


    private DBean d;

    public DBean getD() {
        return d;
    }

    public void setD(DBean d) {
        this.d = d;
    }

    public static class DBean {

        /**
         * sign : CA7B0A4EE8B523A6D601871FBB93D277
         * orderParam :
         * timestamp : 1501818535
         * prepay_id : wx20170804114838997969e9fd0797321339
         * nonce_str : 2891114855
         * serialNumber : d24b989b0e8c4754b7edae55cbce2751
         */

        private String sign;
        private String orderParam;
        private String timestamp;
        private String prepay_id;
        private String nonce_str;
        private String serialNumber;//呼信订单号

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getOrderParam() {
            return orderParam;
        }

        public void setOrderParam(String orderParam) {
            this.orderParam = orderParam;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getPrepay_id() {
            return prepay_id;
        }

        public void setPrepay_id(String prepay_id) {
            this.prepay_id = prepay_id;
        }

        public String getNonce_str() {
            return nonce_str;
        }

        public void setNonce_str(String nonce_str) {
            this.nonce_str = nonce_str;
        }

        public String getSerialNumber() {
            return serialNumber;
        }

        public void setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
        }
    }
}
