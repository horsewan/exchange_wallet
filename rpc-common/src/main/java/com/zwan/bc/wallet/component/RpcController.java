package com.zwan.bc.wallet.component;

import com.zwan.bc.wallet.util.MessageResult;

import java.math.BigDecimal;

public interface RpcController {
    /**
     * 获取当前区块高度
     * @return
     */
    MessageResult blockHeight();

    MessageResult getNewAddress(String uuid);

    MessageResult withdraw(String toAddress, BigDecimal amount, BigDecimal fee,Boolean isSync,String withdrawId);

    MessageResult transfer();

    MessageResult balance();
}
