package com.zwan.bc.wallet.job;

import com.zwan.bc.wallet.config.JsonrpcClient;
import com.zwan.bc.wallet.entity.Coin;
import com.zwan.bc.wallet.service.AccountService;
import com.zwan.bc.wallet.util.AccountReplay;
import com.spark.blockchain.rpcclient.BitcoinUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CoinCollectJob {
    private Logger logger = LoggerFactory.getLogger(CoinCollectJob.class);
    @Autowired
    private AccountService accountService;
    @Autowired
    private JsonrpcClient rpcClient;
    @Autowired
    private Coin coin;

    @Scheduled(cron = "0 0 15 * * *")
    public void rechargeMinerFee(){
        try {
            AccountReplay accountReplay = new AccountReplay(accountService, 100);
            accountReplay.run(account -> {
                BigDecimal btcBalance = rpcClient.getAddressBalance(account.getAddress());
                if(btcBalance.compareTo(coin.getRechargeMinerFee()) < 0) {
                    BigDecimal usdtBalance = rpcClient.omniGetBalance(account.getAddress());
                    if(usdtBalance.compareTo(coin.getMinCollectAmount()) >= 0) {
                        try {
                            String txid = BitcoinUtil.sendTransaction(rpcClient, coin.getWithdrawAddress(), account.getAddress(), coin.getRechargeMinerFee(), coin.getDefaultMinerFee());
                            logger.info("BitcoinUtil.sendTransaction:address={},txid={}", account.getAddress(), txid);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
