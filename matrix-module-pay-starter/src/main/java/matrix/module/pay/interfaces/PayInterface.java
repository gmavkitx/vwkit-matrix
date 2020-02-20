package matrix.module.pay.interfaces;

import matrix.module.pay.entity.MatrixPayEntity;
import matrix.module.pay.entity.MatrixRefundEntity;

import java.util.List;

/**
 * @author wangcheng
 * @date 2019/4/28
 */
public interface PayInterface {

    /**
     * 支付成功回调
     *
     * @param payEntities
     * @return
     */
    boolean paySuccess(List<MatrixPayEntity> payEntities);

    /**
     * 退款成功回调
     *
     * @param refundEntities
     * @return
     */
    boolean refundSuccess(List<MatrixRefundEntity> refundEntities);
}
